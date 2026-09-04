// ==================== Oritech 冗余矿石与金属材料 JEI 隐藏 ====================
// 与 server_scripts/oritech_ore_removal.js、oritech_material_removal.js 配套
// 末地的 oritech:endstone_platinum_ore 保留显示

RecipeViewerEvents.removeEntries('item', event => {
	event.remove("oritech:nickel_ore")
	event.remove("oritech:deepslate_nickel_ore")
	event.remove("oritech:deepslate_platinum_ore")
	event.remove("oritech:deepslate_uranium_ore")

	// 冗余金属材料（与其他模组重合，详见 .docs/Oritech冗余材料清理说明.md）
	const REMOVED_MATERIALS = [
		"oritech:raw_nickel", "oritech:nickel_clump", "oritech:small_nickel_clump",
		"oritech:nickel_dust", "oritech:small_nickel_dust", "oritech:nickel_gem",
		"oritech:nickel_ingot", "oritech:nickel_nugget",
		"oritech:raw_platinum", "oritech:platinum_clump", "oritech:small_platinum_clump",
		"oritech:platinum_dust", "oritech:small_platinum_dust", "oritech:platinum_gem",
		"oritech:platinum_ingot", "oritech:platinum_nugget",
		"oritech:iron_clump", "oritech:small_iron_clump",
		"oritech:iron_dust", "oritech:small_iron_dust", "oritech:iron_gem",
		"oritech:copper_clump", "oritech:small_copper_clump",
		"oritech:copper_dust", "oritech:small_copper_dust", "oritech:copper_gem", "oritech:copper_nugget",
		"oritech:gold_clump", "oritech:small_gold_clump",
		"oritech:gold_dust", "oritech:small_gold_dust", "oritech:gold_gem",
		"oritech:raw_uranium", "oritech:uranium_dust", "oritech:small_uranium_dust", "oritech:uranium_gem",
		"oritech:steel_ingot", "oritech:steel_dust",
		"oritech:electrum_ingot", "oritech:electrum_dust"
	]
	for (let item of REMOVED_MATERIALS) {
		event.remove(item)
	}
})
