package com.tajlpawa.ageofexpansion.image.client;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
public final class ImageSlider extends AbstractSliderButton {
    private long lastClick;
    private final int min,max;private final String suffix;private final IntConsumer change;
    public ImageSlider(int x,int y,int width,int min,int max,int current,String suffix,IntConsumer change){super(x,y,width,18,Component.empty(),min==max?0:(current-min)/(double)(max-min));this.min=min;this.max=max;this.suffix=suffix;this.change=change;setTooltip(net.minecraft.client.gui.components.Tooltip.create(ImageScreen.text("slider_hint")));updateMessage();}
    public int intValue(){return min+(int)Math.round(value*(max-min));}
    public void setIntValue(int n){value=min==max?0:(Math.clamp(n,min,max)-min)/(double)(max-min);updateMessage();applyValue();}
    @Override public boolean mouseClicked(double x,double y,int button){
        if(button==0&&active&&visible&&isMouseOver(x,y)){
            long now=System.currentTimeMillis();if(now-lastClick<300){lastClick=0;var mc=net.minecraft.client.Minecraft.getInstance();mc.setScreen(new ImageDialogs.NumberInput(mc.screen,min,max,intValue(),this::setIntValue));return true;}lastClick=now;
        }return super.mouseClicked(x,y,button);
    }
    @Override public void renderWidget(net.minecraft.client.gui.GuiGraphics g,int mouseX,int mouseY,float delta){
        var font=net.minecraft.client.Minecraft.getInstance().font;int x=getX(),y=getY(),knob=x+4+(int)Math.round(value*(getWidth()-8));
        ImageSkin.box(g,x,y,getWidth(),18,active?0:3);g.drawCenteredString(font,getMessage(),x+getWidth()/2,y+1,active?0xffe5f1ed:0xff7c8987);
        ImageSkin.track(g,x+4,y+12,getWidth()-8);g.fill(x+4,y+13,knob,y+15,active?0xff69bea2:0xff53615f);
        ImageSkin.knob(g,knob-2,y+10);
        if(isHoveredOrFocused())g.renderOutline(x,y,getWidth(),18,0xff83b6a5);
    }
    @Override protected void updateMessage(){setMessage(Component.literal(intValue()+suffix));}
    @Override protected void applyValue(){change.accept(intValue());}
}
