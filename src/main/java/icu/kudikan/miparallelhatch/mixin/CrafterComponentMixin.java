package icu.kudikan.miparallelhatch.mixin;

import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractCraftingMultiblockBlockEntity;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import icu.kudikan.miparallelhatch.MiParallelHatchConfig;
import icu.kudikan.miparallelhatch.api.machine.IMultiblockMachineParallelData;
import icu.kudikan.miparallelhatch.api.machine.IParallelCrafterComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static aztech.modern_industrialization.util.Simulation.ACT;
import static aztech.modern_industrialization.util.Simulation.SIMULATE;

@Mixin(CrafterComponent.class)
public abstract class CrafterComponentMixin implements IParallelCrafterComponent {
    @Unique
    private static final String MI_PARALLEL_HATCH_ACTIVE_PARALLELS_TAG = "miParallelHatchActiveParallels";

    @Final
    @Shadow
    private MachineProcessCondition.Context conditionContext;
    @Final
    @Shadow
    private CrafterComponent.Behavior behavior;
    @Shadow
    private RecipeHolder<MachineRecipe> activeRecipe = null;
    @Shadow
    private long usedEnergy;
    @Shadow
    private long recipeEnergy;
    @Shadow
    private long recipeMaxEu;
    @Shadow
    private int efficiencyTicks;
    @Shadow
    private int maxEfficiencyTicks;
    @Shadow
    private long previousBaseEu = -1L;
    @Shadow
    private long previousMaxEu = -1L;
    @Shadow
    private int lastForcedTick = 0;

    @Unique
    private int miParallelHatch$activeParallelRecipes = 1;
    @Unique
    private long miParallelHatch$extraRecipeEuPerTick = 0;
    @Unique
    private double miParallelHatch$currentEnergyFactor = 1.0D;


    @Shadow
    private void loadDelayedActiveRecipe() {
    }

    @Shadow
    private boolean updateActiveRecipe() {
        return false;
    }

    @Shadow
    private long getRecipeMaxEu(long recipeEu, long totalEu, int efficiencyTicks) {
        return recipeMaxEu;
    }

    @Shadow
    private int getRecipeMaxEfficiencyTicks(MachineRecipe recipe) {
        return 0;
    }

    @Shadow
    private boolean tryStartRecipe(MachineRecipe recipe) {
        return false;
    }

    @Shadow
    protected abstract boolean putItemOutputs(MachineRecipe recipe, boolean simulate, boolean toggleLock);

    @Shadow
    protected abstract boolean putFluidOutputs(MachineRecipe recipe, boolean simulate, boolean toggleLock);

    @Shadow
    protected abstract void clearLocks();

    @Shadow
    private void clearActiveRecipeIfPossible() {
    }

    @Inject(method = "updateActiveRecipe", at = @At("RETURN"))
    private void parallel$modifyRecipeEnergy(CallbackInfoReturnable<Boolean> cir) {
        miParallelHatch$activeParallelRecipes = 1;
        miParallelHatch$extraRecipeEuPerTick = 0;
        miParallelHatch$currentEnergyFactor = 1.0D;

        if (cir.getReturnValue() && activeRecipe != null) {
            int configuredParallel = mIParallelHatch$getParallelCount();
            if (configuredParallel > 1) {
                int startedParallelRecipes = 1;
                while (startedParallelRecipes < configuredParallel && tryStartRecipe(activeRecipe.value())) {
                    startedParallelRecipes++;
                }

                miParallelHatch$activeParallelRecipes = startedParallelRecipes;
                recipeEnergy = miParallelHatch$applyEnergyFactor(activeRecipe.value().getTotalEu(), startedParallelRecipes);
                miParallelHatch$currentEnergyFactor = miParallelHatch$getEnergyFactor(startedParallelRecipes);
            }
        }
    }

