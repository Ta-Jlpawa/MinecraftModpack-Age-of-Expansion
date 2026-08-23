
const $SoundEvents = Java.loadClass('net.minecraft.sounds.SoundEvents')
const $ParticleTypes = Java.loadClass('net.minecraft.core.particles.ParticleTypes')

// 葡园酒香自动化

StartupEvents.registry('fluid', event => {
    // 液态红葡萄汁
    event.create('red_grapejuice_fluid')
        .tag(['kubejs:red_grapejuice_fluid'])
        .tint(0x3F76E4)
        .noBucket() // 无配套桶
        .type(type => type // 流式构建器
        .displayName('液态红葡萄汁')
        .renderType(3) // 强制
        .stillTexture('kubejs:block/thin_fluid_still') // 强制
        .flowingTexture('kubejs:block/thin_fluid_flow') // 强制
        .addDripstoneDripping(1, $ParticleTypes.DRIPPING_DRIPSTONE_WATER, 'minecraft:water_cauldron', $SoundEvents.POINTED_DRIPSTONE_DRIP_WATER)
    );
    // 液态诺耶红葡萄酒
    event.create('noir_wine_fluid')
        .tag(['kubejs:noir_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态诺耶红葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow') 
    );
    // 液态红葡萄酒
    event.create('red_wine_fluid')
        .tag(['kubejs:red_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态红葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态strad红葡萄酒
    event.create('strad_wine_fluid')
        .tag(['kubejs:strad_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态strad红葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow') 
    );
    // 液态樱桃酒
    event.create('cherry_wine_fluid')
        .tag(['kubejs:cherry_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态樱桃酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态信徒红葡萄酒
    event.create('cristel_wine_fluid')
        .tag(['kubejs:cristel_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态信徒红葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态Mojang红葡萄酒
    event.create('bottle_mojang_noir_fluid')
        .tag(['kubejs:bottle_mojang_noir_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态Mojang红葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );

    // 液态白葡萄汁
    event.create('white_grapejuice_fluid')
        .tag(['kubejs:white_grapejuice_fluid'])
        .tint(0x3F76E4)
        .noBucket()
        .type(type => type
        .displayName('液态白葡萄汁')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态Mellohi白葡萄酒
    event.create('mellohi_wine_fluid')
        .tag(['kubejs:mellohi_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态Mellohi白葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态阳光白葡萄酒
    event.create('glowing_wine_fluid')
        .tag(['kubejs:glowing_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态阳光白葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态阳光白葡萄酒(Solaris)
    event.create('solaris_wine_fluid')
        .tag(['kubejs:solaris_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态阳光Solaris白葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态Jellie猫咪白葡萄酒
    event.create('jellie_wine_fluid')
        .tag(['kubejs:jellie_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态Jellie猫咪白葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
})