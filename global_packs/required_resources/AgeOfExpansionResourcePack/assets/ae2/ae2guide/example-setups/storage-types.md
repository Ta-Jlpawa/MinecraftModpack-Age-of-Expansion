---
navigation:
  parent: example-setups/example-setups-index.md
  title: 存储类型与网络整洁
  icon: drive
---

# 各种类型的存储与保持你的网络井井有条

利用过滤器、[分区](../items-blocks-machines/cell_workbench.md)和[存储优先级](../ae2-mechanics/import-export-storage.md#storage-priority)，
你可以为各种类型的物品建立多个层级的存储。

存储的类型通常有：
* 通用存储，用于存放那些你拥有几个到几千个不等的杂七杂八的东西。它使用小容量[存储元件](../items-blocks-machines/storage_cells.md)，
比如 4k 或 16k。
* 大宗存储，用于存放那些你有几千个以上的东西，比如圆石或铁。它使用大容量元件，比如 256k
或 MEGA 附加包中的元件。
* 农场处的本地存储，如[专用本地存储](specialized-local-storage.md)以及
[各种](simple-certus-farm.md)[赛特斯](semiauto-certus-farm.md)[农场](advanced-certus-farm.md)所述。

优先级的设置方式是：当物品被倾倒进主网络时，网络首先尝试把它们存入专门的大宗存储或本地存储，
如果做不到（由于过滤器和分区的限制），再把物品放进通用存储。
这意味着物品不会主动从一个存储移动到另一个存储，而是在进出网络的过程中"迁移"。
要主动移动物品，请使用<ItemLink id="io_port" />。

<GameScene zoom="3" interactive={true}>
  <ImportStructure src="../assets/assemblies/network_storage_types.snbt" />

    <BoxAnnotation color="#33dd33" min="11 0 1" max="12 1.3 2" thickness="0.05">
        大宗存储。本例中是大型容量存储（如抽屉）上一个带过滤的存储总线。该存储总线过滤为
        煤炭。它具有高优先级，因此每当煤炭进入网络时，都会送往这个存储总线；而每当煤炭从
        网络中被拉取时，都是从*除这里以外的任何地方*拉取的，所以煤炭会"迁移"到这个抽屉中。

        重要提示：像抽屉这样的大型优化容器用于此目的是没问题的，但有许多槽位的大型非优化容器，比如
        巨型箱子，在与存储总线一起使用时对性能的影响非常糟糕。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="11 0 3" max="12 1 4" thickness="0.05">
        大宗存储。本例中是高优先级驱动器中的一个已分区 256k 存储元件。该元件分区为
        圆石和铁。它装有一张均衡分配卡，因此不会被圆石完全填满，
        给铁留不出空间。驱动器具有高优先级，因此每当圆石或铁进入网络时，都会送往这个存储总线；
        而每当圆石或铁从网络中被拉取时，都是从*除这里以外的任何地方*拉取的，所以圆石和铁会"迁移"到这个元件中。
    </BoxAnnotation>

    <BoxAnnotation color="#33dddd" min="11 0 5" max="12 1 6" thickness="0.05">
        通用存储。本例中是一个装满 16k 存储元件的驱动器。这些元件没有分区。驱动器具有中性优先级
        （本例中为 0），因此每当有东西进入网络时，会先送往专门的大宗存储或本地存储；
        而每当有东西从网络中被拉取时，都会先从这里拉取，因此有专门存储的物品自然会
        从通用存储中"迁移"出去。
    </BoxAnnotation>

    <BoxAnnotation color="#88ff88" min="11 0 8" max="12 1 9" thickness="0.05">
        这个IO端口在保持网络整洁方面发挥着重要作用。由于存储优先级不会*主动*
        移动物品，通用存储中使用的存储元件应定期通过IO端口"轮换"一遍，
        把那些在大宗存储中有位置的物品移过去。这相当于对存储进行"碎片整理"，
        确保同一物品不会分散存放在多处。
    </BoxAnnotation>

    <BoxAnnotation color="#dd3333" min="14 0 11" max="15 1 12" thickness="0.05">
        刷怪农场的本地存储。这个驱动器中的存储元件分区为你想保留的掉落物，比如骨头和箭。
        驱动器本身不设置优先级，因为影响优先级的是从主网络访问子网络的那个存储总线。
        存储元件装有均衡分配卡和溢出销毁卡。
    </BoxAnnotation>

    <BoxAnnotation color="#dd3333" min="14 1 10" max="15 2.3 11" thickness="0.05">
        刷怪农场的本地存储。这套存储总线-接口组合让主网络能够访问该子网络的存储。
        存储总线被赋予高优先级，并过滤为子网络存储元件中所存的物品。

        重要提示：由于子网络上设有垃圾桶装置，务必给这个存储总线设置过滤，否则它会开始销毁
        进入网络的*每一个物品、流体等*！
    </BoxAnnotation>

    <BoxAnnotation color="#dd3333" min="14 0 9" max="15 1.3 10" thickness="0.05">
        刷怪农场的本地存储。物质冷凝器上的这个存储总线被设置为比驱动器更低的优先级。这意味着
        无法进入驱动器存储元件的刷怪掉落物会溢出到这里并被处理掉。这一点很重要，
        可以防止子网络被快断掉的弓之类的垃圾杂物堵满。
    </BoxAnnotation>

    <BoxAnnotation color="#dd33dd" min="8 1 11.7" max="9 2.3 13" thickness="0.05">
        西瓜农场的本地存储。这套装置采用了与各种赛特斯农场示例类似的方法。子网络上的一个存储总线
        把所种植的产物插入桶中。主网络上的另一个存储总线（过滤为西瓜片并具有高优先级）让主网络
        能够访问所种植的产物。
    </BoxAnnotation>

  <IsometricCamera yaw="270" pitch="30" />
</GameScene>
