package com.minecraftmod.meeptech.blocks;

import com.minecraftmod.meeptech.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DraftingStationBlockEntity extends BlockEntity {
    public DraftingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRAFTING_STATION_BE.get(), pos, state);
    }
}
