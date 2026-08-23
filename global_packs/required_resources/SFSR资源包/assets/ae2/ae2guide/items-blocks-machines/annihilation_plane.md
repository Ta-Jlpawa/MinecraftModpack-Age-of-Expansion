---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 湮灭面板
  icon: annihilation_plane
  position: 210
categories:
- devices
item_ids:
- ae2:annihilation_plane
---

# 湮灭面板

<GameScene zoom="8" background="transparent">
<ImportStructure src="../assets/blocks/annihilation_plane.snbt" />
</GameScene>

湮灭面板会破坏方块并拾取物品。它的工作方式类似于 <ItemLink id="import_bus" />，将物品推入[网络存储](../ae2-mechanics/import-export-storage.md)。只有与面板表面发生碰撞的物品才会被拾取，它不会进行范围拾取。

湮灭面板可以附魔任何镐类附魔，所以没错，只要你的整合包允许，你可以给它附上高等级时运来[自动化矿石处理](../example-setups/ore-fortuner.md)。此外，精准采集的效果正如你所预期，效率附魔可降低破坏方块的能量消耗，耐久附魔则有一定概率不消耗能量。

它们是[线缆子部件](../ae2-mechanics/cable-subparts.md)。

**切记在你的区块领地中启用假玩家**

## 过滤

只有当网络能够存储破坏/拾取所得的掉落物或物品时，湮灭面板才会执行相应操作。这意味着若要对其进行过滤，*你必须限制其所在网络能够存储的物品*，最常用的方法是将其置于[子网络](../ae2-mechanics/subnetworks.md)上。<ItemLink id="storage_bus" /> 或[存储元件](../items-blocks-machines/storage_cells.md)可以通过[分区](cell_workbench.md)来实现这一点。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/annihilation_filtering.snbt" />

  <DiamondAnnotation pos="1 0.5 0.5" color="#00ff00">
        过滤为想要破坏的目标所掉落的物品。
  </DiamondAnnotation>

  <DiamondAnnotation pos=".5 0.5 2.5" color="#00ff00">
        分区为想要破坏的目标所掉落的物品。
  </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

再次强调，它是*根据物品掉落物*进行过滤的。举例来说，如果你想过滤对 <ItemLink id="minecraft:amethyst_cluster" /> 的破坏，就需要给面板附魔精准采集；否则紫水晶簇之前的每个生长阶段都不会掉落任何东西，而网络总能存储"空无一物"，因此面板无论如何都会将其破坏。

## 配方

<RecipeFor id="annihilation_plane" />
