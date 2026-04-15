# SwapBar

A Minecraft Fabric mod that lets you swap items between your hotbar and the inventory slots directly above it — without opening your inventory.

![SwapBar in action](docs/icon.png)

## How It Works

SwapBar adds a **vertical swap column** above any hotbar slot. When triggered, a small menu appears showing the items stacked above your selected slot. Choose one and it swaps instantly into your hotbar.

### Opening the Menu

**Mode 1 — Number Key:**
Press the number key of the hotbar slot you are *already on*.
Example: you are on slot 3, press `3` — the SwapBar opens above slot 3.

**Mode 2 — Keybind:**
Press your configured SwapBar key (unbound by default).
Set it under **Options → Controls → SwapBar**.

![Keybind location](docs/keybind.png)

### Selecting an Item

**Click mode:**
- Mode 1: press `1`–`4` to select a row (`1` = current hotbar item, cancels the swap)
- Mode 2: press `1`–`3` to select a row, press the SwapBar key again to cancel

**Scroll mode:**
Hold the opening key and scroll to highlight a row, then release to confirm.

---

## Configuration

The config file is located at `.minecraft/config/swapbar.json` and is created automatically on first launch.

```json
{
  "enableCycling": false,
  "enableSlotTexture": true
}
```

| Option | Default | Description |
|---|---|---|
| `enableCycling` | `false` | When scrolling, wrap around from top to bottom and vice versa. Note: scrolling down on the first action always wraps regardless of this setting. |
| `enableSlotTexture` | `true` | Render slot backgrounds using the vanilla hotbar texture. Disable if your texture pack causes visual issues. |

**Note:** Mod Menu support is planned. Until then, changes to the config file require a game restart to take effect.

---

## Compatibility

- Minecraft: `26.2-snapshot-2`
- Fabric Loader: `0.19.1+`
- Fabric API: required

---

## License

MIT — see [LICENSE](LICENSE).