---
navigation:
  parent: example-setups/example-setups-index.md
  title: 递归合成
  icon: minecraft:netherite_upgrade_smithing_template
---

# 递归合成装置

如[自动合成](../ae2-mechanics/autocrafting.md)中所述，自动合成的规划算法无法处理主要输出是输入之一的配方。
例如，它无法处理复制<ItemLink id="minecraft:netherite_upgrade_smithing_template" />的情况。

一种解决方案是利用<ItemLink id="level_emitter" />伪装成[样板](../items-blocks-machines/patterns.md)的能力。

它随后会用于开启一个持续执行该合成的小型装置。在本例中，我们来看一个复制<ItemLink id="minecraft:netherite_upgrade_smithing_template" />的装置。

<RecipeFor id="minecraft:netherite_upgrade_smithing_template" />

***

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/recursive_recipe_setup.snbt" />

  <BoxAnnotation color="#dddddd" min="1 0 0" max="2 1 1">
        (1) 接口：设置为储备所需的额外材料：钻石和下界岩。
        <Row><ItemImage id="minecraft:diamond" scale="2" /> <ItemImage id="minecraft:netherrack" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.3 1 0.3" max="2.7 1.3 0.7">
        (2) 电平发射器：配置为“下界合金锻造模板”，并设置为“发出红石信号以合成物品”。
        <Row><ItemImage id="minecraft:netherite_upgrade_smithing_template" scale="2" /> <ItemImage id="crafting_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2 0 0" max="2.3 1 1">
        (3) 输入总线 #1：过滤为接口正在储备的物品。带有一张红石卡。红石模式设置为
        “有信号时激活”。
        <Row>
        <ItemImage id="minecraft:diamond" scale="2" />
        <ItemImage id="minecraft:netherrack" scale="2" />
        <ItemImage id="redstone_card" scale="2" />
        </Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="3 1 1" max="4 1.3 2">
        (4) 存储总线 #1：优先级设置得比另一个存储总线更高。非常重要。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="3 0 1" max="4 1 2">
        (5) 分子装配室：里面装有用于复制锻造模板的样板。

        ![Pattern](../assets/diagrams/smithing_template_pattern_small.png)

        首次搭建时，还需要手动向其中预先放入一个锻造模板。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 0 1" max="3 1 2">
        (6) 输入总线 #2：处于默认配置。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 0 1" max="2 1 1.3">
        (7) 存储总线 #2：过滤为“下界合金锻造模板”。优先级设置得比另一个存储总线更低。
        <ItemImage id="minecraft:netherite_upgrade_smithing_template" scale="2" />
  </BoxAnnotation>

<DiamondAnnotation pos="0 0.5 0.5" color="#00ff00">
        连接至主网络
    </DiamondAnnotation>

  <IsometricCamera yaw="15" pitch="30" />
</GameScene>

## 配置

* <ItemLink id="interface" /> (1) 设置为储备所需的额外材料：钻石和下界岩。
* <ItemLink id="level_emitter" /> (2) 配置为“下界合金锻造模板”，并设置为“发出红石信号以合成物品”。
* 第一个<ItemLink id="import_bus" /> (3) 过滤为接口正在储备的物品。它带有一张红石卡。红石模式设置为“有信号时激活”。
* 第一个<ItemLink id="storage_bus" /> (4) 的[优先级](../ae2-mechanics/import-export-storage.md#storage-priority)设置得比第二个存储总线更*高*。
* <ItemLink id="molecular_assembler" /> (5) 装有用于复制锻造模板的样板，并且已手动放入一个锻造模板。

  ![Pattern](../assets/diagrams/smithing_template_pattern.png)

* 第二个<ItemLink id="import_bus" /> (6) 处于默认配置。
* 第二个<ItemLink id="storage_bus" /> (7) 过滤为“下界合金锻造模板”。它的[优先级](../ae2-mechanics/import-export-storage.md#storage-priority)比第一个存储总线更*低*。

## 工作原理

1. 由于插入了<ItemLink id="crafting_card" />并被设置为“发出红石信号以合成物品”，<ItemLink id="level_emitter" />会伪装成一个[样板](../items-blocks-machines/patterns.md)。因此，“下界合金锻造模板”会作为可被[自动合成](../ae2-mechanics/autocrafting.md)的有效条目出现在[终端](../items-blocks-machines/terminals.md)中。
2. 当收到来自玩家或系统本身的合成该物品的请求时，电平发射器就会开启。
3. 第一个<ItemLink id="import_bus" />被电平发射器激活，从<ItemLink id="interface" />中拉取储备的材料。
4. 网络上唯一能够存储这些材料的<ItemLink id="storage_bus" />就是分子装配室上的那一个。
5. <ItemLink id="molecular_assembler" />接收到材料（其内已放有 1 个锻造模板），执行合成，产出 2 个锻造模板。
6. 第二个<ItemLink id="import_bus" />提取出 1 个锻造模板。
7. 第一个存储总线拥有更高的优先级，因此该锻造模板回到分子装配室中。
8. 第二个<ItemLink id="import_bus" />提取出 1 个锻造模板。
9. 分子装配室无法再接收另一个锻造样板，于是第二个锻造样板流向优先级较低的存储总线，从而被插入接口中。
10. <ItemLink id="interface" />未被设置为储备锻造模板，因此将它存入网络。
