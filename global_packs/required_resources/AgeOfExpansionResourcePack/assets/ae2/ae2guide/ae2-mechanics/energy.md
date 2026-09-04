---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: 能量
  icon: energy_cell
---

# 能量

你的网络需要能量才能运转。网络拥有一个能量池，[设备](../ae2-mechanics/devices.md)直接从中取电，
而<ItemLink id="vibration_chamber" />、<ItemLink id="energy_acceptor" />（以及<ItemLink id="controller" />）则向其中注入能量。
你可以用<ItemLink id="network_tool" />右键点击网络上的任意位置查看网络的能量统计，
或者右键点击网络的控制器（如果有的话）。这种全网统一的存储和分配方式意味着不存在能量传输速率限制，
设备可以抽取任意高的能量功率，能量接收器也能以实际上无限的速度输入能量，唯一的限制是你的储能容量。

## 能量接入

<Row>
  <BlockImage id="energy_acceptor" scale="4" />

  <GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/blocks/cable_energy_acceptor.snbt" />
  </GameScene>

  <BlockImage id="controller" p:state="online" scale="4" />

  <BlockImage id="vibration_chamber" p:active="true" scale="4" />
  
  <BlockImage id="crystal_resonance_generator" scale="4" />
</Row>

AE2内部不使用Forge Energy（Forge平台）或TechReborn Energy（Fabric平台），而是将它们转换为自己的单位：AE。这种转换是单向的。能量可以通过<ItemLink id="energy_acceptor" />和
<ItemLink id="controller" />转换进来，不过控制器的面最好留给更多[频道](../ae2-mechanics/channels.md)使用。
它也可以由<ItemLink id="vibration_chamber" />产生，或通过<ItemLink id="crystal_resonance_generator" />被动产生，但AE2的设计本意是配合拥有更优发电能力的其他科技模组一起使用。

这意味着在规划基地的能量分配基础设施时，最好把整个AE2网络当作一台大型多方块机器来看待。

Forge Energy和TechReborn Energy的转换比率为：

*   2 FE = 1 AE（Forge）
*   1 E  = 2 AE（Fabric）

## 能量存储

<Row>
  <BlockImage id="energy_cell" scale="4" p:fullness="4" />

  <BlockImage id="dense_energy_cell" scale="4" p:fullness="4" />

  <BlockImage id="creative_energy_cell" scale="4" />
</Row>

原因显而易见：网络在单个游戏刻内输入或消耗的能量不能超过其可存储量。如果一个网络只能存800 AE，
那么当它的[设备](../ae2-mechanics/devices.md)请求能量时，最多只能用到800 AE（假设储能是满的）；
能量接收器也最多只能向网络注入800 AE（假设储能是空的）。

这是奇怪行为的常见诱因：有人搭了一个只有能量接收器、驱动器、终端和一些设备的小网络，
然后试图把背包里满满一箱圆石一次性塞进网络。在单个游戏刻内插入这么多圆石所需的能量超过了网络的储能，
于是并非所有圆石都能被插入，网络耗尽电量并随之重启。

**这个问题可以通过加装能量元件解决。**

网络对每条线缆、每台机器或每个部件自带25 AE的内建缓冲。

<ItemLink id="controller" />拥有少量内部储能，为8,000 AE。

<ItemLink id="energy_cell" />可存储200k AE，通常一个就足以应付大多数场景，轻松应对日常网络使用的电力波动。

<ItemLink id="dense_energy_cell" />可存储1.6M AE，适用于想靠储备电力维持网络运转的情况，
或者应对大型[空间储存](spatial-io.md)装置瞬间产生的巨大能耗。

<ItemLink id="creative_energy_cell" />是用于测试的创造模式物品，能提供无限能量之类的效果。
