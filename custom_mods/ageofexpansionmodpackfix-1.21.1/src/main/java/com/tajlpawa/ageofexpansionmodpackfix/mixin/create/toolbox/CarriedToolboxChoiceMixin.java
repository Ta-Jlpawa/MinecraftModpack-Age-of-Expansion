package com.tajlpawa.ageofexpansionmodpackfix.mixin.create.toolbox;

import com.simibubi.create.content.equipment.toolbox.RadialToolboxMenu;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.tajlpawa.ageofexpansionmodpackfix.create.toolbox.CarriedToolboxBlockEntity;
import com.tajlpawa.ageofexpansionmodpackfix.create.toolbox.CarriedToolboxes;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RadialToolboxMenu.class, remap = false)
public abstract class CarriedToolboxChoiceMixin {
    @Shadow private RadialToolboxMenu.State state;
    @Shadow private ToolboxBlockEntity selectedBox;

    // Native selection stays on the same radial screen; restore the bound compartment's unequip action.
    @Inject(method = "mouseClicked", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/toolbox/RadialToolboxMenu;selectedBox:Lcom/simibubi/create/content/equipment/toolbox/ToolboxBlockEntity;", opcode = org.objectweb.asm.Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void expansion$choose(double x, double y, int button, CallbackInfoReturnable<Boolean> ci) {
        if (!(selectedBox instanceof CarriedToolboxBlockEntity)) return;
        var player = Minecraft.getInstance().player;
        var data = player.getPersistentData().getCompound("CreateToolboxData").getCompound(String.valueOf(player.getInventory().selected));
        if (!CarriedToolboxes.matches(data, selectedBox)) return;
        state = RadialToolboxMenu.State.SELECT_ITEM_UNEQUIP;
        ((RadialToolboxMenu) (Object) this).prevSlot(data.getInt("Slot"));
    }
}
