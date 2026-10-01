package com.tajlpawa.ageofexpansionmodpackfix.minecraft;

import com.tajlpawa.ageofexpansionmodpackfix.AgeofExpansionModpackFix;
import com.tajlpawa.ageofexpansionmodpackfix.curios.PhantomWardCurios;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PhantomWardItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AgeofExpansionModpackFix.MODID);
    public static final DeferredItem<PhantomWardItem> CAT_EAR_HEADBAND = ITEMS.register("cat_ear_headband", PhantomWardItem::new);
    private PhantomWardItems() {}
    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        bus.addListener(PhantomWardItems::setup);
    }
    private static void setup(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("curios")) event.enqueueWork(PhantomWardCurios::register);
    }
}
