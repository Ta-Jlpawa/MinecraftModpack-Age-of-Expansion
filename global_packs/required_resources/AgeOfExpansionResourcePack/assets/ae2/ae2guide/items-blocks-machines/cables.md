---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 线缆
  icon: fluix_glass_cable
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:white_glass_cable
- ae2:orange_glass_cable
- ae2:magenta_glass_cable
- ae2:light_blue_glass_cable
- ae2:yellow_glass_cable
- ae2:lime_glass_cable
- ae2:pink_glass_cable
- ae2:gray_glass_cable
- ae2:light_gray_glass_cable
- ae2:cyan_glass_cable
- ae2:purple_glass_cable
- ae2:blue_glass_cable
- ae2:brown_glass_cable
- ae2:green_glass_cable
- ae2:red_glass_cable
- ae2:black_glass_cable
- ae2:fluix_glass_cable
- ae2:white_covered_cable
- ae2:orange_covered_cable
- ae2:magenta_covered_cable
- ae2:light_blue_covered_cable
- ae2:yellow_covered_cable
- ae2:lime_covered_cable
- ae2:pink_covered_cable
- ae2:gray_covered_cable
- ae2:light_gray_covered_cable
- ae2:cyan_covered_cable
- ae2:purple_covered_cable
- ae2:blue_covered_cable
- ae2:brown_covered_cable
- ae2:green_covered_cable
- ae2:red_covered_cable
- ae2:black_covered_cable
- ae2:fluix_covered_cable
- ae2:white_covered_dense_cable
- ae2:orange_covered_dense_cable
- ae2:magenta_covered_dense_cable
- ae2:light_blue_covered_dense_cable
- ae2:yellow_covered_dense_cable
- ae2:lime_covered_dense_cable
- ae2:pink_covered_dense_cable
- ae2:gray_covered_dense_cable
- ae2:light_gray_covered_dense_cable
- ae2:cyan_covered_dense_cable
- ae2:purple_covered_dense_cable
- ae2:blue_covered_dense_cable
- ae2:brown_covered_dense_cable
- ae2:green_covered_dense_cable
- ae2:red_covered_dense_cable
- ae2:black_covered_dense_cable
- ae2:fluix_covered_dense_cable
- ae2:white_smart_cable
- ae2:orange_smart_cable
- ae2:magenta_smart_cable
- ae2:light_blue_smart_cable
- ae2:yellow_smart_cable
- ae2:lime_smart_cable
- ae2:pink_smart_cable
- ae2:gray_smart_cable
- ae2:light_gray_smart_cable
- ae2:cyan_smart_cable
- ae2:purple_smart_cable
- ae2:blue_smart_cable
- ae2:brown_smart_cable
- ae2:green_smart_cable
- ae2:red_smart_cable
- ae2:black_smart_cable
- ae2:fluix_smart_cable
- ae2:white_smart_dense_cable
- ae2:orange_smart_dense_cable
- ae2:magenta_smart_dense_cable
- ae2:light_blue_smart_dense_cable
- ae2:yellow_smart_dense_cable
- ae2:lime_smart_dense_cable
- ae2:pink_smart_dense_cable
- ae2:gray_smart_dense_cable
- ae2:light_gray_smart_dense_cable
- ae2:cyan_smart_dense_cable
- ae2:purple_smart_dense_cable
- ae2:blue_smart_dense_cable
- ae2:brown_smart_dense_cable
- ae2:green_smart_dense_cable
- ae2:red_smart_dense_cable
- ae2:black_smart_dense_cable
- ae2:fluix_smart_dense_cable
---

# 线缆

<GameScene zoom="3" background="transparent">
  <ImportStructure src="../assets/assemblies/cables.snbt" />
  <IsometricCamera yaw="180" pitch="30" />
</GameScene>

虽然 ME 网络也可以由相邻的兼容机器组成，但线缆是在更大范围内扩展 ME 网络的主要方式。

