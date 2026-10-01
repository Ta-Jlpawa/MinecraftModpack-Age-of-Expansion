package com.tajlpawa.ageofexpansionmodpackfix.mixin.oreexcavation;

import com.tajlpawa.ageofexpansionmodpackfix.oreexcavation.DrillDurability;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class DrillItemMixin implements IItemExtension {
    @Inject(method = "getEnchantmentValue()I", at = @At("HEAD"), cancellable = true)
    private void drillEnchantability(CallbackInfoReturnable<Integer> ci) {
        if (DrillDurability.capacity((Item) (Object) this) > 0) ci.setReturnValue(14);
    }
    @Inject(method = "isRepairable", at = @At("HEAD"), cancellable = true, remap = false)
    private void oilOnly(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
        if (DrillDurability.isDrill(stack)) ci.setReturnValue(false);
    }
    @Override public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return DrillDurability.isDrill(stack) ? enchantment.is(Enchantments.UNBREAKING) : IItemExtension.super.supportsEnchantment(stack, enchantment);
    }
    @Override public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        return DrillDurability.isDrill(stack) ? enchantment.is(Enchantments.UNBREAKING) : IItemExtension.super.isPrimaryItemFor(stack, enchantment);
    }
    @Override public boolean canGrindstoneRepair(ItemStack stack) {
        return !DrillDurability.isDrill(stack) && IItemExtension.super.canGrindstoneRepair(stack);
    }
}
