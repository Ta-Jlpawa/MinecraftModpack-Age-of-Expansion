---
navigation:
  parent: example-setups/example-setups-index.md
  title: 简单赛特斯农场
  icon: certus_quartz_crystal
  position: 110
---

# 简单赛特斯农场

如[赛特斯生长](../ae2-mechanics/certus-growth.md)中所述，<ItemLink id="certus_quartz_crystal" />的收获自动化
涉及<ItemLink id="annihilation_plane" />和<ItemLink id="storage_bus" />。
<ItemLink id="growth_accelerator" />用于大幅加快赛特斯石英幼芽的生长速度，然后由面板
破坏完全长成的<ItemLink id="quartz_cluster" />。过滤则利用了一个堪称幸运的特性：未成熟的
赛特斯幼芽掉落的是<ItemLink id="certus_quartz_dust" />，而不是什么都不掉。

这个农场配合<ItemLink id="flawless_budding_quartz" />可以完全自动运行，但如果是有瑕、破损或损坏的
萌芽赛特斯石英，你就必须手动更换萌芽方块。或者如[半自动赛特斯农场](semiauto-certus-farm.md)
和[进阶赛特斯农场](advanced-certus-farm.md)所述，实现自动更换。

估计速度参见[赛特斯生长](../ae2-mechanics/certus-growth.md)。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/simple_certus_farm.snbt" />

  <BoxAnnotation color="#dddddd" min="3.7 1 1" max="4 2 2">
        (1) 湮灭面板：没有可配置的界面，但可以附魔时运。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="3 1 1" max="3.3 2 2">
        (2) 存储总线 #1：过滤为赛特斯石英水晶。
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="3 1 .7" max="2 2 1">
        (3) 存储总线 #2：过滤为赛特斯石英水晶。优先级设置得高于主存储。
        <ItemImage id="certus_quartz_crystal" scale="2" />
  </BoxAnnotation>

<DiamondAnnotation pos="1 0.5 0.5" color="#00ff00">
        连接至主网络
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 配置

* 第一个<ItemLink id="annihilation_plane" />（1）没有界面且无法配置，但可以附魔时运。
* 第一个<ItemLink id="storage_bus" />（2）过滤为<ItemLink id="certus_quartz_crystal" />。
* 第二个<ItemLink id="storage_bus" />（3）过滤为<ItemLink id="certus_quartz_crystal" />，并且其
  [优先级](../ae2-mechanics/import-export-storage.md#storage-priority)设置得高于主存储。

## 工作原理

1. <ItemLink id="annihilation_plane" />试图破坏它面前的方块，但它只能破坏<ItemLink id="quartz_cluster" />，
   因为子网络上唯一的存储是被过滤为<ItemLink id="certus_quartz_crystal" />的<ItemLink id="storage_bus" />。
4. 第一个<ItemLink id="storage_bus" />把赛特斯石英水晶存入桶中。
5. 第二个<ItemLink id="storage_bus" />让主网络能够访问桶中的所有赛特斯石英水晶。它被设置为
   高[优先级](../ae2-mechanics/import-export-storage.md#storage-priority)，这样赛特斯石英水晶会被优先
   放回桶中而不是你的主存储。
