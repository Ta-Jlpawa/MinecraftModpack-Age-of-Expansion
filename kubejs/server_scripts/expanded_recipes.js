ServerEvents.recipes(event => {
    // ================== 矿脉生成 ==================
    // 定义时间(tick)
    const TIME = {
        NORMAL: 600,
        DIAMOND: 400,
        NETHERITE: 160
    };
    // === 铝土矿
    event.recipes.createoreexcavation.vein('{"text": "铝土矿"}', 'immersiveengineering:raw_aluminum') // 生成矿脉(名称，贴图)
        .placement(128, 8, 333333)                   // 生成规则（平均间隔128区块，最小间距8区块，随机数）
        .biomeWhitelist('minecraft:is_overworld')    // 可生成的群系白名单；黑名单使用 .biomeBlacklist()
        .priority(0)                                 // 多个矿物都命中同一个区块时，采用数值高的矿脉
        .alwaysInfinite()                            // 矿脉是不是无限开采的，可为.alwaysFinite()，不添加默认为 .alwaysInfinite()
        .id("kubejs:raw_aluminum_vein");             // id
    // 普通钻头配方
    event.recipes.createoreexcavation.drilling('immersiveengineering:raw_aluminum', 'kubejs:raw_aluminum_vein', TIME.NORMAL)// 矿物配置（产出xx{几率产出使用中括号例如:['mekanism:raw_osmium', Item.of('minecraft:diamond').withChance(0.05)]，在 xx 矿脉中，32 RPM 下每采集一次需要 xx tick），id（xx）
        .drill(['createoreexcavation:drill'])
        .id("kubejs:drilling_immersiveengineering_raw_aluminum_normal");
    // 钻石钻头配方（只允许钻石钻头）
    event.recipes.createoreexcavation.drilling('immersiveengineering:raw_aluminum', 'kubejs:raw_aluminum_vein', TIME.DIAMOND)// 矿物配置（产出xx{几率产出使用中括号例如:['mekanism:raw_osmium', Item.of('minecraft:diamond').withChance(0.05)几率.withCount([number])数量]，在 xx 矿脉中，32 RPM 下每采集一次需要 xx tick），id（xx）
        .drill(['createoreexcavation:diamond_drill'])   // 只允许钻石钻头
        .id("kubejs:drilling_immersiveengineering_raw_aluminum_diamond");
        //.fluid('minecraft:lava 10')                                   // 需要流体 minecraft:lava，每次 10ml，不配置为 1000ml
        //.drill('createoreexcavation:diamond_drill')           // 需要钻头 钻石以上 （下界合金钻头为 createoreexcavation:netherite_drill）
        //.stress(512)                                                     // 需要应力 512 xRPM ，不加默认 256
    // 下界合金钻头配方（只允许下界合金钻头）
    event.recipes.createoreexcavation.drilling(['immersiveengineering:raw_aluminum', coeutil.processingOutput('minecraft:cobbled_deepslate', 0.2)], 'kubejs:raw_aluminum_vein', TIME.NETHERITE)
        .drill(['createoreexcavation:netherite_drill'])
        .id("kubejs:drilling_immersiveengineering_raw_aluminum_netherite");

    // === 沥青铀矿
    event.recipes.createoreexcavation.vein('{"text": "沥青铀矿"}', 'immersiveengineering:raw_uranium')
        .placement(256, 16, 327671)
        .biomeWhitelist('minecraft:is_overworld')
        .id("kubejs:immersiveengineering_raw_uranium_vein");
    event.recipes.createoreexcavation.drilling([coeutil.processingOutput('immersiveengineering:raw_uranium', 0.05), coeutil.processingOutput('minecraft:cobbled_deepslate', 0.3), coeutil.processingOutput('minecraft:granite', 0.1), coeutil.processingOutput('immersiveengineering:raw_lead', 0.3), coeutil.processingOutput('mekanism:pellet_polonium', 0.001)], 'kubejs:immersiveengineering_raw_uranium_vein', TIME.NORMAL)
        .drill('createoreexcavation:netherite_drill')
        .id("kubejs:drilling_immersiveengineering_raw_uranium_normal")
        .fluid('minecraft:water 50')
        
    
    // === 方铀矿
    event.recipes.createoreexcavation.vein('{"text": "方铀矿"}', 'mekanism:raw_uranium')
        .placement(312, 16, 123412)
        .biomeWhitelist('minecraft:is_overworld')
        .id("kubejs:mek_raw_uranium_vein");
    event.recipes.createoreexcavation.drilling([coeutil.processingOutput('mekanism:raw_uranium', 0.1), coeutil.processingOutput('minecraft:cobbled_deepslate', 0.5), coeutil.processingOutput('minecraft:granite', 0.5), coeutil.processingOutput('mekanism:raw_lead', 0.05)], 'kubejs:mek_raw_uranium_vein', TIME.NORMAL)
        .drill('createoreexcavation:netherite_drill')
        .id("kubejs:drilling_mek_raw_uranium_normal")
        .fluid('minecraft:water 100')
        .stress(300);

    // === 粗镍矿
    event.recipes.createoreexcavation.vein('{"text": "粗镍矿"}', 'immersiveengineering:raw_nickel')
        .placement(128, 8, 655363)
        .biomeWhitelist('minecraft:is_overworld')
        .id("kubejs:raw_nickel_vein");
    event.recipes.createoreexcavation.drilling('immersiveengineering:raw_nickel', 'kubejs:raw_nickel_vein', TIME.NORMAL)
        .id("kubejs:drilling_raw_nickel_normal");

    // === 方铅矿
    event.recipes.createoreexcavation.vein('{"text": "方铅矿"}', 'immersiveengineering:raw_lead')
        .placement(128, 8, 919810)
        .biomeWhitelist('minecraft:is_overworld')
        .id("kubejs:immersiveengineering_raw_lead_vein");
    event.recipes.createoreexcavation.drilling([coeutil.processingOutput('immersiveengineering:raw_lead', 0.3), coeutil.processingOutput('immersiveengineering:dust_sulfur', 0.3), coeutil.processingOutput('immersiveengineering:raw_silver', 0.01)], 'kubejs:immersiveengineering_raw_lead_vein', TIME.NORMAL)
        .id("kubejs:drilling_immersiveengineering_raw_lead_normal");
    
    // === 铅锌矿
    event.recipes.createoreexcavation.vein('{"text": "铅锌矿"}', 'mekanism:raw_lead')
        .placement(128, 8, 893074)
        .biomeWhitelist('minecraft:is_overworld')
        .id("kubejs:mek_raw_lead_vein");
    event.recipes.createoreexcavation.drilling([coeutil.processingOutput('mekanism:raw_lead', 0.5), coeutil.processingOutput('create:raw_zinc', 0.5)], 'kubejs:mek_raw_lead_vein', TIME.NORMAL)
        .id("kubejs:drilling_mek_raw_lead_normal");

    // === 粗银
    event.recipes.createoreexcavation.vein('{"text": "粗银矿"}', 'immersiveengineering:raw_silver')
        .placement(128, 8, 191981)
        .biomeWhitelist('minecraft:is_overworld')
        .id("kubejs:raw_silver_vein");
    event.recipes.createoreexcavation.drilling('immersiveengineering:raw_silver', 'kubejs:raw_silver_vein', TIME.NORMAL)
        .id("kubejs:drilling_raw_silver_normal");

    // === 粗铂矿
    event.recipes.createoreexcavation.vein('{"text": "粗铂矿"}', 'createpropulsion:raw_platinum')
        .placement(128, 8, 114514)
        .biomeWhitelist('minecraft:is_overworld')
        .id("kubejs:raw_platinum_vein");
    event.recipes.createoreexcavation.drilling('createpropulsion:raw_platinum', 'kubejs:raw_platinum_vein', TIME.NORMAL)
        .id("kubejs:drilling_raw_platinum_normal");

    // === 盐矿
    event.recipes.createoreexcavation.vein('{"text": "粗盐矿"}', 'expandeddelight:salt_rock')
        .placement(128, 4, 187000)
        .biomeWhitelist('minecraft:is_overworld')
        .id("kubejs:raw_salt_vein");
    event.recipes.createoreexcavation.drilling('expandeddelight:salt_rock', 'kubejs:raw_salt_vein', TIME.NORMAL)
        .id("kubejs:drilling_raw_salt_normal");
    
    // === 粗锇矿
    event.recipes.createoreexcavation.vein('{"text": "粗锇矿"}', 'mekanism:raw_osmium')
        .placement(128, 4, 652000)
        .biomeWhitelist('minecraft:is_overworld')
        .id("kubejs:mek_raw_osmium_vein");
    event.recipes.createoreexcavation.drilling([coeutil.processingOutput('mekanism:raw_osmium', 0.6)], 'kubejs:mek_raw_osmium_vein', TIME.NORMAL)
        .id("kubejs:drilling_raw_osmium_normal");
    
    // === 粗锡矿
    event.recipes.createoreexcavation.vein('{"text": "粗锡矿"}', 'mekanism:raw_tin')
        .placement(128, 4, 114001)
        .biomeWhitelist('minecraft:is_overworld')
        .id("kubejs:mek_raw_tin_vein");
    event.recipes.createoreexcavation.drilling('mekanism:raw_tin', 'kubejs:mek_raw_tin_vein', TIME.NORMAL)
        .id("kubejs:drilling_raw_tin_normal");
    
    // === 氟石矿
    event.recipes.createoreexcavation.vein('{"text": "氟石矿"}', 'mekanism:fluorite_gem')
        .placement(128, 8, 876903)
        .biomeWhitelist('minecraft:is_overworld')
        .id("kubejs:mek_fluorite_gem_vein");
    event.recipes.createoreexcavation.drilling('mekanism:fluorite_gem', 'kubejs:mek_fluorite_gem_vein', TIME.NORMAL)
        .id("kubejs:drilling_mek_fluorite_gem_normal");


    // ================== 杂项 ==================
    // 古卷合成
    event.shaped(Item.of('quark:ancient_tome', 1), [
        'AB',
        'CD'
    ], {
        A: 'minecraft:netherite_upgrade_smithing_template',
        B: 'quark:soul_sandstone',
        C: 'minecraft:netherite_ingot',
        D: 'minecraft:book'
    });

    event.shapeless(Item.of('minecraft:enchanted_golden_apple', 64), [
        'kubejs:ta_jlpawa'
    ]);

    event.shapeless(Item.of('immersivepetroleum:asphalt', 1), [
        'createdieselgenerators:asphalt_block'
    ]);

    event.shapeless(Item.of('createdieselgenerators:asphalt_block', 1), [
        'immersivepetroleum:asphalt'
    ]);
    // 经验转换
    event.custom({
        type: 'create:mixing',
        ingredients: [{
                type: "neoforge:single",
                amount: 200,
                fluid: "sophisticatedcore:xp_still"
            }],
        results: [
            {
                amount: 10,
                id: "create_enchantment_industry:experience"
            }]
    });

    event.custom({
        type: 'create:mixing',
        ingredients: [{
                type: "neoforge:single",
                amount: 10,
                fluid: "create_enchantment_industry:experience"
            }],
        results: [
            {
                amount: 200,
                id: "sophisticatedcore:xp_still"
            }]
    });
    // 禁用背包嵌套
    event.remove({
        id: "sophisticatedbackpacks:inception_upgrade"
    });
    // 禁用经验桶
    event.remove({
        output: "sophisticatedcore:xp_bucket"
    });
    event.remove({
        id: "create:empty_sophisticatedcore_xp_bucket_of_sophisticatedcore_xp_still"
    });
    event.remove({
        id: "immersiveengineering:jei_bucket_sophisticatedcore_xp_still"
    });
    event.remove({
        id: "create:fill_minecraft_bucket_with_sophisticatedcore_xp_still"
    });
    
    
})