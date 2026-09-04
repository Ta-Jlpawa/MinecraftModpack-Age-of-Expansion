---
navigation:
  parent: example-setups/example-setups-index.md
  title: 投水自动化
  icon: fluix_crystal
---

# 投入水中类配方的自动化

注意由于此装置使用了<ItemLink id="pattern_provider" />，它旨在集成到你的[自动合成](../ae2-mechanics/autocrafting.md)
系统中。

有些配方需要将物品投入水中（不过类似的装置也可以用来把物品投到其他地方）。
这可以用一个<ItemLink id="formation_plane" />、一个<ItemLink id="annihilation_plane" />以及一些配套
结构来自动化（这本质上就是 2 个经过改造的[管道子网络](pipe-subnet.md)）。

此装置旨在与[充能器自动化](charger-automation.md)结合使用，以提供<ItemLink id="charged_certus_quartz_crystal" />。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/throw_in_water.snbt" />

<BoxAnnotation color="#dddddd" min="2 0 1" max="3 1 2">
        (1) 样板供应器：处于默认配置，装有相关处理样板。

        ![福鲁伊克斯样板](../assets/diagrams/fluix_pattern_small.png) ![有瑕萌芽样板](../assets/diagrams/flawed_budding_pattern_small.png)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1.7 0 1" max="2 1 2">
        (2) 接口：处于默认配置。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 .7 1" max="2 1 2">
        (3) 生成面板：设置为将输入以掉落物形式放置。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 2 1" max="2 2.3 2">
        (4) 湮灭面板：没有可配置的界面。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2 1 1" max="3 1.3 2">
        (5) 存储总线：过滤为各样板的产物
        <Row><ItemImage id="fluix_crystal" scale="2" /><BlockImage id="flawless_budding_quartz" scale="2" /></Row>
  </BoxAnnotation>

<DiamondAnnotation pos="3.9 0.5 1.5" color="#00ff00">
        连接至主网络与充能器自动化
        <GameScene zoom="3" background="transparent">
          <ImportStructure src="../assets/assemblies/charger_automation.snbt" />
          <IsometricCamera yaw="195" pitch="30" />
        </GameScene>
    </DiamondAnnotation>

  <IsometricCamera yaw="180" pitch="0" />
</GameScene>

## 配置与样板

* <ItemLink id="pattern_provider" />（1）处于默认配置，装有相关<ItemLink id="processing_pattern" />
  * 对于<ItemLink id="fluix_crystal" />，JEI/REI 的默认配方即可正常工作：

    ![福鲁伊克斯样板](../assets/diagrams/fluix_pattern.png)

  * 对于<ItemLink id="flawed_budding_quartz" />，最好直接用<ItemLink id="quartz_block" />制作，
    这样可以避免一个配方的输入恰好是另一个配方的输出所导致的问题，即存储总线无法过滤：

    ![有瑕萌芽样板](../assets/diagrams/flawed_budding_pattern.png)

* <ItemLink id="interface" />（2）处于默认配置。
* <ItemLink id="formation_plane" />（3）设置为将输入以掉落物形式放置。
* <ItemLink id="annihilation_plane" />（4）没有界面且无法配置。
* <ItemLink id="storage_bus" />（5）过滤为各样板的产物。

## 工作原理

1.  <ItemLink id="pattern_provider" />把材料推入其侧面绿色子网络上的<ItemLink id="interface" />中
2.  接口（默认配置为不储存任何东西）尝试将其内容推入[网络存储](../ae2-mechanics/import-export-storage.md)
3.  绿色子网络上唯一的存储是<ItemLink id="formation_plane" />，它会将收到的物品丢进水中
4.  橙色子网络上的<ItemLink id="annihilation_plane" />试图捡起刚掉落的物品，但无法做到，因为
    样板供应器顶部的<ItemLink id="storage_bus" />（橙色子网络上唯一的存储）被过滤为只接受可能的合成产物
5.  物品在世界中进行转化。
6.  现在湮灭面板可以捡起它面前的物品了，因为存储总线允许存储它们。
7.  存储总线把产物存入样板供应器，使其返回网络。
