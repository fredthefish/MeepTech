package com.minecraftmod.meeptech.blocks.pipes;

import org.checkerframework.checker.units.qual.A;

import com.minecraftmod.meeptech.registries.ModBlockEntities;
import com.minecraftmod.meeptech.registries.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ItemPipeBlock extends PipeBlock {
    public ItemPipeBlock(Properties props) {
        super(props);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (newState.getBlock() instanceof PipeBlock) {
            super.onRemove(state, level, pos, newState, movedByPiston);
            return;
        }
        if (level instanceof ServerLevel serverLevel) {
            PipeBlockEntity be = (PipeBlockEntity)level.getBlockEntity(pos);
            if (be != null) {
                for (Direction dir : Direction.values()) {
                    PipeConnection connection = be.getFace(dir);
                    if (connection == PipeConnection.EXTRACTOR) {
                        ItemStack drop = new ItemStack(ModItems.ITEM_EXTRACTOR.get());
                        Block.popResource(level, pos, drop);
                    } else if (connection == PipeConnection.INSERTER) {
                        ItemStack drop = new ItemStack(ModItems.ITEM_INSERTER.get());
                        Block.popResource(level, pos, drop);
                    }
                }
            }
            PipeNetworkManager.get(serverLevel).onPipeRemoved(pos, state, serverLevel);
        }
        for (Direction dir : Direction.values()) {
            if (state.getValue(CONNECTIONS.get(dir)) != PipeConnection.PIPE) continue;
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (!(neighborState.getBlock() instanceof PipeBlock)) continue;
            level.setBlock(neighborPos, neighborState.setValue(CONNECTIONS.get(dir.getOpposite()), PipeConnection.NONE), UPDATE_ALL);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
    @SuppressWarnings({ "unchecked", "hiding" })
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
        BlockEntityType<A> type, BlockEntityType<E> checkedType, BlockEntityTicker<? super E> ticker) {
        return checkedType == type ? (BlockEntityTicker<A>)ticker : null;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.PIPE_BE.get(), PipeBlockEntity::tickItem);
    }
}
