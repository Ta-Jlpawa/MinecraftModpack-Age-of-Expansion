---
navigation:
  title: 入门指南（1.20+）
  position: 10
---

<div class="notification is-info">
  以下内容仅适用于 Minecraft 1.20 及更新版本的应用能源2。
</div>

# 入门指南

## 获取初始材料

<GameScene zoom="4" background="transparent">
  <ImportStructure src="assets/assemblies/meteor_interior.snbt" />
</GameScene>

要开始游玩应用能源2，首先需要找到一颗[陨石](ae2-mechanics/meteorites.md)。陨石相当常见，往往会在地表留下巨大的坑洞，你在探险途中很可能已经遇到过。
如果还没遇到过，你可以合成一个 <ItemLink id="meteorite_compass" />，它会指向最近的 <ItemLink id="mysterious_cube" />。

找到陨石后，向其中心挖掘。你会找到赛特斯石英晶簇、赛特斯石英芽，以及各种类型的[萌芽赛特斯方块](items-blocks-machines/budding_certus.md)，中心还有一颗神秘方块。

开采赛特斯石英晶簇以及你看到的所有赛特斯石英块。萌芽赛特斯方块也可以采集带走，但若没有精准采集，它们会退化一个等级。

不要敲碎任何无瑕的萌芽赛特斯——即便使用精准采集，它也会退化成有瑕萌芽赛特斯，而且无法再修复回无瑕品质。

另外记得挖走陨石中央的神秘方块，可以获得全部4种压印模板。

## 种植赛特斯石英

<GameScene zoom="4" background="transparent">
<ImportStructure src="assets/assemblies/budding_certus_1.snbt" />
</GameScene>

赛特斯石英芽会从[萌芽赛特斯方块](items-blocks-machines/budding_certus.md)上生长出来，与紫水晶类似。如果敲下尚未成熟的石英芽，
它会掉落一个 <ItemLink id="certus_quartz_dust" />，且不受时运影响。如果敲下完全长成的石英晶簇，则会掉落四个
<ItemLink id="certus_quartz_crystal" />，时运附魔可以增加这一数量。

萌芽赛特斯方块共有4个等级：无瑕、有瑕、破损和损坏。

<GameScene zoom="4" background="transparent">
<ImportStructure src="assets/assemblies/budding_blocks.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

石英芽每生长一个阶段，萌芽方块就有一定概率退化一个等级，最终退化为普通的赛特斯石英块。将萌芽方块（或普通赛特斯石英块）
与一个或多个 <ItemLink id="charged_certus_quartz_crystal" /> 一起丢入水中，即可修复（或重新制造）萌芽方块。

<RecipeFor id="damaged_budding_quartz" />

无瑕萌芽赛特斯方块不会退化，能无限产生赛特斯石英。但它们无法被合成获得，也无法用镐移动，即使附带精准采集也不行
（不过可以借助[空间储存](ae2-mechanics/spatial-io.md)来移动）。

放任不管的话，赛特斯石英芽的生长非常缓慢。幸运的是， <ItemLink id="growth_accelerator" /> 放置在萌芽方块旁边时能
大幅加速这个过程。你应该优先制作几个这样的加速器。

<GameScene zoom="4" background="transparent">
<ImportStructure src="assets/assemblies/budding_certus_2.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

如果你的石英不足以同时制作 <ItemLink id="energy_acceptor" /> 或 <ItemLink id="vibration_chamber" />，
可以先做一个 <ItemLink id="crank" />，装在加速器的末端手动摇柄。

关于赛特斯的自动化采集，[见此处的说明](example-setups/simple-certus-farm.md)。

## 关于福鲁伊克斯的小知识

另一种必需材料是福鲁伊克斯（Fluix），在制作生长加速器的过程中你应该已经接触过它。把充能赛特斯、红石和下界石英一起丢进水中即可制成。自动化制法就"留给读者练习"了。

<ItemLink id="charger" />（充能器）用于生产 <ItemLink id="charged_certus_quartz_crystal" />，如果你还没做的话现在可以做一台。

## 压印处理器

在挖掘陨石的过程中，你会通过打碎神秘方块获得四种"压印模板"。它们用于在 <ItemLink id="inscriber" /> 中制作三种处理器。

<ItemGrid>
  <ItemIcon id="silicon_press" />

  <ItemIcon id="logic_processor_press" />

  <ItemIcon id="calculation_processor_press" />

  <ItemIcon id="engineering_processor_press" />
</ItemGrid>

压印器是一台有朝向的机器，与原版熔炉类似。从顶部或底部插入物品会进入顶部或底部槽位，从侧面或背面插入则进入中间槽位。产物可从侧面或背面取出。

