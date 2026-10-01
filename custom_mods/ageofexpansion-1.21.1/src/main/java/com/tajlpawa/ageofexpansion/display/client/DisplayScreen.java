package com.tajlpawa.ageofexpansion.display.client;

import com.tajlpawa.ageofexpansion.display.DisplayDocument;
import com.tajlpawa.ageofexpansion.display.DisplayDocument.Glyph;
import com.tajlpawa.ageofexpansion.display.DisplayDocument.Ink;
import com.tajlpawa.ageofexpansion.display.DisplayNetwork;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

// 独立编辑草稿：工具栏和调色板切换焦点时保留字符选区。
public final class DisplayScreen extends Screen {
    private final UUID token;
    private final DisplayNetwork.Meta meta;
    private DisplayDocument document;
    private DisplayLayout layout;
    private int caret, anchor;
    private Ink typing = Ink.WHITE;
    private boolean textFocus = true, dragging, pending, finished, invalid;
    private final ArrayDeque<Edit> undo = new ArrayDeque<>(), redo = new ArrayDeque<>();
    private record Snapshot(DisplayDocument document, int caret, int anchor, Ink typing) {}
    private record Position(int size,int align,int vertical,int caret,int anchor,Ink typing) {}
    private record Edit(int start,List<Glyph> removed,List<Glyph> inserted,Position before,Position after) {}
    private Snapshot undoSource;
    private DisplayLayout.Job layoutJob;
    private float scroll;
    private boolean scrollDragging;
    private double dragX,dragY;
    private EditBox sizeBox;
    private Button done;
    private Button clearButton;
    private final List<Button> formatButtons = new ArrayList<>(), alignmentButtons = new ArrayList<>();
    private final List<Button> verticalButtons = new ArrayList<>();
    private int left, top, panelWidth, panelHeight, previewX, previewY, previewWidth, previewHeight;
    private Component status = Component.empty();
    private long lastClick;
    public DisplayScreen(UUID token, DisplayDocument document) {
        this(token,document,DisplayNetwork.Meta.EMPTY);
    }
    public DisplayScreen(UUID token,DisplayDocument document,DisplayNetwork.Meta meta) {
        super(Component.translatable("block.ageofexpansion."+((meta.kind()==0?"":meta.kind()==1?"square_":"modular_")+(meta.holographic()?"holographic_display":"electronic_display"))));
        this.token=token;this.document=document.metadata(Math.clamp(document.size(),meta.minFontSize(),meta.maxFontSize()),document.alignment(),document.vertical());this.meta=meta;
    }
    static Component label(String key) { return Component.translatable("screen.ageofexpansion.display." + key); }
    @Override protected void init() {
        panelWidth = Math.min(410, width - 12); panelHeight = Math.min(350, height - 12);
        left = (width - panelWidth) / 2; top = (height - panelHeight) / 2;
        int buttonWidth = (panelWidth - 28) / 5;
        formatButtons.clear(); alignmentButtons.clear(); verticalButtons.clear();
        String[] labels = {"bold", "italic", "obfuscated", "color"};
        for (int i = 0; i < 4; i++) {
            final int index = i;
            Button button = addRenderableWidget(Button.builder(label(labels[i]), b -> {
                if (index == 3) minecraft.setScreen(new DisplayColorScreen(this, currentInk().color(), this::setColor));
                else toggle(1 << index);
            }).bounds(left + 10 + i * (buttonWidth + 2), top + 24, buttonWidth, 18).build());
            formatButtons.add(button);
        }
        clearButton=addRenderableWidget(Button.builder(label("clear"),button->clearText())
            .bounds(left+10+4*(buttonWidth+2),top+24,buttonWidth,18).build());
        int ratio=meta.kind()==0?2:1;
        previewHeight = Math.min((panelWidth - 28) / ratio, panelHeight - 108);
        previewWidth = previewHeight * ratio; previewX = left + (panelWidth - previewWidth) / 2; previewY = top + 49;
        int controlsY = top+panelHeight-51;
        String[] alignments = {"left", "center", "right"};
        for (int i = 0; i < 3; i++) {
            final int alignment = i;
            alignmentButtons.add(addRenderableWidget(Button.builder(label(alignments[i]), b -> {
                if (document.alignment() != alignment) { remember(); document = document.metadata(document.size(), alignment, document.vertical()); changed(); }
                focusText();
            }).bounds(left + 10 + i * 25, controlsY, 23, 17).build()));
        }
        String[] verticals={"top","middle","bottom"};
        for(int i=0;i<3;i++){
            final int value=i;
            verticalButtons.add(addRenderableWidget(Button.builder(label(verticals[i]),button->{
                if(document.vertical()!=value){remember();document=document.metadata(document.size(),document.alignment(),value);changed();}focusText();
            }).bounds(left+93+i*25,controlsY,23,17).build()));
        }
        sizeBox = new EditBox(font, left + panelWidth - 52, controlsY, 42, 17, label("size"));
        sizeBox.setMaxLength(4); sizeBox.setValue(Integer.toString(document.size()));
        sizeBox.setFilter(value -> value.matches("[0-9]*"));
        sizeBox.setResponder(value -> {
            int size = value.isEmpty() ? 0 : Integer.parseInt(value);
            if (size >= meta.minFontSize() && size <= meta.maxFontSize()) {
                if (size != document.size()) { remember(); document = document.metadata(size, document.alignment(), document.vertical()); changed(); }
                status = Component.empty();
            } else status = Component.translatable("screen.ageofexpansion.display.size_error",meta.minFontSize(),meta.maxFontSize());
            updateButtons();
        });
        addRenderableWidget(sizeBox);
        done = addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> save())
            .bounds(left + panelWidth / 2 + 2, top + panelHeight - 21, panelWidth / 2 - 12, 17).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
            .bounds(left + 10, top + panelHeight - 21, panelWidth / 2 - 12, 17).build());
        changed();
        if (textFocus) setFocused(null);
    }
    private Snapshot snapshot() { return new Snapshot(document, caret, anchor, typing); }
    private void clearText(){
        if(pending||invalid||layoutJob!=null||document.glyphs().isEmpty())return;
        remember();document=document.replace(0,document.glyphs().size(),List.of());caret=anchor=0;scroll=0;typing=Ink.WHITE;
        status=Component.empty();changed();focusText();
    }
    private static Position position(Snapshot s){return new Position(s.document.size(),s.document.alignment(),s.document.vertical(),s.caret,s.anchor,s.typing);}
    private void remember() { undoSource=snapshot(); }
    private void restore(Edit edit,boolean forward) {
        Position p=forward?edit.after:edit.before;
        document=document.replace(edit.start,edit.start+(forward?edit.removed.size():edit.inserted.size()),forward?edit.inserted:edit.removed).metadata(p.size,p.align,p.vertical);
        caret=p.caret;anchor=p.anchor;typing=p.typing;
        sizeBox.setResponder(value -> {}); sizeBox.setValue(Integer.toString(document.size()));
        rebuildWidgets(); changed(); focusText();
    }
    private int start() { return Math.min(caret, anchor); }
    private int end() { return Math.max(caret, anchor); }
    private Ink currentInk() { return caret != anchor ? document.glyphs().get(start()).ink() : typing; }
    private void focusText() { textFocus = true; setFocused(null); }
    void returnFromPalette() { focusText(); minecraft.setScreen(this); }
    private float logicalWidth(){return meta.kind()==1?108:120;}
    private float logicalHeight(){return meta.kind()==0?60:meta.kind()==1?108:120;}
    private void changed() {
        if(undoSource!=null){
            DisplayDocument old=undoSource.document;int start=0,oldEnd=old.glyphs().size(),newEnd=document.glyphs().size();
            while(start<oldEnd&&start<newEnd&&old.packed(start)==document.packed(start))start++;
            while(oldEnd>start&&newEnd>start&&old.packed(oldEnd-1)==document.packed(newEnd-1)){oldEnd--;newEnd--;}
            undo.addLast(new Edit(start,old.slice(start,oldEnd),document.slice(start,newEnd),position(undoSource),position(snapshot())));
            if(undo.size()>100)undo.removeFirst();redo.clear();undoSource=null;
        }
        layoutJob=new DisplayLayout.Job(document,logicalWidth(),logicalHeight(),meta.kind()==2);
        DisplayLayout next=layoutJob.step(font,8192);
        if(next!=null){layout=next;layoutJob=null;ensureCaret();}
        else if(layout==null)layout=DisplayLayout.create(font,DisplayDocument.EMPTY,logicalWidth(),logicalHeight(),meta.kind()==2);
        updateButtons();
    }
    private float maxScroll(){return meta.kind()==2?Math.max(0,layout.contentHeight()-logicalHeight()):0;}
    private void ensureCaret(){
        if(meta.kind()!=2)return;float y=layout.y(layout.rowFor(caret));
        if(y<scroll)scroll=y;else if(y+10>scroll+logicalHeight())scroll=y+10-logicalHeight();
        scroll=Math.clamp(scroll,0,maxScroll());
    }
    @Override public void tick(){
        super.tick();
        if(layoutJob!=null){DisplayLayout next=layoutJob.step(font,8192);if(next!=null){layout=next;layoutJob=null;ensureCaret();updateButtons();}}
        if(dragging&&meta.kind()==2&&(dragY<previewY||dragY>previewY+previewHeight)){
            scroll=Math.clamp(scroll+(dragY<previewY?-8:8),0,maxScroll());caret=hit(dragX,dragY);updateButtons();
        }
    }
    private void updateButtons() {
        for (int i = 0; i < 3 && i < formatButtons.size(); i++) {
            int flag = 1 << i;
            boolean active = caret == anchor ? (typing.flags() & flag) != 0
                : document.glyphs().subList(start(), end()).stream().allMatch(g -> (g.ink().flags() & flag) != 0);
            String key = new String[]{"bold", "italic", "obfuscated"}[i];
            formatButtons.get(i).setMessage(Component.literal(active ? "✓ " : "").append(label(key)));
        }
        for (int i = 0; i < alignmentButtons.size(); i++) alignmentButtons.get(i).active = !pending && i != document.alignment();
        for (int i=0;i<verticalButtons.size();i++)verticalButtons.get(i).active=!pending&&layoutJob==null&&i!=document.vertical();
        if (done != null) done.active = !pending && !invalid && layoutJob==null && sizeBox.getValue().equals(Integer.toString(document.size()));
        for (Button button : formatButtons) button.active = !pending && !invalid && layoutJob==null;
        if(clearButton!=null)clearButton.active=!pending&&!invalid&&layoutJob==null&&!document.glyphs().isEmpty();
    }
    private void toggle(int flag) {
        boolean remove = caret == anchor ? (typing.flags() & flag) != 0
            : document.glyphs().subList(start(), end()).stream().allMatch(g -> (g.ink().flags() & flag) != 0);
        format(ink -> new Ink(ink.color(), remove ? ink.flags() & ~flag : ink.flags() | flag));
    }
    private void setColor(int color) { format(ink -> new Ink(color, ink.flags())); }
    private void format(UnaryOperator<Ink> transform) {
        if (caret != anchor) {
            remember(); List<Glyph> glyphs = new ArrayList<>(document.slice(start(),end()));
            for (int i = 0; i < glyphs.size(); i++) { Glyph glyph = glyphs.get(i); glyphs.set(i, new Glyph(glyph.codepoint(), transform.apply(glyph.ink()))); }
            document = document.replace(start(),end(),glyphs);
        }
        typing = transform.apply(typing); changed(); focusText();
    }
    private void insert(String value) {
        if (pending || invalid || layoutJob!=null) return;
        int[] codepoints = value.replace("\r\n", "\n").replace('\r', '\n').replace("\t", "    ").codePoints()
            .filter(DisplayDocument::validCodepoint).toArray();
        if (document.glyphs().size() - (end() - start()) + codepoints.length > Math.max(meta.capacity(),document.glyphs().size())) { status = label("limit"); return; }
        if (codepoints.length == 0 && caret == anchor) return;
        remember(); List<Glyph> glyphs = new ArrayList<>(codepoints.length);
        int from = start();
        for (int cp : codepoints) glyphs.add(new Glyph(cp, typing));
        document=document.replace(from,end(),glyphs);caret=anchor=from+codepoints.length;
        status = Component.empty(); changed();
    }
    private void move(int index, boolean shift) {
        caret = Math.clamp(index, 0, document.glyphs().size()); if (!shift) anchor = caret;
        if (caret == anchor) typing = caret > 0 ? document.glyphs().get(caret - 1).ink() : Ink.WHITE;
        updateButtons();
        ensureCaret();
    }
    private int word(int direction) {
        int position = caret;
        if (direction < 0) {
            while (position > 0 && Character.isWhitespace(document.glyphs().get(position - 1).codepoint())) position--;
            while (position > 0 && !Character.isWhitespace(document.glyphs().get(position - 1).codepoint())) position--;
        } else {
            while (position < document.glyphs().size() && !Character.isWhitespace(document.glyphs().get(position).codepoint())) position++;
            while (position < document.glyphs().size() && Character.isWhitespace(document.glyphs().get(position).codepoint())) position++;
        }
        return position;
    }
    @Override public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) { onClose(); return true; }
        if(invalid&&hasControlDown()&&key==GLFW.GLFW_KEY_C){minecraft.keyboardHandler.setClipboard(document.text(0,document.glyphs().size()));return true;}
        if (pending || invalid || layoutJob!=null) return super.keyPressed(key, scanCode, modifiers);
        if (!textFocus) return super.keyPressed(key, scanCode, modifiers);
        boolean shift = hasShiftDown(), control = hasControlDown();
        if (control) {
            switch (key) {
                case GLFW.GLFW_KEY_A -> { anchor = 0; caret = document.glyphs().size(); updateButtons(); return true; }
                case GLFW.GLFW_KEY_C -> { if (caret != anchor) minecraft.keyboardHandler.setClipboard(document.text(start(), end())); return true; }
                case GLFW.GLFW_KEY_X -> { if (caret != anchor) { minecraft.keyboardHandler.setClipboard(document.text(start(), end())); insert(""); } return true; }
                case GLFW.GLFW_KEY_V -> { insert(minecraft.keyboardHandler.getClipboard()); return true; }
                case GLFW.GLFW_KEY_Z, GLFW.GLFW_KEY_Y -> {
                    boolean forward = key == GLFW.GLFW_KEY_Y || shift; var source = forward ? redo : undo; var target = forward ? undo : redo;
                    if (!source.isEmpty()) { Edit edit=source.removeLast(); target.addLast(edit); restore(edit,forward); } return true;
                }
                case GLFW.GLFW_KEY_B -> { toggle(1); return true; }
                case GLFW.GLFW_KEY_I -> { toggle(2); return true; }
            }
        }
        int row = layout.rowFor(caret); var line = layout.lines().get(row);
        switch (key) {
            case GLFW.GLFW_KEY_LEFT -> move(!shift && caret != anchor ? start() : control ? word(-1) : caret - 1, shift);
            case GLFW.GLFW_KEY_RIGHT -> move(!shift && caret != anchor ? end() : control ? word(1) : caret + 1, shift);
            case GLFW.GLFW_KEY_HOME -> move(control ? 0 : line.start(), shift);
            case GLFW.GLFW_KEY_END -> move(control ? document.glyphs().size() : line.end(), shift);
            case GLFW.GLFW_KEY_UP, GLFW.GLFW_KEY_DOWN -> {
                int next = Math.clamp(row + (key == GLFW.GLFW_KEY_UP ? -1 : 1), 0, layout.lines().size() - 1);
                move(layout.hit(layout.x(line, caret), layout.y(next) + layout.scale() * 5), shift);
            }
            case GLFW.GLFW_KEY_BACKSPACE -> { if (caret == anchor) anchor = control ? word(-1) : Math.max(0, caret - 1); insert(""); }
            case GLFW.GLFW_KEY_DELETE -> { if (caret == anchor) anchor = control ? word(1) : Math.min(document.glyphs().size(), caret + 1); insert(""); }
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> insert("\n");
            case GLFW.GLFW_KEY_TAB -> { textFocus = false; return super.keyPressed(key, scanCode, modifiers); }
            default -> { return super.keyPressed(key, scanCode, modifiers); }
        }
        return true;
    }
    private char pendingHighSurrogate;
    @Override public boolean charTyped(char character, int modifiers) {
        if (!textFocus) return super.charTyped(character, modifiers);
        if (Character.isHighSurrogate(character)) { pendingHighSurrogate = character; return true; }
        if (Character.isLowSurrogate(character) && pendingHighSurrogate != 0) {
            insert(new String(new char[]{pendingHighSurrogate, character})); pendingHighSurrogate = 0;
        } else { pendingHighSurrogate = 0; insert(Character.toString(character)); }
        return true;
    }
    private boolean inPreview(double x, double y) { return x >= previewX && x <= previewX + previewWidth && y >= previewY && y <= previewY + previewHeight; }
    private int hit(double x, double y) { return layout.hit((float)(x - previewX) * logicalWidth() / previewWidth, (float)(y - previewY) * logicalHeight() / previewHeight+scroll); }
    @Override public boolean mouseScrolled(double x,double y,double dx,double dy){
        if(meta.kind()==2&&inPreview(x,y)){scroll=Math.clamp(scroll-(float)dy*20,0,maxScroll());return true;}return super.mouseScrolled(x,y,dx,dy);
    }
    @Override public boolean mouseClicked(double x, double y, int button) {
        if(button==0&&meta.kind()==2&&maxScroll()>0&&x>=previewX+previewWidth+1&&x<=previewX+previewWidth+9&&y>=previewY&&y<=previewY+previewHeight){scrollDragging=true;scroll=Math.clamp((float)(y-previewY)/previewHeight*maxScroll(),0,maxScroll());return true;}
        if (button == 0 && inPreview(x, y) && !pending && !invalid && layoutJob==null) {
            focusText(); int index = hit(x, y); long now = System.currentTimeMillis();
            if (now - lastClick < 250 && index == caret && !document.glyphs().isEmpty()) {
                caret = index; anchor = word(-1); caret = word(1);
            } else move(index, hasShiftDown());
            lastClick = now; dragging = true;dragX=x;dragY=y; updateButtons(); return true;
        }
        textFocus = false; return super.mouseClicked(x, y, button);
    }
    @Override public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if(scrollDragging&&button==0){scroll=Math.clamp((float)(y-previewY)/previewHeight*maxScroll(),0,maxScroll());return true;}
        if (dragging && button == 0) {dragX=x;dragY=y; caret = hit(x, y); updateButtons(); return true; }
        return super.mouseDragged(x, y, button, dx, dy);
    }
    @Override public boolean mouseReleased(double x, double y, int button) { dragging = false;scrollDragging=false; return super.mouseReleased(x, y, button); }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0x99000000);
        graphics.fill(left, top, left + panelWidth, top + panelHeight, 0xff25292e);
        graphics.renderOutline(left, top, panelWidth, panelHeight, 0xff707982);
        graphics.drawCenteredString(font, title, width / 2, top + 10, 0xffeeeeee);
        graphics.fill(previewX - 5, previewY - 5, previewX + previewWidth + 5, previewY + previewHeight + 5, 0xff10171b);
        graphics.renderOutline(previewX - 6, previewY - 6, previewWidth + 12, previewHeight + 12, textFocus ? 0xffa4beb0 : 0xff59636a);
        graphics.enableScissor(previewX, previewY, previewX + previewWidth, previewY + previewHeight);
        graphics.pose().pushPose(); graphics.pose().translate(previewX, previewY, 0);
        float zoom = previewWidth / logicalWidth(); graphics.pose().scale(zoom, zoom, 1); graphics.pose().translate(0,-scroll,0);
        int first= Math.max(0,(int)((scroll-layout.top())/(10*layout.scale()))),last=Math.min(layout.lines().size(),(int)((scroll+logicalHeight()-layout.top())/(10*layout.scale()))+2);
        for (int row = first; row < last; row++) {
            var line = layout.lines().get(row);
            int selectionStart=Math.max(start(),line.start()),selectionEnd=Math.min(end(),line.end());
            if(selectionStart<selectionEnd)fillTextRect(graphics,layout.x(line,selectionStart),layout.y(row),
                layout.x(line,selectionEnd)-layout.x(line,selectionStart),10*layout.scale(),0xff375871);
            graphics.pose().pushPose(); graphics.pose().translate(layout.drawX(line), layout.drawY(row), 0); graphics.pose().scale(layout.scale(), layout.scale(), 1);
            graphics.drawString(font,layout.sequence(line),0,0,0xffffffff,false);
            graphics.pose().popPose();
        }
        if (textFocus && !pending && System.currentTimeMillis() / 500 % 2 == 0) {
            int row = layout.rowFor(caret); float x = layout.x(layout.lines().get(row), caret), y = layout.y(row);
            fillTextRect(graphics,Math.min(x,logicalWidth()-1/zoom),y,1/zoom,Math.max(1/zoom,DisplayLayout.LINE_HEIGHT*layout.scale()),0xffe0eee5);
        }
        graphics.pose().popPose(); graphics.disableScissor();
        if(meta.kind()==2&&maxScroll()>0){
            graphics.fill(previewX+previewWidth+2,previewY,previewX+previewWidth+7,previewY+previewHeight,0xff14191d);
            int thumb=Math.max(8,(int)(previewHeight*logicalHeight()/layout.contentHeight()));
            int sy=previewY+(int)((previewHeight-thumb)*scroll/maxScroll());
            graphics.fill(previewX+previewWidth+2,sy,previewX+previewWidth+7,sy+thumb,0xffa4beb0);
        }
        graphics.drawString(font, label("size"), left + panelWidth - 89, top+panelHeight-47, 0xffdddddd, false);
        Component hint = layoutJob!=null?label("layout_pending"):status.getString().isEmpty() ? Component.translatable("screen.ageofexpansion.display."+(document.glyphs().size()>meta.capacity()?"over_capacity":"capacity"), document.glyphs().size(),meta.capacity()) : status;
        if(meta.kind()==2)graphics.drawString(font,Component.literal(meta.width()+"×"+meta.height()),left+panelWidth-47,top+panelHeight-32,0xffb9c9bf,false);
        graphics.drawString(font, font.plainSubstrByWidth(hint.getString(), panelWidth - (meta.kind()==2?65:20)), left + 10, top + panelHeight - 32, 0xffb9c9bf, false);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    private void save() {
        if (!done.active) return; pending = true; status = label("saving"); updateButtons();
        DisplayNetwork.sendClient(new DisplayNetwork.Packet(1, token, document,meta));
    }
    // 选区与光标在文字坐标系中保留小数，缩放后才由渲染器栅格化。
    private static void fillTextRect(GuiGraphics graphics,float x,float y,float width,float height,int color){
        graphics.pose().pushPose();graphics.pose().translate(x,y,0);graphics.pose().scale(width,height,1);
        graphics.fill(0,0,1,1,color);graphics.pose().popPose();
    }
    public void acknowledge(DisplayNetwork.Packet packet) {
        if (!token.equals(packet.token())) return;
        if (packet.action() == 2) { finished = true; minecraft.setScreen(null); }
        else if (packet.action() == 4) { pending = false; invalid = true; status = label("conflict"); updateButtons(); }
    }
    @Override public void onClose() {
        if(pending)return;
        if (!finished && minecraft.getConnection() != null) DisplayNetwork.sendClient(new DisplayNetwork.Packet(3, token, DisplayDocument.EMPTY));
        finished = true; minecraft.setScreen(null);
    }
    @Override public boolean isPauseScreen() { return false; }
    @Override public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}
}



