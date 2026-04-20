package icu.kudikan.miparallelhatch;


import aztech.modern_industrialization.client.machines.GuiComponentsClient;
import aztech.modern_industrialization.config.MIStartupConfig;
import aztech.modern_industrialization.misc.runtime_datagen.RuntimeDataGen;
import icu.kudikan.miparallelhatch.client.machines.guicomponents.ParallelControlGuiClient;
import icu.kudikan.miparallelhatch.client.machines.guicomponents.ParallelMachineStatusGuiClient;
import icu.kudikan.miparallelhatch.datagen.DatagenClient;
import icu.kudikan.miparallelhatch.datagen.DatagenServer;
import icu.kudikan.miparallelhatch.machines.guicomponents.ParallelControlGui;
import icu.kudikan.miparallelhatch.machines.guicomponents.ParallelMachineStatusGui;
import net.minecraft.server.packs.PackType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.Objects;

@Mod(value = MiParallelHatch.MODID, dist = Dist.CLIENT)
public class MiParallelHatchClient {
    public MiParallelHatchClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        var modBus = ModLoadingContext.get().getActiveContainer().getEventBus();
        Objects.requireNonNull(modBus);

        GuiComponentsClient.register(ParallelControlGui.TYPE, ParallelControlGuiClient::new);
        GuiComponentsClient.register(ParallelMachineStatusGui.TYPE, ParallelMachineStatusGuiClient::new);

        modBus.addListener(GatherDataEvent.class, event -> {
            DatagenClient.configure(
                    event.getGenerator(),
                    event.getExistingFileHelper(),
                    event.getLookupProvider(),
                    event.includeServer(),
                    false);
        });

        if (true) {
            modBus.addListener(AddPackFindersEvent.class, event -> {
                if (event.getPackType() == PackType.CLIENT_RESOURCES) {
                    RuntimeDataGen.run(DatagenClient::configure, DatagenServer::configure);
                }
            });
        }
    }
}
