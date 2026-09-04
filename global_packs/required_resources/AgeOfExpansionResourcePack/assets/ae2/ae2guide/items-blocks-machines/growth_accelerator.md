---
navigation:
  parent: items-blocks-machines/items-blocks-machines-index.md
  title: 生长加速器
  icon: growth_accelerator
  position: 310
categories:
- machines
item_ids:
- ae2:growth_accelerator
---

# 生长加速器

<BlockImage id="growth_accelerator" p:powered="true" scale="8"/>

生长加速器在紧邻萌芽方块放置时，能大幅加速赛特斯石英或紫水晶[的生长](../ae2-mechanics/certus-growth.md)。

有趣的是，它*还可以*加速各种植物的生长。

其原理是在自然随机刻之外，对相邻方块额外施加"随机刻"。
理论上，1个加速器应能让作物以约90倍于正常的速度生长，且效果可以叠加。

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/assemblies/growth_accelerator.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

可以通过顶部或底部供电，既可使用AE2的[线缆](cables.md)，也可使用其他模组的电力线缆。它可以
接受AE2的能量（AE）或Forge能量（FE）。

若要手动供能，可在顶部或底部放置一个<ItemLink id="crank" />并右键点击它。

顶部和底部可以通过其上的粉色通量装饰来辨认。

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/accelerator_connections.snbt" />
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

## 配方

<RecipeFor id="growth_accelerator" />
