package com.minecraftmod.meeptech.registries;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.blocks.EngineeringStationBlock;
import com.minecraftmod.meeptech.blocks.FluidTankBlock;
import com.minecraftmod.meeptech.blocks.MaterialWorkstationBlock;
import com.minecraftmod.meeptech.blocks.pipes.FluidPipeBlock;
import com.minecraftmod.meeptech.blocks.pipes.ItemPipeBlock;
import com.minecraftmod.meeptech.items.HullItem;
import com.minecraftmod.meeptech.logic.material.Material;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MeepTech.MODID);
    public static final Map<DeferredBlock<Block>, DeferredItem<Item>> BLOCK_ITEMS = new LinkedHashMap<>();

    public static final Map<Material, DeferredBlock<Block>> HULL_BLOCKS = new LinkedHashMap<>();
    public static final Map<Material, DeferredBlock<Block>> ORE_BLOCKS = new LinkedHashMap<>();

    public static final DeferredBlock<Block> MATERIAL_WORKSTATION = registerBlock("material_workstation",
        () -> new MaterialWorkstationBlock(BlockBehaviour.Properties.of()
            .strength(5f, 6f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
    ));
    public static final DeferredBlock<Block> ENGINEERING_STATION = registerBlock("engineering_station", 
        () -> new EngineeringStationBlock(BlockBehaviour.Properties.of()
            .strength(2f, 6f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
    ));
    public static final DeferredBlock<Block> ITEM_PIPE = registerBlock("item_pipe", () -> new ItemPipeBlock(BlockBehaviour.Properties.of()
        .strength(2f, 3f)
        .sound(SoundType.COPPER)
        .noOcclusion()
        .isViewBlocking((state, level, pos) -> false)));
    public static final DeferredBlock<Block> FLUID_PIPE = registerBlock("fluid_pipe", () -> new FluidPipeBlock(BlockBehaviour.Properties.of()
        .strength(2f, 3f)
        .sound(SoundType.COPPER)
        .noOcclusion()
        .isViewBlocking((state, level, pos) -> false)));
    public static final DeferredBlock<Block> FLUID_TANK = registerBlock("fluid_tank", () -> new FluidTankBlock(BlockBehaviour.Properties.of()
        .strength(2f, 6f)
        .sound(SoundType.METAL)
        .requiresCorrectToolForDrops(),
        8000));
    private static DeferredBlock<Block> registerBlock(String name, Supplier<Block> block) {
        return registerBlock(name, block, true);
    }
    public static DeferredBlock<Block> registerBlock(String name, Supplier<Block> block, boolean registerItem) {
        DeferredBlock<Block> returnBlock = BLOCKS.register(name, block);
        if (registerItem) registerBlockItem(name, returnBlock);
        return returnBlock;
    }
    private static DeferredItem<Item> registerBlockItem(String name, DeferredBlock<Block> block) {
        DeferredItem<Item> deferredItem = ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        BLOCK_ITEMS.put(block, deferredItem);
        return deferredItem;
    }
    public static DeferredItem<Item> registerBlockHullItem(String name, DeferredBlock<Block> block) {
        DeferredItem<Item> deferredItem = ModItems.ITEMS.register(name, () -> new HullItem(block.get(), new Item.Properties()));
        BLOCK_ITEMS.put(block, deferredItem);
        return deferredItem;
    }
}
