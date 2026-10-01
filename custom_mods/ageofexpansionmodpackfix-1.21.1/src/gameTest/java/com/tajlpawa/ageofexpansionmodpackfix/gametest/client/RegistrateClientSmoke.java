package com.tajlpawa.ageofexpansionmodpackfix.gametest.client;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelStructuralBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;

import java.lang.reflect.Modifier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

// 在真实客户端验证 Mixin 已生效、扩展注册完整及跨模组并发回调只执行一次。
@EventBusSubscriber(modid = "ageofexpansionmodpackfix", value = Dist.CLIENT)
public final class RegistrateClientSmoke {
    private static boolean checked;
    private static int completedFrames;

    public static final class ProbeEvent extends Event implements IModBusEvent {}

    private static final class ProbeRegistrate extends AbstractRegistrate<ProbeRegistrate> {
        private volatile IEventBus bus;
        private ProbeRegistrate() { super("ageofexpansionmodpackfix"); }
        @Override public IEventBus getModEventBus() { return bus; }
    }

    @SubscribeEvent
    public static void render(ScreenEvent.Render.Post event) throws Exception {
        boolean startupOnly = Boolean.getBoolean("ageofexpansion.registrateClientSmoke");
        if ((!startupOnly && !Boolean.getBoolean("ageofexpansion.routerClientSmoke"))
                || Minecraft.getInstance().getOverlay() != null) return;
        if (checked) {
            if (startupOnly && ++completedFrames == 20) {
                System.out.println("REGISTRATE_STARTUP_CHECK: startup and screen rendering completed; stopping client normally");
                Minecraft.getInstance().stop();
            }
            return;
        }
        checked = true;
        var method = OneTimeEventReceiver.class.getDeclaredMethod("ageofexpansion$addModListener", AbstractRegistrate.class,
                EventPriority.class, Class.class, Consumer.class);
        if (!Modifier.isSynchronized(method.getModifiers())) throw new AssertionError("Registrate Mixin not applied");
        if (!(IClientBlockExtensions.of(AllBlocks.BELT.get()) instanceof BeltBlock.RenderProperties)
                || !(IClientBlockExtensions.of(AllBlocks.WATER_WHEEL_STRUCTURAL.get()) instanceof WaterWheelStructuralBlock.RenderProperties)
                || !(IClientBlockExtensions.of(AllBlocks.TRACK.get()) instanceof TrackBlock.RenderProperties)) {
            throw new AssertionError("Create client block extensions are missing");
        }

        try (var workers = Executors.newFixedThreadPool(4)) {
            for (int round = 0; round < 32; round++) {
                var owner = new ProbeRegistrate();
                var calls = new AtomicInteger();
                OneTimeEventReceiver.addModListener(owner, ProbeEvent.class, probe -> calls.incrementAndGet());
                owner.bus = BusBuilder.builder().build();
                var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();
                for (int worker = 0; worker < 4; worker++) {
                    futures.add(workers.submit(() -> OneTimeEventReceiver.addModListener(owner,
                            ProbeEvent.class, probe -> calls.incrementAndGet())));
                }
                for (var future : futures) future.get(5, TimeUnit.SECONDS);
                owner.bus.post(new ProbeEvent());
                owner.bus.post(new ProbeEvent());
                if (calls.get() != 5) throw new AssertionError("Expected 5 one-shot callbacks, got " + calls.get());
            }
        }
        System.out.println("REGISTRATE_CLIENT_CHECK: Mixin applied; belt/water wheel/track extensions present; 32 concurrent registration rounds passed");
    }
}
