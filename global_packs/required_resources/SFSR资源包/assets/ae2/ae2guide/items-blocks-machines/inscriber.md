---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 压印器
  icon: inscriber
  position: 310
categories:
- machines
item_ids:
- ae2:inscriber
---

# 压印器

<BlockImage id="inscriber" scale="8" />

压印器用于借助[压印模板](presses.md)压印电路和[处理器](processors.md)，也可将各种物品压碎成粉末。
它可以接受AE2的能量（AE）或Fabric/Forge能量（E/FE）。它支持分面输入，即从不同侧面插入的物品
会进入其物品栏中的不同槽位。为方便使用，可以用<ItemLink id="certus_quartz_wrench" />旋转它。
它还可以设置为将合成产物推入相邻容器。

输入缓冲区的大小可以调整。例如，如果你想从一个容器向一大排压印器供料，
就应该选择小缓冲区，使材料在各个压印器之间分配得更均衡（而不是第一个
压印器装满64个而其余全空）。

4种电路压印模板用于制作[处理器](processors.md)

<Row>
  <ItemImage id="silicon_press" scale="4" />

  <ItemImage id="logic_processor_press" scale="4" />

  <ItemImage id="calculation_processor_press" scale="4" />

  <ItemImage id="engineering_processor_press" scale="4" />
</Row>

而命名压印模板可以像铁砧一样给方块命名，适合在<ItemLink id="pattern_access_terminal" />中标注各类东西。

<ItemImage id="name_press" scale="4" />

## 设置

* 压印器可以设置为分面模式（如下所述），或允许从任意侧面输入到任意槽位，由内部过滤器决定
    物品去向。在非分面模式下，无法从顶部和底部槽位抽取物品。
* 压印器可以设置为将物品推入相邻容器。
* 输入缓冲区的大小可以调整，大缓冲区适合手动供料的独立压印器，
    小缓冲区则让大规模并行配置更加可行。

## GUI与分面

在分面模式下，压印器根据你插入或抽取物品的侧面来决定物品去向。

![Inscriber GUI](../assets/diagrams/inscriber_gui.png) ![Inscriber Sides](../assets/diagrams/inscriber_sides.png)

A. **顶部输入** 通过压印器的顶面访问（物品既可推入也可从此槽位拉出）

B. **中央输入** 通过压印器的左、右、前、后面插入（物品只能推入此槽位，不能从中取出）

C. **底部输入** 通过压印器的底面访问（物品既可推入也可从此槽位拉出）

D. **输出** 通过压印器的左、右、前、后面取出（物品只能从此槽位取出，不能推入）

## 简易自动化

举个例子，得益于分面特性和可旋转性，你可以像这样半自动化压印器：

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/inscriber_hopper_automation.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

或者在非分面模式下直接用管道向压印器输入和输出物品。

## 升级

压印器支持以下[升级卡](upgrade_cards.md)：

*   <ItemLink id="speed_card" />

## 配方

<RecipeFor id="inscriber" />
