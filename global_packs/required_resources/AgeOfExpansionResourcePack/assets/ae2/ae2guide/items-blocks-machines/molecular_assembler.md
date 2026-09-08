---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 分子装配室
  icon: molecular_assembler
  position: 310
categories:
- machines
item_ids:
- ae2:molecular_assembler
---

# 分子装配室

<BlockImage id="molecular_assembler" scale="8" />

分子装配室接收输入的物品，执行相邻<ItemLink id="pattern_provider" />所定义的操作，
或执行其插入的<ItemLink id="crafting_pattern" />、<ItemLink id="smithing_table_pattern" />或<ItemLink id="stonecutting_pattern" />
所定义的操作，然后将产物推入相邻容器。

这台装配室中有一个指定"1个橡木原木 = 4块橡木木板"配方的合成样板。当把橡木原木送入上方漏斗时，
装配室会进行合成并将橡木木板吐入下方漏斗。

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/standalone_assembler.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 分子装配室的主要用途

不过，它们的主要用途是紧邻<ItemLink id="pattern_provider" />放置。在这种情况下，样板供应器有特殊行为，
会将相关样板的信息连同原料一起发送给相邻的装配室。由于装配室会自动将合成产物
弹出到相邻容器（也就是样板供应器的回收槽位），因此只需在样板供应器上贴一个装配室
即可实现合成样板的自动化。

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/assembler_tower.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 升级

分子装配室支持以下[升级卡](upgrade_cards.md)：

*   <ItemLink id="speed_card" />

## 配方

<RecipeFor id="molecular_assembler" />

## 注意

Optifine会破坏"推送到相邻容器"功能，因此大多数使用装配室的合成配置都无法正常工作。
