package com.tajlpawa.ageofexpansion.image;
import com.tajlpawa.ageofexpansion.display.*;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelResource;

public final class ImageLibrary extends SavedData {
    public record Entry(UUID id,UUID owner,String ownerName,String name,int width,int height,boolean visible){
        public CompoundTag save(){var t=new CompoundTag();t.putUUID("id",id);t.putUUID("owner",owner);t.putString("ownerName",ownerName);t.putString("name",name);t.putInt("width",width);t.putInt("height",height);t.putBoolean("visible",visible);return t;}
        public static Entry load(CompoundTag t){return new Entry(t.getUUID("id"),t.getUUID("owner"),t.getString("ownerName"),t.getString("name"),t.getInt("width"),t.getInt("height"),t.getBoolean("visible"));}
    }
    private UUID storageId=UUID.randomUUID(),boundScope;
    private final Set<UUID> previousScopes=new LinkedHashSet<>();
    private final Map<UUID,ImageAsset> assets=new HashMap<>();
    private final Map<UUID,Entry> entries=new LinkedHashMap<>();
    private final Set<DisplayBlockEntity> loaded=Collections.newSetFromMap(new WeakHashMap<>());
    public static ImageLibrary get(ServerLevel level){return level.getServer().overworld().getDataStorage().computeIfAbsent(new Factory<>(ImageLibrary::new,ImageLibrary::load,null),"ageofexpansion_images");}
    public static UUID scope(ServerLevel level){var lib=get(level);UUID scope=UUID.nameUUIDFromBytes((lib.storageId+"|"+level.getServer().getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize()).getBytes(java.nio.charset.StandardCharsets.UTF_8));if(!scope.equals(lib.boundScope)){lib.previousScopes.add(lib.boundScope==null?lib.storageId:lib.boundScope);lib.boundScope=scope;lib.setDirty();}return scope;}
    public static Path directory(ServerLevel level){return ImageStorage.originals(scope(level));}
    public static List<Path> migrationDirectories(ServerLevel level){scope(level);var paths=new ArrayList<Path>();for(var id:get(level).previousScopes)paths.add(ImageStorage.originals(id));paths.add(legacyDirectory(level));return List.copyOf(paths);}
    public ImageAsset asset(UUID id){return assets.get(id);}
    public void attach(UUID id,ImageAsset asset){if(entries.containsKey(id)&&!asset.equals(assets.get(id))){assets.put(id,asset);setDirty();}}
    public Set<UUID> ids(){return Set.copyOf(entries.keySet());}
    public static Path legacyDirectory(ServerLevel level){return level.getServer().getWorldPath(LevelResource.ROOT).resolve("data/ageofexpansion_images");}
    public static Path file(Path directory,UUID id,boolean thumb){return directory.resolve(id+(thumb?"_thumb.png":".png"));}
    public Entry at(UUID id){return entries.get(id);}
    public boolean visible(ImageSelection value){var e=at(value.id());return e!=null&&e.visible;}
    public int count(UUID owner){return (int)entries.values().stream().filter(e->e.owner.equals(owner)).count();}
    public boolean canUse(ServerPlayer player,UUID id){var entry=at(id);return entry!=null&&(player.hasPermissions(2)||entry.owner.equals(player.getUUID()));}
    public List<Entry> list(ServerPlayer player,String filter){return entries.values().stream().filter(e->!player.hasPermissions(2)?e.owner.equals(player.getUUID()):filter.equals("*")||filter.isBlank()&&e.owner.equals(player.getUUID())||e.owner.toString().equalsIgnoreCase(filter)||e.ownerName.equalsIgnoreCase(filter)).sorted(Comparator.comparing(Entry::name).thenComparing(Entry::id)).toList();}
    public record Owner(UUID id,String name,int count){
        public CompoundTag save(){var n=new CompoundTag();n.putUUID("id",id);n.putString("name",name);n.putInt("count",count);return n;}
        public static Owner load(CompoundTag n){return new Owner(n.getUUID("id"),n.getString("name"),n.getInt("count"));}
    }
    public List<Owner> owners(ServerPlayer player,String query){
        if(!player.hasPermissions(2))return List.of();var owners=new LinkedHashMap<UUID,Owner>();
        for(var entry:entries.values()){var old=owners.get(entry.owner);owners.put(entry.owner,new Owner(entry.owner,entry.ownerName,old==null?1:old.count+1));}
        String search=query.strip().toLowerCase(Locale.ROOT);return owners.values().stream().filter(o->o.name.toLowerCase(Locale.ROOT).contains(search)||o.id.toString().contains(search)).sorted(Comparator.comparing(Owner::name).thenComparing(Owner::id)).toList();
    }
    public static String name(String name){String s=name.strip().replaceAll("[\\p{Cntrl}]","");return s.isEmpty()?"Image":s.substring(0,Math.min(64,s.length()));}
    public void add(Entry entry){entries.put(entry.id,entry);setDirty();}
    public void rename(UUID id,String name){var e=at(id);if(e!=null)add(new Entry(id,e.owner,e.ownerName,name(name),e.width,e.height,e.visible));}
    public void watch(DisplayBlockEntity be){loaded.add(be);}
    public void delete(ServerLevel level,UUID id){
        if(entries.remove(id)==null)return;assets.remove(id);setDirty();
        for(var dimension:level.getServer().getAllLevels())ModuleScreens.get(dimension).deleteImage(dimension,id);
        for(var be:List.copyOf(loaded))if(!be.isRemoved()&&be.image().id().equals(id))be.updateImage(ImageSelection.EMPTY);
        Path directory=directory(level);try{ImageFiles.WORK.execute(()->{try{ImageStore.retire(directory,id);}catch(java.io.IOException e){com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.warn("Could not retire image reference {}",id,e);}});}catch(java.util.concurrent.RejectedExecutionException ignored){}
    }
    @Override public CompoundTag save(CompoundTag t,HolderLookup.Provider provider){t.putUUID("storageId",storageId);if(boundScope!=null)t.putUUID("boundScope",boundScope);var scopes=new ListTag();previousScopes.forEach(id->scopes.add(StringTag.valueOf(id.toString())));t.put("previousScopes",scopes);var content=new ListTag();assets.forEach((id,asset)->{var n=asset.save();n.putUUID("id",id);content.add(n);});t.put("content",content);var list=new ListTag();entries.values().forEach(e->list.add(e.save()));t.put("images",list);return t;}
    public static ImageLibrary load(CompoundTag t,HolderLookup.Provider provider){var library=new ImageLibrary();if(t.hasUUID("storageId"))library.storageId=t.getUUID("storageId");else library.setDirty();if(t.hasUUID("boundScope"))library.boundScope=t.getUUID("boundScope");for(var scope:t.getList("previousScopes",Tag.TAG_STRING))try{library.previousScopes.add(UUID.fromString(scope.getAsString()));}catch(IllegalArgumentException ignored){}for(var tag:t.getList("content",Tag.TAG_COMPOUND))try{var n=(CompoundTag)tag;library.assets.put(n.getUUID("id"),ImageAsset.load(n));}catch(RuntimeException ignored){}for(var value:t.getList("images",Tag.TAG_COMPOUND))try{var e=Entry.load((CompoundTag)value);if(e.width>0&&e.height>0&&e.width<=2048&&e.height<=2048)library.entries.put(e.id,e);}catch(RuntimeException ignored){}return library;}
}
