---
navigation:
  parent: example-setups/example-setups-index.md
  title: 存储元件倒空器或填充器
  icon: io_port
---

# 存储元件倒空器或填充器

有人可能会问：“如何快速把一个存储元件里的东西倒进箱子、抽屉组或背包？或者反过来，从这些东西里填满一个存储元件？”

答案是使用一个<ItemLink id="io_port" />，再辅以一些子网络划分，来限制它可以存入物品或取出物品的位置。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/cell_dumper_filler.snbt" />

<BoxAnnotation color="#dddddd" min="1 1 0" max="2 2 1">
        (1) IO端口：可以使用界面中间的箭头按钮将其设置为“将数据传输到网络”或
        “将数据传输到存储元件”。装有 3 张加速卡。
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 0.7 0" max="1 1 1">
        (2) 存储总线：保持默认配置。
  </BoxAnnotation>

<BoxAnnotation color="#33dd33" min="0 1 0" max="1 2 1">
        在这里放置任何你想要填充或倒空的容器。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2 0.35 0.35" max="2.3 0.65 0.65">
        石英纤维：仅当能量来源是另一个网络时才需要。
  </BoxAnnotation>

<DiamondAnnotation pos="3 0.5 0.5" color="#00ff00">
        连接至某种能量来源，例如另一个网络或一个能量接收器。
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 配置

* <ItemLink id="io_port" /> (1) 可以使用界面中间的箭头按钮设置为“将数据传输到网络”或“将数据传输到存储元件”。
  它装有 3 张加速卡以达到最高速度。
* <ItemLink id="storage_bus" /> (2) 保持默认配置。

## 工作原理

### “传输到网络”模式下

1. <ItemLink id="io_port" /> 尝试将插入的[存储元件](../items-blocks-machines/storage_cells.md)的内容
    倒进[网络存储](../ae2-mechanics/import-export-storage.md)。
2. 该子网络上唯一的存储是<ItemLink id="storage_bus" />，它会把物品、流体等存入你放在它面前的任意容器中。
* <ItemLink id="energy_cell" /> 提供了足够大的[能量](../ae2-mechanics/energy.md)缓冲，
    使网络不会因为每游戏刻传输大量物品的电力消耗而耗尽能量。

### “传输到存储元件”模式下

1. <ItemLink id="io_port" /> 尝试把[网络的存储](../ae2-mechanics/import-export-storage.md)内容
   倒进插入的[存储元件](../items-blocks-machines/storage_cells.md)中。
2. 该子网络上唯一的存储是<ItemLink id="storage_bus" />，它会把物品、流体等从你放在它面前的任意容器中抽出。
* <ItemLink id="energy_cell" /> 提供了足够大的[能量](../ae2-mechanics/energy.md)缓冲，
  使网络不会因为每游戏刻传输大量物品的电力消耗而耗尽能量。
