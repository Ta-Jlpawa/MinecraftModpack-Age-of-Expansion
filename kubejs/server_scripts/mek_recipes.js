ServerEvents.recipes(event => {

    // 原子分解机
    event.remove({ id: "mekanism:atomic_disassembler" })
    event.shaped("mekanism:atomic_disassembler",[
        'ACA',
        'ABA',
        ' D '
    ],
    {
        A: "mekanism:alloy_atomic",
        B: "mekanism:pellet_polonium",
        C: "mekanism:energy_tablet",
        D: "#c:ingots/refined_obsidian"
    })
    // meka工具
    event.replaceInput(
        { id: "mekanism:meka_tool" },
        "mekanism:pellet_polonium",
        "mekanism:pellet_plutonium"
    )
    event.replaceInput(
        { id: "mekanism:meka_tool" },
        "mekanism:basic_induction_cell",
        "mekanism:pellet_antimatter"
    )
    // 维度稳定锚
    event.replaceInput(
        { id: "mekanism:dimensional_stabilizer" },
        "minecraft:diamond_block",
        "minecraft:respawn_anchor"
    )
    // 锚升级
    event.replaceInput(
        { id: "mekanism:upgrade/anchor" },
        "#c:dusts/diamond",
        "mekanism:dimensional_stabilizer"
    )
    // 精英加压管道
    event.replaceInput(
        { id: "mekanism:transmitter/pressurized_tube/elite" },
        "mekanism:advanced_pressurized_tube",
        "mekanism:basic_pressurized_tube"
    )
    // 终极加压管道
    event.replaceInput(
        { id: "mekanism:transmitter/pressurized_tube/ultimate" },
        "mekanism:elite_pressurized_tube",
        "mekanism:basic_pressurized_tube"
    )

})