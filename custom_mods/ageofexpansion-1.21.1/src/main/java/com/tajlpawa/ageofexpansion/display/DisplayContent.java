package com.tajlpawa.ageofexpansion.display;
import com.tajlpawa.ageofexpansion.AgeofExpansion;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class DisplayContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks(AgeofExpansion.MODID);
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems(AgeofExpansion.MODID);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,AgeofExpansion.MODID);
    private static BlockBehaviour.Properties properties(){return BlockBehaviour.Properties.of().strength(1f).sound(SoundType.METAL).noOcclusion().noCollission().lightLevel(s->s.getValue(DisplayBlock.LIT)?4:0);}
    public static final DeferredBlock<DisplayBlock> BLOCK=BLOCKS.register("electronic_display",()->new DisplayBlock(properties(),0));
    public static final DeferredBlock<DisplayBlock> SQUARE=BLOCKS.register("square_electronic_display",()->new DisplayBlock(properties(),1));
    public static final DeferredBlock<DisplayBlock> MODULE=BLOCKS.register("modular_electronic_display",()->new DisplayBlock(properties(),2));
    private static BlockBehaviour.Properties holographicProperties(){return properties().lightLevel(s->s.getValue(DisplayBlock.LIT)?7:0);}
    public static final DeferredBlock<DisplayBlock> HOLOGRAPHIC=BLOCKS.register("holographic_display",()->new DisplayBlock(holographicProperties(),0,true));
    public static final DeferredBlock<DisplayBlock> HOLOGRAPHIC_SQUARE=BLOCKS.register("square_holographic_display",()->new DisplayBlock(holographicProperties(),1,true));
    public static final DeferredBlock<DisplayBlock> HOLOGRAPHIC_MODULE=BLOCKS.register("modular_holographic_display",()->new DisplayBlock(holographicProperties(),2,true));
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredItem<BlockItem> SQUARE_ITEM=ITEMS.registerSimpleBlockItem(SQUARE);
    public static final DeferredItem<BlockItem> MODULE_ITEM=ITEMS.registerSimpleBlockItem(MODULE);
    public static final DeferredItem<BlockItem> HOLOGRAPHIC_ITEM=ITEMS.registerSimpleBlockItem(HOLOGRAPHIC);
    public static final DeferredItem<BlockItem> HOLOGRAPHIC_SQUARE_ITEM=ITEMS.registerSimpleBlockItem(HOLOGRAPHIC_SQUARE);
    public static final DeferredItem<BlockItem> HOLOGRAPHIC_MODULE_ITEM=ITEMS.registerSimpleBlockItem(HOLOGRAPHIC_MODULE);
    public static final DeferredBlock<DisplayBlock> IMAGE=BLOCKS.register("image_display",()->new DisplayBlock(holographicProperties(),0,false,true));
    public static final DeferredBlock<DisplayBlock> IMAGE_SQUARE=BLOCKS.register("square_image_display",()->new DisplayBlock(holographicProperties(),1,false,true));
    public static final DeferredBlock<DisplayBlock> IMAGE_MODULE=BLOCKS.register("modular_image_display",()->new DisplayBlock(holographicProperties(),2,false,true));
    public static final DeferredItem<BlockItem> IMAGE_ITEM=ITEMS.registerSimpleBlockItem(IMAGE),IMAGE_SQUARE_ITEM=ITEMS.registerSimpleBlockItem(IMAGE_SQUARE),IMAGE_MODULE_ITEM=ITEMS.registerSimpleBlockItem(IMAGE_MODULE);
    public static final DeferredItem<DataCopierItem> COPIER=ITEMS.register("data_copier",()->new DataCopierItem(new Item.Properties().stacksTo(1)));
    public static List<DeferredItem<? extends Item>> items(){return List.of(ITEM,SQUARE_ITEM,MODULE_ITEM,HOLOGRAPHIC_ITEM,HOLOGRAPHIC_SQUARE_ITEM,HOLOGRAPHIC_MODULE_ITEM,IMAGE_ITEM,IMAGE_SQUARE_ITEM,IMAGE_MODULE_ITEM,COPIER);}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DisplayBlockEntity>> ENTITY=ENTITIES.register("electronic_display",
        ()->BlockEntityType.Builder.of(DisplayBlockEntity::new,BLOCK.get(),SQUARE.get(),MODULE.get(),HOLOGRAPHIC.get(),HOLOGRAPHIC_SQUARE.get(),HOLOGRAPHIC_MODULE.get(),IMAGE.get(),IMAGE_SQUARE.get(),IMAGE_MODULE.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(DisplayNetwork::register);bus.addListener(com.tajlpawa.ageofexpansion.image.ImageNetwork::register);}
    private DisplayContent(){}
}


