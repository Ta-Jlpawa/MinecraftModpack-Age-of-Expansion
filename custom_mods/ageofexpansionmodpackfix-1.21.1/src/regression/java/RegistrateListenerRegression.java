import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.AbstractRegistrate;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;

import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.jar.JarFile;

// Exercise the installed Registrate bytecode; only widen its existing first-bus race window.
public class RegistrateListenerRegression {
    private static final String TARGET = "com/tterrag/registrate/util/OneTimeEventReceiver";
    private static final String MIXIN = "com/tajlpawa/ageofexpansionmodpackfix/mixin/create/RegistrateEventReceiverMixin";
    private static CountDownLatch firstBusReaders;
    private static boolean runtime;
    public static boolean pauseFirstBusRead(boolean seen) throws InterruptedException {
        if (!seen) {
            firstBusReaders.countDown();
            firstBusReaders.await(150, TimeUnit.MILLISECONDS);
        }
        return seen;
    }

    public static final class ProbeEvent extends Event implements IModBusEvent {}

    public static final class PublishingRegistrate extends AbstractRegistrate<PublishingRegistrate> {
        IEventBus nextBus;
        int reads;
        private PublishingRegistrate() { super("regression"); }
        @Override public IEventBus getModEventBus() {
            if (nextBus != null && ++reads >= 2) return nextBus;
            return super.getModEventBus();
        }
    }

    static final class Bus {
        final Map<Class<?>, List<Consumer<?>>> listeners = new ConcurrentHashMap<>();
        final IEventBus bus = (IEventBus) Proxy.newProxyInstance(IEventBus.class.getClassLoader(),
                new Class<?>[]{IEventBus.class}, (proxy, method, args) -> {
                    if (method.getName().equals("addListener") && args.length == 4) {
                        listeners.computeIfAbsent((Class<?>) args[2], key -> new CopyOnWriteArrayList<>())
                                .add((Consumer<?>) args[3]);
                    }
                    return null;
                });
        @SuppressWarnings("unchecked")
        void post() {
            for (Consumer<?> listener : listeners.getOrDefault(ProbeEvent.class, List.of()))
                ((Consumer<ProbeEvent>) listener).accept(new ProbeEvent());
        }
    }

    static AbstractRegistrate<?> owner() throws Exception {
        return owner(Registrate.class);
    }
    static AbstractRegistrate<?> owner(Class<?> type) throws Exception {
        Field singleton = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        singleton.setAccessible(true);
        return (AbstractRegistrate<?>) ((sun.misc.Unsafe) singleton.get(null)).allocateInstance(type);
    }
    static void attach(AbstractRegistrate<?> owner, Bus bus) throws Exception {
        Field field = AbstractRegistrate.class.getDeclaredField("modEventBus");
        field.setAccessible(true);
        field.set(owner, bus.bus);
    }

