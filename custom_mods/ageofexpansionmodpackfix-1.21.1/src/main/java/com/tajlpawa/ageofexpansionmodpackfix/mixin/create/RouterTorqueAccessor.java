package com.tajlpawa.ageofexpansionmodpackfix.mixin.create;

import java.util.Map;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.TorquePropagator;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TorquePropagator.class, remap = false)
public interface RouterTorqueAccessor {
    @Accessor("networks") static Map<LevelAccessor, Map<Long, KineticNetwork>> routerNetworks() { throw new AssertionError(); }
}
