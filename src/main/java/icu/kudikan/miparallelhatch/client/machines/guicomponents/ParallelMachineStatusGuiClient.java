package icu.kudikan.miparallelhatch.client.machines.guicomponents;

import aztech.modern_industrialization.client.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.client.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.client.machines.gui.MachineScreen;
import icu.kudikan.miparallelhatch.machines.guicomponents.ParallelMachineStatusGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;

public class ParallelMachineStatusGuiClient extends GuiComponentClient<Unit, ParallelMachineStatusGui.Data> {
    private static final int TEXT_X = 8;
    private static final int TEXT_Y = 10;
    private static final int LINE_HEIGHT = 11;

    public ParallelMachineStatusGuiClient(Unit params, ParallelMachineStatusGui.Data data) {
        super(params, data);
    }

    @Override
    public @NotNull ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new Renderer();
    }

    private class Renderer implements ClientComponentRenderer {
        @Override
        public void renderBackground(GuiGraphics guiGraphics, int x, int y) {
            Font font = Minecraft.getInstance().font;
            if (data.availableParallel() > 1) {
                guiGraphics.drawString(font,
                        Component.translatable("text.miparallelhatch.machineParallelStatus", data.activeParallel(), data.availableParallel(), String.format("%.1f", data.energyFactor())),
                        x + TEXT_X,
                        y - TEXT_Y,
                        0xFFFFFF,
                        false);
            }
        }
    }
}
