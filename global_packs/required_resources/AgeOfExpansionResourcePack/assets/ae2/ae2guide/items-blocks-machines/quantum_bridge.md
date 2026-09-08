---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 量子桥
  icon: quantum_ring
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:quantum_link
- ae2:quantum_ring
---

# 量子网络桥

![A formed Quantum Network Bridge](../assets/diagrams/quantum_bridge_demonstration.png)

量子网络桥可以将[网络](../ae2-mechanics/me-network-connections.md)延伸到无限远的距离，甚至跨维度连接。
它们总共可以承载32个频道（无论线缆如何连接到每个面），本质上
就像一根无线的[致密线缆](cables.md#dense-cable)。

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/quantum_bridge_internal_structure_1.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/quantum_bridge_internal_structure_2.snbt" />

  <BoxAnnotation color="#33dd33" min="1 1 1" max="6 2 3">
        两端之间的一根想象中的线缆
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

值得注意的是，**两端都必须保持区块加载**，因此如果两端相距很远，
必须使用<ItemLink id="spatial_anchor" />或其他区块加载器。

# 量子环

<BlockImage id="quantum_ring" scale="8" />

将8个这样的方块围绕一个<ItemLink id="quantum_link" />放置，即可构成一台
量子网络桥。只有与<ItemLink id="quantum_link" />相邻的
4个<ItemLink id="quantum_ring" />方块能接受网络连接，
4个角落方块无法连接线缆。

## 配方

<RecipeFor id="quantum_ring" />

# 量子链接腔

<BlockImage id="quantum_link" scale="8" />

1个这样的方块被<ItemLink id="quantum_ring" />环绕，
即构成一台量子网络桥。这个方块不与任何线缆连接，只有在完整的桥建成之后才会
作为网络的一部分注册。

该方块的物品栏只能容纳单个<ItemLink id="quantum_entangled_singularity" />，且
支持自动化访问。

## 配方

<RecipeFor id="quantum_link" />
