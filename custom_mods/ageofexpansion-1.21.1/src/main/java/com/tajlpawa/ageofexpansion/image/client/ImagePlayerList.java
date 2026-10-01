package com.tajlpawa.ageofexpansion.image.client;

import com.tajlpawa.ageofexpansion.image.ImageLibrary;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;

// 分页结果共用一个列表控件；选中状态与搜索输入相互独立。
public final class ImagePlayerList extends AbstractWidget {
    public static final int ROW_HEIGHT=22;
    private final List<ImageLibrary.Owner> entries;
    private final Consumer<ImageLibrary.Owner> select,apply;
    private UUID selected,lastClicked;
    private long lastClick;
    public ImagePlayerList(int x,int y,int width,int rows,List<ImageLibrary.Owner> entries,UUID selected,Consumer<ImageLibrary.Owner> select,Consumer<ImageLibrary.Owner> apply){
        super(x,y,width,rows*ROW_HEIGHT,ImageScreen.text("player_list"));this.entries=List.copyOf(entries);this.selected=selected;this.select=select;this.apply=apply;
    }
    public UUID selectedId(){return selected;}
    @Override protected void renderWidget(GuiGraphics g,int mouseX,int mouseY,float delta){
        var font=Minecraft.getInstance().font;int x=getX(),y=getY();g.fill(x,y,x+width,y+height,0xff242c30);g.renderOutline(x,y,width,height,0xff556964);
        for(int i=0;i<entries.size()&&i*ROW_HEIGHT<height;i++){
            var entry=entries.get(i);int rowY=y+i*ROW_HEIGHT;boolean chosen=entry.id().equals(selected),hover=active&&mouseX>=x&&mouseX<x+width&&mouseY>=rowY&&mouseY<rowY+ROW_HEIGHT;
            if(chosen||hover)g.fill(x+1,rowY+1,x+width-1,rowY+ROW_HEIGHT-1,chosen?0xff354e47:0xff303d3a);
            if(chosen)g.fill(x+1,rowY+1,x+3,rowY+ROW_HEIGHT-1,0xff8fd1b8);
            String count=Integer.toString(entry.count());int textColor=active?0xffdfede7:0xff87948f;
            g.drawString(font,font.plainSubstrByWidth(entry.name(),width-font.width(count)-24),x+8,rowY+7,textColor,false);
            g.drawString(font,count,x+width-font.width(count)-8,rowY+7,active?0xff92bcae:0xff87948f,false);
            if(i+1<entries.size())g.fill(x+8,rowY+ROW_HEIGHT-1,x+width-8,rowY+ROW_HEIGHT,0xff38443f);
        }
        if(isFocused())g.renderOutline(x,y,width,height,0xff8fd1b8);
    }
    @Override public boolean mouseClicked(double x,double y,int button){
        if(button!=0||!active||!visible||!isMouseOver(x,y))return false;
        int index=(int)(y-getY())/ROW_HEIGHT;if(index>=entries.size())return true;
        var entry=entries.get(index);long now=System.currentTimeMillis();boolean twice=entry.id().equals(lastClicked)&&now-lastClick<300;
        selected=entry.id();lastClicked=entry.id();lastClick=now;select.accept(entry);
        if(twice){lastClick=0;apply.accept(entry);}return true;
    }
    @Override public boolean keyPressed(int key,int scan,int mods){
        if(!active||entries.isEmpty())return false;int index=-1;for(int i=0;i<entries.size();i++)if(entries.get(i).id().equals(selected))index=i;
        if(key==264||key==265){index=Math.clamp(index+(key==264?1:-1),0,entries.size()-1);var entry=entries.get(index);selected=entry.id();select.accept(entry);return true;}
        if((key==257||key==335)&&selected!=null){apply.accept(entries.get(index));return true;}return false;
    }
    @Override protected void updateWidgetNarration(NarrationElementOutput output){
        output.add(NarratedElementType.TITLE,getMessage());for(var entry:entries)if(entry.id().equals(selected))output.add(NarratedElementType.POSITION,entry.name()+", "+entry.count());
        output.add(NarratedElementType.USAGE,ImageScreen.text("player_list_hint"));
    }
}
