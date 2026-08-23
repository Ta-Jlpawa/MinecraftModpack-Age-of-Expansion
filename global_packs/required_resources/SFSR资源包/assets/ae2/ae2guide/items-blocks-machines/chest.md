---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: ME存储箱
  icon: chest
  position: 210
categories:
- devices
item_ids:
- ae2:chest
---

# ME存储箱

<GameScene zoom="8" background="transparent">
<ImportStructure src="../assets/blocks/chest.snbt" />
</GameScene>

ME存储箱就像一个集成了 <ItemLink id="terminal" />、<ItemLink id="drive" /> 和 <ItemLink id="energy_acceptor" /> 的微型网络。虽然它可以用作一个小型存储网络，但它只能容纳一个[存储元件](../items-blocks-machines/storage_cells.md)，因此这方面的用途相当有限。

它的价值更多体现在与装入其中的那块存储元件直接交互上。其内置终端只能看到和访问已插入元件中的物品，而普通网络上的[设备](../ae2-mechanics/devices.md)则可以访问任何[网络存储](../ae2-mechanics/import-export-storage.md)中的物品，包括ME存储箱。

它有 2 种不同的界面，并且对物品运输具有朝向性。与顶部的终端面交互会打开内置终端。物品可以从这一面插入已安装的存储元件，但不能取出。与其他任意面交互则会打开包含存储元件槽位和优先级设置的界面。存储元件只能通过有元件槽位的那一面用物品物流手段插入或取出。

可以用 <ItemLink id="certus_quartz_wrench" /> 旋转朝向。

它内置少量 AE 能量缓存，因此如果不在带有[能量元件](../items-blocks-machines/energy_cells.md)的网络上，一次插入或取出过多物品可能导致其因电压不足而停机。

终端可用 <ItemLink id="color_applicator" /> 涂色。

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/chest_color.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 设置

ME存储箱拥有与 <ItemLink id="terminal" /> 或 <ItemLink id="crafting_terminal" /> 完全相同的设置。
不过，它不支持 <ItemLink id="view_cell" />。

## 元件状态指示灯

存储箱中的元件带有一个显示状态的LED：

| 颜色 | 状态 |
| :----- | :------------------------------------------------------------------------------- |
| 绿色 | 空 |
| 蓝色 | 已有部分内容物 |
| 橙色 | [类型](../ae2-mechanics/bytes-and-types.md)已满，无法再存入新类型 |
| 红色 | [字节](../ae2-mechanics/bytes-and-types.md)已满，无法再存入更多物品 |
| 黑色 | 无电力，或驱动器没有分配到[频道](../ae2-mechanics/channels.md) |

## 优先级

在元件槽位界面的右上角点击扳手图标即可设置优先级。
进入网络的物品会以最高优先级的存储作为第一目的地。
当两个存储设备或元件优先级相同时，若其中一个已含有该物品，则会优先选择该存储。
在与其他存储相同的优先级组中，任何经过[分区](cell_workbench.md)的元件都会被视为"已含有该物品"。
从网络中移除物品时，会从优先级最低的存储中移除。这一优先级系统意味着，随着物品不断存入和取出，
高优先级存储会被逐渐填满，而低优先级存储则会被逐渐清空。

## 配方

<RecipeFor id="chest" />
