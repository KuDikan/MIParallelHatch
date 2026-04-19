package icu.kudikan.miparallelhatch.datagen.model;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.datagen.model.BaseModelProvider;
import aztech.modern_industrialization.datagen.model.MachineModelsToGenerate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModelProvider extends BaseModelProvider {
    public ModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (var entry : MachineModelsToGenerate.props.entrySet()) {
            if (entry.getKey().contains("parallel")) {
                simpleBlockWithItem(BuiltInRegistries.BLOCK.get(MI.id(entry.getKey())), models()
                        .getBuilder(entry.getKey())
                        .customLoader((bmb, exFile) -> new MachineModelBuilder<>(entry.getValue(), bmb, exFile))
                        .end());
            }
        }
    }
}
