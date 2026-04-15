package tapm.swapbar.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;


public class SwapBarState {

    public enum Phase {
        IDLE,
        KEY_HELD,
        SCROLL_MODE,
        MENU_WAITING
    }

    private static Phase phase = Phase.IDLE;
    private static int hotbarSlot = 0;

    // -1 so we can detect first Scroll Action and allow cycling downwards once
    private static int scrollIndex = -1;
    private static int menuHeight = 0;
    private static boolean openedWithKeybind = false;

    // Getters
    public static boolean isActive()            { return phase != Phase.IDLE; }
    public static Phase   getPhase()            { return phase; }
    public static int     getHotbarSlot()       { return hotbarSlot; }
    public static int     getScrollIndex()      { return scrollIndex; }
    public static int     getMenuHeight()       { return menuHeight; }
    public static boolean isOpenedWithKeybind() { return openedWithKeybind; }

    static SwapBarConfig config = SwapBarConfig.get();

    // Slot Mapping

    public static int getInventorySlot(int v) {
        return switch (v) {
            case 0 -> hotbarSlot;
            case 1 -> 27 + hotbarSlot;
            case 2 -> 18 + hotbarSlot;
            case 3 ->  9 + hotbarSlot;
            default -> -1;
        };
    }

    public static int getScreenHandlerSlot(int v) {
        return switch (v) {
            case 1 -> 27 + hotbarSlot;
            case 2 -> 18 + hotbarSlot;
            case 3 ->  9 + hotbarSlot;
            default -> -1;
        };
    }
    private static int slotFor(int h, int v) {
        return switch (v) {
            case 0 -> h;
            case 1 -> 27 + h;
            case 2 -> 18 + h;
            case 3 ->  9 + h;
            default -> -1;
        };
    }

    // State Transitions
    public static boolean openMenu(int slot, boolean withKeybind) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;

        Inventory inv = mc.player.getInventory();

        int height = 0;
        for (int v = 3; v >= 1; v--) {
            int invSlot = slotFor(slot, v);
            if (!inv.getItem(invSlot).isEmpty()) {
                height = v;
                break;
            }
        }
        if (height == 0) return false;

        hotbarSlot        = slot;
        openedWithKeybind = withKeybind;
        menuHeight        = height;
        scrollIndex       = -1;
        phase             = Phase.KEY_HELD;
        return true;
    }

    public static void transitionToMenuWaiting() { phase = Phase.MENU_WAITING; }
    public static void transitionToScrollMode()  { phase = Phase.SCROLL_MODE; }

    public static void scrollUp() {
        if (scrollIndex == -1 )  scrollIndex = 1;
        else if (scrollIndex < menuHeight) scrollIndex++;
        else if (config.enableCycling) scrollIndex = 0;
    }

    public static void scrollDown() {
        if (scrollIndex == -1 )  scrollIndex = menuHeight;
        else if (scrollIndex > 0) scrollIndex--;
        else if (config.enableCycling) scrollIndex = menuHeight;
    }

    public static void close() {
        phase       = Phase.IDLE;
        hotbarSlot  = 0;
        scrollIndex = -1;
        menuHeight  = 0;
    }

    public static void executeSwap(int v) {
        if (v <= 0) { close(); return; }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gameMode == null) { close(); return; }

        int screenSlot = getScreenHandlerSlot(v);
        if (screenSlot < 0) { close(); return; }

        mc.gameMode.handleContainerInput(
                mc.player.inventoryMenu.containerId,
                screenSlot,
                hotbarSlot,
                ContainerInput.SWAP,
                mc.player
        );
        close();
    }
}
