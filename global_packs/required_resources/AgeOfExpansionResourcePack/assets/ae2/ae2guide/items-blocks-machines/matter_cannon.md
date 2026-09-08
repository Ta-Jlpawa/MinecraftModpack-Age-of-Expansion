---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 物质炮
  icon: matter_cannon
  position: 410
categories:
- tools
item_ids:
- ae2:matter_cannon
---

# 物质炮

<ItemImage id="matter_cannon" scale="4" />

物质炮是一种便携式电磁炮，可以发射小型物品作为弹丸，例如<ItemLink id="matter_ball" />和金属粒。伤害
取决于发射的物品，"较重"的物品如金粒（10点伤害）比轻物品如物质球（2点伤害）造成更多伤害。
它每次射击消耗1600 AE的基础能量。

当配置选项"matterCannonBlockDamage"为true时，物质炮会根据方块硬度和
弹药伤害破坏方块。

它的能量可以在<ItemLink id="charger" />中充能。

物质炮的行为类似[存储元件](storage_cells.md)，其弹药仓最方便的填充方式是把它插进
<ItemLink id="chest" />的存储元件槽位。

## 升级

物质炮支持以下[升级卡](upgrade_cards.md)，需通过<ItemLink id="cell_workbench" />插入：

*   <ItemLink id="fuzzy_card" /> 让元件按损坏程度分区和/或忽略物品NBT
*   <ItemLink id="inverter_card" /> 将过滤器从白名单切换为黑名单
*   <ItemLink id="speed_card" /> 增加每次射击消耗的能量，使其以更大威力射击。
*   <ItemLink id="void_card" /> 当元件已满时销毁存入的物品。请务必小心分区！
*   <ItemLink id="energy_card" /> 用于增加电池容量

## 配方

<RecipeFor id="matter_cannon" />
