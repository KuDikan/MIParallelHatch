package icu.kudikan.miparallelhatch.client.compat.jade;

import aztech.modern_industrialization.machines.MachineBlock;
import icu.kudikan.miparallelhatch.MiParallelHatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class ParallelJadeProvider implements IWailaPlugin, IBlockComponentProvider {
    private static final String HATCH_CURRENT = "ParallelHatchCurrent";
    private static final String HATCH_MAX = "ParallelHatchMax";
    private static final String MACHINE_AVAILABLE = "MachineAvailableParallel";
    private static final String MACHINE_ACTIVE = "MachineActiveParallel";
    private static final String MACHINE_ENERGY_FACTOR = "MachineEnergyFactor";

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(this, MachineBlock.class);
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(MiParallelHatch.MODID, "parallel_status");
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();

        if (data.contains(HATCH_CURRENT) && data.contains(HATCH_MAX)) {
            tooltip.add(Component.translatable("text.miparallelhatch.jadeParallelHatchStatus", data.getInt(HATCH_CURRENT), data.getInt(HATCH_MAX)));
        }

        if (data.contains(MACHINE_AVAILABLE) && data.contains(MACHINE_ACTIVE) && (data.getInt(MACHINE_ACTIVE) > 1 || data.getInt(MACHINE_AVAILABLE) > 1)) {
            if (data.getDouble(MACHINE_ENERGY_FACTOR) != 1.0D){
                tooltip.add(Component.translatable("text.miparallelhatch.jadeMachineParallelStatus", data.getInt(MACHINE_ACTIVE), data.getInt(MACHINE_AVAILABLE), String.format("%.1f", data.getDouble(MACHINE_ENERGY_FACTOR))));
            } else {
                tooltip.add(Component.translatable("text.miparallelhatch.jadeMachineParallelStatusWithoutEnergyFactor", data.getInt(MACHINE_ACTIVE), data.getInt(MACHINE_AVAILABLE)));
            }

        }
    }
}
