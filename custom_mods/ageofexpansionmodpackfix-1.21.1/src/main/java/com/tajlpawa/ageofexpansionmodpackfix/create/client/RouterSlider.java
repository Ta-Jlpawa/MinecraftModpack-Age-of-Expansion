package com.tajlpawa.ageofexpansionmodpackfix.create.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import static com.tajlpawa.ageofexpansionmodpackfix.create.client.StressRouterScreen.tr;

public final class RouterSlider extends AbstractSliderButton {
    private final boolean speed;
    public RouterSlider(int x, int y, int w, int h, boolean speed, int initial) {
        super(x, y, w, h, Component.empty(), speed ? speedIndex(initial == 0 ? 1 : initial) / 511d : Math.clamp(initial, 0, 100) / 100d);
        this.speed = speed;
        setTooltip(net.minecraft.client.gui.components.Tooltip.create(tr(speed ? "speed_help" : "ratio_help")));
        updateMessage();
    }
    private static int speedIndex(int speed) { return speed < 0 ? speed + 256 : speed + 255; }
    public int number() { int n = (int) Math.round(value * (speed ? 511 : 100)); return speed ? (n < 256 ? n - 256 : n - 255) : n; }
    private void pointer(double x, double y) {
        value = Math.clamp((x - getX() - 7) / (width - 14), 0, 1);
        updateMessage();
    }
    @Override public void onClick(double x, double y) { pointer(x, y); }
    @Override protected void onDrag(double x, double y, double dx, double dy) { pointer(x, y); }
    @Override public boolean keyPressed(int key, int scan, int modifiers) {
        if (key >= 262 && key <= 265) {
            value = Math.clamp(value + (key == 262 || key == 265 ? 1 : -1) / (speed ? 511d : 100d), 0, 1);
            updateMessage(); return true;
        }
        return super.keyPressed(key, scan, modifiers);
    }
    @Override public boolean mouseScrolled(double x, double y, double dx, double dy) {
        if (!isMouseOver(x, y)) return false;
        value = Math.clamp(value + Math.signum(dy) / (speed ? 511d : 100d), 0, 1); updateMessage(); return true;
    }
    @Override protected void updateMessage() { setMessage(Component.literal(Integer.toString(number()))); }
    @Override protected void applyValue() {}
    @Override public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
        int x = getX(), y = getY();
        g.fill(x + 4, y + 8, x + width - 4, y + 13, 0xff544e40);
        g.fill(x + 5, y + 9, x + width - 5, y + 11, 0xff23312e);
        for (int i = 0; i <= 10; i++) { int column = x + 7 + i * (width - 14) / 10; g.fill(column, y + 15, column + 1, y + 18, 0xff92856a); }
        knob(g, x + (int) Math.round(value * (width - 14)), y + 4, 14, 12);
    }
    public boolean isSpeed() { return speed; }
    private void knob(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, 0xff493f31);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, isHoveredOrFocused() ? 0xffdfc993 : 0xffbca574);
        g.fill(x + 2, y + 1, x + w - 2, y + 2, 0xfff5e4b2);
        g.fill(x + 3, y + h / 2, x + w - 3, y + h / 2 + 1, 0xff4a6968);
    }
}
