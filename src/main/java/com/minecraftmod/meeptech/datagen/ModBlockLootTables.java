package com.minecraftmod.meeptech.datagen;

import java.util.Set;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.registries.ModBlocks;
import com.minecraftmod.meeptech.registries.ModDataComponents;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }
    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.MATERIAL_WORKSTATION.get());
        this.dropSelf(ModBlocks.ENGINEERING_STATION.get());
        this.dropSelf(ModBlocks.ITEM_PIPE.get());
        this.dropSelf(ModBlocks.FLUID_PIPE.get());
        this.add(ModBlocks.FLUID_TANK.get(), block -> this.createSingleItemTable(block)
            .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
            .include(DataComponents.BLOCK_ENTITY_DATA)));
        for (DeferredBlock<Block> hullBlock : ModBlocks.HULL_BLOCKS.values()) {
            this.add(hullBlock.get(), block -> this.createSingleItemTable(block)
                .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                .include(ModDataComponents.MACHINE_CONFIG_DATA.get())));
        }
        for (Material material : ModBlocks.ORE_BLOCKS.keySet()) {
            DeferredBlock<Block> oreBlock = ModBlocks.ORE_BLOCKS.get(material);
            this.add(oreBlock.get(), block -> this.createOreDrop(block, material.getForm(MaterialForm.RAW)));
        }
    }
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream().filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(MeepTech.MODID))::iterator;
    }
}
