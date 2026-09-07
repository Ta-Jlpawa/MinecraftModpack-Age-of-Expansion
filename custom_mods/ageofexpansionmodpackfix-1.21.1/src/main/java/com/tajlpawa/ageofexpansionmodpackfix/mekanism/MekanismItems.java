package com.tajlpawa.ageofexpansionmodpackfix.mekanism;

import com.tajlpawa.ageofexpansionmodpackfix.AgeofExpansionModpackFix;
import mekanism.api.tier.AlloyTier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MekanismItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AgeofExpansionModpackFix.MODID);

    public static final DeferredItem<TransmitterInstallerItem> ADVANCED_TRANSMITTER_INSTALLER = ITEMS.register(
          "advanced_transmitter_installer", () -> new TransmitterInstallerItem(AlloyTier.INFUSED, new Item.Properties()));
    public static final DeferredItem<TransmitterInstallerItem> ELITE_TRANSMITTER_INSTALLER = ITEMS.register(
          "elite_transmitter_installer", () -> new TransmitterInstallerItem(AlloyTier.REINFORCED, new Item.Properties()));
    public static final DeferredItem<TransmitterInstallerItem> ULTIMATE_TRANSMITTER_INSTALLER = ITEMS.register(
          "ultimate_transmitter_installer", () -> new TransmitterInstallerItem(AlloyTier.ATOMIC, new Item.Properties()));

    private MekanismItems() {
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        bus.addListener(MekanismItems::addCreativeItems);
    }

    private static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ADVANCED_TRANSMITTER_INSTALLER);
            event.accept(ELITE_TRANSMITTER_INSTALLER);
            event.accept(ULTIMATE_TRANSMITTER_INSTALLER);
        }
    }
}
