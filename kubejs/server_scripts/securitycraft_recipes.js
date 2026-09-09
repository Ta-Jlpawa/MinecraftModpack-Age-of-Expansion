// 移除强化方块、强化铁门与强化工具配方；设备原料替换位于 AgeOfExpansionModConnector 数据包。
ServerEvents.recipes(event => {
    event.remove({ output: /^securitycraft:reinforced_/ });
    event.remove({ output: 'securitycraft:door_indestructible_iron_item' });
    event.remove({ output: /^securitycraft:universal_block_reinforcer_lvl[123]$/ });
    event.remove({ output: 'securitycraft:universal_block_remover' });
    event.remove({ type: 'securitycraft:block_reinforcing_recipe' });
    event.remove({ type: 'securitycraft:block_unreinforcing_recipe' });

    // 可疑方块地雷改用可获取的普通方块，并添加燧石以区别普通沙子、砂砾地雷配方。
    ['sand', 'gravel'].forEach(material => {
        const recipeId = `securitycraft:suspicious_${material}_mine`;
        event.remove({ id: recipeId });
        event.shapeless(recipeId, [
            `minecraft:${material}`,
            'minecraft:flint',
            'securitycraft:mine'
        ]).id(recipeId);
    });
});
