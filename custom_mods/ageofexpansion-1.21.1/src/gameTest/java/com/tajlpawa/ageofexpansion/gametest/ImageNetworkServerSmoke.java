package com.tajlpawa.ageofexpansion.gametest;
import com.tajlpawa.ageofexpansion.display.*;
import com.tajlpawa.ageofexpansion.image.ImageNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
@EventBusSubscriber(modid="ageofexpansion")
public final class ImageNetworkServerSmoke {
    private static ServerPlayer player;private static int ticks;private static boolean opened;
    public static final BlockPos POS=new BlockPos(0,80,0);
    @SubscribeEvent public static void login(PlayerEvent.PlayerLoggedInEvent event){if(!Boolean.getBoolean("ageofexpansion.imageNetworkServer")||!(event.getEntity() instanceof ServerPlayer p))return;player=p;ticks=0;com.tajlpawa.ageofexpansion.Config.IMAGE_KB.set(4096);p.server.getPlayerList().op(p.getGameProfile());p.setGameMode(GameType.CREATIVE);
        for(int x=-2;x<=2;x++)for(int z=-3;z<=2;z++)p.serverLevel().setBlockAndUpdate(new BlockPos(x,79,z),Blocks.STONE.defaultBlockState());p.serverLevel().setBlockAndUpdate(POS.south(),Blocks.STONE.defaultBlockState());p.serverLevel().setBlockAndUpdate(POS,DisplayContent.IMAGE_SQUARE.get().defaultBlockState());p.connection.teleport(.5,80,-2,0,0);
    }
    @SubscribeEvent public static void tick(ServerTickEvent.Post event){if(!Boolean.getBoolean("ageofexpansion.imageNetworkServer")||player==null)return;ticks++;if(ticks==100&&!opened){opened=true;ImageNetwork.open(player,(DisplayBlockEntity)player.level().getBlockEntity(POS));}if(ticks>2400)event.getServer().halt(false);}
    @SubscribeEvent public static void logout(PlayerEvent.PlayerLoggedOutEvent event){if(Boolean.getBoolean("ageofexpansion.imageNetworkServer")&&player!=null&&event.getEntity().getUUID().equals(player.getUUID()))player.server.halt(false);}
}
