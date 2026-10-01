package com.tajlpawa.ageofexpansion.gametest;

import com.tajlpawa.ageofexpansion.display.*;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder("ageofexpansion")
public final class DisplayTests {
    @GameTestGenerator public static Collection<TestFunction> tests() {
        List<TestFunction> tests = new ArrayList<>();
        add(tests, "display_document_roundtrip", DisplayTests::document);
        add(tests, "display_light_and_persistence", DisplayTests::light);
        add(tests, "display_four_walls_and_support", DisplayTests::support);
        add(tests, "display_recipe", DisplayTests::recipe);
        add(tests, "display_limits_and_invalid_data", DisplayTests::limits);
        add(tests, "display_wall_only_placement_and_blank_drop", DisplayTests::placement);
        add(tests, "display_save_cancel_stale_and_distance", DisplayTests::sessions);
        add(tests, "display_font_size_config_and_wire", DisplayTests::fontSizes);
        return tests;
    }
    private static void add(List<TestFunction> tests, String name, java.util.function.Consumer<GameTestHelper> test) {
        tests.add(new TestFunction("ageofexpansion:display", "ageofexpansion:" + name, "ageofexpansion:display_empty", 100, 0, true, test));
    }
    private static DisplayDocument sample() {
        return new DisplayDocument("开拓\nHello😀".codePoints().mapToObj(cp -> new DisplayDocument.Glyph(cp, new DisplayDocument.Ink(0x22bb99, 7))).toList(), 175, 2);
    }
    private static void document(GameTestHelper h) {
        DisplayDocument doc = sample();
        h.assertTrue(DisplayDocument.load(doc.save()).equals(doc), "NBT retains Unicode and all styles");
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), h.getLevel().registryAccess());
        try { doc.write(buf); h.assertTrue(DisplayDocument.read(buf).equals(doc), "Network round trip"); } finally { buf.release(); }
        h.succeed();
    }
    private static void light(GameTestHelper h) {
        BlockPos pos = h.absolutePos(new BlockPos(2, 2, 2));
        h.getLevel().setBlockAndUpdate(pos.south(), Blocks.STONE.defaultBlockState());
        h.getLevel().setBlockAndUpdate(pos, DisplayContent.BLOCK.get().defaultBlockState());
        var entity = (DisplayBlockEntity)h.getLevel().getBlockEntity(pos);
        entity.update(sample());
        h.assertTrue(h.getLevel().getBlockState(pos).getLightEmission(h.getLevel(), pos) == 4, "Text emits level four");
        var saved = entity.saveWithFullMetadata(h.getLevel().registryAccess());
        var restored = new DisplayBlockEntity(pos, entity.getBlockState()); restored.loadWithComponents(saved, h.getLevel().registryAccess());
        h.assertTrue(restored.document().equals(sample()) && restored.revision() == entity.revision(), "Block entity reload retains data and revision");
        entity.update(new DisplayDocument(List.of(new DisplayDocument.Glyph(' ', DisplayDocument.Ink.WHITE)), 100, 1));
        h.assertTrue(h.getLevel().getBlockState(pos).getLightEmission(h.getLevel(), pos) == 0, "Whitespace turns light off");
        h.succeed();
    }
    private static void support(GameTestHelper h) {
        BlockPos pos = h.absolutePos(new BlockPos(2, 2, 2));
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            var state = DisplayContent.BLOCK.get().defaultBlockState().setValue(DisplayBlock.FACING, facing);
            BlockPos wall = pos.relative(facing.getOpposite());
            h.getLevel().setBlockAndUpdate(wall, Blocks.STONE.defaultBlockState()); h.getLevel().setBlockAndUpdate(pos, state);
            h.assertTrue(state.canSurvive(h.getLevel(), pos), "Wall supported " + facing);
            h.getLevel().setBlockAndUpdate(wall, Blocks.AIR.defaultBlockState());
            h.assertTrue(h.getLevel().getBlockState(pos).isAir(), "Loses support " + facing);
        }
        h.succeed();
    }
    private static void recipe(GameTestHelper h) {
        CraftingRecipe recipe = (CraftingRecipe)h.getLevel().getRecipeManager().byKey(ResourceLocation.parse("ageofexpansion:electronic_display")).orElseThrow().value();
        var stacks = List.of(ItemStack.EMPTY, new ItemStack(Items.REDSTONE), ItemStack.EMPTY,
            new ItemStack(Items.REDSTONE), new ItemStack(Items.BLACK_STAINED_GLASS_PANE), new ItemStack(Items.REDSTONE),
            ItemStack.EMPTY, new ItemStack(Items.QUARTZ), ItemStack.EMPTY);
        var input = CraftingInput.of(3, 3, stacks);
        h.assertTrue(recipe.matches(input, h.getLevel()), "Exact native recipe");
        var result = recipe.assemble(input, h.getLevel().registryAccess());
        h.assertTrue(result.is(DisplayContent.ITEM.get()) && result.getCount() == 1, "One display crafted"); h.succeed();
    }
    private static void limits(GameTestHelper h) {
        h.assertTrue(!DisplayDocument.validCodepoint(0xd800) && !DisplayDocument.validCodepoint(0xa7), "Reject broken Unicode and formatting codes");
        boolean rejected = false;
        try { new DisplayDocument(Collections.nCopies(DisplayDocument.LIMIT+1, new DisplayDocument.Glyph('A', DisplayDocument.Ink.WHITE)), 100, 1); }
        catch (IllegalArgumentException expected) { rejected = true; }
        h.assertTrue(rejected, "Length limit enforced");
        var tag = sample().save(); tag.putByteArray("packed",new byte[]{0});
        h.assertTrue(DisplayDocument.load(tag).equals(DisplayDocument.EMPTY), "Invalid saved data recovers safely"); h.succeed();
    }
    private static void fontSizes(GameTestHelper h){
        int oldMin=com.tajlpawa.ageofexpansion.Config.MIN_FONT_SIZE.get(),oldMax=com.tajlpawa.ageofexpansion.Config.MAX_FONT_SIZE.get();
        try{
            com.tajlpawa.ageofexpansion.Config.MIN_FONT_SIZE.set(1);com.tajlpawa.ageofexpansion.Config.MAX_FONT_SIZE.set(1000);
            for(int size:new int[]{1,25,400,1000}){
                var doc=sample().metadata(size,1,1);
                h.assertTrue(DisplayDocument.load(doc.save()).equals(doc),"Full absolute size range survives save/load");
                h.assertTrue(com.tajlpawa.ageofexpansion.Config.allowsFontSize(size),"Configured size accepted");
            }
            for(int size:new int[]{0,1001}){
                boolean rejected=false;try{sample().metadata(size,1,1);}catch(IllegalArgumentException expected){rejected=true;}
                h.assertTrue(rejected,"Absolute size bound enforced");
            }
            var meta=new DisplayNetwork.Meta(1,1,1,1024,BlockPos.ZERO,UUID.randomUUID(),9);
            var frame=DisplayNetwork.frames(new DisplayNetwork.Packet(0,UUID.randomUUID(),sample(),meta)).getFirst();
            var buf=new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),h.getLevel().registryAccess());
            try{
                DisplayNetwork.Frame.CODEC.encode(buf,frame);var decoded=DisplayNetwork.Frame.CODEC.decode(buf);
                h.assertTrue(decoded.meta().minFontSize()==1&&decoded.meta().maxFontSize()==1000,"Server size range survives network codec");
            }finally{buf.release();}
            com.tajlpawa.ageofexpansion.Config.MIN_FONT_SIZE.set(700);com.tajlpawa.ageofexpansion.Config.MAX_FONT_SIZE.set(300);
            h.assertTrue(com.tajlpawa.ageofexpansion.Config.maxFontSize()==700,"Inverted range is never exposed to editor or validation");
            com.tajlpawa.ageofexpansion.Config.normalizeFontRange();
            h.assertTrue(com.tajlpawa.ageofexpansion.Config.MAX_FONT_SIZE.get()==700,"Invalid maximum corrected to minimum");
            h.assertTrue(com.tajlpawa.ageofexpansion.Config.clampFontSize(100)==700&&!com.tajlpawa.ageofexpansion.Config.allowsFontSize(699),"Equal bounds and lower clamp");
        }finally{
            com.tajlpawa.ageofexpansion.Config.MIN_FONT_SIZE.set(oldMin);com.tajlpawa.ageofexpansion.Config.MAX_FONT_SIZE.set(oldMax);com.tajlpawa.ageofexpansion.Config.SPEC.save();
        }
        // Allow the config file watcher to finish before the next batch overrides module limits.
        h.runAfterDelay(40,h::succeed);
    }
    private static void placement(GameTestHelper h) {
        BlockPos wall = h.absolutePos(new BlockPos(2, 2, 2)); h.getLevel().setBlockAndUpdate(wall, Blocks.STONE.defaultBlockState());
        var player = h.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        var stack = DisplayContent.ITEM.get().getDefaultInstance();
        for (Direction facing : Direction.values()) {
            var hit = new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(wall), facing, wall, false);
            var context = new net.minecraft.world.item.context.BlockPlaceContext(new net.minecraft.world.item.context.UseOnContext(
                h.getLevel(), player, net.minecraft.world.InteractionHand.MAIN_HAND, stack, hit));
            var state = DisplayContent.BLOCK.get().getStateForPlacement(context);
            h.assertTrue((state != null) == facing.getAxis().isHorizontal(), "Only horizontal placement: " + facing);
            if (state != null) h.assertTrue(state.getValue(DisplayBlock.FACING) == facing, "Faces away from clicked wall");
        }
        BlockPos pos = wall.north(); var state = DisplayContent.BLOCK.get().defaultBlockState();
        h.getLevel().setBlockAndUpdate(pos, state); var entity = (DisplayBlockEntity)h.getLevel().getBlockEntity(pos); entity.update(sample());
        var drops = net.minecraft.world.level.block.Block.getDrops(entity.getBlockState(), h.getLevel(), pos, entity);
        h.assertTrue(drops.size() == 1 && drops.get(0).is(DisplayContent.ITEM.get())
            && !drops.get(0).has(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA), "Dropped item has no saved text");
        h.succeed();
    }
    private static void sessions(GameTestHelper h) {
        try {
            BlockPos pos = h.absolutePos(new BlockPos(2, 2, 2));
            h.getLevel().setBlockAndUpdate(pos.south(), Blocks.STONE.defaultBlockState());
            h.getLevel().setBlockAndUpdate(pos, DisplayContent.BLOCK.get().defaultBlockState());
            var display = (DisplayBlockEntity)h.getLevel().getBlockEntity(pos);
            var player = net.neoforged.neoforge.common.util.FakePlayerFactory.get(h.getLevel(), new com.mojang.authlib.GameProfile(UUID.randomUUID(), "DisplayTester"));
            player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL); player.setPos(pos.getX() + .5, pos.getY(), pos.getZ() - 1);
            var sessions = DisplayNetwork.class.getDeclaredField("SESSIONS"); sessions.setAccessible(true);
            var receive = DisplayNetwork.class.getDeclaredMethod("receive", net.minecraft.server.level.ServerPlayer.class, DisplayNetwork.Packet.class); receive.setAccessible(true);
            DisplayNetwork.open(player, display);
            Object session = ((Map<?, ?>)sessions.get(null)).get(player.getUUID());
            h.assertTrue(session != null, "Nearby builder can open");
            var tokenMethod = session.getClass().getDeclaredMethod("token"); tokenMethod.setAccessible(true);
            UUID token = (UUID)tokenMethod.invoke(session);
            receive.invoke(null, player, new DisplayNetwork.Packet(1, token, sample()));
            h.assertTrue(display.document().equals(sample()), "Authorized save updates display");
            for(int invalidSize:new int[]{1,1000}){
                DisplayNetwork.open(player,display);session=((Map<?,?>)sessions.get(null)).get(player.getUUID());token=(UUID)tokenMethod.invoke(session);
                receive.invoke(null,player,new DisplayNetwork.Packet(1,token,sample().metadata(invalidSize,1,1)));
                h.assertTrue(display.document().equals(sample()),"Server rejects size outside configured range even in forged packet");
            }
            DisplayNetwork.open(player, display); session = ((Map<?, ?>)sessions.get(null)).get(player.getUUID()); token = (UUID)tokenMethod.invoke(session);
            receive.invoke(null, player, new DisplayNetwork.Packet(3, token, DisplayDocument.EMPTY));
            h.assertTrue(display.document().equals(sample()), "Cancel leaves content unchanged");
            DisplayNetwork.open(player, display); session = ((Map<?, ?>)sessions.get(null)).get(player.getUUID()); token = (UUID)tokenMethod.invoke(session);
            display.update(DisplayDocument.EMPTY);
            receive.invoke(null, player, new DisplayNetwork.Packet(1, token, sample()));
            h.assertTrue(display.document().equals(DisplayDocument.EMPTY), "Stale revision cannot overwrite concurrent edit");
            DisplayNetwork.open(player, display); session = ((Map<?, ?>)sessions.get(null)).get(player.getUUID()); token = (UUID)tokenMethod.invoke(session);
            player.setPos(pos.getX() + 100, pos.getY(), pos.getZ());
            receive.invoke(null, player, new DisplayNetwork.Packet(1, token, sample()));
            h.assertTrue(display.document().equals(DisplayDocument.EMPTY), "Out of reach save rejected");
            DisplayNetwork.forget(player.getUUID()); h.succeed();
        } catch (ReflectiveOperationException exception) { throw new RuntimeException(exception); }
    }
}
