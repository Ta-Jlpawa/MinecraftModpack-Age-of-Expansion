package com.tajlpawa.ageofexpansion.image.client;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

// 图像不接受方向光调暗，并保留低于10%的像素alpha。
public final class ImageRenderType extends RenderType {
    static ShaderInstance shader;
    private static final Map<ResourceLocation,RenderType> CACHE=new LinkedHashMap<>();
    private ImageRenderType(){super("image_display",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,1536,false,true,()->{},()->{});}
    public static RenderType get(ResourceLocation texture){
        var old=CACHE.get(texture);if(old!=null)return old;
        var state=CompositeState.builder().setShaderState(new ShaderStateShard(()->shader)).setTextureState(new TextureStateShard(texture,false,false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false);
        var result=create("ageofexpansion_image",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,1536,false,true,state);CACHE.put(texture,result);if(CACHE.size()>256)CACHE.remove(CACHE.keySet().iterator().next());return result;
    }
}

