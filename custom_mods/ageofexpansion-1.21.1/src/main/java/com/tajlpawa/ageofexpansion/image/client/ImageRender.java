package com.tajlpawa.ageofexpansion.image.client;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tajlpawa.ageofexpansion.display.*;
import com.tajlpawa.ageofexpansion.image.ImageSelection;
import java.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public final class ImageRender {
    private static final Set<UUID> DRAWN=new HashSet<>();
    public static void beginFrame(){DRAWN.clear();}
    public static void quad(PoseStack pose,MultiBufferSource buffers,ResourceLocation texture,ImageSelection.Rect r){
        quad(pose,buffers,texture,r,false);
    }
    private static void quad(PoseStack pose,MultiBufferSource buffers,ResourceLocation texture,ImageSelection.Rect r,boolean world){
        if(r.width()<=0||r.height()<=0)return;var v=buffers.getBuffer(world&&ImageShaderCompat.enabled()?RenderType.entityTranslucentEmissive(texture):ImageRenderType.get(texture));var m=pose.last().pose();float x=r.x(),y=r.y(),x1=x+r.width(),y1=y+r.height();
        v.addVertex(m,x,y,0).setColor(-1).setUv(r.u0(),r.v0()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0,0,1);
        v.addVertex(m,x,y1,0).setColor(-1).setUv(r.u0(),r.v1()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0,0,1);
        v.addVertex(m,x1,y1,0).setColor(-1).setUv(r.u1(),r.v1()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0,0,1);
        v.addVertex(m,x1,y,0).setColor(-1).setUv(r.u1(),r.v0()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0,0,1);
    }
    public static void render(DisplayBlockEntity be,PoseStack pose,MultiBufferSource buffers){
        if(ImageShaderCompat.shadow())return;var value=be.image();if(!value.present())return;boolean module=be.kind()==2;if(module&&!DRAWN.add(be.screenId))return;
        var texture=ImageTextures.get(value.id(),false,ImageSelection.NONE,be.getBlockPos());if(texture==null)return;
        int columns=module?be.screenWidth:1,rows=module?be.screenHeight:1;float w=be.kind()==0?120:144*(columns-.25f),h=be.kind()==0?60:144*(rows-.25f);
        pose.pushPose();if(module)pose.translate(be.origin.getX()-be.getBlockPos().getX(),be.origin.getY()-be.getBlockPos().getY(),be.origin.getZ()-be.getBlockPos().getZ());
        pose.translate(.5,be.kind()==0?8.66665/16:.5,.5);pose.mulPose(Axis.YP.rotationDegrees(-be.getBlockState().getValue(DisplayBlock.FACING).toYRot()));
        pose.translate(module?(columns-1)/2.0:0,module?-(rows-1)/2.0:0,-.3748);pose.scale(1f/144,-1f/144,1f/144);pose.translate(-w/2,-h/2,0);
        quad(pose,buffers,texture.location(),value.layout(texture.width(),texture.height(),w,h),true);pose.popPose();
    }
    private ImageRender(){}
}

