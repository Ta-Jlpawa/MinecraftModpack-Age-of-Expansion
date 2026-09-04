---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: P2P隧道
  icon: me_p2p_tunnel
  position: 210
categories:
- devices
item_ids:
- ae2:me_p2p_tunnel
- ae2:redstone_p2p_tunnel
- ae2:item_p2p_tunnel
- ae2:fluid_p2p_tunnel
- ae2:fe_p2p_tunnel
- ae2:light_p2p_tunnel
---

# 点对点隧道

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_tunnels.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

P2P隧道是一种在网络中传输物品、流体、红石信号、能量、光线和[频道](../ae2-mechanics/channels.md)等内容的途径，
且它们不会直接与网络交互。P2P隧道有很多变体，但每种
只传输其特定类型的内容。它们本质上就像在远距离直接连接
两个方块面的传送门。它们不是双向的，有明确的输入端和输出端。

![Portal](../assets/assemblies/p2p_portal.png)

例如，朝向物品P2P的漏斗会表现得像与木桶直接相连一样，物品可以流通。

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_hopper_barrel.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

然而，两个相邻的木桶之间不会互相传输物品。

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_barrel_barrel.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

还有其他变体，例如红石P2P。

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_redstone.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

以及传输频道的ME P2P。

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_channels.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## P2P隧道的类型与调谐

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_tunnels.snbt" />
  <IsometricCamera yaw="180" pitch="90" />
</GameScene>

P2P隧道有多种类型。只有ME P2P隧道可以直接合成，其他类型需要用特定物品右键点击
其他P2P隧道来转换：
- 手持任意[线缆](../items-blocks-machines/cables.md)右键点击可选择ME P2P隧道。
- 手持各种红石组件右键点击可选择红石P2P隧道。
- 手持箱子或漏斗右键点击可选择物品P2P隧道。
- 手持桶或瓶子右键点击可选择流体P2P隧道。
- 手持几乎任何含能量的物品右键点击可选择能量P2P隧道。
- 手持火把或荧石右键点击可选择光P2P隧道。

某些隧道类型有一些特性。例如，ME P2P隧道的频道无法穿过其他ME P2P隧道，而
能量P2P隧道会通过增加自身[能量](../ae2-mechanics/energy.md)消耗的方式，间接对流经自身的FE抽取2.5%的"税"。

## P2P最常用的形式

P2P隧道最常见的用途是使用ME P2P隧道来压缩[频道](../ae2-mechanics/channels.md)传输的密度。
无需成捆的致密线缆，一根致密线缆就能承载大量频道。

在这个例子中，8个ME P2P输入端从主网络的<ItemLink id="controller" />获取256个频道（8*32），8个ME P2P输出端
将它们输出到别处。注意每个P2P隧道输入端或输出端占用1个频道。因此我们可以用一根细线缆
承载许多频道。而且由于我们的P2P隧道位于专用的[子网络](../ae2-mechanics/subnetworks.md)上，
这样做甚至不会占用主网络的任何频道！还要注意，虽然P2P隧道可以直接贴着控制器放置，
但也可以在中间放置一根[致密智能线缆](../items-blocks-machines/cables.md#smart-cable)，以便更直观地查看频道。

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/p2p_compact_channels.snbt" />

  <BoxAnnotation color="#dddddd" min="1.3 1.3 6.3" max="2 2.7 6.7">
        石英纤维在主网络和P2P子网络之间共享能量。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4.1 0 5.7" max="5 2.3 6.4">
        你可以把隧道输入端直接放在控制器上，也可以用线缆接入。
  </BoxAnnotation>

  <IsometricCamera yaw="225" pitch="30" />
</GameScene>

另一个例子（包括它与[量子桥](quantum_bridge.md)的配合使用）请看这张我懒得修饰的MS Paint示意图：

![P2P and quantum bridges](../assets/diagrams/p2p_quantum_network.png)

## 嵌套

不过，你不能借此通过单根线缆发送无限频道。ME P2P隧道的频道不会
穿过另一个ME P2P隧道，因此你无法递归嵌套它们。观察红色线缆上的外层ME P2P隧道
是如何离线的。注意这只适用于ME P2P隧道，其他类型的P2P隧道可以穿过ME P2P隧道，
比如图中正常工作的红石P2P隧道。

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_nesting.snbt" />
  <IsometricCamera yaw="225" pitch="30" />
</GameScene>

## 连接

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_linking_frequency.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

可以使用<ItemLink id="memory_card" />连接P2P隧道的两端。频率会以
2x2的颜色阵列显示在隧道背面。
- Shift+右键点击可生成新的P2P连接频率。
- 右键点击可粘贴设置、升级卡或连接频率。

你Shift+右键点击的隧道将成为输入端，而你右键点击的隧道将成为输出端。可以有多个输出端，
但对于ME P2P隧道，流入输入端的频道会在各输出端之间分配，所以你不能复制频道。

## 配方

<RecipeFor id="me_p2p_tunnel" />
