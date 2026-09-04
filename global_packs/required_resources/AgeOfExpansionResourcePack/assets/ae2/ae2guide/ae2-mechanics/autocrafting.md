---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: 自动合成
  icon: pattern_provider
---

# 自动合成

### 重头戏

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/autocraft_setup_greebles.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

自动合成是AE2的核心功能之一。你不必手动合成正确数量的各种中间材料，像*苦力*一样埋头苦干，而是可以让你的ME系统代劳。或者自动合成物品并将它们输出到某处。又或者通过巧妙的涌现行为自动维持某些物品的库存量。它还能处理流体，如果你安装了支持其他模组材料类型的附加模组（比如通用机械的气体），那些材料也不在话下。这功能相当棒。

这是个相当复杂的话题，系好安全带，我们出发。

一套自动合成装置由3部分组成：
- 发送合成请求的东西
- 合成CPU
- <ItemLink id="pattern_provider" />。

流程如下：

1.  某个东西创建了合成请求。可以是你在终端里点击某个可自动合成的物品，
    也可以是装有合成卡的输出总线或接口请求其设定的输出/库存物品。

*   （**重要：**使用你绑定的"选取方块"键（通常是鼠标中键）来请求合成你已有库存的物品，这可能与物品栏排序模组冲突），

2.  ME系统计算完成该请求所需的材料和前置合成步骤，并将其存入选定的合成CPU

3.  带有相关[样板](../items-blocks-machines/patterns.md)的<ItemLink id="pattern_provider" />将样板中指定的材料推送到任意相邻的容器。
    如果是工作台配方（即"合成样板"），目标会是<ItemLink id="molecular_assembler" />。
    如果是非合成配方（即"处理样板"），目标则是其他方块、机器或复杂的红石控制装置。

4.  合成产物以某种方式返回系统，可以是通过输入总线、接口，或是将产物推回样板供应器。
    **注意必须发生"物品进入系统"事件，你不能只是把产物用管道输入到一个贴着<ItemLink id="storage_bus" />的箱子里。**

5.  如果该合成是请求中另一项合成的前置步骤，这些物品会存储在那个合成CPU中，然后用于那次合成。

## 递归配方

<ItemImage id="minecraft:netherite_upgrade_smithing_template" scale="4" />

自动合成算法*无法*处理的一件事就是递归配方。例如"1个红石粉 = 2个红石粉"这类复制配方（把红石扔进植物魔法魔力池）。另一个例子是原版Minecraft的锻造模板。不过，[有一种方法可以处理这些配方。](../example-setups/recursive-crafting-setup.md)

# 样板

<ItemImage id="crafting_pattern" scale="4" />

样板在<ItemLink id="pattern_encoding_terminal" />中用空白样板制作。

有几种不同类型的样板，用途各异：

*   <ItemLink id="crafting_pattern" />编码工作台制作的配方。它们可以直接放入<ItemLink id="molecular_assembler" />，使其一收到材料就合成产物，但它们的主要用途是放在分子装配室旁边的<ItemLink id="pattern_provider" />里。这种情况下样板供应器有特殊行为：会把相关样板连同材料一起发送给相邻的装配室。由于装配室会把合成产物自动弹出到相邻容器，样板供应器上放一个装配室就足以自动化合成样板了。

***

*   <ItemLink id="smithing_table_pattern" />与合成样板非常相似，但它们编码的是锻造台配方。它们同样通过样板供应器和分子装配室自动化，运作方式完全相同。事实上，合成、锻造和切石样板可以在同一套装置中使用。

***

*   <ItemLink id="stonecutting_pattern" />与合成样板非常相似，但它们编码的是切石机配方。它们同样通过样板供应器和分子装配室自动化，运作方式完全相同。事实上，合成、锻造和切石样板可以在同一套装置中使用。

***

*   <ItemLink id="processing_pattern" />是自动合成灵活性的主要来源。它是最通用的类型，只表达一个意思："如果样板供应器把这些材料推送到相邻容器，ME系统将在或近或远的将来收到这些东西"。你可以用它配合几乎任何模组机器，或者熔炉之类的设备进行自动合成。由于处理样板用途极其宽泛，不关心从推送材料到接收结果之间发生了什么，你可以玩出各种花样：比如把材料送入一条完整的复杂工厂生产线，它会自己分拣东西、从无限产出的农场接收其他材料、打印整部《蜜蜂总动员》剧本——只要ME系统最终收到样板指定的结果就行，它根本不在乎。实际上，它甚至不在乎材料与结果是否有任何关系。你可以告诉它"1个樱花木木板 = 1个下界之星"，让你的凋灵农场在收到樱花木木板时杀死一只凋灵，照样能正常运作。

支持多个带有相同样板的<ItemLink id="pattern_provider" />并行工作。此外，你可以让样板写成例如8个圆石 = 8个石头而不是1个圆石 = 1个石头，这样样板供应器每次操作会一次性插入8个圆石到你的熔炼设备中，而不是一次一个。

## "样板"的最广义形式

其实还有比处理样板更"广义"的"样板"。装有合成卡的<ItemLink id="level_emitter" />可以设置为发出红石信号来触发合成。这种"样板"不定义、也不关心材料。它只表示"如果从这个电平发射器发出红石信号，ME系统将在或近或远的将来收到这个物品"。这通常用于开关无需输入材料的无限农场，或者激活处理递归配方的系统（标准自动合成无法理解递归配方），比如你有台复制圆石的机器时设置"1个圆石 = 2个圆石"。

