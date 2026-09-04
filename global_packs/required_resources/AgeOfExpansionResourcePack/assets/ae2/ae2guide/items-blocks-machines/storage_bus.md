---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: ME存储总线
  icon: storage_bus
  position: 220
categories:
- devices
item_ids:
- ae2:storage_bus
---

# 存储总线

<GameScene zoom="8" background="transparent">
<ImportStructure src="../assets/blocks/storage_bus.snbt" />
</GameScene>

是否曾想过*继续留着*你的箱子怪物，而不愿用更理智的东西取代它？为你介绍存储总线！

存储总线将其所接触的容器变为[网络存储](../ae2-mechanics/import-export-storage.md)。
它的工作方式是让网络能够看到该容器的内容物，并通过向该容器推送和拉取物品，
来满足向网络存储推送和拉取物品的[设备](../ae2-mechanics/devices.md)。

基于 AE2 通过各[设备](../ae2-mechanics/devices.md)功能相互作用产生涌现机制的设计理念，你并不一定非要把存储总线用于*存储*。通过[子网络](../ae2-mechanics/subnetworks.md)
让一根（或几根）存储总线成为网络上的*唯一*存储，你就可以把它当作物品传输的来源或目的地。（参见["管道子网络"](../example-setups/pipe-subnet.md)）

重要提示：抽屉这类经过优化的大型容器没有问题，但巨型储箱这类槽位众多、未经优化的大型容器在与存储总线搭配使用时性能极差。

它们是[线缆子部件](../ae2-mechanics/cable-subparts.md)。

## 过滤

默认情况下，总线会存储一切。放入其过滤槽位的物品将作为白名单，只允许这些特定物品被存储。

即使你实际上没有任何该物品，也可以从 JEI/REI 将物品和流体拖入槽位。

用流体容器（如桶或流体储罐）右键点击，即可将该流体本身而非桶或储罐物品设为过滤器。

## 优先级

点击界面右上角的扳手即可设置优先级。
进入网络的物品会以最高优先级的存储作为首选目的地。当两个存储优先级相同时，
如果其中一个已含有该物品，则会优先选择该存储。在同一优先级组中，
任何设有过滤的存储都会被视为已含有该物品。从存储中移除物品时，
会从优先级最低的存储中移除。这一优先级系统意味着，随着物品不断存入和取出网络存储，
高优先级存储会被填满，低优先级存储则会被清空。

## 设置

*   总线可以根据相邻容器当前的内容进行分区（过滤）
*   可禁止或允许网络看到相邻容器中总线无法抽取的物品
    （例如，存储总线无法从 <ItemLink id="inscriber" /> 的中间输入槽位抽取物品）
*   总线可以在存入和取出时都进行过滤，或仅在存入时过滤
*   总线可以是双向、仅存入或仅取出

## 升级

存储总线支持以下[升级](upgrade_cards.md)：

*   <ItemLink id="capacity_card" /> 增加过滤槽位的数量
*   <ItemLink id="fuzzy_card" /> 让总线按损坏程度过滤和/或忽略物品 NBT
*   <ItemLink id="inverter_card" /> 将过滤器从白名单切换为黑名单
*   <ItemLink id="void_card" /> 在所连接容器已满时销毁存入的物品，有助于防止农场积压。请务必小心设置分区！

## 配方

<RecipeFor id="storage_bus" />
