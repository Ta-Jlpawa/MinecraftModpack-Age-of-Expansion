package com.tajlpawa.ageofexpansion.image.client;
import com.tajlpawa.ageofexpansion.image.*;
import java.util.UUID;
import java.util.function.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public final class ImageDialogs {
    public abstract static class Modal extends Screen {
        protected final Screen parent;protected int left,top,w,h;
        private int layer(){return parent instanceof Modal modal?modal.layer()+300:300;}
        protected Modal(Screen parent,Component title){super(title);this.parent=parent;}
        @Override protected void init(){w=Math.min(400,width-24);h=Math.min(250,height-24);left=(width-w)/2;top=(height-h)/2;}
        protected Button button(String key,int x,int y,int width,Runnable run){return addRenderableWidget(new ImageSkin.Action(ImageScreen.text(key),x,y,width,20,run));}
        protected void background(GuiGraphics g,float delta){parent.render(g,-1,-1,delta);g.flush();g.pose().pushPose();g.pose().translate(0,0,layer());g.fill(0,0,width,height,0x99000000);ImageSkin.box(g,left,top,w,h,5);g.drawCenteredString(font,font.plainSubstrByWidth(title.getString(),w-24),width/2,top+10,0xffeeeeee);}
        @Override public void render(GuiGraphics g,int x,int y,float delta){background(g,delta);super.render(g,x,y,delta);g.flush();g.pose().popPose();}
        @Override public void onClose(){minecraft.setScreen(parent);}
        @Override public boolean isPauseScreen(){return false;}
        @Override public void renderBackground(GuiGraphics g,int x,int y,float delta){}
    }
    public static final class LongInput extends Modal {
        private final String initial;private final int limit;private final Consumer<String> submit;private MultiLineEditBox input;
        public LongInput(Screen parent,Component title,String value,int limit,Consumer<String> submit){super(parent,title);initial=value;this.limit=limit;this.submit=submit;}
        @Override protected void init(){super.init();input=new MultiLineEditBox(font,left+12,top+34,w-24,h-76,Component.empty(),title);input.setCharacterLimit(limit);input.setValue(initial);addRenderableWidget(input);setInitialFocus(input);button("cancel",left+12,top+h-30,w/2-18,this::onClose);button("confirm",left+w/2+6,top+h-30,w/2-18,()->{String value=input.getValue().replace("\r","").replace("\n","").strip();onClose();submit.accept(value);});}
    }
    public static final class NumberInput extends Modal {
        private final int min,max,initial;private final IntConsumer submit;private EditBox input;private Component error=Component.empty();
        public NumberInput(Screen parent,int min,int max,int initial,IntConsumer submit){super(parent,ImageScreen.text("enter_value"));this.min=min;this.max=max;this.initial=initial;this.submit=submit;}
        @Override protected void init(){super.init();w=Math.min(260,width-24);h=130;left=(width-w)/2;top=(height-h)/2;input=new EditBox(font,left+12,top+38,w-24,20,title);input.setMaxLength(4);input.setFilter(v->v.matches("[0-9]*"));input.setValue(Integer.toString(initial));addRenderableWidget(input);setInitialFocus(input);button("cancel",left+12,top+h-30,w/2-18,this::onClose);button("confirm",left+w/2+6,top+h-30,w/2-18,this::confirm);}
        private void confirm(){try{int value=Integer.parseInt(input.getValue());if(value<min||value>max)throw new IllegalArgumentException();submit.accept(value);onClose();}catch(IllegalArgumentException e){error=ImageScreen.text("value_range",min,max);}}
        @Override public boolean keyPressed(int key,int scan,int mods){if(key==257||key==335){confirm();return true;}return super.keyPressed(key,scan,mods);}
        @Override public void render(GuiGraphics g,int x,int y,float delta){background(g,delta);g.drawString(font,error.getString().isEmpty()?ImageScreen.text("value_range",min,max):error,left+12,top+70,error.getString().isEmpty()?0xffbdcfc9:0xffffaaaa,false);for(var child:children())if(child instanceof Renderable r)r.render(g,x,y,delta);g.flush();g.pose().popPose();}
    }
    public static final class CanvasSize extends Modal {
        private final ImageLimits limits;private final BiConsumer<Integer,Integer> create;private final int initialW,initialH;private EditBox xBox,yBox;private Component error=Component.empty();
        public CanvasSize(Screen parent,ImageLimits limits,int x,int y,BiConsumer<Integer,Integer> create){super(parent,ImageScreen.text("new_canvas"));this.limits=limits;initialW=x;initialH=y;this.create=create;}
        @Override protected void init(){super.init();h=154;top=(height-h)/2;xBox=new EditBox(font,left+70,top+43,w-86,20,ImageScreen.text("width"));yBox=new EditBox(font,left+70,top+74,w-86,20,ImageScreen.text("height"));for(var box:new EditBox[]{xBox,yBox}){box.setMaxLength(4);box.setFilter(s->s.matches("[0-9]*"));addRenderableWidget(box);}xBox.setValue(Integer.toString(initialW));yBox.setValue(Integer.toString(initialH));button("cancel",left+12,top+h-30,w/2-18,this::onClose);button("create",left+w/2+6,top+h-30,w/2-18,()->{try{int x=Integer.parseInt(xBox.getValue()),y=Integer.parseInt(yBox.getValue());if(x<limits.minX()||x>limits.maxX()||y<limits.minY()||y>limits.maxY())throw new IllegalArgumentException();onClose();create.accept(x,y);}catch(IllegalArgumentException e){error=ImageScreen.text("error.canvas_size");}});}
        @Override public void render(GuiGraphics g,int x,int y,float delta){background(g,delta);g.drawString(font,ImageScreen.text("width"),left+12,top+49,-1,false);g.drawString(font,ImageScreen.text("height"),left+12,top+80,-1,false);g.drawString(font,font.plainSubstrByWidth(error.getString(),w-24),left+12,top+104,0xffffaaaa,false);for(var child:children())if(child instanceof net.minecraft.client.gui.components.Renderable r)r.render(g,x,y,delta);g.flush();g.pose().popPose();}
    }
    public static final class Filter extends Modal {
        private final ImageScreen editor;private final boolean admin;private String owner,query;private final BiConsumer<String,String> apply;
        public Filter(Screen parent,boolean admin,String owner,String query,BiConsumer<String,String> apply){this(parent,admin,owner,query,apply,parent instanceof ImagePanelScreen panel?panel.owner:parent instanceof ImageScreen screen?screen:null);}
        public Filter(Screen parent,boolean admin,String owner,String query,BiConsumer<String,String> apply,ImageScreen editor){super(parent,ImageScreen.text("filter"));this.admin=admin;this.owner=owner;this.query=query;this.apply=apply;this.editor=editor;}
        @Override protected void init(){super.init();button("filter_name",left+12,top+37,w-24,()->minecraft.setScreen(new LongInput(this,ImageScreen.text("filter_name"),query,64,value->{query=value;rebuildWidgets();})));
            if(admin){button("own_images",left+12,top+68,(w-30)/2,()->{owner="";rebuildWidgets();});button("all_players",left+w/2+3,top+68,(w-30)/2,()->{owner="*";rebuildWidgets();});button("filter_player",left+12,top+94,w-24,()->minecraft.setScreen(new PlayerPicker(this,editor,owner.equals("*")?"":owner,value->{owner=value;rebuildWidgets();},value->{owner=value;onClose();apply.accept(owner,query);})));}
            button("cancel",left+12,top+h-30,w/2-18,this::onClose);button("apply",left+w/2+6,top+h-30,w/2-18,()->{onClose();apply.accept(admin?owner:"",query);});
        }
        @Override public void render(GuiGraphics g,int x,int y,float delta){background(g,delta);String who=!admin||owner.isBlank()?ImageScreen.text("own_images").getString():owner.equals("*")?ImageScreen.text("all_players").getString():owner;g.drawString(font,font.plainSubstrByWidth(who+" / "+query,w-24),left+12,top+h-52,0xffc6d9d6,false);for(var child:children())if(child instanceof net.minecraft.client.gui.components.Renderable r)r.render(g,x,y,delta);g.flush();g.pose().popPose();}
    }
    public static final class PlayerPicker extends Modal {
        private final ImageScreen editor;private final Consumer<String> apply,quickApply;private String query;private EditBox input;private ImagePlayerList playerList;
        private java.util.List<ImageLibrary.Owner> players=java.util.List.of();private ImageLibrary.Owner chosen;
        private int page,pages=1,rows;private long due;private boolean requested,loading;private Component status=Component.empty();
        public PlayerPicker(Screen parent,ImageScreen editor,String initial,Consumer<String> apply){this(parent,editor,initial,apply,apply);}
        public PlayerPicker(Screen parent,ImageScreen editor,String initial,Consumer<String> apply,Consumer<String> quickApply){super(parent,ImageScreen.text("filter_player"));this.editor=editor;query=initial;this.apply=apply;this.quickApply=quickApply;}
        @Override protected void init(){super.init();rows=Math.clamp((h-124)/22,1,6);
            input=new EditBox(font,left+12,top+34,w-24,20,ImageScreen.text("player_name_uuid"));input.setMaxLength(64);input.setHint(ImageScreen.text("player_name_uuid"));input.setValue(query);input.setResponder(value->{query=value;chosen=null;page=0;due=System.currentTimeMillis()+250;if(playerList!=null)playerList.active=false;});addRenderableWidget(input);setInitialFocus(input);
            playerList=addRenderableWidget(new ImagePlayerList(left+12,top+64,w-24,rows,players,chosen==null?null:chosen.id(),entry->{chosen=entry;status=Component.empty();},entry->{chosen=entry;finish(true);}));playerList.active=!loading&&due==0;
            button("previous",left+12,top+h-56,70,()->{page=Math.max(0,page-1);request();}).active=page>0&&!loading;
            button("next",left+w-82,top+h-56,70,()->{page=Math.min(pages-1,page+1);request();}).active=page+1<pages&&!loading;
            button("cancel",left+12,top+h-30,w/2-18,this::onClose);button("confirm",left+w/2+6,top+h-30,w/2-18,this::confirm);
            if(!requested){requested=true;request();}
        }
        private void request(){if(editor==null)return;loading=true;if(playerList!=null)playerList.active=false;status=ImageScreen.text("working");editor.requestOwners(query.strip(),page,rows,n->{
            if(minecraft.screen!=this)return;loading=false;var list=new java.util.ArrayList<ImageLibrary.Owner>();for(var tag:n.getList("owners",net.minecraft.nbt.Tag.TAG_COMPOUND))list.add(ImageLibrary.Owner.load((net.minecraft.nbt.CompoundTag)tag));players=java.util.List.copyOf(list);if(chosen!=null&&players.stream().noneMatch(p->p.id().equals(chosen.id())))chosen=null;page=n.getInt("page");pages=Math.max(1,n.getInt("pages"));status=players.isEmpty()?ImageScreen.text("error.no_player_images"):Component.empty();rebuildWidgets();
        });}
        private void confirm(){finish(false);}
        private void finish(boolean quick){if(loading||due!=0){status=ImageScreen.text("working");return;}
            var exact=players.stream().filter(p->p.name().equalsIgnoreCase(query.strip())||p.id().toString().equalsIgnoreCase(query.strip())).toList();
            var value=chosen!=null?chosen:exact.size()==1?exact.getFirst():null;
            if(value==null){status=ImageScreen.text("error.no_player_images");return;}onClose();(quick?quickApply:apply).accept(value.id().toString());
        }
        @Override public void tick(){super.tick();if(due!=0&&System.currentTimeMillis()>=due){due=0;request();}}
        @Override public boolean keyPressed(int key,int scan,int mods){if(key==257||key==335){confirm();return true;}return super.keyPressed(key,scan,mods);}
        @Override public void render(GuiGraphics g,int x,int y,float delta){background(g,delta);g.drawString(font,ImageScreen.text("player_name_uuid"),left+12,top+23,0xffb6c9c5,false);g.drawString(font,font.plainSubstrByWidth(status.getString().isEmpty()?ImageScreen.text("players_with_images").getString():status.getString(),w-24),left+12,top+55,!status.getString().isEmpty()&&!loading?0xffffaaaa:0xffb6c9c5,false);g.drawCenteredString(font,(page+1)+" / "+pages,width/2,top+h-50,0xffc5d7d1);for(var child:children())if(child instanceof Renderable r)r.render(g,x,y,delta);g.flush();g.pose().popPose();}
    }
    public static final class Preview extends Modal {
        private final ImageLibrary.Entry entry;private final UUID token;private final BlockPos pos;private float zoom=1,dx,dy;private boolean dragging;
        public Preview(Screen parent,ImageLibrary.Entry entry,UUID token,BlockPos pos){super(parent,Component.literal(entry.name()));this.entry=entry;this.token=token;this.pos=pos;}
        @Override protected void init(){super.init();w=Math.min(360,(int)(width*.82));h=Math.min(240,(int)(height*.82));left=(width-w)/2;top=(height-h)/2;button("reset_view",left+12,top+h-30,w/2-18,()->{zoom=1;dx=dy=0;});button("close",left+w/2+6,top+h-30,w/2-18,this::onClose);}
        private boolean inside(double x,double y){return x>=left+12&&x<left+w-12&&y>=top+32&&y<top+h-40;}
        @Override public boolean mouseClicked(double x,double y,int button){if(inside(x,y)&&(button==0||button==2)){dragging=true;return true;}return super.mouseClicked(x,y,button);}
        @Override public boolean mouseReleased(double x,double y,int button){dragging=false;return super.mouseReleased(x,y,button);}
        @Override public boolean mouseDragged(double x,double y,int button,double mx,double my){if(dragging){dx+=mx;dy+=my;return true;}return super.mouseDragged(x,y,button,mx,my);}
        @Override public boolean mouseScrolled(double x,double y,double sx,double sy){if(inside(x,y)){zoom=Math.clamp(zoom*(float)Math.pow(1.2,sy),.25f,64);return true;}return super.mouseScrolled(x,y,sx,sy);}
        @Override public void render(GuiGraphics g,int x,int y,float delta){background(g,delta);int vw=w-24,vh=h-72;ImageScreen.checker(g,left+12,top+32,vw,vh);g.enableScissor(left+12,top+32,left+w-12,top+h-40);var texture=ImageTextures.get(entry.id(),false,token,pos);if(texture!=null){float scale=Math.min(vw/(float)texture.width(),vh/(float)texture.height())*zoom,iw=texture.width()*scale,ih=texture.height()*scale;g.pose().pushPose();g.pose().translate(left+12+(vw-iw)/2+dx,top+32+(vh-ih)/2+dy,0);ImageRender.quad(g.pose(),g.bufferSource(),texture.location(),new ImageSelection.Rect(0,0,iw,ih,0,0,1,1));g.flush();g.pose().popPose();}g.flush();g.disableScissor();for(var child:children())if(child instanceof net.minecraft.client.gui.components.Renderable r)r.render(g,x,y,delta);g.flush();g.pose().popPose();}
    }
    private ImageDialogs(){}
}
