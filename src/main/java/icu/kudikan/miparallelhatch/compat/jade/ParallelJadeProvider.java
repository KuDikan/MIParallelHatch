package icu.kudikan.miparallelhatch.compat.jade;

import aztech.modern_industrialization.api.machine.holder.CrafterComponentHolder;
import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import icu.kudikan.miparallelhatch.MiParallelHatch;
import icu.kudikan.miparallelhatch.api.machine.IMultiblockMachineParallelData;
import icu.kudikan.miparallelhatch.api.machine.IParallelCrafterComponent;
import icu.kudikan.miparallelhatch.machines.blockentities.hatches.ParallelHatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class ParallelJadeProvider implements IWailaPlugin, IServerDataProvider<BlockAccessor> {
    private static final String HATCH_CURRENT = "ParallelHatchCurrent";
    private static final String HATCH_MAX = "ParallelHatchMax";
    private static final String MACHINE_AVAILABLE = "MachineAvailableParallel";
    private static final String MACHINE_ACTIVE = "MachineActiveParallel";
    private static final String MACHINE_ENERGY_FACTOR = "MachineEnergyFactor";

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(this, ParallelHatch.class);
        registration.registerBlockDataProvider(this, MachineBlockEntity.class);
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(MiParallelHatch.MODID, "parallel_status");
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (accessor.getTarget() instanceof ParallelHatch hatch) {
            data.putInt(HATCH_CURRENT, hatch.getParallelCount());
            data.putInt(HATCH_MAX, hatch.getMaxParallel());
        }

        if (accessor.getTarget() instanceof CrafterComponentHolder holder && accessor.getTarget() instanceof IMultiblockMachineParallelData parallelData) {
            IParallelCrafterComponent parallelCrafter = (IParallelCrafterComponent) holder.getCrafterComponent();
            data.putInt(MACHINE_AVAILABLE, parallelData.miParallelHatch$getParallelCount());
            data.putInt(MACHINE_ACTIVE, parallelCrafter.miParallelHatch$getActiveParallelRecipes());
            data.putDouble(MACHINE_ENERGY_FACTOR, parallelCrafter.miParallelHatch$getCurrentEnergyFactor());
        }
    }
}
