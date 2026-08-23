---
navigation:
  parent: example-setups/example-setups-index.md
  title: 电平发射器自动补货
  icon: level_emitter
---

# 电平发射器自动补货

有人可能会问：“如何让某种物品保持一定的库存量，并在需要时自动合成更多？”

一种方案是使用<ItemLink id="export_bus" />、<ItemLink id="level_emitter" />和<ItemLink id="crafting_card" />，
自动向网络的[自动合成](../ae2-mechanics/autocrafting.md)请求新物品。这种装置适用于维持某一种物品的大量库存。

当然，你也可以省去电平发射器和红石卡，让网络不停地持续合成。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/level_emitter_autostocking.snbt" />

  <BoxAnnotation color="#dddddd" min="1 1 0" max="2 1.3 1">
        (1) 输出总线：过滤为目标物品。带有一张红石卡和一张合成卡。红石模式设置为
        “有信号时激活”，合成行为设置为“不使用已存物品”。
        <Row><ItemImage id="redstone_card" scale="2" /> <ItemImage id="crafting_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="0.7 1 0" max="1 2 1">
        (2) 电平发射器：配置为目标物品和数量，并设置为“当数量低于限制时发出信号”。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 0 0" max="2 1 1">
        (3) 接口：保持默认配置。
  </BoxAnnotation>

<DiamondAnnotation pos="4 0.5 0.5" color="#00ff00">
        连接至主网络
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 配置

* <ItemLink id="export_bus" /> (1) 过滤为目标物品。它带有一张<ItemLink id="redstone_card" />和一张<ItemLink id="crafting_card" />。
  “红石模式”设置为“有信号时激活”，“合成行为”设置为“不使用已存物品”。
* <ItemLink id="level_emitter" /> (2) 配置为目标物品和数量，并设置为“当数量低于限制时发出信号”。
* <ItemLink id="interface" /> (3) 保持默认配置。

## 工作原理

1. 如果[网络存储](../ae2-mechanics/import-export-storage.md)中目标物品的数量低于
   <ItemLink id="level_emitter" />中设定的数量，它就会发出红石信号。
2. 在收到红石信号时（由于带有<ItemLink id="crafting_card" />且被设置为不使用已存物品），
   <ItemLink id="export_bus" />会请求网络的[自动合成](../ae2-mechanics/autocrafting.md)合成
   更多的目标物品，然后将其输出。
3. 在有物品被推入时（且它未被配置为在自身内保有任何物品），<ItemLink id="interface" />会把该物品推进网络存储。
