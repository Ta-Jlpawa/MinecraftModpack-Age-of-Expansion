---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 无线接入点
  icon: wireless_access_point
  position: 210
categories:
- devices
item_ids:
- ae2:wireless_booster
- ae2:wireless_access_point
---

# 无线接入点

<BlockImage id="wireless_access_point" p:state="has_channel" scale="8" />

允许通过<ItemLink id="wireless_terminal" />进行无线访问。
作用范围和耗电量取决于已安装的<ItemLink id="wireless_booster" />数量。

一个网络可以有任意数量的无线接入点，每个接入点内也可以安装任意数量的
<ItemLink id="wireless_booster" />，让你可以通过调整配置来优化功耗和作用范围。

需要一个[频道](../ae2-mechanics/channels.md)。

也用于绑定[无线终端](wireless_terminals.md)

# 无线增程器

<ItemImage id="wireless_booster" scale="2" />

用于增加无线接入点的作用范围。

## 配方

<RecipeFor id="wireless_access_point" />

<RecipeFor id="wireless_booster" />
