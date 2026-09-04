---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 能量元件
  icon: energy_cell
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:energy_cell
- ae2:dense_energy_cell
- ae2:creative_energy_cell
---

# 能量元件

<Row gap="20">
  <BlockImage id="energy_cell" scale="8" p:fullness="4" />

  <BlockImage id="dense_energy_cell" scale="8" p:fullness="4" />

  <BlockImage id="creative_energy_cell" scale="8" />
</Row>

能量元件为网络提供更多[能量](../ae2-mechanics/energy.md)存储。一定的能量缓存有助于平抑大量物品存取时的能耗峰值，而更大的能量存储容量则能让网络在能量产出中断时（比如太阳能板在夜间）继续运行，或应对[空间存储](../ae2-mechanics/spatial-io.md)带来的巨大瞬时能耗。

## 填充条

<Row>
<BlockImage id="energy_cell" scale="4" p:fullness="0" />
<BlockImage id="energy_cell" scale="4" p:fullness="1" />
<BlockImage id="energy_cell" scale="4" p:fullness="2" />
<BlockImage id="energy_cell" scale="4" p:fullness="3" />
<BlockImage id="energy_cell" scale="4" p:fullness="4" />
</Row>

元件侧面的条纹代表其当前储存的能量。

*   电量低于 25% 时为 0 格
*   电量介于 25% 和 50% 之间时为 1 格
*   电量介于 50% 和 75% 之间时为 2 格
*   电量介于 75% 和 99% 之间时为 3 格
*   电量高于 99% 时为 4 格

## 元件类型

*   <ItemLink id="energy_cell" /> 可存储 200k AE，对大多数使用场景而言一个就足够了，可以轻松应对正常网络使用的电力波动。
*   <ItemLink id="dense_energy_cell" /> 可存储 1.6M AE，适合想让网络完全依靠存储电力运行的情况，或需要应对大型[空间存储](../ae2-mechanics/spatial-io.md)设施巨大瞬时能耗的情况。
*   <ItemLink id="creative_energy_cell" /> 是用于测试的创造模式物品，提供无穷无尽的能量。

## 配方

<Row>
  <RecipeFor id="energy_cell" />

  <RecipeFor id="dense_energy_cell" />
</Row>
