package com.tajlpawa.ageofexpansion.image.client;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

// Pixel atlas authored in Blockbench; corners stay crisp when panels resize.
public final class ImageSkin {
    public static final ResourceLocation ATLAS=ResourceLocation.parse("ageofexpansion:textures/gui/image_editor.png");
    private static void part(GuiGraphics g,int x,int y,int w,int h,int u,int v,int sw,int sh){if(w>0&&h>0)g.blit(ATLAS,x,y,w,h,(float)u,(float)v,sw,sh,128,128);}
    public static void box(GuiGraphics g,int x,int y,int w,int h,int state){int u=state*16;for(int row=0;row<3;row++)for(int col=0;col<3;col++){int dx=col==0?0:col==1?3:w-3,dy=row==0?0:row==1?3:h-3;part(g,x+dx,y+dy,col==1?w-6:3,row==1?h-6:3,u+(col==0?0:col==1?3:13),row==0?0:row==1?3:13,col==1?10:3,row==1?10:3);}}
    public static void icon(GuiGraphics g,int x,int y,int id){part(g,x,y,16,16,id*16,32,16,16);}
    public static void track(GuiGraphics g,int x,int y,int w){part(g,x,y,w,4,0,64,16,8);}
    public static void knob(GuiGraphics g,int x,int y){part(g,x,y,5,8,16,64,5,8);}
    public static class Action extends Button {
        public Action(Component title,int x,int y,int w,int h,Runnable action){super(x,y,w,h,title,b->action.run(),DEFAULT_NARRATION);}
        @Override public void renderWidget(GuiGraphics g,int x,int y,float delta){box(g,getX(),getY(),width,height,!active?3:isHoveredOrFocused()?1:0);renderString(g,net.minecraft.client.Minecraft.getInstance().font,active?0xffe2eee8:0xff899791);}
    }
    private ImageSkin(){}
}
