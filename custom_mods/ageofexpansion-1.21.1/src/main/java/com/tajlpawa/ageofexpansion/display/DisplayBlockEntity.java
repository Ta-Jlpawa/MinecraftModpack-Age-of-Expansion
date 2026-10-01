package com.tajlpawa.ageofexpansion.display;
import com.tajlpawa.ageofexpansion.Config;
import com.tajlpawa.ageofexpansion.image.*;
import java.util.UUID;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class DisplayBlockEntity extends BlockEntity {
    private DisplayDocument document=DisplayDocument.EMPTY;
    private long revision;
    private ImageSelection image=ImageSelection.EMPTY;
    public UUID screenId=new UUID(0,0);
    public BlockPos origin;
    public int screenWidth=1,screenHeight=1;
    public DisplayBlockEntity(BlockPos pos,BlockState state){super(DisplayContent.ENTITY.get(),pos,state);origin=pos;}
    public int kind(){return getBlockState().getBlock() instanceof DisplayBlock block?block.kind:0;}
    public boolean holographic(){return getBlockState().getBlock() instanceof DisplayBlock block&&block.holographic;}
    public boolean imageScreen(){return getBlockState().getBlock() instanceof DisplayBlock block&&block.imageScreen;}
    public ImageSelection image(){if(kind()==2&&level instanceof ServerLevel server){var g=ModuleScreens.get(server).at(worldPosition);return g==null?ImageSelection.EMPTY:g.image;}return image;}
    public void updateImage(ImageSelection value){
        if(!imageScreen())return;
        if(kind()==2&&level instanceof ServerLevel server){var g=ModuleScreens.get(server).at(worldPosition);if(g!=null)ModuleScreens.get(server).updateImage(server,g,value);return;}
        image=value;revision++;sync(level instanceof ServerLevel server&&ImageLibrary.get(server).visible(value),0);
    }
    public int capacity(){return kind()==2?screenWidth*screenHeight*Config.perBlock():Config.maxChar();}
    public DisplayDocument document(){
        if(kind()!=2)return document;
        if(level instanceof ServerLevel server){var group=ModuleScreens.get(server).at(worldPosition);return group==null?DisplayDocument.EMPTY:group.document;}
        var view=DisplayNetwork.CLIENT_VIEWS.get(screenId);
        return view!=null&&view.revision()==revision?view.document():DisplayDocument.EMPTY;
    }
    public long revision(){return revision;}
    public void update(DisplayDocument value){
        if(kind()==2&&level instanceof ServerLevel server){
            var group=ModuleScreens.get(server).at(worldPosition);if(group!=null)ModuleScreens.get(server).update(server,group,value);return;
        }
        document=value;revision++;sync(value.hasText(),0);
    }
    public void assign(ModuleScreens.Group group){
        boolean changed=!screenId.equals(group.id)||revision!=group.revision||!origin.equals(group.origin)||screenWidth!=group.width||screenHeight!=group.height;
        screenId=group.id;revision=group.revision;origin=group.origin;screenWidth=group.width;screenHeight=group.height;
        image=group.image;
        boolean lit=imageScreen()?level instanceof ServerLevel server&&ImageLibrary.get(server).visible(image):group.document.hasText();
        int x=ModuleScreens.column(worldPosition,group.facing)-ModuleScreens.column(origin,group.facing),y=origin.getY()-worldPosition.getY();
        int edges=(x>0?2:0)|(x<screenWidth-1?1:0)|(y>0?4:0)|(y<screenHeight-1?8:0);
        if(changed||getBlockState().getValue(DisplayBlock.CONNECTIONS)!=edges||getBlockState().getValue(DisplayBlock.LIT)!=lit)
            sync(lit,edges);
    }
    private void sync(boolean lit,int edges){
        setChanged();if(level==null)return;
        BlockState old=getBlockState(),next=old.setValue(DisplayBlock.LIT,lit).setValue(DisplayBlock.CONNECTIONS,edges);
        if(old!=next)level.setBlock(worldPosition,next,3);
        level.sendBlockUpdated(worldPosition,old,next,3);
    }
    @Override public void onLoad(){
        super.onLoad();if(kind()==2&&level instanceof ServerLevel server)ModuleScreens.get(server).register(server,this);
        if(imageScreen()&&level instanceof ServerLevel server){var library=ImageLibrary.get(server);library.watch(this);if(image().present()&&library.at(image().id())==null)updateImage(ImageSelection.EMPTY);}
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){
        super.saveAdditional(tag,registries);tag.putLong("revision",revision);if(imageScreen())tag.put("image",image().save());
        if(kind()==2){tag.putUUID("screen",screenId);tag.putLong("origin",origin.asLong());tag.putInt("width",screenWidth);tag.putInt("height",screenHeight);}
        else tag.put("display",document.save());
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){
        super.loadAdditional(tag,registries);revision=tag.getLong("revision");image=ImageSelection.load(tag.getCompound("image"));
        if(tag.hasUUID("screen")){screenId=tag.getUUID("screen");origin=BlockPos.of(tag.getLong("origin"));screenWidth=Math.clamp(tag.getInt("width"),1,10);screenHeight=Math.clamp(tag.getInt("height"),1,10);}
        else document=DisplayDocument.load(tag.getCompound("display"));
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveCustomOnly(registries);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}


