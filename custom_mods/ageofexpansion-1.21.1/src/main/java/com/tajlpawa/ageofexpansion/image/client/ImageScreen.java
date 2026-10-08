package com.tajlpawa.ageofexpansion.image.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.tajlpawa.ageofexpansion.display.client.DisplayColorScreen;
import com.tajlpawa.ageofexpansion.image.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

// 四模式共用显示草稿；图像入库与应用到方块是两个独立的提交动作。
public final class ImageScreen extends Screen {
    public final UUID token;private final ImageLimits limits;private final BlockPos pos;private final int kind,columns,rows;
    private ImageSelection selection;private int mode,owned,page,pages=1,left,top,panelW,panelH,px,py,side,lx,rx,cw;
    private boolean busy,disposed,dirty,panning,localDirty,paintingTask;private int epoch;
    private String name="Image",uploadName="",renameName="",path="",filter="";private Component status=Component.empty();
    private int newW,newH,diameter=8,opacity=100,color=0x75d7ac;private boolean erase;
    private float viewZoom=1,panX,panY;
    private Button applyButton;private UUID uploadRequest,copyRequestId;
    private Runnable afterSaved;private long transferStarted;private UUID copyTransfer,applyRequest;
    private byte[] uploadBytes;private BufferedImage uploadImage;private PaintCanvas canvas;private UUID canvasBase=ImageSelection.NONE;
    private List<ImageLibrary.Entry> entries=List.of();private ImageLibrary.Entry selected;
    private DynamicTexture localTexture;private final ResourceLocation localId=ResourceLocation.parse("ageofexpansion:runtime/editor_"+UUID.randomUUID());
    public static Component text(String key,Object...args){return Component.translatable("screen.ageofexpansion.image."+key,args);}
    public ImageScreen(UUID token,CompoundTag data){
        super(text("title"));if(data.contains("entry"))displaying=ImageLibrary.Entry.load(data.getCompound("entry"));this.token=token;limits=ImageLimits.load(data);pos=BlockPos.of(data.getLong("pos"));kind=data.getInt("kind");columns=Math.max(1,data.getInt("width"));rows=Math.max(1,data.getInt("height"));owned=data.getInt("owned");
        var old=ImageSelection.load(data.getCompound("selection"));selection=new ImageSelection(old.id(),Math.clamp(old.scale(),limits.minScale(),limits.maxScale()),old.horizontal(),old.vertical(),old.fit());newW=Math.clamp(256,limits.minX(),limits.maxX());newH=Math.clamp(256,limits.minY(),limits.maxY());
    }
    private Button button(String key,int x,int y,int w,Runnable run){var b=new ImageSkin.Action(text(key),x,y,w,18,run);b.active=!busy;return addRenderableWidget(b);}
    private Button primary(String key,int x,int y,int w,boolean enabled,Runnable action){
        var b=button(busy?"cancel_task":key,x,y,w,busy?this::cancelTask:action);b.active=busy?!paintingTask:enabled;return b;
    }
    private ImageSlider scaleSlider;
    private final List<Button> horizontalButtons=new ArrayList<>(),verticalButtons=new ArrayList<>();
    private ImageLibrary.Entry displaying;
    private String nameQuery="";
    private boolean closeAfterApply;
    private long lastImageClick;
    private UUID lastClickedImage;
    private List<Renderable> baseWidgets=List.of();
    private int[] baseLayout;
    private Button nameButton,undoButton,clearSelectionButton;
    private PaintToolPalette palette;private PaintToolPalette.Tool tool=PaintToolPalette.Tool.BRUSH;
    private final List<PaintRegion.Point> trace=new ArrayList<>();private BitSet mask;
    private final PaintOverlay paintOverlay=new PaintOverlay();private PaintCanvas.Move moving;
    private DynamicTexture movingTexture;private final ResourceLocation movingId=ResourceLocation.parse("ageofexpansion:runtime/move_"+UUID.randomUUID());
    private boolean tracing,moveHeld;private double startMoveX,startMoveY;private int moveDx,moveDy;
    private final Map<AbstractWidget,Integer> paintControls=new LinkedHashMap<>();private int paintScroll,paintBottom;
    private long strokeRevision;

