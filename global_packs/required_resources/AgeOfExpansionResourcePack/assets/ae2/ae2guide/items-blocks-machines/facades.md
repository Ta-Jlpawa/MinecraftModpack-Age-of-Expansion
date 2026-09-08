---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 伪装板
  icon: facade
  icon_components:
    "ae2:facade_item": "minecraft:stone"
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:facade
---

# 伪装板

伪装板可以让你的基地看起来更加整洁。它们可以包覆两种尺寸的线缆，并可由多种方块制成。

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/facades_1.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

伪装板可以包覆线缆的各个面，但[子部件](../ae2-mechanics/cable-subparts.md)和线缆连接处仍会露出来。

<GameScene zoom="6"  interactive={true}>
  <ImportStructure src="../assets/assemblies/facades_2.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

巧妙运用伪装板，既能提升基地的美观度，也能制作出各面材质不同的方块。

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/facades_3.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 隐藏伪装板

只要任一手持有<a href="network_tool.md">网络工具</a>，伪装板就会隐藏起来。

你可以直接与隐藏伪装板后方的方块交互，无需先拆除伪装板。

## 配方

将你想要其材质的方块放在 4 个 <ItemLink id="cable_anchor" /> 中间即可。

![伪装板配方](../assets/diagrams/facade_recipe.png)
