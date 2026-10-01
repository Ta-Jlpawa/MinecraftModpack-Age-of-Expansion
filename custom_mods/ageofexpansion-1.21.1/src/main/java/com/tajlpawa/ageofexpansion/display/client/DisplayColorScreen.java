package com.tajlpawa.ageofexpansion.display.client;

import java.awt.Color;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

// HSV 色板与十六进制输入共用颜色值，确认后才应用选区。
public final class DisplayColorScreen extends Screen {
    private final Screen parent;
    private final Runnable back;
    private final IntConsumer apply;
    private float hue, saturation, brightness;
    private int left, top, dragMode;
    private EditBox hex;
    private Button done;
    private boolean updating;
    DisplayColorScreen(DisplayScreen parent, int color, IntConsumer apply) {
        this(parent,color,apply,parent::returnFromPalette);
    }
    public DisplayColorScreen(Screen parent,int color,IntConsumer apply,Runnable back){
        super(DisplayScreen.label("color")); this.parent = parent; this.apply = apply;this.back=back;
        float[] hsv = Color.RGBtoHSB((color >> 16) & 255, (color >> 8) & 255, color & 255, null);
        hue = hsv[0]; saturation = hsv[1]; brightness = hsv[2];
    }
    private int color() { return Color.HSBtoRGB(hue, saturation, brightness) & 0xffffff; }
    @Override protected void init() {
        left = (width - 264) / 2; top = (height - 212) / 2;
        hex = new EditBox(font, left + 180, top + 47, 70, 20, Component.literal("#RRGGBB"));
        hex.setMaxLength(7); hex.setValue(String.format("#%06X", color()));
        hex.setFilter(value -> value.matches("#?[0-9a-fA-F]{0,6}"));
        hex.setResponder(value -> {
            if (updating) return;
            boolean valid = value.matches("#?[0-9a-fA-F]{6}");
            if (done != null) done.active = valid;
            if (valid) {
                int rgb = Integer.parseInt(value.replace("#", ""), 16);
                float[] hsv = Color.RGBtoHSB((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255, null);
                hue = hsv[0]; saturation = hsv[1]; brightness = hsv[2];
            }
        });
        addRenderableWidget(hex);
        int[] colors = {0xffffff, 0xaaaaaa, 0x555555, 0, 0xff5555, 0xffaa00, 0xffff55, 0x55ff55,
            0x55ffff, 0x5555ff, 0xff55ff, 0xaa0000, 0x00aa00, 0x00aaaa, 0x0000aa, 0xaa00aa};
        for (int i = 0; i < colors.length; i++) {
            int rgb = colors[i];
            addRenderableWidget(Button.builder(Component.literal("■").withColor(rgb), b -> {
                float[] hsv = Color.RGBtoHSB((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255, null);
                hue = hsv[0]; saturation = hsv[1]; brightness = hsv[2]; syncHex();
            }).bounds(left + 12 + i * 15, top + 155, 15, 18).build());
        }
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose()).bounds(left + 12, top + 183, 116, 20).build());
        done = addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> {
            if (done.active) { apply.accept(color()); back.run(); }
        }).bounds(left + 136, top + 183, 116, 20).build());
    }
    private void syncHex() { updating = true; hex.setValue(String.format("#%06X", color())); updating = false; done.active = true; }
    private void pick(double x, double y) {
        if (dragMode == 1) {
            saturation = Math.clamp((float)(x - left - 12) / 128, 0, 1);
            brightness = 1 - Math.clamp((float)(y - top - 22) / 128, 0, 1);
        } else if (dragMode == 2) hue = Math.clamp((float)(y - top - 22) / 128, 0, 1);
        syncHex();
    }
    @Override public boolean mouseClicked(double x, double y, int button) {
        if (button == 0 && y >= top + 22 && y <= top + 150) {
            if (x >= left + 12 && x <= left + 140) dragMode = 1;
            else if (x >= left + 149 && x <= left + 165) dragMode = 2;
            if (dragMode != 0) { pick(x, y); return true; }
        }
        return super.mouseClicked(x, y, button);
    }
    @Override public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (dragMode != 0) { pick(x, y); return true; } return super.mouseDragged(x, y, button, dx, dy);
    }
    @Override public boolean mouseReleased(double x, double y, int button) { dragMode = 0; return super.mouseReleased(x, y, button); }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xbb000000);
        graphics.fill(left, top, left + 264, top + 212, 0xff25292e); graphics.renderOutline(left, top, 264, 212, 0xff707982);
        graphics.drawCenteredString(font, title, width / 2, top + 7, 0xffffffff);
        for (int y = 0; y < 128; y += 4) for (int x = 0; x < 128; x += 4)
            graphics.fill(left + 12 + x, top + 22 + y, left + 16 + x, top + 26 + y, Color.HSBtoRGB(hue, x / 127f, 1 - y / 127f));
        for (int y = 0; y < 128; y += 2) graphics.fill(left + 149, top + 22 + y, left + 165, top + 24 + y, Color.HSBtoRGB(y / 127f, 1, 1));
        int sx = left + 12 + Math.round(saturation * 127), sy = top + 22 + Math.round((1 - brightness) * 127);
        graphics.renderOutline(sx - 2, sy - 2, 5, 5, 0xffffffff);
        graphics.renderOutline(left + 147, top + 20 + Math.round(hue * 127), 20, 5, 0xffffffff);
        graphics.drawString(font, "#RRGGBB", left + 180, top + 31, 0xffdddddd, false);
        graphics.fill(left + 180, top + 79, left + 250, top + 126, 0xff10171b);
        graphics.drawCenteredString(font, DisplayScreen.label("sample").copy().withColor(color()), left + 215, top + 96, 0xffffffff);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    @Override public void onClose() { back.run(); }
    @Override public boolean isPauseScreen() { return false; }
    @Override public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}
}

