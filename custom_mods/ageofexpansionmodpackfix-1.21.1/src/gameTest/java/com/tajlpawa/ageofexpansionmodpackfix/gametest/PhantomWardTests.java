package com.tajlpawa.ageofexpansionmodpackfix.gametest;

import com.tajlpawa.ageofexpansionmodpackfix.minecraft.PhantomWardItems;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerSpawnPhantomsEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import top.theillusivec4.curios.api.CuriosApi;

@GameTestHolder("ageofexpansionmodpackfix")
public final class PhantomWardTests {
    @GameTestGenerator public static Collection<TestFunction> tests() {
        var tests = new ArrayList<TestFunction>();
        add(tests, "helmet_equip_remove_and_player_isolation", PhantomWardTests::helmet);
        add(tests, "inventory_and_hands_do_not_protect", PhantomWardTests::inventory);
        add(tests, "insomnia_stat_is_not_reset", PhantomWardTests::insomnia);
        if (ModList.get().isLoaded("curios")) add(tests, "curios_head_equip_remove_and_cosmetic", PhantomWardTests::curios);
        if (ModList.get().isLoaded("immersiveengineering")) add(tests, "cat_headband_native_recipe", PhantomWardTests::recipe);
        add(tests, "old_headband_id_loads_as_new_id", PhantomWardTests::legacyId);
        return tests;
    }
    private static void add(Collection<TestFunction> tests, String name, java.util.function.Consumer<GameTestHelper> run) {
        tests.add(new TestFunction("ageofexpansionmodpackfix:phantom_ward", "ageofexpansionmodpackfix:" + name,
                "ageofexpansionmodpackfix:router_empty", 100, 0, true, run));
    }
    private static ItemStack ward() { return PhantomWardItems.CAT_EAR_HEADBAND.get().getDefaultInstance(); }
    private static PlayerSpawnPhantomsEvent attempt(Player player) {
        return NeoForge.EVENT_BUS.post(new PlayerSpawnPhantomsEvent(player, 3));
    }
    private static void helmet(GameTestHelper h) {
        var wearer = h.makeMockPlayer(GameType.SURVIVAL);
        var other = h.makeMockPlayer(GameType.SURVIVAL);
        h.assertTrue(Equipable.get(ward()).getEquipmentSlot() == EquipmentSlot.HEAD, "Vanilla helmet slot supported");
        h.assertTrue(ward().canEquip(EquipmentSlot.HEAD, wearer) && !ward().canEquip(EquipmentSlot.CHEST, wearer), "Actual armor slot accepts only head");
        wearer.setItemSlot(EquipmentSlot.HEAD, ward());
        var denied = attempt(wearer);
        h.assertTrue(denied.getResult() == PlayerSpawnPhantomsEvent.Result.DENY
                && !denied.shouldSpawnPhantoms(h.getLevel(), wearer.blockPosition()), "Spawner skips wearer");
        h.assertTrue(attempt(other).getResult() == PlayerSpawnPhantomsEvent.Result.DEFAULT, "Other player unaffected");
        wearer.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        h.assertTrue(attempt(wearer).getResult() == PlayerSpawnPhantomsEvent.Result.DEFAULT, "Removal immediately restores vanilla rules");
        h.succeed();
    }
    private static void inventory(GameTestHelper h) {
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        player.getInventory().setItem(9, ward());
        player.setItemSlot(EquipmentSlot.MAINHAND, ward());
        player.setItemSlot(EquipmentSlot.OFFHAND, ward());
        h.assertTrue(attempt(player).getResult() == PlayerSpawnPhantomsEvent.Result.DEFAULT, "Only wearing protects");
        var event = new PlayerSpawnPhantomsEvent(player, 3);
        event.setResult(PlayerSpawnPhantomsEvent.Result.ALLOW);
        NeoForge.EVENT_BUS.post(event);
        h.assertTrue(event.getResult() == PlayerSpawnPhantomsEvent.Result.ALLOW && event.getPhantomsToSpawn() == 3, "Unprotected event remains unchanged");
        h.succeed();
    }
    private static void insomnia(GameTestHelper h) {
        var player = net.neoforged.neoforge.common.util.FakePlayerFactory.get(h.getLevel(),
                new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "WardTest"));
        var stat = net.minecraft.stats.Stats.CUSTOM.get(net.minecraft.stats.Stats.TIME_SINCE_REST);
        player.getStats().setValue(player, stat, 100000);
        player.setItemSlot(EquipmentSlot.HEAD, ward());
        h.assertTrue(attempt(player).getResult() == PlayerSpawnPhantomsEvent.Result.DENY, "Protection beyond three days");
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        h.assertTrue(attempt(player).getResult() == PlayerSpawnPhantomsEvent.Result.DEFAULT, "No residual protection");
        h.assertTrue(player.getStats().getValue(stat) == 100000, "Insomnia preserved on equip and removal");
        h.succeed();
    }
    private static void curios(GameTestHelper h) {
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        var inventory = CuriosApi.getCuriosInventory(player).orElseThrow();
        var head = inventory.getStacksHandler("head").orElseThrow();
        h.assertTrue(head.getStacks().getSlots() > 0, "Curios head slot available");
        h.assertTrue(CuriosApi.isStackValid(new top.theillusivec4.curios.api.SlotContext("head", player, 0, false, true), ward()), "Head tag permits item");
        head.getStacks().setStackInSlot(0, ward());
        h.assertTrue(attempt(player).getResult() == PlayerSpawnPhantomsEvent.Result.DENY, "Functional Curios slot protects");
        head.getStacks().setStackInSlot(0, ItemStack.EMPTY);
        h.assertTrue(attempt(player).getResult() == PlayerSpawnPhantomsEvent.Result.DEFAULT, "Curios removal immediately restores rules");
        if (head.getCosmeticStacks().getSlots() == 0) head.getCosmeticStacks().grow(1);
        head.getCosmeticStacks().setStackInSlot(0, ward());
        h.assertTrue(attempt(player).getResult() == PlayerSpawnPhantomsEvent.Result.DEFAULT, "Cosmetic stack does not protect");
        h.succeed();
    }
    private static void legacyId(GameTestHelper h) {
        var tag = new net.minecraft.nbt.CompoundTag();
        tag.putString("id", "ageofexpansionmodpackfix:cat_watch_headband");
        tag.putInt("count", 1);
        var stack = ItemStack.parseOptional(h.getLevel().registryAccess(), tag);
        h.assertTrue(stack.is(ward().getItem()), "Old saved stack resolves to new item");
        h.assertTrue(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()
                .equals("ageofexpansionmodpackfix:cat_ear_headband"), "Canonical registry ID is new ID");
        h.succeed();
    }
    private static void recipe(GameTestHelper h) {
        var recipe = (net.minecraft.world.item.crafting.CraftingRecipe) h.getLevel().getRecipeManager()
                .byKey(ResourceLocation.parse("ageofexpansionmodpackfix:cat_ear_headband")).orElseThrow().value();
        var sign = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.parse("immersiveengineering:warning_sign_cat"));
        h.assertTrue(sign != Items.AIR, "Real IE ingredient registered");
        var stacks = java.util.List.of(ItemStack.EMPTY, new ItemStack(Items.STRING), ItemStack.EMPTY,
                new ItemStack(Items.STRING), new ItemStack(sign), new ItemStack(Items.STRING),
                ItemStack.EMPTY, new ItemStack(Items.STRING), ItemStack.EMPTY);
        var input = net.minecraft.world.item.crafting.CraftingInput.of(3, 3, stacks);
        h.assertTrue(recipe.matches(input, h.getLevel()), "Four strings around one sign");
        var result = recipe.assemble(input, h.getLevel().registryAccess());
        h.assertTrue(result.is(ward().getItem()) && result.getCount() == 1, "One headband output");
        h.succeed();
    }
}
