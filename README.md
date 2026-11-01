# 迷上城建模组（Forge 1.20.1 移植版）

Users using English may read [the English version](README-en.md).

> **⚠️ 本仓库是 Forge 移植版 / This repository is an unofficial Forge port**
>
> 本仓库是 [SolidBlock-cn/mishanguc](https://github.com/SolidBlock-cn/mishanguc)（Fabric 版）的**非官方 Forge 1.20.1 移植**，由 **MilkTea°半糖酱** 移植。原作者为 **SolidBlock**，原作以 LGPLv3 协议发布，本移植版同样以 **LGPLv3** 协议发布。
>
> 移植方式与改动见下方[移植改动说明](#移植改动说明)。原作内容与文档请参见[原仓库](https://github.com/SolidBlock-cn/mishanguc)。遇到本移植版特有的问题，请在[本仓库 Issues](https://github.com/MilkTea-bantang/Mishang-Urban-Construction-1.20.1-Forge/issues) 报告。

迷上城建（Mishang Urban Construction）是一个城市建设主题的建筑装饰模组，围绕道路、灯光、标识、栏杆与色彩提供了一整套方块和配套工具：

- **道路与路面标记**：可拼接的路面与标线方块，相邻放置会自动衔接；还能放置导向箭头等路面标记。
- **灯光**：多种形状与颜色的灯，以及可装在墙上、地面、天花板的装饰灯具。
- **告示牌**：样式丰富（悬挂全息、竖式、双面、泛光等），文字的颜色、大小、位置均可自由调节，支持 `/mishanguc:signpreset` 管理预设；原版告示牌也增加了可调的“密度”属性，密度相同的告示牌相邻放置时可合并成更大的告示牌。
- **栏杆**：多种纹理与连接方式，可以放置在楼梯上。
- **自定义色块**：不受原版 16 色的限制，可指定任意颜色。
- **建造与调试工具**：快速建造、强制放置、旋转/镜像、搬运、道路标线刷、颜色与文本工具等，方便批量施工和调试。

完整的方块、物品与工具列表请参见 [原仓库 Wiki](https://github.com/SolidBlock-cn/mishanguc/wiki) 或 [MC模组百科](https://www.mcmod.cn/class/5743.html)；更新记录见[更新记录](UpdateLog.md)。

原作自 1.4.0 起不再依赖 [BRRP](https://github.com/SolidBlock-cn/BRRP)（Better Runtime Resource Pack）。**本 Forge 移植版不需要 Fabric API**，所需接口已由内置兼容层提供，详见下方改动说明。

## 移植改动说明

本仓库相对原作（Fabric 1.6.5）的改动：

- **构建系统**：由 Fabric Loom 迁移至 ForgeGradle 6（Gradle 8.14.3 + JDK 17），MCP/官方映射。
- **元数据**：`fabric.mod.json` → Forge 的 `META-INF/mods.toml`；jar 构建时不再包含 `fabric.mod.json`。
- **Fabric API**：不依赖 Fabric API。原作使用的 Fabric API 接口（网络、注册表、事件、游戏规则、物品栏等）由本仓库内置的兼容层 `pers.solid.mishang.uc.shim.fabricmc.*` 在 Forge 上实现，**原作源码中的调用基本保持原样**。
- **Mixin**：适配 Forge 的 Mixin 加载（refmap、`MixinConfigs` 清单属性、Access Transformer）。
- **数据生成**：Fabric 的 datagen 提供者类不参与编译，其生成的资源已直接提交于 `src/main/generated`。
- **游戏内容**：不故意改动任何玩法内容、方块、物品、配方与文本。

除以上移植适配外，本模组内容与原作一致。原作者为 SolidBlock，本移植版以 **LGPLv3** 协议发布，传播时须遵守本协议（jar 内附带 `LICENSE` 全文）。

本模组**依赖 Forge 47.x+（Minecraft 1.20.1）**。

如遇问题，请在 [Issues](https://github.com/MilkTea-bantang/Mishang-Urban-Construction-1.20.1-Forge/issues) 中报告。
