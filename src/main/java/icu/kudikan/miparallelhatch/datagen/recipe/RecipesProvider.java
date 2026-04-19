package icu.kudikan.miparallelhatch.datagen.recipe;

import aztech.modern_industrialization.datagen.recipe.MIRecipesProvider;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipeBuilder;
import aztech.modern_industrialization.recipe.json.ShapedRecipeJson;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

public class RecipesProvider extends MIRecipesProvider {
    private static final String pathPrefix = "hatches/";

    public RecipesProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        String[] casings = {"bronze", "steel", "basic", "advanced", "turbo", "highly_advanced", "quantum"};
        String[] circuits = {"analog_circuit_board", "analog_circuit", "analog_circuit", "electronic_circuit", "digital_circuit", "processing_unit", "quantum_circuit"};
        String[] gears = {"bronze", "steel", "tin", "aluminum", "stainless_steel", "titanium", "iridium"};

        for (int i = 0; i < casings.length; i++) {
            String casing = String.format("modern_industrialization:%s_machine_hull", casings[i]);
            if (i < 2) {
                casing = String.format("modern_industrialization:%s_machine_casing", casings[i]);
            }
            String circuit = String.format("modern_industrialization:%s", circuits[i]);
            String parallelHatch = String.format("modern_industrialization:%s_parallel_control_hatch", casings[i]);
            String lastPparallelHatch = "modern_industrialization:bronze_rotor";
            if (i > 0) {
                lastPparallelHatch = String.format("modern_industrialization:%s_parallel_control_hatch", casings[i - 1]);
            }
            String gear = String.format("modern_industrialization:%s_gear", gears[i]);
            if (i == casings.length - 1) {
                gear = "modern_industrialization:superconductor_coil";
            }

            ShapedRecipeJson craft = new ShapedRecipeJson(parallelHatch, 1, "ATA", "VCV", "ATA")
                    .addInput('C', casing)
                    .addInput('V', lastPparallelHatch)
                    .addInput('T', circuit)
                    .addInput('A', gear);

            MachineRecipeBuilder craftAsbl = craft.exportToMachine(MIMachineRecipeTypes.ASSEMBLER, 8, 200, 1);

            craft.offerTo(consumer, pathPrefix + casings[i] + "/" + casings[i] + "_parallel_hatch");
            craftAsbl.offerTo(consumer, pathPrefix + casings[i] + "/assembler/" + casings[i] + "_parallel_hatch");
        }

    }
}
