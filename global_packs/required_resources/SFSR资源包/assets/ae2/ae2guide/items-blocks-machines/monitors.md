---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 显示器
  icon: storage_monitor
  position: 210
categories:
- devices
item_ids:
- ae2:storage_monitor
- ae2:conversion_monitor
---

# 显示器

<GameScene zoom="8" background="transparent">
<ImportStructure src="../assets/assemblies/monitors.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

显示器让你无需打开GUI，即可查看单一物品或流体并与之交互。

显示器会继承其所安装的[线缆](cables.md)的颜色。

如果显示器安装在地板或天花板上，可以用<ItemLink id="certus_quartz_wrench" />旋转它。

它们是[线缆子部件](../ae2-mechanics/cable-subparts.md)。

# 存储显示器

会显示某种物品或流体及其数量。把它们放在你的农场旁边之类的地方……

*不*需要[频道](../ae2-mechanics/channels.md)。

按键操作：

*   右键点击时手持某物品，或双击右键手持流体容器，可将显示器设为该物品/流体。
*   空手右键点击可清除显示器。
*   空手Shift+右键点击可锁定显示器。

## 配方

<RecipeFor id="storage_monitor" />

# 转换显示器

转换显示器与存储显示器类似，但允许你插入或取出其配置的物品。

如果配置的物品[可以自动合成](../ae2-mechanics/autocrafting.md)且存储中没有，尝试取出物品时
反而会打开一个界面，用于指定要合成的数量。

*确实*需要[频道](../ae2-mechanics/channels.md)。

额外的按键操作：

*   左键点击可取出最多一组配置的物品；若存储中没有，则请求合成该物品。
*   右键点击时手持任意物品，可插入该物品。
*   空手右键点击可从你的物品栏插入所有配置的物品。

## 配方

<RecipeFor id="conversion_monitor" />
