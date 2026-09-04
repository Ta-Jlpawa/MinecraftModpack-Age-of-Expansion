---
navigation:
  parent: ae2-mechanics/ae2-mechanics-index.md
  title: 赛特斯石英生长
  icon: quartz_cluster
---

# 赛特斯石英生长

## 基本就是从入门页复制粘贴过来的

<GameScene zoom="6" background="transparent">
<ImportStructure src="../assets/assemblies/budding_certus_1.snbt" />
</GameScene>

赛特斯石英芽会从[萌芽赛特斯方块](../items-blocks-machines/budding_certus.md)上长出，与紫水晶类似。如果你掰断尚未长成的芽，它会掉落1个<ItemLink id="certus_quartz_dust" />，时运附魔不影响掉落。如果你掰断完全长成的晶簇，它会掉落4个<ItemLink id="certus_quartz_crystal" />，时运可以增加这个数量。

萌芽赛特斯方块有4个等级：无瑕、有瑕、破损和损坏，最初可以在[陨石](../ae2-mechanics/meteorites.md)中找到它们。

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/budding_blocks.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

每当芽再生长一个阶段，萌芽方块就有几率降低一个等级，最终变成普通的赛特斯石英块。将萌芽方块（或普通赛特斯石英块）连同1个或多个<ItemLink id="charged_certus_quartz_crystal" />一起丢入水中，即可修复（并制造新的萌芽方块）。

<RecipeFor id="damaged_budding_quartz" />

无瑕萌芽赛特斯方块不会降级，能无限生成赛特斯石英。但它们无法被合成获得，也无法用镐移动，即使带有精准采集。（不过*可以*用[空间储存](../ae2-mechanics/spatial-io.md)移动）

放任不管的话，赛特斯石英芽的生长非常缓慢。幸运的是，把<ItemLink id="growth_accelerator" />放在萌芽方块旁边可以大幅加速这一过程。你应该把建造几个生长加速器当作第一要务。

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/budding_certus_2.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

由于复杂的相互作用，萌芽方块每一面被遮挡都会拖慢其累计生长速度，最终超过更多加速器带来的增益。实测数据如下：

![各比例下的物品/分钟](../assets/diagrams/certus_farm_speed_chart_1.png)

![常见布局](../assets/diagrams/certus_farm_speed_chart_2.png)

如果你的石英不够再做一台<ItemLink id="energy_acceptor" />或<ItemLink id="vibration_chamber" />，
可以做一根<ItemLink id="crank" />装在加速器末端。

自动收获赛特斯石英的方法[在此说明](../example-setups/simple-certus-farm.md)。
