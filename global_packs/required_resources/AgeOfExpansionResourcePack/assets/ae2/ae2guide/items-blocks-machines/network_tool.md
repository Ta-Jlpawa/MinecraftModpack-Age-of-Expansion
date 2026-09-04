---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 网络工具
  icon: network_tool
  position: 410
categories:
- tools
item_ids:
- ae2:network_tool
---

# 网络工具

<ItemImage id="network_tool" scale="4" />

网络工具是一种改进版[扳手](wrench.md)，还能显示网络诊断信息并存储[升级卡](upgrade_cards.md)。
它保留了扳手快速拆解物品和将[子部件](../ae2-mechanics/cable-subparts.md)
从线缆上取下的能力，但不能旋转物品。

它有9个用于存放[升级卡](upgrade_cards.md)的槽位，只要该工具
在你物品栏的任意位置，这些升级卡就能在任何AE2设备的UI中生效。

右键点击网络的任何部分会显示一个诊断信息窗口，类似于右键点击<ItemLink id="controller" />。
此窗口显示：

*   网络中已使用的频道数量
*   一个全局设置开关：以AE还是E/FE查看能量
*   网络中存储的[能量](../ae2-mechanics/energy.md)数量，以及网络的最大能量容量
*   进入网络和被网络消耗的能量数量
*   网络上所有[设备](../ae2-mechanics/devices.md)和组件的列表

在摆弄[子网络](../ae2-mechanics/subnetworks.md)时，
这个窗口也有助于判断两条不同的线缆或两台设备是否属于同一网络。

## 隐藏伪装板

手持网络工具（无论哪只手）时，<a href="facades.md">伪装板</a>会被隐藏。

你可以与隐藏的伪装板后面的方块交互，而无需先拆除伪装板。

## 配方

<RecipeFor id="network_tool" />
