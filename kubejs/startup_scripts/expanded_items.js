StartupEvents.registry('item', event => {
    // 古卷前置：古书
    event.create('ta_jlpawa')
        .displayName('彩蛋物品！')
        .maxStackSize(1)
        .glow(true)
        .rarity('uncommon');
    
})