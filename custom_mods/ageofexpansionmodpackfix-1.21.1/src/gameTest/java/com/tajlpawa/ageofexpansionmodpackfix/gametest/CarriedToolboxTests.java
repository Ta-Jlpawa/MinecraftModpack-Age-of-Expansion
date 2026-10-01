package com.tajlpawa.ageofexpansionmodpackfix.gametest;

import com.mojang.authlib.GameProfile;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.toolbox.*;
import com.tajlpawa.ageofexpansionmodpackfix.create.toolbox.*;
import com.tajlpawa.ageofexpansionmodpackfix.mixin.create.toolbox.ToolboxBlockEntityAccess;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

@GameTestHolder("ageofexpansionmodpackfix")
public final class CarriedToolboxTests {
    @GameTestGenerator public static Collection<TestFunction> tests() {
        var tests = new ArrayList<TestFunction>();
        if (ModList.get().isLoaded("curios")) {
            add(tests, "belt_colors_cosmetic_and_inactive", CarriedToolboxTests::slots);
            add(tests, "worn_toolbox_nearest_and_private", CarriedToolboxTests::nearest);
            add(tests, "native_equip_replenish_move_and_return", CarriedToolboxTests::replenish);
            add(tests, "native_deposit_saves_item_and_syncs_counts", CarriedToolboxTests::deposit);
            add(tests, "swap_remove_and_stale_binding", CarriedToolboxTests::swap);
            add(tests, "serialized_item_places_with_same_contents", CarriedToolboxTests::saveAndPlace);
            add(tests, "invalid_packets_and_no_worn_access", CarriedToolboxTests::invalid);
            add(tests, "world_toolbox_binding_still_works", CarriedToolboxTests::world);
        }
        return tests;
    }

    private static void add(Collection<TestFunction> tests, String name, Consumer<GameTestHelper> run) {
        tests.add(new TestFunction("ageofexpansionmodpackfix:carried_toolbox", "ageofexpansionmodpackfix:" + name,
                "ageofexpansionmodpackfix:router_empty", 100, 0, true, run));
    }

