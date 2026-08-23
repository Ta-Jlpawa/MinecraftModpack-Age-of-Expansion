---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 空间存储元件
  icon: spatial_storage_cell_128
  position: 410
categories:
- tools
item_ids:
- ae2:spatial_storage_cell_2
- ae2:spatial_storage_cell_16
- ae2:spatial_storage_cell_128
- ae2:spatial_cell_component_2
- ae2:spatial_cell_component_16
- ae2:spatial_cell_component_128
---

# 空间存储元件

  <Row>
    <ItemImage id="spatial_storage_cell_2" scale="4" />

    <ItemImage id="spatial_storage_cell_16" scale="4" />

    <ItemImage id="spatial_storage_cell_128" scale="4" />
  </Row>

空间存储元件用于[储存物理空间体积](../ae2-mechanics/spatial-io.md)。
它们用在 <ItemLink id="spatial_io_port" /> 中。

与[存储元件](../items-blocks-machines/storage_cells.md)不同，空间元件无法重新格式化。

再强调一次，**空间存储元件一经使用后，便不能再重置、重新格式化或调整大小。**如果想使用不同的尺寸，请制作一个新的元件。


## 配方

  <Row>
    <Recipe id="network/cells/spatial_storage_cell_2_cubed_storage" />

    <Recipe id="network/cells/spatial_storage_cell_16_cubed_storage" />

    <Recipe id="network/cells/spatial_storage_cell_128_cubed_storage" />
  </Row>

# 外壳

元件可以用空间组件加外壳制作，也可以使用包裹空间组件的外壳配方制作：

<Row>
  <Recipe id="network/cells/spatial_storage_cell_2_cubed" />

  <Recipe id="network/cells/spatial_storage_cell_2_cubed_storage" />
</Row>

外壳本身的配方如下：

  <RecipeFor id="item_cell_housing" />

# 空间组件

空间组件是空间存储元件的核心。每一等级都会将可储存体积的尺寸扩大为原来的 8 倍。

  <Row>
    <RecipeFor id="spatial_cell_component_2" />

    <RecipeFor id="spatial_cell_component_16" />

    <RecipeFor id="spatial_cell_component_128" />
  </Row>
