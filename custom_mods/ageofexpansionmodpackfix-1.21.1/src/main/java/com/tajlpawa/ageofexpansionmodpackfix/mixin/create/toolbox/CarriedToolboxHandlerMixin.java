package com.tajlpawa.ageofexpansionmodpackfix.mixin.create.toolbox;

import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.tajlpawa.ageofexpansionmodpackfix.create.toolbox.CarriedToolboxBlockEntity;
import com.tajlpawa.ageofexpansionmodpackfix.create.toolbox.CarriedToolboxes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ToolboxHandler.class, remap = false)
public abstract class CarriedToolboxHandlerMixin {
    @Inject(method = "getNearest", at = @At("RETURN"), cancellable = true)
    private static void expansion$nearest(LevelAccessor world, Player player, int maxAmount, CallbackInfoReturnable<List<ToolboxBlockEntity>> ci) {
        if (maxAmount <= 0 || world != player.level()) return;
        var box = CarriedToolboxes.get(player);
        if (box == null) return;
        var boxes = new ArrayList<ToolboxBlockEntity>();
        boxes.add(box);
        ci.getReturnValue().stream().limit(maxAmount - 1L).forEach(boxes::add);
        ci.setReturnValue(boxes);
    }

    @Inject(method = "withinRange", at = @At("HEAD"), cancellable = true)
    private static void expansion$range(Player player, ToolboxBlockEntity box, CallbackInfoReturnable<Boolean> ci) {
        if (box instanceof CarriedToolboxBlockEntity carried) ci.setReturnValue(carried.isOwnedBy(player) && player.isAlive() && !player.isSpectator());
    }

    @Inject(method = "entityTick", at = @At("HEAD"))
    private static void expansion$tick(Entity entity, Level level, CallbackInfo ci) {
        if (entity instanceof ServerPlayer player && !level.isClientSide) CarriedToolboxes.tick(player);
        else if (entity instanceof Player player && level.isClientSide) CarriedToolboxes.get(player);
    }

    @Redirect(method = "entityTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isLoaded(Lnet/minecraft/core/BlockPos;)Z"))
    private static boolean expansion$skipWorldValidation(Level level, BlockPos pos) {
        return !CarriedToolboxes.ADDRESS.equals(pos) && level.isLoaded(pos);
    }

    @Redirect(method = "unequip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private static BlockEntity expansion$unequip(Level level, BlockPos pos, Player player, int hotbarSlot, boolean keepItems) {
        return CarriedToolboxes.resolveLink(player, pos, hotbarSlot);
    }
}
