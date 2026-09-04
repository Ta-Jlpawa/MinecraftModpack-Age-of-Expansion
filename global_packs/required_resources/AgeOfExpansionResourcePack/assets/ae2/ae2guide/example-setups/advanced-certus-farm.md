---
navigation:
  parent: example-setups/example-setups-index.md
  title: 高级赛特斯农场
  icon: certus_quartz_crystal
  position: 120
---

# 高级赛特斯农场

这基本上就是[半自动赛特斯农场](semiauto-certus-farm.md)，只不过它已完全整合进你的 ME 系统中。

这套设施不再需要囤积大量萌芽方块并定期手动刷新，而是利用[充能器自动化](charger-automation.md)和[投水自动化](throw-in-water-automation.md)
自动完成这一切。

预计速度请参见[赛特斯生长](../ae2-mechanics/certus-growth.md)。

**这个结构非常复杂，有设备藏在其他设备后面，请旋转视角从各个角度查看**

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/advanced_certus_farm.snbt" />

  <BoxAnnotation color="#ddaaaa" min="3.7 2 1" max="4 3 2">
        (1) 湮灭面板 #1：没有可配置的界面，但可以附魔时运。
  </BoxAnnotation>

  <BoxAnnotation color="#ddaaaa" min="2 2 1.7" max="3 3 2">
        (2) 存储总线 #1：过滤为赛特斯石英水晶。
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

  <DiamondAnnotation pos="3 2.5 1.5" color="#ff0000">
    晶簇破坏子网络
  </DiamondAnnotation>

  <BoxAnnotation color="#aaddaa" min="3.7 1 1" max="4 2 2">
        (3) 湮灭面板 #2：没有可配置的界面，但附魔了精准采集。
  </BoxAnnotation>

  <BoxAnnotation color="#aaddaa" min="2 1 1.7" max="3 2 2">
        (4) 存储总线 #2：过滤为赛特斯石英块。
        <BlockImage id="quartz_block" scale="2" />
  </BoxAnnotation>

  <DiamondAnnotation pos="3 1.5 1.5" color="#00ff00">
    赛特斯方块破坏子网络
  </DiamondAnnotation>

  <BoxAnnotation color="#ffddaa" min="4 0.7 1" max="5 1 2">
        (5) 生成面板：保持默认配置。
  </BoxAnnotation>

  <BoxAnnotation color="#ffddaa" min="2 0.7 2" max="3 1 3">
        (6) 输入总线：过滤为有瑕萌芽赛特斯石英。
        <BlockImage id="flawed_budding_quartz" scale="2" />
  </BoxAnnotation>

  <DiamondAnnotation pos="3 0.5 1.5" color="#ddcc00">
    萌芽方块放置子网络
  </DiamondAnnotation>

  <BoxAnnotation color="#aaaadd" min="1.7 2 2" max="2 3 3">
        (7) 存储总线 #3：过滤为赛特斯石英水晶。优先级设置得高于你的主存储。
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#aaaadd" min="2 1 2" max="3 2 3">
        (8) 接口：设置为在自身内保有 1 个有瑕萌芽赛特斯方块，带有一张合成卡。
        <Row><BlockImage id="flawed_budding_quartz" scale="2" /> <ItemImage id="crafting_card" scale="2" /></Row>
  </BoxAnnotation>

<DiamondAnnotation pos="1.5 0.5 0" color="#00ff00">
        连接至主网络、充能器自动化和投水自动化
        <Row>
        <GameScene zoom="3" background="transparent">
          <ImportStructure src="../assets/assemblies/charger_automation.snbt" />
          <IsometricCamera yaw="195" pitch="30" />
        </GameScene>
        <GameScene zoom="3" background="transparent">
          <ImportStructure src="../assets/assemblies/throw_in_water.snbt" />
          <IsometricCamera yaw="195" pitch="30" />
        </GameScene>
        </Row>
    </DiamondAnnotation>

  <IsometricCamera yaw="165" pitch="5" />
