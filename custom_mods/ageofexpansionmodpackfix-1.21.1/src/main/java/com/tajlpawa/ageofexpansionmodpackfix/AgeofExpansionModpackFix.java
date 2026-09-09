package com.tajlpawa.ageofexpansionmodpackfix;

import com.mojang.logging.LogUtils;
import com.tajlpawa.ageofexpansionmodpackfix.create.StressRouterContent;
import com.tajlpawa.ageofexpansionmodpackfix.minecraft.PhantomWardItems;
import com.tajlpawa.ageofexpansionmodpackfix.mekanism.MekanismItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

@Mod(AgeofExpansionModpackFix.MODID)
public class AgeofExpansionModpackFix {
    public static final String MODID = "ageofexpansionmodpackfix";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Dedicated icon and a complete catalogue of this mod's registered items.
    public static final DeferredItem<Item> TAB_ICON = ITEMS.registerSimpleItem("mod_icon");
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_TAB = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ageofexpansionmodpackfix"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> TAB_ICON.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(StressRouterContent.COMBINER.get());
                output.accept(StressRouterContent.SPLITTER.get());
                output.accept(MekanismItems.ADVANCED_TRANSMITTER_INSTALLER.get());
                output.accept(MekanismItems.ELITE_TRANSMITTER_INSTALLER.get());
                output.accept(MekanismItems.ULTIMATE_TRANSMITTER_INSTALLER.get());
                output.accept(PhantomWardItems.CAT_EAR_HEADBAND.get());
                output.accept(TAB_ICON.get());
            }).build());

    public AgeofExpansionModpackFix(IEventBus bus) {
        MekanismItems.register(bus);
        PhantomWardItems.register(bus);
        StressRouterContent.register(bus);
        ITEMS.register(bus);
        CREATIVE_MODE_TABS.register(bus);
    }
}
