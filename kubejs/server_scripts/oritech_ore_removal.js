// ==================== Oritech 冗余矿石移除 ====================
// 移除 Oritech 的镍矿石、深层镍矿石、深层铂矿石、深层铀矿石
// （这些矿物在其他模组中已有：镍/铀 → IE & Mekanism，铂 → CreatePropulsion）
// 处理内容：标签摘除 / 配方删除 / 世界生成停止见数据包 SFSRModConnector
// 注意：末地的 oritech:endstone_platinum_ore 为 Oritech 独有，保留不动

const REMOVED_ORES = [
    'oritech:nickel_ore',
    'oritech:deepslate_nickel_ore',
    'oritech:deepslate_platinum_ore',
    'oritech:deepslate_uranium_ore'
];

// 从通用标签中摘除（使研磨/粉碎配方不再匹配这些方块，
// 同时保证 IE / Mekanism / CreatePropulsion 的同类矿石不受影响）
ServerEvents.tags('block', event => {
    for (let ore of REMOVED_ORES) {
        event.remove('c:ores', ore);
        event.remove('c:ores_in_ground/deepslate', ore);
        event.remove('c:ores_in_ground/stone', ore);
    }
    event.remove('c:ores/nickel', 'oritech:nickel_ore');
    event.remove('c:ores/nickel', 'oritech:deepslate_nickel_ore');
    event.remove('c:ores/platinum', 'oritech:deepslate_platinum_ore');
    event.remove('c:ores/uranium', 'oritech:deepslate_uranium_ore');
});

ServerEvents.tags('item', event => {
    for (let ore of REMOVED_ORES) {
        event.remove('c:ores', ore);
    }
    event.remove('c:ores/nickel', 'oritech:nickel_ore');
    event.remove('c:ores/nickel', 'oritech:deepslate_nickel_ore');
    event.remove('c:ores/platinum', 'oritech:deepslate_platinum_ore');
    event.remove('c:ores/uranium', 'oritech:deepslate_uranium_ore');
});

ServerEvents.recipes(event => {
    // 铀矿研磨/粉碎配方（输入直接引用深层铀矿石）
    event.remove({ id: 'oritech:grinder/uraniumore' });
    event.remove({ id: 'oritech:pulverizer/uraniumore' });
    // 兜底：移除任何以这四种矿石作为输入或输出的配方
    for (let ore of REMOVED_ORES) {
        event.remove({ input: ore });
        event.remove({ output: ore });
    }
});
