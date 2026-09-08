---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 能量接收器
  icon: energy_acceptor
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:energy_acceptor
---

# 能量接收器

<Row gap="20">
<BlockImage id="energy_acceptor" scale="8" /> 

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../assets/blocks/cable_energy_acceptor.snbt" />
</GameScene>
</Row>

能量接收器可将其他科技模组的常见能量形式转换为 AE2 的内部[能量](../ae2-mechanics/energy.md)形式——AE。虽然 <ItemLink id="controller" /> 也能做到这一点，但控制器各面都很宝贵，因此通常用能量接收器更为合适。

Forge 能量与 TechReborn 能量的转换比率为：

*   2 FE = 1 AE（Forge）
*   1 E  = 2 AE（Fabric）

转换的速度完全取决于你的网络能存储多少 AE，原因在[这个页面](../ae2-mechanics/energy.md)有解释。

## 变体

能量接收器有两种变体：普通型和平面型/[子部件](../ae2-mechanics/cable-subparts.md)。这让你可以把某些布局做得更紧凑。

能量接收器可在合成网格中于普通型和平面型之间互相转换。

## 配方

<RecipeFor id="energy_acceptor" />

<RecipeFor id="cable_energy_acceptor" />
