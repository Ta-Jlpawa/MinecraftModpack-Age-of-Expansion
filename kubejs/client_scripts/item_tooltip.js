ItemEvents.modifyTooltips(event => {

    event.add('kubejs:ta_jlpawa', { shift: false }, [
        Text.darkGray('按住 Shift 查看???信息')
    ])
    event.add('kubejs:ta_jlpawa', { shift: true }, [
        Text.gold('恭喜你发现了一个彩蛋物品！没想到这里还能藏彩蛋吧哈哈')
    ])

});