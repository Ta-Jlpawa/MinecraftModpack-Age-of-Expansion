---
navigation:
  parent: example-setups/example-setups-index.md
  title: 桶填充器
  icon: minecraft:water_bucket
---

# 桶填充器

另请参见[桶排空器](bucket-emptier.md)。

注意，由于它使用了<ItemLink id="pattern_provider" />，因此该装置应当集成到你的[自动合成](../ae2-mechanics/autocrafting.md)
系统中使用。

有时候事情就是这么不方便：你需要的是一桶流体，而不是流体本身。有时某些机器可以帮你完成这件事（例如热力膨胀的流体装填机），但你未必总能找到能方便地做到这一点的模组。幸运的是，
原版 Minecraft 提供了一种稍微没那么方便的方法——<ItemLink id="minecraft:dispenser" />。

**注意，你通常不必这样做，因为[样板编码终端](../items-blocks-machines/terminals.md#pattern-encoding-terminal)中的
流体替换功能允许你在合成配方中直接使用流体本身来代替桶。**

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/bucket_filler.snbt" />

<BoxAnnotation color="#dddddd" min="2 1 0" max="3 2 1">
        (1) 样板供应器：设置为“有红石信号时”锁定合成，并放入相关的处理样板。

        <Row>
        ![填充样板](../assets/diagrams/water_fill_pattern_small.png)
        ![填充样板](../assets/diagrams/lava_fill_pattern_small.png)
        </Row>
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3 1.1 0.1" max="3.2 1.9 0.9">
        (2) 接口：保持默认配置。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3.1 1.1 0.8" max="3.9 1.9 1">
        (3) 存储总线 #1：保持默认配置。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="4.05 1.05 0.8" max="4.95 1.95 1">
        (4) 生成面板：使用反相卡过滤为黑名单排除桶。
        <Row><ItemImage id="minecraft:bucket" scale="2" /><ItemImage id="inverter_card" scale="2" /></Row>
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3.2 2 1.2" max="3.8 2.2 1.8">
        (5) 输入总线：使用反相卡过滤为黑名单排除桶。
        <Row><ItemImage id="minecraft:bucket" scale="2" /><ItemImage id="inverter_card" scale="2" /></Row>
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2.1 2 0.1" max="2.9 2.2 0.9">
        (6) 存储总线 #2：保持默认配置。
  </BoxAnnotation>

<DiamondAnnotation pos="0 1.5 0.5" color="#00ff00">
        连接至主网络
    </DiamondAnnotation>

  <IsometricCamera yaw="225" pitch="45" />
</GameScene>

## 配置

* <ItemLink id="pattern_provider" /> (1) 设置为“有红石信号时”锁定合成，并放入相关的<ItemLink id="processing_pattern" />。
  
    ![充能样板](../assets/diagrams/water_fill_pattern.png)
    ![充能样板](../assets/diagrams/lava_fill_pattern.png)

* <ItemLink id="interface" /> (2) 保持默认配置。
* 第一个<ItemLink id="storage_bus" /> (3) 保持默认配置。
* <ItemLink id="formation_plane" /> (4) 使用反相卡过滤为黑名单排除桶。
  <Row><ItemImage id="minecraft:bucket" scale="2" /><ItemImage id="inverter_card" scale="2" /></Row>
* <ItemLink id="import_bus" /> (5) 使用反相卡过滤为黑名单排除桶。
  <Row><ItemImage id="minecraft:bucket" scale="2" /><ItemImage id="inverter_card" scale="2" /></Row>
* 第二个<ItemLink id="storage_bus" /> (6) 保持默认配置。

## 工作原理

1. <ItemLink id="pattern_provider" /> 将原料推入<ItemLink id="interface" />。
   （实际上，作为一种优化，它会直接穿过存储总线和生成面板推送，就像它们是供应器各面的延伸一样。物品实际上从不进入接口内部。）
2. 通过[管道子网络](pipe-subnet.md#providing-to-multiple-places)中描述的机制和<ItemLink id="formation_plane" />,
   桶最终进入<ItemLink id="minecraft:dispenser" />中，流体则由生成面板放置到位。
3. <ItemLink id="minecraft:comparator" /> 检测到发射器中的桶，从而同时为发射器充能并锁定
   <ItemLink id="pattern_provider" />。
4. 发射器用桶舀取流体，此时其内部是一个装满的桶。
5. <ItemLink id="import_bus" /> 将装满的桶从发射器中抽出，并通过
   <ItemLink id="storage_bus" />存入样板供应器，使其返回主网络。
6. 红石比较器检测到发射器已空，解除对样板供应器的锁定。
