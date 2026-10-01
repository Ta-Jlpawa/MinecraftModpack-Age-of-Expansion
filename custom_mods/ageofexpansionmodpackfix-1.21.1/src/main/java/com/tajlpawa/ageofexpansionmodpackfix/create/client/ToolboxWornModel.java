package com.tajlpawa.ageofexpansionmodpackfix.create.client;

import com.tajlpawa.ageofexpansionmodpackfix.AgeofExpansionModpackFix;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = AgeofExpansionModpackFix.MODID, value = Dist.CLIENT)
public final class ToolboxWornModel {
    public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(AgeofExpansionModpackFix.MODID, "item/toolbox_worn");
    @SubscribeEvent public static void register(ModelEvent.RegisterAdditional event) {
        event.register(ModelResourceLocation.standalone(LOCATION));
    }
}
