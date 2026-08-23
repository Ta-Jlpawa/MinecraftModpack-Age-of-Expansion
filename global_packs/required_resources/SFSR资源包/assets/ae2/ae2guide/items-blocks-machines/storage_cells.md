---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 存储元件
  icon: item_storage_cell_1k
  position: 410
categories:
- tools
item_ids:
- ae2:item_cell_housing
- ae2:fluid_cell_housing
- ae2:cell_component_1k
- ae2:cell_component_4k
- ae2:cell_component_16k
- ae2:cell_component_64k
- ae2:cell_component_256k
- ae2:item_storage_cell_1k
- ae2:item_storage_cell_4k
- ae2:item_storage_cell_16k
- ae2:item_storage_cell_64k
- ae2:item_storage_cell_256k
- ae2:fluid_storage_cell_1k
- ae2:fluid_storage_cell_4k
- ae2:fluid_storage_cell_16k
- ae2:fluid_storage_cell_64k
- ae2:fluid_storage_cell_256k
---

# 存储元件

<Column>
  <Row>
    <ItemImage id="item_storage_cell_1k" scale="4" />

    <ItemImage id="item_storage_cell_4k" scale="4" />

    <ItemImage id="item_storage_cell_16k" scale="4" />

    <ItemImage id="item_storage_cell_64k" scale="4" />

    <ItemImage id="item_storage_cell_256k" scale="4" />
  </Row>

  <Row>
    <ItemImage id="fluid_storage_cell_1k" scale="4" />

    <ItemImage id="fluid_storage_cell_4k" scale="4" />

    <ItemImage id="fluid_storage_cell_16k" scale="4" />

    <ItemImage id="fluid_storage_cell_64k" scale="4" />

    <ItemImage id="fluid_storage_cell_256k" scale="4" />
  </Row>
</Column>

存储元件是应用能源中最主要的存储方式之一。它们放入<ItemLink id="drive" />（驱动器）或<ItemLink id="chest" />（ME存储箱）中。

关于其以字节和类型计的容量说明，参见[字节与类型](../ae2-mechanics/bytes-and-types.md)。

若元件已空，手持存储元件按住 Shift 右键点击即可将存储组件从外壳中取出。

<Row>
    <Recipe id="upgrade/item_storage_cell_1k_to_4k" />

    你可以将存储元件与更高等级的存储组件在合成格中结合，从而将其升级到更高等级。其中的内容会被保留，低等级组件则会退还。
</Row>

## 不同类型数量下的存储容量

由于[类型的预先开销](../ae2-mechanics/bytes-and-types.md)，仅存放 1 种类型的元件容量可达 63 种类型全部使用时的 2 倍。

| 元件                                     | 使用 1 种类型时元件的总容量 | 使用 63 种类型时元件的总容量 |
| ---------------------------------------- | ----------------------------------------: | ------------------------------------------: |
| <ItemLink id="item_storage_cell_1k" />   |                                     8,128 |                                       4,160 |
| <ItemLink id="item_storage_cell_4k" />   |                                    32,512 |                                      16,640 |
| <ItemLink id="item_storage_cell_16k" />  |                                   130,048 |                                      66,560 |
| <ItemLink id="item_storage_cell_64k" />  |                                   520,192 |                                     266,240 |
| <ItemLink id="item_storage_cell_256k" /> |                                 2,080,768 |                                   1,064,960 |


## 分区

可以对元件进行过滤，使其只接受特定物品，类似于对<ItemLink id="storage_bus" />（存储总线）进行过滤的方式。这需要在<ItemLink id="cell_workbench" />中完成。

即使你实际上没有任何该物品，也可以从 JEI/REI 将物品拖入槽位。

## 升级

存储元件支持以下[升级](upgrade_cards.md)，需通过<ItemLink id="cell_workbench" />插入：

*   <ItemLink id="fuzzy_card" />（流体元件不可用）让元件可以按损坏程度分区和/或忽略物品 NBT
*   <ItemLink id="inverter_card" /> 将过滤器从白名单切换为黑名单
*   <ItemLink id="equal_distribution_card" /> 为每种类型分配等量的元件字节空间，防止单一类型占满整个元件
*   <ItemLink id="void_card" /> 在元件已满时销毁存入的物品（若装有均分卡，则为该特定类型的分配空间已满），有助于防止农场积压。请务必小心设置分区！
*   便携元件可以接受 <ItemLink id="energy_card" /> 来提升电池容量

