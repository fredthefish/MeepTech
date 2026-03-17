package com.minecraftmod.meeptech.datagen;

import java.util.concurrent.CompletableFuture;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.registries.ModBlocks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
        super(output, lookupProvider, MeepTech.MODID, fileHelper);
    }
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.MATERIAL_WORKSTATION.get())
            .add(ModBlocks.ENGINEERING_STATION.get());
        for (DeferredBlock<Block> hullBlock : ModBlocks.HULL_BLOCKS.values()) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(hullBlock.get());
        }
        for (DeferredBlock<Block> oreBlock : ModBlocks.ORE_BLOCKS.values()) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(oreBlock.get());
        }
    }
}
