package icu.kudikan.miparallelhatch.machines.blockentities.hatches;

import aztech.modern_industrialization.MITooltips;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import icu.kudikan.miparallelhatch.machines.guicomponents.ParallelControlGui;
import icu.kudikan.miparallelhatch.machines.init.MultiblockParallelHatches;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ParallelHatch extends HatchBlockEntity {
    private static final String PARALLEL_COUNT_NBT_KEY = "parallelCount";
    private static int parallelConfigVersion;

    private final int maxParallel;
    private int parallelCount;

    public ParallelHatch(BEP bep, MachineGuiParameters guiParams, int parallelCount) {
        super(bep, guiParams, OrientationComponent.Params.noFacingNoOutput());
        this.maxParallel = Math.max(parallelCount, 1);
        this.parallelCount = maxParallel;

        registerComponents(new MachineComponent.ServerOnly() {
            @Override
            public void writeNbt(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
                tag.putInt(PARALLEL_COUNT_NBT_KEY, ParallelHatch.this.parallelCount);
            }

            @Override
            public void readNbt(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean isUpgradingMachine) {
                ParallelHatch.this.parallelCount = Math.clamp(tag.getInt(PARALLEL_COUNT_NBT_KEY), 1, ParallelHatch.this.maxParallel);
            }
        });

        registerGuiComponent(new ParallelControlGui(this::getParallelCount, maxParallel));
    }

    public static int getParallelConfigVersion() {
        return parallelConfigVersion;
    }

    private static void bumpParallelConfigVersion() {
        parallelConfigVersion++;
    }

    @Override
    public @NotNull HatchType getHatchType() {
        return MultiblockParallelHatches.PARALLEL_CONTROL;
    }

    @Override
    public boolean upgradesToSteel() {
        return false;
    }

    @Override
    public @NotNull MIInventory getInventory() {
        return MIInventory.EMPTY;
    }

    @Override
    public @NotNull List<Component> getTooltips() {
        return List.of(Component.translatable("text.miparallelhatch.HatchParallelCount",
                        Component.literal(String.valueOf(getMaxParallel())).setStyle(MITooltips.NUMBER_TEXT)
                ).withStyle(MITooltips.DEFAULT_STYLE)
        );
    }

    public int getMaxParallel() {
        return maxParallel;
    }

    public int getParallelCount() {
        return parallelCount;
    }

    public void setParallelCount(int parallelCount) {
        int newParallelCount = Math.clamp(parallelCount, 1, maxParallel);
        if (newParallelCount != this.parallelCount) {
            this.parallelCount = newParallelCount;
            bumpParallelConfigVersion();
            setChanged();
            if (level != null && !level.isClientSide()) {
                sync(false);
            }
        }
    }
}
