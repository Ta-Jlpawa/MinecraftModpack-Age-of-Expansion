PlayerEvents.chat(event => {
  if (event.message.startsWith('查询整合包作者')) {
    event.server.scheduleInTicks(1, event.server, ctx => {
      ctx.data.tell(Text.green('[AUTO MESSAGE] 整合包的作者为:TA_JLPawa！'))
    })
  }
  if (event.message.startsWith('彩蛋')) {
    event.server.scheduleInTicks(1, event.server, ctx => {
      ctx.data.tell(event.player.name, Text.green('你找到了一个聊天框里的彩蛋！'))
    })
  }

})