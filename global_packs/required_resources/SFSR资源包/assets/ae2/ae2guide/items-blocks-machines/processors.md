---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 处理器
  icon: logic_processor
  position: 010
categories:
- misc ingredients blocks
item_ids:
- ae2:logic_processor
- ae2:calculation_processor
- ae2:engineering_processor
- ae2:printed_silicon
- ae2:printed_logic_processor
- ae2:printed_calculation_processor
- ae2:printed_engineering_processor
- ae2:silicon
---

# 处理器

<Row>
  <ItemImage id="logic_processor" scale="4" />

  <ItemImage id="calculation_processor" scale="4" />

  <ItemImage id="engineering_processor" scale="4" />
</Row>

处理器是AE2[设备](../ae2-mechanics/devices.md)和机器的主要原料之一。它们也是你面临的第一个
大型自动化挑战。处理器有三种类型，分别用金、<ItemLink id="certus_quartz_crystal" />
和钻石制成。它们使用[压印模板](presses.md)在<ItemLink id="inscriber" />中制作，流程分为
多个步骤（通常通过一系列压印器和带过滤的管道实现）。

## 生产步骤

<Column gap="5">
  1.  收集/制作所需原料：硅、红石、金、<ItemLink id="certus_quartz_crystal" />、钻石。

  <RecipeFor id="silicon" />

  <br />

  2.  压印前置的印刷电路部件

  <Row>
    <RecipeFor id="printed_silicon" />

    <RecipeFor id="printed_logic_processor" />
  </Row>

  <Row>
    <RecipeFor id="printed_calculation_processor" />

    <RecipeFor id="printed_engineering_processor" />
  </Row>

  <br />

  3.  最终组装

  <Row>
    <RecipeFor id="logic_processor" />

    <RecipeFor id="calculation_processor" />
  </Row>

  <RecipeFor id="engineering_processor" />
</Column>
