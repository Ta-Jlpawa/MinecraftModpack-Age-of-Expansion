package com.tajlpawa.ageofexpansion.display.client;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tajlpawa.ageofexpansion.display.*;
import java.util.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.world.phys.AABB;

public final class DisplayRenderer implements BlockEntityRenderer<DisplayBlockEntity>{
    private final Font font;
    private static final Set<UUID> RENDERED=new HashSet<>();
    private static final class Cached{
        DisplayDocument document;DisplayLayout layout;DisplayLayout.Job job;int width,height;
        Cached(DisplayDocument doc,int w,int h,boolean original){document=doc;width=w;height=h;job=new DisplayLayout.Job(doc,original?120:144*(w-.25f),original?60:144*(h-.25f),false);}
    }
    private final Map<Object,Cached> cache=new LinkedHashMap<>();
    public static void beginFrame(){RENDERED.clear();}
    public DisplayRenderer(BlockEntityRendererProvider.Context context){font=context.getFont();}
    @Override public void render(DisplayBlockEntity display,float partialTick,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        if(!display.getBlockState().getValue(DisplayBlock.LIT))return;
        if(display.imageScreen()){com.tajlpawa.ageofexpansion.image.client.ImageRender.render(display,pose,buffers);return;}
        boolean module=display.kind()==2;
        if(module){DisplayNetwork.requestView(display);if(!RENDERED.add(display.screenId))return;}
        DisplayDocument document=display.document();if(!document.hasText())return;
        Object key=module?display.screenId:display;
        Cached cached=cache.get(key);int w=module?display.screenWidth:1,h=module?display.screenHeight:1;
        if(cached==null||cached.document!=document||cached.width!=w||cached.height!=h){
            cached=new Cached(document,w,h,display.kind()==0);cache.put(key,cached);if(cache.size()>128)cache.remove(cache.keySet().iterator().next());
        }
        if(cached.layout==null){cached.layout=cached.job.step(font,8192);if(cached.layout==null)return;cached.job=null;}
        DisplayLayout layout=cached.layout;
        pose.pushPose();
        if(module){
            pose.translate(display.origin.getX()-display.getBlockPos().getX(),display.origin.getY()-display.getBlockPos().getY(),display.origin.getZ()-display.getBlockPos().getZ());
        }
        pose.translate(.5,display.kind()==0?8.66665/16:.5,.5);
        pose.mulPose(Axis.YP.rotationDegrees(-display.getBlockState().getValue(DisplayBlock.FACING).toYRot()));
        pose.translate(module?(w-1)/2.0:0,module?-(h-1)/2.0:0,-.3748);
        float unit=1f/144;
        // 模块整屏只留外缘边框；两个轴分别按实际尺寸映射。
        float unitY=unit;
        pose.scale(unit,-unitY,unit);pose.translate(-layout.width()/2,-layout.height()/2,0);
        for(int row=0;row<layout.lines().size();row++){
            var line=layout.lines().get(row);
            pose.pushPose();pose.translate(layout.drawX(line),layout.drawY(row),0);pose.scale(layout.scale(),layout.scale(),layout.scale());
            font.drawInBatch(layout.sequence(line),0,0,0xffffffff,false,pose.last().pose(),buffers,Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);
            pose.popPose();
        }
        pose.popPose();
    }
    @Override public AABB getRenderBoundingBox(DisplayBlockEntity be){
        if(be.kind()!=2)return new AABB(be.getBlockPos());
        var facing=be.getBlockState().getValue(DisplayBlock.FACING);
        var end=ModuleScreens.position(be.origin,facing,be.screenWidth-1,be.screenHeight-1);
        return new AABB(be.origin).minmax(new AABB(end));
    }
    @Override public int getViewDistance(){return 96;}
}



