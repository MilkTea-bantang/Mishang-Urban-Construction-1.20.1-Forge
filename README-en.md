# Mishang Urban Construction (Forge 1.20.1 Port)

中文用户可阅读[中文版](README.md).

> **⚠️ This repository is an unofficial Forge port**
>
> This repository is an **unofficial port of [SolidBlock-cn/mishanguc](https://github.com/SolidBlock-cn/mishanguc) (Fabric) to Forge 1.20.1**, ported by **MilkTea°半糖酱**. The original mod is by **SolidBlock** and is licensed under LGPLv3; this port is likewise licensed under **LGPLv3**.
>
> See [Porting changes](#porting-changes) below. For the original content and documentation, visit the [original repository](https://github.com/SolidBlock-cn/mishanguc). Report issues specific to this port in [this repository's Issues](https://github.com/MilkTea-bantang/Mishang-Urban-Construction-1.20.1-Forge/issues).

Mishang Urban Construction is a building and decoration mod focused on city building, providing a full set of blocks and tools around roads, lighting, signage, railings, and colors:

- **Roads and markings**: connectable road and line blocks that join automatically when placed next to each other, plus guide arrows and other road marks.
- **Lights**: lamps in various shapes and colors, including decorations that can be mounted on walls, floors, or ceilings.
- **Signs**: many styles (hanging holographic, vertical, double-sided, glowing, etc.) with freely adjustable text color, size, and position; presets can be managed with `/mishanguc:signpreset`. Vanilla signs also gain an adjustable "density" property, letting same-density signs merge into larger ones when placed adjacent.
- **Railings**: various textures and connection modes; can be placed on stairs.
- **Custom-colored blocks**: any color you want, not limited to the 16 vanilla colors.
- **Building and debugging tools**: fast building, force placing, rotate/mirror, carrying, road marking brush, color and text tools, and more.

For the full list of blocks, items, and tools, see the [original wiki](https://github.com/SolidBlock-cn/mishanguc/wiki) or [MC模组百科](https://www.mcmod.cn/class/5743.html); see the [update log](UpdateLog-en.md) for changes.

The original mod no longer depends on [BRRP](https://github.com/SolidBlock-cn/BRRP) (Better Runtime Resource Pack) since version 1.4.0. **This Forge port does not require Fabric API** — the required interfaces are provided by a bundled compatibility layer (see the porting notes below).

## Porting changes

Changes made by this repository relative to the original (Fabric 1.6.5):

- **Build system**: migrated from Fabric Loom to ForgeGradle 6 (Gradle 8.14.3 + JDK 17), official/MCP mappings.
- **Metadata**: `fabric.mod.json` replaced by Forge's `META-INF/mods.toml`; the built jar no longer contains `fabric.mod.json`.
- **Fabric API**: no Fabric API dependency. The Fabric API interfaces used by the original (networking, registries, events, game rules, item groups, etc.) are implemented on Forge by the bundled compatibility layer `pers.solid.mishang.uc.shim.fabricmc.*`; **calls in the original source remain essentially unchanged**.
- **Mixin**: adapted to Forge's Mixin loading (refmap, `MixinConfigs` manifest attribute, Access Transformer).
- **Datagen**: the Fabric datagen provider classes are excluded from compilation; their generated resources are committed under `src/main/generated`.
- **Gameplay content**: no intentional changes to any gameplay content, blocks, items, recipes, or text.

Other than these porting adaptations, the mod content is identical to the original. The original author is SolidBlock; this port is published under the **LGPLv3** license, which you must follow when propagating it (the full `LICENSE` text is included inside the jar).

This mod **requires Forge 47.x+ (Minecraft 1.20.1)**.

Please report problems in [Issues](https://github.com/MilkTea-bantang/Mishang-Urban-Construction-1.20.1-Forge/issues).
