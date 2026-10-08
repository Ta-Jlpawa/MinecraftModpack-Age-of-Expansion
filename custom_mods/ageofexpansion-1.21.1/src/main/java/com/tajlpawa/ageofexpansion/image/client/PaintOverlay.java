package com.tajlpawa.ageofexpansion.image.client;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;

// Outline cache is bounded by the viewport, not the canvas dimensions.
public final class PaintOverlay {
    private record Span(int x,int y,int length,boolean vertical){}
    private final List<Span> spans=new ArrayList<>();private long revision=Long.MIN_VALUE;private float oldScale,oldX,oldY;private int oldSide,oldDx,oldDy;
    public void invalidate(){revision=Long.MIN_VALUE;}
    public void render(GuiGraphics g,PaintCanvas canvas,BitSet mask,int px,int py,int side,float scale,float tx,float ty,int dx,int dy){if(mask==null)return;
        if(revision!=canvas.selectionRevision()||oldSide!=side||scale!=oldScale||tx!=oldX||ty!=oldY||oldDx!=dx||oldDy!=dy){revision=canvas.selectionRevision();oldSide=side;oldScale=scale;oldX=tx;oldY=ty;oldDx=dx;oldDy=dy;spans.clear();boolean[][] grid=new boolean[side+2][side+2];int w=canvas.image.getWidth(),h=canvas.image.getHeight();for(int y=-1;y<=side;y++)for(int x=-1;x<=side;x++){int targetX=(int)Math.floor((px+x+.5-tx)/scale),targetY=(int)Math.floor((py+y+.5-ty)/scale);long ix=(long)targetX-dx,iy=(long)targetY-dy;grid[y+1][x+1]=targetX>=0&&targetY>=0&&targetX<w&&targetY<h&&ix>=0&&iy>=0&&ix<w&&iy<h&&mask.get((int)iy*w+(int)ix);}
            for(int y=0;y<=side;y++){int start=-1;for(int x=0;x<=side;x++){boolean edge=x<side&&grid[y][x+1]!=grid[y+1][x+1];if(edge&&start<0)start=x;if(!edge&&start>=0){spans.add(new Span(px+start,py+y,x-start,false));start=-1;}}}
            for(int x=0;x<=side;x++){int start=-1;for(int y=0;y<=side;y++){boolean edge=y<side&&grid[y+1][x]!=grid[y+1][x+1];if(edge&&start<0)start=y;if(!edge&&start>=0){spans.add(new Span(px+x,py+start,y-start,true));start=-1;}}}}
        int phase=(int)(System.currentTimeMillis()/170)%8;for(var s:spans)for(int n=0;n<s.length;){int run=Math.min(s.length-n,4-Math.floorMod((s.vertical?s.y:s.x)+n+phase,4));int color=(Math.floorDiv((s.vertical?s.y:s.x)+n+phase,4)&1)==0?0xffedfff5:0xff142922;g.fill(s.x+(s.vertical?0:n),s.y+(s.vertical?n:0),s.x+(s.vertical?1:n+run),s.y+(s.vertical?n+run:1),color);n+=run;}
    }
    public static void line(GuiGraphics g,int x0,int y0,int x1,int y1,int color){int dx=Math.abs(x1-x0),sx=x0<x1?1:-1,dy=-Math.abs(y1-y0),sy=y0<y1?1:-1,e=dx+dy;while(true){g.fill(x0,y0,x0+1,y0+1,color);if(x0==x1&&y0==y1)break;int ee=e*2;if(ee>=dy){e+=dy;x0+=sx;}if(ee<=dx){e+=dx;y0+=sy;}}}
}
