package icu.kudikan.miparallelhatch.datagen;

import icu.kudikan.miparallelhatch.datagen.model.ModelProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class DatagenClient {
    public static void configure(
            DataGenerator gen,
            ExistingFileHelper fileHelper,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            boolean run,
            boolean runtimeDatagen) {
        gen.addProvider(run, new ModelProvider(gen.getPackOutput(), fileHelper));
    }
}
