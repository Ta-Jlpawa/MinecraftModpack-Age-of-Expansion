package com.tajlpawa.ageofexpansion.image;
import com.tajlpawa.ageofexpansion.display.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

// 控制消息与图像分段共用会话，原子入库；路径从不来自网络。
public final class ImageNetwork {
    public static final int OPEN=0,APPLY=1,CLOSE=2,LIST=3,UPLOAD=4,GET=5,DELETE=6,RENAME=7,INVALIDATE=8,ERROR=9,APPLIED=10,CANCEL=11,OWNERS=12,META=13,SCOPE=14,QUEUED=15,PART=24000;
    public record Packet(int action,UUID request,UUID token,CompoundTag data,byte[] bytes){}
    public record Frame(int action,UUID request,UUID token,CompoundTag data,int index,int total,byte[] bytes) implements CustomPacketPayload{
        public static final Type<Frame> TYPE=new Type<>(ResourceLocation.parse("ageofexpansion:image_transfer"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Frame> CODEC=new StreamCodec<>(){
            public Frame decode(RegistryFriendlyByteBuf b){int op=b.readUnsignedByte();UUID r=b.readUUID(),t=b.readUUID();Tag tag=b.readNbt(NbtAccounter.create(65536));if(!(tag instanceof CompoundTag n))throw new IllegalArgumentException("image_tag");int i=b.readVarInt(),count=b.readVarInt();byte[] bytes=b.readByteArray(PART);if(n==null||op>15||count<1||count>(ImageFiles.MAX_BYTES+PART-1)/PART||i<0||i>=count)throw new IllegalArgumentException("image_frame");return new Frame(op,r,t,n,i,count,bytes);}
            public void encode(RegistryFriendlyByteBuf b,Frame f){b.writeByte(f.action);b.writeUUID(f.request);b.writeUUID(f.token);b.writeNbt(f.data);b.writeVarInt(f.index);b.writeVarInt(f.total);b.writeByteArray(f.bytes);}
        };
        public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    private static final class Incoming{
        final Frame first;long started=System.currentTimeMillis();final ByteArrayOutputStream bytes=new ByteArrayOutputStream();int next;
        Incoming(Frame f){first=f;}
        boolean append(Frame f,int limit){if(f.index!=next||f.total!=first.total||f.action!=first.action||!f.request.equals(first.request)||!f.token.equals(first.token)||!f.data.equals(first.data)||bytes.size()+f.bytes.length>limit)return false;bytes.writeBytes(f.bytes);started=System.currentTimeMillis();next++;return true;}
        Packet finish(){return new Packet(first.action,first.request,first.token,first.data,bytes.toByteArray());}
    }
    private static final class Sending{
        final Packet packet;int index;Sending(Packet p){packet=p;}
        int total(){return Math.max(1,(packet.bytes.length+PART-1)/PART);}
        Frame next(){int start=index*PART;return new Frame(packet.action,packet.request,packet.token,packet.data,index++,total(),Arrays.copyOfRange(packet.bytes,start,Math.min(packet.bytes.length,start+PART)));}
    }
    private record Session(DisplayBlockEntity be,UUID token,long revision,UUID group){}
    private static final Map<UUID,Session> SESSIONS=new HashMap<>();
    private static final Map<UUID,Incoming> IN=new HashMap<>(),CLIENT_IN=new HashMap<>();
    private static final Map<UUID,ArrayDeque<Sending>> OUT=new HashMap<>();
    private static final Map<UUID,Map<UUID,UUID>> DOWNLOADS=new HashMap<>();
    private static final Map<UUID,Long> OUT_BYTES=new HashMap<>();private static long queuedBytes;private static final Map<UUID,Integer> TURN=new HashMap<>();
    public static java.util.function.Consumer<UUID> clientProgress=id->{};
    private static final Map<UUID,Integer> BUDGET=new HashMap<>();
    private static final Map<UUID,Long> CLIENT_CANCELLED=new LinkedHashMap<>();
    private static final Map<UUID,UUID> JOBS=new HashMap<>();
    private static final ArrayDeque<Sending> CLIENT_OUT=new ArrayDeque<>();
    public static Consumer<Packet> clientReceiver=p->{};
    public static void register(RegisterPayloadHandlersEvent e){e.registrar("2").playBidirectional(Frame.TYPE,Frame.CODEC,(f,c)->c.enqueueWork(()->{if(c.player() instanceof ServerPlayer p)server(p,f);else client(f);}));}
    private static boolean allowed(ServerPlayer p,DisplayBlockEntity be){return be.imageScreen()&&!be.isRemoved()&&p.isAlive()&&!p.isSpectator()&&p.mayBuild()&&p.level()==be.getLevel()&&p.canInteractWithBlock(be.getBlockPos(),0)&&p.level().mayInteract(p,be.getBlockPos())&&p.level().getBlockEntity(be.getBlockPos())==be;}
    private static boolean session(ServerPlayer p,UUID token){var s=SESSIONS.get(p.getUUID());return s!=null&&s.token.equals(token)&&allowed(p,s.be);}
    public static void open(ServerPlayer p,DisplayBlockEntity be){
        if(!allowed(p,be))return;if(be.kind()==2)ModuleScreens.get(p.serverLevel()).tick(p.serverLevel());closeSession(p.getUUID());
        UUID token=UUID.randomUUID();SESSIONS.put(p.getUUID(),new Session(be,token,be.revision(),be.screenId));
        var n=ImageLimits.current(p.hasPermissions(2)).save();n.putUUID("scope",ImageLibrary.scope(p.serverLevel()));n.put("selection",be.image().save());var entry=ImageLibrary.get(p.serverLevel()).at(be.image().id());if(entry!=null)n.put("entry",entry.save());n.putLong("pos",be.getBlockPos().asLong());n.putInt("kind",be.kind());n.putInt("width",be.screenWidth);n.putInt("height",be.screenHeight);n.putInt("owned",ImageLibrary.get(p.serverLevel()).count(p.getUUID()));
        send(p,new Packet(OPEN,UUID.randomUUID(),token,n,new byte[0]));
    }
    private static void send(ServerPlayer p,Packet packet){
        if(packet.bytes.length==0){PacketDistributor.sendToPlayer(p,new Sending(packet).next());return;}
        var queue=OUT.computeIfAbsent(p.getUUID(),id->new ArrayDeque<>());long size=packet.bytes.length;
        if(queue.size()<12&&OUT_BYTES.getOrDefault(p.getUUID(),0L)+size<=40L*1024*1024&&queuedBytes+size<=128L*1024*1024){queue.add(new Sending(packet));OUT_BYTES.merge(p.getUUID(),size,Long::sum);queuedBytes+=size;}else error(p,packet.request,packet.token,"busy");
    }
    private static void reply(ServerPlayer p,Frame f,int op,CompoundTag data){send(p,new Packet(op,f.request,f.token,data,new byte[0]));}
    private static void error(ServerPlayer p,UUID request,UUID token,String reason){var n=new CompoundTag();n.putString("error",reason);send(p,new Packet(ERROR,request,token,n,new byte[0]));}
    public static UUID sendClient(int action,UUID token,CompoundTag data,byte[] bytes){UUID request=UUID.randomUUID();CLIENT_OUT.add(new Sending(new Packet(action,request,token,data.copy(),bytes)));return request;}
    public static int clientQueueSize(){return CLIENT_OUT.size();}
    public static void cancelClient(UUID token,UUID request){CLIENT_OUT.removeIf(s->s.packet.request.equals(request));CLIENT_IN.remove(request);CLIENT_CANCELLED.put(request,System.currentTimeMillis());if(CLIENT_CANCELLED.size()>4096)CLIENT_CANCELLED.remove(CLIENT_CANCELLED.keySet().iterator().next());var n=new CompoundTag();n.putUUID("request",request);sendClient(CANCEL,token,n,new byte[0]);}
    private static void server(ServerPlayer p,Frame f){
        if(BUDGET.merge(p.getUUID(),1,Integer::sum)>32)return;
        if(f.action==GET&&f.index==0&&f.total==1){get(p,f);return;}
        if(f.action==CANCEL){UUID request=f.data.hasUUID("request")?f.data.getUUID("request"):ImageSelection.NONE;var downloads=DOWNLOADS.get(p.getUUID());if(downloads!=null&&f.token.equals(downloads.get(request)))downloads.remove(request);removeSending(p.getUUID(),job->job.packet.request.equals(request)&&job.packet.token.equals(f.token));if(session(p,f.token)){var incoming=IN.get(p.getUUID());if(incoming!=null&&incoming.first.request.equals(request))IN.remove(p.getUUID());JOBS.remove(p.getUUID(),request);}return;}
        if(!session(p,f.token))return;
        if(f.action==CLOSE){closeSession(p.getUUID());return;}

        if(f.action==UPLOAD){uploadFrame(p,f);return;}
        if(f.total!=1||f.index!=0||f.bytes.length!=0)return;
        var lib=ImageLibrary.get(p.serverLevel());var s=SESSIONS.get(p.getUUID());
        if(f.action==OWNERS){
            if(!p.hasPermissions(2)){error(p,f.request,f.token,"permission");return;}String query=f.data.getString("query");if(query.length()>64)return;
            var owners=lib.owners(p,query);int size=Math.clamp(f.data.getInt("size"),1,8),page=Math.clamp(f.data.getInt("page"),0,Math.max(0,(owners.size()-1)/size));var n=new CompoundTag();var list=new ListTag();for(var owner:owners.subList(Math.min(page*size,owners.size()),Math.min((page+1)*size,owners.size())))list.add(owner.save());n.put("owners",list);n.putInt("page",page);n.putInt("pages",Math.max(1,(owners.size()+size-1)/size));reply(p,f,OWNERS,n);
        }else if(f.action==APPLY){
            var value=ImageSelection.load(f.data.getCompound("selection"));var limits=ImageLimits.current(p.hasPermissions(2));
            if(s.be.kind()==2)ModuleScreens.get(p.serverLevel()).tick(p.serverLevel());
            if(s.be.revision()!=s.revision||!s.be.screenId.equals(s.group)){error(p,f.request,f.token,"conflict");return;}
            if(value.scale()<limits.minScale()||value.scale()>limits.maxScale()||value.present()&&(!lib.canUse(p,value.id())&&!value.id().equals(s.be.image().id())||lib.at(value.id())==null)){error(p,f.request,f.token,"permission");return;}
            s.be.updateImage(value);SESSIONS.put(p.getUUID(),new Session(s.be,s.token,s.be.revision(),s.be.screenId));var result=new CompoundTag();var entry=lib.at(value.id());if(entry!=null)result.put("entry",entry.save());reply(p,f,APPLIED,result);
        }else if(f.action==LIST){
            String filter=f.data.getString("filter");if(filter.length()>64)return;if(!p.hasPermissions(2))filter="";if(!filter.isBlank()&&!filter.equals("*")&&lib.list(p,filter).isEmpty()){error(p,f.request,f.token,"no_player_images");return;}String query=f.data.getString("query");if(query.length()>64)return;var list=lib.list(p,filter).stream().filter(e->e.name().toLowerCase(java.util.Locale.ROOT).contains(query.toLowerCase(java.util.Locale.ROOT))).toList();int page=Math.clamp(f.data.getInt("page"),0,Math.max(0,(list.size()-1)/9));var n=new CompoundTag();var entries=new ListTag();for(var entry:list.subList(Math.min(page*9,list.size()),Math.min(page*9+9,list.size())))entries.add(entry.save());n.put("entries",entries);n.putInt("page",page);n.putInt("pages",Math.max(1,(list.size()+8)/9));n.putInt("owned",lib.count(p.getUUID()));reply(p,f,LIST,n);
        }else if(f.action==DELETE||f.action==RENAME){
            if(!f.data.hasUUID("id")||!lib.canUse(p,f.data.getUUID("id"))){error(p,f.request,f.token,"permission");return;}UUID id=f.data.getUUID("id");
            if(f.action==DELETE){UUID owner=lib.at(id).owner();lib.delete(p.serverLevel(),id);ImageReads.invalidate(ImageLibrary.directory(p.serverLevel()),id);var n=new CompoundTag();n.putUUID("id",id);n.putUUID("owner",owner);for(var viewer:p.server.getPlayerList().getPlayers())send(viewer,new Packet(INVALIDATE,UUID.randomUUID(),ImageSelection.NONE,n,new byte[0]));}else lib.rename(id,f.data.getString("name"));
            reply(p,f,f.action,new CompoundTag());
        }
    }
    private static void uploadFrame(ServerPlayer p,Frame f){
        var limits=ImageLimits.current(p.hasPermissions(2));var lib=ImageLibrary.get(p.serverLevel());
        if(lib.count(p.getUUID())>=limits.count()){IN.remove(p.getUUID());error(p,f.request,f.token,"quota");return;}
        int limit=f.data.getBoolean("drawn")?ImageFiles.MAX_BYTES:limits.uploadKB()*1024;
        if(f.index==0){if(JOBS.containsKey(p.getUUID())||!IN.containsKey(p.getUUID())&&IN.size()+JOBS.size()>=4){error(p,f.request,f.token,"busy");return;}IN.put(p.getUUID(),new Incoming(f));}
        var incoming=IN.get(p.getUUID());if(incoming==null)return;
        if(!incoming.append(f,limit)){IN.remove(p.getUUID());error(p,f.request,f.token,"file_size");return;}
        if(incoming.next!=f.total)return;IN.remove(p.getUUID());var packet=incoming.finish();
        UUID base=f.data.hasUUID("base")?f.data.getUUID("base"):ImageSelection.NONE;var source=lib.at(base);
        if(!base.equals(ImageSelection.NONE)&&!lib.canUse(p,base)){error(p,f.request,f.token,"permission");return;}
        JOBS.put(p.getUUID(),f.request);var directory=ImageLibrary.directory(p.serverLevel());UUID id=UUID.randomUUID();
        try{ImageFiles.WORK.execute(()->{
            try{
                var prepared=ImageStore.prepare(directory,packet.bytes,true);int w=prepared.asset().full().width(),h=prepared.asset().full().height();
                if(f.data.getBoolean("drawn")&&(source!=null?(w!=source.width()||h!=source.height()):(w<limits.minX()||w>limits.maxX()||h<limits.minY()||h>limits.maxY())))throw new IOException("canvas_size");
                var asset=ImageStore.put(directory,id,prepared);
                p.server.execute(()->{
                    boolean current=JOBS.remove(p.getUUID(),f.request)&&session(p,f.token);var now=ImageLimits.current(p.hasPermissions(2));
                    boolean sizeOk=f.data.getBoolean("drawn")?(source!=null?lib.canUse(p,base):w>=now.minX()&&w<=now.maxX()&&h>=now.minY()&&h<=now.maxY()):packet.bytes.length<=now.uploadKB()*1024;
                    if(!current||!sizeOk||lib.count(p.getUUID())>=now.count()){cleanup(directory,id);if(current)error(p,f.request,f.token,!sizeOk?(source!=null?"permission":f.data.getBoolean("drawn")?"canvas_size":"file_size"):"quota");return;}
                    var entry=new ImageLibrary.Entry(id,p.getUUID(),p.getGameProfile().getName(),ImageLibrary.name(f.data.getString("name")),w,h,asset.visible());lib.add(entry);lib.attach(id,asset);send(p,new Packet(UPLOAD,f.request,f.token,entry.save(),new byte[0]));
                });
            }catch(Exception e){cleanup(directory,id);p.server.execute(()->{if(JOBS.remove(p.getUUID(),f.request)&&session(p,f.token))error(p,f.request,f.token,errorKey(e));});}
        });}catch(java.util.concurrent.RejectedExecutionException e){JOBS.remove(p.getUUID());error(p,f.request,f.token,"busy");}
    }
    private static void cleanup(Path directory,UUID id){try{ImageFiles.WORK.execute(()->{try{ImageStore.retire(directory,id);}catch(IOException ignored){}});}catch(java.util.concurrent.RejectedExecutionException ignored){}}
    public static String errorKey(Exception e){String s=e.getMessage();return Set.of("file_size","format","pixels","canvas_size").contains(s==null?"":s)?s:"io";}
    private static boolean canRead(ServerPlayer p,Frame f,UUID id){
        var lib=ImageLibrary.get(p.serverLevel());if(lib.at(id)==null)return false;var current=SESSIONS.get(p.getUUID());
        if(session(p,f.token)&&(lib.canUse(p,id)||current.be.image().id().equals(id)))return true;
        BlockPos pos=BlockPos.of(f.data.getLong("pos"));return p.level().hasChunkAt(pos)&&p.distanceToSqr(pos.getCenter())<=96*96&&p.level().getBlockEntity(pos) instanceof DisplayBlockEntity be&&be.imageScreen()&&be.image().id().equals(id);
    }
    private static void get(ServerPlayer p,Frame f){
        if(!f.data.hasUUID("id"))return;UUID id=f.data.getUUID("id");if(!canRead(p,f,id)){error(p,f.request,f.token,"permission");return;}
        var jobs=DOWNLOADS.computeIfAbsent(p.getUUID(),k->new HashMap<>());if(jobs.size()>=12){error(p,f.request,f.token,"busy");return;}jobs.put(f.request,f.token);
        reply(p,f,QUEUED,new CompoundTag());var directory=ImageLibrary.directory(p.serverLevel());var legacy=ImageLibrary.migrationDirectories(p.serverLevel());UUID scope=ImageLibrary.scope(p.serverLevel());var lib=ImageLibrary.get(p.serverLevel());
        ImageReads.request(directory,legacy,id,f.data.getBoolean("thumb"),f.data.getBoolean("fallback"),f.data.getBoolean("describe")).whenComplete((result,failed)->p.server.execute(()->{
            var active=DOWNLOADS.get(p.getUUID());if(active==null||!active.remove(f.request,f.token))return;if(!p.connection.isAcceptingMessages())return;
            if(!canRead(p,f,id)){error(p,f.request,f.token,"permission");return;}
            if(failed!=null){error(p,f.request,f.token,failed.getCause() instanceof java.util.concurrent.RejectedExecutionException?"busy":"io");return;}
            lib.attach(id,result.asset());var n=f.data.copy();n.put("variant",result.variant().save());n.putUUID("scope",scope);send(p,new Packet(f.data.getBoolean("describe")?META:GET,f.request,f.token,n,result.bytes()));
        }));
    }
    public static void hello(ServerPlayer p){var n=new CompoundTag();n.putUUID("scope",ImageLibrary.scope(p.serverLevel()));send(p,new Packet(SCOPE,UUID.randomUUID(),ImageSelection.NONE,n,new byte[0]));}
    private static void removeSending(UUID player,java.util.function.Predicate<Sending> test){var queue=OUT.get(player);if(queue==null)return;queue.removeIf(job->{if(!test.test(job))return false;long size=job.packet.bytes.length;queuedBytes-=size;OUT_BYTES.computeIfPresent(player,(key,n)->n-size);return true;});if(queue.isEmpty()){OUT.remove(player);OUT_BYTES.remove(player);}}
    private static void closeSession(UUID player){SESSIONS.remove(player);IN.remove(player);JOBS.remove(player);}
    private static Sending nextSending(ArrayDeque<Sending> queue,int turn){boolean interactive=turn%5!=0;Sending job=null;for(var candidate:queue)if(!candidate.packet.token.equals(ImageSelection.NONE)==interactive){job=candidate;break;}if(job==null)job=queue.peek();queue.remove(job);return job;}
    public static void tick(net.minecraft.server.MinecraftServer server){
        BUDGET.clear();IN.entrySet().removeIf(e->{if(System.currentTimeMillis()-e.getValue().started<=30000)return false;var p=server.getPlayerList().getPlayer(e.getKey());if(p!=null)error(p,e.getValue().first.request,e.getValue().first.token,"timeout");return true;});
        var it=OUT.entrySet().iterator();while(it.hasNext()){var e=it.next();var p=server.getPlayerList().getPlayer(e.getKey());if(p==null){queuedBytes-=OUT_BYTES.getOrDefault(e.getKey(),0L);OUT_BYTES.remove(e.getKey());it.remove();continue;}var queue=e.getValue();
            for(int n=0;n<4&&!queue.isEmpty();n++){int turn=TURN.compute(e.getKey(),(key,value)->value==null?1:(value+1)%5);Sending job=nextSending(queue,turn);PacketDistributor.sendToPlayer(p,job.next());if(job.index<job.total())queue.addLast(job);else{long size=job.packet.bytes.length;queuedBytes-=size;OUT_BYTES.computeIfPresent(e.getKey(),(key,value)->value-size);}}
            if(queue.isEmpty()){OUT_BYTES.remove(e.getKey());it.remove();}
        }
    }
    public static void clientTick(){CLIENT_CANCELLED.values().removeIf(time->System.currentTimeMillis()-time>60000);for(int n=0;n<4&&!CLIENT_OUT.isEmpty();n++){var s=CLIENT_OUT.peek();PacketDistributor.sendToServer(s.next());if(s.index>=s.total())CLIENT_OUT.remove();}CLIENT_IN.entrySet().removeIf(e->System.currentTimeMillis()-e.getValue().started>30000);}
    private static void client(Frame f){
        if(CLIENT_CANCELLED.containsKey(f.request))return;clientProgress.accept(f.request);
        if(f.index==0){if(CLIENT_IN.size()>=16)return;CLIENT_IN.put(f.request,new Incoming(f));}var in=CLIENT_IN.get(f.request);if(in==null)return;
        if(!in.append(f,ImageFiles.MAX_BYTES)){CLIENT_IN.remove(f.request);return;}if(in.next==f.total){CLIENT_IN.remove(f.request);clientReceiver.accept(in.finish());}
    }
    public static void forget(UUID id){closeSession(id);removeSending(id,job->true);DOWNLOADS.remove(id);BUDGET.remove(id);TURN.remove(id);}
    public static void clear(){SESSIONS.clear();IN.clear();JOBS.clear();OUT.clear();BUDGET.clear();DOWNLOADS.clear();OUT_BYTES.clear();TURN.clear();queuedBytes=0;ImageReads.clear();ImageMaintenance.clear();}
    public static void clearClient(){CLIENT_OUT.clear();CLIENT_IN.clear();CLIENT_CANCELLED.clear();}
    private ImageNetwork(){}
}




