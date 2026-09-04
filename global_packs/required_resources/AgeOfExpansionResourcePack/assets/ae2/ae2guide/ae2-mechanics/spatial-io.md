---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: 空间储存
  icon: spatial_storage_cell_2
---

# 空间储存

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/spatial_storage_1x1x1.snbt" />

  <BoxAnnotation color="#33dd33" min="1 1 1" max="2 2 2">
        要被移动的空间
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />

</GameScene>

空间储存是一种对世界中物理空间的体积进行"剪切-粘贴"的方法。它可以用来移动<ItemLink id="flawless_budding_quartz" />，
让你在基地里拥有一间可以随时更换内部构造、用作不同用途的房间，甚至可以移动末地传送门！

它的工作原理是将定义的体积与空间储存维度中一个大小完全相同的体积进行*交换*：把空间塔阵列中的东西送往空间储存维度，
同时把维度中的东西送到空间塔阵列。

这意味着如果你有办法在维度之间旅行（空间储存*确实*可以被用来做传送器，但实现起来非常复杂、有点不靠谱，且超出了本指南的范围），
你就可以把它们当作自定义尺寸的紧凑机器或口袋维度来使用。

# 多方块结构搭建

空间储存要求其组件以特定方式排列才能运作，并以此定义要被剪切-粘贴的体积。

所有组件必须位于同一个[网络](me-network-connections.md)上才能运作，并且每个网络上只能有一套
空间储存装置。因此推荐使用[子网络](subnetworks.md)。

## 空间IO端口

<BlockImage id="spatial_io_port" p:powered="true" scale="4" />

<ItemLink id="spatial_io_port" />控制空间储存操作。它显示多方块结构的状态数据，并放置
[空间存储元件](../items-blocks-machines/spatial_cells.md)。

它会显示：
- 网络中已存储的和最大的[能量](energy.md)
- 执行该操作所需的能量。这个数值可能非常巨大，而且会被瞬间抽取，所以要确保你有足够的
  [能量元件](../items-blocks-machines/energy_cells.md)来容纳它。
- 空间塔阵列的效率
- 定义体积的大小

执行一次空间储存操作的方法是：把空间存储元件放入输入槽，然后给空间IO端口一个红石脉冲。
它就会把空间塔内的体积与空间储存维度中的体积进行*交换*。也就是说，如果你把一组方块发送到空间储存维度，
*然后在空间塔里放入另一组方块*，再把存储元件放回输入槽并再次触发IO端口，第二组方块会消失，
而第一组方块会重新出现。

**千万小心：定义体积中的任何实体（包括你自己）都会被一同带走，如果你没有出去的办法，你就会被困在
空间储存维度里一个漆黑空荡的盒子中。** 用这个功能捉弄你的朋友们吧！

## 空间塔

<BlockImage id="spatial_pylon" p:powered_on="true" scale="4" />

<ItemLink id="spatial_pylon" />是空间储存装置的主体部分，用于定义受影响的体积。

体积由空间塔外侧的包围盒向内收缩1个方块来确定。

规则如下：
- 最小尺寸为3x3x3（即定义出1x1x1的体积）
- 所有空间塔必须位于外侧包围盒上
- 所有空间塔必须处于同一网络
- 每台空间塔至少要有2格长

举个例子，假设你想定义一个3x3x3的体积。根据第2条规则，所有空间塔都必须位于目标体积周围一圈5x5x5的外壳之内。
它们的布局几乎可以是任意形状，只要包含在那个厚度为1格的5x5x5外壳内即可。

<GameScene zoom="4" interactive={true}>
<ImportStructure src="../assets/assemblies/spatial_storage_3x3x3_pylon_demonstration.snbt" />

<BoxAnnotation color="#33dd33" min="1 1 1" max="4 4 4">
        要被移动的空间
  </BoxAnnotation>

<BoxAnnotation color="#3333ff" min="5 5 0" max="0 0 5">
  </BoxAnnotation>

<IsometricCamera yaw="195" pitch="30" />
</GameScene>

更合理的布局是这样：

<GameScene zoom="4" interactive={true}>
<ImportStructure src="../assets/assemblies/better_spatial_storage_3x3x3.snbt" />

<BoxAnnotation color="#33dd33" min="1 1 1" max="4 4 4">
        要被移动的空间
  </BoxAnnotation>

<BoxAnnotation color="#3333ff" min="5 5 0" max="0 0 5">
  </BoxAnnotation>

<IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 效率

空间塔阵列的效率取决于外壳被填满的比例。围绕大体积的最简布局效率会非常低，
可能需要*数十亿*AE的能量。

## 元件尺寸

一张[空间存储元件](../items-blocks-machines/spatial_cells.md)一旦被使用过，就会永久固定一组XYZ尺寸（例如3x4x2），
并与空间储存维度中的一块空间绑定。**空间存储元件一经使用就无法重置、重格式化或改变尺寸。**
想使用不同的尺寸就制作新元件。

这些尺寸与元件名称里的数字不是一回事：16^3的元件可以使用*最高至*16x16x16的任意尺寸。

注意这个体积是有方向性且不可旋转的：2x2x3的体积和3x2x2的体积并不相同，尽管它们大小一样。

如果元件的XYZ尺寸与定义的体积（可在IO端口查看）不匹配，IO端口将不会运作。
