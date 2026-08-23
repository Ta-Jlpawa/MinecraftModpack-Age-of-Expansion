ServerEvents.recipes(event => {
    // 机械动力 + 葡园酒香自动化
    // 红葡萄汁
    event.custom({
        type: 'create:mixing',
        ingredients: [
            {   
                item: "vinery:red_grape"
            },{   
                item: "vinery:red_grape"
            },{
                type: "neoforge:single",
                amount: 250,
                fluid: "minecraft:water"
            }],
        results: [
            {
                amount: 250,
                id: "kubejs:red_grapejuice_fluid"
            }]
    });
    event.custom({
        type: "immersiveengineering:mixer",
        energy: 800,
        fluid: {
            amount: 250,
            tag: "minecraft:water" // 可使用fluid/tag
        },
        inputs: [
            {
                "basePredicate": {
                "item": "vinery:red_grape" // 可使用item/tag
                },
                "count": 2
                //item: "vinery:red_grape",
            }
        ],
        result: {
            amount: 250,
            id: "kubejs:red_grapejuice_fluid"
        }
    });
    // 液态樱桃酒
    event.custom({
        type: 'create:mixing',
        ingredients: [{item: "vinery:cherry"},{item: "vinery:cherry"},{type: "neoforge:single",amount: 250,fluid: "kubejs:red_grapejuice_fluid"}],
        results: [{amount: 250,id: "kubejs:cherry_wine_fluid"}]
    });
    event.custom({
        type: "immersiveengineering:mixer",
        energy: 800,
        fluid: {amount: 250,tag: "kubejs:red_grapejuice_fluid"},
        inputs: [{"basePredicate": {"item": "vinery:cherry"},"count": 2}],
        result: {amount: 250,id: "kubejs:cherry_wine_fluid"}
    });
    // 樱桃酒
    event.custom({
        type: 'create:filling',
        ingredients: [{"item": "vinery:wine_bottle"},{type: "neoforge:single",amount: 250,fluid: "kubejs:cherry_wine_fluid"}],
        results: [{id: "vinery:cherry_wine"}]
    });
    event.custom({
        type: "immersiveengineering:bottling_machine",
        fluid: {amount: 250,tag: "kubejs:cherry_wine_fluid"},
        input: {item: "vinery:wine_bottle"},
        results: [{id: "vinery:cherry_wine"}]
    });
    // 液态诺耶红葡萄酒
    event.custom({
        type: 'create:mixing',
        ingredients: [{item: "vinery:cherry"},{item: "vinery:cherry"},{type: "neoforge:single",amount: 250,fluid: "kubejs:red_grapejuice_fluid"}],
        results: [{amount: 250,id: "kubejs:noir_wine_fluid"}]
    });
    event.custom({
        type: "immersiveengineering:mixer",
        energy: 800,
        fluid: {amount: 250,tag: "kubejs:red_grapejuice_fluid"},
        inputs: [{"basePredicate": {"item": "vinery:cherry"},"count": 2}],
        result: {amount: 250,id: "kubejs:noir_wine_fluid"}
    });
    // 诺耶红葡萄酒
    event.custom({
        type: 'create:filling',
        ingredients: [{"item": "vinery:wine_bottle"},{type: "neoforge:single",amount: 250,fluid: "kubejs:noir_wine_fluid"}],
        results: [{id: "vinery:noir_wine"}]
    });
    event.custom({
        type: "immersiveengineering:bottling_machine",
        fluid: {amount: 250,tag: "kubejs:noir_wine_fluid"},
        input: {item: "vinery:wine_bottle"},
        results: [{id: "vinery:noir_wine"}]
    });
    // 白葡萄汁
    event.custom({
        type: 'create:mixing',
        ingredients: [{item: "vinery:white_grape"},{item: "vinery:white_grape"},{type: "neoforge:single",amount: 250,fluid: "minecraft:water"}],
        results: [{amount: 250,id: "kubejs:white_grapejuice_fluid"}]});
    event.custom({
        type: "immersiveengineering:mixer",
        energy: 800,
        fluid: {amount: 250,tag: "minecraft:water"},
        inputs: [{"basePredicate": {"item": "vinery:white_grape"},"count": 2}],
        result: {amount: 250,id: "kubejs:white_grapejuice_fluid"}
    });
    // 液态阳光白葡萄酒
    event.custom({
        type: 'create:mixing',
        ingredients: [{item: "minecraft:glow_berries"},{item: "minecraft:glow_berries"},{type: "neoforge:single",amount: 250,fluid: "kubejs:white_grapejuice_fluid"}],
        results: [{amount: 250,id: "kubejs:glowing_wine_fluid"}]
    });
    event.custom({
        type: "immersiveengineering:mixer",
        energy: 800,
        fluid: {amount: 250,tag: "kubejs:white_grapejuice_fluid"},
        inputs: [{"basePredicate": {"item": "minecraft:glow_berries"},"count": 2}],
        result: {amount: 250,id: "kubejs:glowing_wine_fluid"}
    });
    // 阳光白葡萄酒
    event.custom({
        type: 'create:filling',
        ingredients: [{"item": "vinery:wine_bottle"},{type: "neoforge:single",amount: 500,fluid: "kubejs:glowing_wine_fluid"}],
        results: [{id: "vinery:glowing_wine"}]
    });
    event.custom({
        type: "immersiveengineering:bottling_machine",
        fluid: {amount: 500,tag: "kubejs:glowing_wine_fluid"},
        input: {item: "vinery:wine_bottle"},
        results: [{id: "vinery:glowing_wine"}]
    });

})