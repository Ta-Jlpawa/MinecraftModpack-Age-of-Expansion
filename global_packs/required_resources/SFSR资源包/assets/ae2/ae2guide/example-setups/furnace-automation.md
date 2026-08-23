---
navigation:
  parent: example-setups/example-setups-index.md
  title: 熔炉自动化
  icon: minecraft:furnace
---

# 熔炉自动化

注意，由于它使用了<ItemLink id="pattern_provider" />，因此该装置应当集成到你的[自动合成](../ae2-mechanics/autocrafting.md)
系统中。如果你只是想单独自动化一座熔炉，用漏斗和箱子之类的东西就够了。

自动化一座<ItemLink id="minecraft:furnace" />要比自动化[充能器](../example-setups/charger-automation.md)这类更简单的机器复杂一些。
熔炉需要从两个不同的侧面输入，并从第三个面输出。待熔炼的物品必须从顶面推入，
燃料必须从侧面推入，而产物则必须从底部抽出。

这可以通过在顶部放置一个<ItemLink id="pattern_provider" />、在侧面放置一个持续推入燃料的<ItemLink id="export_bus" />、再在底部放置一个把产物导入网络的<ItemLink id="import_bus" />来实现。
不过，这样会占用 3 个[频道](../ae2-mechanics/channels.md)。

下面这种方法只需 1 个频道即可实现：

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/furnace_automation.snbt" />

<BoxAnnotation color="#dddddd" min="1 0 0" max="2 1 1">
        (1) 样板供应器：定向变体（使用赛特斯石英扳手转换而成），放入相关的处理样板。

        ![铁矿样板](../assets/diagrams/furnace_pattern_small.png)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="2 1.3 1">
        (2) 接口：保持默认配置。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="1.3 2 1">
        (3) 存储总线 #1：过滤为煤炭。
        <ItemImage id="minecraft:coal" scale="2" />
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 2 0" max="1 2.3 1">
        (4) 存储总线 #2：使用反相卡过滤为黑名单排除煤炭。
        <Row><ItemImage id="minecraft:coal" scale="2" /><ItemImage id="inverter_card" scale="2" /></Row>
  </BoxAnnotation>

<DiamondAnnotation pos="4 0.5 0.5" color="#00ff00">
        连接至主网络
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 配置

* <ItemLink id="pattern_provider" /> (1) 保持默认配置，并放入相关的<ItemLink id="processing_pattern" />。
    对它使用一次<ItemLink id="certus_quartz_wrench" />即可使其变为定向模式。

  ![铁矿样板](../assets/diagrams/furnace_pattern.png)

* <ItemLink id="interface" /> (2) 保持默认配置。
* 第一个<ItemLink id="storage_bus" /> (3) 过滤为煤炭，或任何你想使用的燃料。
* 第二个<ItemLink id="storage_bus" /> (4) 使用<ItemLink id="inverter_card" />过滤为黑名单排除你所使用的燃料。

## 工作原理

1. <ItemLink id="pattern_provider" /> 将原料推入<ItemLink id="interface" />中。
   （实际上，作为一种优化，它会直接穿过各条存储总线推送，就像它们是供应器各面的延伸一样。物品实际上从不进入接口内部。）
2. 接口被设置为不存放任何物品，因此它会尝试将原料推入[网络存储](../ae2-mechanics/import-export-storage.md)中。
3. 绿色子网络上唯一的存储是各个<ItemLink id="storage_bus" />。过滤为煤炭的总线会通过侧面把煤炭放入熔炉的燃料槽。
    过滤为非煤炭的总线则会通过顶面把待熔炼物品放入顶部输入槽。
4. 熔炉开始它的熔炉本职工作。
5. 漏斗从熔炉底部抽出产物，并把它们放进样板供应器的回收槽，使其返回主网络。