不同颜色的线缆可以确保相邻的线缆不会互相连接，从而使[频道](../ae2-mechanics/channels.md)得到更高效的分配。线缆还会影响所连接终端的颜色，这样你就不必让所有终端都是紫色。福鲁伊克斯色线缆可与所有其他颜色的线缆相连。

值得注意的是：**频道与线缆颜色毫无关系**

## 重要提示

**如果你是 AE2 新手且不熟悉频道机制，请尽量使用智能线缆和致密智能线缆。它们会显示频道在网络中的走向，让你更容易理解频道的行为。**

## 另一提示

**这些不是物品、流体、能量之类的管道。** 它们没有内部容器，样板供应器和机器也不会向其中"推送"内容，它们的全部作用只是将 AE2 [设备](../ae2-mechanics/devices.md)连成一个网络。

## 玻璃线缆

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/fluix_glass_cable.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

<ItemLink id="fluix_glass_cable" /> 是制作最简单的线缆，可传输电力以及最多 8 条[频道](../ae2-mechanics/channels.md)。它有 17 种颜色，默认为福鲁伊克斯色，并可用 16 种染料中的任意一种染色。

要合成彩色线缆，请用 8 根相同类型的线缆包围一个任意类型的染料（线缆颜色无所谓，但必须是同一类型：玻璃线缆、智能线缆等）。你也可以在世界中使用任何 Forge 兼容的刷子为线缆涂色。

将任意颜色的线缆与水桶合成即可去除染色。

用羊毛包覆线缆可以制成 <ItemLink id="fluix_covered_cable" />，再合成 <ItemLink id="fluix_smart_cable" /> 则能更直观地了解[频道](../ae2-mechanics/channels.md)的使用情况。

<RecipeFor id="fluix_glass_cable" />

<RecipeFor id="blue_glass_cable" />

## 包层线缆

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/fluix_covered_cable.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

包层线缆相比 <ItemLink id="fluix_glass_cable" /> 在玩法上没有任何优势。但如果你更喜欢包层的外观，它可以作为一种备选的美观选择。

其染色方式与 <ItemLink id="fluix_glass_cable" /> 相同。四根 <ItemLink id="fluix_covered_cable" /> 可与红石和荧石合成为 <ItemLink id="fluix_covered_dense_cable" />。

<Recipe id="network/cables/covered_fluix" />

<RecipeFor id="blue_covered_cable" />

## 致密线缆

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/fluix_covered_dense_cable.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

更高容量的线缆，可承载 32 条频道，而普通线缆只能承载 8 条。但它不支持总线，因此在使用总线或面板之前，必须先从致密线缆降级为更细的线缆（例如 <ItemLink id="fluix_glass_cable" /> 或 <ItemLink id="fluix_smart_cable" />）。

致密线缆会略微覆盖频道的"最短路径"行为：频道会先走最短路径到达致密线缆，然后再沿该致密线缆走最短路径通往控制器。

<Recipe id="network/cables/dense_covered_fluix" />

<RecipeFor id="blue_covered_dense_cable" />

## 智能线缆

<Row>
<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/fluix_smart_cable.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>
<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/fluix_smart_dense_cable.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>
</Row>

虽然外观上与 <ItemLink id="fluix_covered_cable" /> 有些相似，但智能线缆提供诊断功能，可将频道使用情况可视化地显示在线缆上：频道表现为沿线缆黑色条纹延伸的亮色线条，让你了解网络中频道的实际占用情况。对于普通智能线缆，前四条频道显示为与线缆同色的线条，后四条显示为白色线条。对于致密智能线缆，每条条纹代表 4 条频道。

在带有 <ItemLink id="controller" /> 的网络上，线缆上的线条会精确显示频道所经过的路径。

而在非持久网络（ad-hoc 网络）上，智能线缆显示的是整个网络正在使用的频道总数，而非流经该条线缆的频道数。

智能线缆同样可以用与 <ItemLink id="fluix_glass_cable" /> 相同的方式染色。

<Recipe id="network/cables/smart_fluix" />

<Recipe id="network/cables/dense_smart_fluix" />

<RecipeFor id="blue_smart_cable" />
