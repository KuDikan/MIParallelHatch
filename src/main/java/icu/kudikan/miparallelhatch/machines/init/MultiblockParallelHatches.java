package icu.kudikan.miparallelhatch.machines.init;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.datagen.model.MachineModelProperties;
import aztech.modern_industrialization.datagen.model.MachineModelsToGenerate;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.init.MachineDefinition;
import aztech.modern_industrialization.machines.init.MachineRegistrationHelper;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import icu.kudikan.miparallelhatch.MiParallelHatchConfig;
import icu.kudikan.miparallelhatch.machines.blockentities.hatches.ParallelHatch;

public class MultiblockParallelHatches {
    public static final HatchType PARALLEL_CONTROL = HatchTypes.register(MI.id("parallel_control"), MI.id("parallel_control_hatch"));
    public static final HatchType PARALLEL_FORBIDDEN = HatchTypes.register(MI.id("forbidden_parallel"), MI.id("forbidden_parallel_control"));

    public static MachineDefinition<ParallelHatch> registerParallelHatches(String englishPrefix, String prefix, MachineCasing casing, int parallelCount) {
        String machine = prefix + "_parallel_control_hatch";
        String englishName = englishPrefix + " Parallel Control Hatch";
        var def = MachineRegistrationHelper.registerMachine(englishName, machine,
                bet -> new ParallelHatch(bet, new MachineGuiParameters.Builder(machine, false).build(), parallelCount));

        var model = new MachineModelProperties.Builder(casing);
        model.addOverlay("side", MI.id("block/machines/hatch_parallel/overlay_side"));
        MachineModelsToGenerate.register(machine, model.build());
        return def;
    }

    public static void init() {
        final MachineDefinition<ParallelHatch> BRONZE_PARALLEL = registerParallelHatches("Bronze", "bronze", MachineCasings.BRONZE, MiParallelHatchConfig.INSTANCE.bronzeParallelCount.getAsInt());
        final MachineDefinition<ParallelHatch> STEEL_PARALLEL = registerParallelHatches("Steel", "steel", MachineCasings.STEEL, MiParallelHatchConfig.INSTANCE.steelParallelCount.getAsInt());
        final MachineDefinition<ParallelHatch> BASIC_PARALLEL = registerParallelHatches("Basic", "basic", CableTier.LV.casing, MiParallelHatchConfig.INSTANCE.basicParallelCount.getAsInt());
        final MachineDefinition<ParallelHatch> ADVANCED_PARALLEL = registerParallelHatches("Advanced", "advanced", CableTier.MV.casing, MiParallelHatchConfig.INSTANCE.advancedParallelCount.getAsInt());
        final MachineDefinition<ParallelHatch> TURBO_PARALLEL = registerParallelHatches("Turbo", "turbo", CableTier.HV.casing, MiParallelHatchConfig.INSTANCE.turboParallelCount.getAsInt());
        final MachineDefinition<ParallelHatch> HIGHLY_ADVANCED_PARALLEL = registerParallelHatches("Highly Advanced", "highly_advanced", CableTier.EV.casing, MiParallelHatchConfig.INSTANCE.highlyAdvancedParallelCount.getAsInt());
        final MachineDefinition<ParallelHatch> QUANTUM_PARALLEL = registerParallelHatches("Quantum", "quantum", CableTier.SUPERCONDUCTOR.casing, MiParallelHatchConfig.INSTANCE.quantumParallelCount.getAsInt());
    }

}