为了方便漏斗自动化（并减少管道乱绕），压印器可以用 <ItemLink id="certus_quartz_wrench" /> 旋转。

每种处理器都先做出几个，为下一步——搭建一个非常基础的 ME 系统——做准备。处理器的自动化生产"[留给读者练习](example-setups/processor-automation.md)"。

## 物质能量科技：ME 网络与存储

### 什么是 ME 存储？

它的发音是"Em-Eee"，是物质能量（Matter Energy）的缩写。

物质能量是应用能源2的核心，就像疯狂科学家版的多格箱子，
它能彻底改变你的存储方式。ME 与 Minecraft 中其他存储系统截然不同，
可能需要一点跳出固有思维的适应过程；不过一旦上手，小小的空间里海量的存储、
多个访问终端等等，都只是冰山一角。

### 入门需要了解什么？

首先，ME 将物品存放在其他物品内部，也就是[存储元件](items-blocks-machines/storage_cells.md)；共有5个等级，容量逐级递增。要使用存储元件，必须将其放入
<ItemLink id="chest" />（ME 存储箱）或 <ItemLink id="drive" />（驱动器）中。

<ItemLink id="chest" /> 会在元件放入后立即显示其中的内容，
你可以像操作 <ItemLink id="minecraft:chest" /> 一样从中取放物品，区别在于物品实际
存放于存储元件中，而不是 <ItemLink id="chest" /> 本身。

<ItemLink id="chest" /> 用途较为有限且偏特定场景。要真正发挥 AE2 的实力，你需要搭建一套 [ME 网络](ae2-mechanics/me-network-connections.md)。

## 你的第一套 ME 系统

现在你已经备齐了应用能源2的基础材料和机器，可以搭建第一套 ME（物质能量）系统了。这将是非常基础的一套：没有自动合成，没有物流，只有简洁、易用的搜索式存储。

<GameScene zoom="6" interactive={true}>
<ImportStructure src="assets/assemblies/tiny_me_system.snbt" />

</GameScene>

*   所需材料清单：
    * 1x <ItemLink id="drive" />
    * 1x <ItemLink id="terminal" /> 或 <ItemLink id="crafting_terminal" />
    * 1x <ItemLink id="energy_acceptor" />
    * 若干[线缆](items-blocks-machines/cables.md)，玻璃线缆、包层线缆或智能线缆均可，但不能是致密线缆
    * 几个[存储元件](items-blocks-machines/storage_cells.md)，推荐混用 4k 规格，兼顾容量与类型
    （更高效的做法是用[元件工作台](items-blocks-machines/cell_workbench.md)对 4k 和 1k 进行分区配置，但这里暂不展开）
---
1.  放置驱动器。
2.  能量接收器（以及其他一些 AE2 [设备](ae2-mechanics/devices.md)）有立方体和平板两种形态，可在合成格中切换。如果是立方体形态，将其紧贴驱动器放置；如果是平板形态，先在驱动器上放一根线缆，再把接收器放在线缆上。
3.  用线缆/管道/导管把你喜爱的产能模组的能量接入能量接收器。
4.  在驱动器上方（或大致齐眼高度）放一根线缆，然后把终端或合成终端接到上面。
5.  把存储元件放入驱动器
6.  大功告成
7.  调整一下终端的设置
8.  尽情享受你的强大能力吧
9.  然后意识到这套网络其实还很小

### 扩展你的网络

现在你有了基础的存储和访问手段，是个不错的开始，但你可能很快就会想
实现一些加工自动化。

一个典型的例子：在熔炉顶部放置 <ItemLink id="export_bus" />
输入矿石，再在熔炉底部放置一个 <ItemLink id="import_bus" />

输出熔炼完成的矿物。

<ItemLink id="export_bus" /> 用于把物品从网络导出到相邻容器，
而 <ItemLink id="import_bus" /> 则把相邻容器中的物品导入网络。

### 突破上限

到这里，你的[设备](ae2-mechanics/devices.md)数量大概接近8个了。一旦达到9个设备，你就得开始
管理[频道](ae2-mechanics/channels.md)。许多设备（虽非全部）都需要占用频道才能运作。

默认情况下，一套网络最多支持8个频道；超过这个限制后，就需要在网络中加入
<ItemLink id="controller" />，这能让你的网络规模大幅扩展。
[智能线缆](items-blocks-machines/cables.md)可以让你直观地看到频道在网络中的走向。初期建议大量使用它们来熟悉频道的行为——尤其是在红石和萤石充裕的时候。
