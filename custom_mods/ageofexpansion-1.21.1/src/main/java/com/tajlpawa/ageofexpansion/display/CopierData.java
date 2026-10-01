package com.tajlpawa.ageofexpansion.display;

import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

// 复制器携带不可变快照ID；大文本只存服务端，避免物品栏同步巨型NBT。
public final class CopierData extends SavedData {
    private final Map<UUID,DisplayDocument> snapshots=new LinkedHashMap<>();
    public static CopierData get(ServerLevel level){
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(CopierData::new,CopierData::load,null),"ageofexpansion_copier_data");
    }
    public UUID store(DisplayDocument document){
        for(var entry:snapshots.entrySet())if(entry.getValue().equals(document))return entry.getKey();
        UUID id=UUID.randomUUID();snapshots.put(id,document);setDirty();return id;
    }
    public DisplayDocument document(UUID id){return snapshots.get(id);}
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider registries){
        ListTag list=new ListTag();snapshots.forEach((id,doc)->{CompoundTag entry=new CompoundTag();entry.putUUID("id",id);entry.put("document",doc.save());list.add(entry);});
        tag.put("snapshots",list);return tag;
    }
    public static CopierData load(CompoundTag tag,HolderLookup.Provider registries){
        CopierData data=new CopierData();ListTag list=tag.getList("snapshots",Tag.TAG_COMPOUND);
        for(int i=0;i<list.size();i++){CompoundTag entry=list.getCompound(i);if(entry.hasUUID("id"))data.snapshots.put(entry.getUUID("id"),DisplayDocument.load(entry.getCompound("document")));}
        return data;
    }
}
