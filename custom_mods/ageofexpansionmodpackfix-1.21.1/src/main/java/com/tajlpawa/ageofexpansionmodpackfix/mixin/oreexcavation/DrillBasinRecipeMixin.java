package com.tajlpawa.ageofexpansionmodpackfix.mixin.oreexcavation;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.tajlpawa.ageofexpansionmodpackfix.oreexcavation.DrillOilMixingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BasinRecipe.class, remap = false)
public abstract class DrillBasinRecipeMixin {
    @Inject(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
            at = @At("HEAD"), cancellable = true)
    private static void replan(BasinBlockEntity basin, Recipe<?> recipe, boolean simulate, CallbackInfoReturnable<Boolean> ci) {
        if (!(recipe instanceof DrillOilMixingRecipe)) return;
        var fresh = DrillOilMixingRecipe.plan(basin, false);
        ci.setReturnValue(fresh != null && BasinRecipe.match(basin, fresh) && (simulate || BasinRecipe.apply(basin, fresh)));
    }
}
