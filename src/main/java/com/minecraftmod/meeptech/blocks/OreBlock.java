package com.minecraftmod.meeptech.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class OreBlock extends Block {
    public static final EnumProperty<OreStoneType> STONE_TYPE = EnumProperty.create("stone_type", OreStoneType.class);
    public OreBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(STONE_TYPE, OreStoneType.STONE));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STONE_TYPE);
    }
}
