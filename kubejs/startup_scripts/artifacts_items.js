// 将五件低概率生成的 Artifacts 饰品默认稀有度设为史诗。
ItemEvents.modification(event => {
    [
        'artifacts:umbrella',
        'artifacts:cloud_in_a_bottle',
        'artifacts:aqua_dashers',
        'artifacts:running_shoes',
        'artifacts:steadfast_spikes'
    ].forEach(id => event.modify(id, item => {
        item.set('minecraft:rarity', 'epic');
    }));
});

// 从 Artifacts 创造模式栏及其搜索条目移除已禁用饰品。
StartupEvents.modifyCreativeTab('artifacts:main', event => {
    event.remove('artifacts:everlasting_beef');
    event.remove('artifacts:eternal_steak');
    event.remove('artifacts:scarf_of_invisibility');
});
