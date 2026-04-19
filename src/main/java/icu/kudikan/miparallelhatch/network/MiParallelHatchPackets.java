package icu.kudikan.miparallelhatch.network;

import icu.kudikan.miparallelhatch.MiParallelHatch;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class MiParallelHatchPackets {
    private MiParallelHatchPackets() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MiParallelHatch.MODID, path);
    }

    public static void init(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(SetParallelCountPacket.TYPE, SetParallelCountPacket.STREAM_CODEC, SetParallelCountPacket::handle);
    }
}
