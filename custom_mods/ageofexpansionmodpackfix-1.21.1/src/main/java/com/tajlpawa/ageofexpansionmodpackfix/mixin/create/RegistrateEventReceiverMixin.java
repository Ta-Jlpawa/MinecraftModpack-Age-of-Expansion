package com.tajlpawa.ageofexpansionmodpackfix.mixin.create;

import com.google.common.collect.Table;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mixin(value = OneTimeEventReceiver.class, remap = false, priority = 500)
public abstract class RegistrateEventReceiverMixin {
    @Shadow private static boolean seenModBus;
    @Shadow @Final private static Table<AbstractRegistrate<?>, Class<?>, List<Pair<EventPriority, Consumer<?>>>> waitingModListeners;

    @Shadow public static <T extends Event> void addListener(IEventBus bus, EventPriority priority,
            Class<? super T> eventClass, Consumer<? super T> listener) { throw new AssertionError(); }
    @Shadow private static void onLoadComplete(FMLLoadCompleteEvent event) { throw new AssertionError(); }

    // 较低优先级使本 HEAD 注入后应用、先执行，提前接管 Azimuth 1.4.7 的重复消费逻辑。
    @Inject(method = "addModListener(Lcom/tterrag/registrate/AbstractRegistrate;Lnet/neoforged/bus/api/EventPriority;Ljava/lang/Class;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"), cancellable = true)
    private static <T extends Event & IModBusEvent> void ageofexpansion$registerOnce(AbstractRegistrate<?> owner,
            EventPriority priority, Class<? super T> eventClass, Consumer<? super T> listener, CallbackInfo ci) {
        ageofexpansion$addModListener(owner, priority, eventClass, listener);
        ci.cancel();
    }

    @Unique
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static synchronized <T extends Event & IModBusEvent> void ageofexpansion$addModListener(AbstractRegistrate<?> owner,
            EventPriority priority, Class<? super T> eventClass, Consumer<? super T> listener) {
        // 所有模组共用该队列；检查、入队与消费必须在同一把锁内完成。
        IEventBus bus = owner.getModEventBus();
        if (bus == null) {
            List<Pair<EventPriority, Consumer<?>>> pending = waitingModListeners.get(owner, eventClass);
            if (pending == null) {
                pending = new ArrayList<>();
                waitingModListeners.put(owner, eventClass, pending);
            }
            pending.add(Pair.of(priority, listener));
            return;
        }

        // 每个 Registrate 实例独立消费待注册项，全局标记只控制清理回调。
        var row = waitingModListeners.row(owner);
        for (var entry : row.entrySet()) {
            for (var pending : entry.getValue()) {
                addListener(bus, pending.getLeft(), (Class) entry.getKey(), (Consumer) pending.getRight());
            }
        }
        row.clear();
        if (!seenModBus) {
            seenModBus = true;
            addListener(bus, EventPriority.NORMAL, FMLLoadCompleteEvent.class,
                    RegistrateEventReceiverMixin::onLoadComplete);
        }
        addListener(bus, priority, eventClass, listener);
    }
}
