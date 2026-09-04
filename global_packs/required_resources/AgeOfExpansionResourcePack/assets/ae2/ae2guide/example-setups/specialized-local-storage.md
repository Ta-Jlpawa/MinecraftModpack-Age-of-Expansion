---
navigation:
  parent: example-setups/example-setups-index.md
  title: 专用本地存储
  icon: drive
---

# 专用本地存储

利用[接口的一种特殊行为](../items-blocks-machines/interface.md#special-interactions)，
一个[子网络](../ae2-mechanics/subnetworks.md)可以向主网络展示其存储的内容，却看不到主网络的存储，
并且只占用 1 条[频道](../ae2-mechanics/channels.md)。

这对于农场等处的本地存储很有用，这样物品就不会溢出到你的主存储中。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/local_storage.snbt" />

<BoxAnnotation color="#dddddd" min="4 0 0" max="5 2 1">
        (1) 某种导入物品的方式（本例中是一个接口）
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3 0 0" max="4 1 1">
        (2) 驱动器：里面放有一些存储元件。存储元件应按农场产出的物品进行过滤。
        存储元件可以装有均衡分配卡和溢出销毁卡。
        <Row><ItemImage id="item_storage_cell_4k" scale="2" /> <ItemImage id="equal_distribution_card" scale="2" /> <ItemImage id="void_card" scale="2" /></Row>
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3 1 0" max="4 2 0.3">
        (3) 合成终端：可以看到子网络上驱动器中的内容，但看不到主网络存储的内容。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2 0 0" max="2.3 1 1">
        (4) 接口 #2：处于默认配置。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1.7 0 0" max="2 1 1">
        (5) 存储总线：优先级设置得高于主存储，可按农场产出的物品进行过滤。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="2 2 0.3">
        合成终端：既能看到主网络存储的内容，*也*能看到子网络的内容。
  </BoxAnnotation>

<DiamondAnnotation pos="0 0.5 0.5" color="#00ff00">
        连接至主网络
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 配置

* 第一个<ItemLink id="interface" />（1）只是接收你的农场产出的物品，并把它们推入子网络。
* <ItemLink id="drive" />（2）中放有一些[存储元件](../items-blocks-machines/storage_cells.md)。存储元件应
  针对农场产出的物品进行[分区](../items-blocks-machines/cell_workbench.md)。
  存储元件可以装有<ItemLink id="equal_distribution_card" />和<ItemLink id="void_card" />。
* 第二个<ItemLink id="interface" />（4）处于默认配置。
* <ItemLink id="storage_bus" />的[优先级](../ae2-mechanics/import-export-storage.md#storage-priority)设置得
  高于主存储。它可以按农场产出的物品进行过滤。

## 工作原理

* 子网络上的<ItemLink id="interface" />向主网络上的<ItemLink id="storage_bus" />展示<ItemLink id="drive" />的内容。
这意味着存储总线可以直接从驱动器的存储元件中拉取和推送物品。
* 存储总线被设置为高[优先级](../ae2-mechanics/import-export-storage.md#storage-priority)，以便物品优先
  放回子网络而不是你的主存储。
* 重要的是，如果子网络中的存储元件满了，物品不会溢出到主网络。如果你的农场属于那种堵塞就会出问题的类型，
  可以改用<ItemLink id="void_card" />来删除多余的物品。 
* 如果农场产出多种物品，<ItemLink id="equal_distribution_card" />可以防止某一种物品填满所有存储元件，
而导致其他物品无法存入。
