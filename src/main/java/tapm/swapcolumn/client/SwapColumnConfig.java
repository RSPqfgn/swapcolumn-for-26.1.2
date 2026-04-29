package tapm.swapcolumn.client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class SwapColumnConfig {

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("swapcolumn.json");

    private static SwapColumnConfig instance;

    // Cycling through vertical hotbar
    // Enable if you like it - remember you can always scroll down to the top on your first scroll action
    public boolean enableCycling = false;

    // Should slots be rendered (using the Hotbar textures)?
    // Disable if the Slots look weird/are incompatible with the loaded Texture Pack
    public boolean enableSlotTexture = true;

    public static SwapColumnConfig get() {
        if (instance == null) {
            instance = new SwapColumnConfig();
            instance.load();
        }
        return instance;
    }

    public void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            if (json.has("enableCycling")) {
                enableCycling = json.get("enableCycling").getAsBoolean();
            }
            if (json.has("enableSlotTexture")) {
                enableSlotTexture = json.get("enableSlotTexture").getAsBoolean();
            }
        } catch (Exception e) {
            SwapColumnClient.LOGGER.warn("Failed to load SwapColumn config, using defaults", e);
        }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            JsonObject json = new JsonObject();
            json.addProperty("enableCycling", enableCycling);
            json.addProperty("enableSlotTexture", enableSlotTexture);

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                writer.write(gson.toJson(json));
            }
        } catch (Exception e) {
            SwapColumnClient.LOGGER.warn("Failed to save SwapColumn config", e);
        }
    }
}