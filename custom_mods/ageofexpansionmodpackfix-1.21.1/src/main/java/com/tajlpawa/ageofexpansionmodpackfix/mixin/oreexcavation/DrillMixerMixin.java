package com.tajlpawa.ageofexpansionmodpackfix.mixin.oreexcavation;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.tajlpawa.ageofexpansionmodpackfix.oreexcavation.DrillOilMixingRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MechanicalMixerBlockEntity.class, remap = false)
public abstract class DrillMixerMixin {
    @Inject(method = "getMatchingRecipes", at = @At("RETURN"), cancellable = true)
    private void repairCandidate(CallbackInfoReturnable<List<Recipe<?>>> ci) {
        var mixer = (MechanicalMixerBlockEntity) (Object) this;
        if (mixer.getLevel() == null || !(mixer.getLevel().getBlockEntity(mixer.getBlockPos().below(2)) instanceof BasinBlockEntity basin)) return;
        var recipe = DrillOilMixingRecipe.plan(basin, true);
        if (recipe == null || !BasinRecipe.match(basin, recipe)) return;
        var recipes = new ArrayList<Recipe<?>>(ci.getReturnValue());
        recipes.add(0, recipe);
        ci.setReturnValue(recipes);
    }
}
