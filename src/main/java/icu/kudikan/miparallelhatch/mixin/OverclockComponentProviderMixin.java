package icu.kudikan.miparallelhatch.mixin;


import aztech.modern_industrialization.client.compat.jade.OverclockComponentProvider;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(OverclockComponentProvider.class)
public class OverclockComponentProviderMixin {

    @ModifyVariable(
            method = "appendTooltip(Lsnownee/jade/api/ITooltip;Lsnownee/jade/api/BlockAccessor;Lsnownee/jade/api/config/IPluginConfig;)V",
            at = @At(
                    value = "STORE",
                    target = "Lnet/minecraft/nbt/CompoundTag;getLong(Ljava/lang/String;)J"
            ),
            name = "baseRecipeEu"
    )
    private long mi_modifyBaseRecipeEu(long original, @Local(name = "tag") CompoundTag tag) {
        // 你要的逻辑：原值 * (包含MACHINE_ACTIVE? 对应int值 : 1)
        int multiplier = tag.contains("MachineActiveParallel") ? tag.getInt("MachineActiveParallel") : 1;
        return original * multiplier;
    }
}
