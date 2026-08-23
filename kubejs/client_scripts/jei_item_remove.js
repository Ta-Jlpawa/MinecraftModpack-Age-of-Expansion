
RecipeViewerEvents.removeEntriesCompletely('item', event => {
	event.remove("sophisticatedbackpacks:inception_upgrade")
	event.remove("sophisticatedcore:xp_bucket")
})

RecipeViewerEvents.removeEntries('item', event => {
	event.remove("sophisticatedbackpacks:inception_upgrade")
	event.remove("sophisticatedcore:xp_bucket")
})

RecipeViewerEvents.removeRecipes(event => {
	event.remove("create:empty_sophisticatedcore_xp_bucket_of_sophisticatedcore_xp_still")
	event.remove("immersiveengineering:jei_bucket_sophisticatedcore_xp_still")
	event.remove("create:fill_minecraft_bucket_with_sophisticatedcore_xp_still")
})