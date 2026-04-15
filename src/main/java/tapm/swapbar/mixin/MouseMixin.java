package tapm.swapbar.mixin;

import tapm.swapbar.client.SwapBarState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void swapbar$onScroll(long window, double horizontal, double vertical,
                                  CallbackInfo ci) {
        if (!SwapBarState.isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gui.screen() != null) return;

        switch (SwapBarState.getPhase()) {
            case KEY_HELD -> {
                SwapBarState.transitionToScrollMode();
                applyScroll(vertical);
                ci.cancel();
            }
            case SCROLL_MODE -> {
                applyScroll(vertical);
                ci.cancel();
            }
            case MENU_WAITING -> {
                SwapBarState.close();
                ci.cancel();
            }
            default -> {}
        }
    }

    @Unique
    private static void applyScroll(double vertical) {
        if (vertical > 0) SwapBarState.scrollUp();
        else if (vertical < 0) SwapBarState.scrollDown();
    }
}
