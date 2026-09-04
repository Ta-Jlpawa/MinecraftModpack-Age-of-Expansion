// ==================== Oritech 铀燃料球补充配方 ====================
// 原组装机配方（oritech:assembler/uranpellet*）已改为铀锭合成，
// 此处按整合包设计追加三条更基础的获取途径：
//   1. 工作台：1×粗铀矿(#c:raw_materials/uranium) → 1×小铀球
//   2. 粉碎：1×铀锭(#c:ingots/uranium) → 4×小铀球（Mekanism 粉碎机 + IE 粉碎机多方块）
//   3. 加压反应室(PRC)：水1000mB + 氧化铀气200mB(mek) + 铀粉 → 1×铀球
//      注：化学氧化机仅支持"物品→气体"，无法接受流体/输出物品，经确认改用 PRC

ServerEvents.recipes(event => {
    // 1. 粗铀矿工作台直接合成小铀球
    event.shapeless('oritech:small_uranium_pellet', ['#c:raw_materials/uranium']);

    // 2a. Mekanism 粉碎机：铀锭 → 小铀球×4
    event.custom({
        type: 'mekanism:crushing',
        input: { count: 1, tag: 'c:ingots/uranium' },
        output: { count: 4, id: 'oritech:small_uranium_pellet' }
    });

    // 2b. IE 粉碎机多方块：铀锭 → 小铀球×4
    event.custom({
        type: 'immersiveengineering:crusher',
        energy: 3000,
        input: { tag: 'c:ingots/uranium' },
        result: { count: 4, id: 'oritech:small_uranium_pellet' }
    });

    // 3. 加压反应室：水 + 氧化铀气 + 铀粉 → 铀球
    //    氧化铀气来源：Mek 化学氧化机氧化黄饼(250mB/个)
    event.custom({
        type: 'mekanism:reaction',
        chemical_input: { amount: 200, chemical: 'mekanism:uranium_oxide' },
        fluid_input: { amount: 1000, tag: 'minecraft:water' },
        item_input: { count: 1, tag: 'c:dusts/uranium' },
        item_output: { count: 1, id: 'oritech:uranium_pellet' },
        duration: 200,
        energy_required: 5000
    });
});
