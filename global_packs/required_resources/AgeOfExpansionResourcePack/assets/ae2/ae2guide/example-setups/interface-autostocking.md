---
navigation:
  parent: example-setups/example-setups-index.md
  title: 接口自动补货
  icon: interface
---

# 接口自动补货

有人可能会问：“如何让各种物品保持一定的库存量，并在需要时自动合成更多？”

一种方案是使用<ItemLink id="interface" />和<ItemLink id="crafting_card" />，自动向网络的[自动合成](../ae2-mechanics/autocrafting.md)
请求新物品。这种装置更适合维持种类繁多但每种数量不多的物品库存。

这个演示装置为了不至于太宽而做了缩减，最理想的配置通常是使用 4 个<ItemLink id="interface" />和 4 个<ItemLink id="storage_bus" />，
以用满一根普通[线缆](../items-blocks-machines/cables.md)上的全部 8 个[频道](../ae2-mechanics/channels.md)。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/interface_autostocking.snbt" />

<BoxAnnotation color="#dddddd" min="0 0 0" max="2 1 1">
        (1) 接口：设置为在自身内保有目标物品。它们都带有合成卡。
        <ItemImage id="crafting_card" scale="2" />
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 1 0" max="2 1.3 1">
        (2) 存储总线：“输入/输出模式”设置为“仅提取”。
  </BoxAnnotation>

<DiamondAnnotation pos="4 0.5 0.5" color="#00ff00">
        连接至主网络
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 配置

* 各个<ItemLink id="interface" /> (1) 设置为在自身内保有目标物品，做法是把目标物品点击放入
   它们的顶部槽位，或从 JEI 拖入顶部槽位，然后点击槽位上方的扳手图标来设定数量。它们都带有<ItemLink id="crafting_card" />。
* 各个<ItemLink id="storage_bus" /> (2) 设置为“输入/输出模式”为“仅提取”。

## 工作原理

1. 如果某个<ItemLink id="interface" />无法从[网络存储](../ae2-mechanics/import-export-storage.md)中取回足量的已配置物品，
   （且它带有一张<ItemLink id="crafting_card" />），它就会请求网络的[自动合成](../ae2-mechanics/autocrafting.md)合成更多该物品。
2. 各个<ItemLink id="storage_bus" /> 让网络能够访问这些接口的内容。