## 染色

便携物品元件和便携流体元件可以像皮革盔甲一样，与染料一起合成来进行染色。

# 外壳

元件可以用存储组件加外壳制作，也可以使用包裹存储组件的外壳配方制作：

<Row>
  <Recipe id="network/cells/item_storage_cell_1k" />

  <Recipe id="network/cells/item_storage_cell_1k_storage" />
</Row>

外壳本身的配方如下：

<Row>
  <RecipeFor id="item_cell_housing" />

  <RecipeFor id="fluid_cell_housing" />
</Row>

# 存储组件

存储组件是所有 AE2 元件的核心，决定元件的容量。每一等级的容量都是上一等级的 4 倍，且需要消耗 3 个上一等级组件。

<Column>
  <Row>
    <RecipeFor id="cell_component_1k" />

    <RecipeFor id="cell_component_4k" />

    <RecipeFor id="cell_component_16k" />
  </Row>

  <Row>
    <RecipeFor id="cell_component_64k" />

    <RecipeFor id="cell_component_256k" />
  </Row>
</Column>

# 物品存储元件

物品存储元件最多可存放 63 种不同类型的物品，并提供所有标准容量。

<Column>
  <Row>
    <Recipe id="network/cells/item_storage_cell_1k_storage" />

    <Recipe id="network/cells/item_storage_cell_4k_storage" />

    <Recipe id="network/cells/item_storage_cell_16k_storage" />
  </Row>

  <Row>
    <Recipe id="network/cells/item_storage_cell_64k_storage" />

    <Recipe id="network/cells/item_storage_cell_256k_storage" />
  </Row>
</Column>

## 便携物品存储

它们就像你口袋里的迷你<ItemLink id="chest" />（ME存储箱），或者说是一种背包。可以在<ItemLink id="charger" />（充能器）中充电

与标准存储元件不同，随着字节容量的增加，它们的类型容量反而会*减少*，且总字节容量只有一半。

除了所有元件都可接受的升级卡外，它们还可以接受<ItemLink id="energy_card" />来升级内部电池。

<Column>
  <Row>
    <RecipeFor id="portable_item_cell_1k" />

    <RecipeFor id="portable_item_cell_4k" />

    <RecipeFor id="portable_item_cell_16k" />
  </Row>

  <Row>
    <RecipeFor id="portable_item_cell_64k" />

    <RecipeFor id="portable_item_cell_256k" />
  </Row>
</Column>

# 流体存储元件

流体存储元件最多可存放 5 种不同类型的流体，并提供所有标准容量。

<Column>
  <Row>
    <Recipe id="network/cells/fluid_storage_cell_1k_storage" />

    <Recipe id="network/cells/fluid_storage_cell_4k_storage" />

    <Recipe id="network/cells/fluid_storage_cell_16k_storage" />
  </Row>

  <Row>
    <Recipe id="network/cells/fluid_storage_cell_64k_storage" />

    <Recipe id="network/cells/fluid_storage_cell_256k_storage" />
  </Row>
</Column>

## 便携流体存储

它们就像你口袋里的迷你<ItemLink id="chest" />（ME存储箱），或者说是一种背包。可以在<ItemLink id="charger" />（充能器）中充电

与标准存储元件不同，随着字节容量的增加，它们的类型容量反而会*减少*，且总字节容量只有一半。

除了所有元件都可接受的升级卡外，它们还可以接受<ItemLink id="energy_card" />来升级内部电池。

<Column>
  <Row>
    <RecipeFor id="portable_fluid_cell_1k" />

    <RecipeFor id="portable_fluid_cell_4k" />

    <RecipeFor id="portable_fluid_cell_16k" />
  </Row>

  <Row>
    <RecipeFor id="portable_fluid_cell_64k" />

    <RecipeFor id="portable_fluid_cell_256k" />
  </Row>
</Column>

# 创造存储元件

<Row>
  <ItemImage id="creative_storage_cell" scale="2" />
</Row>

创造元件**并不提供无限存储**。相反，它们是你所[分区](cell_workbench.md)指定的任意物品或流体的无限源与无限汇。
