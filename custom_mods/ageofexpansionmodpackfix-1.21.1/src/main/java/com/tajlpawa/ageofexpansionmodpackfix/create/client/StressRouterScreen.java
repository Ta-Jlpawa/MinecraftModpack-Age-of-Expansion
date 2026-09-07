package com.tajlpawa.ageofexpansionmodpackfix.create.client;

import com.tajlpawa.ageofexpansionmodpackfix.create.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class StressRouterScreen extends AbstractContainerScreen<StressRouterMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(StressRouterContent.MOD_ID, "textures/gui/stress_router.png");
    private EditBox inputA, inputB;
    private RouterSlider ratio, speedA, speedB, dragging;
    private boolean splitter, submitted, initialized;
    private int a, b, split, rpmA, rpmB;
    public StressRouterScreen(StressRouterMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth = 340; imageHeight = 238; }
    public static Component tr(String key, Object... args) { return Component.translatable("gui.ageofexpansionmodpackfix.router." + key, args); }
    @Override protected void init() {
        if (initialized) remember();
        else {
            a = menu.value(0); b = menu.value(1); split = menu.value(2);
            rpmA = StressRouterBlockEntity.safeSpeed(menu.value(3)); rpmB = StressRouterBlockEntity.safeSpeed(menu.value(4));
            splitter = menu.value(5) != 0;
        }
        super.init();
        inputA = field(70, a);
        inputB = splitter ? null : field(134, b);
        ratio = splitter ? addRenderableWidget(new RouterSlider(leftPos + 25, topPos + 136, 132, 20, false, split)) : null;
        speedA = addRenderableWidget(new RouterSlider(leftPos + 191, topPos + (splitter ? 70 : 102), 128, 20, true, rpmA));
        speedB = splitter ? addRenderableWidget(new RouterSlider(leftPos + 191, topPos + 136, 128, 20, true, rpmB)) : null;
        initialized = true;
    }
    private EditBox field(int y, int value) {
        EditBox box = new EditBox(font, leftPos + 27, topPos + y, 128, 16, tr("stress"));
        box.setMaxLength(8); box.setBordered(false); box.setTextColor(0xffd8e7df);
        box.setTooltip(net.minecraft.client.gui.components.Tooltip.create(tr("input_range")));
        box.setFilter(text -> text.matches("[0-9]*")); box.setValue(Integer.toString(value));
        return addRenderableWidget(box);
    }
    private int parsed(EditBox box, int fallback) {
        if (box == null) return fallback;
        if (box.getValue().isEmpty()) return 0;
        try { return Math.clamp(Integer.parseInt(box.getValue()), 0, StressRouterBlockEntity.MAX_STRESS); }
        catch (NumberFormatException exception) { return fallback; }
    }
    private void remember() {
        a = parsed(inputA, a); b = parsed(inputB, b);
        if (ratio != null) split = ratio.number();
        if (speedA != null) rpmA = speedA.number();
        if (speedB != null) rpmB = speedB.number();
    }
    @Override public void onClose() {
        if (!submitted && initialized) {
            remember();
            PacketDistributor.sendToServer(new StressRouterPayload(menu.containerId, a, b, split, rpmA, rpmB));
            submitted = true;
        }
        // Queue settings before vanilla closes the server-side menu.
        super.onClose();
    }
    @Override public boolean mouseClicked(double x, double y, int button) {
        for (RouterSlider slider : new RouterSlider[]{ratio, speedA, speedB}) {
            if (slider != null && button == 0 && slider.isMouseOver(x, y)) {
                setFocused(slider); dragging = slider; setDragging(true); return slider.mouseClicked(x, y, button);
            }
        }
        return super.mouseClicked(x, y, button);
    }
    @Override public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        return dragging != null && button == 0 ? dragging.mouseDragged(x, y, button, dx, dy) : super.mouseDragged(x, y, button, dx, dy);
    }
    @Override public boolean mouseReleased(double x, double y, int button) {
        if (dragging != null) { dragging.mouseReleased(x, y, button); dragging = null; setDragging(false); return true; }
        return super.mouseReleased(x, y, button);
    }
    @Override protected void renderBg(GuiGraphics g, float partial, int mouseX, int mouseY) {
        g.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        if (!splitter) {
            g.fill(leftPos + 24, topPos + 129, leftPos + 158, topPos + 155, 0xff605c4d);
            g.fill(leftPos + 25, topPos + 130, leftPos + 157, topPos + 154, 0xff252e2d);
        }
    }
    @Override protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, title, (imageWidth - font.width(title)) / 2, 13, 0xfff3e6c3, false);
        g.drawString(font, tr("input_section"), 24, 39, 0xff665139, false);
        g.drawString(font, tr("speed_section"), 191, 39, 0xff665139, false);
        g.drawString(font, tr(splitter ? "front_input" : "left_input"), 25, 55, 0xff383e38, false);
        if (splitter) {
            g.drawString(font, tr("distribution"), 25, 111, 0xff383e38, false);
            g.fill(25, 160, 157, 174, 0xff35423e);
            g.drawCenteredString(font, tr("ratio", ratio.number(), 100 - ratio.number()), 91, 163, 0xffffecc1);
        } else g.drawString(font, tr("right_input"), 25, 119, 0xff383e38, false);
        speedLabels(g, speedA, splitter ? 55 : 87, splitter ? "left_output" : "front_output");
        if (splitter) speedLabels(g, speedB, 121, "right_output");
        int state = menu.value(6);
        int color = state == 1 ? 0xff83d18a : state == 3 ? 0xffefcd6a : 0xfff18370;
        g.fill(22, 201, 27, 206, color); g.drawString(font, tr("status." + state), 33, 200, color, false);
        g.drawString(font, splitter ? tr("actual", menu.value(7), menu.value(8)) : tr("output_total", menu.value(7)), 22, 215, 0xffdfddc9, false);
    }
    private void speedLabels(GuiGraphics g, RouterSlider slider, int y, String side) {
        g.drawString(font, tr(side), 191, y, 0xff444b40, false);
        g.fill(191, y + 38, 319, y + 64, 0xff35423e);
        g.drawCenteredString(font, Component.literal(Math.abs(slider.number()) + " RPM"), 255, y + 40, 0xffffecc1);
        g.drawCenteredString(font, tr(slider.number() > 0 ? "clockwise" : "counterclockwise"), 255, y + 52, 0xffd9efe5);
    }
}
