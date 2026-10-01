package com.tajlpawa.ageofexpansion.image.client;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import java.util.List;

// 子页面只接收自己的输入，主界面作为不可交互的底层保留。
public final class ImagePanelScreen extends Screen {
    public final ImageScreen owner;
    public ImagePanelScreen(ImageScreen owner){super(owner.getTitle());this.owner=owner;}
    @Override protected void init(){owner.init(minecraft,width,height);}
    @Override public List<? extends GuiEventListener> children(){return owner.children();}
    @Override public void render(GuiGraphics g,int x,int y,float delta){owner.render(g,x,y,delta);}
    @Override public void tick(){owner.tick();}
    @Override public boolean mouseClicked(double x,double y,int button){return owner.mouseClicked(x,y,button);}
    @Override public boolean mouseReleased(double x,double y,int button){return owner.mouseReleased(x,y,button);}
    @Override public boolean mouseDragged(double x,double y,int button,double dx,double dy){return owner.mouseDragged(x,y,button,dx,dy);}
    @Override public boolean mouseScrolled(double x,double y,double dx,double dy){return owner.mouseScrolled(x,y,dx,dy);}
    @Override public boolean keyPressed(int key,int scan,int mods){return owner.keyPressed(key,scan,mods);}
    @Override public boolean keyReleased(int key,int scan,int mods){return owner.keyReleased(key,scan,mods);}
    @Override public boolean charTyped(char ch,int modifiers){return owner.charTyped(ch,modifiers);}
    @Override public void onClose(){owner.onClose();}
    @Override public boolean isPauseScreen(){return false;}
    @Override public void renderBackground(GuiGraphics g,int x,int y,float delta){}
}
