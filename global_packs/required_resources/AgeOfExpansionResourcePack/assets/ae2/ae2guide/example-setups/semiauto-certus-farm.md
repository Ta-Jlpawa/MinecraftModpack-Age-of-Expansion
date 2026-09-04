---
navigation:
  parent: example-setups/example-setups-index.md
  title: 半自动赛特斯农场
  icon: certus_quartz_crystal
  position: 115
---

# 半自动赛特斯农场

遗憾的是，[简单赛特斯农场](simple-certus-farm.md)需要一个<ItemLink id="flawless_budding_quartz" />
才能完全自动运行。这要么需要[空间IO](../ae2-mechanics/spatial-io.md)，要么要把农场建在[陨石](../ae2-mechanics/meteorites.md)处。

不过，AE2 可以放置和破坏方块，所以也许可以
让你的农场*替你更换萌芽赛特斯方块*。（你需要定期向输入桶中放入一些
<ItemLink id="flawed_budding_quartz" />，并从耗尽的萌芽赛特斯桶中取出<ItemLink id="quartz_block" />）

要实现完全自动，参见[进阶赛特斯农场](advanced-certus-farm.md)。

这个农场比[简单赛特斯农场](simple-certus-farm.md)稍微复杂一些，因为它实际上是
3 个独立装置挤在一起。

估计速度参见[赛特斯生长](../ae2-mechanics/certus-growth.md)。

**这是一个复杂的建筑，有些东西藏在其他东西后面，请环视旋转以从各个角度查看**

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/semiauto_certus_farm.snbt" />

  <BoxAnnotation color="#ddaaaa" min="3.7 2 1" max="4 3 2">
        (1) 湮灭面板 #1：没有可配置的界面，但可以附魔时运。
  </BoxAnnotation>

  <BoxAnnotation color="#ddaaaa" min="2 2 1" max="2.3 3 2">
        (2) 存储总线 #1：过滤为赛特斯石英水晶。
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

  <DiamondAnnotation pos="3 2.5 1.5" color="#ff0000">
    晶簇破坏子网络
  </DiamondAnnotation>

  <BoxAnnotation color="#aaddaa" min="3.7 1 1" max="4 2 2">
        (3) 湮灭面板 #2：没有可配置的界面，但已附魔精准采集。
  </BoxAnnotation>

  <BoxAnnotation color="#aaddaa" min="2 1 1" max="2.3 2 2">
        (4) 存储总线 #2：过滤为赛特斯石英块。
        <BlockImage id="quartz_block" scale="2" />
  </BoxAnnotation>

  <DiamondAnnotation pos="3 1.5 1.5" color="#00ff00">
    赛特斯方块破坏子网络
  </DiamondAnnotation>

  <BoxAnnotation color="#ffddaa" min="4 0.7 1" max="5 1 2">
        (5) 生成面板：处于默认配置。
  </BoxAnnotation>

  <BoxAnnotation color="#ffddaa" min="2 0 1" max="2.3 1 2">
        (6) 输入总线：处于默认配置。
  </BoxAnnotation>

  <DiamondAnnotation pos="3 0.5 1.5" color="#ddcc00">
    萌芽方块放置子网络
  </DiamondAnnotation>

  <BoxAnnotation color="#aaaadd" min="0.7 2 1" max="1 3 2">
        (7) 存储总线 #3：过滤为赛特斯石英水晶。优先级设置得高于你的主存储。
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

    <DiamondAnnotation pos="1.5 0.5 1.5" color="#00ff00">
        手动放入有瑕萌芽赛特斯石英。
        <BlockImage id="flawed_budding_quartz" scale="2" />
    </DiamondAnnotation>

    <DiamondAnnotation pos="1.5 1.5 1.5" color="#00ff00">
        手动取出赛特斯石英块。
        <BlockImage id="quartz_block" scale="2" />
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 0" color="#00ff00">
        连接至主网络
    </DiamondAnnotation>

  <IsometricCamera yaw="165" pitch="5" />
</GameScene>

## 配置

### 晶簇破坏器：

* 第一个<ItemLink id="annihilation_plane" />（1）没有界面且无法配置，但可以附魔时运。
* 第一个<ItemLink id="storage_bus" />（2）过滤为<ItemLink id="certus_quartz_crystal" />。

### 赛特斯方块破坏器：

* 第二个<ItemLink id="annihilation_plane" />（3）没有界面且无法配置，但必须附魔精准采集。
* 第二个<ItemLink id="storage_bus" />（4）过滤为<ItemLink id="quartz_block" />。

### 萌芽方块放置器：

* <ItemLink id="formation_plane" />（5）处于默认配置。
* <ItemLink id="import_bus" />（6）处于默认配置。

### 主网络上：

* 第三个<ItemLink id="storage_bus" />（7）过滤为<ItemLink id="certus_quartz_crystal" />，并且其
  [优先级](../ae2-mechanics/import-export-storage.md#storage-priority)设置得高于你的主存储。

## 工作原理

### 晶簇破坏器：

晶簇破坏子网络的工作方式与[简单赛特斯农场](simple-certus-farm.md)中的子网络非常相似。

1. <ItemLink id="annihilation_plane" />试图破坏它面前的方块，但它只能破坏<ItemLink id="quartz_cluster" />，
   因为子网络上唯一的存储是被过滤为<ItemLink id="certus_quartz_crystal" />的<ItemLink id="storage_bus" />。
2. <ItemLink id="storage_bus" />把赛特斯石英水晶存入桶中。

### 赛特斯方块破坏器

赛特斯方块破坏子网络用于在萌芽方块退化为普通<ItemLink id="quartz_block" />后将其破坏。
它的工作方式与晶簇破坏器类似。

1. <ItemLink id="annihilation_plane" />试图破坏它面前的方块，但它只能破坏<ItemLink id="quartz_block" />，
   因为子网络上唯一的存储是被过滤为<ItemLink id="quartz_block" />的<ItemLink id="storage_bus" />。
   面板需要精准采集，这样萌芽方块在被破坏时就不会退化，因而面板也不会过早将其破坏。
2. <ItemLink id="storage_bus" />把赛特斯石英块存入耗尽的
   萌芽赛特斯桶中，你需要手动把它连同<ItemLink id="charged_certus_quartz_crystal" />一起投入水中来刷新它。

### 萌芽方块放置器

萌芽方块放置子网络用于在破坏子网络破坏旧的耗尽萌芽方块后放置一个新的<ItemLink id="flawed_budding_quartz" />。

1. <ItemLink id="import_bus" />从输入桶中导入一个萌芽方块。
2. 子网络上唯一的存储是<ItemLink id="formation_plane" />，它会放置这个萌芽方块。

### 在主网络上

* <ItemLink id="storage_bus" />让主网络（以及[充能器自动化](charger-automation.md)）能够访问桶中的所有赛特斯石英水晶。它被设置为
  高[优先级](../ae2-mechanics/import-export-storage.md#storage-priority)，这样赛特斯石英水晶会被优先
  放回桶中而不是你的主存储。
