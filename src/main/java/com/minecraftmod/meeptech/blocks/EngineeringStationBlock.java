package com.minecraftmod.meeptech.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class EngineeringStationBlock extends Block implements EntityBlock {
    public EngineeringStationBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EngineeringStationBlockEntity(pos, state);
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof EngineeringStationBlockEntity menuProvider) {
                player.openMenu(menuProvider, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof EngineeringStationBlockEntity station) {
                for (int i = 0; i < station.inventory.getSlots(); i++) {
                    ItemStack stack = station.inventory.getStackInSlot(i);
                    if (!stack.isEmpty()) Block.popResource(level, pos, stack);
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