    @Inject(method = "tickRecipe", at = @At("HEAD"), cancellable = true, require = 1)
    public void tickRecipe(CallbackInfoReturnable<Boolean> cir) {
        if (miParallelHatch$activeParallelRecipes > 1 || mIParallelHatch$getParallelCount() > 1) {
            if (behavior.getCrafterWorld().isClientSide()) {
                throw new IllegalStateException("May not call client side.");
            }

            boolean isActive;
            boolean isEnabled = behavior.isEnabled();

            loadDelayedActiveRecipe();

            boolean recipeStarted = false;
            if (usedEnergy == 0 && isEnabled) {
                if (behavior.consumeEu(1, SIMULATE) == 1) {
                    recipeStarted = updateActiveRecipe();
                }
            }

            if (activeRecipe != null) {
                lastForcedTick = 0;
            }

            long eu = 0;
            boolean finishedRecipe = false;
            if (activeRecipe != null && isEnabled) {
                if (usedEnergy > 0 || recipeStarted) {
                    long singleRecipeEnergy = activeRecipe.value().getTotalEu();
                    long baseRecipeMaxEu = getRecipeMaxEu(activeRecipe.value().eu, singleRecipeEnergy, efficiencyTicks);
                    long linearRecipeMaxEu = miParallelHatch$safeMultiply(baseRecipeMaxEu, miParallelHatch$activeParallelRecipes);
                    recipeMaxEu = miParallelHatch$applyEnergyFactor(
                            baseRecipeMaxEu,
                            miParallelHatch$activeParallelRecipes);
                    miParallelHatch$extraRecipeEuPerTick = Math.max(0, recipeMaxEu - linearRecipeMaxEu);
                    miParallelHatch$currentEnergyFactor = miParallelHatch$getEnergyFactor(miParallelHatch$activeParallelRecipes);
                    eu = activeRecipe.value().conditionsMatch(conditionContext)
                            ? behavior.consumeEu(Math.min(recipeMaxEu, recipeEnergy - usedEnergy), ACT)
                            : 0;
                    isActive = eu > 0;
                    usedEnergy += eu;

                    if (usedEnergy == recipeEnergy) {
                        for (int i = 0; i < miParallelHatch$activeParallelRecipes; i++) {
                            putItemOutputs(activeRecipe.value(), false, false);
                            putFluidOutputs(activeRecipe.value(), false, false);
                        }
                        clearLocks();
                        usedEnergy = 0;
                        finishedRecipe = true;
                        behavior.onCraft();
                    }
                } else if (behavior.isOverdriving()) {
                    eu = activeRecipe.value().conditionsMatch(conditionContext) ? behavior.consumeEu(recipeMaxEu, ACT) : 0;
                    isActive = eu > 0;
                } else {
                    isActive = false;
                }
            } else {
                isActive = false;
            }

            if (activeRecipe != null) {
                if (previousBaseEu != behavior.getBaseRecipeEu() || previousMaxEu != behavior.getMaxRecipeEu()) {
                    previousBaseEu = behavior.getBaseRecipeEu();
                    previousMaxEu = behavior.getMaxRecipeEu();
                    maxEfficiencyTicks = getRecipeMaxEfficiencyTicks(activeRecipe.value());
                    efficiencyTicks = Math.min(efficiencyTicks, maxEfficiencyTicks);
                }
            }

            if (finishedRecipe) {
                if (efficiencyTicks < maxEfficiencyTicks) {
                    efficiencyTicks++;
                }
            } else if (eu < recipeMaxEu) {
                if (efficiencyTicks > 0) {
                    efficiencyTicks--;
                }
            }

            clearActiveRecipeIfPossible();
            if (activeRecipe == null) {
                miParallelHatch$activeParallelRecipes = 1;
                miParallelHatch$extraRecipeEuPerTick = 0;
                miParallelHatch$currentEnergyFactor = 1.0D;
            }

            cir.setReturnValue(isActive);
        }
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void miParallelHatch$writeNbt(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        tag.putInt(MI_PARALLEL_HATCH_ACTIVE_PARALLELS_TAG, miParallelHatch$activeParallelRecipes);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void miParallelHatch$readNbt(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgradingMachine, CallbackInfo ci) {
        miParallelHatch$activeParallelRecipes = Math.max(1, tag.getInt(MI_PARALLEL_HATCH_ACTIVE_PARALLELS_TAG));
        miParallelHatch$currentEnergyFactor = miParallelHatch$getEnergyFactor(miParallelHatch$activeParallelRecipes);
    }

    @Inject(method = "tryContinueRecipe", at = @At("RETURN"), cancellable = true)
    private void miParallelHatch$relockParallelOutputs(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || miParallelHatch$activeParallelRecipes <= 1 || activeRecipe == null) {
            return;
        }

        for (int i = 1; i < miParallelHatch$activeParallelRecipes; i++) {
            if (putItemOutputs(activeRecipe.value(), true, false) && putFluidOutputs(activeRecipe.value(), true, false)) {
                putItemOutputs(activeRecipe.value(), true, true);
                putFluidOutputs(activeRecipe.value(), true, true);
            } else {
                clearLocks();
                cir.setReturnValue(false);
                return;
            }
        }
    }

    @Unique
    public int mIParallelHatch$getParallelCount() {
        if (conditionContext.getBlockEntity() instanceof AbstractCraftingMultiblockBlockEntity mmbe) {
            return ((IMultiblockMachineParallelData) mmbe).miParallelHatch$getParallelCount();
        }
        return 1;
    }

    @Unique
    private double miParallelHatch$getConfiguredExtraEnergyMultiplier() {
        return MiParallelHatchConfig.INSTANCE.parallelExtraEnergyMultiplier.getAsDouble();
    }

    @Unique
    private double miParallelHatch$getEnergyFactor(int parallelRecipes) {
        if (parallelRecipes <= 1) {
            return 1.0D;
        }
        return 1.0D + (parallelRecipes - 1) * miParallelHatch$getConfiguredExtraEnergyMultiplier();
    }

    @Unique
    private long miParallelHatch$applyEnergyFactor(long singleRecipeValue, int parallelRecipes) {
        if (parallelRecipes <= 1) {
            return singleRecipeValue;
        }
        long linearValue = miParallelHatch$safeMultiply(singleRecipeValue, parallelRecipes);
        long factoredValue = miParallelHatch$safeScale(singleRecipeValue, miParallelHatch$getEnergyFactor(parallelRecipes));
        return Math.max(linearValue, factoredValue);
    }

    @Unique
    private long miParallelHatch$safeMultiply(long value, int multiplier) {
        if (value <= 0 || multiplier <= 0) {
            return 0;
        }
        if (value > Long.MAX_VALUE / multiplier) {
            return Long.MAX_VALUE;
        }
        return value * multiplier;
    }

    @Unique
    private long miParallelHatch$safeScale(long value, double multiplier) {
        if (value <= 0 || multiplier <= 0) {
            return 0;
        }
        double scaled = value * multiplier;
        if (!Double.isFinite(scaled) || scaled >= Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        return Math.round(scaled);
    }

    @Override
    public int miParallelHatch$getActiveParallelRecipes() {
        return activeRecipe == null ? 0 : miParallelHatch$activeParallelRecipes;
    }


    @Override
    public double miParallelHatch$getCurrentEnergyFactor() {
        return miParallelHatch$currentEnergyFactor;
    }

}
