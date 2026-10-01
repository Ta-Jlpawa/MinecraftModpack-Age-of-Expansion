package com.tajlpawa.ageofexpansionmodpackfix.oreexcavation;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.fluids.FluidStack;

public final class DrillDurability {
    public static final TagKey<Fluid> PLANT_OIL = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:plantoil"));
    public static final TagKey<Fluid> SEED_OIL = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:seed_oil"));
    public static final TagKey<Fluid> LUBRICANT = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:lubricant"));
    private DrillDurability() {}
    public static int capacity(Item item) {
        var id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null || !id.getNamespace().equals("createoreexcavation")) return 0;
        return switch (id.getPath()) { case "drill" -> 200; case "diamond_drill" -> 600; case "netherite_drill" -> 1500; default -> 0; };
    }
    public static boolean isDrill(ItemStack stack) { return !stack.isEmpty() && capacity(stack.getItem()) > 0; }
    public static boolean exhausted(ItemStack stack) { return isDrill(stack) && stack.getDamageValue() >= stack.getMaxDamage(); }
    public static void components(ModifyDefaultComponentsEvent event) {
        BuiltInRegistries.ITEM.forEach(item -> {
            int max = capacity(item);
            if (max > 0) event.modify(item, builder -> builder.set(DataComponents.MAX_STACK_SIZE, 1)
                    .set(DataComponents.MAX_DAMAGE, max).set(DataComponents.DAMAGE, 0));
        });
    }
    // Use the same enchantment durability processing as vanilla, but never shrink the exhausted stack.
    public static void wear(ItemStack stack, ServerLevel level) {
        if (!isDrill(stack) || !stack.isDamageableItem() || exhausted(stack)) return;
        int damage = EnchantmentHelper.processDurabilityChange(level, stack, 1);
        stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + Math.max(0, damage)));
    }
    public static int cost(FluidStack fluid) {
        if (fluid.isEmpty()) return 0;
        if (fluid.is(LUBRICANT)) return 10;
        return fluid.is(PLANT_OIL) || fluid.is(SEED_OIL) ? 50 : 0;
    }
    public static int repairAmount(ItemStack stack, FluidStack fluid) {
        int cost = cost(fluid);
        return isDrill(stack) && cost > 0 ? Math.min(stack.getDamageValue(), fluid.getAmount() / cost) : 0;
    }
    public static ItemStack repaired(ItemStack input, int points) {
        ItemStack result = input.copyWithCount(1);
        result.setDamageValue(Math.max(0, result.getDamageValue() - points));
        return result;
    }
}
