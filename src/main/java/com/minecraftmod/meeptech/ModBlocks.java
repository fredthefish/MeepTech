package com.minecraftmod.meeptech;

import java.util.HashMap;
import java.util.function.Supplier;

import com.minecraftmod.meeptech.blocks.DesigningStationBlock;
import com.minecraftmod.meeptech.blocks.DraftingStationBlock;
import com.minecraftmod.meeptech.blocks.EngineeringStationBlock;
import com.minecraftmod.meeptech.blocks.MaterialWorkstationBlock;
import com.minecraftmod.meeptech.blocks.machines.PrimitiveSmelterBlock;
import com.minecraftmod.meeptech.items.HullItem;

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
    public static final HashMap<DeferredBlock<Block>, DeferredItem<Item>> BLOCK_ITEMS = new HashMap<>();

    public static final DeferredBlock<Block> MATERIAL_WORKSTATION = registerBlock("material_workstation",
        () -> new MaterialWorkstationBlock(BlockBehaviour.Properties.of()
            .strength(5f, 6f)
            .destroyTime(5)
            .requiresCorrectToolForDrops()
            .sound(SoundType.METAL)
    ));
    public static final DeferredBlock<Block> DESIGNING_STATION = registerBlock("designing_station", 
        () -> new DesigningStationBlock(BlockBehaviour.Properties.of()
            .strength(2f, 6f)
            .destroyTime(1.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
    ));
    public static final DeferredBlock<Block> DRAFTING_STATION = registerBlock("drafting_station", 
        () -> new DraftingStationBlock(BlockBehaviour.Properties.of()
            .strength(2f, 6f)
            .destroyTime(1.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
    ));
    public static final DeferredBlock<Block> ENGINEERING_STATION = registerBlock("engineering_station", 
        () -> new EngineeringStationBlock(BlockBehaviour.Properties.of()
            .strength(2f, 6f)
            .destroyTime(2f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
    ));

    public static final DeferredBlock<Block> PRIMITIVE_SMELTER = registerBlock("primitive_smelter", 
        () -> new PrimitiveSmelterBlock(BlockBehaviour.Properties.of()
            .strength(2f, 6f)
            .destroyTime(3)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
    ));

    public static final DeferredBlock<Block> BRICK_HULL = registerBlock("brick_hull", () -> new Block(BlockBehaviour.Properties.of()
        .strength(2f, 6f)
        .destroyTime(3)
        .requiresCorrectToolForDrops()
        .sound(SoundType.STONE)
    ), false);
    public static final DeferredItem<Item> BRICK_HULL_ITEM = registerBlockHullItem("brick_hull", BRICK_HULL);

    private static DeferredBlock<Block> registerBlock(String name, Supplier<Block> block) {
        return registerBlock(name, block, true);
    }
    private static DeferredBlock<Block> registerBlock(String name, Supplier<Block> block, boolean registerItem) {
        DeferredBlock<Block> returnBlock = BLOCKS.register(name, block);
        if (registerItem) registerBlockItem(name, returnBlock);
        return returnBlock;
    }
    private static DeferredItem<Item> registerBlockItem(String name, DeferredBlock<Block> block) {
        DeferredItem<Item> deferredItem = ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        BLOCK_ITEMS.put(block, deferredItem);
        return deferredItem;
    }
    private static DeferredItem<Item> registerBlockHullItem(String name, DeferredBlock<Block> block) {
        DeferredItem<Item> deferredItem = ModItems.ITEMS.register(name, () -> new HullItem(block.get(), new Item.Properties()));
        BLOCK_ITEMS.put(block, deferredItem);
        return deferredItem;
    }
}