# 合成CPU

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/crafting_cpus.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

合成CPU管理合成请求/任务。在执行多步合成任务时，它们存储中间材料，并影响任务规模的上限，以及在一定程度影响完成速度。它们是多方块结构，且必须是至少含1个合成存储单元的长方体。

合成CPU由以下部件构成：

*   （必需）[合成存储单元](../items-blocks-machines/crafting_cpu_multiblock.md)，提供所有标准元件容量规格（1k、4k、16k、64k、256k）。它们存储合成所涉及的材料和中间材料，因此要处理的合成任务材料越多，就需要更大或更多的存储单元。
*   （可选）<ItemLink id="crafting_accelerator" />，让系统更频繁地从样板供应器发出成批材料。这使得比如说被6个分子装配室包围的样板供应器能同时向全部6个发送材料（也就是同时利用6个），而不是只用一个。
*   （可选）<ItemLink id="crafting_monitor" />，显示CPU当前正在处理的任务。可以用<ItemLink id="color_applicator" />染色
*   （可选）<ItemLink id="crafting_unit" />，纯粹用来填补空间，使CPU构成长方体。

每个合成CPU一次只处理1个请求或任务，所以如果你想同时请求1个运算处理器和256个平滑石头，就需要2个CPU多方块结构。

可以将它们设置为只处理玩家请求、只处理自动化请求（输出总线和接口），或两者兼收。

# 样板供应器

<Row>
<BlockImage id="pattern_provider" scale="4" />

<BlockImage id="pattern_provider" p:push_direction="up" scale="4" />

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/blocks/cable_pattern_provider.snbt" />
</GameScene>
</Row>

<ItemLink id="pattern_provider" />是你的自动合成系统与世界交互的主要方式。它们把[样板](../items-blocks-machines/patterns.md)中的材料推送到相邻容器，物品也可以插入其中从而进入网络。通常可以把机器的输出管道接回附近的样板供应器（往往就是推送材料的那一个），从而省下一个频道，不必用<ItemLink id="import_bus" />把机器输出拉入网络。

值得注意的是，由于它们直接从合成CPU的[合成存储单元](../items-blocks-machines/crafting_cpu_multiblock.md#crafting-storage)推送材料，其自身容器内从不真正存放材料，所以你不能从它们那里用管道抽出物品。你必须让供应器先推送到另一个容器（比如木桶），再从那个容器用管道输送。

另外要注意，供应器必须一次性推送全部材料，不能推半批。这一点可以利用。

样板供应器与[子网络](../ae2-mechanics/subnetworks.md)上的接口有特殊互动：如果接口未被修改（请求槽位为空），供应器会完全跳过接口，直接推送到该子网络的[存储](../ae2-mechanics/import-export-storage.md)，既跳过接口也不把配方批次填进接口，更重要的是，只有当存储腾出空间时才会插入下一批。

支持多个带有相同样板的样板供应器并行工作。

样板供应器会尝试将批次轮询分配到所有面，从而并行利用所有连接的机器。

## 变体

样板供应器有3种变体：普通型、定向型和扁平型。这会影响它们向哪些具体面推送材料、从哪些面接收物品以及为哪些面提供网络连接。

*   普通样板供应器向所有面推送材料、从所有面接收输入，并且与大多数AE2机器一样，像线缆一样为所有面提供网络连接。

*   定向样板供应器是用<ItemLink id="certus_quartz_wrench" />右键普通样板供应器改变其朝向得到的。它们只向选定面推送材料、从所有面接收输入，并且特别地不在选定面提供网络连接。这样它们就能在不连接网络的情况下向AE2机器推送物品，适合搭建子网络。

*   扁平样板供应器是一种[线缆子部件](../ae2-mechanics/cable-subparts.md)，因此多条线缆上可放置多个，实现紧凑布局。它们的行为类似定向样板供应器的选定面：提供样板、接收输入，并且不在其表面提供网络连接。

样板供应器可在合成网格中于普通型和扁平型之间切换。

## 设置

样板供应器有多种模式：

*   **阻挡模式**：若机器内已有材料，则阻止供应器推送新一批材料。
*   **锁定合成**：可在多种红石条件下锁定供应器，或锁定直至上次合成的产物被插回该特定样板供应器。
*   可以选择在<ItemLink id="pattern_access_terminal" />上显示或隐藏该供应器。

## 优先级

优先级可通过点击GUI右上角的扳手图标设置。若同一物品存在多个[样板](../items-blocks-machines/patterns.md)，高优先级供应器中的样板会优先于低优先级供应器中的样板被使用，除非网络缺少高优先级样板所需的材料。

# 分子装配室

<BlockImage id="molecular_assembler" scale="4" />

<ItemLink id="molecular_assembler" />接收输入的物品，执行相邻<ItemLink id="pattern_provider" />定义的操作，
或执行已插入的<ItemLink id="crafting_pattern" />、<ItemLink id="smithing_table_pattern" />或<ItemLink id="stonecutting_pattern" />定义的操作，
然后将产物推送到相邻容器。

它们的主要用途是紧邻<ItemLink id="pattern_provider" />放置。这种情况下样板供应器有特殊行为：会把相关样板的信息连同材料一起发送给相邻的装配室。由于装配室会把合成产物自动弹出到相邻容器（也就进入了样板供应器的回收槽），样板供应器上放一个装配室就足以自动化合成样板了。

<GameScene zoom="4" background="transparent">
<ImportStructure src="../assets/assemblies/assembler_tower.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>
