---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 空间锚
  icon: spatial_anchor
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:spatial_anchor
---

# 空间锚

<BlockImage id="spatial_anchor" p:powered="true" scale="8"/>

AE2 网络需要保持区块加载，其上的任何[设备](../ae2-mechanics/devices.md)才能正常运作；如果只有部分被加载，网络可能无法正确运行。空间锚解决了这个问题。它会强制加载其网络所占用的区块。
哪怕只是一根跨越区块边界的线缆，也足以加载那个新区块。

它会通过[量子桥](quantum_bridge.md)传播其"加载"效果，但无法跨维度生效。因此如果你有一条通往下界的量子桥，就需要在主基地的网络和下界的网络上各放置一个空间锚。

默认情况下，它还会在其加载的区块中启用随机刻，可在 ae2 配置文件中关闭此功能。

如果有某种原因需要旋转它，可以用 <ItemLink id="certus_quartz_wrench" /> 来旋转。

## 设置

*   空间锚提供查看 AE 或 E/FE 能量的全局设置入口。
*   可以显示游戏内的全息图，展示正在加载的区块。

## 能量

空间锚会按照以下公式消耗[能量](../ae2-mechanics/energy.md)：

e = 80 + (x\*(x+1))/2

其中 x 是正在加载的区块数量

## 配方

<RecipeFor id="spatial_anchor" />
