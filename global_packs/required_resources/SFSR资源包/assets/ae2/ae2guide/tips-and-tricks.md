---
navigation:
  title: 技巧与窍门
  position: 20
---

# 技巧与窍门

一堆实用的小建议

* 卸载 Optifine
* 带有缩放和标注显示/隐藏按钮的指南场景可以旋转和缩放视角
* 保持你的网络呈树状结构，避免成环
* 整组[设备](ae2-mechanics/devices.md)（整方块形态）数量控制在8个以内，除非你深入理解了[频道](ae2-mechanics/channels.md)
  在网络中的路由方式
* 选定一种木头并坚持在所有[样板](items-blocks-machines/patterns.md)中使用它。没错，开启样板的替代有时确实可行，
  但统一木种能极大减少麻烦。
* 在 <ItemLink id="pattern_access_terminal" /> 中将[样板](items-blocks-machines/patterns.md)纵向排列，
  或将样板分散到多个[供应器](items-blocks-machines/pattern_provider.md)中，让配方得以并行执行。
* 添加一个[能量元件](items-blocks-machines/energy_cells.md)，让你的网络能够应对功率尖峰。
* <ItemLink id="condenser" />（物质冷凝器）里可以倒水
* 保持网络清爽的最好办法，是不要把怪物随机掉落的剑和盔甲之类塞进去。每一种独特的附魔与耐久组合都会占用一个
  [类型](ae2-mechanics/bytes-and-types.md)。
* [处理样板](items-blocks-machines/patterns.md)的产物返回时必须发生一次"物品进入系统"事件，
  例如通过 <ItemLink id="import_bus" />、<ItemLink id="interface" /> 或 <ItemLink id="pattern_provider" /> 的退回槽，
  而不能直接用带 <ItemLink id="storage_bus" /> 的箱子接收产物管道。
* 别忘了，带有缩放和标注显示/隐藏按钮的指南场景可以旋转和缩放视角
* <ItemLink id="pattern_provider" /> 只会推出完整的配方批次，而且只从单一面输出。这有助于避免机器收到不完整的批次，
  但有时你会希望原料分发到多处。这时可以用 <ItemLink id="interface" /> 来实现——既可以把接口当作
  ["管道"子网络](example-setups/pipe-subnet.md)使用，也可以利用它能同时容纳多种物品堆、流体、化学品等的特性，
  把它当成一个中转型箱子/储罐来用。
* 带有缩放和标注显示/隐藏按钮的指南场景可以缩放和旋转视角
