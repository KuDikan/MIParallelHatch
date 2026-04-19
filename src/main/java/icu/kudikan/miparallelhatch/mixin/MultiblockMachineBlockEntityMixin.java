package icu.kudikan.miparallelhatch.mixin;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import icu.kudikan.miparallelhatch.api.machine.IMultiblockMachineParallelData;
import icu.kudikan.miparallelhatch.machines.blockentities.hatches.ParallelHatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiblockMachineBlockEntity.class)
public abstract class MultiblockMachineBlockEntityMixin extends MachineBlockEntity implements IMultiblockMachineParallelData {
    @Unique
    private int miParallelHatch$cachedParallelCount = 1;
    @Unique
    private boolean miParallelHatch$parallelCountDirty = true;
    @Unique
    private long miParallelHatch$lastParallelConfigVersion = -1;
    @Shadow
    private ShapeMatcher shapeMatcher;

    public MultiblockMachineBlockEntityMixin(BEP bep, MachineGuiParameters guiParams, OrientationComponent.Params orientationParams) {
        super(bep, guiParams, orientationParams);
    }

    @Unique
    public ShapeMatcher miParallelHatch$getShapeMatcher() {
        return this.shapeMatcher;
    }

    @Unique
    public int miParallelHatch$getParallelCount() {
        miParallelHatch$refreshParallelCount();
        return this.miParallelHatch$cachedParallelCount;
    }

    @Unique
    public void miParallelHatch$refreshParallelCount() {
        long currentVersion = ParallelHatch.getParallelConfigVersion();
        if (!miParallelHatch$parallelCountDirty && miParallelHatch$lastParallelConfigVersion == currentVersion) {
            return;
        }

        int parallel = 0;
        var shape = miParallelHatch$getShapeMatcher();
        var level = getLevel();
        if (shape != null && level != null) {
            for (var pos : shape.getPositions()) {
                var be = level.getBlockEntity(pos);
                if (be instanceof ParallelHatch hatch) {
                    parallel += hatch.getParallelCount();
                }
            }
        }
        this.miParallelHatch$cachedParallelCount = Math.max(1, parallel);
        this.miParallelHatch$parallelCountDirty = false;
        this.miParallelHatch$lastParallelConfigVersion = currentVersion;
    }

    @Unique
    public void miParallelHatch$invalidateParallelCount() {
        this.miParallelHatch$parallelCountDirty = true;
    }

    @Inject(method = "link", at = @At("HEAD"))
    private void miParallelHatch$invalidateBeforeLink(CallbackInfo ci) {
        if (shapeMatcher == null || shapeMatcher.needsRematch()) {
            miParallelHatch$invalidateParallelCount();
        }
    }

    @Inject(method = "unlink", at = @At("HEAD"))
    private void miParallelHatch$invalidateOnUnlink(CallbackInfo ci) {
        miParallelHatch$cachedParallelCount = 1;
        miParallelHatch$invalidateParallelCount();
    }
}