    static Class<?> receiver(boolean fixed) throws Exception {
        return receiver(fixed, false);
    }
    static Class<?> receiver(boolean fixed, boolean azimuth) throws Exception {
        ClassNode node = new ClassNode();
        try (var input = runtime ? Files.newInputStream(Path.of("build/registrateRegression/OneTimeEventReceiver.class"))
                : RegistrateListenerRegression.class.getClassLoader().getResourceAsStream(TARGET + ".class")) {
            new ClassReader(input).accept(node, 0);
        }
        if (fixed && !runtime) {
            ClassNode patch = new ClassNode();
            new ClassReader(Files.readAllBytes(Path.of("build/classes/java/main/" + MIXIN + ".class")))
                    .accept(new ClassRemapper(patch, new SimpleRemapper(MIXIN, TARGET)), 0);
            MethodNode replacement = patch.methods.stream().filter(m -> m.name.equals("ageofexpansion$addModListener")).findFirst().orElseThrow();
            replacement.name = "addModListener";
            replacement.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNCHRONIZED;
            node.methods.removeIf(m -> m.name.equals(replacement.name) && m.desc.equals(replacement.desc));
            node.methods.add(replacement);
        }
        if (azimuth && !fixed && !runtime) addAzimuthInjection(node);
        for (MethodNode method : node.methods) {
            if (!method.name.equals("addModListener")) continue;
            for (AbstractInsnNode instruction : method.instructions.toArray()) {
                if (instruction instanceof FieldInsnNode field && field.getOpcode() == Opcodes.GETSTATIC
                        && field.name.equals("seenModBus")) {
                    method.instructions.insert(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC,
                            "RegistrateListenerRegression", "pauseFirstBusRead", "(Z)Z", false));
                }
            }
        }
        ClassWriter writer = new ClassWriter(0);
        node.accept(writer);
        byte[] bytes = writer.toByteArray();
        return new ClassLoader(RegistrateListenerRegression.class.getClassLoader()) {
            Class<?> define() { return defineClass(TARGET.replace('/', '.'), bytes, 0, bytes.length); }
        }.define();
    }

    // Merge the shipped Azimuth handler, including its recursive cleanup registration.
    static void addAzimuthInjection(ClassNode node) throws Exception {
        String name = "com/cake/azimuth/mixin/fix/RegistrateOTERFix";
        ClassNode patch = new ClassNode();
        try (JarFile jar = new JarFile("../../mods/azimuth-1.4.7.jar")) {
            new ClassReader(jar.getInputStream(jar.getJarEntry(name + ".class")))
                    .accept(new ClassRemapper(patch, new SimpleRemapper(name, TARGET)), 0);
        }
        for (FieldNode field : patch.fields) {
            if (field.name.equals("azimuth$flushedOwners")) node.fields.add(field);
        }
        for (MethodNode method : patch.methods) {
            if (!method.name.startsWith("<")) node.methods.add(method);
        }
        MethodNode initializer = node.methods.stream().filter(m -> m.name.equals("<clinit>")).findFirst().orElseThrow();
        InsnList init = new InsnList();
        init.add(new TypeInsnNode(Opcodes.NEW, "java/util/HashSet"));
        init.add(new InsnNode(Opcodes.DUP));
        init.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/HashSet", "<init>", "()V", false));
        init.add(new FieldInsnNode(Opcodes.PUTSTATIC, TARGET, "azimuth$flushedOwners", "Ljava/util/Set;"));
        initializer.instructions.insert(init);
        MethodNode handler = patch.methods.stream().filter(m -> m.name.equals("azimuth$flushPerOwner")).findFirst().orElseThrow();
        MethodNode add = node.methods.stream().filter(m -> m.name.equals("addModListener")
                && m.desc.contains("EventPriority")).findFirst().orElseThrow();
        InsnList head = new InsnList();
        for (int i = 0; i < 4; i++) head.add(new VarInsnNode(Opcodes.ALOAD, i));
        head.add(new InsnNode(Opcodes.ACONST_NULL));
        head.add(new MethodInsnNode(Opcodes.INVOKESTATIC, TARGET, handler.name, handler.desc, false));
        add.instructions.insert(head);
    }

    static Method addMethod(Class<?> receiver) throws Exception {
        return receiver.getMethod("addModListener", AbstractRegistrate.class, EventPriority.class, Class.class, Consumer.class);
    }
    static void add(Method method, AbstractRegistrate<?> owner, Consumer<ProbeEvent> callback) {
        try { method.invoke(null, owner, EventPriority.NORMAL, ProbeEvent.class, callback); }
        catch (ReflectiveOperationException failure) { throw new RuntimeException(failure); }
    }

    // Use NeoForge's real duplicate-registration check, without starting Minecraft or allocating a world.
    static Consumer<ProbeEvent> clientExtensionCallback() throws Exception {
        Class<?> manager = Class.forName("net.neoforged.neoforge.client.extensions.common.ClientExtensionsManager");
        Method register = manager.getDeclaredMethod("register", Object.class, Map.class, Object[].class);
        register.setAccessible(true);
        Map<Object, Object> extensions = new IdentityHashMap<>();
        Object belt = "Block{create:belt}";
        return event -> {
            try { register.invoke(null, new Object(), extensions, new Object[]{belt}); }
            catch (InvocationTargetException failure) { throw (RuntimeException) failure.getCause(); }
            catch (ReflectiveOperationException failure) { throw new RuntimeException(failure); }
        };
    }

    static void concurrentFirstBus(boolean fixed) throws Exception {
        firstBusReaders = new CountDownLatch(2);
        Method add = addMethod(receiver(fixed));
        var owner = owner();
        Bus bus = new Bus();
        add(add, owner, clientExtensionCallback());
        attach(owner, bus);
        try (var threads = Executors.newFixedThreadPool(2)) {
            var start = new CountDownLatch(1);
            Callable<Void> register = () -> { start.await(); add(add, owner, event -> {}); return null; };
            var first = threads.submit(register);
            var second = threads.submit(register);
            start.countDown();
            first.get(5, TimeUnit.SECONDS);
            second.get(5, TimeUnit.SECONDS);
        }
        bus.post();
        bus.post(); // A one-shot receiver must also survive repeated event dispatch.
        System.out.println("PASS concurrent deferred client-extension registration");
    }

    static void multipleOwners(boolean fixed) throws Exception {
        firstBusReaders = new CountDownLatch(0);
        Method add = addMethod(receiver(fixed));
        int[] calls = {0, 0};
        for (int i = 0; i < 2; i++) {
            int index = i;
            var owner = owner();
            Bus bus = new Bus();
            add(add, owner, event -> calls[index]++);
            attach(owner, bus);
            add(add, owner, event -> {});
            bus.post();
            if (calls[index] != 1) throw new AssertionError("Deferred listener lost for owner " + index);
        }
        System.out.println("PASS deferred listeners on separate mod buses");
    }

    static void azimuthBusPublication(boolean fixed) throws Exception {
        firstBusReaders = new CountDownLatch(0);
        Method add = addMethod(receiver(fixed, true));
        var owner = (PublishingRegistrate) owner(PublishingRegistrate.class);
        Bus bus = new Bus();
        add(add, owner, clientExtensionCallback());
        // The owner publishes its bus between Azimuth's HEAD check and Registrate's check.
        owner.nextBus = bus.bus;
        add(add, owner, event -> {});
        add(add, owner, event -> {});
        bus.post();
        bus.post();
        System.out.println("PASS bus publication between Azimuth and Registrate checks");
    }

    public static void main(String[] args) throws Exception {
        runtime = args.length > 0 && args[0].equals("runtime");
        boolean fixed = runtime || args.length > 0 && args[0].equals("fixed");
        if (args.length > 0 && args[0].equals("azimuth")) {
            azimuthBusPublication(false);
            return;
        }
        int failures = 0;
        try { concurrentFirstBus(fixed); } catch (Throwable failure) { failures++; failure.printStackTrace(); }
        try { multipleOwners(fixed); } catch (Throwable failure) { failures++; failure.printStackTrace(); }
        if (fixed) try { azimuthBusPublication(true); } catch (Throwable failure) { failures++; failure.printStackTrace(); }
        if (failures > 0) throw new AssertionError(failures + " regression scenarios failed");
    }
}
