package com.tajlpawa.ageofexpansionmodpackfix.create;

import com.tajlpawa.ageofexpansionmodpackfix.AgeofExpansionModpackFix;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

public final class StressRouterContent {
    public static final String MOD_ID = AgeofExpansionModpackFix.MODID;
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MOD_ID);
    public static final DeferredBlock<StressRouterBlock> COMBINER = BLOCKS.register("stress_network_combiner",
            () -> new StressRouterBlock(false, properties()));
    public static final DeferredBlock<StressRouterBlock> SPLITTER = BLOCKS.register("stress_network_splitter",
            () -> new StressRouterBlock(true, properties()));
    public static final DeferredBlock<StressPortBlock> PORT = BLOCKS.register("stress_network_port",
            () -> new StressPortBlock(properties().noOcclusion()));
    public static final DeferredBlock<StressConnectorBlock> CONNECTOR = BLOCKS.register("stress_network_connector",
            () -> new StressConnectorBlock(properties().noOcclusion()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StressRouterBlockEntity>> ROUTER_ENTITY = ENTITIES.register(
            "stress_router", () -> BlockEntityType.Builder.of(StressRouterBlockEntity::new, COMBINER.get(), SPLITTER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StressPortBlockEntity>> PORT_ENTITY = ENTITIES.register(
            "stress_port", () -> BlockEntityType.Builder.of(StressPortBlockEntity::new, PORT.get(), CONNECTOR.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<StressRouterMenu>> MENU = MENUS.register("stress_router",
            () -> net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create(StressRouterMenu::new));

    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(3.0f).requiresCorrectToolForDrops()
                .pushReaction(PushReaction.BLOCK);
    }

    public static void register(IEventBus bus) {
        ITEMS.register("stress_network_combiner", () -> new StressRouterBlockItem(COMBINER.get()));
        ITEMS.register("stress_network_splitter", () -> new StressRouterBlockItem(SPLITTER.get()));
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        MENUS.register(bus);
        bus.addListener(StressRouterPayload::register);
        bus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                    || event.getTabKey().equals(com.simibubi.create.AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())) {
                event.accept(COMBINER.get());
                event.accept(SPLITTER.get());
            }
        });
    }
}
