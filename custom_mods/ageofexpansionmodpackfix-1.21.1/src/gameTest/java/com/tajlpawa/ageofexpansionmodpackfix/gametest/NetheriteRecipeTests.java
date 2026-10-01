package com.tajlpawa.ageofexpansionmodpackfix.gametest;

import blusunrize.immersiveengineering.api.crafting.ArcFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.tom.createores.recipe.DrillingRecipe;
import java.util.List;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.PressurizedReactionRecipe;
import mekanism.common.registries.MekanismChemicals;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import rearth.oritech.init.recipes.OritechRecipe;

public final class NetheriteRecipeTests {
    public static List<TestFunction> tests() {
        return List.of(new TestFunction("ageofexpansionmodpackfix:netherite_recipes", "ageofexpansionmodpackfix:netherite_processing",
                "ageofexpansionmodpackfix:router_empty", 100, 0, true, NetheriteRecipeTests::verify));
    }

    private static Recipe<?> recipe(GameTestHelper helper, String id) {
        return helper.getLevel().getRecipeManager().byKey(ResourceLocation.parse(id)).orElseThrow().value();
    }

    private static void assertOutput(GameTestHelper helper, ItemStack actual, String itemId, int count) {
        Item expected = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        helper.assertTrue(actual.is(expected) && actual.getCount() == count, itemId + " x" + count);
    }

    private static void verify(GameTestHelper helper) {
        var manager = helper.getLevel().getRecipeManager();
        for (String id : List.of(
                "mekanism:processing/netherite/ancient_debris_to_dirty_scrap",
                "mekanism:processing/netherite/dirty_scrap_to_scrap",
                "mekanism:processing/netherite/dust_to_ancient_debris",
                "mekanism:processing/netherite/ingot_from_dust_blasting",
                "mekanism:processing/netherite/ingot_from_dust_smelting",
                "mekanism:processing/netherite/ingot_to_dust",
                "mekanism:processing/netherite/scrap_to_dust",
                "mekanism:processing/netherite/ancient_debris_to_scrap",
                "oritech:grinder/compat/mekanism/dust/netherite",
                "oritech:pulverizer/compat/mekanism/dust/netherite",
                "oritech:grinder/netheritescrap",
                "immersiveengineering:arcfurnace/netherite_scrap",
                "minecraft:netherite_scrap",
                "minecraft:netherite_scrap_from_blasting")) {
            helper.assertTrue(manager.byKey(ResourceLocation.parse(id)).isEmpty(), "Removed old recipe " + id);
        }

        ItemStackToItemStackRecipe dirty = (ItemStackToItemStackRecipe) recipe(helper, "ageofexpansionpack:netherite/dirty_scrap_to_dust");
        assertOutput(helper, dirty.getOutputDefinition().getFirst(), "mekanism:dust_netherite", 1);
        PressurizedReactionRecipe acid = (PressurizedReactionRecipe) recipe(helper, "ageofexpansionpack:netherite/dust_to_scrap");
        assertOutput(helper, acid.getOutputDefinition().getFirst().item(), "minecraft:netherite_scrap", 1);
        helper.assertTrue(acid.getDuration() == 100, "Reaction duration retained");
        var fluid = acid.getInputFluid().getRepresentations().getFirst();
        helper.assertTrue(fluid.getAmount() == 200 && BuiltInRegistries.FLUID.getKey(fluid.getFluid()).toString().equals("mekanism:sulfuric_acid"),
                "Reaction consumes 200 mB liquid sulfuric acid");
        var chemical = acid.getInputChemical().getRepresentations().getFirst();
        helper.assertTrue(chemical.getAmount() == 10 && chemical.getChemical() == MekanismChemicals.REFINED_OBSIDIAN.get(),
                "Reaction consumes 10 mB refined obsidian chemical");
        AbstractCookingRecipe furnace = (AbstractCookingRecipe) recipe(helper, "ageofexpansionpack:netherite/ancient_debris_smelting");
        assertOutput(helper, furnace.getResultItem(helper.getLevel().registryAccess()), "minecraft:netherite_scrap", 2);
        AbstractCookingRecipe blastFurnace = (AbstractCookingRecipe) recipe(helper, "ageofexpansionpack:netherite/ancient_debris_blasting");
        assertOutput(helper, blastFurnace.getResultItem(helper.getLevel().registryAccess()), "minecraft:netherite_scrap", 2);

        ArcFurnaceRecipe arc = (ArcFurnaceRecipe) recipe(helper, "ageofexpansionpack:netherite/ancient_debris_arc_furnace");
        assertOutput(helper, arc.getBaseOutputs().getFirst(), "minecraft:netherite_scrap", 4);
        ItemStackToItemStackRecipe mekanismCrusher = (ItemStackToItemStackRecipe) recipe(helper, "ageofexpansionpack:netherite/ancient_debris_crushing");
        assertOutput(helper, mekanismCrusher.getOutputDefinition().getFirst(), "mekanism:dust_netherite", 4);
        ItemStackToItemStackRecipe enriching = (ItemStackToItemStackRecipe) recipe(helper, "ageofexpansionpack:netherite/ancient_debris_enriching");
        assertOutput(helper, enriching.getOutputDefinition().getFirst(), "minecraft:netherite_scrap", 3);
        CrusherRecipe ieCrusher = (CrusherRecipe) recipe(helper, "ageofexpansionpack:netherite/ancient_debris_crusher");
        assertOutput(helper, ieCrusher.getActualItemOutputs().getFirst(), "mekanism:dust_netherite", 4);
        OritechRecipe oritechGrinder = (OritechRecipe) recipe(helper, "ageofexpansionpack:netherite/ancient_debris_oritech_grinder");
        assertOutput(helper, oritechGrinder.getResults().getFirst(), "mekanism:dust_netherite", 4);
        helper.assertTrue(oritechGrinder.getTime() == 40, "Oritech grinder time preserved");

        DrillingRecipe drilling = (DrillingRecipe) recipe(helper, "createoreexcavation:drilling/netherite");
        List<ProcessingOutput> output = drilling.getOutput();
        helper.assertTrue(output.size() == 4, "Other drilling outputs preserved");
        assertOutput(helper, output.getFirst().getStack(), "mekanism:dirty_netherite_scrap", 1);
        helper.assertTrue(Math.abs(output.getFirst().getChance() - 0.5f) < 0.0001f, "Dirty scrap is 50 percent");
        assertOutput(helper, output.get(1).getStack(), "minecraft:gold_nugget", 1);
        assertOutput(helper, output.get(2).getStack(), "minecraft:netherrack", 1);
        assertOutput(helper, output.get(3).getStack(), "minecraft:magma_block", 1);
        helper.assertTrue(drilling.getTicks() == 4000 && drilling.getStress() == 2048, "Drilling speed and stress preserved");
        helper.assertTrue(drilling.getDrillingFluid().orElseThrow().getRequiredAmount() == 1000, "Drilling lava amount preserved");
        helper.succeed();
    }
}
