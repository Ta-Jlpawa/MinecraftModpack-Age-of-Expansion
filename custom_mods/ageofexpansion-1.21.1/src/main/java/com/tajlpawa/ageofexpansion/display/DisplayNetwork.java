package com.tajlpawa.ageofexpansion.display;
import com.tajlpawa.ageofexpansion.AgeofExpansion;
import com.tajlpawa.ageofexpansion.Config;
import java.util.*;
import java.io.*;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

// 每帧最多24KB，按tick限速；完整组装且版本校验成功后原子提交。
public final class DisplayNetwork {
    public record Meta(int kind,int width,int height,int capacity,BlockPos pos,UUID screen,long revision,int minFontSize,int maxFontSize,boolean holographic){
        public Meta(int kind,int width,int height,int capacity,BlockPos pos,UUID screen,long revision,int minFontSize,int maxFontSize){this(kind,width,height,capacity,pos,screen,revision,minFontSize,maxFontSize,false);}
        public Meta(int kind,int width,int height,int capacity,BlockPos pos,UUID screen,long revision){this(kind,width,height,capacity,pos,screen,revision,Config.minFontSize(),Config.maxFontSize());}
        public static final Meta EMPTY=new Meta(0,1,1,1024,BlockPos.ZERO,new UUID(0,0),0);
    }
    public record Packet(int action,UUID token,DisplayDocument document,Meta meta){
        public Packet(int action,UUID token,DisplayDocument document){this(action,token,document,Meta.EMPTY);}
    }
    public record View(long revision,DisplayDocument document){}
    public static final Map<UUID,View> CLIENT_VIEWS=new LinkedHashMap<>();
    public static Consumer<Packet> clientReceiver=packet->{};
    private record Session(DisplayBlockEntity display,UUID token,long revision,UUID screen,int startingLength){}
    private static final Map<UUID,Session> SESSIONS=new HashMap<>();
    private static final int PART=24000,MAX_BYTES=10+DisplayDocument.LIMIT*8;
    public record Frame(int action,UUID token,Meta meta,int index,int total,byte[] bytes) implements CustomPacketPayload{
        public static final Type<Frame> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath(AgeofExpansion.MODID,"display_transfer"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Frame> CODEC=new StreamCodec<>(){
            @Override public Frame decode(RegistryFriendlyByteBuf b){
                int action=b.readUnsignedByte();UUID token=b.readUUID();
                Meta m=new Meta(b.readUnsignedByte(),b.readUnsignedByte(),b.readUnsignedByte(),b.readVarInt(),b.readBlockPos(),b.readUUID(),b.readLong(),b.readVarInt(),b.readVarInt(),b.readBoolean());
                int index=b.readVarInt(),total=b.readVarInt();byte[] data=b.readByteArray(PART);
                if(index<0||total<1||total>(MAX_BYTES+PART-1)/PART||index>=total||m.kind<0||m.kind>2||m.width<1||m.width>10||m.height<1||m.height>10||m.capacity<1||m.capacity>DisplayDocument.LIMIT||m.minFontSize<1||m.maxFontSize>1000||m.minFontSize>m.maxFontSize)
                    throw new IllegalArgumentException("Invalid transfer");
                return new Frame(action,token,m,index,total,data);
            }
            @Override public void encode(RegistryFriendlyByteBuf b,Frame f){
                b.writeByte(f.action);b.writeUUID(f.token);b.writeByte(f.meta.kind);b.writeByte(f.meta.width);b.writeByte(f.meta.height);b.writeVarInt(f.meta.capacity);
                b.writeBlockPos(f.meta.pos);b.writeUUID(f.meta.screen);b.writeLong(f.meta.revision);b.writeVarInt(f.meta.minFontSize);b.writeVarInt(f.meta.maxFontSize);b.writeBoolean(f.meta.holographic);b.writeVarInt(f.index);b.writeVarInt(f.total);b.writeByteArray(f.bytes);
            }
        };
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    private static final class Incoming {
        Frame first;int next;long created=System.currentTimeMillis();ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        Incoming(Frame frame){first=frame;}
        boolean append(Frame frame){
            if(frame.index!=next||frame.total!=first.total||frame.action!=first.action||!frame.token.equals(first.token)||!frame.meta.equals(first.meta)||bytes.size()+frame.bytes.length>MAX_BYTES)return false;
            bytes.writeBytes(frame.bytes);next++;return true;
        }
    }
    private static final Map<UUID,Incoming> SERVER_IN=new HashMap<>(),CLIENT_IN=new HashMap<>();
    private record Sending(ServerPlayer player,ArrayDeque<Frame> frames){}
    private static final Map<UUID,Sending> SERVER_OUT=new HashMap<>();
    private static final ArrayDeque<Frame> CLIENT_OUT=new ArrayDeque<>();
    private static final Map<UUID,Long> REQUESTS=new HashMap<>(),VIEW_REQUESTS=new HashMap<>();
    public static void register(RegisterPayloadHandlersEvent event){
        event.registrar("4").playBidirectional(Frame.TYPE,Frame.CODEC,(frame,context)->context.enqueueWork(()->{
            if(context.player() instanceof ServerPlayer player)serverFrame(player,frame);else clientFrame(frame);
        }));
    }
    public static List<Frame> frames(Packet packet){
        byte[] bytes=packet.document.bytes();int total=(bytes.length+PART-1)/PART;List<Frame> result=new ArrayList<>(total);
        for(int i=0;i<total;i++)result.add(new Frame(packet.action,packet.token,packet.meta,i,total,Arrays.copyOfRange(bytes,i*PART,Math.min(bytes.length,(i+1)*PART))));
        return result;
    }
    private static void send(ServerPlayer player,Packet packet){
        Sending sending=SERVER_OUT.computeIfAbsent(player.getUUID(),id->new Sending(player,new ArrayDeque<>()));
        if(sending.frames.size()>512){sending.frames.clear();}
        sending.frames.addAll(frames(packet));
    }
    public static void sendClient(Packet packet){CLIENT_OUT.addAll(frames(packet));}
    public static void tick(){
        var it=SERVER_OUT.values().iterator();while(it.hasNext()){
            Sending sending=it.next();for(int i=0;i<4&&!sending.frames.isEmpty();i++)PacketDistributor.sendToPlayer(sending.player,sending.frames.removeFirst());
            if(sending.frames.isEmpty())it.remove();
        }
        SERVER_IN.values().removeIf(in->System.currentTimeMillis()-in.created>30000);
    }
    public static void clientTick(){
        for(int i=0;i<4&&!CLIENT_OUT.isEmpty();i++)PacketDistributor.sendToServer(CLIENT_OUT.removeFirst());
        CLIENT_IN.values().removeIf(in->System.currentTimeMillis()-in.created>30000);
    }
    public static void clearClient(){CLIENT_VIEWS.clear();CLIENT_IN.clear();CLIENT_OUT.clear();REQUESTS.clear();}
    public static void clear(){SESSIONS.clear();SERVER_IN.clear();SERVER_OUT.clear();VIEW_REQUESTS.clear();}
    public static void forget(UUID player){SESSIONS.remove(player);SERVER_IN.remove(player);SERVER_OUT.remove(player);VIEW_REQUESTS.remove(player);}
    private static boolean allowed(ServerPlayer player,DisplayBlockEntity display){
        return player.isAlive()&&!player.isSpectator()&&player.mayBuild()&&display.getLevel()==player.level()
            &&player.canInteractWithBlock(display.getBlockPos(),0)&&player.level().mayInteract(player,display.getBlockPos());
    }
    private static Meta meta(DisplayBlockEntity display){return new Meta(display.kind(),display.screenWidth,display.screenHeight,display.capacity(),display.getBlockPos(),display.screenId,display.revision(),Config.minFontSize(),Config.maxFontSize(),display.holographic());}
    public static void open(ServerPlayer player,DisplayBlockEntity display){
        if(display.imageScreen()){com.tajlpawa.ageofexpansion.image.ImageNetwork.open(player,display);return;}
        if(!allowed(player,display))return;
        if(display.kind()==2)ModuleScreens.get(player.serverLevel()).tick(player.serverLevel());
        UUID token=UUID.randomUUID();SESSIONS.put(player.getUUID(),new Session(display,token,display.revision(),display.screenId,display.document().glyphs().size()));
        SERVER_IN.remove(player.getUUID());send(player,new Packet(0,token,display.document(),meta(display)));
    }
    private static void serverFrame(ServerPlayer player,Frame frame){
        if(frame.action==7){if(frame.index==0&&frame.total==1)DataCopierItem.paste(player,frame.meta.pos);return;}
        if(frame.action==6){
            long now=System.currentTimeMillis(),last=VIEW_REQUESTS.getOrDefault(player.getUUID(),0L);
            if(now-last<50)return;VIEW_REQUESTS.put(player.getUUID(),now);
            if(!player.level().hasChunkAt(frame.meta.pos)||player.distanceToSqr(frame.meta.pos.getCenter())>128*128)return;
            if(player.level().getBlockEntity(frame.meta.pos) instanceof DisplayBlockEntity display&&display.kind()==2&&!display.imageScreen()&&display.screenId.equals(frame.token))
                send(player,new Packet(5,frame.token,display.document(),meta(display)));
            return;
        }
        Session session=SESSIONS.get(player.getUUID());
        if(session==null||!session.token.equals(frame.token))return;
        if(frame.action==3){SESSIONS.remove(player.getUUID());SERVER_IN.remove(player.getUUID());return;}
        if(frame.action!=1)return;
        Incoming incoming=SERVER_IN.get(player.getUUID());
        if(frame.index==0){incoming=new Incoming(frame);SERVER_IN.put(player.getUUID(),incoming);}
        if(incoming==null||!incoming.append(frame)){SERVER_IN.remove(player.getUUID());return;}
        if(incoming.next==frame.total){
            SERVER_IN.remove(player.getUUID());
            try{receive(player,new Packet(1,frame.token,DisplayDocument.bytes(incoming.bytes.toByteArray()),frame.meta));}
            catch(IllegalArgumentException e){send(player,new Packet(4,frame.token,DisplayDocument.EMPTY));}
        }
    }
    private static void receive(ServerPlayer player,Packet packet){
        Session session=SESSIONS.get(player.getUUID());if(session==null||!session.token.equals(packet.token))return;
        if(packet.action==3){SESSIONS.remove(player.getUUID());return;}if(packet.action!=1)return;
        DisplayBlockEntity display=session.display;
        if(display.kind()==2)ModuleScreens.get(player.serverLevel()).tick(player.serverLevel());
        boolean valid=allowed(player,display)&&!display.isRemoved()&&player.level().getBlockEntity(display.getBlockPos())==display
            &&display.revision()==session.revision&&display.screenId.equals(session.screen)
            &&Config.allowsFontSize(packet.document.size())
            &&packet.document.glyphs().size()<=Math.max(display.capacity(),session.startingLength);
        if(valid)display.update(packet.document);
        send(player,new Packet(valid?2:4,packet.token,DisplayDocument.EMPTY));
        SESSIONS.remove(player.getUUID());
    }
    private static void clientFrame(Frame frame){
        Incoming incoming=CLIENT_IN.get(frame.token);
        if(frame.index==0){if(CLIENT_IN.size()>128)CLIENT_IN.clear();incoming=new Incoming(frame);CLIENT_IN.put(frame.token,incoming);}
        if(incoming==null||!incoming.append(frame)){CLIENT_IN.remove(frame.token);return;}
        if(incoming.next==frame.total){
            CLIENT_IN.remove(frame.token);
            DisplayDocument document=DisplayDocument.bytes(incoming.bytes.toByteArray());
            if(frame.action==5){
                CLIENT_VIEWS.put(frame.token,new View(frame.meta.revision,document));
                if(CLIENT_VIEWS.size()>128)CLIENT_VIEWS.remove(CLIENT_VIEWS.keySet().iterator().next());
            }else clientReceiver.accept(new Packet(frame.action,frame.token,document,frame.meta));
        }
    }
    public static void requestView(DisplayBlockEntity display){
        var view=CLIENT_VIEWS.get(display.screenId);if(view!=null&&view.revision==display.revision())return;
        long now=System.currentTimeMillis();if(now-REQUESTS.getOrDefault(display.screenId,0L)<2000)return;
        REQUESTS.put(display.screenId,now);sendClient(new Packet(6,display.screenId,DisplayDocument.EMPTY,meta(display)));
    }
    private DisplayNetwork(){}
}




