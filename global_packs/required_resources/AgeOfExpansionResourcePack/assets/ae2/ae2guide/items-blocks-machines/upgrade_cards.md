---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 升级卡
  icon: speed_card
  position: 410
categories:
- tools
item_ids:
- ae2:basic_card
- ae2:advanced_card
- ae2:redstone_card
- ae2:capacity_card
- ae2:void_card
- ae2:fuzzy_card
- ae2:speed_card
- ae2:inverter_card
- ae2:crafting_card
- ae2:equal_distribution_card
- ae2:energy_card
---

# 升级卡

<Row>
  <ItemImage id="redstone_card" scale="2" />

  <ItemImage id="capacity_card" scale="2" />

  <ItemImage id="void_card" scale="2" />

  <ItemImage id="fuzzy_card" scale="2" />

  <ItemImage id="speed_card" scale="2" />

  <ItemImage id="inverter_card" scale="2" />

  <ItemImage id="crafting_card" scale="2" />

  <ItemImage id="equal_distribution_card" scale="2" />

  <ItemImage id="energy_card" scale="2" />
</Row>

升级卡可以改变 AE2 [设备](../ae2-mechanics/devices.md)和机器的行为，提升它们的速度、增强过滤能力、启用红石控制等等。

## 卡片组件

<Row>
  <ItemImage id="basic_card" scale="2" />

  <ItemImage id="advanced_card" scale="2" />
</Row>

卡片由基础或进阶卡片基底合成而成

<Row>
  <RecipeFor id="basic_card" />

  <RecipeFor id="advanced_card" />
</Row>

## 红石卡

<ItemImage id="redstone_card" scale="2" />

红石卡添加红石控制功能，会在设备的界面中增加一个切换按钮，用于在各种红石条件之间切换。

<RecipeFor id="redstone_card" />

## 容量卡

<ItemImage id="capacity_card" scale="2" />

容量卡可增加输入总线、输出总线、存储总线和生成面板的过滤槽位数量。

<RecipeFor id="capacity_card" />

## 溢出销毁卡

<ItemImage id="void_card" scale="2" />

溢出销毁卡可以在<ItemLink id="cell_workbench" />中应用于[存储元件](storage_cells.md)，当元件已满时会删除存入的物品。（务必给你的元件设置[分区](cell_workbench.md)！）与均分卡配合使用时，只要该特定物品在元件中所占的区域已满，物品就会被销毁，即使其他物品的区域仍有空余。

<RecipeFor id="void_card" />

## 模糊卡

<ItemImage id="fuzzy_card" scale="2" />

模糊卡让带有过滤功能的设备和工具能够按损坏程度过滤和/或忽略物品 NBT，例如导出所有铁斧而不论其损坏程度和附魔如何，或只导出受损的钻石剑而不导出完好修复的。

下面展示了模糊损坏比较模式的工作原理示例：左侧为总线配置，顶部为参与比较的物品。

| 25%                    | 10% 损坏的镐        | 30% 损坏的镐        | 80% 损坏的镐        | 满耐久镐             |
| ---------------------- | ------------------- | ------------------- | ------------------- | ------------------- |
| 几乎完全损坏的镐        | ✅                   | \*\*\*\*            | \*\*\*\*            | \*\*\*\*            |
| 完全修复的镐            | \*\*\*\*            | ✅                   | ✅                   | ✅                   |

| 50%                    | 10% 损坏的镐        | 30% 损坏的镐        | 80% 损坏的镐        | 满耐久镐             |
| ---------------------- | ------------------- | ------------------- | ------------------- | ------------------- |
| 几乎完全损坏的镐        | ✅                   | ✅                   | \*\*\*\*            | \*\*\*\*            |
| 完全修复的镐            | \*\*\*\*            | \*\*\*\*            | ✅                   | ✅                   |

| 75%                    | 10% 损坏的镐        | 30% 损坏的镐        | 80% 损坏的镐        | 满耐久镐             |
| ---------------------- | ------------------- | ------------------- | ------------------- | ------------------- |
| 几乎完全损坏的镐        | ✅                   | ✅                   | \*\*\*\*            | \*\*\*\*            |
| 完全修复的镐            | \*\*\*\*            |                     | ✅                   | ✅                   |

| 99%                    | 10% 损坏的镐        | 30% 损坏的镐        | 80% 损坏的镐        | 满耐久镐             |
| ---------------------- | ------------------- | ------------------- | ------------------- | ------------------- |
| 几乎完全损坏的镐        | ✅                   | ✅                   | ✅                   | \*\*\*\*            |
| 完全修复的镐            | \*\*\*\*            | \*\*\*\*            | \*\*\*\*            | ✅                   |

| 忽略                   | 10% 损坏的镐        | 30% 损坏的镐        | 80% 损坏的镐        | 满耐久镐             |
| ---------------------- | ------------------- | ------------------- | ------------------- | ------------------- |
| 几乎完全损坏的镐        | ✅                   | ✅                   | ✅                   | **✅**               |
| 完全修复的镐            | **✅**               | **✅**               | **✅**               | ✅                   |

<RecipeFor id="fuzzy_card" />

## 加速卡

<ItemImage id="speed_card" scale="2" />

加速卡能让东西运行得更快：使输入总线和输出总线每次操作移动更多物品，并让压印器和分子装配室工作得更快。

<RecipeFor id="speed_card" />

## 反相卡

<ItemImage id="inverter_card" scale="2" />

反相卡将设备和工具中的过滤器从白名单切换为黑名单。

<RecipeFor id="inverter_card" />

## 合成卡

<ItemImage id="crafting_card" scale="2" />

合成卡让设备能够向你的[自动合成](../ae2-mechanics/autocrafting.md)
系统发送合成请求，以获取它想要的物品。

<RecipeFor id="crafting_card" />

## 均分卡

<ItemImage id="equal_distribution_card" scale="2" />

均分卡可以在<ItemLink id="cell_workbench" />中应用于[存储元件](storage_cells.md)，并根据该卡的[分区](cell_workbench.md)设定将元件划分为大小相等的区域。这可以防止单一物品类型占满整个元件。

<RecipeFor id="equal_distribution_card" />

## 能量卡

<ItemImage id="energy_card" scale="2" />

能量卡可为便携终端等某些工具增加更多能量存储，并使<ItemLink id="vibration_chamber" />（振动腔）更加高效。

<RecipeFor id="energy_card" />
