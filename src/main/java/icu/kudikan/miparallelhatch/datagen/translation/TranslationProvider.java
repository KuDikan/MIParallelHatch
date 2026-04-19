package icu.kudikan.miparallelhatch.datagen.translation;

import aztech.modern_industrialization.definition.Definition;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import icu.kudikan.miparallelhatch.util.ConfigBuilder;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public final class TranslationProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String OUTPUT_PATH = "assets/miparallelhatch/lang/en_us.json";

    private final PackOutput packOutput;
    private final Map<String, String> translationPairs = new TreeMap<>();

    public TranslationProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    public void addTranslation(String key, String englishValue) {
        if (!translationPairs.containsKey(key)) {
            translationPairs.put(key, englishValue);
        } else {
            throw new IllegalArgumentException(
                    String.format("Error adding translation key %s for translation %s : already registered for translation %s", key, englishValue,
                            translationPairs.get(key)));
        }
    }

    private void addManualEntries() {
        addTranslation("mod.miparallelhatch", "ModernIndustrialization Parallel Control Hatch");
        addTranslation("fml.menu.mods.info.description.miparallelhatch", "Add Parallel Control Hatch to ModernIndustrialization Mod.\nSupport KubeJS, Jade.");
        addTranslation("text.miparallelhatch.HatchParallelCount", "Has a parallel count of %s.");
        addTranslation("text.miparallelhatch.maxParallelDisplay", "Max parallel count: %s");
        addTranslation("text.miparallelhatch.currentParallelDisplay", "Current: %s");
        addTranslation("text.miparallelhatch.decreaseParallel", "Decrease parallel count\nShift -8  Ctrl -32  Alt -128");
        addTranslation("text.miparallelhatch.increaseParallel", "Increase parallel count\nShift +8  Ctrl +32  Alt +128");
        addTranslation("text.miparallelhatch.machineParallelStatus", "Parallel: %s / %s   Energy factor: × %s");
        addTranslation("text.miparallelhatch.jadeParallelHatchStatus", "Parallel Count %s / %s");
        addTranslation("text.miparallelhatch.jadeMachineParallelStatus", "Parallel %s / %s | Energy factor × %s");
        addTranslation("text.miparallelhatch.jadeMachineParallelStatusWithoutEnergyFactor", "Parallel %s / %s");
        addTranslation("config.jade.plugin_miparallelhatch.parallel_status", "Parallel Status");
    }

    private void collectTranslationEntries() {
        addManualEntries();

        for (var entry : ConfigBuilder.configTranslations.entrySet()) {
            addTranslation(entry.getKey(), entry.getValue());
        }

        for (Definition definition : Definition.TRANSLATABLE_DEFINITION) {
            if (definition.getTranslationKey().contains("parallel")) {
                addTranslation(definition.getTranslationKey(), definition.getEnglishName());
            }
        }
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.runAsync(() -> {
            try {
                innerRun(output);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, Util.backgroundExecutor());
    }

    public void innerRun(CachedOutput cache) throws IOException {
        collectTranslationEntries();

        customJsonSave(cache, GSON.toJsonTree(translationPairs), packOutput.getOutputFolder().resolve(OUTPUT_PATH));
    }

    private void customJsonSave(CachedOutput cache, JsonElement jsonElement, Path path) throws IOException {
        String sortedJson = GSON.toJson(jsonElement);
        String prettyPrinted = sortedJson.replace("\\u0027", "'");
        cache.writeIfNeeded(path, prettyPrinted.getBytes(StandardCharsets.UTF_8), Hashing.sha256().hashString(prettyPrinted, StandardCharsets.UTF_8));
    }

    @Override
    public String getName() {
        return "Translations";
    }
}
