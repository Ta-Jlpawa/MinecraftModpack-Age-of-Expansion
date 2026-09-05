package com.tajlpawa.ageofexpansionmodpackfix.mekanism;

import java.util.List;
import mekanism.api.tier.AlloyTier;
import mekanism.common.content.network.transmitter.IUpgradeableTransmitter;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

public class TransmitterInstallerItem extends Item {
    private final AlloyTier tier;

    public TransmitterInstallerItem(AlloyTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public AlloyTier getTier() {
        return tier;
    }

    public boolean canUpgrade(TileEntityTransmitter tile) {
        boolean supported = switch (tile.getTransmitterType()) {
            case UNIVERSAL_CABLE, MECHANICAL_PIPE, PRESSURIZED_TUBE, LOGISTICAL_TRANSPORTER, THERMODYNAMIC_CONDUCTOR -> true;
            default -> false;
        };
        return supported && tile.getTransmitter() instanceof IUpgradeableTransmitter<?> transmitter
              && transmitter.getTier().getBaseTierLevel() < tier.getBaseTierLevel();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.mayBuild()
              || !context.getLevel().mayInteract(player, context.getClickedPos())
              || !player.mayUseItemAt(context.getClickedPos(), context.getClickedFace(), context.getItemInHand())) {
            return InteractionResult.FAIL;
        }
        if (!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof TileEntityTransmitter tile)
              || !canUpgrade(tile)) {
            return InteractionResult.PASS;
        }
        if (!context.getLevel().isClientSide) {
            // Reuse Mekanism's eight-transmitter upgrade and buffer/connection migration.
            tile.onAlloyInteraction(player, context.getItemInHand(), tier);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.ageofexpansionmodpackfix.transmitter_installer")
              .withStyle(ChatFormatting.GRAY));
    }
}
