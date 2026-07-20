# SwapColumn — Minecraft 26.1.2 移植版

> 此仓库是 [TAPM](https://github.com/TAPM04) 开发的[原模组](https://github.com/TAPM04/swapcolumn)移植到 Minecraft **26.1.2** 的版本。

SwapColumn 是一个 Fabric 客户端模组，让你无需打开背包即可将物品在快捷栏和其正上方的三格库存栏位之间快速交换。

![SwapColumn 效果预览](docs/icon.png)

## 使用方法

SwapColumn 会在所选快捷栏上方打开一个**纵向交换栏**，显示该栏位上方各格的物品，选择后即可直接与快捷栏交换。

### 打开菜单

**模式一 — 数字键：**
按下当前所在快捷栏位对应的数字键。
示例：你在第 3 格，按下 `3`，SwapColumn 就会在第 3 格上方打开。

**模式二 — 自定义按键：**
按下你设置的 SwapColumn 按键（默认未绑定）。
在 **选项 → 控制 → SwapColumn** 中设置。

![按键设置位置](docs/keybind.png)

### 选择物品

**点击模式：**
- 模式一：按 `1`–`4` 选择行（`1` = 当前快捷栏物品，取消交换）
- 模式二：按 `1`–`3` 选择行，再次按 SwapColumn 键取消

**滚轮模式：**
按住开启键并滚动滚轮高亮某一行，松开确认交换。

---

## 配置

配置文件位于 `.minecraft/config/swapcolumn.json`，首次启动时自动生成。

```json
{
  "enableCycling": false,
  "enableSlotTexture": true
}
```

| 选项 | 默认值 | 说明 |
|---|---|---|
| `enableCycling` | `false` | 滚动时从顶部/底部循环。注意：首次滚动操作时向下滚动始终会循环，不受此设置影响。 |
| `enableSlotTexture` | `true` | 使用原版快捷栏纹理渲染栏位背景。若纹理包导致显示异常可关闭。 |

### Mod Menu 支持

安装了 [Mod Menu](https://modrinth.com/mod/modmenu) 后，可在模组列表中直接切换选项，立即生效。Mod Menu 为可选依赖，未安装时直接编辑配置文件即可（文件修改在下一次启动游戏时生效）。

![Mod Menu 配置界面](docs/modmenu_config.png)

---

## 兼容性

- Minecraft: `26.1.2`
- Fabric Loader: `>= 0.19.3`
- Fabric API: 必需
- Java: `>= 25`

---

## 许可证

MIT — 见 [LICENSE](LICENSE)。