</GameScene>

## 配置

### 晶簇破坏子网络：

* 第一个<ItemLink id="annihilation_plane" /> (1) 没有可配置的界面，但可以附魔时运。
* 第一个<ItemLink id="storage_bus" /> (2) 过滤为<ItemLink id="certus_quartz_crystal" />。

### 赛特斯方块破坏子网络：

* 第二个<ItemLink id="annihilation_plane" /> (3) 没有可配置的界面，但必须附魔精准采集。
* 第二个<ItemLink id="storage_bus" /> (4) 过滤为<ItemLink id="quartz_block" />。

### 萌芽方块放置子网络：

* <ItemLink id="formation_plane" /> (5) 保持默认配置。
* <ItemLink id="import_bus" /> (6) 过滤为<ItemLink id="flawed_budding_quartz" />。

### 主网络上：

* 第三个<ItemLink id="storage_bus" /> (7) 过滤为<ItemLink id="certus_quartz_crystal" />，并且其
  [优先级](../ae2-mechanics/import-export-storage.md#storage-priority)设置得高于你的主存储。
* <ItemLink id="interface" /> (8) 设置为在自身内保有 1 个有瑕萌芽赛特斯方块，并带有一张<ItemLink id="crafting_card" />。

## 工作原理

### 晶簇破坏子网络：

晶簇破坏子网络的工作方式与[简易赛特斯农场](simple-certus-farm.md)中的子网络非常相似。

1. <ItemLink id="annihilation_plane" /> 会尝试破坏它面前的物品，但它只能破坏<ItemLink id="quartz_cluster" />，
   因为该子网络上唯一的存储是过滤为<ItemLink id="certus_quartz_crystal" />的<ItemLink id="storage_bus" />。
2. <ItemLink id="storage_bus" /> 将赛特斯石英水晶存入木桶。

### 赛特斯方块破坏子网络

赛特斯方块破坏子网络的作用是：当枯竭的萌芽方块变成普通的<ItemLink id="quartz_block" />之后将其破坏。
它的工作方式与晶簇破坏子网络类似。

1. <ItemLink id="annihilation_plane" /> 会尝试破坏它面前的物品，但它只能破坏<ItemLink id="quartz_block" />，
   因为该子网络上唯一的存储是过滤为<ItemLink id="quartz_block" />的<ItemLink id="storage_bus" />。
   该面板必须附魔精准采集，这样萌芽方块在被破坏时才不会降级，面板也就不会提前把它破坏掉。
2. <ItemLink id="storage_bus" /> 将赛特斯石英块存入<ItemLink id="interface" />，让
   [投水自动化](throw-in-water-automation.md)用它来制作新的<ItemLink id="flawed_budding_quartz" />。

### 萌芽方块放置子网络

萌芽方块放置子网络的作用是：当破坏子网络破坏掉旧的枯竭萌芽方块后，放置一个新的<ItemLink id="flawed_budding_quartz" />。

1. <ItemLink id="import_bus" /> 从<ItemLink id="interface" />中输入一个萌芽方块到[网络存储](../ae2-mechanics/import-export-storage.md)
2. 该子网络上唯一的存储是<ItemLink id="formation_plane" />，由它放置萌芽方块。

### 主网络上

* <ItemLink id="storage_bus" /> 让主网络（以及[充能器自动化](charger-automation.md)）能够访问木桶中的所有赛特斯石英水晶。它的
  [优先级](../ae2-mechanics/import-export-storage.md#storage-priority)设置得很高，使得赛特斯石英水晶会优先进回木桶，
  而不是进入你的主存储。
* <ItemLink id="interface" /> 让萌芽方块放置子网络能够取得一个<ItemLink id="flawed_budding_quartz" />，并为
  赛特斯方块破坏子网络提供了一条把枯竭方块送回主网络的途径。<ItemLink id="crafting_card" />
  使接口可以向主网络的[自动合成](../ae2-mechanics/autocrafting.md)请求新的萌芽方块。
