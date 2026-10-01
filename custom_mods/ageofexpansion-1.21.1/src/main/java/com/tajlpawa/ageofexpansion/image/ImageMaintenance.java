package com.tajlpawa.ageofexpansion.image;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.nio.file.*;
import java.util.*;
import net.minecraft.commands.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

// 清理只接受只读报告中的候选，并再次检查引用、存档索引和文件指纹。
@EventBusSubscriber(modid="ageofexpansion")
public final class ImageMaintenance {
    public record Candidate(Path file,long size,long modified){}
    public record Report(Path directory,Set<String> retained,List<Candidate> candidates,long objectBytes,long legacyBytes){}
    private record Ticket(String owner,long created,Report report){}
    private static final Map<String,Ticket> tickets=new LinkedHashMap<>();
    @SubscribeEvent public static void register(RegisterCommandsEvent event){event.getDispatcher().register(Commands.literal("aoe_image_cache").requires(s->s.hasPermission(2))
        .then(Commands.literal("report").executes(c->{run(c.getSource(),null);return 1;}))
        .then(Commands.literal("clean").then(Commands.argument("report",StringArgumentType.word()).executes(c->{run(c.getSource(),StringArgumentType.getString(c,"report"));return 1;}))));}
    private static void run(CommandSourceStack source,String ticketId){
        var server=source.getServer();var level=source.getLevel();var lib=ImageLibrary.get(level);Path directory=ImageLibrary.directory(level),saved=server.getWorldPath(LevelResource.ROOT).resolve("data/ageofexpansion_images.dat"),legacy=ImageLibrary.legacyDirectory(level);
        Set<UUID> live=lib.ids();Map<UUID,ImageAsset> assets=new HashMap<>();for(var id:live)if(lib.asset(id)!=null)assets.put(id,lib.asset(id));
        Ticket ticket=ticketId==null?null:tickets.remove(ticketId);
        if(ticketId!=null&&(ticket==null||!ticket.owner.equals(source.getTextName())||System.currentTimeMillis()-ticket.created>600000||!ticket.report.directory.equals(directory))){source.sendFailure(Component.literal("图像缓存报告无效或已过期，请重新执行 report。"));return;}
        try{ImageFiles.WORK.execute(()->{try{
            Report report;long removed=0;
            synchronized(ImageStore.class){report=report(directory,legacy,saved,live,assets);if(ticket!=null)removed=clean(report,ticket.report);}
            long cleaned=removed;server.execute(()->{
                if(ticket!=null){source.sendSuccess(()->Component.literal("已清理 "+cleaned+" 字节；仍被引用、尚未安全落盘或来源不明的文件已保留。"),false);return;}
                String id=UUID.randomUUID().toString();tickets.entrySet().removeIf(e->System.currentTimeMillis()-e.getValue().created>600000);if(tickets.size()>=16)tickets.remove(tickets.keySet().iterator().next());tickets.put(id,new Ticket(source.getTextName(),System.currentTimeMillis(),report));long reclaim=report.candidates.stream().mapToLong(Candidate::size).sum();
                source.sendSuccess(()->Component.literal("图像缓存：内容 "+report.objectBytes+" 字节，当前世界旧PNG "+report.legacyBytes+" 字节，可安全清理 "+reclaim+" 字节。未核实的旧图库命名空间保留。确认清理：/aoe_image_cache clean "+id),false);
            });
        }catch(Exception e){server.execute(()->source.sendFailure(Component.literal("缓存维护未执行："+e.getMessage())));}});}catch(java.util.concurrent.RejectedExecutionException e){source.sendFailure(Component.literal("图像后台队列忙，请稍后重试。"));}
    }
    public static Report report(Path directory,Path legacy,Path saved,Set<UUID> live,Map<UUID,ImageAsset> current)throws Exception{
        synchronized(ImageStore.class){var ids=new HashSet<>(live);var protectedAssets=new ArrayList<>(current.values());
            if(Files.exists(saved)){var data=NbtIo.readCompressed(saved,NbtAccounter.create(32L*1024*1024)).getCompound("data");for(var tag:data.getList("images",Tag.TAG_COMPOUND))ids.add(((CompoundTag)tag).getUUID("id"));for(var tag:data.getList("content",Tag.TAG_COMPOUND))protectedAssets.add(ImageAsset.load((CompoundTag)tag));}
            var keep=new HashSet<String>();for(var asset:protectedAssets){keep.add(asset.full().digest());keep.add(asset.thumbnail().digest());}
            var candidates=new ArrayList<Candidate>();
            for(String folder:List.of("refs","retired")){Path refs=directory.resolve("v2/"+folder);if(Files.exists(refs))try(var paths=Files.list(refs)){for(var file:paths.filter(p->p.toString().endsWith(".nbt")).toList()){UUID id=UUID.fromString(file.getFileName().toString().replace(".nbt",""));var asset=ImageAsset.load(ImageStore.readTag(file));if(folder.equals("refs")||ids.contains(id)){keep.add(asset.full().digest());keep.add(asset.thumbnail().digest());}else if(oldEnough(file))candidates.add(candidate(file));}}}
            long objectBytes=0,legacyBytes=0;Path objects=directory.resolve("v2/objects");
            if(Files.exists(objects))try(var files=Files.list(objects)){for(var file:files.filter(p->p.getFileName().toString().matches("[0-9a-f]{64}\\.blob")).toList()){objectBytes+=Files.size(file);String digest=file.getFileName().toString().substring(0,64);if(!keep.contains(digest)&&oldEnough(file))candidates.add(candidate(file));}}
            if(Files.isDirectory(legacy))try(var files=Files.list(legacy)){for(var file:files.filter(p->p.toString().endsWith(".png")).toList()){
                legacyBytes+=Files.size(file);String name=file.getFileName().toString();boolean thumb=name.endsWith("_thumb.png");UUID id;try{id=UUID.fromString(name.replace("_thumb.png","").replace(".png",""));}catch(IllegalArgumentException e){continue;}
                Path ref=ImageStore.ref(directory,id);if(!Files.exists(ref))continue;var asset=ImageAsset.load(ImageStore.readTag(ref));var v=asset.variant(thumb);ImageFiles.verify(v,ImageStore.read(directory,v));if(ImageFiles.pixelHash(ImageFiles.decodePixels(ImageFiles.read(file,ImageFiles.MAX_BYTES))).equals(v.pixels()))candidates.add(candidate(file));
            }}
            return new Report(directory,Set.copyOf(keep),List.copyOf(candidates),objectBytes,legacyBytes);
        }
    }
    private static boolean oldEnough(Path file)throws Exception{return System.currentTimeMillis()-Files.getLastModifiedTime(file).toMillis()>3600000;}
    private static Candidate candidate(Path file)throws Exception{return new Candidate(file,Files.size(file),Files.getLastModifiedTime(file).toMillis());}
    public static long clean(Report now,Report approved)throws Exception{long removed=0;synchronized(ImageStore.class){var allowed=new HashSet<>(approved.candidates);for(var item:now.candidates)if(allowed.contains(item)&&Files.exists(item.file)&&Files.size(item.file)==item.size&&Files.getLastModifiedTime(item.file).toMillis()==item.modified){Path real=item.file.toRealPath();if(!real.equals(item.file.toAbsolutePath().normalize()))continue;if(Files.deleteIfExists(item.file))removed+=item.size;}}return removed;}
    public static void clear(){tickets.clear();}
    private ImageMaintenance(){}
}
