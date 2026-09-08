---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 充能器
  icon: charger
  position: 310
categories:
- machines
item_ids:
- ae2:charger
---

# 充能器

<BlockImage id="charger" scale="8" />

充能器可为受支持的工具以及 <ItemLink id="certus_quartz_crystal" /> 充能。

电力可通过顶部或底部输入，既可使用 AE2 的[线缆](cables.md)，也可使用其他模组的能源线缆。它可以接受 AE2 的能量（AE）或 Forge 能量（FE）。物品可从任意侧面插入或取出，但只能取出成品，因此无需过滤器来防止取出未充能的赛特斯水晶。可用 <ItemLink id="certus_quartz_wrench" /> 旋转朝向，以便于自动化。

可用它将 <ItemLink id="certus_quartz_crystal" /> 制成 <ItemLink id="charged_certus_quartz_crystal" />，或将 <ItemLink id="minecraft:compass" /> 制成 <ItemLink id="meteorite_compass" />。

若要手动供电，可在其顶部或底部放置一个 <ItemLink id="crank" /> 并右键点击曲柄，直到物品充能完毕。

它同时也是[福鲁伊克斯研究员](fluix_researcher.md)的工作站点。

## 简单自动化

举例来说，利用可旋转的特性，你可以像这样半自动化地运行充能器：

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/charger_hopper.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 配方

<RecipeFor id="charger" />
