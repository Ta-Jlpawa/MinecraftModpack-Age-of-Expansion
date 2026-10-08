package com.tajlpawa.ageofexpansion.gametest;
import com.tajlpawa.ageofexpansion.image.client.*;
import java.nio.file.Path;
import java.util.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import org.lwjgl.glfw.GLFW;

public final class PaintClientSmoke {
    private static ImageScreen editor;private static int step;private static long revision;private static int unionCount,cropPixel,cropUndo;private static int[] cropBefore;private static BitSet cropMask;
    private static Object get(String key)throws Exception{var f=ImageScreen.class.getDeclaredField(key);f.setAccessible(true);return f.get(editor);}
    private static void set(String key,Object value)throws Exception{var f=ImageScreen.class.getDeclaredField(key);f.setAccessible(true);f.set(editor,value);}
    private static void call(String key)throws Exception{var m=ImageScreen.class.getDeclaredMethod(key);m.setAccessible(true);m.invoke(editor);}
    private static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    private static double x(double pixel)throws Exception{double scale=(int)get("side")/32.0*(float)get("viewZoom");return (int)get("px")+((int)get("side")-32*scale)/2+(float)get("panX")+scale*pixel;}
    private static double y(double pixel)throws Exception{double scale=(int)get("side")/32.0*(float)get("viewZoom");return (int)get("py")+((int)get("side")-32*scale)/2+(float)get("panY")+scale*pixel;}
    private static void tool(int index)throws Exception{var p=(PaintToolPalette)get("palette");editor.mouseClicked(p.getX()+index%2*(p.getWidth()/2)+5,p.getY()+index/2*24+10,0);editor.mouseReleased(p.getX(),p.getY(),0);}
    private static void rect(int x0,int y0,int x1,int y1)throws Exception{editor.mouseClicked(x(x0+.2),y(y0+.2),0);editor.mouseDragged(x(x1+.2),y(y1+.2),0,0,0);editor.mouseReleased(x(x1+.2),y(y1+.2),0);}
    private static void leaf()throws Exception{editor.mouseClicked(x(0),y(0),0);editor.mouseDragged(x(14),y(0),0,0,0);editor.mouseDragged(x(8),y(14),0,0,0);editor.mouseReleased(x(8),y(14),0);}
    private static void render(GuiGraphics g,String file)throws Exception{var mc=Minecraft.getInstance();com.mojang.blaze3d.systems.RenderSystem.clear(256,Minecraft.ON_OSX);mc.screen.render(g,0,0,0);g.flush();try(var shot=Screenshot.takeScreenshot(mc.getMainRenderTarget())){shot.writeToFile(Path.of(file));}}
    private static int menuField(PaintToolPalette p,String name)throws Exception{var f=PaintToolPalette.class.getDeclaredField(name);f.setAccessible(true);return f.getInt(p);}
    private static void menuRegression(GuiGraphics g)throws Exception{var p=(PaintToolPalette)get("palette");var timer=PaintToolPalette.class.getDeclaredField("hoverAt");timer.setAccessible(true);
        for(int tool=0;tool<2;tool++){p.closeMenu();int x=p.getX()+tool*(p.getWidth()/2)+5,y=p.getY()+10;p.renderWidget(g,x,y,0);timer.setLong(p,System.currentTimeMillis()-300);p.renderWidget(g,x,y,0);p.renderMenu(g,x,y,editor.width,editor.height);int mx=menuField(p,"menuX")+10,my=menuField(p,"menuY")+10;
            p.renderWidget(g,mx,my,0);timer.setLong(p,System.currentTimeMillis()-300);p.renderWidget(g,mx,my,0);p.renderMenu(g,mx,my,editor.width,editor.height);check(menuField(p,"open")==tool,"Hovered dropdown must not switch to covered selection tool");editor.mouseClicked(menuField(p,"menuX")+30,menuField(p,"menuY")+30,0);editor.mouseReleased(0,0,0);check(((PaintToolPalette.Tool)get("tool")).ordinal()==tool&&(boolean)get("erase"),"Clear action belongs to original dropdown");}
        com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.info("PAINT_MENU_OCCLUSION_PASSED brush_and_leaf");
    }
    public static boolean run(ImageScreen screen,GuiGraphics g)throws Exception{editor=screen;var c=(PaintCanvas)get("canvas");var mc=Minecraft.getInstance();if((boolean)get("busy"))return false;
        switch(step){
            case 0 -> {menuRegression(g);revision=c.imageRevision();tool(2);rect(2,2,8,8);}
            case 1 -> {check(c.selection()!=null&&c.selection().cardinality()==49,"Mouse rectangle produces exact mask");check(c.imageRevision()==revision,"Selecting does not edit image");tool(1);set("color",0xff7755);set("opacity",100);leaf();}
            case 2 -> {check(c.image.getRGB(4,4)==0xffff7755&&c.image.getRGB(10,4)==0,"Leaf fill is limited by selection");render(g,"paint-leaf-selected.png");tool(2);rect(7,7,11,11);}
            case 3 -> {check(c.selection().cardinality()==70,"New rectangle grows beyond old mask");var p=(PaintToolPalette)get("palette");int mx=p.getX()+5,my=p.getY()+34;p.renderWidget(g,mx,my,0);var f=PaintToolPalette.class.getDeclaredField("hoverAt");f.setAccessible(true);f.setLong(p,System.currentTimeMillis()-300);p.renderWidget(g,mx,my,0);p.renderMenu(g,mx,my,screen.width,screen.height);check(p.menuOpen(),"Hover opens menu");var fx=PaintToolPalette.class.getDeclaredField("menuX");var fy=PaintToolPalette.class.getDeclaredField("menuY");fx.setAccessible(true);fy.setAccessible(true);int xx=fx.getInt(p),yy=fy.getInt(p);com.mojang.blaze3d.systems.RenderSystem.clear(256,Minecraft.ON_OSX);screen.render(g,mx,my,0);g.flush();try(var shot=Screenshot.takeScreenshot(mc.getMainRenderTarget())){shot.writeToFile(Path.of("paint-mode-menu.png"));}editor.mouseClicked(xx+30,yy+29,0);editor.mouseReleased(xx+30,yy+29,0);check((boolean)get("erase")&&!p.menuOpen(),"Submenu selects clear mode without painting through");rect(7,7,11,11);}
            case 4 -> {check(c.selection().cardinality()==45&&c.image.getRGB(4,4)!=0,"Selection subtraction leaves image intact");editor.mouseClicked(x(4.2),y(4.2),1);}
            case 5 -> {check(get("moving")!=null,"Right press prepares moving pixel snapshot");editor.mouseDragged(x(10.2),y(8.2),1,0,0);render(g,"paint-move-preview.png");check(c.image.getRGB(4,4)!=0,"Move preview has not committed pixels");editor.mouseReleased(x(10.2),y(8.2),1);}
            case 6 -> {check(c.image.getRGB(4,4)==0&&c.image.getRGB(10,8)==0xffff7755,"Right drag cuts source and pastes target");render(g,"paint-move-committed.png");call("undo");}
            case 7 -> {check(c.image.getRGB(4,4)==0xffff7755&&c.selection().get(4*32+4),"GUI undo restores move and selection");((Button)get("clearSelectionButton")).onPress();}
            case 8 -> {check(!c.hasSelection()&&c.image.getRGB(4,4)!=0,"Deselect button does not erase pixels");call("undo");}
            case 9 -> {check(c.hasSelection(),"Deselect can be undone");tool(1);set("erase",true);set("opacity",50);leaf();}
            case 10 -> {check(c.image.getRGB(4,4)>>>24==127,"Leaf eraser uses configured opacity");tool(1);check(!(boolean)get("erase"),"Direct tool click always restores normal mode");unionCount=c.selection().cardinality();tool(3);editor.mouseClicked(x(15),y(15),0);editor.mouseDragged(x(23),y(15),0,0,0);editor.mouseDragged(x(23),y(23),0,0,0);editor.mouseReleased(x(23),y(23),0);}
            case 11 -> {check(c.selection().cardinality()>unionCount,"Lasso adds independent selected region");render(g,"paint-lasso-union.png");tool(1);editor.mouseClicked(x(2),y(2),0);editor.mouseDragged(x(12),y(12),0,0,0);int before=c.undoCount();editor.keyPressed(GLFW.GLFW_KEY_ESCAPE,0,0);check(!(boolean)get("tracing")&&c.undoCount()==before,"Esc cancels pending polygon");}
            case 12 -> {cropPixel=c.image.getRGB(6,4);cropBefore=c.image.getRGB(0,0,32,32,null,0,32);cropMask=c.selection();set("viewZoom",.5f);set("panX",3f);set("panY",-2f);editor.mouseClicked(x(4.2),y(4.2),1);}
            case 13 -> {check(get("moving")!=null,"Crop drag prepared");editor.mouseDragged(x(-1.8),y(4.2),1,0,0);check((int)get("moveDx")==-6,"GUI permits dragging past canvas edge");render(g,"paint-crop-preview.png");try(var shot=Screenshot.takeScreenshot(mc.getMainRenderTarget())){int gx=(int)x(-2.5),gy=(int)y(4.5);int expected=((gx-(int)get("px"))/8+(gy-(int)get("py"))/8)%2==0?0x304048:0x45535a;int value=shot.getPixelRGBA((int)((gx+.5)*mc.getWindow().getGuiScale()),(int)((gy+.5)*mc.getWindow().getGuiScale()));check(net.minecraft.util.FastColor.ABGR32.red(value)==(expected>>16&255)&&net.minecraft.util.FastColor.ABGR32.green(value)==(expected>>8&255)&&net.minecraft.util.FastColor.ABGR32.blue(value)==(expected&255),"Move preview clips to canvas, even when zoomed out inside viewport");}editor.mouseReleased(x(-1.8),y(4.2),1);}
            case 14 -> {check(c.image.getRGB(0,4)==cropPixel&&c.image.getRGB(4,4)==0,"GUI commits cropped move instead of clamping");render(g,"paint-crop-committed.png");call("undo");}
            case 15 -> {check(Arrays.equals(cropBefore,c.image.getRGB(0,0,32,32,null,0,32))&&cropMask.equals(c.selection()),"Undo restores off-canvas content and selection");cropUndo=c.undoCount();editor.mouseClicked(x(4.2),y(4.2),1);}
            case 16 -> {editor.mouseDragged(x(-80),y(-80),1,0,0);editor.keyPressed(GLFW.GLFW_KEY_ESCAPE,0,0);check(get("moving")==null&&Arrays.equals(cropBefore,c.image.getRGB(0,0,32,32,null,0,32))&&c.undoCount()==cropUndo,"Esc cancels fully outside preview without deletion");set("viewZoom",1f);set("panX",0f);set("panY",0f);com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.info("PAINT_CROP_GUI_PASSED zoom_pan_preview_commit_undo_cancel");}
            case 17 -> {if(c.undoCount()>0){call("undo");return false;}check(!c.hasSelection()&&c.image.getRGB(4,4)==0&&c.image.getRGB(16,16)==0,"All gestures restore original canvas through undo");call("refreshMask");com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.info("PAINT_SELECTION_GUI_PASSED rectangle_union_subtract leaf_opacity right_move_preview_commit undo_menu_escape");return true;}
        }step++;return false;
    }
}
