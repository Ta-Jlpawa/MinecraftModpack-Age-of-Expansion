package com.tajlpawa.ageofexpansionmodpackfix.mixin.create.toolbox;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.simibubi.create.content.equipment.toolbox.RadialToolboxMenu;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandlerClient;
import com.tajlpawa.ageofexpansionmodpackfix.create.toolbox.CarriedToolboxBlockEntity;
import com.tajlpawa.ageofexpansionmodpackfix.create.toolbox.CarriedToolboxes;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import java.util.List;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ToolboxHandlerClient.class, remap = false)
public abstract class CarriedToolboxClientMixin {
    @Unique private static boolean expansion$opening;

    // A release closes the radial menu; it must not queue another open after the screen becomes null.
    @Inject(method = "onKeyInput", at = @At("HEAD"), cancellable = true)
    private static void expansion$guard(int key, boolean pressed, CallbackInfo ci) {
        var mc = Minecraft.getInstance();
        if (expansion$opening || CarriedToolboxes.get(mc.player) != null && (!pressed || mc.screen != null)) ci.cancel();
    }

    // Keep the worn box first, use native box selection when there is more than one choice.
    @Inject(method = "onKeyInput", at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V", shift = At.Shift.AFTER), cancellable = true)
    private static void expansion$open(int key, boolean pressed, CallbackInfo ci, @Local List<ToolboxBlockEntity> boxes) {
        var player = Minecraft.getInstance().player;
        var box = CarriedToolboxes.get(player);
        if (box == null) return;
        boxes.removeIf(CarriedToolboxBlockEntity.class::isInstance);
        boxes.addFirst(box);
        RadialToolboxMenu screen;
        if (boxes.size() > 1) screen = new RadialToolboxMenu(boxes, RadialToolboxMenu.State.SELECT_BOX, null);
        else {
            var data = player.getPersistentData().getCompound("CreateToolboxData").getCompound(String.valueOf(player.getInventory().selected));
            boolean connected = CarriedToolboxes.matches(data, box);
            screen = new RadialToolboxMenu(boxes, connected ? RadialToolboxMenu.State.SELECT_ITEM_UNEQUIP : RadialToolboxMenu.State.SELECT_ITEM, box);
            if (connected) screen.prevSlot(data.getInt("Slot"));
        }
        expansion$opening = true;
        ScreenOpener.open(screen);
        Minecraft.getInstance().tell(() -> expansion$opening = false);
        ci.cancel();
    }

    // Portable bindings are in range only while their UUID still matches the active worn toolbox.
    @ModifyReceiver(method = "renderOverlay", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/gui/AllGuiTextures;render(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
    private static AllGuiTextures expansion$wornOverlay(AllGuiTextures texture, GuiGraphics graphics, int x, int y,
            @Local Player player, @Local String key) {
        var data = player.getPersistentData().getCompound("CreateToolboxData").getCompound(key);
        if (!CarriedToolboxes.isBinding(data)) return texture;
        boolean selected = texture == AllGuiTextures.TOOLBELT_SELECTED_ON || texture == AllGuiTextures.TOOLBELT_SELECTED_OFF;
        boolean available = CarriedToolboxes.matches(data, CarriedToolboxes.get(player));
        return available ? selected ? AllGuiTextures.TOOLBELT_SELECTED_ON : AllGuiTextures.TOOLBELT_HOTBAR_ON
                : selected ? AllGuiTextures.TOOLBELT_SELECTED_OFF : AllGuiTextures.TOOLBELT_HOTBAR_OFF;
    }
}
