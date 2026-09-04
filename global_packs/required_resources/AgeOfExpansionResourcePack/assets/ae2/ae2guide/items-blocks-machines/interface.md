---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 接口
  icon: interface
  position: 210
categories:
- devices
item_ids:
- ae2:interface
- ae2:cable_interface
---

# 接口

<Row gap="20">
<BlockImage id="interface" scale="8" />
<GameScene zoom="8" background="transparent">
  <ImportStructure src="../assets/blocks/cable_interface.snbt" />
</GameScene>
</Row>

接口就像一个小箱子加流体储罐，会根据你在槽位中设置的库存需求，从[网络存储](../ae2-mechanics/import-export-storage.md)
填充自身或向其清空内容。它会在单个游戏刻内尽力完成，因此每个游戏刻最多可以
填充或清空9组物品，如果你有高速物品管道的话，这是一种非常快速的输入/输出方式。

另一个有用的特性是：大多数流体储罐只能存储1种流体，而接口最多可存储9种流体，还能存物品。
它们本质上就是带有额外功能的箱子/多流体储罐，只要让它们
不接入任何网络，就可以禁用那些额外功能。
因此，在你想存储少量多种不同东西的某些特定场景下，它们非常有用。

## 接口的内部工作原理

如前所述，接口本质上就是一个箱子/储罐，附带几个超级强化版<ItemLink id="import_bus" />和
<ItemLink id="export_bus" />，以及一堆<ItemLink id="level_emitter" />。

<GameScene zoom="3" interactive={true}>
  <ImportStructure src="../assets/assemblies/interface_internals.snbt" />

  <BoxAnnotation color="#dddddd" min="1.3 0.3 1.3" max="9.7 1 1.7">
        一堆电平发射器，用于控制请求的库存数量
        <GameScene zoom="4" background="transparent">
        <ImportStructure src="../assets/blocks/level_emitter.snbt" />
        </GameScene>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1.3 4 1.3" max="9.7 4.7 1.7">
        一堆电平发射器，用于控制请求的库存数量
        <GameScene zoom="4" background="transparent">
        <ImportStructure src="../assets/blocks/level_emitter.snbt" />
        </GameScene>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1.3 1.3 1.3" max="9.7 2 1.7">
        一堆超级强化的输入总线，每个游戏刻可传输1组物品
        <GameScene zoom="4" background="transparent">
        <ImportStructure src="../assets/blocks/import_bus.snbt" />
        </GameScene>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1.3 3 1.3" max="9.7 3.7 1.7">
        一堆超级强化的输出总线，每个游戏刻可传输1组物品
        <GameScene zoom="4" background="transparent">
        <ImportStructure src="../assets/blocks/export_bus.snbt" />
        </GameScene>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 2 1" max="10 3 2">
        9个独立的内部槽位
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="15" />
</GameScene>

## 特殊交互

接口与其他AE2[设备](../ae2-mechanics/devices.md)还有一些特殊功能：

放在未配置接口上的<ItemLink id="storage_bus" />会将其所在网络的整个[网络存储](../ae2-mechanics/import-export-storage.md)
呈现给该存储总线所在的网络，就好像存储总线放置在一个由接口网络构成的巨型箱子上一样。
在接口的过滤槽中设置要备货的物品即可禁用此行为。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/interface_storage.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

样板供应器与[子网络](../ae2-mechanics/subnetworks.md)上的接口有特殊交互：如果接口未配置，
供应器会完全跳过该接口，直接推送到该子网络的[存储](../ae2-mechanics/import-export-storage.md)，
跳过接口且不往里面塞配方批次；更重要的是，在存储腾出空间之前不会插入下一批。

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/provider_interface_storage.snbt" />

<BoxAnnotation color="#dddddd" min="2.7 0 1" max="3 1 2">
        接口（必须是平板型，不是整块方块）
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 4">
        各存储总线
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 0 0" max="1 1 4">
        你想供应样板的位置（多台机器，或1台机器的多个面）
  </BoxAnnotation>

<IsometricCamera yaw="185" pitch="30" />
</GameScene>

## 变体

接口有2种不同变体：普通和平板/[子部件](../ae2-mechanics/cable-subparts.md)。这影响从哪些面可以访问它们的物品栏，
以及它们向哪些面提供网络连接。

*   普通接口允许从所有面推入、拉出和访问其物品栏，并且像大多数AE2机器一样，
    表现得像一根线缆，向所有面提供网络连接。

*   平板接口是[线缆子部件](../ae2-mechanics/cable-subparts.md)，因此可以在同一根线缆上放置多个，实现紧凑布局。
    它们允许从其所在面推入、拉出和访问其物品栏，但不在该面提供网络连接。

接口可以在合成网格中于普通形态和平板形态之间互换。

## 设置

接口上部的槽位决定接口要在自身内部备货的物品。将物品放入其中或从JEI/REI拖入时，
会出现一个扳手图标，让你设置数量。

右键点击时手持流体容器（如桶或流体储罐），可将该流体设为过滤器，而不是桶或储罐物品本身。

当你把某个槽位设为备货模式后，外部机器也无法再向该槽位插入其他任何东西。

## 升级

接口支持以下[升级卡](upgrade_cards.md)：

*   <ItemLink id="fuzzy_card" /> 让总线按损坏程度过滤和/或忽略物品NBT
*   <ItemLink id="crafting_card" /> 让接口向你[自动合成](../ae2-mechanics/autocrafting.md)
    系统发送合成请求以获取所需物品。它会先尽可能从存储中拉取物品，
    然后才发起新的合成请求。

## 优先级

可以点击GUI右上角的扳手图标设置优先级。优先级更高的接口会先于优先级更低的接口获得物品。

## 配方

<Recipe id="network/blocks/interfaces_interface" />

<RecipeFor id="cable_interface" />
