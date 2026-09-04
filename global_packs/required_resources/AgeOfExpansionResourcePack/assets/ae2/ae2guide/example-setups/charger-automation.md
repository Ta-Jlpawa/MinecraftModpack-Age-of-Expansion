---
navigation:
  parent: example-setups/example-setups-index.md
  title: 充能器自动化
  icon: charger
---

# 充能器自动化

注意，由于它使用了<ItemLink id="pattern_provider" />，因此该装置应当集成到你的[自动合成](../ae2-mechanics/autocrafting.md)
系统中。如果你只是想单独自动化一台<ItemLink id="charger" />，用漏斗和箱子之类的东西就够了。

自动化一台<ItemLink id="charger" />相当简单。<ItemLink id="pattern_provider" />把原料推进充能器，
然后由一条[管道子网络](pipe-subnet.md)或其他物品管道把产物推回供应器。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/charger_automation.snbt" />

<BoxAnnotation color="#dddddd" min="1 0 0" max="2 1 1">
        (1) 样板供应器：保持默认配置，放入相关的处理样板。它还会为充能器提供能量。

        ![充能样板](../assets/diagrams/charger_pattern_small.png)
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 1 0" max="1 1.3 1">
        (2) 输入总线：保持默认配置。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 1 0" max="2 1.3 1">
        (3) 存储总线：保持默认配置。
  </BoxAnnotation>

<DiamondAnnotation pos="4 0.5 0.5" color="#00ff00">
        连接至主网络
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 配置

* <ItemLink id="pattern_provider" /> (1) 保持默认配置，并放入相关的<ItemLink id="processing_pattern" />。
  它还会为<ItemLink id="charger" />提供[能量](../ae2-mechanics/energy.md)，因为它表现得像一根[线缆](../items-blocks-machines/cables.md)。
  
    ![充能样板](../assets/diagrams/charger_pattern.png)

* <ItemLink id="import_bus" /> (2) 保持默认配置。
* <ItemLink id="storage_bus" /> (3) 保持默认配置。

## 工作原理

1. <ItemLink id="pattern_provider" /> 将原料推入<ItemLink id="charger" />中。
2. 充能器开始它的充能工作。
3. 绿色子网络上的<ItemLink id="import_bus" />将产物从充能器中抽出，并尝试将其存入
   [网络存储](../ae2-mechanics/import-export-storage.md)中。
4. 绿色子网络上唯一的存储是<ItemLink id="storage_bus" />，它会把产物存入样板供应器，使其返回主网络。
