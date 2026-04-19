package icu.kudikan.miparallelhatch.client.machines.guicomponents;

import aztech.modern_industrialization.client.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.client.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.client.machines.gui.MachineScreen;
import icu.kudikan.miparallelhatch.machines.guicomponents.ParallelControlGui;
import icu.kudikan.miparallelhatch.network.SetParallelCountPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ParallelControlGuiClient extends GuiComponentClient<Unit, ParallelControlGui.Data> {
    public ParallelControlGuiClient(Unit params, ParallelControlGui.Data data) {
        super(params, data);
    }

    @Override
    public @NotNull ClientComponentRenderer createRenderer(@NotNull MachineScreen machineScreen) {
        return new Renderer();
    }

    public class Renderer implements ClientComponentRenderer {
        private static final int BUTTON_Y = 36;
        private static final int LEFT_BUTTON_X = 10;
        private static final int RIGHT_BUTTON_X = 94;

        @Override
        public void renderBackground(GuiGraphics guiGraphics, int x, int y) {
            Font font = Minecraft.getInstance().font;
            guiGraphics.drawString(font, Component.translatable("text.miparallelhatch.maxParallelDisplay", Component.literal(String.valueOf(data.maxParallel()))), x + 10, y + 23, 0x000000, false);

            drawCenteredString(guiGraphics, font,
                    Component.translatable("text.miparallelhatch.currentParallelDisplay", data.parallelCount()),
                    x + 58,
                    y + 39,
                    0x000000);

        }

        public void drawCenteredString(GuiGraphics guiGraphics, Font font, Component text, int x, int y, int color) {
            FormattedCharSequence formattedcharsequence = text.getVisualOrderText();
            guiGraphics.drawString(font, formattedcharsequence, x - font.width(formattedcharsequence) / 2, y, color, false);
        }

        @Override
        public void addButtons(ButtonContainer container) {
            container.addButton(LEFT_BUTTON_X, BUTTON_Y, 12, 12, syncId -> {
                        int stepValue = getStepValue();

                        int newValue = Math.clamp(data.parallelCount() - stepValue, 1, data.maxParallel());
                        if (newValue != data.parallelCount()) {
                            data = new ParallelControlGui.Data(newValue, data.maxParallel());
                            new SetParallelCountPacket(syncId, newValue).sendToServer();
                        }
                    },
                    () -> List.of(Component.translatable("text.miparallelhatch.decreaseParallel")),
                    (screen, button, guiGraphics, mouseX, mouseY, delta) -> {
                        if (data.parallelCount() > 1) {
                            screen.blitButtonSmall(button, guiGraphics, 174, 58);
                        } else {
                            screen.blitButtonNoHighlight(button, guiGraphics, 174, 70);
                        }
                    });

            container.addButton(RIGHT_BUTTON_X, BUTTON_Y, 12, 12, syncId -> {
                        int stepValue = getStepValue();

                        int newValue = Math.clamp(data.parallelCount() + stepValue, 1, data.maxParallel());
                        if (newValue != data.parallelCount()) {
                            data = new ParallelControlGui.Data(newValue, data.maxParallel());
                            new SetParallelCountPacket(syncId, newValue).sendToServer();
                        }
                    },
                    () -> List.of(Component.translatable("text.miparallelhatch.increaseParallel")),
                    (screen, button, guiGraphics, mouseX, mouseY, delta) -> {
                        if (data.parallelCount() < data.maxParallel()) {
                            screen.blitButtonSmall(button, guiGraphics, 186, 58);
                        } else {
                            screen.blitButtonNoHighlight(button, guiGraphics, 186, 70);
                        }
                    });
        }

        public int getStepValue() {
            if (MachineScreen.hasShiftDown()) {
                return 8;
            } else if (MachineScreen.hasControlDown()) {
                return 32;
            } else if (MachineScreen.hasAltDown()) {
                return 128;
            }
            return 1;
        }
    }
}
