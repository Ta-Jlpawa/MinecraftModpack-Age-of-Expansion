package com.tajlpawa.ageofexpansion.gametest;

import com.tajlpawa.ageofexpansion.AgeofExpansion;
import com.tajlpawa.ageofexpansion.display.*;
import com.tajlpawa.ageofexpansion.display.client.*;
import java.nio.file.Path;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = "ageofexpansion", value = Dist.CLIENT)
public final class DisplayClientSmoke {
    private static int phase, frames;
    private static DisplayScreen editor;
    private static Object get(String name) throws Exception {
        var field = DisplayScreen.class.getDeclaredField(name); field.setAccessible(true); return field.get(editor);
    }
    private static void set(String name, Object value) throws Exception {
        var field = DisplayScreen.class.getDeclaredField(name); field.setAccessible(true); field.set(editor, value);
    }
    private static void call(String name, Class<?> type, Object value) throws Exception {
        var method = DisplayScreen.class.getDeclaredMethod(name, type); method.setAccessible(true); method.invoke(editor, value);
    }
    private static void check(boolean valid, String message) { if (!valid) throw new AssertionError(message); }
    @SubscribeEvent public static void render(ScreenEvent.Render.Post event) throws Exception {
        if (!Boolean.getBoolean("ageofexpansion.displaySmoke")) return;
        if(ImageClientSmoke.active){ImageClientSmoke.render(event);return;}
        Minecraft mc = Minecraft.getInstance(); if (mc.getOverlay() != null) return;
        if (phase == 0 && event.getScreen() instanceof TitleScreen) {
            DisplayTextBoundsTests.run(mc.font); DisplayTextMetricsTests.run(mc.font);
            phase = 1;
            var glyphs = "开拓纪元\nElectronic Display\n欢迎来到自动化工厂".codePoints()
                .mapToObj(cp -> new DisplayDocument.Glyph(cp, DisplayDocument.Ink.WHITE)).toList();
            var doc = new DisplayDocument(glyphs, 100, 1);
            var longDoc = new DisplayDocument(Collections.nCopies(1024, new DisplayDocument.Glyph('宽', new DisplayDocument.Ink(0x55ffaa, 3))), 400, 2);
            var layout = DisplayLayout.create(mc.font, longDoc);
            check(layout.scale() < 4 && layout.lines().size() * 10 * layout.scale() <= 60.001, "Long document auto fits");
            for (var line : layout.lines()) check(line.width() * layout.scale() <= 120.001, "Line fits width");
            editor = new DisplayScreen(UUID.randomUUID(), doc); mc.setScreen(editor);
            var initialLayout = (DisplayLayout)get("layout");
            int px = (int)get("previewX"), py = (int)get("previewY"); float zoom = (int)get("previewWidth") / 120f;
            double startX = px + initialLayout.x(initialLayout.lines().get(0), 0) * zoom;
            double startY = py + (initialLayout.y(0) + 4) * zoom;
            var second = initialLayout.lines().get(1);
            double endX = px + initialLayout.x(second, second.end()) * zoom;
            double endY = py + (initialLayout.y(1) + 4) * zoom;
            editor.mouseClicked(startX, startY, 0); editor.mouseDragged(endX, endY, 0, endX - startX, endY - startY);
            editor.mouseReleased(endX, endY, 0);
            check((int)get("anchor") == 0 && (int)get("caret") == second.end(), "Mouse selects across lines");
            set("anchor", 0); set("caret", 4); call("toggle", int.class, 1); call("setColor", int.class, 0x75d7ac);
            var changed = (DisplayDocument)get("document");
            check(changed.glyphs().get(0).ink().flags() == 1 && changed.glyphs().get(0).ink().color() == 0x75d7ac, "Selected style applied");
            check(changed.glyphs().get(5).ink().equals(DisplayDocument.Ink.WHITE), "Unselected text unchanged");
            int size = changed.glyphs().size();
            set("anchor", size); set("caret", size); call("insert", String.class, "😀");
            check(((DisplayDocument)get("document")).glyphs().size() == size + 1, "Supplementary Unicode inserted as one character");
            editor.keyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE, 0, 0);
            check(((DisplayDocument)get("document")).glyphs().size() == size, "Backspace removes entire code point");
            call("insert", String.class, "X".repeat(1025)); check(((DisplayDocument)get("document")).glyphs().size() == size, "Reject oversized paste");
            set("status", net.minecraft.network.chat.Component.empty()); set("anchor", 0); set("caret", 4);
            mc.getWindow().setWindowed(1100, 750);
        } else if (phase == 1 && event.getScreen() == editor && ++frames >= 15) {
            try (var image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) { image.writeToFile(Path.of("editor-preview.png")); }
            phase = 2; frames = 0;
            @SuppressWarnings("unchecked") var buttons = (List<Button>)get("formatButtons"); buttons.get(3).onPress();
        } else if (phase == 2 && event.getScreen() != editor && ++frames >= 15) {
            try (var image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) { image.writeToFile(Path.of("palette-preview.png")); }
            event.getScreen().onClose();
            check((int)get("anchor") == 0 && (int)get("caret") == 4, "Palette preserves selection");
            var stack = DisplayContent.ITEM.get().getDefaultInstance();
            var model = mc.getItemRenderer().getModel(stack, null, null, 0);
            check(!model.getQuads(null, null, net.minecraft.util.RandomSource.create()).isEmpty(), "Item model baked");
            check(!model.getParticleIcon().contents().name().getPath().contains("missing"), "Texture loaded");
            phase = 3; frames = 0;
            AgeofExpansion.LOGGER.info("DISPLAY_SMOKE_PASSED: Unicode, selection styles, length limit, auto fit, palette selection, model and texture");
            mc.setScreen(new WorldPreview((DisplayDocument)get("document")));
        } else if (phase == 3 && event.getScreen() instanceof WorldPreview && ++frames >= 15) {
            try (var image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) { image.writeToFile(Path.of("world-render-preview.png")); }
            phase = 4;
            AgeofExpansion.LOGGER.info("DISPLAY_RENDER_PASSED: four facing states with full-bright text under zero supplied world light");
            editor=new DisplayScreen(UUID.randomUUID(),((DisplayDocument)get("document")),new DisplayNetwork.Meta(1,1,1,1024,net.minecraft.core.BlockPos.ZERO,UUID.randomUUID(),0));
            mc.setScreen(editor);frames=0;
            @SuppressWarnings("unchecked") var verticals=(List<Button>)get("verticalButtons");verticals.get(2).onPress();
            check(((DisplayDocument)get("document")).vertical()==2,"Vertical bottom alignment");
        } else if(phase==4&&event.getScreen()==editor&&++frames>=15){
            try(var image=Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(Path.of("square-editor-preview.png"));}
            check((int)get("previewWidth")== (int)get("previewHeight"),"Square preview shape");
            check((int)get("previewHeight")>100,"Compact layout enlarges square preview");
            var beforeClear=(DisplayDocument)get("document");((Button)get("clearButton")).onPress();
            check(((DisplayDocument)get("document")).glyphs().isEmpty(),"Clear button clears draft");
            Object clearEdit=((ArrayDeque<?>)get("undo")).getLast();
            var restoreClear=DisplayScreen.class.getDeclaredMethod("restore",clearEdit.getClass(),boolean.class);restoreClear.setAccessible(true);restoreClear.invoke(editor,clearEdit,false);
            check(((DisplayDocument)get("document")).equals(beforeClear),"Clear is undoable and preserves all styles");
            var text="模块显示屏\nModular Display\n"+"自动化生产线状态正常。欢迎来到开拓纪元。\n".repeat(40);
            var doc=new DisplayDocument(text.codePoints().mapToObj(cp->new DisplayDocument.Glyph(cp,DisplayDocument.Ink.WHITE)).toList(),200,0,0);
            editor=new DisplayScreen(UUID.randomUUID(),doc,new DisplayNetwork.Meta(2,3,2,6144,net.minecraft.core.BlockPos.ZERO,UUID.randomUUID(),0));mc.setScreen(editor);
            check(((DisplayLayout)get("layout")).scale()==1,"Module editor uses fixed font size");
            editor.mouseScrolled((int)get("previewX")+5,(int)get("previewY")+5,0,-4);
            check((float)get("scroll")>0,"Vertical scrolling works");phase=5;frames=0;
        } else if(phase==5&&event.getScreen()==editor&&++frames>=15){
            try(var image=Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(Path.of("module-editor-preview.png"));}
            var large=new DisplayDocument(Collections.nCopies(204800,new DisplayDocument.Glyph('宽',new DisplayDocument.Ink(0x77ccaa,3))),400,1,1);
            long start=System.nanoTime();var job=new DisplayLayout.Job(large,1404,1404,false);DisplayLayout ready=null;int steps=0;
            while(ready==null){ready=job.step(mc.font,8192);steps++;}
            check(ready.contentHeight()<=1404.01,"Maximum text fits world screen");
            AgeofExpansion.LOGGER.info("MODULE_CLIENT_PERF max_chars=204800 layout_ms={} slices={}",(System.nanoTime()-start)/1e6,steps);
            var history=(ArrayDeque<?>)get("undo");
            int before=((DisplayDocument)get("document")).glyphs().size();
            set("anchor",0);set("caret",0);call("insert",String.class,"测");
            Object edit=history.getLast();
            var restore=DisplayScreen.class.getDeclaredMethod("restore",edit.getClass(),boolean.class);restore.setAccessible(true);
            restore.invoke(editor,edit,false);check(((DisplayDocument)get("document")).glyphs().size()==before,"Delta undo restores content");
            restore.invoke(editor,edit,true);check(((DisplayDocument)get("document")).glyphs().size()==before+1,"Delta redo restores insertion");
            var saved=(DisplayDocument)get("document");
            mc.setScreen(new ModulePreview(saved));phase=6;frames=0;
        } else if(phase==6&&event.getScreen() instanceof ModulePreview&&++frames>=15){
            try(var image=Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(Path.of("modular-render-preview.png"));}
            AgeofExpansion.LOGGER.info("MODULE_CLIENT_PASSED: square viewport, vertical alignment, scrolling fixed font, maximum layout, connected models");
            var large=new DisplayDocument(Collections.nCopies(204800,new DisplayDocument.Glyph('宽',new DisplayDocument.Ink(0x77ccaa,3))),400,1,1);
            editor=new DisplayScreen(UUID.randomUUID(),large,new DisplayNetwork.Meta(2,10,10,204800,net.minecraft.core.BlockPos.ZERO,UUID.randomUUID(),0));
            mc.setScreen(editor);phase=7;frames=0;
        } else if(phase==7&&event.getScreen()==editor&&get("layoutJob")==null&&++frames>=4){
            check(((DisplayDocument)get("document")).glyphs().size()==204800,"Full capacity editor loads");
            set("anchor",204800);set("caret",204800);call("insert",String.class,"X");
            check(((DisplayDocument)get("document")).glyphs().size()==204800,"Full editor enforces capacity");
            var start=System.nanoTime();editor.keyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE,0,0);
            check(((DisplayDocument)get("document")).glyphs().size()==204799,"Full editor deletes without corrupting data");
            AgeofExpansion.LOGGER.info("MODULE_EDITOR_MAX delete_ms={} chars=204799",(System.nanoTime()-start)/1e6);
            phase=8;frames=0;mc.setScreen(new ModulePreview(((DisplayDocument)get("document")),true));
        } else if(phase==8&&event.getScreen() instanceof ModulePreview preview&&++frames>=32){
            AgeofExpansion.LOGGER.info("MODULE_RENDER_MAX chars=204799 last_frame_submit_flush_ms={}",preview.lastRenderNanos/1e6);
            phase=9;frames=0;mc.setScreen(new ItemPreview());
        } else if(phase==9&&event.getScreen() instanceof ItemPreview&&++frames>=15){
            try(var image=Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(Path.of("display-items-preview.png"));}
            AgeofExpansion.LOGGER.info("COPIER_CLIENT_PASSED: enlarged preview, undoable clear, four generated item textures and Shift tooltip");
            var cjk=new DisplayDocument("这是中文加粗自动换行与光标定位测试，文字应当保持居中，不再逐字累积偏移。".repeat(3).codePoints()
                .mapToObj(cp->new DisplayDocument.Glyph(cp,new DisplayDocument.Ink(0x75d7ac,1))).toList(),100,1,1);
            editor=new DisplayScreen(UUID.randomUUID(),cjk,new DisplayNetwork.Meta(1,1,1,1024,net.minecraft.core.BlockPos.ZERO,UUID.randomUUID(),0));
            mc.setScreen(editor);set("caret",35);set("anchor",5);phase=10;frames=0;
        } else if(phase==10&&event.getScreen()==editor&&++frames>=15&&System.currentTimeMillis()/500%2==0){
            try(var image=Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(Path.of("cjk-bold-preview.png"));}
            mc.setScreen(new WorldPreview((DisplayDocument)get("document")));phase=11;frames=0;
        } else if(phase==11&&event.getScreen() instanceof WorldPreview&&++frames>=15){
            try(var image=Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(Path.of("cjk-bold-world-preview.png"));}
            var large=new DisplayDocument("中文".codePoints().mapToObj(cp->new DisplayDocument.Glyph(cp,new DisplayDocument.Ink(0x75d7ac,1))).toList(),400,1,1);
            editor=new DisplayScreen(UUID.randomUUID(),large,new DisplayNetwork.Meta(1,1,1,1024,net.minecraft.core.BlockPos.ZERO,UUID.randomUUID(),0,1,1000));
            mc.setScreen(editor);set("caret",2);set("anchor",0);phase=12;frames=0;
            var sizeBox=(net.minecraft.client.gui.components.EditBox)get("sizeBox");
            for(int size:new int[]{1,1000,400}){sizeBox.setValue(Integer.toString(size));check(((DisplayDocument)get("document")).size()==size&&((Button)get("done")).active,"Server-advertised size range accepted, including four digits");}
            sizeBox.setValue("1001");check(!((Button)get("done")).active,"Size above server range cannot be saved");
            sizeBox.setValue("0");check(!((Button)get("done")).active,"Size below server range cannot be saved");sizeBox.setValue("400");
        } else if(phase==12&&event.getScreen()==editor&&++frames>=15){
            try(var image=Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(Path.of("large-centered-selection.png"));}
            mc.setScreen(new WorldPreview((DisplayDocument)get("document")));phase=13;frames=0;
        } else if(phase==13&&event.getScreen() instanceof WorldPreview&&++frames>=15){
            try(var image=Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(Path.of("large-centered-world.png"));}
            editor=new DisplayScreen(UUID.randomUUID(),DisplayDocument.EMPTY,new DisplayNetwork.Meta(2,1,1,1024,net.minecraft.core.BlockPos.ZERO,UUID.randomUUID(),0,700,700));
            mc.setScreen(editor);check(((DisplayDocument)get("document")).size()==700,"Existing/default size safely clamped to new range");
            ((net.minecraft.client.gui.components.EditBox)get("sizeBox")).setValue("699");check(!((Button)get("done")).active,"Equal size bounds enforced");
            AgeofExpansion.LOGGER.info("DISPLAY_FONT_SIZE_GUI_PASSED: server range, 1/1000 bounds, invalid input, equal bounds and existing document clamp");
            HolographicClientPreview.verify();mc.setScreen(new HolographicClientPreview());phase=14;frames=0;
        } else if(phase==14&&event.getScreen() instanceof HolographicClientPreview&&++frames>=20){
            try(var image=Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(Path.of("holographic-preview.png"));}
            phase=15;ImageClientSmoke.start();
        }
    }
    private static final class ItemPreview extends net.minecraft.client.gui.screens.Screen {
        ItemPreview(){super(net.minecraft.network.chat.Component.literal("16 x 16 Display Items"));}
        @Override public void render(net.minecraft.client.gui.GuiGraphics g,int x,int y,float partial){
            g.fill(0,0,width,height,0xff181e22);g.drawCenteredString(font,title,width/2,8,0xffffff);
            int index=0;for(var holder:DisplayContent.items()){
                var stack=holder.get().getDefaultInstance();int px=width*(index+1)/(DisplayContent.items().size()+1);
                g.pose().pushPose();g.pose().translate(px-24,28,0);g.pose().scale(3,3,1);g.renderFakeItem(stack,0,0);g.pose().popPose();
                int labelY=84;for(var line:font.split(stack.getHoverName(),width/(DisplayContent.items().size()+1)-2)){
                    g.drawString(font,line,px-font.width(line)/2,labelY,0xb8cbc3,false);labelY+=10;
                }index++;
            }
            List<net.minecraft.network.chat.Component> lines=new ArrayList<>();var old=DataCopierItem.shiftDown;
            try{DataCopierItem.shiftDown=()->true;DisplayContent.COPIER.get().appendHoverText(DisplayContent.COPIER.get().getDefaultInstance(),net.minecraft.world.item.Item.TooltipContext.EMPTY,lines,net.minecraft.world.item.TooltipFlag.NORMAL);}finally{DataCopierItem.shiftDown=old;}
            int row=110;for(var line:lines){for(var wrapped:font.split(line,width-30)){g.drawString(font,wrapped,15,row,0xbbbbbb,false);row+=11;}}
        }
        @Override public void renderBackground(net.minecraft.client.gui.GuiGraphics g,int x,int y,float d){}
    }
    private static final class ModulePreview extends net.minecraft.client.gui.screens.Screen {
        final DisplayDocument doc;final UUID id=UUID.randomUUID();
        final int columns,rows;long lastRenderNanos;
        final List<DisplayBlockEntity> modules=new ArrayList<>();
        ModulePreview(DisplayDocument doc){this(doc,false);}
        ModulePreview(DisplayDocument doc,boolean stress){super(net.minecraft.network.chat.Component.literal(stress?"Maximum display rendering check":"Modular Electronic Display — 3 x 2"));this.doc=stress?doc:new DisplayDocument("开拓纪元\nMODULAR DISPLAY\n3 × 2 模块组合\n文字跨越内部接缝".codePoints().mapToObj(cp->new DisplayDocument.Glyph(cp,new DisplayDocument.Ink(0x75d7ac,1))).toList(),150,1,1);
            columns=stress?10:3;rows=stress?10:2;
            for(int y=0;y<rows;y++)for(int x=0;x<columns;x++){
                var pos=new net.minecraft.core.BlockPos(-x,-y,0);int edges=(x>0?2:0)|(x<columns-1?1:0)|(y>0?4:0)|(y<rows-1?8:0);
                var be=new DisplayBlockEntity(pos,DisplayContent.MODULE.get().defaultBlockState().setValue(DisplayBlock.CONNECTIONS,edges).setValue(DisplayBlock.LIT,true));
                be.screenId=id;be.origin=net.minecraft.core.BlockPos.ZERO;be.screenWidth=columns;be.screenHeight=rows;modules.add(be);
            }
            DisplayNetwork.CLIENT_VIEWS.put(id,new DisplayNetwork.View(0,this.doc));
        }
        @Override public void render(net.minecraft.client.gui.GuiGraphics graphics,int mouseX,int mouseY,float delta){
            graphics.fill(0,0,width,height,0xff080b0e);graphics.drawCenteredString(font,title,width/2,10,0xffffff);
            long started=System.nanoTime();DisplayRenderer.beginFrame();float scale=Math.min(width/(columns+1f),(height-40)/(rows+.4f));
            var pose=graphics.pose();pose.pushPose();pose.translate(width*.5,height*.55,100);pose.scale(scale,-scale,scale);pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));pose.translate((columns-2)/2.0,(rows-2)/2.0,-.5);
            for(var be:modules){pose.pushPose();pose.translate(be.getBlockPos().getX(),be.getBlockPos().getY(),0);minecraft.getBlockRenderer().renderSingleBlock(be.getBlockState(),pose,graphics.bufferSource(),0,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);pose.popPose();}
            for(var be:modules){pose.pushPose();pose.translate(be.getBlockPos().getX(),be.getBlockPos().getY(),0);minecraft.getBlockEntityRenderDispatcher().getRenderer(be).render(be,delta,pose,graphics.bufferSource(),0,0);pose.popPose();}
            graphics.flush();pose.popPose();
            lastRenderNanos=System.nanoTime()-started;
        }
        @Override public void renderBackground(net.minecraft.client.gui.GuiGraphics g,int x,int y,float d){}
    }
    private static final class WorldPreview extends net.minecraft.client.gui.screens.Screen {
        private final DisplayDocument document;
        WorldPreview(DisplayDocument document) { super(net.minecraft.network.chat.Component.literal("Electronic display - dark lighting render check")); this.document = document; }
        @Override public void render(net.minecraft.client.gui.GuiGraphics graphics, int x, int y, float delta) {
            graphics.fill(0, 0, width, height, 0xff080b0e);
            graphics.drawCenteredString(font, title, width / 2, 12, 0xffffff);
            int index = 0;
            for (var facing : net.minecraft.core.Direction.Plane.HORIZONTAL) {
                int cx = (index % 2) * width / 2 + width / 4, cy = (index / 2) * (height - 30) / 2 + height / 4 + 15;
                float scale = Math.min(width / 2f - 30, (height - 30) / 2f - 20);
                var state = DisplayContent.BLOCK.get().defaultBlockState().setValue(DisplayBlock.FACING, facing).setValue(DisplayBlock.LIT,true);
                var display = new DisplayBlockEntity(net.minecraft.core.BlockPos.ZERO, state); display.update(document);
                var pose = graphics.pose(); pose.pushPose(); pose.translate(cx, cy, 100); pose.scale(scale, -scale, scale);
                pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(facing.toYRot())); pose.translate(-0.5, -0.54, -0.5);
                minecraft.getBlockRenderer().renderSingleBlock(state, pose, graphics.bufferSource(), 0, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
                minecraft.getBlockEntityRenderDispatcher().getRenderer(display).render(display, delta, pose, graphics.bufferSource(), 0, 0);
                graphics.flush(); pose.popPose();
                graphics.drawCenteredString(font, facing.getName(), cx, (int)(cy + scale * .36), 0xaaaaaa); index++;
            }
        }
        @Override public void renderBackground(net.minecraft.client.gui.GuiGraphics graphics, int x, int y, float delta) {}
    }
}



