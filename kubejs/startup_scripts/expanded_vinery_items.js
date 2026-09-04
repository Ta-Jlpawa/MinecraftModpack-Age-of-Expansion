
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
        .displayName('液态Strad唱片白葡萄酒')
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
        .displayName('液态信徒葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态Mojang红葡萄酒
    event.create('bottle_mojang_noir_fluid')
        .tag(['kubejs:bottle_mojang_noir_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态“Mojang诺耶”红葡萄酒')
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
        .displayName('液态Mellohi唱片白葡萄酒')
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
        .displayName('液态阳光白葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态Jellie猫咪白葡萄酒
    event.create('jellie_wine_fluid')
        .tag(['kubejs:jellie_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态Jellie猫咪葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );

    // ==================== 补全：覆盖其余全部酒种 ====================

    // 液态苹果汁（苹果系酒基底）
    event.create('apple_juice_fluid')
        .tag(['kubejs:apple_juice_fluid'])
        .tint(0x3F76E4)
        .noBucket()
        .type(type => type
        .displayName('液态苹果汁')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态神盾红葡萄酒
    event.create('aegis_wine_fluid')
        .tag(['kubejs:aegis_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态神盾葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态苹果汽酒
    event.create('apple_cider_fluid')
        .tag(['kubejs:apple_cider_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态苹果西打酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态苹果酒
    event.create('apple_wine_fluid')
        .tag(['kubejs:apple_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态苹果葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态伯瓦尔红葡萄酒
    event.create('bolvar_wine_fluid')
        .tag(['kubejs:bolvar_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态伯瓦尔什锦红葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态谢内红葡萄酒
    event.create('chenet_wine_fluid')
        .tag(['kubejs:chenet_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态香奈红葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态紫颂红葡萄酒
    event.create('chorus_wine_fluid')
        .tag(['kubejs:chorus_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态紫颂果酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态克拉克白葡萄酒
    event.create('clark_wine_fluid')
        .tag(['kubejs:clark_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态克拉克白葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态苦力怕压榨
    event.create('creepers_crush_fluid')
        .tag(['kubejs:creepers_crush_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态“苦力怕的威压”')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态冰酒
    event.create('eiswein_fluid')
        .tag(['kubejs:eiswein_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态冰酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态乔氏特酿
    event.create('jo_special_mixture_fluid')
        .tag(['kubejs:jo_special_mixture_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态Jo\'s特调混合酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态海带汽酒
    event.create('kelp_cider_fluid')
        .tag(['kubejs:kelp_cider_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态海带苹果西打酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态莉莉图红葡萄酒
    event.create('lilitu_wine_fluid')
        .tag(['kubejs:lilitu_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态Miss Lilitus红葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态磁化红葡萄酒
    event.create('magnetic_wine_fluid')
        .tag(['kubejs:magnetic_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态磁石葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态蜂蜜酒
    event.create('mead_fluid')
        .tag(['kubejs:mead_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态蜂蜜酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态史塔红葡萄酒
    event.create('stal_wine_fluid')
        .tag(['kubejs:stal_wine_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态Stal唱片红葡萄酒')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
    // 液态村民惊魂白葡萄酒
    event.create('villagers_fright_fluid')
        .tag(['kubejs:villagers_fright_fluid'])
        .tint(0x3F76E4)
        .type(type => type
        .displayName('液态“村民梦魇”')
        .renderType(3)
        .stillTexture('kubejs:block/thin_fluid_still')
        .flowingTexture('kubejs:block/thin_fluid_flow')
    );
})