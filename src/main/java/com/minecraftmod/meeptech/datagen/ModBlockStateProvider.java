package com.minecraftmod.meeptech.datagen;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.registries.ModBlocks;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, MeepTech.MODID, fileHelper);
    }
    @Override
    protected void registerStatesAndModels() {
        for (DeferredBlock<Block> hullBlock : ModBlocks.HULL_BLOCKS) {
            simpleBlock(hullBlock.get(), this.models().getExistingFile(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/hull")));
        }
        this.simpleBlock(ModBlocks.MATERIAL_WORKSTATION.get(), 
            this.models().getExistingFile(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/material_workstation")));
        this.simpleBlock(ModBlocks.ENGINEERING_STATION.get(), 
            this.models().getExistingFile(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/engineering_station")));
    }
}
