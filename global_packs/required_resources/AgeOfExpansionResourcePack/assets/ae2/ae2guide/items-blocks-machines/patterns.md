---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 样板
  icon: crafting_pattern
  position: 410
categories:
- tools
item_ids:
- ae2:blank_pattern
- ae2:crafting_pattern
- ae2:processing_pattern
- ae2:smithing_table_pattern
- ae2:stonecutting_pattern
---

# 样板

<ItemImage id="crafting_pattern" scale="4" />

样板在<ItemLink id="pattern_encoding_terminal" />中用空白样板制成，然后插入<ItemLink id="pattern_provider" />
或<ItemLink id="molecular_assembler" />。

针对不同用途，样板有几种不同的类型：

*   <ItemLink id="crafting_pattern" />用于编码合成台制作的配方。它们可以直接放入<ItemLink id="molecular_assembler" />，
    使其在收到原料时立即合成产物，但它们的主要用途是放在分子装配室旁的<ItemLink id="pattern_provider" />里。
    在这种情况下样板供应器有特殊行为，会把相关样板连同原料一起发送给相邻的装配室。
    由于装配室会自动将合成产物弹出到相邻容器，因此只需在样板供应器上贴一个装配室即可实现合成样板的自动化。

***

*   <ItemLink id="smithing_table_pattern" />与合成样板非常相似，但编码的是锻造台配方。它们同样由样板
    供应器和分子装配室自动化，工作方式完全相同。事实上，合成、锻造和切石样板可以
    在同一套配置中使用。

***

*   <ItemLink id="stonecutting_pattern" />与合成样板非常相似，但编码的是切石机配方。它们同样由样板
    供应器和分子装配室自动化，工作方式完全相同。事实上，合成、锻造和切石样板可以
    在同一套配置中使用。

***

*   <ItemLink id="processing_pattern" />是自动合成灵活性的主要来源。它是最通用的类型，只是表示
    "如果样板供应器把这些原料推入相邻容器，ME系统将在不久或遥远的将来收到这些物品"。有了它们，你几乎可以用任何模组机器或熔炉之类的设备进行自动合成。由于它们的用途如此
    通用，且不关心推送原料到收到结果之间发生了什么，你可以玩出很多花样，比如把
    原料输入一整条复杂的工厂生产线，让它分拣物品、从无限产出的
    农场接收其他原料、打印整部《蜜蜂总动员》剧本——ME系统并不在乎，只要最终收到样板所指定的结果就行。实际上，
    它甚至不在乎原料与结果是否有任何关联。你完全可以告诉它"1块樱花木板 = 1个下界之星"，让你的凋灵农场在收到樱花木板时击杀一只凋灵，这也能行得通。

支持放置多个带有相同样板的<ItemLink id="pattern_provider" />并行工作。此外，你还可以让样板写成例如
8个圆石 = 8个石头而不是1个圆石 = 1个石头，这样样板供应器每次操作就会把8个圆石插入你的熔炼装置，
而不再是一次一个。

## 配方

<RecipeFor id="blank_pattern" />
