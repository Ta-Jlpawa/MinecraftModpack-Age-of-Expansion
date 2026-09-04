---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 萌芽赛特斯石英方块
  icon: flawless_budding_quartz
  position: 010
categories:
- misc ingredients blocks
item_ids:
- ae2:flawless_budding_quartz
- ae2:flawed_budding_quartz
- ae2:chipped_budding_quartz
- ae2:damaged_budding_quartz
- ae2:small_quartz_bud
- ae2:medium_quartz_bud
- ae2:large_quartz_bud
- ae2:quartz_cluster
---

# 萌芽赛特斯石英方块

（另见[赛特斯生长](../ae2-mechanics/certus-growth.md)）

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/budding_blocks.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

赛特斯石英芽会从萌芽赛特斯方块上长出，与紫水晶类似。它们可以在[陨石](../ae2-mechanics/meteorites.md)中找到。
萌芽赛特斯方块共有 4 个等级：无瑕、有瑕、破损、损坏。借助 HWYLA、Jade、The One Probe 等模组（或 F3 调试屏幕）可以最轻松地分辨它们。

对于有瑕、破损和损坏的萌芽赛特斯方块，石英芽每生长一个阶段，萌芽方块就有一定概率降低一个等级，最终变成普通的 <ItemLink id="quartz_block" />。

无瑕萌芽赛特斯方块不会因石英芽生长而降级，可以作为无限来源。

用普通镐破坏萌芽赛特斯方块会使其降低 1 个等级。用附魔了精准采集的镐破坏则不会降级——除非它是无瑕的。**这意味着无瑕萌芽赛特斯方块无法用镐采集并移动**。不过可以改用[空间存储](../ae2-mechanics/spatial-io.md)，以剪切粘贴的方式挪动无瑕萌芽方块。

## 配方

将上一等级的萌芽方块（或 <ItemLink id="quartz_block" />）与一个或多个 <ItemLink id="charged_certus_quartz_crystal" /> 一起丢入水中，即可合成有瑕、破损和损坏的萌芽赛特斯方块。

无瑕萌芽赛特斯无法合成，只能在世界中找到。

<Row>
  <RecipeFor id="damaged_budding_quartz" />

  <RecipeFor id="chipped_budding_quartz" />

  <RecipeFor id="flawed_budding_quartz" />
</Row>
