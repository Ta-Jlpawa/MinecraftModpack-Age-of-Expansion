---
navigation:
  parent: example-setups/example-setups-index.md
  title: 处理器自动化
  icon: logic_processor
---

# 处理器生产自动化

自动化[处理器](../items-blocks-machines/processors.md)生产的方法有很多种，这只是其中之一。

这种通用布局可以用任何类型的物品物流管道、导管、风管等（随模组的叫法而定）来实现，
只要你能够对它进行过滤就行。

![处理流程图](../assets/diagrams/processor_flow_diagram.png)

下面详细介绍如何仅用 AE2 来实现，使用的是["管道"子网络](pipe-subnet.md)。

注意由于此装置使用了 <ItemLink id="pattern_provider" />，它旨在集成到你的[自动合成](../ae2-mechanics/autocrafting.md)
系统中。如果你只是想单独自动化处理器生产，请把样板供应器换成另一个桶，并直接把原材料放进上方的桶中。

碰巧的是，这种做法与之前的 AE2 版本向后兼容，因为即使 <ItemLink id="inscriber" /> 是有朝向之分的，
管道子网络依然会向正确的面插入并从正确的面抽取。

## 样板编码的一课

通常，你需要编码的[样板](../items-blocks-machines/patterns.md)**不会与你在 JEI 中看到的一致**，也不是点击 + 按钮时 JEI 生成的结果。
在这种情况（压印器配方）下，JEI 会生成 2 个独立的样板：一个用于压制后的中间产物，一个用于最终组装，而且压制中间产物的样板
还会包含一枚[压印模板](../items-blocks-machines/presses.md)。这不是我们想要的，因为这个装置并不是这样工作的。我们要的是 1 个样板：
输入原材料并输出成品处理器；既然压印模板已经装在压印器里了，就不应该再把它放进样板。

---

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/processor_automation.snbt" />

  <BoxAnnotation color="#dddddd" min="5 1 0" max="6 2 1" thickness=".05">
        (1) 样板供应器：处于默认配置，装有相关的处理样板。

        <Row>
            ![逻辑样板](../assets/diagrams/logic_pattern_small.png)
            ![运算样板](../assets/diagrams/calculation_pattern_small.png)
            ![工程样板](../assets/diagrams/engineering_pattern_small.png)
        </Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4.7 2 0" max="5 3 1" thickness=".05">
        (2) 存储总线 #1：处于默认配置。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 1 0" max="4.3 2 1" thickness=".05">
        (3) 输出总线 #1：过滤为硅，装有 2 张加速卡
        <Row><ItemImage id="silicon" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 4 0" max="4.3 3 1" thickness=".05">
        (4) 输出总线 #2：过滤为金锭，装有 2 张加速卡
        <Row><ItemImage id="minecraft:gold_ingot" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 5 0" max="4.3 4 1" thickness=".05">
        (5) 输出总线 #3：过滤为赛特斯石英水晶，装有 2 张加速卡
        <Row><ItemImage id="certus_quartz_crystal" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 6 0" max="4.3 5 1" thickness=".05">
        (6) 输出总线 #4：过滤为钻石，装有 2 张加速卡
        <Row><ItemImage id="minecraft:diamond" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.3 3 0" max="2 2 1" thickness=".05">
        (7) 输出总线 #5：过滤为红石粉，装有 2 张加速卡
        <Row><ItemImage id="minecraft:redstone" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 1 0" max="3 2 1" thickness=".05">
        (8) 压印器 #1：处于默认配置。装有硅压印模板和 4 张加速卡
        <Row><ItemImage id="silicon_press" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 3 0" max="3 4 1" thickness=".05">
        (9) 压印器 #2：处于默认配置。装有逻辑压印模板和 4 张加速卡
        <Row><ItemImage id="logic_processor_press" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 4 0" max="3 5 1" thickness=".05">
        (10) 压印器 #3：处于默认配置。装有运算压印模板和 4 张加速卡
        <Row><ItemImage id="calculation_processor_press" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="4 5 0" max="3 6 1" thickness=".05">
        (11) 压印器 #4：处于默认配置。装有工程压印模板和 4 张加速卡
        <Row><ItemImage id="engineering_processor_press" scale="2" /> <ItemImage id="speed_card" scale="2" /></Row>
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2 2 0" max="1 3 1" thickness=".05">
        (12) 压印器 #5：处于默认配置。装有 4 张加速卡
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 2 0" max="3 1 1" thickness=".05">
        (13) 输入总线 #1：处于默认配置，装有 2 张加速卡
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 4 0" max="3 3 1" thickness=".05">
        (14) 输入总线 #2：处于默认配置，装有 2 张加速卡
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 5 0" max="3 4 1" thickness=".05">
        (15) 输入总线 #3：处于默认配置，装有 2 张加速卡
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 6 0" max="3 5 1" thickness=".05">
        (16) 输入总线 #4：处于默认配置，装有 2 张加速卡
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2 3 0" max="1 3.3 1" thickness=".05">
        (17) 存储总线 #2：处于默认配置。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2 1.7 0" max="1 2 1" thickness=".05">
        (18) 存储总线 #3：处于默认配置。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 2 0" max="0.7 3 1" thickness=".05">
        (19) 输入总线 #5：处于默认配置，装有 2 张加速卡
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="5 0.7 0" max="6 1 1" thickness=".05">
        (20) 存储总线 #4：处于默认配置。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3.3 2.7 0.3" max="3.7 3 0.7" thickness=".05">
        石英纤维为所有 3 台压印器供电，因为压印器的作用类似于线缆，因此可以传输能量
  </BoxAnnotation>

