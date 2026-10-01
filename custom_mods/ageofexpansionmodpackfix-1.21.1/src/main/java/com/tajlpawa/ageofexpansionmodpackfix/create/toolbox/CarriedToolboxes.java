package com.tajlpawa.ageofexpansionmodpackfix.create.toolbox;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.tajlpawa.ageofexpansionmodpackfix.curios.ToolboxCurios;
import java.util.UUID;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;

public final class CarriedToolboxes {
    // This position is only a protocol address; no block or chunk is created there.
    public static final BlockPos ADDRESS = new BlockPos(0, -2048, 0);
    public static final String BINDING = "ExpansionCarriedToolbox";

    private CarriedToolboxes() {}

    public static boolean isToolbox(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BlockItem item && item.getBlock() instanceof ToolboxBlock;
    }

    public static CarriedToolboxBlockEntity get(Player player) {
        if (player == null) return null;
        var holder = (CarriedToolboxHolder) player;
        ItemStack stack = ModList.get().isLoaded("curios") && player.isAlive() && !player.isSpectator()
                ? ToolboxCurios.find(player) : ItemStack.EMPTY;
        if (stack.isEmpty()) {
            holder.expansion$setToolbox(null);
            return null;
        }
        UUID id = stack.get(AllDataComponents.TOOLBOX_UUID);
        if (id == null) {
            if (player.level().isClientSide) return null;
            stack.set(AllDataComponents.TOOLBOX_UUID, id = UUID.randomUUID());
        }
        var box = holder.expansion$getToolbox();
        if (box != null && player.level().isClientSide && id.equals(box.getUniqueId()) && box.canRefreshClient(player, stack)) {
            box.refreshClientItem(stack);
        } else if (box == null || !box.isOwnedBy(player) || !id.equals(box.getUniqueId())) {
            box = new CarriedToolboxBlockEntity(player, stack);
            holder.expansion$setToolbox(box);
        }
        return box;
    }

    public static boolean isBinding(CompoundTag data) {
        return data.hasUUID(BINDING) && ADDRESS.equals(NBTHelper.readBlockPos(data, "Pos"));
    }

    public static boolean matches(CompoundTag data, ToolboxBlockEntity box) {
        return box != null && isBinding(data) && data.getUUID(BINDING).equals(box.getUniqueId());
    }

    public static BlockEntity resolveLink(Player player, BlockPos pos, int hotbarSlot) {
        if (!ADDRESS.equals(pos)) return player.level().getBlockEntity(pos);
        var box = get(player);
        var data = player.getPersistentData().getCompound("CreateToolboxData").getCompound(String.valueOf(hotbarSlot));
        return matches(data, box) ? box : null;
    }

    // Validate portable bindings every tick and use Create's own replenishment/return algorithm.
    public static void tick(ServerPlayer player) {
        var box = get(player);
        var compound = player.getPersistentData().getCompound("CreateToolboxData");
        boolean changed = false;
        for (int i = 0; i < 9; i++) {
            String key = String.valueOf(i);
            var data = compound.getCompound(key);
            if (!ADDRESS.equals(NBTHelper.readBlockPos(data, "Pos"))) continue;
            int slot = data.getInt("Slot");
            if (!matches(data, box) || slot < 0 || slot >= 8) {
                compound.remove(key);
                changed = true;
            } else {
                box.connectPlayer(slot, player, i);
            }
        }
        if (changed) ToolboxHandler.syncData(player);
        if (box != null) box.tickContents();
    }
}
