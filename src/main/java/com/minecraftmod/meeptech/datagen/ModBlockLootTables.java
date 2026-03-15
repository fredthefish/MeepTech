package com.minecraftmod.meeptech.datagen;

import java.util.Set;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.registries.ModBlocks;
import com.minecraftmod.meeptech.registries.ModDataComponents;

import net.minecraft.core.HolderLookup;
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
        for (DeferredBlock<Block> hullBlock : ModBlocks.HULL_BLOCKS) {
            this.add(hullBlock.get(), block -> this.createSingleItemTable(block)
                .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                .include(ModDataComponents.MACHINE_CONFIG_DATA.get())));
        }
    }
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream().filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(MeepTech.MODID))::iterator;
    }
}
