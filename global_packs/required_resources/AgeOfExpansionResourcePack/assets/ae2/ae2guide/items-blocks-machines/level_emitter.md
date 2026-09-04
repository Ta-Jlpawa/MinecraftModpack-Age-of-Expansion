---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 电平发射器
  icon: level_emitter
  position: 220
categories:
- devices
item_ids:
- ae2:level_emitter
- ae2:energy_level_emitter
---

# 电平发射器

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../assets/blocks/level_emitter.snbt" />
</GameScene>

电平发射器会根据[网络存储](../ae2-mechanics/import-export-storage.md)中某种物品的数量
发出红石信号。

还有一种版本会根据网络中存储的[能量](../ae2-mechanics/energy.md)发出红石信号。

即使你实际上没有某种物品，也可以从JEI/REI将物品和流体拖入槽位。

右键点击时手持流体容器（如桶或流体储罐），可将该流体设为过滤器，而不是桶或储罐物品本身。

它们是[线缆子部件](../ae2-mechanics/cable-subparts.md)。

与其他[设备](../ae2-mechanics/devices.md)不同，电平发射器*不需要*[频道](../ae2-mechanics/channels.md)。

## 设置

*   电平发射器可以设置为"大于等于"或"小于"模式
*   插入<ItemLink id="crafting_card" />后，可设置为"在合成物品期间发出红石信号"或
    "发出红石信号以合成物品"

## 升级

电平发射器支持以下[升级卡](upgrade_cards.md)：

*   <ItemLink id="fuzzy_card" /> 让发射器按损坏程度过滤和/或忽略物品NBT
*   <ItemLink id="crafting_card" /> 启用合成功能

## 合成功能

如果插入了<ItemLink id="crafting_card" />，发射器将切换到合成模式。

这会启用两个选项：

第一个选项"在合成物品期间发出红石信号"，使发射器在你的[自动合成](../ae2-mechanics/autocrafting.md)系统
通过<ItemLink id="pattern_provider" />合成某件特定物品时发出红石信号。这对于只在
高耗能自动化配置实际运行时才将其开启非常有用。

第二个选项"发出红石信号以合成物品"，对于某些特定用途极为有用，例如无限农场，
以及只能概率性产出的自动化配置（而非保证产出）。
此设置会为发射器过滤槽中的任意物品创建一个供[自动合成](../ae2-mechanics/autocrafting.md)使用的虚拟[样板](patterns.md)。
（为了正常运作，你的<ItemLink id="pattern_provider" />中**不应存在**同一物品的实际样板）

这个"样板"不定义、也不关心原料是什么。
它只表示"如果你从这个电平发射器发出红石信号，ME系统将在不久或遥远的将来收到该物品"。这通常用于开启和关闭无需输入原料的无限农场，
或者用于激活[处理递归配方的系统](../example-setups/recursive-crafting-setup.md)（标准自动合成无法理解这种配方），
例如你有一台复制圆石的机器时，"1个圆石 = 2个圆石"这样的配方。

## 配方

<RecipeFor id="level_emitter" />

<RecipeFor id="energy_level_emitter" />