    private int galleryW,galleryH;
    private UUID ownerRequest,filterRequest;private Consumer<CompoundTag> ownerResult;private String pendingFilter,pendingQuery;
    private void showCurrent(){if(mode==0)minecraft.setScreen(this);else minecraft.setScreen(new ImagePanelScreen(this));}
    private void editText(String key,String value,int max,Consumer<String> submit){minecraft.setScreen(new ImageDialogs.LongInput(minecraft.screen,text(key),value,max,submit));}
    private ImageSlider slider(int x,int y,int min,int max,int value,String suffix,java.util.function.IntConsumer change){var slider=new ImageSlider(x,y,cw,min,max,value,suffix,change);slider.active=!busy;return addRenderableWidget(slider);}
    private void applySelection(boolean close){
        if(busy)return;closeAfterApply=close;var n=new CompoundTag();n.put("selection",selection.save());applyRequest=ImageNetwork.sendClient(ImageNetwork.APPLY,token,n,new byte[0]);transferStarted=System.currentTimeMillis();busy=true;status=text("working");rebuildWidgets();
    }
    private void align(int horizontal,int vertical){
        var old=selection;selection=new ImageSelection(old.id(),old.scale(),horizontal,vertical,old.fit());
        var texture=ImageTextures.get(selection.id(),false,token,pos);float w=kind==0?120:144*(columns-.25f),h=kind==0?60:144*(rows-.25f);
        status=texture!=null&&old.layout(texture.width(),texture.height(),w,h).equals(selection.layout(texture.width(),texture.height(),w,h))?text("alignment_full"):Component.empty();rebuildWidgets();
    }
    @Override protected void init(){
        panelW=Math.min(680,width-12)-(mode==0?0:12);panelH=Math.min(440,height-12)-(mode==0?0:12);left=(width-panelW)/2;top=(height-panelH)/2;
        cw=Math.max(68,Math.min(150,(int)(panelW*.23)));side=Math.max(48,Math.min(panelH-100,panelW-2*cw-36));px=left+(panelW-side)/2;py=top+48;lx=left+10;rx=left+panelW-cw-10;
        // 先保留原有预览尺寸，再将两侧操作列贴近预览，收起多余的外框宽度。
        if(mode!=3){panelW=side+2*cw+36;left=(width-panelW)/2;px=left+cw+18;lx=px-cw-8;rx=px+side+8;}
        paintControls.clear();palette=null;clearSelectionButton=null;horizontalButtons.clear();verticalButtons.clear();int bottom=top+panelH-28,bw=Math.min(120,(panelW-40)/3);
        button("cancel",mode==3?left+cw+24:left+panelW/2-bw-8,bottom,mode==3?(panelW-cw-46)/2:bw,this::onClose).active=!paintingTask;
        if(mode==0){
            button("upload",lx,py,cw,()->navigate(1));button("paint",lx,py+25,cw,()->navigate(2));button("choose_existing",lx,py+50,cw,()->navigate(3));
            button("clear",lx,py+83,cw,()->{selection=new ImageSelection(ImageSelection.NONE,selection.scale(),selection.horizontal(),selection.vertical(),selection.fit());displaying=null;rebuildWidgets();});
            scaleSlider=slider(rx,py+13,limits.minScale(),limits.maxScale(),selection.scale(),"%",n->selection=new ImageSelection(selection.id(),n,selection.horizontal(),selection.vertical(),selection.fit()));
            button("fit",rx,py+58,cw/2-1,()->{selection=new ImageSelection(selection.id(),selection.scale(),selection.horizontal(),selection.vertical(),true);rebuildWidgets();}).active=!busy&&!selection.fit();
            button("crop",rx+cw/2+1,py+58,cw/2-1,()->{selection=new ImageSelection(selection.id(),selection.scale(),selection.horizontal(),selection.vertical(),false);rebuildWidgets();}).active=!busy&&selection.fit();
            String[] vs={"top","middle","bottom"},hs={"left","center","right"};for(int i=0;i<3;i++){int a=i;
                var vb=button(vs[i],rx+i*cw/3,py+86,cw/3,()->align(selection.horizontal(),a));vb.active=!busy&&selection.vertical()!=i;verticalButtons.add(vb);
                var hb=button(hs[i],rx+i*cw/3,py+113,cw/3,()->align(a,selection.vertical()));hb.active=!busy&&selection.horizontal()!=i;horizontalButtons.add(hb);
            }
            applyButton=primary("apply",left+panelW/2+8,bottom,bw,true,()->applySelection(true));
        }else if(mode==1){
            button("browse",lx,py,cw,this::browse);button("enter_path",lx,py+30,cw,()->editText("path",path,32768,value->{path=value;previewFile();}));
            primary("upload_confirm",left+panelW/2+8,bottom,bw,uploadBytes!=null,()->upload(false));
        }else if(mode==2){
            button("new_canvas",lx,py,cw,()->{if(owned>=limits.count()){status=text("error.quota");return;}guard(()->minecraft.setScreen(new ImageDialogs.CanvasSize(minecraft.screen,limits,newW,newH,(w,h)->createCanvas(new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB),ImageSelection.NONE))),false);});
            button("reset_view",lx,py+24,cw,()->{viewZoom=1;panX=panY=0;});undoButton=button("undo",lx,py+64,cw,this::undo);undoButton.active=!busy&&canvas!=null&&canvas.undoCount()>0;
            palette=addRenderableWidget(new PaintToolPalette(rx,py,cw,()->tool,()->erase,t->PaintToolPalette.BASIC_MODES,(next,selectedMode)->{cancelGesture();tool=next;erase=selectedMode.id().equals("clear");}));palette.active=!busy;paintControls.put(palette,0);
            var diameterSlider=slider(rx,py+62,1,512,diameter," px",n->diameter=n);diameterSlider.setTooltip(Tooltip.create(text("diameter")));paintControls.put(diameterSlider,62);
            var opacitySlider=slider(rx,py+101,0,100,opacity,"%",n->opacity=n);opacitySlider.setTooltip(Tooltip.create(text("opacity")));paintControls.put(opacitySlider,101);
            var colorButton=button("color",lx,py+87,cw,()->minecraft.setScreen(new DisplayColorScreen(minecraft.screen,color,value->color=value,this::showCurrent)));colorButton.setTooltip(Tooltip.create(text("color")));
            paintBottom=top+panelH-46;positionPaintControls();
            clearSelectionButton=button("cancel_selection",px+side-20,py+2,18,()->editCanvas(()->{canvas.clearSelection();return true;}));clearSelectionButton.setMessage(Component.literal("×"));clearSelectionButton.setTooltip(Tooltip.create(text("cancel_selection_hint")));clearSelectionButton.visible=canvas!=null&&canvas.hasSelection();
            nameButton=button("name",px,top+28,side,()->editText("name",name,64,value->{name=value;rebuildWidgets();}));nameButton.setMessage(Component.literal(font.plainSubstrByWidth(name,side-8)));
            primary("save_image",left+panelW/2+8,bottom,bw,canvas!=null,()->upload(true));
        }else{
            galleryW=panelW-cw-34;galleryH=panelH-92;px=left+cw+22;py=top+43;
            button("filter",lx,py+13,cw,()->minecraft.setScreen(new ImageDialogs.Filter(minecraft.screen,limits.admin(),filter,nameQuery,this::applyFilter,this)));
            button("rename",lx,bottom-80,cw,()->{if(selected!=null)editText("name",selected.name(),64,value->{renameName=value;manage(ImageNetwork.RENAME);});}).active=!busy&&selected!=null;
            button("delete",lx,bottom-58,cw,()->{if(selected!=null){Screen parent=minecraft.screen;minecraft.setScreen(new ConfirmScreen(ok->{minecraft.setScreen(parent);if(ok)manage(ImageNetwork.DELETE);},text("delete"),text("delete_warning")));}}).active=!busy&&selected!=null;
            button("edit_copy",lx,bottom-36,cw,this::editCopy).active=!busy&&selected!=null&&owned<limits.count();
            button("previous",lx,bottom,cw/2-1,()->{page=Math.max(0,page-1);requestList();}).active=!busy&&page>0;
            button("next",lx+cw/2+1,bottom,cw/2-1,()->{page=Math.min(pages-1,page+1);requestList();}).active=!busy&&page<pages-1;
            primary("apply",left+cw+36+(panelW-cw-46)/2,bottom,(panelW-cw-46)/2,selected!=null,()->{if(selected!=null){displaying=selected;selection=new ImageSelection(selected.id(),selection.scale(),selection.horizontal(),selection.vertical(),selection.fit());mode=0;showCurrent();applySelection(false);}});
        }
        if(mode==0){baseWidgets=List.copyOf(renderables);baseLayout=new int[]{left,top,panelW,panelH,px,py,side};}
    }
    private boolean validScale(){return selection.scale()>=limits.minScale()&&selection.scale()<=limits.maxScale();}
    private void navigate(int target){if(busy)return;guard(()->{if(target==1){path="";uploadName="";uploadBytes=null;uploadImage=null;}if(target==2)resetPainting();mode=target;status=target==2&&owned>=limits.count()?text("error.quota"):Component.empty();showCurrent();if(target==3)requestList();},false);}
    private void resetPainting(){
        cancelGesture();if(canvas!=null)canvas.close();canvas=null;mask=null;tool=PaintToolPalette.Tool.BRUSH;paintScroll=0;canvasBase=ImageSelection.NONE;dirty=false;name="Image";diameter=8;opacity=100;color=0x75d7ac;erase=false;viewZoom=1;panX=panY=0;localDirty=false;
        newW=Math.clamp(256,limits.minX(),limits.maxX());newH=Math.clamp(256,limits.minY(),limits.maxY());
    }
    private void guard(Runnable next,boolean closing){
        if(mode==2&&dirty){minecraft.setScreen(new LeaveDrawing(this,next));return;}next.run();
    }
    private void createCanvas(BufferedImage image,UUID base){cancelGesture();tool=PaintToolPalette.Tool.BRUSH;erase=false;mask=null;paintOverlay.invalidate();if(canvas!=null)canvas.close();canvas=new PaintCanvas(image,limits.undo());canvasBase=base;newW=image.getWidth();newH=image.getHeight();dirty=base.equals(ImageSelection.NONE);viewZoom=1;panX=panY=0;mode=2;updateLocal(canvas.image);showCurrent();}
    public void requestOwners(String query,int page,int size,Consumer<CompoundTag> result){
        var n=new CompoundTag();n.putString("query",query);n.putInt("page",page);n.putInt("size",size);ownerResult=result;ownerRequest=ImageNetwork.sendClient(ImageNetwork.OWNERS,token,n,new byte[0]);
    }
    private void applyFilter(String owner,String query){
        pendingFilter=limits.admin()?owner:"";pendingQuery=query;var n=new CompoundTag();n.putString("filter",pendingFilter);n.putString("query",query);n.putInt("page",0);filterRequest=ImageNetwork.sendClient(ImageNetwork.LIST,token,n,new byte[0]);
    }
    private void requestList(){var n=new CompoundTag();n.putString("filter",filter.substring(0,Math.min(64,filter.length())));n.putString("query",nameQuery);n.putInt("page",page);ImageNetwork.sendClient(ImageNetwork.LIST,token,n,new byte[0]);}
    private void manage(int action){if(selected==null)return;var n=new CompoundTag();n.putUUID("id",selected.id());n.putString("name",renameName);ImageNetwork.sendClient(action,token,n,new byte[0]);}
    private void browse(){background(()->{try(var stack=org.lwjgl.system.MemoryStack.stackPush()){
        var patterns=stack.pointers(stack.UTF8("*.png"),stack.UTF8("*.jpg"),stack.UTF8("*.jpeg"));return org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_openFileDialog("Select PNG / JPEG",path.isBlank()?null:path,patterns,"PNG / JPEG",false);
    }},value->{if(value!=null){path=value;rebuildWidgets();previewFile();}});}
    private void previewFile(){if(path.isBlank()){status=text("error.io");return;}String source=path;uploadBytes=null;uploadImage=null;background(()->{byte[] bytes=ImageFiles.read(Path.of(source.strip().replaceAll("^\"|\"$","")),limits.uploadKB()*1024);return new Object[]{bytes,ImageFiles.decode(bytes),Path.of(source).getFileName().toString()};},value->{uploadBytes=(byte[])value[0];uploadImage=((ImageFiles.Decoded)value[1]).image();uploadName=(String)value[2];updateLocal(uploadImage);status=text("file_info",uploadImage.getWidth(),uploadImage.getHeight(),(uploadBytes.length+1023)/1024);rebuildWidgets();});}
    private void upload(boolean drawn){
        if(busy||owned>=limits.count()){status=text("error.quota");return;}if(drawn&&canvas==null||!drawn&&uploadBytes==null)return;
        var n=new CompoundTag();n.putString("name",drawn?name:uploadName);n.putBoolean("drawn",drawn);if(drawn&&!canvasBase.equals(ImageSelection.NONE))n.putUUID("base",canvasBase);
        if(drawn)canvasTask(()->ImageFiles.png(canvas.image),bytes->startUpload(n,bytes));else startUpload(n,uploadBytes);
    }
    private void startUpload(CompoundTag data,byte[] bytes){busy=true;status=text("working");transferStarted=System.currentTimeMillis();uploadRequest=ImageNetwork.sendClient(ImageNetwork.UPLOAD,token,data,bytes);rebuildWidgets();}
    private void editCopy(){if(selected==null)return;copyRequestId=selected.id();name=selected.name();var n=new CompoundTag();n.putUUID("id",selected.id());n.putLong("pos",pos.asLong());n.putBoolean("fallback",true);copyTransfer=ImageNetwork.sendClient(ImageNetwork.GET,token,n,new byte[0]);transferStarted=System.currentTimeMillis();busy=true;status=text("working");rebuildWidgets();}
    public void receive(ImageNetwork.Packet packet){
        if(disposed)return;var n=packet.data();
        if(packet.request().equals(ownerRequest)&&(packet.action()==ImageNetwork.OWNERS||packet.action()==ImageNetwork.ERROR)){var result=ownerResult;ownerRequest=null;ownerResult=null;if(result!=null)result.accept(n);return;}
        if(packet.request().equals(filterRequest)){filterRequest=null;if(packet.action()==ImageNetwork.LIST){filter=pendingFilter;nameQuery=pendingQuery;}else{pendingFilter=null;pendingQuery=null;status=text("error.no_player_images");return;}pendingFilter=null;pendingQuery=null;}

        if(packet.action()==ImageNetwork.GET){if(packet.request().equals(copyTransfer)&&copyRequestId!=null&&n.hasUUID("id")&&copyRequestId.equals(n.getUUID("id"))&&!n.getBoolean("thumb")){UUID base=copyRequestId;copyRequestId=null;copyTransfer=null;busy=false;background(()->ImageFiles.decodePixels(packet.bytes()),image->createCanvas(image,base));}return;}
        if(packet.action()==ImageNetwork.INVALIDATE){if(n.hasUUID("owner")&&minecraft.player!=null&&n.getUUID("owner").equals(minecraft.player.getUUID())){owned=Math.max(0,owned-1);if(!busy&&(canvas==null||!canvas.painting()))rebuildWidgets();}UUID id=n.getUUID("id");if(selection.id().equals(id))selection=new ImageSelection(ImageSelection.NONE,selection.scale(),selection.horizontal(),selection.vertical(),selection.fit());if(selected!=null&&selected.id().equals(id))selected=null;if(mode==3)requestList();return;}
        if(packet.action()==ImageNetwork.ERROR){if(busy&&!packet.request().equals(uploadRequest)&&!packet.request().equals(copyTransfer)&&!packet.request().equals(applyRequest))return;busy=false;copyRequestId=null;uploadRequest=null;afterSaved=null;status=text("error."+n.getString("error"));rebuildWidgets();}
        else if(packet.action()==ImageNetwork.LIST){var list=new ArrayList<ImageLibrary.Entry>();for(var entry:n.getList("entries",Tag.TAG_COMPOUND))list.add(ImageLibrary.Entry.load((CompoundTag)entry));entries=List.copyOf(list);page=n.getInt("page");pages=n.getInt("pages");owned=n.getInt("owned");if(selected!=null)selected=entries.stream().filter(e->e.id().equals(selected.id())).findFirst().orElse(null);rebuildWidgets();}
        else if(packet.action()==ImageNetwork.UPLOAD){busy=false;uploadRequest=null;owned++;dirty=false;var entry=ImageLibrary.Entry.load(n);displaying=entry;selection=new ImageSelection(entry.id(),selection.scale(),selection.horizontal(),selection.vertical(),selection.fit());status=text("saved");if(afterSaved!=null){var next=afterSaved;afterSaved=null;next.run();}else{mode=0;showCurrent();}}
        else if(packet.action()==ImageNetwork.APPLIED){busy=false;applyRequest=null;if(n.contains("entry"))displaying=ImageLibrary.Entry.load(n.getCompound("entry"));status=text("applied");if(closeAfterApply){closeAfterApply=false;finishClose();}else rebuildWidgets();}
        else if(packet.action()==ImageNetwork.DELETE||packet.action()==ImageNetwork.RENAME){selected=null;requestList();status=text("saved");}
    }
    private interface IO<T>{T run()throws Exception;}
    private <T> void background(IO<T> job,Consumer<T> success){busy=true;int version=++epoch;status=text("working");rebuildWidgets();try{ImageFiles.WORK.execute(()->{try{T result=job.run();net.minecraft.client.Minecraft.getInstance().execute(()->{if(disposed||version!=epoch){if(disposed&&canvas!=null)canvas.close();return;}busy=false;status=Component.empty();success.accept(result);rebuildWidgets();});}catch(Exception e){net.minecraft.client.Minecraft.getInstance().execute(()->{if(disposed||version!=epoch){if(disposed&&canvas!=null)canvas.close();return;}busy=false;paintingTask=false;status=text("error."+ImageNetwork.errorKey(e));afterSaved=null;rebuildWidgets();});}});}catch(java.util.concurrent.RejectedExecutionException e){busy=false;paintingTask=false;status=text("error.busy");rebuildWidgets();}}
    private <T> void canvasTask(IO<T> job,Consumer<T> success){
        paintingTask=true;busy=true;int version=++epoch;
        try{ImageFiles.WORK.execute(()->{
            try{T result=job.run();minecraft.execute(()->{
                if(disposed||version!=epoch){if(disposed&&canvas!=null)canvas.close();return;}
                paintingTask=false;busy=false;success.accept(result);
                if(undoButton!=null&&canvas!=null)undoButton.active=!busy&&canvas.undoCount()>0;
            });}catch(Exception e){minecraft.execute(()->{if(disposed||version!=epoch)return;paintingTask=false;busy=false;status=text("error."+ImageNetwork.errorKey(e));});}
        });}catch(java.util.concurrent.RejectedExecutionException e){paintingTask=false;busy=false;status=text("error.busy");}
    }
    private void cancelTask(){if(paintingTask)return;epoch++;if(uploadRequest!=null)ImageNetwork.cancelClient(token,uploadRequest);uploadRequest=null;copyRequestId=null;if(copyTransfer!=null)ImageNetwork.cancelClient(token,copyTransfer);copyTransfer=null;applyRequest=null;afterSaved=null;busy=false;status=text("cancelled");rebuildWidgets();}
    private void updateLocal(BufferedImage image){
        if(localTexture==null||localTexture.getPixels().getWidth()!=image.getWidth()||localTexture.getPixels().getHeight()!=image.getHeight()){
            if(localTexture!=null)minecraft.getTextureManager().release(localId);localTexture=new DynamicTexture(image.getWidth(),image.getHeight(),true);minecraft.getTextureManager().register(localId,localTexture);
        }
        var pixels=localTexture.getPixels();for(int y=0;y<image.getHeight();y++)for(int x=0;x<image.getWidth();x++){int c=image.getRGB(x,y);pixels.setPixelRGBA(x,y,(c&0xff00ff00)|(c>>>16&255)|((c&255)<<16));}localTexture.upload();localDirty=false;
    }
    private void updatePaintTexture(){
        if(localTexture==null){updateLocal(canvas.image);return;}int[] area=canvas.takeDirty();
        if(area!=null){var pixels=localTexture.getPixels();for(int y=area[1];y<=area[3];y++)for(int x=area[0];x<=area[2];x++){int c=canvas.image.getRGB(x,y);pixels.setPixelRGBA(x,y,(c&0xff00ff00)|(c>>>16&255)|((c&255)<<16));}localTexture.bind();pixels.upload(0,area[0],area[1],area[0],area[1],area[2]-area[0]+1,area[3]-area[1]+1,false,false);}
        localDirty=false;
    }
    private void undo(){if(canvas==null||busy)return;canvasTask(()->canvas.undo(),changed->{if(changed){dirty|=canvas.lastUndoPixels();updatePaintTexture();refreshMask();}});}
    private float canvasScale(){return canvas==null?1:Math.min(side/(float)canvas.image.getWidth(),side/(float)canvas.image.getHeight())*viewZoom;}
    private double imageX(double x){return (x-px-(side-canvas.image.getWidth()*canvasScale())/2-panX)/canvasScale();}
    private double imageY(double y){return (y-py-(side-canvas.image.getHeight()*canvasScale())/2-panY)/canvasScale();}
    private boolean inside(double x,double y){return x>=px&&x<px+(mode==3?galleryW:side)&&y>=py&&y<py+(mode==3?galleryH:side);}
    private void positionPaintControls(){paintScroll=Math.clamp(paintScroll,0,Math.max(0,py+133-paintBottom));for(var e:paintControls.entrySet()){var w=e.getKey();w.setY(py+e.getValue()-paintScroll);w.visible=w.getY()>=py&&w.getY()+w.getHeight()<=paintBottom;}}
    private void refreshMask(){mask=canvas==null?null:canvas.selection();paintOverlay.invalidate();if(clearSelectionButton!=null)clearSelectionButton.visible=canvas!=null&&canvas.hasSelection();}
    private void editCanvas(IO<Boolean> edit){if(canvas==null||busy)return;long revision=canvas.imageRevision();canvasTask(edit,changed->{dirty|=canvas.imageRevision()!=revision;updatePaintTexture();refreshMask();});}
    private void releaseMove(){moving=null;moveDx=moveDy=0;if(movingTexture!=null){minecraft.getTextureManager().release(movingId);movingTexture=null;}paintOverlay.invalidate();}
    private void cancelGesture(){tracing=false;trace.clear();moveHeld=false;panning=false;if(canvas!=null&&canvas.painting()){canvas.cancel();localDirty=true;}if(moving!=null){releaseMove();updateLocal(canvas.image);}if(palette!=null)palette.closeMenu();}
    private PaintRegion.Point point(double x,double y){return new PaintRegion.Point(imageX(x),imageY(y));}
    private void tracePoint(double x,double y){var point=point(x,y);if(trace.isEmpty()||Math.hypot(point.x()-trace.getLast().x(),point.y()-trace.getLast().y())>=.35){if(trace.size()>32768){var reduced=new ArrayList<PaintRegion.Point>();for(int i=0;i<trace.size();i+=2)reduced.add(trace.get(i));trace.clear();trace.addAll(reduced);}trace.add(point);}}
    @Override public boolean mouseClicked(double x,double y,int button){if(paintingTask||tracing||moving!=null||canvas!=null&&canvas.painting())return true;
        if(mode==2&&palette!=null&&palette.intercept(x,y,button))return true;
        if(mode==2&&super.mouseClicked(x,y,button))return true;
        if(!busy&&inside(x,y)&&mode==2&&canvas!=null){setFocused(null);if(button==2){cancelGesture();panning=true;return true;}var p=point(x,y);if(p.x()<0||p.y()<0||p.x()>=canvas.image.getWidth()||p.y()>=canvas.image.getHeight())return true;
            if(button==1&&mask!=null&&!mask.isEmpty()&&canvas.selected((int)p.x(),(int)p.y())){if(owned>=limits.count()){status=text("error.quota");return true;}moveHeld=true;startMoveX=p.x();startMoveY=p.y();canvasTask(()->canvas.prepareMove(),result->{if(!moveHeld||result==null)return;moving=result;moveDx=moveDy=0;updateLocal(result.background());movingTexture=new DynamicTexture(result.foreground().getWidth(),result.foreground().getHeight(),true);var pixels=movingTexture.getPixels();for(int iy=0;iy<result.foreground().getHeight();iy++)for(int ix=0;ix<result.foreground().getWidth();ix++){int c=result.foreground().getRGB(ix,iy);pixels.setPixelRGBA(ix,iy,(c&0xff00ff00)|(c>>>16&255)|((c&255)<<16));}minecraft.getTextureManager().register(movingId,movingTexture);movingTexture.upload();});return true;}
            if(button==0){if(!tool.selecting()&&owned>=limits.count()){status=text("error.quota");return true;}if(tool==PaintToolPalette.Tool.BRUSH){strokeRevision=canvas.imageRevision();canvas.begin(p.x(),p.y(),diameter,color,opacity,erase);localDirty=true;}else{tracing=true;trace.clear();trace.add(p);}return true;}}
        if(!busy&&mode==3&&button==0&&inside(x,y)){int index=(int)((y-py)/(galleryH/3f))*3+(int)((x-px)/(galleryW/3f));if(index<entries.size()){selected=entries.get(index);long now=System.currentTimeMillis();boolean doubleClick=selected.id().equals(lastClickedImage)&&now-lastImageClick<350;lastClickedImage=selected.id();lastImageClick=now;rebuildWidgets();if(doubleClick)minecraft.setScreen(new ImageDialogs.Preview(minecraft.screen,selected,token,pos));}return true;}
        return mode==2?false:super.mouseClicked(x,y,button);
    }
    @Override public boolean mouseDragged(double x,double y,int button,double dx,double dy){if(mode==2&&canvas!=null&&!busy){if(panning&&button==2){panX+=dx;panY+=dy;return true;}if(moving!=null&&button==1){moveDx=(int)Math.round(imageX(x)-startMoveX);moveDy=(int)Math.round(imageY(y)-startMoveY);return true;}if(button==0&&tracing){tracePoint(x,y);return true;}if(button==0&&canvas.painting()){canvas.drag(imageX(Math.clamp(x,px,px+side)),imageY(Math.clamp(y,py,py+side)));localDirty=true;return true;}}return super.mouseDragged(x,y,button,dx,dy);}
    @Override public boolean mouseReleased(double x,double y,int button){if(button==2)panning=false;if(button==1){moveHeld=false;if(moving!=null&&!busy){moveDx=(int)Math.round(imageX(x)-startMoveX);moveDy=(int)Math.round(imageY(y)-startMoveY);var move=moving;int dx=moveDx,dy=moveDy;long revision=canvas.imageRevision();canvasTask(()->{canvas.move(move,dx,dy);return true;},changed->{releaseMove();dirty|=revision!=canvas.imageRevision();updateLocal(canvas.image);refreshMask();});return true;}}
        if(button==0&&canvas!=null&&!busy){if(canvas.painting()){canvasTask(()->{canvas.end();return true;},changed->{dirty|=canvas.imageRevision()!=strokeRevision;updatePaintTexture();});return true;}if(tracing){tracePoint(x,y);tracing=false;var points=List.copyOf(trace);trace.clear();var activeTool=tool;int tint=color,alpha=opacity;boolean clearing=erase;editCanvas(()->{var region=activeTool==PaintToolPalette.Tool.RECTANGLE?PaintRegion.rectangle(canvas.image.getWidth(),canvas.image.getHeight(),points.getFirst(),points.getLast()):PaintRegion.polygon(canvas.image.getWidth(),canvas.image.getHeight(),points);if(activeTool.selecting()){if(!region.isEmpty())canvas.select(region,clearing);}else canvas.fill(region,tint,alpha,clearing);return true;});return true;}}
        return super.mouseReleased(x,y,button);
    }
    @Override public boolean mouseScrolled(double x,double y,double dx,double dy){if(mode==2&&canvas!=null&&!busy){if(palette!=null&&palette.menuOpen())return true;if(tracing||moving!=null||canvas.painting())return true;if(x>=rx&&x<rx+cw&&y>=py&&y<paintBottom){paintScroll-=(int)(dy*18);positionPaintControls();return true;}if(inside(x,y)){viewZoom=Math.clamp(viewZoom*(float)Math.pow(1.2,dy),.25f,64);return true;}}return super.mouseScrolled(x,y,dx,dy);}
    @Override public boolean keyPressed(int key,int scan,int mods){if(paintingTask){if(key==GLFW.GLFW_KEY_ESCAPE)moveHeld=false;return true;}if(mode==2&&key==GLFW.GLFW_KEY_ESCAPE&&(tracing||moving!=null||canvas!=null&&canvas.painting()||palette!=null&&palette.menuOpen())){cancelGesture();return true;}if(mode==2&&!busy&&!(getFocused() instanceof EditBox)&&hasControlDown()&&key==GLFW.GLFW_KEY_Z){cancelGesture();undo();return true;}return super.keyPressed(key,scan,mods);}
    public static int[] previewBounds(int x,int y,int size,float width,float height){
        int w=width>=height?size:Math.max(1,Math.round(size*width/height));
        int h=height>=width?size:Math.max(1,Math.round(size*height/width));
        return new int[]{x+(size-w)/2,y+(size-h)/2,w,h};
    }
    private void drawDisplay(GuiGraphics g,int x,int y,int size){
        checker(g,x,y,size,size);float w=kind==0?120:144*(columns-.25f),h=kind==0?60:144*(rows-.25f);int[] area=previewBounds(x,y,size,w,h);int tx=area[0],ty=area[1],sw=area[2],sh=area[3];g.fill(tx,ty,tx+(int)sw,ty+(int)sh,0xff111b20);g.enableScissor(tx,ty,tx+(int)sw,ty+(int)sh);
        var texture=ImageTextures.get(selection.id(),false,token,pos);if(texture!=null){g.pose().pushPose();g.pose().translate(tx,ty,0);ImageRender.quad(g.pose(),g.bufferSource(),texture.location(),selection.layout(texture.width(),texture.height(),sw,sh));g.flush();g.pose().popPose();}g.flush();g.disableScissor();g.renderOutline(tx,ty,Math.max(1,(int)sw),Math.max(1,(int)sh),0xff819a9a);
    }
    private void drawBrush(GuiGraphics g,int mouseX,int mouseY){
        if(canvas==null||busy||tool!=PaintToolPalette.Tool.BRUSH||moving!=null||palette!=null&&palette.menuOpen()||!inside(mouseX,mouseY)||diameter<1)return;
        float radius=Math.max(.5f,diameter*canvasScale()/2);int tint=erase?0x22ff7777:0x2275d7ac;
        for(int y=Math.max(py,(int)(mouseY-radius));y<Math.min(py+side,mouseY+radius);y++){float dy=y+.5f-mouseY;float extent=(float)Math.sqrt(Math.max(0,radius*radius-dy*dy));int x0=Math.max(px,(int)(mouseX-extent)),x1=Math.min(px+side,(int)(mouseX+extent+1));if(x1>x0)g.fill(x0,y,x1,y+1,tint);}
        int steps=Math.min(2048,Math.max(16,(int)(radius*7)));for(int i=0;i<steps;i++){double angle=i*Math.PI*2/steps;int x=(int)Math.round(mouseX+Math.cos(angle)*radius),y=(int)Math.round(mouseY+Math.sin(angle)*radius);g.fill(x,y,x+1,y+1,0xffefffff);}
    }
    private void drawPaintLabels(GuiGraphics g){g.enableScissor(rx,py,rx+cw,paintBottom);g.drawString(font,text("diameter"),rx,py+51-paintScroll,tool==PaintToolPalette.Tool.BRUSH?0xffdce5e2:0xff788b83,false);g.drawString(font,text("opacity"),rx,py+90-paintScroll,tool.selecting()?0xff788b83:0xffdce5e2,false);g.fill(rx+2,py+125-paintScroll,rx+cw-2,py+130-paintScroll,0xff000000|color);g.disableScissor();int content=133,visible=paintBottom-py;if(visible<content){int thumb=Math.max(12,visible*visible/content),offset=paintScroll*(visible-thumb)/Math.max(1,content-visible);g.fill(rx+cw+2,py,rx+cw+3,paintBottom,0xff435a50);g.fill(rx+cw+1,py+offset,rx+cw+4,py+offset+thumb,0xff91c7b0);}}
    private void drawTrace(GuiGraphics g,float scale){if(!tracing||trace.isEmpty())return;var points=new ArrayList<>(trace);if(tool==PaintToolPalette.Tool.RECTANGLE){var a=points.getFirst();var b=points.getLast();points=new ArrayList<>(List.of(a,new PaintRegion.Point(b.x(),a.y()),b,new PaintRegion.Point(a.x(),b.y())));}float tx=px+(side-canvas.image.getWidth()*scale)/2+panX,ty=py+(side-canvas.image.getHeight()*scale)/2+panY;for(int i=0;i<points.size();i++){var a=points.get(i);var b=points.get((i+1)%points.size());PaintOverlay.line(g,Math.clamp(Math.round(tx+(float)a.x()*scale),px-1,px+side),Math.clamp(Math.round(ty+(float)a.y()*scale),py-1,py+side),Math.clamp(Math.round(tx+(float)b.x()*scale),px-1,px+side),Math.clamp(Math.round(ty+(float)b.y()*scale),py-1,py+side),erase?0xffffb3a9:0xffa5f0cf);}}
    private void range(GuiGraphics g,int min,int max,int y){g.drawString(font,Integer.toString(min),rx,y,0xffb6c4c2,false);String end=Integer.toString(max);g.drawString(font,end,rx+cw-font.width(end),y,0xffb6c4c2,false);}
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta){
        g.fill(0,0,width,height,0xb0000000);
        if(mode!=0&&baseLayout!=null){int[] b=baseLayout;g.fill(b[0],b[1],b[0]+b[2],b[1]+b[3],0xff25292e);drawDisplay(g,b[4],b[5],b[6]);for(var widget:baseWidgets)widget.render(g,-1,-1,delta);g.flush();g.fill(0,0,width,height,0xaa000000);}
        g.pose().pushPose();g.pose().translate(0,0,mode==0?0:150);ImageSkin.box(g,left,top,panelW,panelH,5);
        Component heading=text(new String[]{"display_title","upload","paint","library"}[mode]);g.drawCenteredString(font,heading,width/2,top+11,0xfff0f5f3);
        if(mode==0||mode==1){String subtitle=mode==0?(displaying==null?text("no_image").getString():text("author",displaying.ownerName()).getString()):(uploadImage==null?text("no_image").getString():uploadName);g.drawCenteredString(font,font.plainSubstrByWidth(subtitle,panelW-24),width/2,top+29,0xffb6c9c5);}
        if(mode==0){drawDisplay(g,px,py,side);g.drawString(font,text("scale"),rx,py,0xffdce5e2,false);range(g,limits.minScale(),limits.maxScale(),py+34);g.drawString(font,text("scale_mode"),rx,py+46,0xffdce5e2,false);}
        else{
            checker(g,px,py,mode==3?galleryW:side,mode==3?galleryH:side);g.enableScissor(px,py,px+(mode==3?galleryW:side),py+(mode==3?galleryH:side));
            if(mode==1&&uploadImage!=null){if(localDirty)updateLocal(uploadImage);g.pose().pushPose();g.pose().translate(px,py,0);ImageRender.quad(g.pose(),g.bufferSource(),localId,ImageSelection.EMPTY.layout(uploadImage.getWidth(),uploadImage.getHeight(),side,side));g.flush();g.pose().popPose();}
            else if(mode==2&&canvas!=null){if(localDirty)updatePaintTexture();float scale=canvasScale(),w=canvas.image.getWidth()*scale,h=canvas.image.getHeight()*scale;g.pose().pushPose();g.pose().translate(px+(side-w)/2+panX,py+(side-h)/2+panY,0);ImageRender.quad(g.pose(),g.bufferSource(),localId,new ImageSelection.Rect(0,0,w,h,0,0,1,1));g.flush();g.pose().popPose();g.renderOutline(Math.round(px+(side-w)/2+panX),Math.round(py+(side-h)/2+panY),Math.max(1,Math.round(w)),Math.max(1,Math.round(h)),0xffd2e4df);if(moving!=null&&movingTexture!=null){var b=moving.bounds();int cx=Math.round(px+(side-w)/2+panX),cy=Math.round(py+(side-h)/2+panY);g.enableScissor(cx,cy,cx+Math.round(w),cy+Math.round(h));g.pose().pushPose();g.pose().translate(px+(side-w)/2+panX+(b[0]+moveDx)*scale,py+(side-h)/2+panY+(b[1]+moveDy)*scale,0);ImageRender.quad(g.pose(),g.bufferSource(),movingId,new ImageSelection.Rect(0,0,b[2]*scale,b[3]*scale,0,0,1,1));g.flush();g.pose().popPose();g.disableScissor();}paintOverlay.render(g,canvas,mask,px,py,side,scale,px+(side-w)/2+panX,py+(side-h)/2+panY,moveDx,moveDy);drawTrace(g,scale);drawBrush(g,mouseX,mouseY);}
            else if(mode==3){for(int i=0;i<entries.size();i++){var entry=entries.get(i);int cell=galleryW/3,cellH=galleryH/3,x=px+i%3*cell,y=py+i/3*cellH;var texture=ImageTextures.get(entry.id(),true,token,pos);if(texture!=null){g.pose().pushPose();g.pose().translate(x+2,y+2,0);ImageRender.quad(g.pose(),g.bufferSource(),texture.location(),ImageSelection.EMPTY.layout(texture.width(),texture.height(),cell-4,cellH-14));g.flush();g.pose().popPose();}g.drawString(font,font.plainSubstrByWidth(entry.name(),cell-4),x+2,y+cellH-11,0xffeeeeee,false);if(selected!=null&&selected.id().equals(entry.id()))g.renderOutline(x,y,cell,cellH,0xff75d7ac);}}
            g.flush();g.disableScissor();
            if(mode==2){g.drawString(font,text("owned_short",owned,limits.count()),lx,py+108,owned>=limits.count()?0xffffaaaa:0xffdce5e2,false);drawPaintLabels(g);g.drawString(font,canvas==null?text("no_canvas"):Component.literal(canvas.image.getWidth()+" × "+canvas.image.getHeight()+" px"),lx,py+48,0xffdce5e2,false);}
            if(mode==3){String who=!limits.admin()||filter.isBlank()?text("own_images").getString():filter.equals("*")?text("all_players").getString():filter;g.drawString(font,font.plainSubstrByWidth(who,cw),lx,py,0xffc4d3d0,false);int infoY=py+39,infoH=Math.max(28,top+panelH-111-infoY);g.renderOutline(lx,infoY,cw,infoH,0xff697a7c);if(selected!=null){String[] info={selected.name(),selected.width()+" × "+selected.height()+" px",selected.ownerName()};for(int i=0;i<info.length&&i*10+13<infoH;i++)g.drawString(font,font.plainSubstrByWidth(info[i],cw-8),lx+4,infoY+4+i*10,0xffdce5e2,false);}else g.drawString(font,font.plainSubstrByWidth(text("no_image").getString(),cw-8),lx+4,infoY+4,0xffb3c3c0,false);}
        }
        g.renderOutline(px-1,py-1,(mode==3?galleryW:side)+2,(mode==3?galleryH:side)+2,0xff758c8c);
        Component hint=status.getString().isEmpty()?(mode==3?text("page_quota",page+1,pages,owned,limits.count()):mode==2?text("paint_hint"):text("quota",owned,limits.count())):status;
        g.drawString(font,font.plainSubstrByWidth(hint.getString(),mode==2?panelW-cw-24:panelW-20),left+10,top+panelH-41,0xffc4d3d0,false);
        super.render(g,mouseX,mouseY,delta);if(mode==2&&palette!=null)palette.renderMenu(g,mouseX,mouseY,width,height);g.flush();g.pose().popPose();
    }
    public static void checker(GuiGraphics g,int x,int y,int w,int h){for(int dy=0;dy<h;dy+=8)for(int dx=0;dx<w;dx+=8)g.fill(x+dx,y+dy,x+Math.min(w,dx+8),y+Math.min(h,dy+8),((dx+dy)/8%2==0)?0xff304048:0xff45535a);}
    private void finishClose(){ImageNetwork.sendClient(ImageNetwork.CLOSE,token,new CompoundTag(),new byte[0]);dispose();minecraft.setScreen(null);}
    @Override public void onClose(){if(paintingTask)return;cancelGesture();if(busy)cancelTask();if(mode!=0){guard(()->{mode=0;showCurrent();},false);return;}finishClose();}
    public void dispose(){if(!paintingTask)cancelGesture();disposed=true;epoch++;if(canvas!=null&&!paintingTask)canvas.close();if(localTexture!=null){net.minecraft.client.Minecraft.getInstance().getTextureManager().release(localId);localTexture=null;}if(ImageClient.active==this)ImageClient.active=null;}
    @Override public void tick(){super.tick();if(mode==2&&!paintingTask&&!minecraft.isWindowActive())cancelGesture();if(busy&&!paintingTask&&(uploadRequest!=null||copyRequestId!=null||applyRequest!=null)&&System.currentTimeMillis()-transferStarted>90000){cancelTask();status=text("error.timeout");}}
    @Override public boolean isPauseScreen(){return false;}
    @Override public void renderBackground(GuiGraphics g,int x,int y,float delta){}
    private static final class LeaveDrawing extends Screen{
        final ImageScreen parent;final Runnable next;LeaveDrawing(ImageScreen parent,Runnable next){super(text("unsaved"));this.parent=parent;this.next=next;}
        @Override protected void init(){int x=width/2-150,y=height/2;addRenderableWidget(Button.builder(text("save_image"),b->{parent.showCurrent();parent.afterSaved=next;parent.upload(true);}).bounds(x,y,96,20).build()).active=parent.owned<parent.limits.count();addRenderableWidget(Button.builder(text("discard"),b->{parent.dirty=false;parent.showCurrent();next.run();}).bounds(x+102,y,96,20).build());addRenderableWidget(Button.builder(text("back"),b->onClose()).bounds(x+204,y,96,20).build());}
        @Override public void render(GuiGraphics g,int x,int y,float delta){g.fill(0,0,width,height,0xff25292e);g.drawCenteredString(font,title,width/2,height/2-24,0xffffffff);super.render(g,x,y,delta);}
        @Override public void onClose(){parent.showCurrent();}
        @Override public void renderBackground(GuiGraphics g,int x,int y,float d){}
    }
}










