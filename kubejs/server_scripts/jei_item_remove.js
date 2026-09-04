ServerEvents.recipes(event => {
	// 禁用背包嵌套
    event.remove({id: "sophisticatedbackpacks:inception_upgrade"});
    // 禁用经验桶
    event.remove({output: "sophisticatedcore:xp_bucket"});
    event.remove({id: "create:empty_sophisticatedcore_xp_bucket_of_sophisticatedcore_xp_still"});
    event.remove({id: "immersiveengineering:jei_bucket_sophisticatedcore_xp_still"});
    event.remove({id: "create:fill_minecraft_bucket_with_sophisticatedcore_xp_still"});
	// 禁用安全工艺强化方块
	event.remove({output: "securitycraft:universal_block_reinforcer_lvl1"});
	event.remove({output: "securitycraft:universal_block_reinforcer_lvl2"});
	event.remove({output: "securitycraft:universal_block_reinforcer_lvl3"});
	event.remove({output: "securitycraft:universal_block_remover"});

})

RecipeViewerEvents.removeEntriesCompletely('item', event => {
	event.remove("sophisticatedbackpacks:inception_upgrade")
	event.remove("sophisticatedcore:xp_bucket")
	event.remove("securitycraft:universal_block_reinforcer_lvl1");
	event.remove("securitycraft:universal_block_reinforcer_lvl2");
	event.remove("securitycraft:universal_block_reinforcer_lvl3");
	event.remove("securitycraft:universal_block_remover");
})

RecipeViewerEvents.removeEntries('item', event => {
	event.remove("sophisticatedbackpacks:inception_upgrade")
	event.remove("sophisticatedcore:xp_bucket")
	event.remove("securitycraft:universal_block_reinforcer_lvl1");
	event.remove("securitycraft:universal_block_reinforcer_lvl2");
	event.remove("securitycraft:universal_block_reinforcer_lvl3");
	event.remove("securitycraft:universal_block_remover");
})

RecipeViewerEvents.removeRecipes(event => {
	event.remove("create:empty_sophisticatedcore_xp_bucket_of_sophisticatedcore_xp_still")
	event.remove("immersiveengineering:jei_bucket_sophisticatedcore_xp_still")
	event.remove("create:fill_minecraft_bucket_with_sophisticatedcore_xp_still")
	event.remove("securitycraft:universal_block_reinforcer_lvl1");
	event.remove("securitycraft:universal_block_reinforcer_lvl2");
	event.remove("securitycraft:universal_block_reinforcer_lvl3");
	event.remove("securitycraft:universal_block_remover");
})