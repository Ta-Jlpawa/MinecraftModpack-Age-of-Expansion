package com.tajlpawa.ageofexpansion;
import com.mojang.logging.LogUtils;
import com.tajlpawa.ageofexpansion.display.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
@Mod(AgeofExpansion.MODID)
public class AgeofExpansion {
    public static final String MODID="ageofexpansion";
    public static final Logger LOGGER=LogUtils.getLogger();
    private static final DeferredRegister<CreativeModeTab> TABS=DeferredRegister.create(Registries.CREATIVE_MODE_TAB,MODID);
    static {
        TABS.register("example_tab",()->CreativeModeTab.builder().title(Component.translatable("itemGroup.ageofexpansion"))
            .withTabsBefore(CreativeModeTabs.COMBAT).icon(()->DisplayContent.ITEM.get().getDefaultInstance())
            .displayItems((parameters,output)->DisplayContent.items().forEach(item->output.accept(item.get()))).build());
    }
    public AgeofExpansion(IEventBus bus,ModContainer container){
        DisplayContent.register(bus);TABS.register(bus);
        bus.addListener((BuildCreativeModeTabContentsEvent event)->{
            if(event.getTabKey()==CreativeModeTabs.BUILDING_BLOCKS)DisplayContent.items().stream().filter(item->item.get() instanceof net.minecraft.world.item.BlockItem).forEach(event::accept);
            if(event.getTabKey()==CreativeModeTabs.TOOLS_AND_UTILITIES)event.accept(DisplayContent.COPIER);
        });
        container.registerConfig(ModConfig.Type.COMMON,Config.SPEC);
        bus.addListener((net.neoforged.fml.event.config.ModConfigEvent.Loading event)->Config.validateFontRange(event));
        bus.addListener((net.neoforged.fml.event.config.ModConfigEvent.Reloading event)->Config.validateFontRange(event));
        NeoForge.EVENT_BUS.addListener(DataCopierItem::leftClick);
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event)->{DisplayNetwork.forget(event.getEntity().getUUID());com.tajlpawa.ageofexpansion.image.ImageNetwork.forget(event.getEntity().getUUID());});
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.tick.ServerTickEvent.Post event)->{
            for(var level:event.getServer().getAllLevels())ModuleScreens.get(level).tick(level);
            DisplayNetwork.tick();com.tajlpawa.ageofexpansion.image.ImageNetwork.tick(event.getServer());
        });
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event)->{if(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)com.tajlpawa.ageofexpansion.image.ImageNetwork.hello(player);});
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStoppedEvent event)->{DisplayNetwork.clear();com.tajlpawa.ageofexpansion.image.ImageNetwork.clear();});
    }
}


