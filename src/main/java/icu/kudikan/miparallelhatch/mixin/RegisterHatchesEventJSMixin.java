package icu.kudikan.miparallelhatch.mixin;

import aztech.modern_industrialization.compat.kubejs.machine.RegisterHatchesEventJS;
import aztech.modern_industrialization.machines.models.MachineCasings;
import icu.kudikan.miparallelhatch.machines.init.MultiblockParallelHatches;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RegisterHatchesEventJS.class)
public class RegisterHatchesEventJSMixin {
    @Unique
    public void parallel(String englishPrefix, String prefix, String casing, int parallelCount) {
        MultiblockParallelHatches.registerParallelHatches(englishPrefix, prefix, MachineCasings.get(casing), parallelCount);
    }
}
