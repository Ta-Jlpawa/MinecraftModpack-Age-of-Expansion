package com.tajlpawa.ageofexpansion.image.client;
import java.util.*;
import java.util.function.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.lwjgl.glfw.GLFW;

// Shared expandable mode menu; children can be added without changing event routing.
public final class PaintToolPalette extends AbstractWidget {
    public enum Tool {BRUSH,LEAF,RECTANGLE,LASSO;public String key(){return "tool_"+name().toLowerCase(Locale.ROOT);}public boolean selecting(){return this==RECTANGLE||this==LASSO;}}
    public record Mode(String id,String key,int icon){}
    public static final List<Mode> BASIC_MODES=List.of(new Mode("normal","mode_normal",5),new Mode("clear","mode_clear",6));
    private final Function<Tool,List<Mode>> options;
    private List<Mode> modes(){return options.apply(Tool.values()[Math.max(0,open)]);}
    private final BiConsumer<Tool,Mode> choose;private final Supplier<Tool> current;private final BooleanSupplier clearing;
    private boolean keyboardMenu;private int keyboardRow;
    private int open=-1,hover=-1,menuX,menuY,menuW=96;private long hoverAt,leaveAt;
    public PaintToolPalette(int x,int y,int w,Supplier<Tool> current,BooleanSupplier clearing,Function<Tool,List<Mode>> options,BiConsumer<Tool,Mode> choose){super(x,y,w,48,ImageScreen.text("tools"));this.options=options;this.choose=choose;this.current=current;this.clearing=clearing;}
    public boolean menuOpen(){return open>=0;}
    public void closeMenu(){open=-1;hover=-1;keyboardMenu=false;}
    private int tile(double x,double y){if(!visible||!active||!isMouseOver(x,y))return -1;int col=(int)(x-getX())/(width/2),row=(int)(y-getY())/24;return Math.min(1,col)+Math.min(1,row)*2;}
    private boolean inMenu(double x,double y){return open>=0&&x>=menuX&&x<menuX+menuW&&y>=menuY&&y<menuY+4+modes().size()*20;}
    @Override public void renderWidget(GuiGraphics g,int mx,int my,float dt){int hit=inMenu(mx,my)?-1:tile(mx,my);if(open>=0)setTooltip(null);else if(hit>=0)setTooltip(net.minecraft.client.gui.components.Tooltip.create(ImageScreen.text(Tool.values()[hit].key()).copy().append(" · ").append(ImageScreen.text(current.get()==Tool.values()[hit]&&clearing.getAsBoolean()?"mode_clear":"mode_normal")).append("\n").append(ImageScreen.text("tool_hint"))));long now=System.currentTimeMillis();if(!active)closeMenu();if(hit!=hover){hover=hit;hoverAt=now;}if(hit>=0&&now-hoverAt>=200)open=hit;if(hit>=0||inMenu(mx,my))leaveAt=now;else if(!keyboardMenu&&now-leaveAt>220)open=-1;
        for(var tool:Tool.values()){int i=tool.ordinal(),x=getX()+i%2*(width/2),y=getY()+i/2*24,w=width/2-2;ImageSkin.box(g,x,y,w,22,!active?3:current.get()==tool?(clearing.getAsBoolean()?4:2):hit==i?1:0);ImageSkin.icon(g,x+3,y+3,i);var font=net.minecraft.client.Minecraft.getInstance().font;String name=ImageScreen.text(tool.key()).getString();if(w>=65)g.drawString(font,font.plainSubstrByWidth(name,w-32),x+21,y+7,0xffe0eee8,false);ImageSkin.icon(g,x+w-15,y+3,5);}
    }
    public void renderMenu(GuiGraphics g,int mx,int my,int screenW,int screenH){if(!visible||!active){closeMenu();return;}if(open<0)return;var font=net.minecraft.client.Minecraft.getInstance().font;menuW=Math.max(106,modes().stream().mapToInt(m->font.width(ImageScreen.text(m.key()))+30).max().orElse(106));int x=getX()+open%2*(width/2),y=getY()+open/2*24+22,h=modes().size()*20+4;menuX=Math.clamp(x,2,Math.max(2,screenW-menuW-2));menuY=y+h<screenH-3?y:Math.max(2,y-22-h);
        g.pose().pushPose();g.pose().translate(0,0,350);ImageSkin.box(g,menuX,menuY,menuW,h,5);for(int i=0;i<modes().size();i++){var m=modes().get(i);int yy=menuY+2+i*20;if(keyboardMenu?i==keyboardRow:inMenu(mx,my)&&(int)(my-menuY-2)/20==i)ImageSkin.box(g,menuX+2,yy,menuW-4,20,1);ImageSkin.icon(g,menuX+4,yy+2,m.icon);g.drawString(font,ImageScreen.text(m.key),menuX+23,yy+6,m.id.equals("clear")?0xffffb4ad:0xffd9eee4,false);}g.flush();g.pose().popPose();}
    public boolean intercept(double x,double y,int button){if(open<0)return false;if(inMenu(x,y)){if(button==0){int index=(int)(y-menuY-2)/20;if(index>=0&&index<modes().size()){choose.accept(Tool.values()[open],modes().get(index));closeMenu();}}return true;}if(tile(x,y)<0){closeMenu();return true;}return false;}
    @Override public boolean mouseClicked(double x,double y,int button){int i=tile(x,y);if(button!=0||i<0)return false;int tileX=getX()+i%2*(width/2);if(x>=tileX+width/2-17){open=i;leaveAt=System.currentTimeMillis();return true;}choose.accept(Tool.values()[i],options.apply(Tool.values()[i]).getFirst());closeMenu();return true;}
    @Override public boolean keyPressed(int key,int scan,int mods){if(key==GLFW.GLFW_KEY_ESCAPE&&menuOpen()){closeMenu();return true;}if(menuOpen()&&(key==GLFW.GLFW_KEY_UP||key==GLFW.GLFW_KEY_DOWN)){keyboardMenu=true;keyboardRow=Math.floorMod(keyboardRow+(key==GLFW.GLFW_KEY_UP?-1:1),modes().size());return true;}if(isFocused()&&(key==GLFW.GLFW_KEY_ENTER||key==GLFW.GLFW_KEY_SPACE)){if(menuOpen()&&keyboardMenu){choose.accept(Tool.values()[open],modes().get(keyboardRow));closeMenu();}else{open=current.get().ordinal();keyboardMenu=true;keyboardRow=0;leaveAt=System.currentTimeMillis();}return true;}return false;}
    @Override protected void updateWidgetNarration(NarrationElementOutput output){defaultButtonNarrationText(output);}
}
