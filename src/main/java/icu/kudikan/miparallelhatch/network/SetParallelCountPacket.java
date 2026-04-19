package icu.kudikan.miparallelhatch.network;

import aztech.modern_industrialization.machines.gui.MachineMenuServer;
import icu.kudikan.miparallelhatch.machines.blockentities.hatches.ParallelHatch;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetParallelCountPacket(int syncId, int parallelCount) implements CustomPacketPayload {
    public static final Type<SetParallelCountPacket> TYPE = new Type<>(MiParallelHatchPackets.id("set_parallel_count"));
    public static final StreamCodec<ByteBuf, SetParallelCountPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SetParallelCountPacket::syncId,
            ByteBufCodecs.VAR_INT,
            SetParallelCountPacket::parallelCount,
            SetParallelCountPacket::new);

    public static void handle(SetParallelCountPacket packet, IPayloadContext context) {
        if (!(context.player().containerMenu instanceof MachineMenuServer machineMenu)) {
            return;
        }
        if (machineMenu.containerId != packet.syncId()) {
            return;
        }
        if (machineMenu.blockEntity instanceof ParallelHatch parallelHatch) {
            parallelHatch.setParallelCount(packet.parallelCount());
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void sendToServer() {
        PacketDistributor.sendToServer(this);
    }
}
