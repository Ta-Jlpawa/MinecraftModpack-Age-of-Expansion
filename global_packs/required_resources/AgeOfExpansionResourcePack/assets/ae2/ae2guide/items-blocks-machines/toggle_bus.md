---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 切换总线
  icon: toggle_bus
  position: 110
categories:
- network infrastructure
item_ids:
- ae2:toggle_bus
- ae2:inverted_toggle_bus
---

# 切换总线

<GameScene zoom="8" background="transparent">
<ImportStructure src="../assets/assemblies/toggle_bus.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

一种功能与<ItemLink id="fluix_glass_cable" />（福鲁伊克斯玻璃线缆）或其他线缆相似的总线，但它
允许通过红石信号来切换连接状态。这让你能够切断
[ME网络](../ae2-mechanics/me-network-connections.md)的一部分。

当提供红石信号时，该部件会启用连接；<ItemLink id="inverted_toggle_bus" /> 则提供相反的行为，改为禁用连接。

值得注意的是，切换这些总线可能会导致网络重启并重新计算所连接的设备。

它们是[线缆子部件](../ae2-mechanics/cable-subparts.md)。

## 配方

<RecipeFor id="toggle_bus" />

<RecipeFor id="inverted_toggle_bus" />
