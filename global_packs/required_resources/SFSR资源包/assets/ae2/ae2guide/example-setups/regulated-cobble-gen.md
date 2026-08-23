---
navigation:
  parent: example-setups/example-setups-index.md
  title: 自动调节的圆石生成机
  icon: minecraft:cobblestone
---

# 自动调节的圆石生成机

圆石生成机的自动化很简单，只需让一个<ItemLink id="annihilation_plane" />朝向标准的原版手动
圆石生成机即可。然而这样做最终会让你的网络塞满圆石，因此需要进行一些调节。

由于湮灭面板的工作方式（它们表现得像<ItemLink id="import_bus" />），
我们不能简单地让一个装有<ItemLink id="redstone_card" />的<ItemLink id="level_emitter" />对着<ItemLink id="export_bus" />
（因为你无法在没有存储的情况下直接从输入到输出）。我们必须稍微绕一点弯子。

<ItemLink id="toggle_bus" />允许你通过红石信号连接和断开网络的某些部分，但每次这样做都会导致网络重启。
有一个简单的解决办法：把切换总线放在[子网络](../ae2-mechanics/subnetworks.md)上，
这样它只会重启子网络。

我们可以让一个自包含的<ItemLink id="annihilation_plane" />与<ItemLink id="storage_bus" />[子网络](../ae2-mechanics/subnetworks.md)
推送到主网络上的一个<ItemLink id="interface" />中。切换总线将通过一根<ItemLink id="quartz_fiber" />来
连接和断开子网络，从而切断面板的供电。

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/regulated_cobble_gen.snbt" />

<BoxAnnotation color="#dddddd" min="3 2 2" max="7 2.3 3">
        (1) 湮灭面板：没有可配置的界面，但可以附魔效率与耐久以降低能耗。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2 2 2" max="2.3 3 3">
        (2) 存储总线：处于默认配置。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.3 2.3 2" max="2.7 2.7 2.3">
        (3) 切换总线：切换总线必须位于子网络上而不是主网络上，这一点非常重要。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.3 3 2.3" max="2.7 3.3 2.7">
        (4) 电平发射器：配置为圆石及所需数量，设为"当数量低于限制时发射红石信号"。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="1 2 3" max="2 3 2">
        (5) 接口：处于默认配置。
  </BoxAnnotation>

<DiamondAnnotation pos="0 2.5 1.5" color="#00ff00">
        连接至主网络
    </DiamondAnnotation>

<DiamondAnnotation pos="5 1.5 3.5" color="#00ff00">
        含水的楼梯可防止水流动并把熔岩变成黑曜石。
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 配置

* <ItemLink id="annihilation_plane" />（1）没有可配置的界面，但可以附魔效率与耐久以降低能耗。
* <ItemLink id="storage_bus" />（2）处于默认配置。
* <ItemLink id="toggle_bus" />（3）必须位于石英纤维的子网络一侧而不是主网络一侧，
  否则每次切换都会导致主网络重启。
* <ItemLink id="level_emitter" />（4）配置为所需物品与数量，并设为"当数量低于限制时发射红石信号"。
* <ItemLink id="interface" />（5）处于默认配置。

## 工作原理

1. 圆石生成机生成一些圆石。
2. <ItemLink id="annihilation_plane" />破坏这些圆石。 
3. <ItemLink id="storage_bus" />把圆石存入<ItemLink id="interface" />中，将其送入主网络。
4. 当主网络中的圆石数量超过设定数量时，<ItemLink id="level_emitter" />停止
   发送信号，从而关闭<ItemLink id="toggle_bus" />。
5. 这会切断子网络的供电，使湮灭面板停止工作。
