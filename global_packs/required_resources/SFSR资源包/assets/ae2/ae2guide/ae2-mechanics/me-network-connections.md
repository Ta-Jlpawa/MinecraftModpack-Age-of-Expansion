---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: 网络连接
  icon: fluix_glass_cable
---

# 网络连接

## "网络"是什么意思？

"网络"是由能够传递[频道](../ae2-mechanics/channels.md)的方块（例如[线缆](../items-blocks-machines/cables.md)或整格机器）和[设备](../ae2-mechanics/devices.md)链接而成的[设备](../ae2-mechanics/devices.md)群。
（<ItemLink id="charger" />、<ItemLink id="interface" />、<ItemLink id="drive" />等。）
严格来说，一根单独的线缆也算一个网络。

## 关于设备位置的题外话

对于具有特定网络功能的[设备](../ae2-mechanics/devices.md)（比如向/从[网络存储](../ae2-mechanics/import-export-storage.md)推送或拉取的<ItemLink id="interface" />、
读取网络存储内容的<ItemLink id="level_emitter" />、充当网络存储的<ItemLink id="drive" />等等）
而言，设备的物理位置无关紧要。

再说一遍，**设备的物理位置无关紧要**。唯一要紧的是设备连接到了网络
（以及当然，它连接的是哪个网络）。

## 网络连接

判断网络中哪些东西相连的一个简单方法是使用<ItemLink id="network_tool" />。它会显示网络上的每个
组件，所以如果你看到了不该看到的东西、或没看到该看到的东西，那就出问题了。

例如，这是2个相互独立的网络。

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/2_networks_1.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="1 2 2">
        网络 1
  </BoxAnnotation>

<BoxAnnotation color="#5CA7CD" min="2 0 0" max="3 2 2">
        网络 2
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

这也是2个相互独立的网络，因为<ItemLink id="quartz_fiber" />只共享[能量](../ae2-mechanics/energy.md)
而不提供网络连接。

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/2_networks_2.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="1 2 2">
        网络 1
  </BoxAnnotation>

  <BoxAnnotation color="#5CA7CD" min="1.3 0 0" max="3 2 2">
        网络 2
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

然而，这只是1个网络，而不是2个独立网络。[量子桥](../items-blocks-machines/quantum_bridge.md)表现得像一条无线的
[致密线缆](../items-blocks-machines/cables.md#dense-cable)，因此两端处于同一网络。

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/actually_1_network.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="7 3 3">
        全都是1个网络
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

这也只是1个网络，因为[线缆](../items-blocks-machines/cables.md)颜色与网络连接毫无关系，只是不同颜色的线缆之间
不会互相连接。所有颜色都会连接福鲁伊克斯（即"无色"）线缆。

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/actually_1_network_2.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="4 2 2">
        全都是1个网络
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 子网络语境下的连接

[子网络](../ae2-mechanics/subnetworks.md)利用网络连接（更准确地说，是**不**被连接这一点）
来限制哪些[设备](../ae2-mechanics/devices.md)能访问哪些其他设备。

说到底，子网络其实就是一张独立的网络。

以[矿石时运自动化装置](../example-setups/ore-fortuner.md)为例。这里有3张独立的网络，
每张都在装置中承担特定职责。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/ore_fortuner.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 2" max="3 1 3">
        网络 1，充当管道式子网络，限制输入总线的访问范围，使它通过
        生成面板来"存储"矿石块。
  </BoxAnnotation>

  <BoxAnnotation color="#5CA7CD" min="0 0 0" max="3 1 1">
        网络 2，同样充当管道式子网络，限制湮灭面板的访问范围，使它们把经过时运处理的矿石碎块
        存进木桶而非你的主网络。同时也意味着它们不会占用主网络的任何频道。
  </BoxAnnotation>

  <BoxAnnotation color="#82CD5C" min="2 0 1" max="4 1 2">
        网络 3，承载你全部存储和合成的主网络。它在这里真的只是供电而已，并且特意*没有*
        连接到那2个子网络。
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## P2P语境下的连接

有一种[P2P隧道](../items-blocks-machines/p2p_tunnels.md)传输的是[频道](channels.md)而不是物品、流体或红石信号，
不知为何这一点常把人搞糊涂。隧道所安装的网络与隧道所承载的网络毫无关系。
它们*可以*是同一个网络，但不必是，而且通常不是。

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/p2p_channels_network_connection.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="1.98 2 1">
        网络 1，被承载的网络（通常是你的主网络）
  </BoxAnnotation>

  <BoxAnnotation color="#5CA7CD" min="2.02 0 0" max="3.98 1 1">
        网络 2，运行ME P2P隧道的网络（通常*不是*你的主网络）
  </BoxAnnotation>

  <BoxAnnotation color="#915dcd" min="4.02 0 0" max="6 1 1">
        网络 1，被承载的网络（通常是你的主网络）
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 不太直观的连接

在这个案例里，这只是1个网络，因为<ItemLink id="pattern_provider" />作为整格设备表现得像一条线缆，
<ItemLink id="inscriber" />也是如此。于是网络连接穿过了供应器和压印器。

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/pattern_provider_network_connection_1.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="4 2 2">
        全都是1个网络
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

要避免这一点（对许多涉及[子网络](../ae2-mechanics/subnetworks.md)的自动合成布局很有用），
可以用<ItemLink id="certus_quartz_wrench" />右键供应器使其变为定向型，这样它就不会
从某一面传递频道。

<Row gap="40">
<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/pattern_provider_network_connection_2.snbt" />

  <BoxAnnotation color="#915dcd" min="0 0 0" max="1.98 2 2">
        网络 1
  </BoxAnnotation>

  <BoxAnnotation color="#5CA7CD" min="2.02 0 0" max="4 2 2">
        网络 2
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/pattern_provider_directional_connection.snbt" />

  <BoxAnnotation color="#ee3333" min="1 .3 .3" max="1.3 .7 .7">
        注意线缆并没有连上来
  </BoxAnnotation>

  <IsometricCamera yaw="255" pitch="30" />
</GameScene>
</Row>

其他不提供定向网络连接的部件是大多数[子部件](../ae2-mechanics/cable-subparts.md)
[设备](../ae2-mechanics/devices.md)，比如<ItemLink id="import_bus" />、<ItemLink id="storage_bus" />和
<ItemLink id="cable_interface" />。

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/subpart_no_connection.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>
