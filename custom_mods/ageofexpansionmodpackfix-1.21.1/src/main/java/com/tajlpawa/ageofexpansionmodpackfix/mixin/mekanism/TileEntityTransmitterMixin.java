package com.tajlpawa.ageofexpansionmodpackfix.mixin.mekanism;

import com.tajlpawa.ageofexpansionmodpackfix.mekanism.TransmitterInstallerItem;
import mekanism.api.tier.IAlloyTier;
import mekanism.common.content.network.transmitter.IUpgradeableTransmitter;
import mekanism.common.content.network.transmitter.Transmitter;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityTransmitter.class, remap = false)
public abstract class TileEntityTransmitterMixin {
    // Only installers may enter the original upgrade path, even if the alloy config is re-enabled.
    @Inject(method = "onAlloyInteraction", at = @At("HEAD"), cancellable = true)
    private void requireInstaller(Player player, ItemStack stack, IAlloyTier tier, CallbackInfo ci) {
        TileEntityTransmitter tile = (TileEntityTransmitter) (Object) this;
        if (!(stack.getItem() instanceof TransmitterInstallerItem installer) || stack.isEmpty()
              || installer.getTier() != tier || !installer.canUpgrade(tile)
              || tile.getLevel() == null || tile.getLevel().isClientSide || !player.mayBuild()) {
            ci.cancel();
        }
    }

    // Change only eligibility; native ordering, eight-item limit and data transfer stay intact.
    @Redirect(method = "onAlloyInteraction", at = @At(value = "INVOKE",
          target = "Lmekanism/common/content/network/transmitter/IUpgradeableTransmitter;canUpgrade(Lmekanism/api/tier/IAlloyTier;)Z"))
    private boolean allowLowerTiers(IUpgradeableTransmitter<?> upgradeable, IAlloyTier requestedTier,
          Player player, ItemStack stack, IAlloyTier tier) {
        if (stack.getItem() instanceof TransmitterInstallerItem installer
              && upgradeable instanceof Transmitter<?, ?, ?> transmitter) {
            return installer.canUpgrade(transmitter.getTransmitterTile())
                  && transmitter.getLevel().mayInteract(player, transmitter.getBlockPos())
                  && player.mayUseItemAt(transmitter.getBlockPos(), Direction.UP, stack);
        }
        return false;
    }
}
