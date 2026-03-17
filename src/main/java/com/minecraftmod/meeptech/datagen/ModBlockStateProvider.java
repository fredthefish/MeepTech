package com.minecraftmod.meeptech.datagen;

import java.util.HashMap;
import java.util.Map;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.blocks.OreBlock;
import com.minecraftmod.meeptech.blocks.OreStoneType;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.registries.ModBlocks;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
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
            ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/hull/" + hullMaterial.getFormTexture(MaterialForm.HULL));
            BlockModelBuilder model = models().withExistingParent(hullBlock.getId().getPath(), mcLoc("block/block"))
                .texture("all", textureLocation)
                .texture("particle", textureLocation);
            model.element()
                .from(0, 0, 0).to(16, 16, 16)
                .allFaces((direction, faceBuilder) -> faceBuilder.texture("#all").cullface(direction).tintindex(0)).end();
            simpleBlock(hullBlock.get(), model);
        }
        customTopBottomBlock(ModBlocks.MATERIAL_WORKSTATION, "material_workstation");
        customTopBottomBlock(ModBlocks.ENGINEERING_STATION, "engineering_station");
        for (Material oreMaterial : ModBlocks.ORE_BLOCKS.keySet()) {
            DeferredBlock<Block> oreBlock = ModBlocks.ORE_BLOCKS.get(oreMaterial);
            ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/ore/" + oreMaterial.getFormTexture(MaterialForm.ORE));
            Map<OreStoneType, ModelFile> models = new HashMap<>();
            for (OreStoneType stoneType : OreStoneType.values()) {
                ResourceLocation stoneTexture = switch (stoneType) {
                    case STONE -> mcLoc("block/stone");
                    case DEEPSLATE -> mcLoc("block/deepslate");
                };
                ModelFile model = models().withExistingParent(oreBlock.getId().getPath() + "_" + stoneType.getSerializedName(), modLoc("block/ore_template"))
                    .texture("stone", stoneTexture).texture("overlay", textureLocation);
                models.put(stoneType, model);
            }
            getVariantBuilder(oreBlock.get()).forAllStates(state -> {
                OreStoneType stoneType = state.getValue(OreBlock.STONE_TYPE);
                return ConfiguredModel.builder().modelFile(models.get(stoneType)).build();
            });
        }
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
