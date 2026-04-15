package tapm.swapbar.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SwapBarClient implements ClientModInitializer {

    public static final String MOD_ID = "swapbar";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static KeyMapping swapKeyMapping;

    @Override
    public void onInitializeClient() {

        LOGGER.info("SwapBar client initializing...");

        KeyMapping.Category swapbarCategory =
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath("swapbar", "category"));

        swapKeyMapping = new KeyMapping(
                "key.swapbar.swap_menu",      // translation key for the keybind name
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,        // unbound by default
                swapbarCategory
        );

        KeyMappingHelper.registerKeyMapping(swapKeyMapping);

        SwapBarConfig.get();

        // Close the Menu if another pops up
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (SwapBarState.isActive() && mc.gui.screen() != null) {
                SwapBarState.close();
            }
        });

        LOGGER.info("SwapBar client initialized!");
    }
}
