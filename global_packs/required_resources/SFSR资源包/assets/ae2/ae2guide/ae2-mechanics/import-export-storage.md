---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: 输入、输出与存储
---

# 输入、输出与存储

**你的ME系统与世界**

AE2中一个重要的概念是网络存储。它是网络内容物存放的地方，
通常是[存储元件](../items-blocks-machines/storage_cells.md)或<ItemLink id="storage_bus" />
所连接的任意容器。大多数AE2[设备](../ae2-mechanics/devices.md)都会以某种方式与之交互。

例如：

*   <ItemLink id="import_bus" />把东西推进网络存储
*   <ItemLink id="export_bus" />从网络存储拉出东西
*   <ItemLink id="interface" />既从网络存储取出也向其存入
*   [终端](../items-blocks-machines/terminals.md)在你放入或取出物品、或为合成槽补充材料时，既向网络存储存入也从其中取出
*   <ItemLink id="storage_bus" />并不真正向存储存取东西，它们是对所连接的容器进行存取，
    以便将该容器用作网络存储（所以实际上是其他设备在对*它们*进行存取）

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/import_export_storage.snbt" />

  <BoxAnnotation color="#dddddd" min="8 1 1" max="9 1.3 2">
        输入总线把它们所指容器中的物品导入网络存储
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="8 2 1" max="9 3 1.3">
        从你的背包把物品放进终端，等同于网络导入了该物品
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="7 0 1" max="8 1 2">
        若某个槽位未配置库存物品、或槽位中物品数量超出配置的库存量，接口就会从其内部容器导入，
        因此可以把物品推入接口以插入网络
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="6 0 1" max="7 1 2">
        样板供应器会从其内部回收槽容器导入，因此可以把物品推入它以插入网络
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 1 1" max="5 2 2">
        驱动器将插入其中的元件作为网络存储提供
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="3 1 1" max="4 1.3 2">
        存储总线把它们所指的容器用作网络存储
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 1 1" max="2 1.3 2">
        输出总线把网络存储中的东西导出到它们所指的容器
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 2 1" max="2 3 1.3">
        从终端里取出东西，等同于网络导出了该物品
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="0 1 1" max="1 2 2">
        若某个槽位配置了要库存的东西，接口就会导出到其内部容器，
        因此可以从接口取出东西以从网络提取
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

在设计自动化和物流系统时，请务必牢记向网络存储存入和取出的这些动作/事件。

## 存储优先级

优先级可以通过点击部分GUI右上角的扳手图标设置。
进入网络的物品会以最高优先级的存储作为第一目的地；若两个存储优先级相同，
且其中一个已含有该物品，则会优先选择该存储。在同一优先级组内与其他存储并列时，
设置了白名单的元件会被视为已包含白名单内的物品。从网络取出的物品
则来自优先级最低的存储。这套优先级系统意味着：随着物品不断存入和移出网络存储，
高优先级存储会被填满，而低优先级存储会被腾空。
