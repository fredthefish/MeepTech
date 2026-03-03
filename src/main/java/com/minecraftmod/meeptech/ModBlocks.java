package com.minecraftmod.meeptech;

import java.util.function.Supplier;

import com.minecraftmod.meeptech.Blocks.PrimitiveWorkstationBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MeepTech.MODID);

    public static final DeferredBlock<Block> PRIMITIVE_WORKSTATION = registerBlock("primitive_workstation",
        () -> new PrimitiveWorkstationBlock(BlockBehaviour.Properties.of()
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
        ModItems.ITEMS.register("primitive_workstation", () -> new BlockItem(PRIMITIVE_WORKSTATION.get(), new Item.Properties()));
    }
}
