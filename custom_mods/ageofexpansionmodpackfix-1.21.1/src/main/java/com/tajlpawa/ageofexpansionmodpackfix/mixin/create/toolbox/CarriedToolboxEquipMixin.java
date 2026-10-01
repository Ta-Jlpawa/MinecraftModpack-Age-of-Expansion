package com.tajlpawa.ageofexpansionmodpackfix.mixin.create.toolbox;

import com.simibubi.create.content.equipment.toolbox.ToolboxEquipPacket;
import com.tajlpawa.ageofexpansionmodpackfix.create.toolbox.CarriedToolboxes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ToolboxEquipPacket.class, remap = false)
public abstract class CarriedToolboxEquipMixin {
    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void expansion$validate(ServerPlayer player, CallbackInfo ci) {
        var packet = (ToolboxEquipPacket) (Object) this;
        if (CarriedToolboxes.ADDRESS.equals(packet.toolboxPos()) && (packet.hotbarSlot() < 0 || packet.hotbarSlot() >= 9 || player.isSpectator())) ci.cancel();
    }

    @Redirect(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private BlockEntity expansion$resolve(Level level, BlockPos pos, ServerPlayer player) {
        return CarriedToolboxes.ADDRESS.equals(pos) ? CarriedToolboxes.get(player) : level.getBlockEntity(pos);
    }

    @Redirect(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;distanceToSqr(DDD)D"))
    private double expansion$range(ServerPlayer player, double x, double y, double z) {
        return CarriedToolboxes.ADDRESS.equals(((ToolboxEquipPacket) (Object) this).toolboxPos()) ? 0 : player.distanceToSqr(x, y, z);
    }
}
