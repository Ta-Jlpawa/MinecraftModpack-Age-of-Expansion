---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: ME驱动器
  icon: drive
  position: 210
categories:
- devices
item_ids:
- ae2:drive
---

# ME驱动器

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../assets/blocks/drive.snbt" />
</GameScene>

驱动器是用于插入[存储元件](storage_cells.md)的[设备](../ae2-mechanics/devices.md)，插入后存储元件即可为[网络存储](../ae2-mechanics/import-export-storage.md)所用。它有 10 个槽位，每个槽位可放入一个元件。

如果有需要，你也可以用漏斗或 AE2 总线之类的物品物流手段从其容器中取出或放入元件。

可以用 <ItemLink id="certus_quartz_wrench" /> 旋转朝向。

## 元件状态指示灯

驱动器中的元件带有一个显示状态的LED：

| 颜色 | 状态 |
| :----- | :------------------------------------------------------------------------------- |
| 绿色 | 空 |
| 蓝色 | 已有部分内容物 |
| 橙色 | [类型](../ae2-mechanics/bytes-and-types.md)已满，无法再存入新类型 |
| 红色 | [字节](../ae2-mechanics/bytes-and-types.md)已满，无法再存入更多物品 |
| 黑色 | 无电力，或驱动器没有分配到[频道](../ae2-mechanics/channels.md) |

## 优先级

点击界面右上角的扳手图标即可设置优先级。
进入网络的物品会以最高优先级的存储作为第一目的地。
当两个存储设备或元件优先级相同时，若其中一个已含有该物品，则会优先选择该存储。
在与其他存储相同的优先级组中，任何经过[分区](cell_workbench.md)的元件都会被视为"已含有该物品"。
从网络中移除物品时，会从优先级最低的存储中移除。这一优先级系统意味着，随着物品不断存入和取出，
高优先级存储会被逐渐填满，而低优先级存储则会被逐渐清空。

## 配方

<RecipeFor id="drive" />
