// 从 JEI 隐藏已禁用的 Artifacts 饰品。
RecipeViewerEvents.removeEntriesCompletely('item', event => {
    event.remove('artifacts:everlasting_beef');
    event.remove('artifacts:eternal_steak');
    event.remove('artifacts:scarf_of_invisibility');
});
