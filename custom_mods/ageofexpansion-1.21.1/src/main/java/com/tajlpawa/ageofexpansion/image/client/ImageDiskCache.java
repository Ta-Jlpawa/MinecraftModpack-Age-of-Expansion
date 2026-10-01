package com.tajlpawa.ageofexpansion.image.client;

import com.tajlpawa.ageofexpansion.image.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

// 下载对象按像素摘要去重；访问索引批量落盘，原图库不参加缓存淘汰。
public final class ImageDiskCache {
    public record Cached(ImageAsset.Variant variant,byte[] bytes){}
    private record Access(long size,long used){}
    private static final Map<String,Access> index=new HashMap<>();private static boolean initialized;private static long total,lastFlush;
    private static Path directory(){return ImageStorage.root().resolve("downloads/v2");}
    private static Path object(String pixels,String extension){if(!pixels.matches("[0-9a-f]{64}"))throw new IllegalArgumentException("pixels");return directory().resolve("objects").resolve(pixels+extension);}
    private static Path alias(UUID scope,UUID id,boolean thumb){return directory().resolve("aliases").resolve(scope.toString()).resolve(id+(thumb?"_thumb":"")+".nbt");}
    public static ImageAsset.Variant alias(UUID scope,UUID id,boolean thumb,ImageAsset.Variant value)throws IOException{Path file=alias(scope,id,thumb);if(value!=null){ImageStore.writeTag(file,value.save());return value;}if(!Files.exists(file))return null;try{return ImageAsset.Variant.load(ImageStore.readTag(file));}catch(Exception e){Files.deleteIfExists(file);return null;}}
    public static Cached read(UUID scope,ImageAsset.Variant desired)throws IOException{
        Path local=ImageStore.blob(ImageStorage.originals(scope),desired.digest());if(Files.isRegularFile(local)){byte[] bytes=ImageFiles.read(local,ImageFiles.MAX_BYTES);if(bytes.length==desired.length()&&ImageFiles.hash(bytes).equals(desired.digest()))return new Cached(desired,bytes);}
        synchronized(ImageDiskCache.class){initialize();Path meta=object(desired.pixels(),".nbt"),file=object(desired.pixels(),".blob");if(!Files.exists(meta)||!Files.exists(file))return null;
            try{var actual=ImageAsset.Variant.load(ImageStore.readTag(meta));byte[] bytes=ImageFiles.read(file,ImageFiles.MAX_BYTES);if(!actual.pixels().equals(desired.pixels())||bytes.length!=actual.length()||!ImageFiles.hash(bytes).equals(actual.digest()))throw new IOException("cache_integrity");index.put(actual.pixels(),new Access(bytes.length,System.currentTimeMillis()));flush(false);return new Cached(actual,bytes);}catch(Exception e){remove(desired.pixels());return null;}
        }
    }
    public static synchronized void put(UUID scope,UUID id,boolean thumb,ImageAsset.Variant value,byte[] bytes)throws IOException{
        initialize();alias(scope,id,thumb,value);Path authoritative=ImageStore.blob(ImageStorage.originals(scope),value.digest());
        if(Files.isRegularFile(authoritative)&&Files.size(authoritative)==value.length())return;
        var old=index.get(value.pixels());if(old==null||!Files.exists(object(value.pixels(),".blob"))){ImageFiles.atomicWrite(object(value.pixels(),".blob"),bytes);ImageStore.writeTag(object(value.pixels(),".nbt"),value.save());total+=bytes.length-(old==null?0:old.size());}
        index.put(value.pixels(),new Access(old==null?bytes.length:old.size(),System.currentTimeMillis()));trim();flush(false);
    }
    public static synchronized void migrateLegacy(UUID scope,UUID id,boolean thumb,ImageAsset.Variant desired)throws IOException{
        Path old=ImageStorage.downloaded(id,thumb);if(!Files.isRegularFile(old))return;byte[] bytes=ImageFiles.read(old,ImageFiles.MAX_BYTES);var pixels=ImageFiles.decodePixels(bytes);if(ImageFiles.pixelHash(pixels).equals(desired.pixels())){put(scope,id,thumb,ImageFiles.describe(pixels,bytes,"png"),bytes);Files.deleteIfExists(old);}
    }
    public static void invalidate(UUID scope,UUID id)throws IOException{Files.deleteIfExists(alias(scope,id,false));Files.deleteIfExists(alias(scope,id,true));Files.deleteIfExists(ImageStorage.downloaded(id,false));Files.deleteIfExists(ImageStorage.downloaded(id,true));}
    private static void initialize()throws IOException{
        if(initialized)return;Files.createDirectories(directory().resolve("objects"));var times=new Properties();Path path=directory().resolve("access.properties");if(Files.exists(path))try(var in=Files.newInputStream(path)){times.load(in);}catch(IOException ignored){}
        try(var files=Files.list(directory().resolve("objects"))){for(var file:files.filter(p->p.getFileName().toString().matches("[0-9a-f]{64}\\.blob")).toList()){String hash=file.getFileName().toString().substring(0,64);long size=Files.size(file),used=Files.getLastModifiedTime(file).toMillis();try{used=Long.parseLong(times.getProperty(hash,Long.toString(used)));}catch(NumberFormatException ignored){}index.put(hash,new Access(size,used));total+=size;}}
        initialized=true;trim();
    }
    private static void trim()throws IOException{if(total<=512L*1024*1024&&index.size()<=16384)return;var order=index.entrySet().stream().sorted(Comparator.comparingLong(e->e.getValue().used())).map(Map.Entry::getKey).toList();for(var key:order){if(total<=512L*1024*1024&&index.size()<=16384)break;remove(key);}}
    public static synchronized void discard(String pixels)throws IOException{initialize();remove(pixels);}
    private static void remove(String pixels)throws IOException{Files.deleteIfExists(object(pixels,".blob"));Files.deleteIfExists(object(pixels,".nbt"));var old=index.remove(pixels);if(old!=null)total-=old.size();}
    public static synchronized void flush(boolean force)throws IOException{long now=System.currentTimeMillis();if(!initialized||!force&&now-lastFlush<60000)return;var props=new Properties();index.forEach((key,value)->props.setProperty(key,Long.toString(value.used())));var bytes=new ByteArrayOutputStream();props.store(bytes,"Image cache access times");ImageFiles.atomicWrite(directory().resolve("access.properties"),bytes.toByteArray());lastFlush=now;}
    private ImageDiskCache(){}
}
