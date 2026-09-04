ServerEvents.recipes(event => {
    // ================== 杂项 ==================
    // 古卷合成
    event.shaped(Item.of('quark:ancient_tome', 1), [
        'AB',
        'CD'
    ], {
        A: 'minecraft:netherite_upgrade_smithing_template',
        B: 'quark:soul_sandstone',
        C: 'minecraft:netherite_ingot',
        D: 'minecraft:book'
    });
    // 彩蛋
    event.shapeless(Item.of('minecraft:enchanted_golden_apple', 64), [
        'kubejs:ta_jlpawa'
    ]);
    // 沥青块转换
    event.shapeless(Item.of('immersivepetroleum:asphalt', 1), [
        'createdieselgenerators:asphalt_block'
    ]);
    event.shapeless(Item.of('createdieselgenerators:asphalt_block', 1), [
        'immersivepetroleum:asphalt'
    ]);
    // 经验转换
    event.custom({
        type: 'create:mixing',
        ingredients: [{
                type: "neoforge:single",
                amount: 200,
                fluid: "sophisticatedcore:xp_still"
            }],
        results: [
            {
                amount: 10,
                id: "create_enchantment_industry:experience"
            }]
    });
    event.custom({
        type: 'create:mixing',
        ingredients: [{
                type: "neoforge:single",
                amount: 10,
                fluid: "create_enchantment_industry:experience"
            }],
        results: [
            {
                amount: 200,
                id: "sophisticatedcore:xp_still"
            }]
    });
    // 禁用背包嵌套
    event.remove({
        id: "sophisticatedbackpacks:inception_upgrade"
    });
    // 禁用经验桶
    event.remove({
        output: "sophisticatedcore:xp_bucket"
    });
    event.remove({
        id: "create:empty_sophisticatedcore_xp_bucket_of_sophisticatedcore_xp_still"
    });
    event.remove({
        id: "immersiveengineering:jei_bucket_sophisticatedcore_xp_still"
    });
    event.remove({
        id: "create:fill_minecraft_bucket_with_sophisticatedcore_xp_still"
    });
    // 随机工具
    event.remove({
        id: "effortlessbuilding:randomizer_tool"
    });
    event.shaped(Item.of('effortlessbuilding:randomizer_tool',1), [
        'CCC',
        'AAA',
        'CCC'
    ],
    {
        A: 'quark:redstone_randomizer',
        C: 'pneumaticcraft:printed_circuit_board'
    })
    // 创造模式物理手杖
    event.shaped(Item.of('simulated:creative_physics_staff',1), [
        'IEAAA',
        'GEACA',
        'FEDAA',
        'HDEEE',
        'BHFGJ'
    ],
    {
        A: 'mekanism:module_gravitational_modulating_unit',
        B: 'mekanism:hdpe_stick',
        C: 'simulated:physics_assembler',
        D: 'immersiveengineering:graphite_electrode',
        E: 'ae2:quantum_entangled_singularity',
        F: 'immersiveengineering:logic_unit',
        G: 'mekanism:ultimate_induction_cell',
        H: 'mekanism:ultimate_induction_provider',
        I: 'create:wrench',
        J: 'mekanism:configurator'
    })
    // 创造流体储罐(空)
    event.shaped(Item.of('mekanism:creative_fluid_tank',1), [
        ' FCF ',
        ' FBF ',
        'CBABC',
        ' FBF ',
        'CDEDC'
    ],
    {
        A: 'mekanism:ultimate_fluid_tank',
        B: 'mekanism:pellet_antimatter',
        C: 'mekanism:dynamic_valve',
        D: 'mekanism:qio_drive_supermassive',
        E: 'mekanism:qio_drive_array',
        F: 'mekanism:block_steel',
    })
    // 创造化学品储罐(空)
    event.shaped(Item.of('mekanism:creative_chemical_tank',1), [
        ' FCF ',
        ' FEF ',
        'CDADC',
        ' FEF ',
        'CBBBC'
    ],
    {
        A: 'mekanism:ultimate_chemical_tank',
        B: 'mekanism:qio_drive_array',
        C: 'mekanism:qio_importer',
        D: 'mekanism:pellet_antimatter',
        E: 'mekanism:qio_drive_supermassive',
        F: 'mekanism:block_steel',
    })
    // 创造能量立方(空)
    event.shaped(Item.of('mekanism:creative_energy_cube',1), [
        'EGCGE',
        'GADAG',
        'CDBDC',
        'GADAG',
        'FGCGF'
    ],
    {
        A: 'mekanism:ultimate_energy_cube',
        B: 'mekanism:sps_port',
        C: 'mekanism:supercharged_coil',
        D: 'mekanism:ultimate_induction_cell',
        E: 'mekanism:block_osmium',
        F: 'mekanism:block_tin',
        G: 'mekanism:ultimate_induction_provider'
    }),
    // 创造范围升级
    event.shaped(Item.of('refinedstorage:creative_range_upgrade',1), [
        '  B  ',
        '  G  ',
        ' GAG ',
        ' FCF ',
        'DEEED'
    ],
    {
        A: 'refinedstorage:range_upgrade',
        B: 'mekanism:supercharged_coil',
        C: 'minecraft:end_crystal',
        D: 'oritech:machine_redstone_addon',
        E: 'mekanism:quantum_entangloporter',
        F: 'minecraft:ender_chest',
        G: 'immersiveengineering:coil_hv'
    })
    // 便携式QIO仪表板修改
    event.remove({id: "mekanism:portable_qio_dashboard"});
    event.shaped(Item.of('mekanism:portable_qio_dashboard', 1), [
        '  B  ',
        'KKKKK',
        'KJAJK',
        'HECFH',
        'IGDGI'
    ],
    {
        A: 'mekanism:qio_dashboard',
        B: 'mekanism:supercharged_coil',
        C: 'refinedstorage:creative_range_upgrade',
        D: 'mekanism:quantum_entangloporter',
        E: 'mekanism:qio_exporter',
        F: 'mekanism:qio_importer',
        G: 'mekanism:teleportation_core',
        H: 'mekanism:pellet_polonium',
        I: 'mekanism:pellet_plutonium',
        J: 'immersiveengineering:component_steel',
        K: 'immersiveengineering:plate_steel'
    })
    

})