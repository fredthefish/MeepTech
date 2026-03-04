package com.minecraftmod.meeptech;

import java.util.function.Supplier;

import com.minecraftmod.meeptech.blocks.MaterialWorkstationBlock;

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
        .strength(5)
        .destroyTime(10)
        .requiresCorrectToolForDrops()
        .sound(SoundType.METAL)
    ));
    
    private static DeferredBlock<Block> registerBlock(String name, Supplier<Block> block) {
        DeferredBlock<Block> returnBlock = BLOCKS.register(name, block);
        registerBlockItem(name, returnBlock);
        return returnBlock;
    }

    private static void registerBlockItem(String name, DeferredBlock<Block> block) {
        ModItems.ITEMS.register("material_workstation", () -> new BlockItem(MATERIAL_WORKSTATION.get(), new Item.Properties()));
    }
}
