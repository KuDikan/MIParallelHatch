package icu.kudikan.miparallelhatch;

import aztech.modern_industrialization.config.MIStartupConfig;
import aztech.modern_industrialization.misc.runtime_datagen.RuntimeDataGen;
import icu.kudikan.miparallelhatch.datagen.DatagenServer;
import icu.kudikan.miparallelhatch.machines.init.MultiblockParallelHatches;
import icu.kudikan.miparallelhatch.network.MiParallelHatchPackets;
import net.minecraft.server.packs.PackType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.logging.Logger;

@Mod(MiParallelHatch.MODID)
public class MiParallelHatch {

    public static final String MODID = "miparallelhatch";

    public MiParallelHatch(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        modContainer.registerConfig(ModConfig.Type.STARTUP, MiParallelHatchConfig.SPEC);
        MultiblockParallelHatches.init();
        modEventBus.addListener(RegisterPayloadHandlersEvent.class, MiParallelHatchPackets::init);

        modEventBus.addListener(GatherDataEvent.class, event -> {
            DatagenServer.configure(
                    event.getGenerator(),
                    event.getExistingFileHelper(),
                    event.getLookupProvider(),
                    event.includeServer(),
                    false);
        });

        modEventBus.addListener(AddPackFindersEvent.class, event -> {
            if (dist == Dist.DEDICATED_SERVER && event.getPackType() == PackType.SERVER_DATA
                    && MIStartupConfig.INSTANCE.datagenOnStartup.getAsBoolean()) {
                RuntimeDataGen.run(DatagenServer::configure);
            }
        });
    }
}
