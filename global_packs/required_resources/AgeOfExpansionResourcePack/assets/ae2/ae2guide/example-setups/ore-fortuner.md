---
navigation:
  parent: example-setups/example-setups-index.md
  title: 自动矿石时运机
  icon: minecraft:raw_iron
---

# 矿石时运自动化

<ItemLink id="annihilation_plane" />可以附魔任何镐的附魔，包括时运。因此一个显而易见的用途是为几个面板附魔时运，
让<ItemLink id="formation_plane" />和<ItemLink id="annihilation_plane" />快速放置并破坏矿石。

注意由于<ItemLink id="import_bus" />会"逐渐加速"，该装置启动时较慢，几秒后会达到全速。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/ore_fortuner.snbt" />

  <BoxAnnotation color="#dddddd" min="2.7 0 2" max="3 1 3">
        (1) 输入总线：里面装有几张加速卡。
        <ItemImage id="speed_card" scale="2" />
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="0 0 2" max="2 1 2.3">
        (2) 生成面板：处于默认配置。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="0 0 0.7" max="2 1 1">
        (3) 湮灭面板：没有可配置的界面，但已附魔时运。
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="2.7 0 0" max="3 1 1">
        (4) 存储总线：处于默认配置。
  </BoxAnnotation>

<DiamondAnnotation pos="3.5 0.5 2.5" color="#00ff00">
        输入
    </DiamondAnnotation>

<DiamondAnnotation pos="3.5 0.5 0.5" color="#00ff00">
        输出
    </DiamondAnnotation>

<DiamondAnnotation pos="4 0.5 1.5" color="#00ff00">
        连接至主网络
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 配置

*   <ItemLink id="import_bus" />（1）里面装有几张<ItemLink id="speed_card" />。阵列中的生成面板越多，
    需要的加速卡就越多，因为它们能让输入总线一次拉取更多物品。
*   <ItemLink id="formation_plane" />（2）处于默认配置。
*   <ItemLink id="annihilation_plane" />（3）没有界面且无法配置，但已附魔时运。
*   <ItemLink id="storage_bus" />（4）处于默认配置。

## 工作原理

1.  绿色子网络上的<ItemLink id="import_bus" />从第一个桶中导入方块并存入[网络存储](../ae2-mechanics/import-export-storage.md)
2.  绿色子网络上唯一的存储是<ItemLink id="formation_plane" />，它会放置这些方块。
3.  橙色子网络上的<ItemLink id="annihilation_plane" />破坏这些方块，并对它们施加时运。
4.  橙色子网络上的<ItemLink id="storage_bus" />把破坏所得的产物存入第二个桶中。
