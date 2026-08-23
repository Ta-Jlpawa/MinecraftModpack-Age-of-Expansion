---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: ME IO端口
  icon: io_port
  position: 210
categories:
- devices
item_ids:
- ae2:io_port
---

# ME IO端口

<BlockImage id="io_port" p:powered="true" scale="8" />

IO端口让你能够在[存储元件](../items-blocks-machines/storage_cells.md)与
[网络存储](../ae2-mechanics/import-export-storage.md)之间快速填充或清空数据。

可以用<ItemLink id="certus_quartz_wrench" />旋转它。

## 设置

*   IO端口可设置为在元件为空、已满或工作完成时将元件移至输出槽。
*   如果插入了<ItemLink id="redstone_card" />，会出现各种红石条件选项。
*   在GUI中央有一个箭头，用于设置物品的传输方向：从元件到[网络存储](../ae2-mechanics/import-export-storage.md)，
    还是从存储到元件。

## 升级

IO端口支持以下[升级卡](upgrade_cards.md)：

*   <ItemLink id="speed_card" /> 增加每次操作搬运的物品数量
*   <ItemLink id="redstone_card" /> 添加红石控制，可设为高信号时激活、低信号时激活，或每个脉冲激活一次

## 配方

<RecipeFor id="io_port" />
