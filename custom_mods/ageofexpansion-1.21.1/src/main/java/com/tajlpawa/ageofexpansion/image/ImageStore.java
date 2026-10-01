package com.tajlpawa.ageofexpansion.image;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import net.minecraft.nbt.*;

// 原图内容不可变，引用文件原子提交；旧PNG保留作为迁移备份。
public final class ImageStore {
    public static Path blob(Path directory,String digest){if(!digest.matches("[0-9a-f]{64}"))throw new IllegalArgumentException("digest");return directory.resolve("v2/objects").resolve(digest+".blob");}
    public static Path ref(Path directory,UUID id){return directory.resolve("v2/refs").resolve(id+".nbt");}
    public static CompoundTag readTag(Path path)throws IOException{return NbtIo.readCompressed(new ByteArrayInputStream(ImageFiles.read(path,65536)),NbtAccounter.create(65536));}
    public static void writeTag(Path path,CompoundTag tag)throws IOException{var out=new ByteArrayOutputStream();NbtIo.writeCompressed(tag,out);ImageFiles.atomicWrite(path,out.toByteArray());}
    private static ImageAsset.Variant storeVariant(Path directory,ImageAsset.Variant variant,byte[] bytes)throws IOException{
        Path pointer=directory.resolve("v2/pixels").resolve(variant.pixels()+".nbt");
        if(Files.exists(pointer))try{var known=ImageAsset.Variant.load(readTag(pointer));var old=blob(directory,known.digest());if(known.pixels().equals(variant.pixels())&&Files.isRegularFile(old)&&Files.size(old)==known.length()&&ImageFiles.hash(ImageFiles.read(old,ImageFiles.MAX_BYTES)).equals(known.digest()))return known;}catch(Exception ignored){}
        Path path=blob(directory,variant.digest());if(!Files.exists(path)||Files.size(path)!=variant.length()||!ImageFiles.hash(ImageFiles.read(path,ImageFiles.MAX_BYTES)).equals(variant.digest()))ImageFiles.atomicWrite(path,bytes);
        writeTag(pointer,variant.save());return variant;
    }
    public static ImageFiles.Prepared prepare(Path directory,byte[] source,boolean uploaded)throws IOException{
        var pixels=ImageFiles.canonicalPixels(source,uploaded);String hash=ImageFiles.pixelHash(pixels);Path pointer=directory.resolve("v2/content").resolve(hash+".nbt");
        if(Files.exists(pointer))try{var asset=ImageAsset.load(readTag(pointer));if(asset.full().pixels().equals(hash)){byte[] full=read(directory,asset.full()),thumb=read(directory,asset.thumbnail());return new ImageFiles.Prepared(asset,full,thumb);}}catch(Exception ignored){}
        return ImageFiles.prepare(pixels,source,uploaded);
    }
    public static synchronized ImageAsset put(Path directory,UUID id,ImageFiles.Prepared prepared)throws IOException{
        var full=storeVariant(directory,prepared.asset().full(),prepared.full());var thumb=storeVariant(directory,prepared.asset().thumbnail(),prepared.thumbnail());var asset=new ImageAsset(full,thumb,prepared.asset().visible());writeTag(ref(directory,id),asset.save());writeTag(directory.resolve("v2/content").resolve(full.pixels()+".nbt"),asset.save());return asset;
    }
    public static ImageAsset resolve(Path directory,List<Path> legacy,UUID id)throws IOException{
        Path reference=ref(directory,id);if(Files.exists(reference))return ImageAsset.load(readTag(reference));
        var places=new ArrayList<Path>();places.add(directory);places.addAll(legacy);
        for(var source:places){Path oldRef=ref(source,id);if(!Files.exists(oldRef))oldRef=source.resolve("v2/retired").resolve(id+".nbt");
            if(Files.exists(oldRef)){var asset=ImageAsset.load(readTag(oldRef));byte[] full=read(source,asset.full()),thumb=read(source,asset.thumbnail());return put(directory,id,new ImageFiles.Prepared(asset,full,thumb));}
            Path old=ImageLibrary.file(source,id,false);if(Files.exists(old)){var prepared=ImageFiles.prepare(ImageFiles.read(old,ImageFiles.MAX_BYTES),false);return put(directory,id,prepared);}
        }throw new FileNotFoundException("image source missing: "+id);
    }
    public static byte[] read(Path directory,ImageAsset.Variant variant)throws IOException{var bytes=ImageFiles.read(blob(directory,variant.digest()),ImageFiles.MAX_BYTES);if(bytes.length!=variant.length()||!ImageFiles.hash(bytes).equals(variant.digest()))throw new IOException("image_integrity");return bytes;}
    public static synchronized void retire(Path directory,UUID id)throws IOException{
        Path source=ref(directory,id);if(Files.exists(source)){Path target=directory.resolve("v2/retired").resolve(id+".nbt");Files.createDirectories(target.getParent());Files.move(source,target,StandardCopyOption.REPLACE_EXISTING);}
    }
    public static ImageAsset.Variant fallback(Path directory,ImageAsset.Variant original)throws IOException{
        if(original.codec().equals("png"))return original;Path marker=directory.resolve("v2/fallback").resolve(original.pixels()+".nbt");
        if(Files.exists(marker)){var v=ImageAsset.Variant.load(readTag(marker));if(Files.exists(blob(directory,v.digest())))return v;}
        var pixels=ImageFiles.verify(original,read(directory,original));byte[] bytes=ImageFiles.compactPng(pixels);var variant=ImageFiles.describe(pixels,bytes,"png");ImageFiles.atomicWrite(blob(directory,variant.digest()),bytes);writeTag(marker,variant.save());return variant;
    }
    private ImageStore(){}
}
