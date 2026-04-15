package tapm.swapbar.mixin;

import tapm.swapbar.client.SwapBarClient;
import tapm.swapbar.client.SwapBarState;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardMixin {

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void swapbar$onKeyPress(long window, int action, KeyEvent event,
                                    CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gui.screen() != null) return;

        if (SwapBarState.isActive()) {
            if (swapbar$handleActiveKey(mc, action, event)) ci.cancel();
        } else if (action == GLFW.GLFW_PRESS) {
            if (swapbar$handleIdlePress(mc, event)) ci.cancel();
        }
    }

    // Returns false for irrelevant keys so they pass through normally (movement, chat, etc.)
    // Returns true for any swap-related key, consuming the event.
    @Unique
    private boolean swapbar$handleActiveKey(Minecraft mc, int action, KeyEvent event) {
        KeyMapping swapKey = SwapBarClient.swapKeyMapping;
        boolean isSwapKey  = swapKey != null && swapKey.matches(event);
        int hotbarIdx      = swapbar$hotbarIndex(mc, event);

        if (!isSwapKey && hotbarIdx < 0) return false;

        switch (action) {
            case GLFW.GLFW_PRESS   -> swapbar$pressWhileActive(isSwapKey, hotbarIdx);
            case GLFW.GLFW_RELEASE -> swapbar$releaseWhileActive(isSwapKey, hotbarIdx);
            // GLFW_REPEAT is consumed silently
        }
        return true;
    }

    @Unique
    private boolean swapbar$handleIdlePress(Minecraft mc, KeyEvent event) {
        KeyMapping swapKey = SwapBarClient.swapKeyMapping;
        int hotbarIdx      = swapbar$hotbarIndex(mc, event);

        // Mode 2: custom keybind → open menu for current hotbar slot
        if (swapKey != null && swapKey.matches(event)) {
            return SwapBarState.openMenu(mc.player.getInventory().getSelectedSlot(), true);
        }

        // Mode 1: pressed the number key of the already-selected slot
        if (hotbarIdx >= 0 && hotbarIdx == mc.player.getInventory().getSelectedSlot()) {
            return SwapBarState.openMenu(hotbarIdx, false);
        }

        return false;
    }

    @Unique
    private void swapbar$pressWhileActive(boolean isSwapKey, int hotbarIdx) {
        if (SwapBarState.isOpenedWithKeybind()) {
            if (isSwapKey) {
                SwapBarState.close(); // keybind cancels in keybind-mode
            } else {
                swapbar$keybindMenuInput(hotbarIdx);
            }
        } else {
            swapbar$numberMenuInput(hotbarIdx);
        }
    }

    // Number mode: key 1 = V=0 (cancel), keys 2–4 = V=1–3
    @Unique
    private void swapbar$numberMenuInput(int hotbarIdx) {
        if (hotbarIdx < 0) return; // swap key pressed in number-mode — consume silently
        if (hotbarIdx == 0) {
            SwapBarState.close(); // V=0 = current hotbar slot = cancel
        } else if (hotbarIdx <= 3 && hotbarIdx <= SwapBarState.getMenuHeight()) {
            SwapBarState.executeSwap(hotbarIdx);
        } else {
            SwapBarState.close();
        }
    }

    // Keybind mode: keys 1–3 map to V=1–3. Cancelling via swap key is handled before this call.
    @Unique
    private void swapbar$keybindMenuInput(int hotbarIdx) {
        int v = hotbarIdx + 1; // key 1 V=1, key 2 V=2, key 3 V=3
        if (v <= 3 && v <= SwapBarState.getMenuHeight()) {
            SwapBarState.executeSwap(v);
        } else {
            SwapBarState.close();
        }
    }

    @Unique
    private void swapbar$releaseWhileActive(boolean isSwapKey, int hotbarIdx) {
        SwapBarState.Phase phase = SwapBarState.getPhase();
        boolean isOpeningKey     = swapbar$isOpeningKey(isSwapKey, hotbarIdx);

        if (phase == SwapBarState.Phase.KEY_HELD && isOpeningKey) {
            SwapBarState.transitionToMenuWaiting();
        } else if (phase == SwapBarState.Phase.SCROLL_MODE && isOpeningKey) {
            SwapBarState.executeSwap(SwapBarState.getScrollIndex());
        }
    }

    @Unique
    private boolean swapbar$isOpeningKey(boolean isSwapKey, int hotbarIdx) {
        return SwapBarState.isOpenedWithKeybind()
                ? isSwapKey
                : hotbarIdx == SwapBarState.getHotbarSlot();
    }

    @Unique
    private int swapbar$hotbarIndex(Minecraft mc, KeyEvent event) {
        KeyMapping[] slots = mc.options.keyHotbarSlots;
        for (int i = 0; i < slots.length; i++) {
            if (slots[i].matches(event)) return i;
        }
        return -1;
    }
}