package tapm.swapcolumn.mixin;

import tapm.swapcolumn.client.SwapColumnState;
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
    private void swapcolumn$onScroll(long window, double horizontal, double vertical,
                                  CallbackInfo ci) {
        if (!SwapColumnState.isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        switch (SwapColumnState.getPhase()) {
            case KEY_HELD -> {
                SwapColumnState.transitionToScrollMode();
                applyScroll(vertical);
                ci.cancel();
            }
            case SCROLL_MODE -> {
                applyScroll(vertical);
                ci.cancel();
            }
            case MENU_WAITING -> {
                SwapColumnState.close();
                ci.cancel();
            }
            default -> {}
        }
    }

    @Unique
    private static void applyScroll(double vertical) {
        if (vertical > 0) SwapColumnState.scrollUp();
        else if (vertical < 0) SwapColumnState.scrollDown();
    }
}
