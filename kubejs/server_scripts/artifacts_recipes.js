// 禁用三件饰品的全部输入、输出配方，包括永恒牛排的熔炉、烟熏炉和营火配方。
ServerEvents.recipes(event => {
    const removed = [
        'artifacts:everlasting_beef',
        'artifacts:eternal_steak',
        'artifacts:scarf_of_invisibility'
    ];
    event.remove({ input: removed });
    event.remove({ output: removed });
});
