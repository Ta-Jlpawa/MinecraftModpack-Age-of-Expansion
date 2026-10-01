package com.tajlpawa.ageofexpansionmodpackfix.mixin.oreexcavation;

import com.simibubi.create.content.fluids.spout.FillingBySpout;
import com.tajlpawa.ageofexpansionmodpackfix.oreexcavation.DrillDurability;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FillingBySpout.class, remap = false)
public abstract class DrillSpoutMixin {
    @Inject(method = "canItemBeFilled", at = @At("HEAD"), cancellable = true)
    private static void accepts(Level level, ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
        if (DrillDurability.isDrill(stack) && stack.getDamageValue() > 0) ci.setReturnValue(true);
    }
    @Inject(method = "getRequiredAmountForItem", at = @At("HEAD"), cancellable = true)
    private static void amount(Level level, ItemStack stack, FluidStack fluid, CallbackInfoReturnable<Integer> ci) {
        if (!DrillDurability.isDrill(stack) || DrillDurability.cost(fluid) == 0) return;
        int points = DrillDurability.repairAmount(stack, fluid);
        ci.setReturnValue(stack.getDamageValue() <= 0 ? -1 : Math.max(1, points) * DrillDurability.cost(fluid));
    }
    @Inject(method = "fillItem", at = @At("HEAD"), cancellable = true)
    private static void repair(Level level, int amount, ItemStack stack, FluidStack fluid, CallbackInfoReturnable<ItemStack> ci) {
        if (!DrillDurability.isDrill(stack) || DrillDurability.cost(fluid) == 0) return;
        int cost = DrillDurability.cost(fluid);
        int points = Math.min(DrillDurability.repairAmount(stack, fluid), Math.max(0, amount) / cost);
        if (points == 0) { ci.setReturnValue(ItemStack.EMPTY); return; }
        ItemStack result = DrillDurability.repaired(stack, points);
        fluid.shrink(points * cost);
        stack.shrink(1);
        ci.setReturnValue(result);
    }
}
