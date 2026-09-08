---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 物质冷凝器
  icon: condenser
  position: 310
categories:
- machines
item_ids:
- ae2:condenser
---

# 物质冷凝器

<BlockImage id="condenser" scale="8" />

物质冷凝器既可以用作垃圾桶，也可以用来制造 <ItemLink id="matter_ball" /> 和[奇点](singularities.md)。它可以接受存储元件所能存储的任何物品、流体等。

## 设置/配方

*   在垃圾桶模式下，物质冷凝器只会销毁进入其中的一切东西
*   在物质球模式下，冷凝器会将投入其中的物品制成 <ItemLink id="matter_ball" />。
    此模式需要在冷凝器顶部的槽位放入一个存储组件。每个物质球需要 256 个物品或桶，因此一个
    <ItemLink id="cell_component_1k" />（提供 8192 位容量）绰绰有余。
*   在物质奇点模式下，冷凝器会将投入其中的物品制成[奇点](singularities.md)。
    此模式同样需要在冷凝器顶部的槽位放入一个存储组件。每个奇点需要 256,000 个物品或桶，因此一个
    <ItemLink id="cell_component_64k" />（提供 524,288 位容量）绰绰有余。

注意，在后两种会产生资源的模式中，物质冷凝器*可能*发生堵塞：当能量缓存和输出物品缓存都被完全填满时，它将不再接受任何输入。

## 配方

<RecipeFor id="condenser" />
