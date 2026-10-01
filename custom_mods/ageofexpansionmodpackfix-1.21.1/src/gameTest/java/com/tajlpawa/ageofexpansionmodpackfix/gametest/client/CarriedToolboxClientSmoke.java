package com.tajlpawa.ageofexpansionmodpackfix.gametest.client;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllKeys;
import com.simibubi.create.content.equipment.toolbox.*;
import com.tajlpawa.ageofexpansionmodpackfix.create.toolbox.CarriedToolboxes;
import com.tajlpawa.ageofexpansionmodpackfix.mixin.create.toolbox.ToolboxBlockEntityAccess;
import java.nio.file.Files;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.CuriosApi;

// Focused real-client regression for radial transitions, release events and native item binding.
@EventBusSubscriber(modid = "ageofexpansionmodpackfix", value = Dist.CLIENT)
public final class CarriedToolboxClientSmoke {
    private static int phase, ticks, rounds;
    private static long started;
    private static RadialToolboxMenu lastMenu;
    private static Throwable queuedFailure;

    private static void server(Consumer<ServerPlayer> action) {
        var mc = Minecraft.getInstance();
        var id = mc.player.getUUID();
        mc.getSingleplayerServer().execute(() -> action.accept(mc.getSingleplayerServer().getPlayerList().getPlayer(id)));
    }
    private static Object field(Object object, String name) throws Exception {
        var field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
    }
    private static void set(Object object, String name, Object value) throws Exception {
        var field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(object, value);
    }
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    private static int key() { return AllKeys.TOOLBELT.getKeybind().getKey().getValue(); }
    private static void press() { NeoForge.EVENT_BUS.post(new InputEvent.Key(key(), 0, org.lwjgl.glfw.GLFW.GLFW_PRESS, 0)); }
    private static RadialToolboxMenu choices() throws Exception {
        var mc = Minecraft.getInstance();
        check(mc.screen instanceof RadialToolboxMenu, "Expected native toolbox choice screen");
        var menu = (RadialToolboxMenu) mc.screen;
        check(field(menu, "state") == RadialToolboxMenu.State.SELECT_BOX, "Multiple toolboxes must start with SELECT_BOX");
        check(((java.util.List<?>) field(menu, "toolboxes")).getFirst() == CarriedToolboxes.get(mc.player), "Worn toolbox must remain first");
        return menu;
    }
    private static void choose(int index) throws Exception {
        lastMenu = choices();
        set(lastMenu, "hoveredSlot", index);
        set(lastMenu, "scrollMode", false);
        lastMenu.mouseClicked(0, 0, 0);
        check(Minecraft.getInstance().screen == lastMenu && field(lastMenu, "state") != RadialToolboxMenu.State.SELECT_BOX,
                "Selection must change the same screen to an item menu");
    }
    private static void release() throws Exception {
        set(lastMenu, "hoveredSlot", -1);
        set(lastMenu, "scrollMode", false);
        lastMenu.keyReleased(key(), 0, 0);
        check(Minecraft.getInstance().screen == null, "Native key release closes the radial menu");
        NeoForge.EVENT_BUS.post(new InputEvent.Key(key(), 0, org.lwjgl.glfw.GLFW.GLFW_RELEASE, 0));
    }

