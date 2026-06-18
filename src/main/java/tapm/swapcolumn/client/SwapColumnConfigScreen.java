package tapm.swapcolumn.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;


public class SwapColumnConfigScreen extends Screen {

    private static final int WIDTH = 220;

    private final Screen parent;

    private boolean enableCycling;
    private boolean enableSlotTexture;

    public SwapColumnConfigScreen(Screen parent) {
        super(Component.translatable("screen.swapcolumn.config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SwapColumnConfig config = SwapColumnConfig.get();
        this.enableCycling = config.enableCycling;
        this.enableSlotTexture = config.enableSlotTexture;

        int x = (this.width - WIDTH) / 2;
        int y = this.height / 2 - 34;

        StringWidget title = new StringWidget(this.title, this.font);
        title.setX((this.width - title.getWidth()) / 2);
        title.setY(y - 30);
        this.addRenderableWidget(title);

        CycleButton<Boolean> cycling = CycleButton.booleanBuilder(
                        Component.translatable("option.swapcolumn.cycling.enabled").withStyle(ChatFormatting.GREEN),
                        Component.translatable("option.swapcolumn.cycling.disabled").withStyle(ChatFormatting.RED),
                        enableCycling)
                .withTooltip(_ -> Tooltip.create(Component.translatable("option.swapcolumn.cycling.desc")))
                .create(x, y, WIDTH, 20, Component.translatable("option.swapcolumn.cycling"),
                        (_, value) -> enableCycling = value);
        this.addRenderableWidget(cycling);

        CycleButton<Boolean> slotTexture = CycleButton.booleanBuilder(
                        Component.translatable("option.swapcolumn.slot_texture.shown").withStyle(ChatFormatting.GREEN),
                        Component.translatable("option.swapcolumn.slot_texture.hidden").withStyle(ChatFormatting.RED),
                        enableSlotTexture)
                .withTooltip(_ -> Tooltip.create(Component.translatable("option.swapcolumn.slot_texture.desc")))
                .create(x, y + 24, WIDTH, 20, Component.translatable("option.swapcolumn.slot_texture"),
                        (_, value) -> enableSlotTexture = value);
        this.addRenderableWidget(slotTexture);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, _ -> this.onDone())
                .bounds((this.width - 200) / 2, y + 60, 200, 20).build());
    }

    public void onDone() {
        SwapColumnConfig config = SwapColumnConfig.get();
        config.enableCycling = enableCycling;
        config.enableSlotTexture = enableSlotTexture;
        config.save();
        this.onClose();
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }
}
