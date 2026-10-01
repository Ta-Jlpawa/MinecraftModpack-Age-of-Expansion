package com.tajlpawa.ageofexpansionmodpackfix.mixin.create.toolbox;

import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ToolboxBlockEntity.class, remap = false)
public interface ToolboxBlockEntityAccess {
    @Accessor("inventory") ToolboxInventory expansion$getInventory();
    @Invoker("tickPlayers") void expansion$tickPlayers();
}
