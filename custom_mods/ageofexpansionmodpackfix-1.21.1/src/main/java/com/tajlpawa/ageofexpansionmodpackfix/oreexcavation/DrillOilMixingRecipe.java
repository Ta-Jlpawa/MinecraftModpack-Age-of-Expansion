package com.tajlpawa.ageofexpansionmodpackfix.oreexcavation;

import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

// Marker recipes are re-planned at application time so inserted/removed items or fluids cannot stale the output.
public final class DrillOilMixingRecipe extends MixingRecipe {
    public DrillOilMixingRecipe(ProcessingRecipeParams params) { super(params); }
    public static MixingRecipe plan(BasinBlockEntity basin, boolean marker) {
        if (basin.getLevel() == null) return null;
        var items = basin.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, basin.getBlockPos(), null);
        var fluids = basin.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, basin.getBlockPos(), null);
        if (items == null || fluids == null) return null;
        for (int slot = 0; slot < items.getSlots(); slot++) {
            var stack = items.getStackInSlot(slot);
            if (!DrillDurability.isDrill(stack) || stack.getDamageValue() <= 0) continue;
            var groups = new LinkedHashMap<Fluid, ArrayList<FluidStack>>();
            for (int tank = 0; tank < fluids.getTanks(); tank++) {
                var fluid = fluids.getFluidInTank(tank);
                if (DrillDurability.cost(fluid) > 0) groups.computeIfAbsent(fluid.getFluid(), key -> new ArrayList<>()).add(fluid);
            }
            var ordered = new ArrayList<>(groups.values());
            ordered.sort(java.util.Comparator.comparingInt(list -> DrillDurability.cost(list.getFirst())));
            int repaired = 0;
            var inputs = new ArrayList<SizedFluidIngredient>();
            for (var group : ordered) {
                int cost = DrillDurability.cost(group.getFirst());
                int total = group.stream().mapToInt(FluidStack::getAmount).sum();
                int points = Math.min(stack.getDamageValue() - repaired, total / cost);
                int needed = points * cost;
                repaired += points;
                for (var fluid : group) {
                    int consume = Math.min(needed, fluid.getAmount());
                    if (consume > 0) inputs.add(SizedFluidIngredient.of(fluid.getFluid(), consume));
                    needed -= consume;
                }
                if (repaired == stack.getDamageValue()) break;
            }
            if (repaired == 0) continue;
            return new StandardProcessingRecipe.Builder<MixingRecipe>(params -> marker ? new DrillOilMixingRecipe(params) : new MixingRecipe(params),
                    ResourceLocation.parse("ageofexpansionmodpackfix:drill_oil_repair"))
                    .require(DataComponentIngredient.of(true, stack.copyWithCount(1)))
                    .withFluidIngredients(inputs.toArray(SizedFluidIngredient[]::new))
                    .withSingleItemOutput(DrillDurability.repaired(stack, repaired)).duration(100).build();
        }
        return null;
    }
}
