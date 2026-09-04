---
navigation:
  parent: example-setups/example-setups-index.md
  title: 物品/流体"管道"子网络
  icon: storage_bus
---

# 物品/流体"管道"子网络

一种用 AE2 [设备](../ae2-mechanics/devices.md)模拟物品和/或流体管道的简单方法，适用于——嗯——任何你会用到物品或流体管道的场合。
这也包括把合成结果送回<ItemLink id="pattern_provider" />。

通常有两种不同的实现方法：

## 输入总线 -> 存储总线

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/import_storage_pipe.snbt" />

<BoxAnnotation color="#dddddd" min="3.7 0 0" max="4 1 1">
        (1) 输入总线：可以过滤。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 1">
        (2) 存储总线：可以过滤。这个（以及其他你想作为目的地的）存储总线
        必须是网络上唯一的存储。
  </BoxAnnotation>

<DiamondAnnotation pos="4.5 0.5 0.5" color="#00ff00">
        来源
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 0.5" color="#00ff00">
        目的地
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

来源容器上的<ItemLink id="import_bus" />（1）导入物品或流体，并尝试将它们存入[网络存储](../ae2-mechanics/import-export-storage.md)。
由于网络上唯一的存储就是这个<ItemLink id="storage_bus" />（2）（这也是为什么它是子网络而不在你的主网络上），物品或流体
会被放入目的地容器中，从而完成传输。能量通过一根<ItemLink id="quartz_fiber" />提供。
输入总线和存储总线都可以设置过滤，但如果不过滤，该装置会传输它能访问的一切。
这套装置同样支持多个输入总线和多个存储总线。

## 存储总线 -> 输出总线

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/storage_export_pipe.snbt" />

<BoxAnnotation color="#dddddd" min="3.7 0 0" max="4 1 1">
        (1) 存储总线：可以过滤。这个（以及其他你想作为来源的）存储总线
        必须是网络上唯一的存储。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 1">
        (2) 输出总线：必须过滤。
  </BoxAnnotation>

<DiamondAnnotation pos="4.5 0.5 0.5" color="#00ff00">
        来源
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 0.5" color="#00ff00">
        目的地
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

目的地容器上的<ItemLink id="export_bus" />尝试从[网络存储](../ae2-mechanics/import-export-storage.md)中拉取其过滤器内的物品。
由于网络上唯一的存储就是这个<ItemLink id="storage_bus" />（这也是为什么它是子网络而不在你的主网络上），物品或流体
会从来源容器中被拉出，从而完成传输。能量通过一根<ItemLink id="quartz_fiber" />提供。
由于输出总线必须设置过滤才能工作，所以只有给输出总线设置了过滤，这套装置才会运作。
这套装置同样支持多个存储总线和多个输出总线。

## 行不通的装置（输入总线 -> 输出总线）

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/import_export_pipe.snbt" />

<BoxAnnotation color="#dd3333" min="3.7 0 0" max="4 1 1">
        输入总线：由于网络没有存储，它没有可以导入的地方。
  </BoxAnnotation>

<BoxAnnotation color="#dd3333" min="1 0 0" max="1.3 1 1">
        (2) 输出总线：由于网络没有存储，它没有可以导出的东西。
  </BoxAnnotation>

<DiamondAnnotation pos="4.5 0.5 0.5" color="#ff0000">
        来源
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 0.5" color="#ff0000">
        目的地
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

只有输入总线和输出总线的装置是行不通的。输入总线会尝试从来源容器中拉取
物品或流体并存入网络存储；输出总线会尝试从网络存储中拉取物品或流体并放入
目的地容器。然而由于这个网络**没有任何存储**，输入总线无法导入，
输出总线也无法导出，所以什么都不会发生。

## 通过同一个面进行输入与输出

假设你有一台机器，它的输入接收和输出来取都通过同一个面。（比如<ItemLink id="charger" />）
通过组合这两种管道子网络方法，你既可以推入材料，也可以取出结果：

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/import_storage_export_pipe.snbt" />

<BoxAnnotation color="#dddddd" min="4 1 1" max="5 1.3 2">
        (1) 输入总线：可以过滤。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2 1 1" max="3 1.3 2">
        (2) 存储总线：可以过滤。这个（以及其他你想推入和拉取物品的）存储总线
        必须是网络上唯一的存储。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="2 0 1" max="3 1 2">
        (3) 你想推入和拉取的东西：本例中是一台充能器。
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 1 1" max="1 1.3 2">
        (4) 输出总线：必须过滤。
  </BoxAnnotation>

<DiamondAnnotation pos="4.5 0.5 1.5" color="#00ff00">
        来源
    </DiamondAnnotation>

<DiamondAnnotation pos="0.5 0.5 1.5" color="#00ff00">
        目的地
    </DiamondAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 接口

事实证明，除了输入总线和输出总线之外，还有其他设备可以把物品推入和拉出
[网络存储](../ae2-mechanics/import-export-storage.md)！
这里相关的是<ItemLink id="interface" />。如果插入的不是接口设置为储备的物品，接口就会把它推入网络存储，
我们可以像输入总线 -> 存储总线管道那样利用这一点。把接口设置为储备某种物品则会把它从
网络存储中拉出来，类似于存储总线 -> 输出总线管道。接口可以被设置为储备某些物品而不储备另一些，
这样如果你出于某种原因想这么做的话，就可以通过存储总线远程推送和拉取物品。

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/interface_pipes.snbt" />

<BoxAnnotation color="#dddddd" min="3.7 0 0" max="4 1 1">
        接口
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 1">
        存储总线
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="3.7 0 2" max="4 1 3">
        存储总线
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 1 2" max="1 1.3 3">
        接口
  </BoxAnnotation>

<IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 一对多与多对一（以及多对多）

当然，你不一定要只使用一个<ItemLink id="import_bus" />、<ItemLink id="export_bus" />或<ItemLink id="storage_bus" />

<GameScene zoom="3" background="transparent">
<ImportStructure src="../assets/assemblies/many_to_many_pipe.snbt" />

<IsometricCamera yaw="185" pitch="30" />
</GameScene>

## 向多个位置供应

综上所述，我们可以得出一种方法，把材料从一个<ItemLink id="pattern_provider" />的面发送到许多不同的位置，
比如一组机器，或一台机器的几个不同面。

我们不需要输入 -> 存储管道或存储 -> 输出管道，因为<ItemLink id="pattern_provider" />实际上从不持有这些材料。
相反，供应器会把材料*推*送到相邻的容器中，所以我们需要一个还能导入物品的相邻容器。

这听起来像是……一个<ItemLink id="interface" />！
确保供应器处于定向或平面子部件模式，和/或接口处于平面子部件模式，
这样两者之间就不会形成网络连接。

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/provider_interface_storage.snbt" />

<BoxAnnotation color="#dddddd" min="2.7 0 1" max="3 1 2">
        接口（必须是平面式，不能是整块式）
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 1 4">
        多个存储总线
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 0 0" max="1 1 4">
        你想要样板供应的目标位置（多台机器，或一台机器的多个面）
  </BoxAnnotation>

<IsometricCamera yaw="185" pitch="30" />
</GameScene>
