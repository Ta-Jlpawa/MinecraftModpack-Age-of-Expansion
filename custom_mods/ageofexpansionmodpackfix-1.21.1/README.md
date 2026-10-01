# Age of Expansion Modpack Fix

一个专用于 Age of Expansion 整合包的模组，用于修改整合包内模组逻辑以及添加部分新物品。

## Registrate 启动注册修复

`RegistrateEventReceiverMixin` 修复 Create 6.0.10 内置 Registrate 1.3.0+67 与 Azimuth 1.4.7 的启动注册竞态。Azimuth 在注册入口增加了逐实例消费逻辑，但没有清空待注册队列；如果事件总线在 Azimuth 检查之后、原库检查之前就绪，原库与 Azimuth 会重复挂载同一批回调，最终触发 `Duplicate client extensions registration for Block{create:belt}`。原库自身还存在共享状态未同步、其他实例的待注册回调遗漏等问题。

补丁用优先级 500 的 HEAD 注入在 Azimuth 的默认优先级 1000 之后应用，插入到入口更前方并提前接管该方法，以同步方法统一管理入队与消费，逐实例清空已消费队列。保留原有一次性事件接收器、优先级与加载完成清理逻辑，不绕过 NeoForge 的重复扩展检查，也不修改动画或网络同步逻辑。

回归命令（在此 MDK 目录执行，需要 Java 21 和已有 Gradle 依赖缓存）：

```powershell
./gradlew.bat --offline compileJava prepareRouterClientSmokeRun
./scripts/test-registrate-listeners.ps1 original
./scripts/test-registrate-listeners.ps1 azimuth
./scripts/test-registrate-listeners.ps1 fixed
./gradlew.bat --offline build runGameTestServer runRouterClientSmoke
```

`original` 与 `azimuth` 应失败。前者读取实际 Registrate 字节码并放大首次状态读取的竞态窗口；后者合入实际 Azimuth 注入方法，模拟事件总线在两次检查之间就绪，稳定复现重复扩展异常。`fixed` 装入最终同步方法，验证三个场景均通过。测试以占位对象调用真实 NeoForge 注册检查，不创建世界。`runRouterClientSmoke` 另外在真实客户端检查 Mixin 的同步方法、传送带/水轮结构/铁轨扩展，以及 32 轮跨实例并发注册和重复事件派发。完整模组集合需要正式启动环境；Connector 在 MDK 的开发启动模式下无法定位原版游戏文件。

还可用 `-Dmixin.debug.export=true` 导出正式客户端的 `OneTimeEventReceiver.class`，复制到 `build/registrateRegression/OneTimeEventReceiver.class` 后运行 `./scripts/test-registrate-listeners.ps1 runtime`，直接验证实际合并后的 Azimuth 与本补丁，而非仅测试替换方法。
