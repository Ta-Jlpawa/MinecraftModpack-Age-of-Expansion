---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 终端
  icon: crafting_terminal
  position: 210
categories:
- devices
item_ids:
- ae2:terminal
- ae2:crafting_terminal
- ae2:pattern_encoding_terminal
- ae2:pattern_access_terminal
---

# 终端

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/terminals.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

<ItemLink id="pattern_provider" />、<ItemLink id="import_bus" />、<ItemLink id="storage_bus" /> 等等是 AE2 网络与世界交互的主要方式，而终端则是 AE2 网络与*你*交互的主要方式。终端有若干种变体，功能各不相同。

终端会继承其所安装的[线缆](cables.md)的颜色。

它们是[线缆子部件](../ae2-mechanics/cable-subparts.md)。

## 终端的放置

由于终端往往是玩家最先放置的[子部件](../ae2-mechanics/cable-subparts.md)，放错方向、把终端装反的情况很常见。下面是一个该做与不该做的示例：

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/assemblies/terminal_placement.snbt" />
  <IsometricCamera yaw="195" pitch="30" />

  <LineAnnotation color="#ff3333" from="2.5 .5 .5" to="4.5 2.5 .5" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#ff3333" from="2.5 2.5 .5" to="4.5 .5 .5" alwaysOnTop={true} thickness="0.05"/>

  <LineAnnotation color="#33ff33" from="-.5 2.5 .5" to="1 .5 .5" alwaysOnTop={true} thickness="0.05"/>
  <LineAnnotation color="#33ff33" from="1 .5 .5" to="1.5 1 .5" alwaysOnTop={true} thickness="0.05"/>
</GameScene>

你依然拥有一个终端和一个能量接收器，只不过现在终端的方向是正确的，真正接入了网络，而且整体占用的空间也更小。

<a name="terminal-ui"></a>

# 终端搜索

搜索框支持正则表达式（Regex），例如你可以输入 "gtceu:.*ore" 来获取 Gregtech 的所有矿石。学习正则表达式就留给读者自行练习了。

# 终端

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/blocks/terminal.snbt" />
  <IsometricCamera yaw="180" />
</GameScene>

你的基础终端，可用于查看和访问[网络存储](../ae2-mechanics/import-export-storage.md)的内容物，
并向你的[自动合成](../ae2-mechanics/autocrafting.md)系统请求物品。

## 用户界面

基础终端的用户界面分为几个区域

中间区域用于访问你的网络存储。你可以放入和取出物品。有以下几种鼠标/键盘快捷操作：

*   左键抓取一组物品，右键抓取半组。
*   如果某物品或流体等可以被[自动合成](../ae2-mechanics/autocrafting.md)，
    按下你绑定的"选取方块"键（通常是鼠标中键）会弹出一个界面，用于指定要合成的数量。你还可以输入 `3*64/2` 这样的算式，
    或输入 `=32` 来只合成补足库存至 32 所需的数量。
*   按住 Shift 可以将当前显示的物品固定在原位，防止数量变化或有新物品进入系统时列表重新排列。
*   手持桶或其他流体容器右键点击可存入流体；用空的流体容器左键点击终端中的流体即可取出流体。

左侧区域有若干设置按钮，可用于：

*   按名称、模组、数量等不同属性排序
*   查看已存储、可合成或两者
*   查看物品、流体或两者
*   更改排序方式
*   打开详细的终端设置窗口
*   更改终端界面的高度

右侧是用于放置 <ItemLink id="view_cell" />（视界元件）的槽位

中间区域的右上角（锤子按钮）会打开自动合成状态
界面，让你查看各项自动合成的进度以及每个合成CPU正在做什么。

## 配方

<RecipeFor id="terminal" />

<a name="crafting-terminal-ui"></a>

# 合成终端

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/blocks/crafting_terminal.snbt" />
  <IsometricCamera yaw="180" />
</GameScene>

