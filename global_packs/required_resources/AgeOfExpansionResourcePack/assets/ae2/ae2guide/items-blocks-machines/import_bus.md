---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: ME输入总线
  icon: import_bus
  position: 220
categories:
- devices
item_ids:
- ae2:import_bus
---

# 输入总线

<GameScene zoom="8" background="transparent">
<ImportStructure src="../assets/blocks/import_bus.snbt" />
</GameScene>

输入总线会从其接触的容器中抽取物品和流体（安装附属模组后还可以抽取其他内容），并将其推入
[网络存储](../ae2-mechanics/import-export-storage.md)。

为了减少卡顿，如果输入总线最近没有导入过东西，它会进入某种
"休眠模式"，以较低速度运行；当它成功导入物品时会唤醒并加速至全速（每秒4次操作）。

它们是[线缆子部件](../ae2-mechanics/cable-subparts.md)。

## 过滤

默认情况下，总线会导入它能接触到的任何东西。放入过滤槽的物品将作为白名单，仅
允许导入这些特定物品。

即使你实际上没有某种物品，也可以从JEI/REI将物品和流体拖入槽位。

右键点击时手持流体容器（如桶或流体储罐），可将该流体设为过滤器，而不是桶或储罐物品本身。

## 升级

输入总线支持以下[升级卡](upgrade_cards.md)：

*   <ItemLink id="capacity_card" /> 增加过滤槽的数量
*   <ItemLink id="speed_card" /> 增加每次操作搬运的物品数量
*   <ItemLink id="fuzzy_card" /> 让总线按损坏程度过滤和/或忽略物品NBT
*   <ItemLink id="inverter_card" /> 将过滤器从白名单切换为黑名单
*   <ItemLink id="redstone_card" /> 添加红石控制，可设为高信号时激活、低信号时激活，或每个脉冲激活一次

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
