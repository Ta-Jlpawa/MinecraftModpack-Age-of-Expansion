---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 生成面板
  icon: formation_plane
  position: 210
categories:
- devices
item_ids:
- ae2:formation_plane
---

# 生成面板

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../assets/blocks/formation_plane.snbt" />
</GameScene>

生成面板用于放置方块或掉落物品。它的工作方式类似于只允许插入的<ItemLink id="storage_bus" />，当[设备](../ae2-mechanics/devices.md)向[网络存储](../ae2-mechanics/import-export-storage.md)插入物品时，
它会放置/掉落这些物品，例如<ItemLink id="import_bus" />和<ItemLink id="interface" />。

<GameScene zoom="8" interactive={true}>
  <ImportStructure src="../assets/assemblies/formation_plane_demonstration.snbt" />
  <IsometricCamera yaw="255" pitch="30" />
</GameScene>

注意，它们与[管道子网络](../example-setups/pipe-subnet.md)中的输入总线 -> 存储总线和接口 -> 存储总线管道组合类似。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/import_storage_pipe.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/interface_storage_pipe.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

这种[设备](../ae2-mechanics/devices.md)利用了存储总线在[管道子网络](../example-setups/pipe-subnet.md)等场景中使用的机制。
如果你想要掉落物品/放置方块而不是运输物品，可以在那些配置中用生成面板替换存储总线。

它们是[线缆子部件](../ae2-mechanics/cable-subparts.md)。

**请记住在你的区块领地声明中启用假玩家**

## 过滤

默认情况下，生成面板会放置/掉落任何物品。放入过滤槽的物品将作为白名单，仅
允许放置这些特定物品。

即使你实际上没有某种物品，也可以从JEI/REI将物品和流体拖入槽位。

右键点击时手持流体容器（如桶或流体储罐），可将该流体设为过滤器，而不是桶或储罐物品本身。

## 优先级

可以点击GUI右上角的扳手图标设置优先级。
进入网络的物品会优先存入优先级最高的存储。

## 设置

*   可将生成面板设置为在世界中放置方块或掉落物品

## 升级

生成面板支持以下[升级卡](upgrade_cards.md)：

*   <ItemLink id="capacity_card" /> 增加过滤槽的数量
*   <ItemLink id="fuzzy_card" /> 让生成面板按损坏程度过滤和/或忽略物品NBT
*   <ItemLink id="inverter_card" /> 将过滤器从白名单切换为黑名单

## 配方

<RecipeFor id="formation_plane" />
