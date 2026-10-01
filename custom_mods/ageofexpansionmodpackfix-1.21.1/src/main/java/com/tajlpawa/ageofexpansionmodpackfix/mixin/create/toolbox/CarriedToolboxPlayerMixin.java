package com.tajlpawa.ageofexpansionmodpackfix.mixin.create.toolbox;

import com.tajlpawa.ageofexpansionmodpackfix.create.toolbox.CarriedToolboxBlockEntity;
import com.tajlpawa.ageofexpansionmodpackfix.create.toolbox.CarriedToolboxHolder;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class CarriedToolboxPlayerMixin implements CarriedToolboxHolder {
    @Unique private CarriedToolboxBlockEntity expansion$toolbox;
    @Override public CarriedToolboxBlockEntity expansion$getToolbox() { return expansion$toolbox; }
    @Override public void expansion$setToolbox(CarriedToolboxBlockEntity toolbox) { expansion$toolbox = toolbox; }
}
