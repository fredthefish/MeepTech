package com.minecraftmod.meeptech.datagen;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.ModMaterials;
import com.minecraftmod.meeptech.registries.ModBlocks;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, MeepTech.MODID, fileHelper);
    }
    @Override
    protected void registerStatesAndModels() {
        for (Material hullMaterial : ModBlocks.HULL_BLOCKS.keySet()) {
            DeferredBlock<Block> hullBlock = ModBlocks.HULL_BLOCKS.get(hullMaterial);
            ModelFile model = models().cubeAll(hullBlock.getId().getPath(), 
                ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/hull/" + hullMaterial.getFormTexture(ModMaterials.HULL)));
            simpleBlock(hullBlock.get(), model);
        }
        customTopBottomBlock(ModBlocks.MATERIAL_WORKSTATION, "material_workstation");
        customTopBottomBlock(ModBlocks.ENGINEERING_STATION, "engineering_station");
    }
    private void customTopBottomBlock(DeferredBlock<Block> block, String path) {
        String name = block.getId().getPath();
        ResourceLocation side = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/" + path + "/side");
        ResourceLocation bottom = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/" + path + "/bottom");
        ResourceLocation top = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/" + path + "/top");
        ModelFile model = models().cubeBottomTop(name, side, bottom, top);
        simpleBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
    }
}
