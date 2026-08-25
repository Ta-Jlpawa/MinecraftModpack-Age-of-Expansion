// ==================== Oritech 冗余金属材料移除 ====================
// 删除与其他模组重合的基础金属处理链物品：
//   镍/铂（IE、CreatePropulsion 提供完整链条）
//   铁/金/铜 粉尘与中间品（原版 + Mekanism + Create 已覆盖）
//   铀粗矿/粉尘/晶体（IE / Mekanism 提供，反应堆燃料棒与钚链保留）
//   钢、琥珀金 Electrum（IE 提供同名物品）
// 保留：钚系燃料链、Oritech 独有合金（Adamant/Biosteel/Duratium/Energite/Prometheum）、
//       Fluxite、铀/钚燃料棒、煤炭粉、石英粉、硅系等
// 配套：client_scripts/oritech_ore_remove.js（JEI 隐藏）、
//       global_packs/required_data/SFSRModConnector（矿物掉落表替换）
// 详细说明见 .docs/Oritech冗余材料清理说明.md

const REMOVED_MATERIALS = [
    // 镍系列
    'oritech:raw_nickel', 'oritech:nickel_clump', 'oritech:small_nickel_clump',
    'oritech:nickel_dust', 'oritech:small_nickel_dust', 'oritech:nickel_gem',
    'oritech:nickel_ingot', 'oritech:nickel_nugget',
    // 铂系列
    'oritech:raw_platinum', 'oritech:platinum_clump', 'oritech:small_platinum_clump',
    'oritech:platinum_dust', 'oritech:small_platinum_dust', 'oritech:platinum_gem',
    'oritech:platinum_ingot', 'oritech:platinum_nugget',
    // 铁系列
    'oritech:iron_clump', 'oritech:small_iron_clump',
    'oritech:iron_dust', 'oritech:small_iron_dust', 'oritech:iron_gem',
    // 铜系列
    'oritech:copper_clump', 'oritech:small_copper_clump',
    'oritech:copper_dust', 'oritech:small_copper_dust', 'oritech:copper_gem', 'oritech:copper_nugget',
    // 金系列
    'oritech:gold_clump', 'oritech:small_gold_clump',
    'oritech:gold_dust', 'oritech:small_gold_dust', 'oritech:gold_gem',
    // 铀系列（燃料棒与钚保留）
    'oritech:raw_uranium', 'oritech:uranium_dust', 'oritech:small_uranium_dust', 'oritech:uranium_gem',
    // 钢 / 琥珀金（IE 重合）
    'oritech:steel_ingot', 'oritech:steel_dust',
    'oritech:electrum_ingot', 'oritech:electrum_dust'
];

// ---------- 标签摘除 ----------
// 将被删物品从通用标签中移除，使保留的配方自动改用 IE / Mekanism / CreatePropulsion 物品
ServerEvents.tags('item', event => {
    const TAGS = [
        'c:dusts', 'c:ingots', 'c:nuggets', 'c:clumps', 'c:raw_materials',
        'c:dusts/nickel', 'c:dusts/platinum', 'c:dusts/iron', 'c:dusts/copper',
        'c:dusts/gold', 'c:dusts/uranium', 'c:dusts/steel', 'c:dusts/electrum',
        'c:ingots/nickel', 'c:ingots/platinum', 'c:ingots/steel', 'c:ingots/electrum',
        'c:nuggets/nickel', 'c:nuggets/platinum', 'c:nuggets/copper',
        'c:clumps/nickel', 'c:clumps/platinum', 'c:clumps/iron', 'c:clumps/copper', 'c:clumps/gold',
        'c:raw_materials/nickel', 'c:raw_materials/platinum', 'c:raw_materials/uranium'
    ];
    for (let tag of TAGS) {
        for (let item of REMOVED_MATERIALS) {
            event.remove(tag, item);
        }
    }
});