    private static ServerPlayer player(GameTestHelper h) {
        var player = FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "ToolboxTest"));
        player.setPos(h.absolutePos(new BlockPos(5, 2, 5)).getCenter());
        return player;
    }

    private static ItemStack box(Item item, int count) {
        var inventory = new ToolboxInventory(null);
        inventory.insertItem(0, new ItemStack(item, count), false);
        var stack = AllBlocks.TOOLBOXES.get(DyeColor.BROWN).asStack();
        stack.set(AllDataComponents.TOOLBOX_INVENTORY, inventory);
        return stack;
    }

    private static void wear(ServerPlayer player, ItemStack stack) {
        CuriosApi.getCuriosInventory(player).orElseThrow().getStacksHandler("belt").orElseThrow().getStacks().setStackInSlot(0, stack);
    }

    private static int count(ItemStack stack) {
        var inventory = stack.get(AllDataComponents.TOOLBOX_INVENTORY);
        return inventory == null ? 0 : inventory.takeFromCompartment(10000, 0, true).getCount();
    }

    private static void slots(GameTestHelper h) {
        var player = player(h);
        var context = new SlotContext("belt", player, 0, false, true);
        for (var color : DyeColor.values()) h.assertTrue(CuriosApi.isStackValid(context, AllBlocks.TOOLBOXES.get(color).asStack()), "Every toolbox color fits belt");
        h.assertTrue(!CuriosApi.isStackValid(new SlotContext("head", player, 0, false, true), box(Items.STONE, 32)), "Not a head accessory");
        var inventory = CuriosApi.getCuriosInventory(player).orElseThrow();
        var belt = inventory.getStacksHandler("belt").orElseThrow();
        if (belt.getCosmeticStacks().getSlots() == 0) belt.getCosmeticStacks().grow(1);
        belt.getCosmeticStacks().setStackInSlot(0, box(Items.STONE, 32));
        h.assertTrue(CarriedToolboxes.get(player) == null, "Cosmetic toolbox gives no storage");
        wear(player, box(Items.STONE, 32));
        h.assertTrue(CarriedToolboxes.get(player) != null, "Functional belt enables storage");
        inventory.setSlotActive("belt", 0, false);
        h.assertTrue(CarriedToolboxes.get(player) == null, "Inactive slot gives no storage");
        h.succeed();
    }

    private static ToolboxBlockEntity placed(GameTestHelper h, BlockPos pos) {
        h.setBlock(pos, AllBlocks.TOOLBOXES.get(DyeColor.BLUE).getDefaultState());
        var box = (ToolboxBlockEntity) h.getBlockEntity(pos);
        box.setUniqueId(UUID.randomUUID());
        box.initialize();
        return box;
    }

    private static void nearest(GameTestHelper h) {
        var player = player(h);
        wear(player, box(Items.COBBLESTONE, 64));
        var world = placed(h, new BlockPos(5, 2, 5));
        var carried = CarriedToolboxes.get(player);
        h.assertTrue(ToolboxHandler.getNearest(h.getLevel(), player, 1).getFirst() == carried, "Worn box wins even over a world box at player's position");
        var boxes = ToolboxHandler.getNearest(h.getLevel(), player, 8);
        h.assertTrue(boxes.getFirst() == carried && boxes.contains(world), "World toolboxes stay available");
        var other = player(h);
        h.assertTrue(!ToolboxHandler.getNearest(h.getLevel(), other, 8).contains(carried) && !ToolboxHandler.withinRange(other, carried), "Other player cannot see/use carried box");
        h.assertTrue(!ToolboxHandler.toolboxes.get(h.getLevel()).containsValue(carried), "Carried adapter is never registered in world");
        h.succeed();
    }

    private static void replenish(GameTestHelper h) {
        var player = player(h);
        var stack = box(Items.COBBLESTONE, 64);
        var before = stack.copy();
        wear(player, stack);
        new ToolboxEquipPacket(CarriedToolboxes.ADDRESS, 0, 0).handle(player);
        CarriedToolboxes.tick(player);
        h.assertTrue(player.getInventory().getItem(0).getCount() == 32 && count(stack) == 32, "Native binding replenishes half a stack");
        h.assertTrue(count(before) == 64, "Item component snapshots are not mutated");
        player.setPos(player.getX() + 1000, player.getY(), player.getZ());
        player.getInventory().getItem(0).shrink(10);
        CarriedToolboxes.tick(player);
        h.assertTrue(player.getInventory().getItem(0).getCount() == 32 && count(stack) == 22, "Moving never interrupts carried replenishment");
        new ToolboxEquipPacket(null, -5, 0).handle(player);
        h.assertTrue(player.getInventory().getItem(0).isEmpty() && count(stack) == 54, "Native unequip returns held items to worn box");
        h.succeed();
    }

    private static void deposit(GameTestHelper h) {
        var player = player(h);
        var stack = box(Items.COBBLESTONE, 16);
        wear(player, stack);
        CarriedToolboxes.get(player);
        var before = stack.copy();
        player.getInventory().setItem(9, new ItemStack(Items.COBBLESTONE, 40));
        player.getInventory().setItem(10, new ItemStack(Items.DIRT, 7));
        new ToolboxDisposeAllPacket(CarriedToolboxes.ADDRESS).handle(player);
        h.assertTrue(count(stack) == 56 && player.getInventory().getItem(9).isEmpty(), "Native deposit saves to worn item");
        h.assertTrue(player.getInventory().getItem(10).getCount() == 7, "Unmatched filters are preserved in player inventory");
        h.assertTrue(!ItemStack.matches(before, stack), "Count-only changes are detected by Curios sync");
        h.assertTrue(stack.get(DataComponents.CUSTOM_DATA).copyTag().getInt("ExpansionToolboxRevision") > 0, "Sync revision updated");
        h.succeed();
    }

    private static void swap(GameTestHelper h) {
        var player = player(h);
        var original = box(Items.COBBLESTONE, 64);
        wear(player, original);
        new ToolboxEquipPacket(CarriedToolboxes.ADDRESS, 0, 0).handle(player);
        CarriedToolboxes.tick(player);
        var replacement = box(Items.DIRT, 48);
        wear(player, replacement);
        CarriedToolboxes.tick(player);
        h.assertTrue(count(original) == 32 && count(replacement) == 48 && player.getInventory().getItem(0).getCount() == 32, "Swap preserves both inventories and held stack");
        h.assertTrue(player.getPersistentData().getCompound("CreateToolboxData").isEmpty(), "Old UUID binding detached");
        new ToolboxEquipPacket(CarriedToolboxes.ADDRESS, 0, 1).handle(player);
        CarriedToolboxes.tick(player);
        wear(player, ItemStack.EMPTY);
        CarriedToolboxes.tick(player);
        h.assertTrue(CarriedToolboxes.get(player) == null && player.getPersistentData().getCompound("CreateToolboxData").isEmpty(), "Removal invalidates access and bindings");
        h.assertTrue(count(replacement) == 16 && player.getInventory().getItem(1).getCount() == 32, "Removal conserves all items");
        h.succeed();
    }

    private static void saveAndPlace(GameTestHelper h) {
        var player = player(h);
        var stack = box(Items.COBBLESTONE, 64);
        stack.set(DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal("Portable Test"));
        wear(player, stack);
        new ToolboxEquipPacket(CarriedToolboxes.ADDRESS, 0, 0).handle(player);
        CarriedToolboxes.tick(player);
        var loaded = ItemStack.parse(h.getLevel().registryAccess(), stack.save(h.getLevel().registryAccess())).orElseThrow();
        wear(player, loaded);
        ((CarriedToolboxHolder) player).expansion$setToolbox(null);
        CarriedToolboxes.tick(player);
        h.assertTrue(count(loaded) == 32 && CarriedToolboxes.get(player).getDisplayName().getString().equals("Portable Test"), "Serialized stack restores contents/name and binding");
        wear(player, ItemStack.EMPTY);
        CarriedToolboxes.tick(player);
        var pos = new BlockPos(5, 2, 6);
        var world = placed(h, pos);
        var state = world.getBlockState();
        state.getBlock().setPlacedBy(h.getLevel(), h.absolutePos(pos), state, player, loaded);
        var contents = ((ToolboxBlockEntityAccess) world).expansion$getInventory();
        h.assertTrue(contents.takeFromCompartment(10000, 0, true).getCount() == 32 && world.getUniqueId().equals(loaded.get(AllDataComponents.TOOLBOX_UUID)), "Placing restores native inventory and UUID");
        h.succeed();
    }

    private static void invalid(GameTestHelper h) {
        var player = player(h);
        var stack = box(Items.COBBLESTONE, 64);
        wear(player, stack);
        new ToolboxEquipPacket(CarriedToolboxes.ADDRESS, 0, -1).handle(player);
        new ToolboxEquipPacket(CarriedToolboxes.ADDRESS, 0, 9).handle(player);
        new ToolboxEquipPacket(CarriedToolboxes.ADDRESS, 8, 0).handle(player);
        h.assertTrue(count(stack) == 64 && player.getPersistentData().getCompound("CreateToolboxData").isEmpty(), "Invalid slot packets never mutate storage");
        wear(player, ItemStack.EMPTY);
        player.getInventory().setItem(9, new ItemStack(Items.COBBLESTONE, 10));
        new ToolboxEquipPacket(CarriedToolboxes.ADDRESS, 0, 0).handle(player);
        new ToolboxDisposeAllPacket(CarriedToolboxes.ADDRESS).handle(player);
        h.assertTrue(count(stack) == 64 && player.getInventory().getItem(9).getCount() == 10, "No access after removal");
        h.succeed();
    }

    private static void world(GameTestHelper h) {
        var player = player(h);
        wear(player, box(Items.DIRT, 48));
        var world = placed(h, new BlockPos(5, 2, 6));
        ((ToolboxBlockEntityAccess) world).expansion$getInventory().insertItem(0, new ItemStack(Items.COBBLESTONE, 64), false);
        new ToolboxEquipPacket(world.getBlockPos(), 0, 0).handle(player);
        ((ToolboxBlockEntityAccess) world).expansion$tickPlayers();
        h.assertTrue(player.getInventory().getItem(0).is(Items.COBBLESTONE) && player.getInventory().getItem(0).getCount() == 32, "World packet and replenishment unchanged");
        new ToolboxEquipPacket(null, -5, 0).handle(player);
        h.assertTrue(player.getInventory().getItem(0).isEmpty() && ((ToolboxBlockEntityAccess) world).expansion$getInventory().takeFromCompartment(10000, 0, true).getCount() == 64, "World return unaffected by worn box");
        h.succeed();
    }

}
