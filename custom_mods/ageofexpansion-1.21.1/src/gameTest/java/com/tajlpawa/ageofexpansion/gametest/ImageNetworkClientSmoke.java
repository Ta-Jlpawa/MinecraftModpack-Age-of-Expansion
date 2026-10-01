package com.tajlpawa.ageofexpansion.gametest;
import com.tajlpawa.ageofexpansion.display.*;
import com.tajlpawa.ageofexpansion.image.*;
import com.tajlpawa.ageofexpansion.image.client.*;
import java.nio.file.*;
import java.util.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid="ageofexpansion",value=Dist.CLIENT)
public final class ImageNetworkClientSmoke {
    private static final Map<UUID,Long> receivedBytes=new HashMap<>();private static long warmBytes,corruptBytes;private static int sourceWidth,sourceHeight;private static boolean jpegFixture;private static String receivedCodec;private static String contentHash;private static long previewAt;private static boolean ownersSeen;private static int baselineOwned;private static int phase;private static long start;private static ImageScreen screen;private static UUID image;
    private static Object get(String name)throws Exception{var f=ImageScreen.class.getDeclaredField(name);f.setAccessible(true);return f.get(screen);}
    private static void set(String name,Object value)throws Exception{var f=ImageScreen.class.getDeclaredField(name);f.setAccessible(true);f.set(screen,value);}
    private static void call(String name)throws Exception{var m=ImageScreen.class.getDeclaredMethod(name);m.setAccessible(true);m.invoke(screen);}
    private static void invoke(String name,Class<?> type,Object value)throws Exception{var m=ImageScreen.class.getDeclaredMethod(name,type);m.setAccessible(true);m.invoke(screen,value);}
    @SubscribeEvent public static void tick(ClientTickEvent.Post event)throws Exception{
        if(!Boolean.getBoolean("ageofexpansion.imageNetworkClient"))return;var mc=Minecraft.getInstance();if(mc.getOverlay()!=null)return;
        if(start!=0&&System.currentTimeMillis()-start>110000)throw new AssertionError("Network smoke timeout phase="+phase);
        if(phase==0&&mc.screen instanceof TitleScreen){var original=ImageNetwork.clientReceiver;ImageNetwork.clientReceiver=packet->{if(packet.action()==ImageNetwork.GET&&packet.data().hasUUID("id")&&!packet.data().getBoolean("thumb")){receivedBytes.merge(packet.data().getUUID("id"),(long)packet.bytes().length,Long::sum);receivedCodec=packet.data().getCompound("variant").getString("codec");}original.accept(packet);};start=System.currentTimeMillis();if(Boolean.getBoolean("ageofexpansion.imageSingleplayer"))mc.createWorldOpenFlows().openWorld("ImageGuiFix",()->{throw new AssertionError("Cannot open isolated integrated world");});else ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:25586"),new ServerData("Image integration","127.0.0.1:25586",ServerData.Type.OTHER),false,null);phase=1;return;}
        if(phase==1&&mc.screen instanceof ImageScreen editor){screen=editor;baselineOwned=(int)get("owned");var pixels=new java.awt.image.BufferedImage(128,128,java.awt.image.BufferedImage.TYPE_INT_ARGB);var random=new Random(123);for(int y=0;y<128;y++)for(int x=0;x<128;x++)pixels.setRGB(x,y,0xff000000|random.nextInt(0x1000000));byte[] png=Files.exists(Path.of("fixture.png"))?Files.readAllBytes(Path.of("fixture.png")):ImageFiles.png(pixels);jpegFixture=Files.exists(Path.of("jpeg-fixture.flag"));if(jpegFixture){var photo=new java.awt.image.BufferedImage(768,768,java.awt.image.BufferedImage.TYPE_INT_RGB);for(int y=0;y<768;y++)for(int x=0;x<768;x++)photo.setRGB(x,y,random.nextInt(0x1000000));var output=new java.io.ByteArrayOutputStream();javax.imageio.ImageIO.write(photo,"jpeg",output);png=output.toByteArray();}var canonical=ImageFiles.canonicalPixels(png,true);sourceWidth=canonical.getWidth();sourceHeight=canonical.getHeight();contentHash=ImageFiles.pixelHash(canonical);ImageTextures.clear();ImageDiskCache.discard(contentHash);if(png.length<=ImageNetwork.PART)throw new AssertionError("Fixture must span multiple network frames");Path file=Path.of("联网上传测试.png").toAbsolutePath();ImageFiles.atomicWrite(file,png);invoke("navigate",int.class,1);set("path",file.toString());call("previewFile");phase=2;return;}
        if(phase==400&&mc.screen==null&&System.currentTimeMillis()-previewAt>1000){try(var shot=Screenshot.takeScreenshot(mc.getMainRenderTarget())){shot.writeToFile(Path.of("world-large.png"));int sum=0;for(int y=-5;y<=5;y++)for(int x=-5;x<=5;x++){int color=shot.getPixelRGBA(shot.getWidth()/2+x,(int)(shot.getHeight()*.42)+y);sum+=net.minecraft.util.FastColor.ABGR32.red(color)+net.minecraft.util.FastColor.ABGR32.green(color)+net.minecraft.util.FastColor.ABGR32.blue(color);}if(sum/363<60)throw new AssertionError("World image is still black while lit");}mc.gameMode.useItemOn(mc.player,net.minecraft.world.InteractionHand.MAIN_HAND,new net.minecraft.world.phys.BlockHitResult(ImageNetworkServerSmoke.POS.getCenter(),net.minecraft.core.Direction.NORTH,ImageNetworkServerSmoke.POS,false));phase=40;return;}
        if(phase==4&&mc.screen==null&&mc.level.getBlockEntity(ImageNetworkServerSmoke.POS) instanceof DisplayBlockEntity be&&be.image().id().equals(image)){
            if(be.image().horizontal()!=0||be.image().vertical()!=2)throw new AssertionError("Mouse-selected alignment not applied");
            mc.player.setXRot(27);previewAt=System.currentTimeMillis();phase=400;return;
        }
        if(phase==40&&mc.screen instanceof ImageScreen editor){screen=editor;invoke("navigate",int.class,3);screen.requestOwners(mc.player.getUUID().toString(),0,5,n->{var owners=n.getList("owners",net.minecraft.nbt.Tag.TAG_COMPOUND);if(owners.size()!=1||!ImageLibrary.Owner.load(owners.getCompound(0)).id().equals(mc.player.getUUID()))throw new AssertionError("Owner picker did not receive authenticated server results");ownersSeen=true;});phase=41;return;}
        if(phase==410&&mc.screen instanceof ImageDialogs.Preview){
            var texture=ImageTextures.get(image,false,screen.token,ImageNetworkServerSmoke.POS);
            if(System.currentTimeMillis()-previewAt>12000&&texture==null)throw new AssertionError("Cold full image never reached preview texture");
            if(System.currentTimeMillis()-previewAt>1500&&texture!=null){try(var shot=Screenshot.takeScreenshot(mc.getMainRenderTarget())){shot.writeToFile(Path.of("cold-preview.png"));}if(receivedBytes.getOrDefault(image,0L)!=warmBytes)throw new AssertionError("Warm full image unnecessarily downloaded");com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.info("IMAGE_V2_WARM_CACHE_PASSED additional_full_bytes=0 cold_full_bytes={}",warmBytes);mc.screen.onClose();phase=412;}return;
        }
        if(phase==412){
            if(Boolean.getBoolean("ageofexpansion.imageSingleplayer")){if(warmBytes!=0)throw new AssertionError("Integrated client duplicated authoritative image download");phase=411;return;}
            if(warmBytes==0)throw new AssertionError("Dedicated cold fixture never downloaded");if(jpegFixture&&!"jpeg".equals(receivedCodec))throw new AssertionError("JPEG fixture was unexpectedly transcoded");corruptBytes=receivedBytes.getOrDefault(image,0L);ImageTextures.clear();Path blob=ImageStorage.root().resolve("downloads/v2/objects").resolve(contentHash+".blob");byte[] damaged=Files.readAllBytes(blob);damaged[damaged.length/2]^=1;Files.write(blob,damaged);previewAt=System.currentTimeMillis();phase=413;return;
        }
        if(phase==413){var texture=ImageTextures.get(image,false,screen.token,ImageNetworkServerSmoke.POS);if(texture==null)return;if(receivedBytes.getOrDefault(image,0L)<=corruptBytes)throw new AssertionError("Damaged cache was not fetched again");com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.info("IMAGE_V2_CORRUPT_CACHE_PASSED refetched_bytes={}",receivedBytes.get(image)-corruptBytes);phase=411;return;}
        if(screen==null||!(mc.screen instanceof ImageScreen||mc.screen instanceof ImagePanelScreen)||(boolean)get("busy"))return;
        if(phase==41){
            @SuppressWarnings("unchecked") var entries=(List<ImageLibrary.Entry>)get("entries");if(entries.stream().noneMatch(e->e.id().equals(image)))return;
            warmBytes=receivedBytes.getOrDefault(image,0L);ImageTextures.clear();int index=0;while(!entries.get(index).id().equals(image))index++;int cell=(int)get("galleryW")/3,cellH=(int)get("galleryH")/3;int x=(int)get("px")+index%3*cell+5,y=(int)get("py")+index/3*cellH+5;mc.screen.mouseClicked(x,y,0);mc.screen.mouseClicked(x,y,0);
            if(!(mc.screen instanceof ImageDialogs.Preview))throw new AssertionError("Preview did not open");previewAt=System.currentTimeMillis();phase=410;return;
        }
        if(phase==411){if(!ownersSeen)return;
            @SuppressWarnings("unchecked") var entries=(List<ImageLibrary.Entry>)get("entries");var entry=entries.stream().filter(e->e.id().equals(image)).findFirst().orElse(null);if(entry==null)return;set("selected",entry);
            var rebuild=Screen.class.getDeclaredMethod("rebuildWidgets");rebuild.setAccessible(true);rebuild.invoke(screen);
            for(var child:screen.children())if(child instanceof Button b&&b.getMessage().getString().equals(ImageScreen.text("apply").getString())){mc.screen.mouseClicked(b.getX()+b.getWidth()/2.0,b.getY()+9,0);break;}phase=42;return;
        }
        if(phase==42){if((int)get("mode")!=0||!((ImageSelection)get("selection")).id().equals(image))throw new AssertionError("Library Apply failed");invoke("navigate",int.class,3);phase=5;return;}
        if(phase==2&&get("uploadImage")!=null){invoke("upload",boolean.class,false);phase=3;}
        else if(phase==3&&((ImageSelection)get("selection")).present()){
            image=((ImageSelection)get("selection")).id();var tex=ImageTextures.get(image,false,screen.token,ImageNetworkServerSmoke.POS);if(tex==null)return;if(tex.width()!=sourceWidth||tex.height()!=sourceHeight)throw new AssertionError("Downloaded image changed dimensions");((ImageSlider)get("scaleSlider")).setIntValue(25);
            for(String field:new String[]{"horizontalButtons","verticalButtons"}){@SuppressWarnings("unchecked") var buttons=(java.util.List<Button>)get(field);var b=buttons.get(field.equals("horizontalButtons")?0:2);screen.mouseClicked(b.getX()+b.getWidth()/2.0,b.getY()+9,0);screen.mouseReleased(b.getX()+b.getWidth()/2.0,b.getY()+9,0);}
            if(((ImageSelection)get("selection")).horizontal()!=0||((ImageSelection)get("selection")).vertical()!=2)throw new AssertionError("Mouse alignment click ignored");
            ((ImageSlider)get("scaleSlider")).setIntValue(400);try(var shot=Screenshot.takeScreenshot(mc.getMainRenderTarget())){shot.writeToFile(Path.of("applied-preview.png"));}((Button)get("applyButton")).onPress();phase=4;
        }else if(phase==4&&mc.level.getBlockEntity(ImageNetworkServerSmoke.POS) instanceof DisplayBlockEntity be&&be.image().id().equals(image)){
            if(be.image().scale()!=400||!be.getBlockState().getValue(DisplayBlock.LIT))throw new AssertionError("Applied state failed to synchronize");invoke("navigate",int.class,3);phase=5;
        }else if(phase==5){@SuppressWarnings("unchecked") var list=(List<ImageLibrary.Entry>)get("entries");var entry=list.stream().filter(e->e.id().equals(image)).findFirst().orElse(null);if(entry==null)return;set("selected",entry);set("renameName","Network renamed");invoke("manage",int.class,ImageNetwork.RENAME);phase=6;
        }else if(phase==6){@SuppressWarnings("unchecked") var list=(List<ImageLibrary.Entry>)get("entries");var entry=list.stream().filter(e->e.id().equals(image)&&e.name().equals("Network renamed")).findFirst().orElse(null);if(entry==null)return;set("selected",entry);invoke("manage",int.class,ImageNetwork.DELETE);phase=7;
        }else if(phase==7&&mc.level.getBlockEntity(ImageNetworkServerSmoke.POS) instanceof DisplayBlockEntity be&&!be.image().present()&&!be.getBlockState().getValue(DisplayBlock.LIT)){
            if((int)get("owned")!=baselineOwned)return;com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.info("IMAGE_NETWORK_E2E_PASSED: multi-frame upload/download, main and library apply, mouse alignment, BE synchronization, paginated listing, rename, delete and lighting clear");mc.stop();phase=8;
        }
    }
}


