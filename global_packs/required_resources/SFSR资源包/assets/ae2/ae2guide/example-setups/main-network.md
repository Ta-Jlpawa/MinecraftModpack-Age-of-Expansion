---
navigation:
  parent: example-setups/example-setups-index.md
  title: 一个“主网络”示例
  icon: controller
---

# 一个“主网络”示例

许多其他装置都会提到“主网络”。你可能也想问，这些[设备](../ae2-mechanics/devices.md)是如何
组合成一个可用的系统的。下面就是一个例子：

<GameScene zoom="2.5" interactive={true}>
  <ImportStructure src="../assets/assemblies/small_base_network.snbt" />

    <BoxAnnotation color="#33dd33" min="5 1 10" max="9 7 14" thickness="0.05">
        一大片样板供应器和分子装配室为合成、切石和锻造样板提供了充足的空间。
        棋盘格式的布局让供应器能够并行利用多个装配室，同时保持结构紧凑。
        每 8 个一组可以确保频道永远不会被错误地路由。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="13 10 12" max="14 11 14" thickness="0.05">
        你其实并不需要这么大的控制器，那些在别人基地里看到的巨大圆环和立方体设计
        主要只是为了好看。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="13 12 13" max="14 13 14" thickness="0.05">
        每个像样的网络都有一个能量元件，以便在每游戏刻获得更高的能量输入，
        并平滑电力波动。
    </BoxAnnotation>
    
    <BoxAnnotation color="#33dd33" min="2 1 10" max="4 4 13" thickness="0.05">
        你可能更想用其他模组的发电设备——反应堆、太阳能板或发电机之类的东西。
        振动腔勉强能用，但 AE2 的设计初衷就是在模组包中使用你基地的主发电机。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="15 1 9" max="16 3 14" thickness="0.05">
        装饰板可以用来把东西藏在墙后。
    </BoxAnnotation>
    <BoxAnnotation color="#33dd33" min="15 3 12" max="16 10 14" thickness="0.05">
        装饰板可以用来把东西藏在墙后。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="13 9 7" max="14 10 9" thickness="0.05">
        一般存储其实不需要那么多驱动器和元件，2 到 4 个驱动器的 4k 或 16k
        元件几乎总是够用的。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="13 9 10" max="14 11 11" thickness="0.05">
        对于大宗物品存储，你需要过滤为特定物品的大容量元件，放在优先级更高的独立驱动器中。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="10 9 13" max="11.7 13 14" thickness="0.05">
        基于接口的自动补货。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="6 10 12" max="9 12 15" thickness="0.05">
        充能器自动化装置扩展到多个充能器的形态。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="2 10 12" max="5 11 15" thickness="0.05">
        另一种自动化处理器的方式，因为在 1.20 中压印器现在可以自动弹出产物了。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="3 10 10" max="4 12 11" thickness="0.05">
        另一种自动化处理器的方式，因为在 1.20 中压印器现在可以自动弹出产物了。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="7.2 9.2 8.2" max="7.8 10 8.8" thickness="0.05">
        无线访问点放在正中央，因为它的作用范围是一个球形。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="14 1 2" max="16 5 7" thickness="0.05">
        通常你会拥有 1-2 个大型合成CPU来处理大工程，再配几个较小的合成CPU在大CPU忙不过来时处理次要任务。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="5 3 6" max="6 4 7" thickness="0.05">
        有时子网络可能需要自己的控制器，比如当设备超过 8 个时（例如要分配到超过 8 个位置）。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="7.3 1 3.3" max="9.7 4 6" thickness="0.05">
        赛特斯农场。
    </BoxAnnotation>

    <BoxAnnotation color="#33dd33" min="10.3 1 2.3" max="12.7 3.7 5" thickness="0.05">
        投水自动化。
    </BoxAnnotation>

  <IsometricCamera yaw="135" pitch="15" />
</GameScene>
