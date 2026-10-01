package com.tajlpawa.ageofexpansion.image;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import net.neoforged.fml.loading.FMLPaths;

// 原图、下载缓存和撤销暂存统一放在游戏根目录；下载缓存不能清理服务端原图。
public final class ImageStorage {
    public static Path root(){return FMLPaths.GAMEDIR.get().resolve("ageofexpansion_image_cache");}
    public static Path originals(UUID library){return root().resolve("originals").resolve(library.toString());}
    public static Path downloaded(UUID id,boolean thumb){return ImageLibrary.file(root().resolve("downloads"),id,thumb);}
    public static Path undoDirectory()throws IOException{var base=root().resolve("undo");Files.createDirectories(base);return Files.createTempDirectory(base,"canvas-");}
    public static byte[] readOriginal(Path directory,Path legacy,UUID id,boolean thumb)throws IOException{
        Path file=ImageLibrary.file(directory,id,thumb);
        if(Files.exists(file))return ImageFiles.read(file,ImageFiles.MAX_BYTES);
        // 旧存档按需复制，保留原文件作为迁移备份；只请求缩略图时也迁移完整图像。
        Path original=ImageLibrary.file(directory,id,false);Path old=Files.isRegularFile(original)?original:ImageLibrary.file(legacy,id,false);
        var decoded=ImageFiles.decode(ImageFiles.read(old,ImageFiles.MAX_BYTES));
        ImageFiles.atomicWrite(ImageLibrary.file(directory,id,false),decoded.png());
        ImageFiles.atomicWrite(ImageLibrary.file(directory,id,true),decoded.thumbnail());
        return thumb?decoded.thumbnail():decoded.png();
    }
    public static void trimDownloads()throws IOException{
        Path directory=root().resolve("downloads");if(!Files.isDirectory(directory))return;
        try(var stream=Files.list(directory)){
            var files=stream.filter(p->p.getFileName().toString().endsWith(".png")).sorted(Comparator.comparingLong(ImageStorage::modified)).toList();
            long size=0;for(var file:files)size+=Files.size(file);
            for(var file:files){if(size<=512L*1024*1024)break;long length=Files.size(file);if(Files.deleteIfExists(file))size-=length;}
        }
    }
    private static long modified(Path path){try{return Files.getLastModifiedTime(path).toMillis();}catch(IOException e){return 0;}}
    private ImageStorage(){}
}