    @SubscribeEvent public static void tick(ClientTickEvent.Post event) throws Exception {
        if (!Boolean.getBoolean("ageofexpansion.toolboxClientSmoke")) return;
        var mc = Minecraft.getInstance();
        if (queuedFailure != null) throw new AssertionError("Queued input failed", queuedFailure);
        if (started == 0) started = System.currentTimeMillis();
        if (System.currentTimeMillis() - started > 120000) throw new AssertionError("Radial probe timeout phase=" + phase);
        if (phase == 0 && mc.screen instanceof TitleScreen && mc.getOverlay() == null) {
            phase = 1; mc.options.pauseOnLostFocus = false;
            if (Files.exists(mc.gameDirectory.toPath().resolve("saves/ToolboxProbe/level.dat"))) {
                mc.createWorldOpenFlows().openWorld("ToolboxProbe", () -> { throw new AssertionError("Cannot load toolbox probe"); });
            } else {
                var settings = new LevelSettings("ToolboxProbe", GameType.SURVIVAL, false, Difficulty.PEACEFUL, true, new GameRules(), WorldDataConfiguration.DEFAULT);
                mc.createWorldOpenFlows().createFreshLevel("ToolboxProbe", settings, new WorldOptions(42L, false, false),
                        registry -> registry.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT).value().createWorldDimensions(), mc.screen);
            }
            return;
        }
        if (mc.player == null || mc.level == null || mc.getOverlay() != null) return;
        ticks++;
        switch (phase) {
            case 1 -> {
                mc.setScreen(null);
                server(player -> {
                    player.closeContainer(); player.getInventory().clearContent(); player.getInventory().selected = 0;
                    player.getPersistentData().remove("CreateToolboxData"); ToolboxHandler.syncData(player);
                    var inventory = new ToolboxInventory(null);
                    inventory.insertItem(0, new ItemStack(Items.COBBLESTONE, 64), false);
                    var stack = AllBlocks.TOOLBOXES.get(DyeColor.BROWN).asStack();
                    stack.set(AllDataComponents.TOOLBOX_INVENTORY, inventory);
                    CuriosApi.getCuriosInventory(player).orElseThrow().getStacksHandler("belt").orElseThrow().getStacks().setStackInSlot(0, stack);
                    var pos = player.blockPosition().east(2);
                    player.level().setBlockAndUpdate(pos, AllBlocks.TOOLBOXES.get(DyeColor.BLUE).getDefaultState());
                    var world = (ToolboxBlockEntity) player.level().getBlockEntity(pos);
                    world.setUniqueId(UUID.randomUUID());
                    ((ToolboxBlockEntityAccess) world).expansion$getInventory().insertItem(0, new ItemStack(Items.DIRT, 64), false);
                    world.sendData();
                    player.inventoryMenu.broadcastChanges();
                });
                phase = 2; ticks = 0;
            }
            case 2 -> {
                if (ticks < 30 || CarriedToolboxes.get(mc.player) == null || ToolboxHandler.getNearest(mc.level, mc.player, 8).size() < 2) return;
                mc.player.getInventory().selected = 0;
                press(); phase = 3; ticks = 0;
            }
            case 3 -> {
                if (ticks < 2) return;
                choose(1);
                release();
                phase = 4; ticks = 0;
            }
            case 4 -> {
                if (ticks < 2) return;
                check(mc.screen == null, "BUG: releasing shortcut after selecting a toolbox reopened SELECT_BOX");
                System.out.println("TOOLBOX_RADIAL_CHECK: shortcut release does not reopen choice menu");
                press();
                mc.tell(() -> { try { choose(1); } catch (Throwable failure) { queuedFailure = failure; } });
                press();
                phase = 5; ticks = 0;
            }
            case 5 -> {
                if (ticks < 2) return;
                check(mc.screen == lastMenu && field(lastMenu, "state") == RadialToolboxMenu.State.SELECT_ITEM,
                        "BUG: two queued key presses replaced selected item menu with SELECT_BOX");
                release();
                phase = 6; ticks = 0;
            }
            case 6 -> {
                if (ticks < 2) return;
                check(mc.screen == null, "Release must not create another menu");
                press(); phase = 7; ticks = 0;
            }
            case 7 -> {
                if (ticks < 2) return;
                choose(rounds % 2);
                check(field(lastMenu, "state") == RadialToolboxMenu.State.SELECT_ITEM, "Chosen toolbox must use radial item selection");
                release();
                if (++rounds < 32) { phase = 6; ticks = 0; }
                else { phase = 8; ticks = 0; }
            }
            case 8 -> {
                if (ticks < 2) return;
                check(mc.screen == null, "32 repeated transitions must not reopen a choice menu");
                press(); phase = 9; ticks = 0;
            }
            case 9 -> {
                if (ticks < 2) return;
                choose(0);
                set(lastMenu, "hoveredSlot", 0);
                lastMenu.mouseClicked(0, 0, 0);
                phase = 10; ticks = 0;
            }
            case 10 -> {
                var box = CarriedToolboxes.get(mc.player);
                if (ticks < 6 || box == null || mc.player.getInventory().getItem(0).getCount() != 32 || box.contents().takeFromCompartment(1000, 0, true).getCount() != 32) return;
                press(); phase = 11; ticks = 0;
            }
            case 11 -> {
                if (ticks < 2) return;
                choose(0);
                check(field(lastMenu, "state") == RadialToolboxMenu.State.SELECT_ITEM_UNEQUIP, "Bound worn compartment retains native unequip menu");
                set(lastMenu, "hoveredSlot", -5);
                lastMenu.mouseClicked(0, 0, 0);
                phase = 12; ticks = 0;
            }
            case 12 -> {
                var box = CarriedToolboxes.get(mc.player);
                if (ticks < 6 || !mc.player.getInventory().getItem(0).isEmpty() || box.contents().takeFromCompartment(1000, 0, true).getCount() != 64) return;
                server(player -> ToolboxHandler.getNearest(player.level(), player, 8).stream().filter(b -> !CarriedToolboxes.ADDRESS.equals(b.getBlockPos())).toList().forEach(b -> player.level().destroyBlock(b.getBlockPos(), false)));
                phase = 13; ticks = 0;
            }
            case 13 -> {
                if (ticks < 6 || ToolboxHandler.getNearest(mc.level, mc.player, 8).size() != 1) return;
                press(); phase = 14; ticks = 0;
            }
            case 14 -> {
                if (ticks < 2) return;
                check(mc.screen instanceof RadialToolboxMenu, "Single worn toolbox must open radial item menu");
                lastMenu = (RadialToolboxMenu) mc.screen;
                check(field(lastMenu, "state") == RadialToolboxMenu.State.SELECT_ITEM, "Single worn toolbox skips box selection");
                release(); phase = 15; ticks = 0;
            }
            case 15 -> {
                if (ticks < 2) return;
                check(mc.screen == null, "Single toolbox release must not reopen");
                System.out.println("CARRIED_TOOLBOX_CLIENT_PASS: release/queued repeat guards, 32 repeated worn/world transitions, first-slot priority, native take/return and single-box radial access");
                phase = 16; mc.stop();
            }
        }
    }
}
