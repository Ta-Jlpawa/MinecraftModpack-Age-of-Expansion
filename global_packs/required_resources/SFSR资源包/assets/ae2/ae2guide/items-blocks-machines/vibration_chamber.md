---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 振动腔
  icon: vibration_chamber
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:vibration_chamber
---

# 振动腔

<BlockImage id="vibration_chamber" p:active="true" scale="8" />

虽然为网络供给[能量](../ae2-mechanics/energy.md)的主要预定方式是
<ItemLink id="energy_acceptor" />（能量接收器），但振动腔可以直接产生少量到中等量的 AE。

在默认情况下（无[升级](upgrade_cards.md)且配置为默认值）它产生 40 AE/t。

当网络的[能量](../ae2-mechanics/energy.md)存储已满时，振动腔会降低功率以节省燃料，但无法完全关闭。

## 设置

*   振动腔提供查看 AE 或 E/FE 能量的全局设置入口。

## 升级

振动腔支持以下[升级](upgrade_cards.md)：

*   <ItemLink id="energy_card" /> 将振动腔的效率提升 +50%，最高可达 +150%，即基础效率的 250%。
*   <ItemLink id="speed_card" /> 将振动腔的燃烧速率提升 +50%，最高可达 +150%，即基础输出功率的 250%。

## 配置

振动腔的各项属性可以在 .minecraft\
目录下 config 文件夹中的 ae2 文件夹里的 common.json 内编辑。

*   baseEnergyPerFuelTick 设定振动腔未升级时的基础效率。
*   minEnergyPerGameTick 设定最低可能的能量产出（即使网络不需要能量，振动腔也总会缓慢消耗一些燃料）。
*   maxEnergyPerGameTick 设定振动腔未升级时的最大输出（及速度）。

## 配方

<RecipeFor id="vibration_chamber" />
