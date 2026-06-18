# SwapColumn

A Minecraft Fabric mod that lets you swap items between your hotbar and the inventory slots directly above it — without opening your inventory.

![SwapColumn in action](docs/icon.png)

## How It Works

SwapColumn adds a **vertical swap column** above any hotbar slot. When triggered, a small menu appears showing the items stacked above your selected slot. Choose one and it swaps instantly into your hotbar.

### Opening the Menu

**Mode 1 — Number Key:**
Press the number key of the hotbar slot you are *already on*.
Example: you are on slot 3, press `3` — the SwapColumn opens above slot 3.

**Mode 2 — Keybind:**
Press your configured SwapColumn key (unbound by default).
Set it under **Options → Controls → SwapColumn**.

![Keybind location](docs/keybind.png)

### Selecting an Item

**Click mode:**
- Mode 1: press `1`–`4` to select a row (`1` = current hotbar item, cancels the swap)
- Mode 2: press `1`–`3` to select a row, press the SwapColumn key again to cancel

**Scroll mode:**
Hold the opening key and scroll to highlight a row, then release to confirm.

---

## Configuration

The config file is located at `.minecraft/config/swapcolumn.json` and is created automatically on first launch.

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

### Mod Menu Support

With [Mod Menu](https://modrinth.com/mod/modmenu) installed, both options can be toggled in-game from the mod list — changes apply immediately. Mod Menu is optional: without it, edit the config file directly (file edits take effect on the next game start).

![SwapColumn configuration screen in Mod Menu, showing the Cycling and Slot Texture toggles](docs/modmenu_config.png)

---

## Compatibility

- Minecraft: `26.2`
- Fabric Loader: `0.19.3+`
- Fabric API: required

---

## License

MIT — see [LICENSE](LICENSE).