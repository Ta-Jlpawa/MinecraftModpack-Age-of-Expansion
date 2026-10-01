package com.tajlpawa.ageofexpansionmodpackfix.create.toolbox;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import com.tajlpawa.ageofexpansionmodpackfix.curios.ToolboxCurios;
import com.tajlpawa.ageofexpansionmodpackfix.mixin.create.toolbox.ToolboxBlockEntityAccess;
import java.lang.ref.WeakReference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

// An item-backed adapter for Create's inventory; never register or tick it as a world block.
public final class CarriedToolboxBlockEntity extends ToolboxBlockEntity {
    private final WeakReference<Player> owner;
    private ItemStack stack;
    private boolean loading;
    private CompoundTag snapshot;
    private ToolboxInventory source;
    private boolean loaded;

    public CarriedToolboxBlockEntity(Player player, ItemStack stack) {
        super(AllBlockEntityTypes.TOOLBOX.get(), CarriedToolboxes.ADDRESS, ((BlockItem) stack.getItem()).getBlock().defaultBlockState());
        this.owner = new WeakReference<>(player);
        this.stack = stack;
        setLevel(player.level());
        setUniqueId(stack.get(AllDataComponents.TOOLBOX_UUID));
        setCustomName(stack.get(DataComponents.CUSTOM_NAME));
        refreshFromItem();
    }

    public ToolboxInventory contents() {
        return ((ToolboxBlockEntityAccess) (Object) this).expansion$getInventory();
    }

    public boolean isOwnedBy(Player player) {
        return owner.get() == player && getLevel() == player.level() && ToolboxCurios.find(player) == stack;
    }

    public boolean canRefreshClient(Player player, ItemStack incoming) {
        return owner.get() == player && getLevel() == player.level() && incoming.is(stack.getItem());
    }

    // Preserve the object already used by an open radial menu when Curios replaces its item stack.
    public void refreshClientItem(ItemStack incoming) {
        stack = incoming;
        setCustomName(stack.get(DataComponents.CUSTOM_NAME));
        refreshFromItem();
    }

    public void refreshFromItem() {
        var stored = stack.get(AllDataComponents.TOOLBOX_INVENTORY);
        if (loaded && source == stored) return;
        loaded = true;
        source = stored;
        CompoundTag data = stored == null ? new ToolboxInventory(null).serializeNBT(level.registryAccess())
                : stored.serializeNBT(level.registryAccess());
        if (data.equals(snapshot)) return;
        loading = true;
        contents().deserializeNBT(level.registryAccess(), data);
        loading = false;
        snapshot = data;
    }

    public void tickContents() {
        ((ToolboxBlockEntityAccess) (Object) this).expansion$tickPlayers();
    }

    @Override public void connectPlayer(int slot, Player player, int hotbarSlot) {
        if (!isOwnedBy(player)) return;
        super.connectPlayer(slot, player, hotbarSlot);
        player.getPersistentData().getCompound("CreateToolboxData").getCompound(String.valueOf(hotbarSlot))
                .putUUID(CarriedToolboxes.BINDING, getUniqueId());
    }

    @Override public void initialize() {}
    @Override public void lazyTick() {}
    @Override public void tick() { tickContents(); }
    @Override public void setChanged() { persist(); }
    @Override public void sendData() { persist(); }

    private void persist() {
        if (loading || stack == null || level == null || level.isClientSide) return;
        CompoundTag data = contents().serializeNBT(level.registryAccess());
        if (data.equals(snapshot)) return;
        // Copy the mutable Create component; a revision makes count-only changes visible to Curios sync.
        var saved = new ToolboxInventory(null);
        saved.deserializeNBT(level.registryAccess(), data);
        stack.set(AllDataComponents.TOOLBOX_INVENTORY, saved);
        var custom = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        custom.putInt("ExpansionToolboxRevision", custom.getInt("ExpansionToolboxRevision") + 1);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(custom));
        snapshot = data;
    }
}
