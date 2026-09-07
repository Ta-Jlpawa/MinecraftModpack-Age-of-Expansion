package com.tajlpawa.ageofexpansionmodpackfix.create;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class StressRouterMenu extends AbstractContainerMenu {
    public final StressRouterBlockEntity router;
    private final ContainerData data;

    public StressRouterMenu(int id, Inventory inventory) { this(id, inventory, (StressRouterBlockEntity) null); }
    public StressRouterMenu(int id, Inventory inventory, net.minecraft.network.RegistryFriendlyByteBuf buf) {
        this(id, inventory);
        for (int i = 0; i < 12; i++) {
            int value = buf.readInt();
            data.set(i * 2, value & 65535);
            data.set(i * 2 + 1, value >>> 16);
        }
    }
    public StressRouterMenu(int id, Inventory inventory, StressRouterBlockEntity router) {
        super(StressRouterContent.MENU.get(), id);
        this.router = router;
        data = router == null ? new SimpleContainerData(24) : new ContainerData() {
            @Override public int get(int index) {
                int value = router.menuValue(index / 2);
                return index % 2 == 0 ? value & 65535 : (value >>> 16) & 65535;
            }
            @Override public void set(int index, int value) {}
            @Override public int getCount() { return 24; }
        };
        addDataSlots(data);
    }
    // Container data travels as 16-bit values; preserve large SU values and signed RPM.
    public int value(int index) { return (data.get(index * 2) & 65535) | (data.get(index * 2 + 1) << 16); }
    @Override public boolean stillValid(Player player) {
        return router == null || (!router.isRemoved() && router.getLevel().getBlockEntity(router.getBlockPos()) == router
                && player.canInteractWithBlock(router.getBlockPos(), 4));
    }
    @Override public ItemStack quickMoveStack(Player player, int slot) { return ItemStack.EMPTY; }
}
