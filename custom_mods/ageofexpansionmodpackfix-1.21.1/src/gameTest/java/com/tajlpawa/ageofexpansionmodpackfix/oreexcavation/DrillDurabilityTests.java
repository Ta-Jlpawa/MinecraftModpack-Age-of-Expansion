package com.tajlpawa.ageofexpansionmodpackfix.oreexcavation;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.spout.FillingBySpout;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.tom.createores.Registration;
import com.tom.createores.block.entity.DrillBlockEntity;
import com.tom.createores.recipe.DrillingRecipe;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.fluids.FluidStack;

public final class DrillDurabilityTests {
    public static Collection<TestFunction> tests() {
        var tests = new ArrayList<TestFunction>();
        add(tests, "drill_default_durability_and_enchanting", DrillDurabilityTests::defaults);
        add(tests, "drill_success_wear_and_exhaustion", DrillDurabilityTests::machine);
        add(tests, "drill_vanilla_unbreaking_probability", DrillDurabilityTests::unbreaking);
        add(tests, "drill_oil_only_repair", DrillDurabilityTests::oilOnly);
        add(tests, "drill_spout_batch_and_components", DrillDurabilityTests::spout);
        add(tests, "drill_oil_tag_variants_and_insufficient", DrillDurabilityTests::oils);
        add(tests, "drill_basin_batch_and_stale_recipe", DrillDurabilityTests::basin);
        add(tests, "drill_basin_output_full_no_consumption", DrillDurabilityTests::blocked);
        add(tests, "drill_real_powered_mixer", DrillDurabilityTests::mixer);
        add(tests, "drill_basin_multiple_tanks_and_mixed_oils", DrillDurabilityTests::multipleTanks);
        return tests;
    }
    private static void add(Collection<TestFunction> tests, String name, java.util.function.Consumer<GameTestHelper> run) {
        tests.add(new TestFunction("ageofexpansionmodpackfix:drill", "ageofexpansionmodpackfix:" + name,
                "ageofexpansionmodpackfix:router_empty", 240, 0, true, run));
    }
    private static ItemStack drill(String type, int damage) {
        var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse("createoreexcavation:" + type));
        if (item == Items.AIR) throw new AssertionError("Missing drill " + type);
        var stack = new ItemStack(item); stack.setDamageValue(damage); return stack;
    }
    private static FluidStack oil(String id, int amount) {
        var fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(id));
        if (fluid == Fluids.EMPTY) throw new AssertionError("Missing test oil " + id);
        return new FluidStack(fluid, amount);
    }
    private static FluidStack lube(int amount) { return oil("immersivepetroleum:lubricant", amount); }
    private static void defaults(GameTestHelper h) {
        String[] ids = {"drill", "diamond_drill", "netherite_drill"}; int[] max = {200,600,1500};
        var enchantments = h.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        for (int i=0;i<ids.length;i++) {
            var stack = drill(ids[i],0);
            h.assertTrue(stack.isDamageableItem() && stack.getMaxDamage()==max[i] && stack.getMaxStackSize()==1, "Durability and non-stackable defaults");
            h.assertTrue(stack.isEnchantable() && stack.getEnchantmentValue()>0, "Enchanting table accepts drill");
            var unbreaking = enchantments.getOrThrow(Enchantments.UNBREAKING);
            h.assertTrue(stack.supportsEnchantment(unbreaking) && stack.isPrimaryItemFor(unbreaking), "Unbreaking supported by table and anvil");
            h.assertTrue(!stack.supportsEnchantment(enchantments.getOrThrow(Enchantments.MENDING)), "No mending");
            h.assertTrue(!stack.supportsEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY)), "No unrelated enchantments");
        }
        h.assertTrue(new ItemStack(Items.DIAMOND_PICKAXE).supportsEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY)), "Other tools unchanged");
        h.succeed();
    }
    private static final class MachineProbe extends DrillBlockEntity {
        MachineProbe(GameTestHelper h) {
            super(Registration.DRILL_TILE.get(), h.absolutePos(new BlockPos(3,2,3)), Registration.DRILL_BLOCK.getDefaultState());
            setLevel(h.getLevel());
            var recipe = h.getLevel().getRecipeManager().byKey(ResourceLocation.parse("createoreexcavation:drilling/iron")).orElseThrow();
            current = new RecipeHolder<>(recipe.id(), (DrillingRecipe)recipe.value());
        }
        void equip(ItemStack stack) { drillStack=stack; }
        void finish() { super.onFinished(); }
        boolean canRun() { return super.canExtract(); }
    }
    private static void machine(GameTestHelper h) {
        var machine = new MachineProbe(h); var stack = drill("drill",199); machine.equip(stack);
        machine.finish();
        h.assertTrue(stack.getDamageValue()==200 && stack.getCount()==1, "One successful cycle consumes last point without deleting drill");
        h.assertTrue(!machine.canRun(), "Exhausted machine cannot start another cycle");
        DrillDurability.wear(stack,h.getLevel());
        h.assertTrue(stack.getDamageValue()==200 && !stack.isEmpty(), "Exhausted drill retained");
        var repaired = DrillDurability.repaired(stack, 1); machine.equip(repaired);
        h.assertTrue(machine.canRun(), "Oil-repaired drill resumes extraction");
        h.succeed();
    }
    private static void unbreaking(GameTestHelper h) {
        var stack = drill("netherite_drill",0);
        stack.enchant(h.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING),3);
        int worn=0; h.getLevel().random.setSeed(428713L);
        for(int i=0;i<4000;i++){stack.setDamageValue(0);DrillDurability.wear(stack,h.getLevel());worn+=stack.getDamageValue();}
        h.assertTrue(worn>850 && worn<1150, "Unbreaking III damages about one in four cycles; actual="+worn);
        h.succeed();
    }
    private static void oilOnly(GameTestHelper h) {
        var a=drill("drill",100);var b=drill("drill",100);
        h.assertTrue(!new RepairItemRecipe(CraftingBookCategory.MISC).matches(CraftingInput.of(2,1,List.of(a,b)),h.getLevel()), "Crafting cannot repair");
        h.assertTrue(!a.canGrindstoneRepair(), "Grindstone cannot repair");
        var player=h.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        var event=NeoForge.EVENT_BUS.post(new AnvilUpdateEvent(a,b,"",0,player));
        h.assertTrue(event.isCanceled(), "Anvil cannot combine drills");
        var book=new ItemStack(Items.ENCHANTED_BOOK);
        h.assertTrue(!NeoForge.EVENT_BUS.post(new AnvilUpdateEvent(a,book,"",0,player)).isCanceled(), "Enchanted books still reach vanilla validation");
        h.succeed();
    }
    private static void spout(GameTestHelper h) {
        var stack=drill("diamond_drill",600);stack.set(DataComponents.CUSTOM_NAME,Component.literal("Named drill"));
        stack.enchant(h.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING),3);
        var before=stack.copy();var fluid=lube(1000);
        int amount=FillingBySpout.getRequiredAmountForItem(h.getLevel(),stack,fluid);
        h.assertTrue(amount==1000, "One spout operation consumes 1000 mB for 100 points");
        var out=FillingBySpout.fillItem(h.getLevel(),amount,stack,fluid);
        h.assertTrue(out.getDamageValue()==500 && out.getCount()==1 && stack.isEmpty() && fluid.isEmpty(), "Batch repair conserved input and oil");
        before.setDamageValue(500);h.assertTrue(ItemStack.isSameItemSameComponents(before,out),"Name and enchantments preserved");
        var almost=drill("drill",3);fluid=lube(1000);amount=FillingBySpout.getRequiredAmountForItem(h.getLevel(),almost,fluid);
        h.assertTrue(amount==30,"Only consume fluid needed to reach full durability");
        out=FillingBySpout.fillItem(h.getLevel(),amount,almost,fluid);
        h.assertTrue(out.getDamageValue()==0 && fluid.getAmount()==970,"No excess consumption");h.succeed();
    }
    private static void oils(GameTestHelper h) {
        for(String id:List.of("immersiveengineering:plantoil","createaddition:seed_oil")) {
            var stack=drill("diamond_drill",600);var fluid=oil(id,1000);
            var out=FillingBySpout.fillItem(h.getLevel(),FillingBySpout.getRequiredAmountForItem(h.getLevel(),stack,fluid),stack,fluid);
            h.assertTrue(out.getDamageValue()==580 && fluid.isEmpty(),"Plant/seed tag repairs 20 points: "+id);
        }
        var stack=drill("drill",10);var fluid=lube(9);
        h.assertTrue(FillingBySpout.getRequiredAmountForItem(h.getLevel(),stack,fluid)==10,"Spout waits for minimum fluid");
        h.assertTrue(FillingBySpout.fillItem(h.getLevel(),10,stack,fluid).isEmpty() && stack.getDamageValue()==10 && fluid.getAmount()==9,"Insufficient fluid does not consume anything");
        h.assertTrue(DrillDurability.repairAmount(stack,new FluidStack(Fluids.WATER,1000))==0,"Water not accepted");h.succeed();
    }
    private static BasinBlockEntity setupBasin(GameTestHelper h) {
        h.setBlock(new BlockPos(5,1,5),AllBlocks.BASIN.getDefaultState());
        return (BasinBlockEntity)h.getBlockEntity(new BlockPos(5,1,5));
    }
    private static void basin(GameTestHelper h) {
        var basin=setupBasin(h);basin.getInputInventory().setStackInSlot(0,drill("diamond_drill",400));
        basin.inputTank.getPrimaryHandler().setFluid(lube(1000));
        var recipe=DrillOilMixingRecipe.plan(basin,true);
        h.assertTrue(recipe!=null && BasinRecipe.match(basin,recipe),"Dynamic mixer recipe matches");
        h.assertTrue(basin.getInputInventory().getStackInSlot(0).getDamageValue()==400 && basin.inputTank.getPrimaryHandler().getFluidAmount()==1000,"Simulation does not consume");
        var changed=drill("diamond_drill",30);changed.set(DataComponents.CUSTOM_NAME,Component.literal("Replacement"));basin.getInputInventory().setStackInSlot(0,changed);
        h.assertTrue(BasinRecipe.apply(basin,recipe),"Stale marker replans actual current contents");
        var out=basin.getOutputInventory().getStackInSlot(0);
        h.assertTrue(out.getDamageValue()==0 && out.getHoverName().getString().equals("Replacement") && basin.inputTank.getPrimaryHandler().getFluidAmount()==700,"Updated stack restored and exact oil consumed");h.succeed();
    }
    private static void blocked(GameTestHelper h) {
        var basin=setupBasin(h);basin.getInputInventory().setStackInSlot(0,drill("drill",100));basin.inputTank.getPrimaryHandler().setFluid(lube(1000));
        for(int i=0;i<9;i++)basin.getOutputInventory().setStackInSlot(i,new ItemStack(Items.COBBLESTONE,64));
        var recipe=DrillOilMixingRecipe.plan(basin,true);
        h.assertTrue(!BasinRecipe.apply(basin,recipe),"Full output rejects operation");
        h.assertTrue(basin.getInputInventory().getStackInSlot(0).getDamageValue()==100 && basin.inputTank.getPrimaryHandler().getFluidAmount()==1000,"Blocked output consumes neither drill nor oil");h.succeed();
    }
    private static void setTank(GameTestHelper h, com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment segment, FluidStack fluid) {
        var data=segment.writeNBT(h.getLevel().registryAccess());
        var tank=new net.neoforged.neoforge.fluids.capability.templates.FluidTank(1000);
        tank.setFluid(fluid);
        data.put("TankContent",tank.writeToNBT(h.getLevel().registryAccess(),new net.minecraft.nbt.CompoundTag()));
        segment.readNBT(data,h.getLevel().registryAccess(),false);
        segment.onFluidStackChanged();
    }
    private static void multipleTanks(GameTestHelper h) {
        var basin=setupBasin(h);basin.getInputInventory().setStackInSlot(0,drill("diamond_drill",200));
        var tanks=basin.inputTank.getTanks();
        setTank(h,tanks[0],lube(499));setTank(h,tanks[1],lube(501));
        var recipe=DrillOilMixingRecipe.plan(basin,true);
        h.assertTrue(BasinRecipe.apply(basin,recipe),"Repair aggregates matching oil across tank boundaries");
        h.assertTrue(basin.getOutputInventory().getStackInSlot(0).getDamageValue()==100,"499+501 mB restores 100 points, not 99");
        basin.getOutputInventory().setStackInSlot(0,ItemStack.EMPTY);
        basin.getInputInventory().setStackInSlot(0,drill("diamond_drill",200));
        setTank(h,tanks[0],lube(500));setTank(h,tanks[1],oil("immersiveengineering:plantoil",1000));
        h.assertTrue(BasinRecipe.apply(basin,DrillOilMixingRecipe.plan(basin,true)),"Mixed eligible oil types repair together");
        h.assertTrue(basin.getOutputInventory().getStackInSlot(0).getDamageValue()==130,"500 mB lubricant plus 1000 mB plant oil restores 70 points");
        h.succeed();
    }
    private static void mixer(GameTestHelper h) {
        var basin=setupBasin(h);basin.getInputInventory().setStackInSlot(0,drill("diamond_drill",250));basin.inputTank.getPrimaryHandler().setFluid(lube(1000));
        h.setBlock(new BlockPos(5,3,5),AllBlocks.MECHANICAL_MIXER.getDefaultState());
        h.setBlock(new BlockPos(6,3,5),AllBlocks.COGWHEEL.getDefaultState().setValue(BlockStateProperties.AXIS,Direction.Axis.Y));
        h.setBlock(new BlockPos(6,4,5),AllBlocks.CREATIVE_MOTOR.getDefaultState().setValue(BlockStateProperties.FACING,Direction.DOWN));
        var motor=(com.simibubi.create.content.kinetics.motor.CreativeMotorBlockEntity)h.getBlockEntity(new BlockPos(6,4,5));
        motor.generatedSpeed.value=64;motor.updateGeneratedRotation();
        h.runAtTickTime(200,()->{
            var out=basin.getOutputInventory().getStackInSlot(0);
            h.assertTrue(!out.isEmpty() && out.getDamageValue()==150,"Powered mixer performs one batch repair, actual="+out);
            h.assertTrue(basin.inputTank.getPrimaryHandler().getFluidAmount()==0,"Powered mixer consumes exact 1000 mB");h.succeed();
        });
    }
}
