package icu.kudikan.miparallelhatch;

import icu.kudikan.miparallelhatch.util.ConfigBuilder;
import net.neoforged.neoforge.common.ModConfigSpec;


public final class MiParallelHatchConfig {
    public static final MiParallelHatchConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        var builder = new ConfigBuilder();
        INSTANCE = new MiParallelHatchConfig(builder);
        SPEC = builder.build();
    }

    public ModConfigSpec.IntValue bronzeParallelCount;
    public ModConfigSpec.IntValue steelParallelCount;
    public ModConfigSpec.IntValue basicParallelCount;
    public ModConfigSpec.IntValue advancedParallelCount;
    public ModConfigSpec.IntValue turboParallelCount;
    public ModConfigSpec.IntValue highlyAdvancedParallelCount;
    public ModConfigSpec.IntValue quantumParallelCount;
    public ModConfigSpec.DoubleValue parallelExtraEnergyMultiplier;

    private MiParallelHatchConfig(ConfigBuilder builder) {
        this.bronzeParallelCount = builder.start("bronzeParallelCount",
                        "Bronze Parallel Count",
                        "MAX parallel processing count for Bronze Parallel Control Hatch")
                .gameRestart()
                .defineInRange("bronzeParallelCount", 2, 1, Integer.MAX_VALUE);

        this.steelParallelCount = builder.start("steelParallelCount",
                        "Steel Parallel Count",
                        "MAX parallel processing count for Steel Parallel Control Hatch")
                .gameRestart()
                .defineInRange("steelParallelCount", 8, 1, Integer.MAX_VALUE);

        this.basicParallelCount = builder.start("basicParallelCount",
                        "Basic Parallel Count",
                        "MAX parallel processing count for Basic Parallel Control Hatch")
                .gameRestart()
                .defineInRange("basicParallelCount", 32, 1, Integer.MAX_VALUE);

        this.advancedParallelCount = builder.start("advancedParallelCount",
                        "Advanced Parallel Count",
                        "MAX parallel processing count for Advanced Parallel Control Hatch")
                .gameRestart()
                .defineInRange("advancedParallelCount", 64, 1, Integer.MAX_VALUE);

        this.turboParallelCount = builder.start("turboParallelCount",
                        "Turbo Parallel Count",
                        "MAX parallel processing count for Turbo Parallel Control Hatch")
                .gameRestart()
                .defineInRange("turboParallelCount", 128, 1, Integer.MAX_VALUE);

        this.highlyAdvancedParallelCount = builder.start("highlyAdvancedParallelCount",
                        "Highly Advanced Parallel Count",
                        "MAX parallel processing count for Highly Advanced Parallel Control Hatch")
                .gameRestart()
                .defineInRange("highlyAdvancedParallelCount", 256, 1, Integer.MAX_VALUE);

        this.quantumParallelCount = builder.start("QuantumParallelCount",
                        "Quantum Parallel Count",
                        "MAX parallel processing count for Quantum Parallel Control Hatch")
                .gameRestart()
                .defineInRange("QuantumParallelCount", 1024, 1, Integer.MAX_VALUE);

        this.parallelExtraEnergyMultiplier = builder.start("parallelExtraEnergyMultiplier",
                        "Parallel Extra Energy Multiplier",
                        "Additional energy multiplier applied for each extra parallel craft.",
                        "Total factor = 1 + (parallelCount - 1) * parallelExtraEnergyMultiplier")
                .gameRestart()
                .defineInRange("parallelExtraEnergyMultiplier", 1.0D, 0.0D, Float.MAX_VALUE);
    }
}
