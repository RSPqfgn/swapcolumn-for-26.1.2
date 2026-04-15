package tapm.swapbar.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class SwapBarRenderer {

    // Vanilla hotbar texture dimensions
    private static final int HB_WIDTH      = 182;
    private static final int HB_HEIGHT     = 22;
    private static final int HB_SLOT_WIDTH = 20;
    private static final int HB_START      = 1;  // left margin inside hotbar texture

    // Slots overlap by 2px vertically so the borders share a pixel — this is the center-to-center distance
    private static final int SLOT_STRIDE   = 20; // HB_SLOT_HEIGHT(22) - 2

    // 1px border on each side of the slot texture
    private static final int SLOT_BORDER   = 1;

    // Standard Minecraft item render size
    private static final int ITEM_SIZE     = 16;

    // Vanilla hotbar selection texture dimensions (no bottom outline — vanilla never shows it)
    private static final int HB_SL_WIDTH   = 24;
    private static final int HB_SL_HEIGHT  = 23;

    private static final SwapBarConfig config = SwapBarConfig.get();

    // Entry point — called from InGameHudMixin at TAIL of extractItemHotbar
    public static void render(GuiGraphicsExtractor gfx) {
        if (!SwapBarState.isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        gfx.pose().pushMatrix();
        renderVHotbar(gfx, mc);
        gfx.pose().popMatrix();
    }

    private static void renderVHotbar(GuiGraphicsExtractor gfx, Minecraft mc) {
        int scaledW    = gfx.guiWidth();
        int scaledH    = gfx.guiHeight();

        int hSlot      = SwapBarState.getHotbarSlot();
        int menuHeight = SwapBarState.getMenuHeight();  // highest visible row (1–3)
        int selIdx     = getSelectedIndex();             // selected row (0 = hotbar slot)
        Font font      = mc.font;

        // Seamless attachment: bottom of menu aligns with top of hotbar
        int hotbarX    = (scaledW - HB_WIDTH) / 2;
        int hotbarY    = scaledH - HB_HEIGHT;
        int slotStartX = hotbarX + hSlot * HB_SLOT_WIDTH;

        if (config.enableSlotTexture) renderSlots(gfx, menuHeight, hotbarY, slotStartX);

        renderSelection(gfx, hotbarY, selIdx, slotStartX);

        // caller guarantees mc.player != null
        renderItems(gfx, mc, menuHeight, hotbarY, slotStartX, font);
    }

    // Renders all items in the column (V=0 is the hotbar slot, V=1..menuHeight above)
    private static void renderItems(GuiGraphicsExtractor gfx, Minecraft mc,
                                    int menuHeight, int hotbarY, int slotStartX, Font font) {
        Inventory inv = mc.player.getInventory();

        for (int v = 0; v <= menuHeight; v++) {
            int slotY   = hotbarY - v * SLOT_STRIDE;
            int invIdx  = SwapBarState.getInventorySlot(v);
            ItemStack stack = inv.getItem(invIdx);

            if (!stack.isEmpty()) {
                int itemX = slotStartX + SLOT_BORDER + (HB_SLOT_WIDTH - ITEM_SIZE) / 2;
                int itemY = slotY + (HB_HEIGHT - ITEM_SIZE) / 2;
                gfx.item(mc.player, stack, itemX, itemY, 0);
                gfx.itemDecorations(font, stack, itemX, itemY);
            }
        }
    }

    // Renders the selection highlight at the given row.
    // The vanilla selection texture has no bottom outline (never visible on the actual hotbar).
    // We draw it manually as a 1px-high blit directly below the texture.
    private static void renderSelection(GuiGraphicsExtractor gfx, int hotbarY,
                                        int selIdx, int slotStartX) {
        int slotY = hotbarY - selIdx * SLOT_STRIDE;

        // Main selection sprite (overhangs 1px on all sides except bottom)
        gfx.blitSprite(RenderPipelines.GUI_TEXTURED,
                Identifier.withDefaultNamespace("hud/hotbar_selection"),
                HB_SL_WIDTH, HB_SL_HEIGHT,
                0, 0,
                slotStartX - 1, slotY - 1,
                HB_SL_WIDTH, HB_SL_HEIGHT,
                -1);

        // Bottom outline: 1px strip directly below the sprite
        gfx.blitSprite(RenderPipelines.GUI_TEXTURED,
                Identifier.withDefaultNamespace("hud/hotbar_selection"),
                HB_SL_WIDTH, HB_SL_HEIGHT,
                0, 0,
                slotStartX - 1, slotY + HB_SL_HEIGHT - 1,
                HB_SL_WIDTH, 1,
                -1);
    }

    // Renders slot backgrounds for rows V=1..menuHeight.
    // Each slot is scissored from the horizontal hotbar texture.
    // Left/right border lines are drawn separately; top/bottom borders are handled by overlap or scissor.
    private static void renderSlots(GuiGraphicsExtractor gfx, int menuHeight,
                                    int hotbarY, int slotStartX) {
        for (int v = 1; v <= menuHeight; v++) {
            int slotY = hotbarY - v * SLOT_STRIDE;

            // In keybind mode the menu always shows slots 1–3 in order (like a fixed palette).
            // In number mode the column is rooted at the active hotbar slot, so we shift by one extra slot.
            int offsetX = SwapBarState.isOpenedWithKeybind()
                    ? HB_START + HB_SLOT_WIDTH * (v - 1)
                    : HB_START + HB_SLOT_WIDTH * v;

            // Clip to slot area, cutting off the bottom border line (shared with row below)
            gfx.enableScissor(
                    slotStartX + SLOT_BORDER,
                    slotY,
                    slotStartX + SLOT_BORDER + HB_SLOT_WIDTH,
                    slotY + HB_HEIGHT - 1);  // -1: remove bottom border, upper row's top covers it

            gfx.blitSprite(RenderPipelines.GUI_TEXTURED,
                    Identifier.withDefaultNamespace("hud/hotbar"),
                    HB_WIDTH, HB_HEIGHT,
                    offsetX, 0,
                    slotStartX + SLOT_BORDER, slotY,
                    HB_SLOT_WIDTH, HB_HEIGHT,
                    -1);

            gfx.disableScissor();

            // Left and right border lines (1px wide, from hotbar texture edge pixels).
            // First row (v=1): shorten by 2px to avoid cutting into the hotbar below.
            int borderHeight = HB_HEIGHT - (v == 1 ? 2 : 0);

            gfx.blitSprite(RenderPipelines.GUI_TEXTURED,  // left border
                    Identifier.withDefaultNamespace("hud/hotbar"),
                    HB_WIDTH, HB_HEIGHT,
                    0, 0,
                    slotStartX, slotY,
                    1, borderHeight, -1);

            gfx.blitSprite(RenderPipelines.GUI_TEXTURED,  // right border
                    Identifier.withDefaultNamespace("hud/hotbar"),
                    HB_WIDTH, HB_HEIGHT,
                    HB_WIDTH - 1, 0,
                    slotStartX + HB_SLOT_WIDTH + SLOT_BORDER, slotY,
                    1, borderHeight, -1);
        }
    }

    // Returns the currently highlighted row index.
    private static int getSelectedIndex() {
        if (SwapBarState.getPhase() == SwapBarState.Phase.SCROLL_MODE) {
            int i = SwapBarState.getScrollIndex();
            return i == -1 ? 0 : i;
        }
        return 0;
    }
}