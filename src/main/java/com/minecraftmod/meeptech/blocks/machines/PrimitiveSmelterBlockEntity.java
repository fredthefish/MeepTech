package com.minecraftmod.meeptech.blocks.machines;

import com.minecraftmod.meeptech.ModBlockEntities;
import com.minecraftmod.meeptech.ModMachineTypes;
import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.logic.MachineType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PrimitiveSmelterBlockEntity extends BaseMachineBlockEntity {
    public PrimitiveSmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRIMITIVE_SMELTER_BE.get(), pos, state);
    }
    @Override
    public MachineType getMachineType() {
        return ModMachineTypes.PRIMITIVE_SMELTER;
    }
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        return null;
    }
    public static void tick(Level level, BlockPos pos, BlockState state, PrimitiveSmelterBlockEntity entity) {
        if (level.isClientSide) return;
        boolean currentLit = state.getValue(BlockStateProperties.LIT);
        boolean updateLit = false;
        if (currentLit != updateLit) {
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, updateLit), 3);
        }
    }
}
