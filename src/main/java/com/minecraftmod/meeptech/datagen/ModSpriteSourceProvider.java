package com.minecraftmod.meeptech.datagen;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.minecraftmod.meeptech.MeepTech;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

public class ModSpriteSourceProvider extends SpriteSourceProvider {

    public ModSpriteSourceProvider(PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MeepTech.MODID, existingFileHelper);
    }

    @Override
    protected void gather() {
        SourceList blockAtlas = atlas(BLOCKS_ATLAS);

        // Register all pipe textures for stitching
        blockAtlas.addSource(new SingleFile(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_end"), Optional.empty()));
        blockAtlas.addSource(new SingleFile(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_arm"), Optional.empty()));
        blockAtlas.addSource(new SingleFile(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_arm_input"), Optional.empty()));
        blockAtlas.addSource(new SingleFile(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_arm_output"), Optional.empty()));
        blockAtlas.addSource(new SingleFile(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_end_input"), Optional.empty()));
        blockAtlas.addSource(new SingleFile(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_end_output"), Optional.empty()));
    }
}