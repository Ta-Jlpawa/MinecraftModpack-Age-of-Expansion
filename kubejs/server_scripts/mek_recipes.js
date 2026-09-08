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
    // 线缆升级，沿用原ID覆盖数据包配方
    const transmitterTypes = [
        "universal_cable",
        "mechanical_pipe",
        "pressurized_tube",
        "logistical_transporter",
        "thermodynamic_conductor"
    ]
    const transmitterTiers = [
        ["advanced", "alloy_infused"],
        ["elite", "alloy_reinforced"],
        ["ultimate", "alloy_atomic"]
    ]
    transmitterTiers.forEach(([tier, alloy]) => {
        transmitterTypes.forEach(type => {
            const id = `mekanism:transmitter/${type}/${tier}`
            const basic = `mekanism:basic_${type}`
            event.remove({ id: id })
            event.shapeless(Item.of(`mekanism:${tier}_${type}`, 4), [
                basic, basic, basic, basic,
                `mekanism:${alloy}`
            ]).id(id)
        })
        // 线缆升级安装器
        event.shaped(Item.of(`ageofexpansionmodpackfix:${tier}_transmitter_installer`, 1), [
            ' A ',
            ' B ',
            ' A '
        ], {
            A: `mekanism:${alloy}`,
            B: "#minecraft:planks"
        }).id(`ageofexpansionmodpackfix:${tier}_transmitter_installer`)
    })

})
