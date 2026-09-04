---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 合成CPU多方块结构（存储单元、协处理器、监视器、单元）
  icon: 1k_crafting_storage
  position: 210
categories:
- devices
item_ids:
- ae2:1k_crafting_storage
- ae2:4k_crafting_storage
- ae2:16k_crafting_storage
- ae2:64k_crafting_storage
- ae2:256k_crafting_storage
- ae2:crafting_accelerator
- ae2:crafting_monitor
- ae2:crafting_unit
---

# 合成CPU

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/crafting_cpus.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

<Row>
  <BlockImage id="1k_crafting_storage" scale="4" />

  <BlockImage id="crafting_accelerator" scale="4" />

  <BlockImage id="crafting_monitor" scale="4" />

  <BlockImage id="crafting_unit" scale="4" />
</Row>

合成CPU负责管理合成请求/任务。在执行多步骤合成任务的过程中，它们会存储中间材料，并决定任务规模的上限，同时在一定程度上影响任务的完成速度。详见[自动合成](../ae2-mechanics/autocrafting.md)。

每个合成CPU一次只处理 1 个请求或任务，因此如果你想同时请求 1 个运算处理器和 256 个平滑石头，就需要 2 组CPU多方块结构。

可以将它们设置为处理来自玩家的请求、来自自动化系统（输出总线和接口）的请求，或两者兼有。

右键点击合成CPU会打开合成状态界面，你可以查看该CPU正在处理的合成任务的进度。

## 设置

*   可以将CPU设置为只接受玩家的请求、只接受自动化系统的请求（例如带有
    <ItemLink id="crafting_card" /> 的 <ItemLink id="export_bus" />），或两者都接受。

## 结构

合成CPU是多方块结构，必须是无空隙的实心长方体。它由若干组件构成。

每组CPU必须至少包含 1 个合成存储方块（事实上，最小可用的CPU就是一个单独的 1k 合成存储单元）。

# 合成单元

<BlockImage id="crafting_unit" scale="4" />

（可选）合成单元只是用来填补CPU中的空间，使其成为实心长方体，适用于其他组件数量不足的情况。它同时也是其他组件的基础原料。

<RecipeFor id="crafting_unit" />

# 合成存储单元

<Row>
  <BlockImage id="1k_crafting_storage" scale="4" />

  <BlockImage id="4k_crafting_storage" scale="4" />

  <BlockImage id="16k_crafting_storage" scale="4" />

  <BlockImage id="64k_crafting_storage" scale="4" />

  <BlockImage id="256k_crafting_storage" scale="4" />
</Row>

（必需）合成存储单元提供所有标准元件容量规格（1k、4k、16k、64k、256k）。它们存储合成任务所涉及的材料和中间材料，因此若要让CPU处理材料更多的合成任务，就需要更大或更多的合成存储单元。

<Column>
  <Row>
    <RecipeFor id="1k_crafting_storage" />

    <RecipeFor id="4k_crafting_storage" />

    <RecipeFor id="16k_crafting_storage" />
  </Row>

  <Row>
    <RecipeFor id="64k_crafting_storage" />

    <RecipeFor id="256k_crafting_storage" />
  </Row>
</Column>

# 协处理单元

<BlockImage id="crafting_accelerator" scale="4" />

（可选）协处理器能让CPU以更快的频率运作，从而使系统更频繁地从 <ItemLink id="pattern_provider" /> 发送材料批次。
这使系统能够跟上处理速度较快的机器。举个例子：被多个 <ItemLink id="molecular_assembler" /> 包围的样板供应器，其推送材料的速度快于单个装配室的处理速度，于是材料批次会被分配给周围的各个装配室。

有些复杂配方的多个步骤可以并行进行，比如为制作书架而同时制作木板和书。在合成状态界面（右键点击CPU，或点击[终端](terminals.md)中的锤子图标打开）中，这些步骤都会显示为"已排程"。每增加一个协处理器，就可以多并行进行一个这样的步骤（从而显示为"正在合成"）。不过这一点通常无关紧要，因为你配置协处理器通常是为了提高推送速度，其数量远多于某个配方中可能并行的步骤数。

<RecipeFor id="crafting_accelerator" />

# 合成监视器

<BlockImage id="crafting_monitor" scale="4" />

（可选）合成监视器显示CPU当前正在处理的任务。
可用 <ItemLink id="color_applicator" /> 为屏幕涂色。

<RecipeFor id="crafting_monitor" />
