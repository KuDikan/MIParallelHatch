package icu.kudikan.miparallelhatch.mixin;

import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import icu.kudikan.miparallelhatch.machines.init.MultiblockParallelHatches;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(HatchFlags.class)
public class HatchFlagsMixin {
    @Shadow
    @Final
    private Set<HatchType> allowed;

    @Inject(method = "allows", at = @At("HEAD"), cancellable = true, require = 1)
    public void allows(HatchType type, CallbackInfoReturnable<Boolean> cir) {
        if (type == MultiblockParallelHatches.PARALLEL_CONTROL) {
            cir.setReturnValue(allowed.contains(HatchTypes.ITEM_INPUT) || allowed.contains(HatchTypes.ITEM_OUTPUT)
                    || allowed.contains(HatchTypes.FLUID_INPUT) || allowed.contains(HatchTypes.FLUID_OUTPUT));
        }
    }
}
