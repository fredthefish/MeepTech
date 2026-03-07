package com.minecraftmod.meeptech.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseMachineBlock extends Block implements EntityBlock {
    public BaseMachineBlock(Properties properties) {
        super(properties);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BaseMachineBlockEntity machine) {
                int inventorySize = machine.getInventorySize();
                for (int i = 0; i < inventorySize; i++) {
                    ItemStack stack = machine.inventory.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        Block.popResource(level, pos, stack);
                    }
                }
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }
}
