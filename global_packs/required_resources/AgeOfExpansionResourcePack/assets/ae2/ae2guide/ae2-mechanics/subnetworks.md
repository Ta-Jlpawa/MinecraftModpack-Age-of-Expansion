---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: 子网络
---

# 子网络

<GameScene zoom="4" interactive={true}>
<ImportStructure src="../assets/assemblies/subnet_demonstration.snbt" />

<DiamondAnnotation pos="6.5 2.5 0.5" color="#00ff00">
        物品管道子网络
    </DiamondAnnotation>

<DiamondAnnotation pos="5.5 2.5 0.5" color="#00ff00">
        流体管道子网络
    </DiamondAnnotation>

<DiamondAnnotation pos="4.5 2.5 0.5" color="#00ff00">
        过滤型湮灭面板
    </DiamondAnnotation>

<DiamondAnnotation pos="3.5 2.5 0.5" color="#00ff00">
        生成面板子网络
    </DiamondAnnotation>

<DiamondAnnotation pos="2.5 2.5 0.5" color="#00ff00">
        利用接口-存储总线互动充当本地子存储的子网络，主网络可以访问它
    </DiamondAnnotation>

<DiamondAnnotation pos="1.5 1.5 0.5" color="#00ff00">
        另一个物品管道子网络，用于把充能后的物品送回样板供应器
    </DiamondAnnotation>

<IsometricCamera yaw="195" pitch="30" />
</GameScene>

"子网络"是一个定义相当宽泛的术语，但可以说：子网络就是为你的主网络提供支持、或承担某项小任务的任何[网络](../ae2-mechanics/me-network-connections.md)。它们通常规模很小，不需要控制器。它们的两大主要用途是：

*  限制哪些[设备](../ae2-mechanics/devices.md)能访问哪些存储（你不会想让"管道"子网络上的输入总线能访问主网络的
    存储，否则它会把物品塞进你的存储元件而不是目标容器）。
*   为你的主网络节省频道，比如让一台样板供应器输出到一个接口，该接口再连接多台机器上的多个存储总线，
    只占用1个频道；而不是在每台机器上都放一台样板供应器，占用好几个频道。

搭建子网络时非常关键的一点是盯紧[网络连接](../ae2-mechanics/me-network-connections.md)。
人们常常随手把一堆接口、总线之类的东西拼在一起就指望它成为子网络，
结果所有设备仍然通过各种整格设备连着主网络。

不同颜色的线缆对组建子网络没有任何帮助，唯一的作用只是互不连接而已。

一些例子：

*   用一整套AE2网络取代你的垃圾桶/虚空升级卡，由它决定如何最优化处理你的垃圾。根据可用性和需求智能地把物品路由到堆肥器阵列或某个模组回收机。
*   构建抽象层。在子网络中打理一次复杂合成操作的所有细节，这样从主网络的视角看，整座工厂"看起来"就像一台机器。
*   并行化。用10台慢机器替代1台慢机器。从主网络的视角看，什么都没变，你甚至没有多占用任何频道。
*   一组输入总线和存储总线，像物品或流体管道一样在两个容器之间传输物品或流体。
*   一台湮灭面板加一条存储总线，使湮灭面板破坏的东西只能放进存储总线，从而实现对面板的过滤。
*   一台接口加一块生成面板，凡是插入接口的东西都会被推到生成面板上，放置或丢出到世界中。
*   一套自动生产赛特斯石英的装置，由主网络上的一台<ItemLink id="level_emitter" />调节控制。
*   一套可从主网络通过特殊的存储总线接接口互动访问的专用存储系统，用来存放农场的产出而不会让你的主存储无限溢出。
*   等等

<ItemLink id="quartz_fiber" />对搭建子网络非常有用。它能在不连接网络的情况下在两张网络之间传输电力，
让你无需到处放置能量接收器和供电线缆就能给子网络供电。
