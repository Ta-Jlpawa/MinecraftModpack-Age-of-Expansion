package com.tajlpawa.ageofexpansion.image.client;
import com.tajlpawa.ageofexpansion.image.*;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="ageofexpansion",value=Dist.CLIENT)
public final class ImageClient {
    static ImageScreen active;
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{
        event.registerShader(new net.minecraft.client.renderer.ShaderInstance(event.getResourceProvider(),net.minecraft.resources.ResourceLocation.parse("ageofexpansion:image_display"),com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY),shader->ImageRenderType.shader=shader);
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent e){e.enqueueWork(()->{ImageNetwork.clientProgress=ImageTextures::progress;ImageNetwork.clientReceiver=p->{
        ImageTextures.receive(p);if(p.action()==ImageNetwork.OPEN){if(p.data().hasUUID("scope"))ImageTextures.scope(p.data().getUUID("scope"));if(active!=null)active.dispose();active=new ImageScreen(p.token(),p.data());Minecraft.getInstance().setScreen(active);}
        else if(p.action()==ImageNetwork.GET){if(active!=null)active.receive(p);}
        else if(p.action()==ImageNetwork.INVALIDATE){if(p.data().hasUUID("id"))ImageTextures.invalidate(p.data().getUUID("id"));if(active!=null)active.receive(p);}
        else if(active!=null&&active.token.equals(p.token()))active.receive(p);
    };});}
    @SubscribeEvent public static void tick(ClientTickEvent.Post e){ImageTextures.tick();if(Minecraft.getInstance().getConnection()!=null)ImageNetwork.clientTick();}
    @SubscribeEvent public static void upload(RenderFrameEvent.Pre e){ImageTextures.uploadFrame();}
    @SubscribeEvent public static void frame(RenderLevelStageEvent e){if(e.getStage()==RenderLevelStageEvent.Stage.AFTER_SKY)ImageRender.beginFrame();}
    @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut e){if(active!=null)active.dispose();active=null;ImageTextures.clear();ImageNetwork.clearClient();}
    private ImageClient(){}
}
