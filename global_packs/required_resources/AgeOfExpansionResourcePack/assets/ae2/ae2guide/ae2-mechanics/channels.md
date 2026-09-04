---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: 频道
  icon: controller
---

# 频道

应用能源2的[ME网络](me-network-connections.md)需要频道来支持使用网络存储或其他网络
服务的[设备](../ae2-mechanics/devices.md)。可以把频道想象成连接所有设备的USB线。一台电脑只有那么多USB接口，只能支持
连接一定数量的设备。大多数机器、整格设备和普通线缆最多只能通过8个频道。你可以把整格设备和普通线缆看作一捆8根"频道导线"。不过，[致密线缆](../items-blocks-machines/cables.md#dense-cable)最多可支持
32个频道。唯一另外能传输32频道的设备是<ItemLink id="me_p2p_tunnel" />
和[量子网络桥](../items-blocks-machines/quantum_bridge.md)。每当一个设备占用一个频道，想象成从导线束里抽走了一根USB"导线"，显然这意味着这根"导线"在线路下游就不可用了。

<GameScene zoom="7" interactive={true}>
  <ImportStructure src="../assets/assemblies/channel_demonstration_1.snbt" />

  <LineAnnotation color="#33ff33" from="1 .4 .7" to="2.4 .4 .7" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .6 .7" to="2.4 .6 .7" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .4 .6" to="2.6 .4 .6" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .6 .6" to="2.6 .6 .6" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .6 .6" to="2.6 .6 .6" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="2.4 .6 .7" to="2.4 .6 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.4 .4 .7" to="2.4 .4 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.6 .6 .6" to="2.6 .6 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.6 .4 .6" to="2.6 .4 1.5" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="2.1 .6 1.5" to="2.4 .6 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.6 .4 1.5" to="2.9 .4 1.5" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="2.6 .6 1.5" to="2.6 .9 1.5" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="2.4 .1 1.5" to="2.4 .4 1.5" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="1 .6 .4" to="3.5 .6 .4" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .4 .4" to="3.5 .4 .4" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="3.5 .6 .4" to="3.5 .9 .4" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="3.5 .1 .4" to="3.5 .4 .4" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="1 .6 .3" to="1.5 .6 .3" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1 .4 .3" to="1.5 .4 .3" alwaysOnTop={true}/>

  <LineAnnotation color="#33ff33" from="1.5 .6 .3" to="1.5 .9 .3" alwaysOnTop={true}/>
  <LineAnnotation color="#33ff33" from="1.5 .1 .3" to="1.5 .4 .3" alwaysOnTop={true}/>

  <LineAnnotation color="#ff3333" from="3.5 .5 .5" to="5.5 .5 .5" alwaysOnTop={true}>
  线缆中的8个频道已全部被占用，所以驱动器分不到频道。
  </LineAnnotation>

  <LineAnnotation color="#993333" from="1 .5 .5" to="1.25 .5 .5" alwaysOnTop={true}/>
  <LineAnnotation color="#993333" from="1.5 .5 .5" to="1.75 .5 .5" alwaysOnTop={true}/>
  <LineAnnotation color="#993333" from="2 .5 .5" to="2.25 .5 .5" alwaysOnTop={true}/>
  <LineAnnotation color="#993333" from="2.5 .5 .5" to="2.75 .5 .5" alwaysOnTop={true}/>
  <LineAnnotation color="#993333" from="3 .5 .5" to="3.25 .5 .5" alwaysOnTop={true}/>

  <DiamondAnnotation pos="3.6 0.5 0.5" color="#ff0000">
        线缆中的8个频道已全部被占用，所以驱动器分不到频道。
    </DiamondAnnotation>

  <IsometricCamera yaw="15" pitch="30" />
</GameScene>

查看网络中频道的分配和走向有一个简单方法：使用[智能线缆](../items-blocks-machines/cables.md)，它会在自身上显示频道的路径和使用情况。

每经过一个节点，频道会消耗1⁄128 AE/t的电力，这意味着在一个拥有8台设备、超过96个节点的网络中，
加装一个<ItemLink id="controller" />反而可能降低功耗，因为它改变了频道的分配方式。

值得注意的是，**频道与线缆颜色毫无关系**，线缆颜色唯一的作用就是让线缆互不连接。

## 频道路由

使用<ItemLink id="controller" />时，
频道按3步路由。首先经过相邻机器走最短路径到达最近的[普通线缆](../items-blocks-machines/cables.md)
（玻璃、包层或智能）。然后沿该普通线缆走最短路径到达最近的[致密线缆](../items-blocks-machines/cables.md)
（致密或致密智能）。最后沿该致密线缆走最短路径到达<ItemLink id="controller" />。
如果最短路径已经满载，某些[设备](devices.md)可能得不到所需频道。请善用彩色线缆、线缆固定器和隧道，
确保频道沿着你期望的路径传输。

例如在这个案例中，一些驱动器没分到频道，因为尽管线缆总容量充足，频道却试图走最短路径，
导致部分线缆过载而其他线缆空置。

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/channel_path_length_issue.snbt" />

  <LineAnnotation color="#33ff33" from="3 .5 1.4" to="0.4 0.5 1.4" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="0.4 .5 1.4" to="0.4 0.5 3.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="0.4 0.5 3.6" to="1.4 0.5 3.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="1.4 0.5 3.6" to="1.4 0.5 5" alwaysOnTop={true} thickness="0.05"/>

  <LineAnnotation color="#33ff33" from="3 0.5 3.6" to="1.6 0.5 3.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="1.6 0.5 3.6" to="1.6 0.5 5" alwaysOnTop={true} thickness="0.05"/>

  <LineAnnotation color="#ff3333" from="3 .5 1.6" to="0.6 .5 1.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#ff3333" from="0.6 .5 1.6" to="0.6 .5 3.4" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#ff3333" from="0.6 .5 3.4" to="1.4 .5 3.4" alwaysOnTop={true} thickness="0.05"/>

  <LineAnnotation color="#ff3333" from="3 .5 3.4" to="1.6 .5 3.4" alwaysOnTop={true} thickness="0.05"/>

  <BoxAnnotation color="#dddddd" min="1.2 0.2 3.2" max="1.8 0.8 3.8" alwaysOnTop={true} thickness="0.05">
        超过8个频道试图从这里通过，因此部分频道被切断。
  </BoxAnnotation>

  <IsometricCamera yaw="90" pitch="90" />

</GameScene>

这个问题可以通过更谨慎地约束频道可走的路径来解决。网络应当设计成树状（或灌木状）。
应尽量减少环路和含糊不清的频道路径。

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/channel_path_length_issue_fix.snbt" />

  <LineAnnotation color="#33ff33" from="3 .5 1.4" to="0.4 0.5 1.4" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="0.4 .5 1.4" to="0.4 0.5 5.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="0.4 0.5 5.6" to="1 0.5 5.6" alwaysOnTop={true} thickness="0.05"/>

  <LineAnnotation color="#33ff33" from="3 0.5 3.6" to="1.6 0.5 3.6" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="1.6 0.5 3.6" to="1.6 0.5 5" alwaysOnTop={true} thickness="0.05"/>

  <IsometricCamera yaw="90" pitch="90" />

</GameScene>

## 即席网络

没有<ItemLink id="controller" />的网络被视为即席（Ad-Hoc）网络，
最多支持8个占用频道的设备。一旦超过8台设备，网络中占用频道的设备就会关机停摆。
你可以选择移除设备，或加装一个<ItemLink id="controller" />。

与有控制器的网络不同，即席网络上的[智能线缆](../items-blocks-machines/cables.md)显示的是全网使用的频道总数，
而不是流经该条特定线缆的频道数。

使用即席网络时，每台设备都占用全网1个频道，这与<ItemLink id="controller" />基于最短路径分配频道的方式截然不同。

## 设计

正如[频道路由](channels.md#channel-routing)中提到的，最好把网络设计成树状结构：致密线缆从控制器分支而出，
普通线缆从致密线缆分支而出，而[设备](../ae2-mechanics/devices.md)以不超过8个为一组接在普通线缆上。

下面是一个反面教材：

跟着频道路径走：

1. 从控制器向右出来立即被限制到8个频道，因为驱动器表现得像条普通线缆。
然而由于这里没用智能线缆，我们看不到已用频道数。剩余8个频道。
2. 驱动器占掉1个频道。
剩7个频道。
3. 有2个频道上行通往终端。
剩5个频道。
4. 继续向右，接口又占掉1个频道。
剩4个频道。
5. 有1个频道上行通往样板供应器。
剩3个频道。
6. 继续向右，有1个频道上行通往输入总线。
剩2个频道。
7. 给装配室供料的样板供应器组只分到2个频道，所以其中2台供应器没有频道。

归根结底，错误在于让频道出现瓶颈，而没有事先想清楚频道将如何分配。

<GameScene zoom="4" interactive={true}>
  <ImportStructure src="../assets/assemblies/bad_network_structure.snbt" />

<LineAnnotation color="#33ff33" from="6.5 .5 1.5" to="6 .5 1.5" alwaysOnTop={true} thickness="0.4">
  32个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="6 .5 1.5" to="5.5 .5 1.5" alwaysOnTop={true} thickness="0.2">
  8个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 .5 1.5" to="5.5 1.5 1.5" alwaysOnTop={true} thickness="0.1">
  2个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 .5 1.5" to="5.5 .3 1.5" alwaysOnTop={true} thickness="0.071">
  1个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 1.5 1.5" to="5.5 2.5 1.5" alwaysOnTop={true} thickness="0.071">
  1个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 2.5 1.5" to="5.5 2.5 1.1" alwaysOnTop={true} thickness="0.071">
  1个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="5.5 .5 1.5" to="4.5 .5 1.5" alwaysOnTop={true} thickness="0.158">
  5个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="4.5 .5 1.5" to="4.5 .3 1.5" alwaysOnTop={true} thickness="0.071">
  1个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="4.5 .5 1.5" to="4.5 1.5 1.5" alwaysOnTop={true} thickness="0.071">
  1个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="4.5 .5 1.5" to="3.5 .5 1.5" alwaysOnTop={true} thickness="0.122">
  3个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="3.5 .5 1.5" to="3.5 2.5 1.5" alwaysOnTop={true} thickness="0.071">
  1个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="3.5 2.5 1.5" to="3.7 2.5 1.5" alwaysOnTop={true} thickness="0.071">
  1个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="3.5 .5 1.5" to="1.5 .5 1.5" alwaysOnTop={true} thickness="0.1">
  2个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="1.5 0.5 1.5" to="1.5 0.3 1.5" alwaysOnTop={true} thickness="0.071">
  1个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="1.5 0.5 1.5" to="0.5 0.5 1.5" alwaysOnTop={true} thickness="0.071">
  1个频道
</LineAnnotation>

<LineAnnotation color="#33ff33" from="0.5 0.5 1.5" to="0.5 0.5 0.5" alwaysOnTop={true} thickness="0.071">
  1个频道
</LineAnnotation>

<LineAnnotation color="#ff3333" from="0.5 1.5 1.5" to="0.5 1.3 1.5" alwaysOnTop={true} thickness="0.071">
  没有频道
</LineAnnotation>

<LineAnnotation color="#ff3333" from="1.5 1.5 0.5" to="1.5 1.3 0.5" alwaysOnTop={true} thickness="0.071">
  没有频道
</LineAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

---

下面是一个优秀结构示例：

<GameScene zoom="2.5" interactive={true}>
  <ImportStructure src="../assets/assemblies/treelike_network_structure.snbt" />

    <BoxAnnotation color="#dddddd" min="6.9 0 4.9" max="9.1 4 7.1" thickness="0.05">
        注意样板供应器以8个一组分开布置。
    </BoxAnnotation>

    <BoxAnnotation color="#dddddd" min="5 4 4" max="8 5 5" thickness="0.05">
        两条装满频道的普通线缆汇聚，意味着你需要一条致密线缆。
    </BoxAnnotation>

    <BoxAnnotation color="#dddddd" min="5 0 13" max="8 1 14" thickness="0.05">
        使用不同颜色的线缆防止相邻线缆互相连接。
    </BoxAnnotation>


  <IsometricCamera yaw="315" pitch="30" />
</GameScene>

## 频道模式

面向Minecraft 1.18的AE2 10.0.0引入了新选项，可以更改AE2频道在你的世界中的运作方式。
常规配置段新增了一个配置项（`channels`）用于控制此选项，同时新增了一条游戏内
命令，供管理员在游戏中直接修改模式和配置。命令`/ae2 channelmode <mode>`用于更改模式，
`/ae2 channelmode`用于显示当前模式。在游戏中更改模式后，所有现有网格会立即重启并采用新模式。

这一功能复活并改进了Minecraft 1.12时代的旧选项，为那些只想要稍微轻松一点的玩法、
但又不想彻底移除该机制的玩家提供了更好的选择。

下表列出了配置文件和命令中可用的各种模式。

| 设置       | 描述                                                                                                                                                                                                                                      |
| ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `default`  | 标准模式，线缆与即席网络的频道容量如本网站通篇所述                                                                                                                                                                                        |
| `x2`       | 所有频道容量翻倍（普通线缆16、致密线缆64、即席网络支持16个频道）                                                                                                                                                                          |
| `x3`       | 所有频道容量三倍（普通线缆24、致密线缆92、即席网络支持24个频道）                                                                                                                                                                          |
| `x4`       | 所有频道容量四倍（普通线缆32、致密线缆128、即席网络支持32个频道）                                                                                                                                                                         |
| `infinite` | 移除所有频道限制。控制器仍能*显著*降低网格的耗电。智能线缆只在全灭（不承载频道）与全亮（承载1个及以上频道）之间切换。                                                                                                                      |
