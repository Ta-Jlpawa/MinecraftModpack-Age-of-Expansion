---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: ME输出总线
  icon: export_bus
  position: 220
categories:
- devices
item_ids:
- ae2:export_bus
---

# 输出总线

<GameScene zoom="8" background="transparent">
<ImportStructure src="../assets/blocks/export_bus.snbt" />
</GameScene>

输出总线会从[网络存储](../ae2-mechanics/import-export-storage.md)中拉取物品和流体（若装有附加模组，还包括其他内容），并推入其贴合的容器中。

为了降低卡顿，如果输出总线最近没有成功导出过物品，它会进入一种"休眠模式"，以较低速度运行；当它成功导出某物时会唤醒并加速到全速（每秒 4 次操作）。

它们是[线缆子部件](../ae2-mechanics/cable-subparts.md)。

## 过滤

默认情况下，总线不会导出任何东西。放入过滤槽位的物品将作为白名单，允许导出这些特定物品。

即使你手上没有某个物品，也可以直接从 JEI/REI 将物品或流体拖入槽位。

右键点击流体容器（如桶或流体储罐）即可将该流体设为过滤器，而不是放入桶或储罐物品本身。

## 升级

该总线支持以下[升级卡](upgrade_cards.md)：

*   <ItemLink id="capacity_card" /> 增加过滤槽位数量，并提供一个设置，用于选择按何种顺序导出被过滤的内容。
*   <ItemLink id="speed_card" /> 增加每次操作搬运的物品数量
*   <ItemLink id="fuzzy_card" /> 让总线可以按损耗程度过滤，并且/或者忽略物品NBT
*   <ItemLink id="crafting_card" /> 让总线向你的[自动合成](../ae2-mechanics/autocrafting.md)系统发送合成请求以获取所需物品。可设置为在可能时直接从存储中提取，或总是发起新的合成请求。
*   <ItemLink id="redstone_card" /> 添加红石控制，可选择高信号时激活、低信号时激活，或每个脉冲激活一次

## 速度

| 加速卡数量 | 每次操作搬运的物品数 |
|:-------------------|:--------------------------|
| 0                  | 1                         |
| 1                  | 8                         |
| 2                  | 32                        |
| 3                  | 64                        |
| 4                  | 96                        |

## 配方

<RecipeFor id="import_bus" />