合成终端与普通终端类似，拥有相同的设置和区域，但额外增加了一个合成网格，它会自动从[网络存储](../ae2-mechanics/import-export-storage.md)补充材料。Shift 点击输出时要小心！

你应该尽快把普通终端升级为合成终端。

## 用户界面

合成终端的用户界面与普通终端相同，只是在中间增加了一个合成网格。

另有 2 个额外的按钮，可将合成网格清空至网络存储或你的物品栏。

## 配方

<RecipeFor id="crafting_terminal" />

<a name="pattern-encoding-terminal-ui"></a>

# 样板编码终端

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/blocks/pattern_encoding_terminal.snbt" />
  <IsometricCamera yaw="180" />
</GameScene>

样板编码终端与普通终端类似，拥有相同的设置和区域，但额外增加了一个
[样板](patterns.md)编码界面。它的外观与合成终端的界面相似，但这个合成网格并不会真正执行合成。

除了合成终端之外，你还应该备有一个这种终端。

## 用户界面

该终端的用户界面与普通终端相同，并增加了[样板](patterns.md)编码界面。

样板编码界面分为几个部分：

一个用于放入 <ItemLink id="blank_pattern" />（空白样板）的槽位。

一个用于编码样板的大箭头。

一个放置已编码样板的槽位。把已经编码过的样板放入此槽位即可编辑，然后点击"编码"箭头。

右侧有 4 个标签页，用于切换待编码样板的类型

*   合成
*   处理
*   锻造
*   切石

中央界面会根据待编码样板的类型而变化：

*   在合成模式下：
    *   在 JEI/REI 中左键点击或拖入材料来组成配方。右键点击可移除材料。
    *   启用替代功能后，可以实现诸如用任意种类的木板合成木棍之类的效果。应当只在绝对必要时使用。
    *   流体替代允许使用已存储的流体来代替桶装流体。
    *   你也可以直接在 JEI/REI 的配方界面中直接编码样板。

*   在处理模式下：
    * 在 JEI/REI 中左键或右键点击或拖入材料，以指定配方的输入和输出。
    * 用流体容器（如桶或流体储罐）右键点击，即可将该流体本身而非桶或储罐物品设为一种材料。
    * 手持一组物品时，左键放入整组，右键放入单个。左键点击已有的材料组可整组移除，右键点击则将其数量减 1。按下你绑定的"选取方块"键（通常是鼠标中键）
        可以精确指定物品或流体的数量。
    * 输出槽位有一个主要输出，以及可供你可能希望自动合成算法知晓的所有次要输出使用的空间。
    * 输入和输出槽位均可滚动，因此你可以设置多达 81 种不同的材料和 26 种次要输出
    * 你也可以直接在 JEI/REI 的配方界面中直接编码样板。

*   锻造和切石模式的界面分别类似于锻造台和切石机。

## 配方

<RecipeFor id="pattern_encoding_terminal" />

<a name="pattern-access-terminal-ui"></a>

# 样板访问终端

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../assets/blocks/pattern_access_terminal.snbt" />
  <IsometricCamera yaw="180" />
</GameScene>

样板访问终端用于解决一个具体问题：在由 <ItemLink id="pattern_provider" />（样板供应器）和 <ItemLink id="molecular_assembler" />（分子装配室）密集堆叠的高塔中，你无法实际接触到供应器来插入新样板。此外，
也许你就是懒得穿过整个基地去插入一张[样板](patterns.md)。样板访问终端让你可以访问网络上的所有样板供应器。

## 用户界面

该终端的用户界面与其他所有终端都不同。

它提供了终端高度以及显示哪些样板供应器的设置。

终端中的每一行对应一个特定的样板供应器。

终端中的样板供应器会按照它们所连接的方块排序，或者按照你在铁砧中
或使用<ItemLink id="name_press" />给它们起的名字排序。

## 配方

<RecipeFor id="pattern_access_terminal" />