<DiamondAnnotation pos="7 1.5 0.5" color="#00ff00">
        连接至主网络
    </DiamondAnnotation>

  <IsometricCamera yaw="185" pitch="5" />
</GameScene>

## 配置

* <ItemLink id="pattern_provider" />（1）处于默认配置，装有相关的 <ItemLink id="processing_pattern" />。
  注意这些样板直接从原材料对应到成品处理器，并且**不**包含[压印模板](../items-blocks-machines/presses.md)。

  ![逻辑样板](../assets/diagrams/logic_pattern.png)
  ![运算样板](../assets/diagrams/calculation_pattern.png)
  ![工程样板](../assets/diagrams/engineering_pattern.png)

* 各<ItemLink id="storage_bus" />（2、17、18、20）均处于默认配置。
* 各<ItemLink id="export_bus" />（3-7）已过滤为相应的材料。它们各装有 2 张<ItemLink id="speed_card" />。
    <Row>
      <ItemImage id="silicon" scale="2" />
      <ItemImage id="minecraft:gold_ingot" scale="2" />
      <ItemImage id="certus_quartz_crystal" scale="2" />
      <ItemImage id="minecraft:diamond" scale="2" />
      <ItemImage id="minecraft:redstone" scale="2" />
    </Row>
* 各<ItemLink id="import_bus" />（13-16、19）均处于默认配置。它们各装有 2 张<ItemLink id="speed_card" />。
* 各<ItemLink id="inscriber" />处于默认配置。它们装有相应的[压印模板](../items-blocks-machines/presses.md)，以及 4 张<ItemLink id="speed_card" />。
   <Row>
     <ItemImage id="silicon_press" scale="2" />
     <ItemImage id="logic_processor_press" scale="2" />
     <ItemImage id="calculation_processor_press" scale="2" />
     <ItemImage id="engineering_processor_press" scale="2" />
   </Row>

## 工作原理

1. <ItemLink id="pattern_provider" /> 把材料推入桶中。
2. 第一个[管道子网络](pipe-subnet.md)（橙色）把硅、红石粉以及相应处理器所需的材料
   （金锭、赛特斯石英水晶或钻石）从桶中抽出，放入对应的 <ItemLink id="inscriber" /> 中。
3. 前四个 <ItemLink id="inscriber" /> 制作出<ItemLink id="printed_silicon" />，以及<ItemLink id="printed_logic_processor" />、
   <ItemLink id="printed_calculation_processor" />或<ItemLink id="printed_engineering_processor" />。
4. 第二个和第三个[管道子网络](pipe-subnet.md)（绿色）从前四个 <ItemLink id="inscriber" /> 中取出压制好的电路，
    放入第五个负责最终组装的 <ItemLink id="inscriber" /> 中。
5. 第五个 <ItemLink id="inscriber" /> 组装出[处理器](../items-blocks-machines/processors.md)。
6. 第四个[管道子网络](pipe-subnet.md)（紫色）把成品处理器放入样板供应器，使其返回主网络。
