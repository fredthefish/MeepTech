package com.minecraftmod.meeptech;

import java.util.function.Supplier;

import com.minecraftmod.meeptech.blocks.DesigningStationBlock;
import com.minecraftmod.meeptech.blocks.DraftingStationBlock;
import com.minecraftmod.meeptech.blocks.EngineeringStationBlock;
import com.minecraftmod.meeptech.blocks.MaterialWorkstationBlock;
import com.minecraftmod.meeptech.blocks.machines.PrimitiveSmelterBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MeepTech.MODID);

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
            .destroyTime(5)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
    ));

    private static DeferredBlock<Block> registerBlock(String name, Supplier<Block> block) {
        DeferredBlock<Block> returnBlock = BLOCKS.register(name, block);
        registerBlockItem(name, returnBlock);
        return returnBlock;
    }

    private static void registerBlockItem(String name, DeferredBlock<Block> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
