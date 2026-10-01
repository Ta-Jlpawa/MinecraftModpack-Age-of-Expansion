package com.tajlpawa.ageofexpansion.image.client;

import java.lang.reflect.Method;
import net.neoforged.fml.ModList;

// 可选 Iris API：世界图像进入光影认可的发光实体通道，GUI 仍使用自己的颜色着色器。
final class ImageShaderCompat {
    private static Object api;
    private static Method enabled,shadow;
    static {
        if(ModList.get().isLoaded("iris"))try{
            var type=Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            api=type.getMethod("getInstance").invoke(null);enabled=type.getMethod("isShaderPackInUse");shadow=type.getMethod("isRenderingShadowPass");
        }catch(ReflectiveOperationException e){com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.warn("Iris image compatibility API is unavailable",e);}
    }
    private static boolean query(Method method){if(api==null||method==null)return false;try{return (boolean)method.invoke(api);}catch(ReflectiveOperationException e){return false;}}
    static boolean enabled(){return query(enabled);}
    static boolean shadow(){return query(shadow);}
    private ImageShaderCompat(){}
}
