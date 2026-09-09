// 从 JEI 隐藏强化方块、强化铁门与已禁用的强化工具。
RecipeViewerEvents.removeEntriesCompletely('item', event => {
    event.remove(/^securitycraft:reinforced_/);
    event.remove('securitycraft:door_indestructible_iron_item');
    event.remove(/^securitycraft:universal_block_reinforcer_lvl[123]$/);
    event.remove('securitycraft:universal_block_remover');
});
