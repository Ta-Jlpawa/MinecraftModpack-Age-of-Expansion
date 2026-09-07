package com.tajlpawa.ageofexpansionmodpackfix.gametest.client;

import com.tajlpawa.ageofexpansionmodpackfix.create.*;
import com.tajlpawa.ageofexpansionmodpackfix.create.client.StressRouterScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

// Explicit opt-in smoke run renders the real screens without opening a player save.
@EventBusSubscriber(modid = StressRouterContent.MOD_ID, value = Dist.CLIENT)
public final class StressRouterClientSmoke {
    private static int frames;
    private static int captured;
    @SubscribeEvent public static void render(ScreenEvent.Render.Post event) throws Exception {
        if (!Boolean.getBoolean("ageofexpansion.routerClientSmoke")) return;
        var mc = Minecraft.getInstance();
        if (mc.getOverlay() != null || ++frames < 20) return;
        if (captured == 2) { mc.stop(); return; }
        var inventory = new Inventory(null);
        var menu = new StressRouterMenu(1, inventory);
        int[] values = {4096, 2048, 73, 64, -128, captured, 1, captured == 0 ? 6144 : 2990, captured == 0 ? 0 : 1106, 0, 0, 1};
        for (int i = 0; i < values.length; i++) {
            menu.setData(i * 2, values[i] & 65535);
            menu.setData(i * 2 + 1, values[i] >>> 16);
        }
        var screen = new StressRouterScreen(menu, inventory, Component.translatable("block.ageofexpansionmodpackfix.stress_network_" + (captured == 0 ? "combiner" : "splitter")));
        screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        checkDragging(screen, mc);
        screen = new StressRouterScreen(menu, inventory, Component.translatable("block.ageofexpansionmodpackfix.stress_network_" + (captured == 0 ? "combiner" : "splitter")));
        screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        event.getGuiGraphics().flush();
        com.mojang.blaze3d.systems.RenderSystem.clear(256, false);
        screen.render(event.getGuiGraphics(), -1, -1, 0);
        event.getGuiGraphics().flush();
        var file = new java.io.File(mc.gameDirectory, (captured == 0 ? "combiner" : "splitter") + ".png");
        try (var pixels = Screenshot.takeScreenshot(mc.getMainRenderTarget())) { pixels.writeToFile(file); }
        captured++;
        frames = 0;
    }
    private static void checkDragging(StressRouterScreen screen, Minecraft mc) {
        var fields = screen.children().stream().filter(net.minecraft.client.gui.components.EditBox.class::isInstance)
                .map(net.minecraft.client.gui.components.EditBox.class::cast).toList();
        if (!fields.getFirst().getValue().equals("4096")) throw new AssertionError("First frame must contain synchronized input");
        fields.getFirst().setValue("12345678");
        for (var child : screen.children()) {
            if (!(child instanceof com.tajlpawa.ageofexpansionmodpackfix.create.client.RouterSlider slider)) continue;
            boolean speed = slider.isSpeed();
            boolean vertical = false;
            double x = slider.getX() + slider.getWidth() / 2d, y = slider.getY() + slider.getHeight() / 2d;
            screen.mouseClicked(x, y, 0);
            screen.mouseDragged(vertical ? x : slider.getX() + slider.getWidth(), vertical ? slider.getY() : y, 0, 20, -20);
            if (slider.number() != (speed ? 256 : 100)) throw new AssertionError("Dragging must reach upper endpoint");
            screen.mouseDragged(vertical ? x : slider.getX(), vertical ? slider.getY() + slider.getHeight() : y, 0, -20, 20);
            if (slider.number() != (speed ? -256 : 0)) throw new AssertionError("Dragging must reach lower endpoint");
            screen.mouseReleased(x, y, 0);
        }
        screen.resize(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        var restored = screen.children().stream().filter(net.minecraft.client.gui.components.EditBox.class::isInstance)
                .map(net.minecraft.client.gui.components.EditBox.class::cast).findFirst().orElseThrow();
        if (!restored.getValue().equals("12345678")) throw new AssertionError("Resize must retain unsaved edits");
        System.out.println("ROUTER_GUI_CHECK: initial values, drag endpoints and unsaved resize state passed");
    }
}
