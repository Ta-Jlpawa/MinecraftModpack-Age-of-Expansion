ServerEvents.recipes(event => {
    // 移除 Mekanism 下界合金碎片与粉尘的旧加工链，以及重复的远古残骸加工配方。
    const oldRecipes = [
        'mekanism:processing/netherite/ancient_debris_to_dirty_scrap',
        'mekanism:processing/netherite/dirty_scrap_to_scrap',
        'mekanism:processing/netherite/dust_to_ancient_debris',
        'mekanism:processing/netherite/ingot_from_dust_blasting',
        'mekanism:processing/netherite/ingot_from_dust_smelting',
        'mekanism:processing/netherite/ingot_to_dust',
        'mekanism:processing/netherite/scrap_to_dust',
        'mekanism:processing/netherite/ancient_debris_to_scrap',
        'oritech:grinder/compat/mekanism/dust/netherite',
        'oritech:pulverizer/compat/mekanism/dust/netherite',
        'oritech:grinder/netheritescrap',
        'immersiveengineering:arcfurnace/netherite_scrap',
        'minecraft:netherite_scrap',
        'minecraft:netherite_scrap_from_blasting'
    ];
    for (const id of oldRecipes) event.remove({ id: id });

});
