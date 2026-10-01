package com.tajlpawa.ageofexpansion.display;

import com.tajlpawa.ageofexpansion.Config;
import com.tajlpawa.ageofexpansion.image.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

// 拓扑索引包含未加载区块；只有真实放置/拆除才改变成员，全文每屏保存一次。
public final class ModuleScreens extends SavedData {
    public static final class Group {
        public UUID id;
        public BlockPos origin;
        public Direction facing;
        public boolean holographic,imageScreen;
        public ImageSelection image=ImageSelection.EMPTY;
        public Object content(){return imageScreen?image:document;}
        public boolean occupied(){return imageScreen?image.present():document.hasText();}
        public int width,height;
        public long revision;
        public DisplayDocument document;
        public final Set<BlockPos> members=new HashSet<>();
        Group(UUID id,BlockPos origin,Direction facing,int width,int height,DisplayDocument doc){
            this.id=id;this.origin=origin;this.facing=facing;this.width=width;this.height=height;this.document=doc;
        }
        public int capacity(){return members.size()*Config.perBlock();}
    }
    private final Map<BlockPos,Group> cells=new HashMap<>();
    private final Map<UUID,Group> groups=new HashMap<>();
    private final Set<BlockPos> dirty=new HashSet<>();
    private int oldX=-1,oldY=-1,oldCapacity=-1;
    public static ModuleScreens get(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(ModuleScreens::new,ModuleScreens::load,null),"ageofexpansion_module_screens");
    }
    public Group at(BlockPos pos){return cells.get(pos);}
    public static Direction right(Direction facing){return facing.getCounterClockWise();}
    public static BlockPos position(BlockPos origin,Direction facing,int x,int y){return origin.relative(right(facing),x).below(y);}
    public static int column(BlockPos pos,Direction facing){return pos.getX()*right(facing).getStepX()+pos.getZ()*right(facing).getStepZ();}
    public static Comparator<BlockPos> order(Direction facing){return Comparator.<BlockPos>comparingInt(BlockPos::getY).reversed().thenComparingInt(p->column(p,facing));}
    public void register(ServerLevel level,DisplayBlockEntity be){
        BlockPos pos=be.getBlockPos();Direction facing=be.getBlockState().getValue(DisplayBlock.FACING);
        Group existing=cells.get(pos);
        if(existing!=null&&(existing.facing!=facing||existing.holographic!=be.holographic()||existing.imageScreen!=be.imageScreen())){remove(level,pos);existing=null;}
        if(existing==null){
            Group group=new Group(UUID.randomUUID(),pos,facing,1,1,DisplayDocument.EMPTY);
            group.holographic=be.holographic();group.imageScreen=be.imageScreen();
            group.members.add(pos);cells.put(pos,group);groups.put(group.id,group);dirty.add(pos);setDirty();
        }else apply(level,existing,be);
    }
    public void remove(ServerLevel level,BlockPos pos){
        Group old=cells.remove(pos);if(old==null)return;
        old.members.remove(pos);dirty.addAll(old.members);
        // 真实拆除立即清空原组合，之后的矩形重组只处理空白数据。
        old.document=DisplayDocument.EMPTY;old.image=ImageSelection.EMPTY;old.revision++;
        for(BlockPos member:old.members)
            if(level.hasChunkAt(member)&&level.getBlockEntity(member) instanceof DisplayBlockEntity be)apply(level,old,be);
        for(Direction d:Direction.values())dirty.add(pos.relative(d));
        if(old.members.isEmpty())groups.remove(old.id);
        setDirty();
    }
    public void tick(ServerLevel level){
        if(oldX!=Config.sizeX()||oldY!=Config.sizeY()||oldCapacity!=Config.perBlock()){
            oldX=Config.sizeX();oldY=Config.sizeY();oldCapacity=Config.perBlock();dirty.addAll(cells.keySet());
        }
        if(dirty.isEmpty())return;
        List<BlockPos> seeds=new ArrayList<>(dirty);dirty.clear();Set<BlockPos> done=new HashSet<>();Set<UUID> claimed=new HashSet<>();
        seeds.sort(Comparator.<BlockPos>comparingInt(BlockPos::getY).reversed().thenComparingInt(p->cells.containsKey(p)?column(p,cells.get(p).facing):0));
        for(BlockPos seed:seeds){
            if(done.contains(seed)||!cells.containsKey(seed))continue;
            Group initial=cells.get(seed);Set<BlockPos> component=new HashSet<>();ArrayDeque<BlockPos> queue=new ArrayDeque<>();queue.add(seed);
            while(!queue.isEmpty()){
                BlockPos pos=queue.removeFirst();Group g=cells.get(pos);
                if(g==null||g.facing!=initial.facing||g.holographic!=initial.holographic||g.imageScreen!=initial.imageScreen||!component.add(pos))continue;
                for(Direction d:new Direction[]{Direction.UP,Direction.DOWN,right(g.facing),right(g.facing).getOpposite()})queue.add(pos.relative(d));
            }
            done.addAll(component);rebuild(level,component,initial.facing,claimed);
        }
    }
    private void rebuild(ServerLevel level,Set<BlockPos> component,Direction facing,Set<UUID> claimed){
        Map<BlockPos,Group> previous=new HashMap<>();for(BlockPos p:component)previous.put(p,cells.get(p));
        TreeSet<BlockPos> remaining=new TreeSet<>(order(facing));remaining.addAll(component);
        List<Group> result=new ArrayList<>();
        while(!remaining.isEmpty()){
            BlockPos origin=remaining.first();int bestW=1,bestH=1;
            for(int h=1;h<=Config.sizeY();h++)for(int w=1;w<=Config.sizeX();w++){
                if(w*h<bestW*bestH||(w*h==bestW*bestH&&w<=bestW))continue;
                Object reference=null;boolean valid=true;
                for(int y=0;y<h&&valid;y++)for(int x=0;x<w;x++){
                    BlockPos pos=position(origin,facing,x,y);
                    if(!remaining.contains(pos)){valid=false;break;}
                    Object doc=previous.get(pos).content();
                    if(previous.get(pos).occupied()){if(reference==null)reference=doc;else if(!reference.equals(doc)){valid=false;break;}}
                }
                if(valid){bestW=w;bestH=h;}
            }
            Set<BlockPos> members=new HashSet<>();Object doc=claimed.contains(previous.get(origin).id)?(previous.get(origin).imageScreen?ImageSelection.EMPTY:DisplayDocument.EMPTY):previous.get(origin).content();
            Set<UUID> sources=new HashSet<>();
            for(int y=0;y<bestH;y++)for(int x=0;x<bestW;x++){
                BlockPos p=position(origin,facing,x,y);members.add(p);remaining.remove(p);
                Group source=previous.get(p);
                if(!claimed.contains(source.id)&&source.occupied()){doc=source.content();sources.add(source.id);}
            }
            claimed.addAll(sources);
            claimed.add(previous.get(origin).id);
            Group old=previous.get(origin);
            Group next=old.members.equals(members)&&old.content().equals(doc)&&old.width==bestW&&old.height==bestH?old:
                new Group(UUID.randomUUID(),origin,facing,bestW,bestH,doc instanceof DisplayDocument d?d:DisplayDocument.EMPTY);
            next.holographic=old.holographic;next.imageScreen=old.imageScreen;next.image=doc instanceof ImageSelection selection?selection:ImageSelection.EMPTY;
            next.members.addAll(members);result.add(next);
        }
        for(Group old:new HashSet<>(previous.values()))groups.remove(old.id);
        for(Group group:result){
            groups.put(group.id,group);
            for(BlockPos p:group.members){
                cells.put(p,group);
                if(level.hasChunkAt(p)&&level.getBlockEntity(p) instanceof DisplayBlockEntity be)apply(level,group,be);
            }
        }
        setDirty();
    }
    private void apply(ServerLevel level,Group group,DisplayBlockEntity be){be.assign(group);}
    public void update(ServerLevel level,Group group,DisplayDocument document){
        group.document=document;group.revision++;setDirty();
        for(BlockPos p:group.members)if(level.hasChunkAt(p)&&level.getBlockEntity(p) instanceof DisplayBlockEntity be)apply(level,group,be);
        if(!document.hasText())dirty.addAll(group.members);
    }
    public void updateImage(ServerLevel level,Group group,ImageSelection value){group.image=value;group.revision++;setDirty();for(var p:group.members)if(level.hasChunkAt(p)&&level.getBlockEntity(p) instanceof DisplayBlockEntity be)apply(level,group,be);if(!value.present())dirty.addAll(group.members);}
    public void deleteImage(ServerLevel level,UUID id){for(var group:List.copyOf(groups.values()))if(group.imageScreen&&group.image.id().equals(id))updateImage(level,group,ImageSelection.EMPTY);}
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider registries){
        ListTag list=new ListTag();
        for(Group group:groups.values()){
            CompoundTag g=new CompoundTag();g.putUUID("id",group.id);g.putLong("origin",group.origin.asLong());
            g.putInt("facing",group.facing.get3DDataValue());g.putInt("width",group.width);g.putInt("height",group.height);
            g.putBoolean("holographic",group.holographic);g.putBoolean("imageScreen",group.imageScreen);if(group.imageScreen)g.put("image",group.image.save());
            g.putLong("revision",group.revision);g.put("document",group.document.save());
            g.putLongArray("members",group.members.stream().mapToLong(BlockPos::asLong).toArray());list.add(g);
        }
        tag.put("groups",list);return tag;
    }
    public static ModuleScreens load(CompoundTag tag,HolderLookup.Provider registries){
        ModuleScreens data=new ModuleScreens();ListTag list=tag.getList("groups",Tag.TAG_COMPOUND);
        for(int i=0;i<list.size();i++){
            CompoundTag g=list.getCompound(i);if(!g.hasUUID("id"))continue;
            Direction facing=Direction.from3DDataValue(g.getInt("facing"));if(!facing.getAxis().isHorizontal())continue;
            Group group=new Group(g.getUUID("id"),BlockPos.of(g.getLong("origin")),facing,Math.clamp(g.getInt("width"),1,10),Math.clamp(g.getInt("height"),1,10),DisplayDocument.load(g.getCompound("document")));
            group.revision=g.getLong("revision");
            group.holographic=g.getBoolean("holographic");group.imageScreen=g.getBoolean("imageScreen");group.image=ImageSelection.load(g.getCompound("image"));
            for(long p:g.getLongArray("members")){BlockPos pos=BlockPos.of(p);group.members.add(pos);data.cells.put(pos,group);}
            data.groups.put(group.id,group);
        }
        return data;
    }
}


