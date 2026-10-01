package com.tajlpawa.ageofexpansion.image;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

// 共享不可变读取结果；同图多人下载只做一次磁盘读取，发送仍分别授权。
public final class ImageReads {
    public record Reply(ImageAsset asset,ImageAsset.Variant variant,byte[] bytes){}
    private record AssetKey(Path directory,UUID id){}
    private record BlobKey(Path directory,String digest){}
    private static final Map<AssetKey,CompletableFuture<ImageAsset>> assets=new LinkedHashMap<>();
    private static final Map<BlobKey,CompletableFuture<byte[]>> pending=new HashMap<>();
    private static final LinkedHashMap<BlobKey,byte[]> cache=new LinkedHashMap<>(16,.75f,true);
    private static long cachedBytes;
    public static long diskReads;
    private static <T> CompletableFuture<T> work(Callable<T> task){try{return CompletableFuture.supplyAsync(()->{try{return task.call();}catch(Exception e){throw new CompletionException(e);}},ImageFiles.WORK);}catch(RejectedExecutionException e){return CompletableFuture.failedFuture(e);}}
    private static synchronized CompletableFuture<ImageAsset> asset(Path directory,List<Path> legacy,UUID id){
        var key=new AssetKey(directory,id);var old=assets.get(key);if(old!=null)return old;
        var future=work(()->ImageStore.resolve(directory,legacy,id));assets.put(key,future);future.whenComplete((value,error)->{if(error!=null)synchronized(ImageReads.class){assets.remove(key,future);}});
        if(assets.size()>2048)assets.entrySet().removeIf(e->assets.size()>2048&&e.getValue().isDone());return future;
    }
    private static synchronized CompletableFuture<byte[]> bytes(Path directory,ImageAsset.Variant variant){
        var key=new BlobKey(directory,variant.digest());var value=cache.get(key);if(value!=null)return CompletableFuture.completedFuture(value);var old=pending.get(key);if(old!=null)return old;
        var future=work(()->{synchronized(ImageReads.class){diskReads++;}return ImageStore.read(directory,variant);});pending.put(key,future);
        future.whenComplete((data,error)->{synchronized(ImageReads.class){if(!pending.remove(key,future))return;if(error==null){var previous=cache.put(key,data);cachedBytes+=data.length-(previous==null?0:previous.length);while(cachedBytes>32L*1024*1024&&!cache.isEmpty()){var first=cache.entrySet().iterator().next();cachedBytes-=first.getValue().length;cache.remove(first.getKey());}}}});return future;
    }
    public static CompletableFuture<Reply> request(Path directory,List<Path> legacy,UUID id,boolean thumb,boolean fallback,boolean metadata){
        return asset(directory,legacy,id).thenCompose(asset->{var original=asset.variant(thumb);var selected=fallback?work(()->ImageStore.fallback(directory,original)):CompletableFuture.completedFuture(original);
            return selected.thenCompose(variant->metadata?CompletableFuture.completedFuture(new Reply(asset,variant,new byte[0])):bytes(directory,variant).thenApply(data->new Reply(asset,variant,data)));});
    }
    public static synchronized void invalidate(Path directory,UUID id){assets.remove(new AssetKey(directory,id));}
    public static synchronized void clear(){assets.clear();pending.clear();cache.clear();cachedBytes=0;diskReads=0;}
    private ImageReads(){}
}
