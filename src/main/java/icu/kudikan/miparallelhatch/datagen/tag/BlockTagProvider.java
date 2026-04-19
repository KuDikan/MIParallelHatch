package icu.kudikan.miparallelhatch.datagen.tag;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.MIBlock;
import aztech.modern_industrialization.definition.BlockDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                            ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MI.ID, existingFileHelper);
    }

    private static TagKey<Block> key(ResourceLocation id) {
        return TagKey.create(BuiltInRegistries.BLOCK.key(), id);
    }

    private static TagKey<Block> key(String id) {
        return key(ResourceLocation.parse(id));
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (BlockDefinition<?> definition : MIBlock.BLOCK_DEFINITIONS.values()) {
            if (definition.asBlock().getDescriptionId().contains("parallel")) {
                for (var tag : definition.tags) {
                    tag(tag).add(definition.asBlock());
                }
            }
        }


    }
}
