package icu.kudikan.miparallelhatch.mixin;

import aztech.modern_industrialization.machines.BEP;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractCraftingMultiblockBlockEntity;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.components.OrientationComponent;
import aztech.modern_industrialization.machines.gui.MachineGuiParameters;
import icu.kudikan.miparallelhatch.api.machine.IMultiblockMachineParallelData;
import icu.kudikan.miparallelhatch.api.machine.IParallelCrafterComponent;
import icu.kudikan.miparallelhatch.machines.guicomponents.ParallelMachineStatusGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractCraftingMultiblockBlockEntity.class)
public abstract class AbstractCraftingMultiblockBlockEntityMixin extends MachineBlockEntity {
    @Final
    @Shadow
    protected CrafterComponent crafter;

    public AbstractCraftingMultiblockBlockEntityMixin(BEP bep, MachineGuiParameters guiParams, OrientationComponent.Params orientationParams) {
        super(bep, guiParams, orientationParams);
    }

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void miParallelHatch$registerParallelStatusGui(CallbackInfo ci) {
        registerGuiComponent(new ParallelMachineStatusGui(
                () -> ((IMultiblockMachineParallelData) this).miParallelHatch$getParallelCount(),
                () -> ((IParallelCrafterComponent) crafter).miParallelHatch$getActiveParallelRecipes(),
                () -> ((IParallelCrafterComponent) crafter).miParallelHatch$getCurrentEnergyFactor()));
    }
}
