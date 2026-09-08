---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 控制器
  icon: controller
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:controller
---

# 控制器

<BlockImage id="controller" p:state="online" scale="8" />

控制器是[ME网络](../ae2-mechanics/me-network-connections.md)的路由枢纽。没有它，网络就处于"非持久"（ad-hoc）状态，最多只能支持总共 8 个占用频道的[设备](../ae2-mechanics/devices.md)。

同一个[ME网络](../ae2-mechanics/me-network-connections.md)中不能存在 2 个控制器。

控制器的每个面可提供 32 条[频道](../ae2-mechanics/channels.md)。

每个控制器方块运行需要 6 AE/t。每个控制器方块可存储 8000 AE，因此更大的网络可能需要额外的能量存储。详见[能量](../ae2-mechanics/energy.md)。

多方块控制器可以用相当自由的形式搭建。

<GameScene zoom="2" background="transparent">
  <ImportStructure src="../assets/assemblies/controllers.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

不过，必须遵守以下几条规则：

1.  同一[ME网络](../ae2-mechanics/me-network-connections.md)上的所有控制器方块必须相互连接，否则方块会变红。
2.  控制器的尺寸必须在 7x7x7 以内，否则会变红。
3.  一个控制器方块在至多 1 个轴向上可以有 2 个相邻方块；违反此规则的方块会失效并变红。

<GameScene zoom="2" background="transparent">
  <ImportStructure src="../assets/assemblies/controller_rules.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

只要遵守所有规则并正常供电，控制器就会发光并循环变换颜色。

右键点击控制器可以打开与 <ItemLink id="network_tool" /> 相同的界面。

## 配方

<RecipeFor id="controller" />
