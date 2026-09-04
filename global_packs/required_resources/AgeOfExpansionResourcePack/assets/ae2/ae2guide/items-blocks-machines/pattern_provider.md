---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 样板供应器
  icon: pattern_provider
  position: 210
categories:
- devices
item_ids:
- ae2:pattern_provider
- ae2:cable_pattern_provider
---

# 样板供应器

<Row gap="20">
<BlockImage id="pattern_provider" scale="8" />
<BlockImage id="pattern_provider" p:push_direction="up" scale="8" />
<GameScene zoom="8" background="transparent">
  <ImportStructure src="../assets/blocks/cable_pattern_provider.snbt" />
</GameScene>
</Row>

样板供应器是你的[自动合成](../ae2-mechanics/autocrafting.md)系统与外界交互的主要方式。它们把
[样板](patterns.md)中的原料推入相邻容器，物品也可以插入其中从而进入网络。通常，
把机器的输出用管道送回附近的样板供应器（往往是推送原料的那一个），而不是用<ItemLink id="import_bus" />将机器输出拉回网络，可以节省一个频道。

值得注意的是，由于它们直接从合成CPU的[合成存储](crafting_cpu_multiblock.md#crafting-storage)中推送原料，
原料实际上从不会存放在其物品栏中，因此你无法从中用管道取出。你必须让供应器把
原料推到另一个容器（比如木桶）里，再从那里用管道传输。

同样值得注意的是，供应器必须一次性推送全部原料，无法只推半批。这一点可以被利用。

样板供应器与[子网络](../ae2-mechanics/subnetworks.md)上的接口有特殊交互：如果接口未被修改（请求槽为空），
供应器会完全跳过该接口，直接推送到该子网络的[存储](../ae2-mechanics/import-export-storage.md)，
跳过接口且不往里面塞配方批次；更重要的是，在机器腾出空间之前不会插入下一批。
这在阻挡模式下也能正确工作，供应器会监控机器内的槽位而非接口的槽位来检查原料。

例如，这个配置会把待熔炼物和燃料直接推入熔炉的对应槽位。
你可以借此向一台机器的多个面或多个机器提供样板供应。

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/furnace_automation.snbt" />

<BoxAnnotation color="#dddddd" min="1 0 0" max="2 1 1">
        (1) 样板供应器：定向变体，通过赛特斯石英扳手调整方向，并放入相关处理样板。

        ![Iron Pattern](../assets/diagrams/furnace_pattern_small.png)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="2 1.3 1">
        (2) 接口：保持默认配置。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="1.3 2 1">
        (3) 存储总线#1：过滤为煤炭。
        <ItemImage id="minecraft:coal" scale="2" />
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 2 0" max="1 2.3 1">
        (4) 存储总线#2：使用反相卡过滤为黑名单煤炭。
        <Row><ItemImage id="minecraft:coal" scale="2" /><ItemImage id="inverter_card" scale="2" /></Row>
  </BoxAnnotation>

<DiamondAnnotation pos="4 0.5 0.5" color="#00ff00">
        通往主网络
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

这是向多台机器供应的一般示意

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/provider_interface_storage.snbt" />

<BoxAnnotation color="#dddddd" min="2.7 0 1" max="3 1 2">
        接口（必须是平板型，不是整块方块）
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 4">
        各存储总线
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 0 0" max="1 1 4">
        你想供应样板的位置
  </BoxAnnotation>

<IsometricCamera yaw="185" pitch="30" />
</GameScene>

支持放置多个带有相同样板的样板供应器并行工作。

样板供应器会尝试将其批次轮流分配到所有面上，从而使所有连接的机器并行工作。

## 变体

样板供应器有3种不同变体：普通、定向和平板/[子部件](../ae2-mechanics/cable-subparts.md)。这影响它们向哪些特定面推送
原料、从哪些面接收物品，以及向哪些面提供网络连接。

* 普通样板供应器向所有面推送原料、从所有面接收输入，并且像大多数AE2机器一样，表现得像一根线缆，
    向所有面提供[网络连接](../ae2-mechanics/me-network-connections.md)。

* 定向样板供应器是通过在普通样板供应器上使用<ItemLink id="certus_quartz_wrench" />改变其
    方向得到的。它们只向选定面推送原料，从所有面接收输入，并且特别地不在选定面上提供
  [网络连接](../ae2-mechanics/me-network-connections.md)。这使它们可以在不连接网络的情况下向AE2机器推送内容，适合搭建子网络。

* 平板样板供应器是[线缆子部件](../ae2-mechanics/cable-subparts.md)，因此可以在同一根线缆上放置多个，实现紧凑布局。
    它们的行为类似于定向样板供应器的选定面，提供样板、接收输入，且在其所在面**不**
    提供[网络连接](../ae2-mechanics/me-network-connections.md)。

样板供应器可以在合成网格中于普通形态和平板形态之间互换。

## 设置

样板供应器有多种模式：

*   **阻挡模式**在机器内已有原料时，阻止供应器推送新的一批原料。
*   **锁定合成**可在各种红石条件下锁定供应器，或直到上一次合成的产物
    被插回那个特定的样板供应器为止。
*   可以设置在<ItemLink id="pattern_access_terminal" />上显示或隐藏该供应器。

## 优先级

可以点击GUI右上角的扳手图标设置优先级。当同一物品存在多个[样板](patterns.md)时，
高优先级供应器中的样板会优先于低优先级供应器中的样板被使用，
除非网络没有高优先级样板所需的原料。

## 一个常见误解

不知为何人们总是这样做，我不明白为什么，但我把它写在这里希望能有所帮助。（也许
人们误以为<ItemLink id="export_bus" />是东西离开网络的唯一途径，而不知道
样板供应器也会导出东西）

下面这样是达不到你想要的效果的。如[线缆](cables.md)一文所述，线缆不是物品管道，它们没有内部
物品栏，供应器不会向其推送。

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../assets/assemblies/provider_misconception_1.snbt" />

  <BoxAnnotation color="#dddddd" min="1 0 3" max="2 1 4">
        不是高炉
  </BoxAnnotation>

  <IsometricCamera yaw="95" pitch="5" />
</GameScene>

由于供应器没有任何可推送的目标，它将
无法运作。它在这里唯一的作用就是充当一根线缆，把<ItemLink id="export_bus" />连到
网络上。

供应器也不会以某种方式告诉<ItemLink id="export_bus" />要导出什么，输出总线只会导出
你放进其过滤器里的所有东西。

我们在这里实际做的事情等价于：

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../assets/assemblies/provider_misconception_2.snbt" />

  <BoxAnnotation color="#dddddd" min="1 0 3" max="2 1 4">
        不是高炉
  </BoxAnnotation>

  <IsometricCamera yaw="95" pitch="5" />
</GameScene>

你真正想要做的很可能是下面这样，让样板供应器能把它样板的内容导出到
相邻的机器：

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../assets/assemblies/provider_misconception_3.snbt" />

  <BoxAnnotation color="#dddddd" min="1 0 3" max="2 1 4">
        不是高炉
  </BoxAnnotation>

  <IsometricCamera yaw="95" pitch="5" />
</GameScene>

## 与分子装配室配合使用

<ItemLink id="molecular_assembler" />基本上和任何其他机器一样。它们有可插入的物品栏，
随后对里面的东西执行操作，然后像许多机器一样把产物推送到相邻容器。因此，应该像对待其他机器一样把它们与供应器搭配使用，只需补充一点：

装配室可以从直接插入其中的<ItemLink id="crafting_pattern" />、<ItemLink id="smithing_table_pattern" />或<ItemLink id="stonecutting_pattern" />
读取所需样板。
这在流水线中很有用，但每种配方都要配一个专用装配室会很麻烦。

于是，样板供应器针对装配室有一项特殊功能：它们可以把样板数据连同原料一起发送。
这样，你只需把装配室放在样板供应器旁边，供应器就能把这个装配室用于其所有的
合成、锻造和切石样板。

就是这么简单，只要把样板放进供应器即可：

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/assembler_tower.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

*注意这里正好有8个供应器，这是能够通过单个装配室、供应器或
非致密线缆路由的最大频道数。*

## 配方

<RecipeFor id="pattern_provider" />

<RecipeFor id="cable_pattern_provider" />
