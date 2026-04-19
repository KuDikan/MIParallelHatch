package icu.kudikan.miparallelhatch.machines.guicomponents;

import aztech.modern_industrialization.machines.gui.GuiComponentServer;
import icu.kudikan.miparallelhatch.MiParallelHatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public class ParallelMachineStatusGui implements GuiComponentServer<Unit, ParallelMachineStatusGui.Data> {
    public static final Type<Unit, Data> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MiParallelHatch.MODID, "parallel_machine_status"),
            StreamCodec.unit(Unit.INSTANCE),
            Data.STREAM_CODEC);

    private final IntSupplier availableParallelSupplier;
    private final IntSupplier activeParallelSupplier;
    private final DoubleSupplier energyFactorSupplier;

    public ParallelMachineStatusGui(IntSupplier availableParallelSupplier, IntSupplier activeParallelSupplier,
                                    DoubleSupplier energyFactorSupplier) {
        this.availableParallelSupplier = availableParallelSupplier;
        this.activeParallelSupplier = activeParallelSupplier;
        this.energyFactorSupplier = energyFactorSupplier;
    }

    @Override
    public Unit getParams() {
        return Unit.INSTANCE;
    }

    @Override
    public Data extractData() {
        return new Data(
                availableParallelSupplier.getAsInt(),
                activeParallelSupplier.getAsInt(),
                energyFactorSupplier.getAsDouble());
    }

    @Override
    public Type<Unit, Data> getType() {
        return TYPE;
    }

    public record Data(int availableParallel, int activeParallel, double energyFactor) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Data::availableParallel,
                ByteBufCodecs.VAR_INT,
                Data::activeParallel,
                ByteBufCodecs.DOUBLE,
                Data::energyFactor,
                Data::new);
    }
}
