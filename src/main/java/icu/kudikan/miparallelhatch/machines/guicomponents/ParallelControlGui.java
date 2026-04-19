package icu.kudikan.miparallelhatch.machines.guicomponents;

import aztech.modern_industrialization.machines.gui.GuiComponentServer;
import icu.kudikan.miparallelhatch.MiParallelHatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntSupplier;

public class ParallelControlGui implements GuiComponentServer<Unit, ParallelControlGui.Data> {
    public static final GuiComponentServer.Type<Unit, Data> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MiParallelHatch.MODID, "parallel_control_gui"),
            StreamCodec.unit(Unit.INSTANCE),
            Data.STREAM_CODEC);

    private final IntSupplier parallelCount;
    private final int maxParallel;

    public ParallelControlGui(IntSupplier parallelCount, int maxParallel) {
        this.parallelCount = parallelCount;
        this.maxParallel = maxParallel;
    }

    @Override
    public Unit getParams() {
        return Unit.INSTANCE;
    }

    @Override
    public Data extractData() {
        return new Data(parallelCount.getAsInt(), maxParallel);
    }

    @Override
    public @NotNull Type<Unit, Data> getType() {
        return TYPE;
    }

    public record Data(int parallelCount, int maxParallel) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC =
                StreamCodec.composite(ByteBufCodecs.VAR_INT, Data::parallelCount, ByteBufCodecs.VAR_INT, Data::maxParallel, Data::new);

    }
}