ServerEvents.recipes(event => {
    // ---------- 第一步：配方替换（必须在删除之前执行） ----------

    // 深层钻井：资源节点产出改为其他模组的粗金属
    event.replaceOutput({ id: 'oritech:deepdrill/nickel' }, 'oritech:raw_nickel', 'immersiveengineering:raw_nickel');
    event.replaceOutput({ id: 'oritech:deepdrill/platinum' }, 'oritech:raw_platinum', 'createpropulsion:raw_platinum');
    event.replaceOutput({ id: 'oritech:deepdrill/uranium' }, 'oritech:raw_uranium', 'mekanism:raw_uranium');

    // 研磨机矿石处理：产出粗金属改为其他模组物品（保留研磨机处理矿石的功能）
    event.replaceOutput({ id: /^oritech:grinder\/ore\/(iron|nickel)$/ }, 'oritech:raw_nickel', 'immersiveengineering:raw_nickel');
    event.replaceOutput({ id: /^oritech:grinder\/ore\/(nickel|platinum)$/ }, 'oritech:raw_platinum', 'createpropulsion:raw_platinum');
    event.replaceOutput({ id: /^oritech:grinder\/(ore\/uranium|uraniumcrystal)$/ }, 'oritech:raw_uranium', 'mekanism:raw_uranium');
    event.replaceOutput({ id: 'oritech:pulverizer/uraniumcrystal' }, 'oritech:raw_uranium', 'mekanism:raw_uranium');

    // 跨模组矿物加工的伴生产物替换（避免整条删除导致其他模组矿石无法在 Oritech 机械中加工）
    event.replaceOutput({ id: 'oritech:grinder/compat/immersiveengineering/ore/silver' },
        'oritech:copper_dust', 'mekanism:dust_copper');
    event.replaceOutput({ id: 'oritech:grinder/compat/mekanism/ore/osmium' },
        'oritech:raw_platinum', 'createpropulsion:raw_platinum');
    event.replaceOutput({ id: 'oritech:grinder/compat/mekanism/raw/osmium' },
        'oritech:small_platinum_clump', 'createpropulsion:platinum_nugget');

    // 铀加工线：铀粉尘改用 Mekanism 铀尘（保留钚副产物，衔接反应堆燃料链）
    event.replaceOutput({ id: /^oritech:(grinder|pulverizer)\/uranium$/ }, 'oritech:uranium_dust', 'mekanism:dust_uranium');
    event.replaceOutput({ id: /^oritech:centrifuge\/compat\/clump\/crushed_uranium$/ }, 'oritech:uranium_dust', 'mekanism:dust_uranium');
    event.replaceOutput({ id: /^oritech:centrifuge\/fluid\/compat\/clumpwet\/crushed_uranium$/ }, 'oritech:uranium_dust', 'mekanism:dust_uranium');
    event.replaceOutput({ id: 'oritech:splashing/compat/create/uraniumclump' }, 'oritech:uranium_dust', 'mekanism:dust_uranium');

    // Mekanism 离心机兼容：铂尘副产物改为 CreatePropulsion 铂粒
    event.replaceOutput({ id: /^oritech:centrifuge\/compat\/mekanism\/clump\/(lead|osmium|tin)$/ },
        'oritech:small_platinum_dust', 'createpropulsion:platinum_nugget');

    // 粒子对撞机：金尘×2 → 铂（原产出铂尘，改为 CreatePropulsion 铂锭，保留物质转化玩法）
    event.replaceOutput({ id: 'oritech:particle/platinum_dust' }, 'oritech:platinum_dust', 'createpropulsion:platinum_ingot');

    // 回收产线：金属工具/装置回收产出改为 Mekanism 粉尘或原版粒
    const RECYCLE_REPLACEMENTS = [
        ['oritech:iron_dust', 'mekanism:dust_iron'],
        ['oritech:copper_dust', 'mekanism:dust_copper'],
        ['oritech:gold_dust', 'mekanism:dust_gold'],
        ['oritech:small_iron_dust', 'minecraft:iron_nugget'],
        ['oritech:small_gold_dust', 'minecraft:gold_nugget']
    ];
    for (let [from, to] of RECYCLE_REPLACEMENTS) {
        event.replaceOutput({ id: /recycle/ }, from, to);
    }
    // 注：small_copper_dust 回收配方无对应替代物，由下方删除规则自动移除

    // 组装机铀燃料棒：铀晶体×2 → 铀锭×2（通用标签，IE / Mekanism 铀锭均可）
    for (let id of ['oritech:assembler/uranpelletbasic', 'oritech:assembler/uranpelletbetter', 'oritech:assembler/uranpelletult']) {
        event.replaceInput({ id: id }, 'oritech:uranium_gem', '#c:ingots/uranium');
    }

    // ---------- 第二步：删除所有涉及被删物品的剩余配方 ----------
    // 覆盖：镍/铂/铁/金/铜/铀 自链转化（研磨、粉碎、离心、熔炼、锻造）、
    //       钢/琥珀金生产链、原子炉晶体配方、兼容模组配方（IE 合金炉 / Create 搅拌 / 洗矿等）
    event.remove({ output: REMOVED_MATERIALS });
    event.remove({ input: REMOVED_MATERIALS });
});
