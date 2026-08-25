// ==================== 机械动力 + 葡园酒香自动化 ====================
// 覆盖 vinery 全部 26 种发酵酒 + 3 种基础果汁
// 流程：葡萄/苹果 → 果汁流体 → 混合配料成为酒液 → 酒瓶灌装
// 每个阶段均提供两条产线：
//   Create   : 搅拌器(mixing) / 灌装(filling)
//   Immersive Engineering: 混合机(mixer) / 灌装机(bottling_machine)
// 配料比例与 vinery 发酵桶原版配方一致；基底果汁按颜色归类
// （red_*→红葡萄汁、white_*→白葡萄汁、apple→苹果汁）
// 注意：刻意使用 ES5 语法（var + for 循环），避免 Rhino 引擎对
// 展开运算符/解构等特性的兼容问题导致整个脚本静默失效。

ServerEvents.recipes(event => {
    var MB = 250;
    var JUICE_RED = 'kubejs:red_grapejuice_fluid';
    var JUICE_WHITE = 'kubejs:white_grapejuice_fluid';
    var JUICE_APPLE = 'kubejs:apple_juice_fluid';

    // ---------- 基础果汁 ----------
    // 红/白葡萄汁：对应葡萄×2 + 水；苹果汁：苹果×2 + 水
    var juices = [
        [JUICE_RED, 'vinery:red_grape'],
        [JUICE_WHITE, 'vinery:white_grape'],
        [JUICE_APPLE, 'minecraft:apple']
    ];
    for (var i = 0; i < juices.length; i++) {
        var juiceOut = juices[i][0];
        var fruit = juices[i][1];
        event.custom({
            type: 'create:mixing',
            ingredients: [
                { item: fruit }, { item: fruit },
                { type: 'neoforge:single', amount: MB, fluid: 'minecraft:water' }
            ],
            results: [{ amount: MB, id: juiceOut }]
        });
        event.custom({
            type: 'immersiveengineering:mixer',
            energy: 800,
            fluid: { amount: MB, tag: 'minecraft:water' },
            inputs: [{ basePredicate: { item: fruit }, count: 2 }],
            result: { amount: MB, id: juiceOut }
        });
    }

    // ---------- 酒类数据表 ----------
    // [成品酒ID, 酒液流体ID, 果汁基底ID, 配料列表]（配料与原版发酵桶一致）
    var wines = [
        // 红葡萄酒系（红葡萄汁基底）
        ['vinery:red_wine',           'kubejs:red_wine_fluid',           JUICE_RED,   ['minecraft:sugar']],
        ['vinery:noir_wine',          'kubejs:noir_wine_fluid',          JUICE_RED,   ['minecraft:sweet_berries']],
        ['vinery:cherry_wine',        'kubejs:cherry_wine_fluid',        JUICE_RED,   ['vinery:cherry']],
        ['vinery:strad_wine',         'kubejs:strad_wine_fluid',         JUICE_RED,   ['minecraft:cocoa_beans', 'minecraft:sugar']],
        ['vinery:stal_wine',          'kubejs:stal_wine_fluid',          JUICE_RED,   ['minecraft:cocoa_beans', 'minecraft:sugar']],
        ['vinery:cristel_wine',       'kubejs:cristel_wine_fluid',       JUICE_RED,   ['minecraft:sugar', 'minecraft:feather', 'minecraft:blaze_rod']],
        ['vinery:bottle_mojang_noir', 'kubejs:bottle_mojang_noir_fluid', JUICE_RED,   ['minecraft:honey_bottle', 'vinery:cherry', 'vinery:red_wine']],
        ['vinery:chenet_wine',        'kubejs:chenet_wine_fluid',        JUICE_RED,   ['minecraft:spider_eye', 'minecraft:honey_bottle']],
        ['vinery:lilitu_wine',        'kubejs:lilitu_wine_fluid',        JUICE_RED,   ['minecraft:honey_bottle', 'vinery:cherry']],
        ['vinery:magnetic_wine',      'kubejs:magnetic_wine_fluid',      JUICE_RED,   ['minecraft:iron_ingot']],
        ['vinery:jo_special_mixture', 'kubejs:jo_special_mixture_fluid', JUICE_RED,   ['minecraft:fermented_spider_eye']],
        ['vinery:bolvar_wine',        'kubejs:bolvar_wine_fluid',        JUICE_RED,   ['minecraft:honey_bottle', 'vinery:cherry']],
        ['vinery:chorus_wine',        'kubejs:chorus_wine_fluid',        JUICE_RED,   ['minecraft:chorus_fruit']],
        // 白葡萄酒系（白葡萄汁基底）
        ['vinery:glowing_wine',       'kubejs:glowing_wine_fluid',       JUICE_WHITE, ['minecraft:glow_berries']],
        ['vinery:solaris_wine',       'kubejs:solaris_wine_fluid',       JUICE_WHITE, ['minecraft:honey_bottle', 'minecraft:sweet_berries']],
        ['vinery:jellie_wine',        'kubejs:jellie_wine_fluid',        JUICE_WHITE, ['vinery:apple_wine', 'vinery:chenet_wine', 'vinery:bolvar_wine']],
        ['vinery:clark_wine',         'kubejs:clark_wine_fluid',         JUICE_WHITE, ['minecraft:sugar']],
        ['vinery:creepers_crush',     'kubejs:creepers_crush_fluid',     JUICE_WHITE, ['minecraft:gunpowder']],
        ['vinery:eiswein',            'kubejs:eiswein_fluid',            JUICE_WHITE, ['minecraft:snowball']],
        ['vinery:kelp_cider',         'kubejs:kelp_cider_fluid',         JUICE_WHITE, ['minecraft:kelp']],
        ['vinery:villagers_fright',   'kubejs:villagers_fright_fluid',   JUICE_WHITE, ['minecraft:arrow']],
        ['vinery:aegis_wine',         'kubejs:aegis_wine_fluid',         JUICE_WHITE, ['minecraft:sugar', 'minecraft:kelp', 'minecraft:iron_ingot']],
        ['vinery:mellohi_wine',       'kubejs:mellohi_wine_fluid',       JUICE_WHITE, ['minecraft:sugar', 'minecraft:glowstone_dust']],
        // 苹果系（苹果汁基底）
        ['vinery:apple_cider',        'kubejs:apple_cider_fluid',        JUICE_APPLE, ['minecraft:sugar']],
        ['vinery:apple_wine',         'kubejs:apple_wine_fluid',         JUICE_APPLE, ['vinery:apple_juice']],
        ['vinery:mead',               'kubejs:mead_fluid',               JUICE_APPLE, ['minecraft:honey_bottle', 'minecraft:sugar']]
    ];

    // ---------- 生成配方：酒液混合 + 酒瓶灌装 ----------
    for (var w = 0; w < wines.length; w++) {
        var wineId = wines[w][0];
        var fluidId = wines[w][1];
        var juiceId = wines[w][2];
        var ing = wines[w][3];

        // Create 搅拌输入：全部配料 + 果汁
        var createIngredients = [];
        for (var k = 0; k < ing.length; k++) {
            createIngredients.push({ item: ing[k] });
        }
        createIngredients.push({ type: 'neoforge:single', amount: MB, fluid: juiceId });

        // IE 混合机输入：全部配料
        var ieInputs = [];
        for (var m = 0; m < ing.length; m++) {
            ieInputs.push({ basePredicate: { item: ing[m] }, count: 1 });
        }

        // 酒液：全部配料 + 果汁 250mB → 酒液 250mB
        event.custom({
            type: 'create:mixing',
            ingredients: createIngredients,
            results: [{ amount: MB, id: fluidId }]
        });
        event.custom({
            type: 'immersiveengineering:mixer',
            energy: 800,
            fluid: { amount: MB, tag: juiceId },
            inputs: ieInputs,
            result: { amount: MB, id: fluidId }
        });

        // 瓶装：酒瓶 + 酒液 250mB → 成品酒
        event.custom({
            type: 'create:filling',
            ingredients: [
                { item: 'vinery:wine_bottle' },
                { type: 'neoforge:single', amount: MB, fluid: fluidId }
            ],
            results: [{ id: wineId }]
        });
        event.custom({
            type: 'immersiveengineering:bottling_machine',
            fluid: { amount: MB, tag: fluidId },
            inputs: [{ item: 'vinery:wine_bottle' }],
            results: [{ id: wineId }]
        });
    }
})
