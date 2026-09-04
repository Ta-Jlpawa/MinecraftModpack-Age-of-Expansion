StartupEvents.registry('item', event => {
    // 彩蛋物品
    event.create('ta_jlpawa')
        .displayName('彩蛋物品！')
        .maxStackSize(1)
        .glow(true)
        .rarity('uncommon');
    
})