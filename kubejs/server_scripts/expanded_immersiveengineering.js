ServerEvents.recipes(event => {
    // 沉浸工程拓展

    // 园艺玻璃罩拓展
    event.custom({
        //辣椒
        type: "immersiveengineering:cloche",
        input: {
            item: "expandeddelight:chili_pepper_seeds"
        },
        render: {
            type: "immersiveengineering:crop",
            block: "expandeddelight:chili_pepper_crop"
        },
        results: [
            {
                id: "expandeddelight:chili_pepper"
            }
        ],
        soil: {
            item: "minecraft:dirt"
        },
        time: 800
    });
        // 树苗
    event.custom({
        type: "immersiveengineering:cloche",
        input: {
            item: "minecraft:oak_sapling"
        },
        render: {
            type: "immersiveengineering:stacking",
            block: "minecraft:oak_sapling"
        },
        results: [
            {
                id: "minecraft:stick"
            },
            {
                chance: 0.5,
                output: {
                    id: "minecraft:oak_log"
                }
            },
            {
                chance: 0.005,
                output: {
                    id: "minecraft:dead_bush"
                }
            }
        ],
        soil: {
            item: "minecraft:dirt"
        },
        time: 800
    });
    event.custom({
        type: "immersiveengineering:cloche",
        input: {
            item: "minecraft:birch_sapling"
        },
        render: {
            type: "immersiveengineering:stacking",
            block: "minecraft:birch_sapling"
        },
        results: [
            {
                id: "minecraft:stick"
            },
            {
                chance: 0.5,
                output: {
                    id: "minecraft:birch_log"
                }
            }
        ],
        soil: {
            item: "minecraft:dirt"
        },
        time: 800
    });
    event.custom({
        type: "immersiveengineering:cloche",
        input: {
            item: "minecraft:acacia_sapling"
        },
        render: {
            type: "immersiveengineering:stacking",
            block: "minecraft:acacia_sapling"
        },
        results: [
            {
                id: "minecraft:stick"
            },
            {
                chance: 0.5,
                output: {
                    id: "minecraft:acacia_log"
                }
            }
        ],
        soil: {
            item: "minecraft:dirt"
        },
        time: 800
    });
    event.custom({
        type: "immersiveengineering:cloche",
        input: {
            item: "minecraft:dark_oak_sapling"
        },
        render: {
            type: "immersiveengineering:stacking",
            block: "minecraft:dark_oak_sapling"
        },
        results: [
            {
                id: "minecraft:stick"
            },
            {
                chance: 0.5,
                output: {
                    id: "minecraft:dark_oak_log"
                }
            }
        ],
        soil: {
            item: "minecraft:dirt"
        },
        time: 800
    });
    event.custom({
        type: "immersiveengineering:cloche",
        input: {
            item: "minecraft:cherry_sapling"
        },
        render: {
            type: "immersiveengineering:stacking",
            block: "minecraft:cherry_sapling"
        },
        results: [
            {
                id: "minecraft:stick"
            },
            {
                chance: 0.5,
                output: {
                    id: "minecraft:cherry_log"
                }
            }
        ],
        soil: {
            item: "minecraft:dirt"
        },
        time: 800
    });
    event.custom({
        type: "immersiveengineering:cloche",
        input: {
            item: "minecraft:jungle_sapling"
        },
        render: {
            type: "immersiveengineering:stacking",
            block: "minecraft:jungle_sapling"
        },
        results: [
            {
                id: "minecraft:stick"
            },
            {
                chance: 0.5,
                output: {
                    id: "minecraft:jungle_log"
                }
            }
        ],
        soil: {
            item: "minecraft:dirt"
        },
        time: 800
    });
    event.custom({
        type: "immersiveengineering:cloche",
        input: {
            item: "minecraft:spruce_sapling"
        },
        render: {
            type: "immersiveengineering:stacking",
            block: "minecraft:spruce_sapling"
        },
        results: [
            {
                id: "minecraft:stick"
            },
            {
                chance: 0.5,
                output: {
                    id: "minecraft:spruce_log"
                }
            }
        ],
        soil: {
            item: "minecraft:dirt"
        },
        time: 800
    });

    // 沉浸石油拓展
    // 焦煤塔-煤炭-沥青配方
    event.custom({
        type: "immersivepetroleum:coker",
        energy: 15360,
        input: {
            basePredicate: {
            item: "minecraft:coal"
            },
            count: 2
        },
        inputfluid: {
            amount: 125,
            tag: "minecraft:water"
        },
        result: {
            count: 2,
            id: "immersivepetroleum:bitumen"
        },
        resultfluid: {
            amount: 10,
            id: "immersiveengineering:creosote"
        },
        time: 30
    });
    // 焦煤塔-木炭-沥青配方
    event.custom({
        type: "immersivepetroleum:coker",
        energy: 15360,
        input: {
            basePredicate: {
            item: "minecraft:charcoal"
            },
            count: 4
        },
        inputfluid: {
            amount: 75,
            tag: "minecraft:water"
        },
        result: {
            count: 2,
            id: "immersivepetroleum:bitumen"
        },
        resultfluid: {
            amount: 5,
            id: "immersiveengineering:creosote"
        },
        time: 20
    });
})