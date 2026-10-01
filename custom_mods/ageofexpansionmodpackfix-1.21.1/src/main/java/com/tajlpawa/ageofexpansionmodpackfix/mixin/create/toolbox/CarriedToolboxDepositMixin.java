package com.tajlpawa.ageofexpansionmodpackfix.mixin.create.toolbox;

import com.simibubi.create.content.equipment.toolbox.ToolboxDisposeAllPacket;
import com.tajlpawa.ageofexpansionmodpackfix.create.toolbox.CarriedToolboxes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ToolboxDisposeAllPacket.class, remap = false)
public abstract class CarriedToolboxDepositMixin {
    @Redirect(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private BlockEntity expansion$resolve(Level level, BlockPos pos, ServerPlayer player) {
        return CarriedToolboxes.ADDRESS.equals(pos) ? CarriedToolboxes.get(player) : level.getBlockEntity(pos);
    }

    @Redirect(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;distanceToSqr(DDD)D"))
    private double expansion$range(ServerPlayer player, double x, double y, double z) {
        return CarriedToolboxes.ADDRESS.equals(((ToolboxDisposeAllPacket) (Object) this).toolboxPos()) ? 0 : player.distanceToSqr(x, y, z);
    }
}
