package com.tajlpawa.ageofexpansion.image.client;
import com.mojang.blaze3d.platform.NativeImage;
import com.tajlpawa.ageofexpansion.AgeofExpansion;
import com.tajlpawa.ageofexpansion.image.*;
import java.awt.image.BufferedImage;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.MemoryUtil;

// 别名、下载和GPU纹理分离；每份像素内容只有一个加载任务和纹理。
public final class ImageTextures {
    public record Key(UUID id,boolean thumb){}
    public record Texture(ResourceLocation location,int width,int height){}
    private static final class Demand {
        final Key key;final int epoch;final UUID scope;UUID token,request,requestToken;BlockPos pos;ImageAsset.Variant variant;
        long used=System.currentTimeMillis(),progress=used,retry;int failures;boolean loading,aliasChecked,fallback;
        Demand(Key key,UUID token,BlockPos pos){this.key=key;this.token=token;this.pos=pos;epoch=generation;scope=currentScope;}
    }
    private record Ready(Demand demand,ImageAsset.Variant variant,NativeImage pixels){}
    private static final LinkedHashMap<String,Texture> CACHE=new LinkedHashMap<>(16,.75f,true);
    private static final LinkedHashMap<Key,Demand> demands=new LinkedHashMap<>(16,.75f,true);
    private static final Map<UUID,Demand> requests=new HashMap<>();
    private static final Map<String,Demand> loading=new HashMap<>();
    private static final ArrayDeque<Ready> ready=new ArrayDeque<>();
    private static int generation;private static UUID currentScope=ImageSelection.NONE;private static long bytes,pendingBytes,lastDiskFlush;
    public static long decodedImages,uploadedTextures;
    public static void scope(UUID value){if(!currentScope.equals(value)){clear();currentScope=value;}}
    private static boolean current(Demand d){return d.epoch==generation&&demands.get(d.key)==d;}
    public static Texture get(UUID id,boolean thumb,UUID token,BlockPos pos){
        if(id.equals(ImageSelection.NONE))return null;var key=new Key(id,thumb);var d=demands.get(key);if(d==null){d=new Demand(key,token,pos);demands.put(key,d);}d.used=System.currentTimeMillis();d.token=token;d.pos=pos;
        if(d.variant!=null){var texture=CACHE.get(d.variant.pixels());if(texture!=null)return texture;}
        if(!d.loading&&d.request==null&&d.used>=d.retry&&ImageNetwork.clientQueueSize()<64){
            if(!d.aliasChecked){d.loading=true;Demand work=d;submit(d,()->{var alias=ImageDiskCache.alias(work.scope,work.key.id,work.key.thumb,null);Minecraft.getInstance().execute(()->{if(!current(work))return;work.variant=alias;work.aliasChecked=true;work.loading=false;start(work);});});}
            else start(d);
        }return null;
    }
    private static void start(Demand d){
        if(!current(d))return;if(d.variant==null){send(d,true);return;}if(CACHE.containsKey(d.variant.pixels())||loading.containsKey(d.variant.pixels()))return;
        var variant=d.variant;d.loading=true;loading.put(variant.pixels(),d);submit(d,()->{
            var cached=ImageDiskCache.read(d.scope,variant);if(cached==null){ImageDiskCache.migrateLegacy(d.scope,d.key.id,d.key.thumb,variant);cached=ImageDiskCache.read(d.scope,variant);}
            if(cached==null){Minecraft.getInstance().execute(()->{if(current(d))send(d,false);});return;}
            try{decode(d,cached.variant(),cached.bytes(),false);}catch(Exception e){ImageDiskCache.discard(variant.pixels());Minecraft.getInstance().execute(()->{if(current(d)){d.fallback=variant.codec().equals("jpeg");send(d,false);}});}
        });
    }
    private static void send(Demand d,boolean describe){
        if(!current(d))return;var n=new CompoundTag();n.putUUID("id",d.key.id);n.putBoolean("thumb",d.key.thumb);n.putLong("pos",d.pos.asLong());n.putBoolean("describe",describe);n.putBoolean("fallback",d.fallback);
        d.loading=false;d.progress=System.currentTimeMillis();d.requestToken=d.token;d.request=ImageNetwork.sendClient(ImageNetwork.GET,d.token,n,new byte[0]);requests.put(d.request,d);
    }
    private interface Job{void run()throws Exception;}
    private static void submit(Demand d,Job job){try{ImageFiles.WORK.execute(()->{try{job.run();}catch(Exception e){Minecraft.getInstance().execute(()->fail(d,e));}});}catch(java.util.concurrent.RejectedExecutionException e){fail(d,e);}}
    private static void fail(Demand d,Exception e){if(!current(d))return;if(d.request!=null)requests.remove(d.request);d.request=null;d.loading=false;if(d.variant!=null)loading.remove(d.variant.pixels(),d);d.retry=System.currentTimeMillis()+Math.min(8000,500L<<Math.min(4,d.failures++));if(d.failures==1&&!(e instanceof java.util.concurrent.RejectedExecutionException))AgeofExpansion.LOGGER.warn("Image load will retry for {}: {}",d.key,e.toString());}
    public static void progress(UUID request){var d=requests.get(request);if(d!=null)d.progress=System.currentTimeMillis();}
    public static void receive(ImageNetwork.Packet packet){
        if(packet.action()==ImageNetwork.SCOPE){scope(packet.data().getUUID("scope"));return;}
        if(packet.action()==ImageNetwork.QUEUED){progress(packet.request());return;}
        if(packet.action()==ImageNetwork.ERROR){var d=requests.get(packet.request());if(d!=null)fail(d,new java.io.IOException(packet.data().getString("error")));return;}
        if(packet.action()!=ImageNetwork.META&&packet.action()!=ImageNetwork.GET)return;
        var n=packet.data();if(!n.hasUUID("id"))return;var key=new Key(n.getUUID("id"),n.getBoolean("thumb"));Demand d=requests.remove(packet.request());
        if(d==null||!current(d))return;if(!d.key.equals(key)||!d.requestToken.equals(packet.token())||n.hasUUID("scope")&&!d.scope.equals(n.getUUID("scope"))){fail(d,new java.io.IOException("image_response"));return;}d.request=null;d.aliasChecked=true;
        try{if(n.contains("variant"))d.variant=ImageAsset.Variant.load(n.getCompound("variant"));}catch(RuntimeException e){fail(d,e);return;}
        Demand work=d;var receivedVariant=d.variant;
        if(packet.action()==ImageNetwork.META){work.loading=true;submit(work,()->{ImageDiskCache.alias(work.scope,key.id,key.thumb,receivedVariant);Minecraft.getInstance().execute(()->{if(current(work)){work.loading=false;start(work);}});});return;}
        if(work.variant!=null&&CACHE.containsKey(work.variant.pixels())){work.loading=false;loading.remove(work.variant.pixels(),work);submit(work,()->ImageDiskCache.alias(work.scope,key.id,key.thumb,receivedVariant));return;}
        work.loading=true;if(work.variant!=null)loading.putIfAbsent(work.variant.pixels(),work);
        submit(work,()->{try{var variant=receivedVariant;if(variant==null){var pixels=ImageFiles.decodePixels(packet.bytes());variant=ImageFiles.describe(pixels,packet.bytes(),"png");}decode(work,variant,packet.bytes(),true);}catch(java.io.IOException e){if(receivedVariant!=null&&receivedVariant.codec().equals("jpeg")&&!work.fallback){Minecraft.getInstance().execute(()->{if(current(work)){work.fallback=true;send(work,false);}});}else throw e;}});
    }
    private static void decode(Demand d,ImageAsset.Variant variant,byte[] encoded,boolean persist)throws Exception{
        BufferedImage pixels=ImageFiles.verify(variant,encoded);synchronized(ImageTextures.class){decodedImages++;}
        if(persist)try{ImageDiskCache.put(d.scope,d.key.id,d.key.thumb,variant,encoded);}catch(java.io.IOException e){AgeofExpansion.LOGGER.warn("Could not save image cache",e);}
        var nativeImage=new NativeImage(pixels.getWidth(),pixels.getHeight(),false);
        try{for(int y=0;y<pixels.getHeight();y++)for(int x=0;x<pixels.getWidth();x++){int c=pixels.getRGB(x,y);nativeImage.setPixelRGBA(x,y,(c&0xff00ff00)|(c>>>16&255)|((c&255)<<16));}}
        catch(Throwable error){nativeImage.close();throw error;}
        Minecraft.getInstance().execute(()->{if(!current(d)){nativeImage.close();return;}d.variant=variant;long size=(long)variant.width()*variant.height()*4;
            if(pendingBytes+size>64L*1024*1024){nativeImage.close();fail(d,new java.util.concurrent.RejectedExecutionException("GPU upload backlog"));return;}ready.add(new Ready(d,variant,nativeImage));pendingBytes+=size;});
    }
    public static void uploadFrame(){
        int count=0;long uploaded=0;while(!ready.isEmpty()&&count<2){var next=ready.peek();long size=(long)next.variant.width()*next.variant.height()*4;if(count>0&&uploaded+size>8L*1024*1024)break;ready.remove();pendingBytes-=size;var d=next.demand;
            if(!current(d)){next.pixels.close();continue;}if(!CACHE.containsKey(next.variant.pixels()))put(next.variant,next.pixels);else next.pixels.close();d.loading=false;d.failures=0;loading.remove(next.variant.pixels(),d);count++;uploaded+=size;
        }
    }
    public static void tick(){
        long now=System.currentTimeMillis();var visible=new HashSet<String>();for(var d:demands.values())if(d.variant!=null&&now-d.used<5000)visible.add(d.variant.pixels());
        for(var d:List.copyOf(demands.values())){
            if(d.request!=null&&now-d.progress>30000){ImageNetwork.cancelClient(d.requestToken,d.request);fail(d,new java.io.IOException("timeout"));}
            if(d.request!=null&&now-d.used>5000&&(d.variant==null||!visible.contains(d.variant.pixels()))){ImageNetwork.cancelClient(d.requestToken,d.request);requests.remove(d.request);d.request=null;if(d.variant!=null)loading.remove(d.variant.pixels(),d);d.loading=false;}
        }
        if(demands.size()>4096)demands.entrySet().removeIf(e->demands.size()>4096&&e.getValue().request==null&&!e.getValue().loading&&now-e.getValue().used>5000);
        if(now-lastDiskFlush>60000){lastDiskFlush=now;try{ImageFiles.WORK.execute(()->{try{ImageDiskCache.flush(false);}catch(java.io.IOException ignored){}});}catch(java.util.concurrent.RejectedExecutionException ignored){}}
    }
    private static void put(ImageAsset.Variant variant,NativeImage pixels){var mc=Minecraft.getInstance();var id=ResourceLocation.parse("ageofexpansion:runtime/content_"+variant.pixels());mc.getTextureManager().register(id,new DynamicTexture(pixels));CACHE.put(variant.pixels(),new Texture(id,variant.width(),variant.height()));bytes+=(long)variant.width()*variant.height()*4;uploadedTextures++;while(bytes>128L*1024*1024||CACHE.size()>128)remove(CACHE.keySet().iterator().next());}
    private static void remove(String pixels){var old=CACHE.remove(pixels);if(old!=null){bytes-=(long)old.width*old.height*4;Minecraft.getInstance().getTextureManager().release(old.location);}}
    public static NativeImage readPng(byte[] png)throws java.io.IOException{var encoded=MemoryUtil.memAlloc(png.length);try{encoded.put(png).flip();return NativeImage.read(NativeImage.Format.RGBA,encoded);}finally{MemoryUtil.memFree(encoded);}}
    public static void invalidate(UUID id){for(var key:List.copyOf(demands.keySet()))if(key.id.equals(id)){var d=demands.remove(key);if(d.request!=null){requests.remove(d.request);ImageNetwork.cancelClient(d.requestToken,d.request);}if(d.variant!=null)loading.remove(d.variant.pixels(),d);}UUID scope=currentScope;try{ImageFiles.WORK.execute(()->{try{ImageDiskCache.invalidate(scope,id);}catch(java.io.IOException e){AgeofExpansion.LOGGER.warn("Could not remove image alias",e);}});}catch(java.util.concurrent.RejectedExecutionException ignored){}}
    public static void clear(){generation++;for(var pixels:List.copyOf(CACHE.keySet()))remove(pixels);for(var image:ready)image.pixels.close();ready.clear();pendingBytes=0;demands.clear();requests.clear();loading.clear();try{ImageFiles.WORK.execute(()->{try{ImageDiskCache.flush(true);}catch(java.io.IOException ignored){}});}catch(java.util.concurrent.RejectedExecutionException ignored){}}
    private ImageTextures(){}
}
