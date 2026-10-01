package com.tajlpawa.ageofexpansion.gametest;

import com.tajlpawa.ageofexpansion.image.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.*;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder("ageofexpansion")
public final class ImagePipelineTests {
    @GameTestGenerator public static Collection<TestFunction> tests(){var list=new ArrayList<TestFunction>();
        add(list,"pixel_codecs",ImagePipelineTests::codecs);add(list,"jpeg_metadata",ImagePipelineTests::jpeg);add(list,"content_dedup",ImagePipelineTests::dedup);add(list,"shared_reads",ImagePipelineTests::shared);add(list,"migration_recovery",ImagePipelineTests::migration);add(list,"maintenance_references",ImagePipelineTests::maintenance);return list;}
    private static void add(List<TestFunction> tests,String name,java.util.function.Consumer<GameTestHelper> action){tests.add(new TestFunction("ageofexpansion:pipeline","ageofexpansion:image_v2_"+name,"ageofexpansion:module_empty",10000,0,true,action));}
    private static Path directory(){return ImageStorage.root().resolve("pipeline-tests").resolve(UUID.randomUUID().toString());}
    private static BufferedImage picture(boolean alpha){var image=new BufferedImage(128,128,BufferedImage.TYPE_INT_ARGB);for(int y=0;y<128;y++)for(int x=0;x<128;x++)image.setRGB(x,y,(alpha?(x*2)<<24:0xff000000)|((x/8)*16)<<16|((y/8)*16)<<8|0x56);return image;}
    private static void codecs(GameTestHelper h){try{
        for(var source:List.of(picture(false),picture(true),new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB))){byte[] png=ImageFiles.png(source);var prepared=ImageFiles.prepare(png,false);h.assertTrue(ImageFiles.pixelHash(ImageFiles.verify(prepared.asset().full(),prepared.full())).equals(ImageFiles.pixelHash(source)),"Lossless encoding preserves every RGBA channel");h.assertTrue(prepared.full().length<=png.length,"PNG optimizer never increases encoded size");h.assertTrue(prepared.asset().visible()==ImageFiles.visible(source),"Opacity visibility unchanged");}
        boolean invalid=false;try{new ImageAsset.Variant("../bad","0".repeat(64),"png",16,16,100);}catch(IllegalArgumentException e){invalid=true;}h.assertTrue(invalid,"Invalid content paths rejected");h.succeed();
    }catch(Exception e){throw new RuntimeException(e);}}
    private static void jpeg(GameTestHelper h){try{
        var source=new BufferedImage(256,256,BufferedImage.TYPE_INT_RGB);var random=new Random(24);for(int y=0;y<256;y++)for(int x=0;x<256;x++)source.setRGB(x,y,random.nextInt());var encoded=new ByteArrayOutputStream();javax.imageio.ImageIO.write(source,"jpeg",encoded);byte[] jpeg=encoded.toByteArray();
        var decorated=new ByteArrayOutputStream();decorated.write(jpeg,0,2);byte[] secret="Exif\0\0PRIVATE-GPS-FIXTURE".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);decorated.write(255);decorated.write(225);decorated.write((secret.length+2)>>8);decorated.write(secret.length+2);decorated.write(secret);decorated.write(jpeg,2,jpeg.length-2);
        var prepared=ImageFiles.prepare(decorated.toByteArray(),true);h.assertTrue(prepared.asset().full().codec().equals("jpeg"),"Photographic JPEG retained without recompression");h.assertTrue(!new String(prepared.full(),java.nio.charset.StandardCharsets.ISO_8859_1).contains("PRIVATE-GPS"),"EXIF removed from distributed JPEG");h.assertTrue(ImageFiles.pixelHash(ImageFiles.decode(decorated.toByteArray()).image()).equals(prepared.asset().full().pixels()),"Sanitized JPEG equals prior canonical pixels");
        var dir=directory();UUID id=UUID.randomUUID();var asset=ImageStore.put(dir,id,prepared);var fallback=ImageStore.fallback(dir,asset.full());h.assertTrue(fallback.codec().equals("png")&&fallback.pixels().equals(asset.full().pixels()),"JPEG has exact PNG fallback");ImageFiles.verify(fallback,ImageStore.read(dir,fallback));h.succeed();
    }catch(Exception e){throw new RuntimeException(e);}}
    private static void dedup(GameTestHelper h){try{
        var dir=directory();byte[] source=ImageFiles.png(picture(false));var prepared=ImageFiles.prepare(source,false);var ids=new ArrayList<UUID>();for(int i=0;i<100;i++){UUID id=UUID.randomUUID();ids.add(id);ImageStore.put(dir,id,prepared);}
        long files;try(var all=Files.list(dir.resolve("v2/objects"))){files=all.count();}h.assertTrue(files==2,"100 logical images share one full and one thumbnail object");
        ImageStore.retire(dir,ids.getFirst());var other=ImageStore.resolve(dir,List.of(),ids.getLast());ImageFiles.verify(other.full(),ImageStore.read(dir,other.full()));h.assertTrue(!Files.exists(ImageStore.ref(dir,ids.getFirst())),"Deleted alias retired without harming other references");
        var second=directory();var isolated=ImageStore.put(second,ids.getLast(),prepared);h.assertTrue(!ImageStore.blob(dir,other.full().digest()).equals(ImageStore.blob(second,isolated.full().digest())),"Independent libraries keep independent ownership");
        byte[] damaged=ImageStore.read(dir,other.full());damaged[damaged.length/2]^=1;Files.write(ImageStore.blob(dir,other.full().digest()),damaged);var repaired=ImageStore.put(dir,UUID.randomUUID(),ImageStore.prepare(dir,source,false));ImageFiles.verify(repaired.full(),ImageStore.read(dir,repaired.full()));h.assertTrue(repaired.full().pixels().equals(other.full().pixels()),"Reupload repairs same-length corrupt content without altering pixels");
        com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.info("IMAGE_V2_DEDUP_PASSED logical_images=100 content_objects={} raw_repeated_bytes={} shared_content_bytes={}",files,100L*(source.length+ImageFiles.png(ImageFiles.thumbnail(picture(false))).length),prepared.full().length+prepared.thumbnail().length);h.succeed();
    }catch(Exception e){throw new RuntimeException(e);}}
    private static void shared(GameTestHelper h){try{
        var dir=directory();var prepared=ImageFiles.prepare(ImageFiles.png(picture(true)),false);var ids=new ArrayList<UUID>();for(int i=0;i<16;i++){var id=UUID.randomUUID();ids.add(id);ImageStore.put(dir,id,prepared);}long before=ImageReads.diskReads;
        var requests=ids.stream().map(id->ImageReads.request(dir,List.of(),id,false,false,false)).toList();h.succeedWhen(()->{h.assertTrue(requests.stream().allMatch(CompletableFuture::isDone),"Parallel reads finished");var first=requests.getFirst().join().bytes();for(var request:requests)h.assertTrue(request.join().bytes()==first,"Concurrent subscribers share the same byte storage");h.assertTrue(ImageReads.diskReads-before==1,"One disk content read for sixteen logical aliases");});
    }catch(Exception e){throw new RuntimeException(e);}}
    private static void migration(GameTestHelper h){try{
        var old=directory();var target=directory();UUID id=UUID.randomUUID();byte[] source=ImageFiles.png(picture(true));ImageFiles.atomicWrite(ImageLibrary.file(old,id,false),source);
        var asset=ImageStore.resolve(target,List.of(old),id);h.assertTrue(Files.exists(ImageLibrary.file(old,id,false)),"Legacy source retained as backup");h.assertTrue(asset.full().pixels().equals(ImageFiles.pixelHash(picture(true))),"Migration retains exact pixels");
        Files.delete(ImageStore.ref(target,id));var recovered=ImageStore.resolve(target,List.of(old),id);h.assertTrue(recovered.full().equals(asset.full()),"Interrupted reference commit can be reconstructed");
        var clone=directory();var copied=ImageStore.resolve(clone,List.of(target),id);h.assertTrue(copied.full().equals(asset.full())&&Files.exists(ImageStore.blob(clone,copied.full().digest())),"Clone imports immutable content into independent namespace");h.succeed();
    }catch(Exception e){throw new RuntimeException(e);}}
    private static void maintenance(GameTestHelper h){try{
        var dir=directory();var legacy=directory();var saved=dir.resolve("world-index.dat");var prepared=ImageFiles.prepare(ImageFiles.png(picture(false)),false);UUID id=UUID.randomUUID();var asset=ImageStore.put(dir,id,prepared);ImageFiles.atomicWrite(ImageLibrary.file(legacy,id,false),ImageFiles.png(picture(false)));
        var report=ImageMaintenance.report(dir,legacy,saved,Set.of(id),Map.of(id,asset));h.assertTrue(report.candidates().stream().noneMatch(c->c.file().toString().endsWith(".blob")),"Live content is never a cleanup candidate");h.assertTrue(report.candidates().stream().anyMatch(c->c.file().equals(ImageLibrary.file(legacy,id,false))),"Verified migration backup is explicit cleanup candidate");
        ImageStore.retire(dir,id);for(var file:List.of(ImageStore.blob(dir,asset.full().digest()),ImageStore.blob(dir,asset.thumbnail().digest()),dir.resolve("v2/retired").resolve(id+".nbt")))Files.setLastModifiedTime(file,java.nio.file.attribute.FileTime.fromMillis(System.currentTimeMillis()-7200000));
        var data=new CompoundTag();var list=new ListTag();var entry=new CompoundTag();entry.putUUID("id",id);list.add(entry);data.put("images",list);var outer=new CompoundTag();outer.put("data",data);ImageStore.writeTag(saved,outer);
        var protectedReport=ImageMaintenance.report(dir,legacy,saved,Set.of(),Map.of());h.assertTrue(protectedReport.candidates().stream().noneMatch(c->c.file().toString().endsWith(".blob")),"Persisted world index pins references until deletion is saved");
        Files.delete(saved);var unreferenced=ImageMaintenance.report(dir,legacy,saved,Set.of(),Map.of());h.assertTrue(unreferenced.candidates().stream().anyMatch(c->c.file().toString().endsWith(".blob")),"Unreferenced old content is reclaimable");long removed=ImageMaintenance.clean(unreferenced,unreferenced);h.assertTrue(removed>0,"Explicit approved cleanup reclaims retired content");h.succeed();
    }catch(Exception e){throw new RuntimeException(e);}}
}
