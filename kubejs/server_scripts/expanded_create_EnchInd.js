ServerEvents.recipes(event => {
    // 机械动力：附魔工业修改
    // 配方修改与移除
    event.remove({
            id: "create_enchantment_industry:compacting/experience_cake_base"
        })

    event.custom({
        type: 'create:compacting',
        heat_requirement: "superheated",
        ingredients: [
            {   
                tag: "c:eggs"
            },{   
                item: "minecraft:sugar"
            },{
                item: "minecraft:lapis_block"
            }],
        results: [
            {
                id: "create_enchantment_industry:experience_cake_base"
            }]
    });

});