---
navigation:
  parent: example-setups/example-setups-index.md
  title: 紫水晶农场
  icon: minecraft:amethyst_shard
---

# 种植紫水晶

虽然<ItemLink id="growth_accelerator" />对紫水晶同样有效，但用<ItemLink id="annihilation_plane" />过滤[赛特斯芽](../items-blocks-machines/budding_certus.md)的惯常做法并不适用于紫水晶芽。未成熟的赛特斯芽会掉落<ItemLink id="certus_quartz_dust" />，而未成熟的紫水晶芽什么都不掉落，因此湮灭面板总是会将其破坏——因为网络永远可以存储"无"。

解决方法是给湮灭面板附魔精准采集。这样一来，未成熟的紫水晶芽*确实*会掉落东西（各个生长阶段的紫水晶芽方块），于是便可以被过滤了。

之后需要由<ItemLink id="formation_plane" />重新放置<ItemLink id="minecraft:amethyst_cluster" />，再由不带精准采集附魔的<ItemLink id="annihilation_plane" />将其重新打破，以获得<ItemLink id="minecraft:amethyst_shard" />。

注意，由于紫水晶簇具有方向性，生成面板的正对面必须紧邻一个实心方块面。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/amethyst_farm.snbt" />

  <BoxAnnotation color="#dddddd" min="2.7 1 1" max="3 2 2">
        (1) 湮灭面板 #1：没有可配置的界面，但附魔了精准采集。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2 1 1" max="2.3 2 2">
        (2) 生成面板：过滤为紫水晶簇。
        <ItemImage id="minecraft:amethyst_cluster" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1.3 0.7 1" max="2 1 2">
        (3) 湮灭面板 #2：没有可配置的界面，但可以附魔时运。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 0 1" max="1.3 1 2">
        (4) 存储总线 #1：过滤为紫水晶碎片。
        <ItemImage id="minecraft:amethyst_shard" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="0 0 .7" max="1 1 1">
        (5) 存储总线 #2：过滤为紫水晶碎片。优先级设置得高于你的主存储。
        <ItemImage id="minecraft:amethyst_shard" scale="2" />
  </BoxAnnotation>

<DiamondAnnotation pos="0 0.5 0.5" color="#00ff00">
        连接至主网络
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 配置

* 第一个<ItemLink id="annihilation_plane" /> (1) 没有可配置的界面，但必须附魔精准采集。
* <ItemLink id="formation_plane" /> (2) 过滤为<ItemLink id="minecraft:amethyst_cluster" />。
* 第二个<ItemLink id="annihilation_plane" /> (3) 没有可配置的界面，但可以附魔时运。
* 第一个<ItemLink id="storage_bus" /> (4) 过滤为<ItemLink id="minecraft:amethyst_shard" />。
* 第二个<ItemLink id="storage_bus" /> (5) 过滤为<ItemLink id="minecraft:amethyst_shard" />，并且其
  [优先级](../ae2-mechanics/import-export-storage.md#storage-priority)设置得高于你的主存储。

## 工作原理

1. 第一个<ItemLink id="annihilation_plane" /> 会尝试破坏它面前的物品，但它只能破坏<ItemLink id="minecraft:amethyst_cluster" />，
    因为该子网络上唯一的存储是过滤为紫水晶簇的<ItemLink id="formation_plane" />。之所以能这样工作，
是因为该面板附魔了精准采集；否则它就能破坏那些什么都不掉落的未成熟芽了。
2. <ItemLink id="formation_plane" /> 将紫水晶簇放置在它对面的方块上。
3. 第二个<ItemLink id="annihilation_plane" /> 打破紫水晶簇，产出<ItemLink id="minecraft:amethyst_shard" />。
4. 第一个<ItemLink id="storage_bus" /> 将碎片存入木桶。严格来说这一步不需要过滤，因为第二个湮灭面板面对的只应是完全长成的紫水晶簇。
5. 第二个<ItemLink id="storage_bus" /> 让主网络能够访问木桶中的所有紫水晶碎片。它的
  [优先级](../ae2-mechanics/import-export-storage.md#storage-priority)设置得很高，使得紫水晶碎片会优先进回木桶，
而不是进入你的主存储。
