package com.minecraftmod.meeptech.items;

import com.minecraftmod.meeptech.blocks.pipes.PipeBlock;
import com.minecraftmod.meeptech.blocks.pipes.PipeBlockEntity;
import com.minecraftmod.meeptech.blocks.pipes.PipeConnection;
import com.minecraftmod.meeptech.blocks.pipes.PipeNetworkManager;
import com.minecraftmod.meeptech.blocks.pipes.PipeBlockEntity.PipeType;
import com.minecraftmod.meeptech.registries.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WrenchItem extends Item {
    public WrenchItem(Properties props) {
        super(props);
    }
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Direction face = ctx.getClickedFace();
        Player player = ctx.getPlayer();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof PipeBlock pipeBlock) {
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            ServerLevel serverLevel = (ServerLevel)level;
            Vec3 hitPos = ctx.getClickLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            Direction clickedConnection = getClickedConnection(state, face, hitPos);
            return handleWrenchAction(state, pos, clickedConnection, player, level, serverLevel, pipeBlock);
        } return InteractionResult.PASS;
    }
    private Direction getClickedConnection(BlockState state, Direction face, Vec3 hitPos) {
        for (Direction dir : Direction.values()) {
            if (state.getValue(PipeBlock.CONNECTIONS.get(dir)) == PipeConnection.NONE) 
                continue;
            if (isHittingArm(dir, hitPos)) return dir;
        }
        return face;
    }
    private boolean isHittingArm(Direction dir, Vec3 hitPos) {
        float min = 6f / 16f;
        float max = 10f / 16f;
        return switch (dir) {
            case SOUTH -> hitPos.z >= max
                && hitPos.x >= min && hitPos.x <= max
                && hitPos.y >= min && hitPos.y <= max;
            case NORTH -> hitPos.z <= min
                && hitPos.x >= min && hitPos.x <= max
                && hitPos.y >= min && hitPos.y <= max;
            case EAST -> hitPos.x >= max
                && hitPos.z >= min && hitPos.z <= max
                && hitPos.y >= min && hitPos.y <= max;
            case WEST -> hitPos.x <= min
                && hitPos.z >= min && hitPos.z <= max
                && hitPos.y >= min && hitPos.y <= max;
            case UP -> hitPos.y >= max
                && hitPos.x >= min && hitPos.x <= max
                && hitPos.z >= min && hitPos.z <= max;
            case DOWN -> hitPos.y <= min
                && hitPos.x >= min && hitPos.x <= max
                && hitPos.z >= min && hitPos.z <= max;
        };
    }
    private InteractionResult handleWrenchAction(BlockState state, BlockPos pos, Direction clickedDir, Player player, 
            Level level, ServerLevel serverLevel, PipeBlock pipeBlock) {
        PipeConnection currentConnection = state.getValue(PipeBlock.CONNECTIONS.get(clickedDir));
        switch (currentConnection) {
            case NONE -> {
                BlockPos neighborPos = pos.relative(clickedDir);
                BlockState neighborState = level.getBlockState(neighborPos);
                if (!(neighborState.getBlock() instanceof PipeBlock neighborBlock)) return InteractionResult.FAIL;
                if (neighborBlock.getPipeType() != pipeBlock.getPipeType()) return InteractionResult.FAIL;
                PipeConnection neighborConnection = neighborState.getValue(PipeBlock.CONNECTIONS.get(clickedDir.getOpposite()));
                if (neighborConnection != PipeConnection.NONE) return InteractionResult.FAIL;
                level.setBlock(pos, state.setValue(PipeBlock.CONNECTIONS.get(clickedDir), PipeConnection.PIPE), Block.UPDATE_ALL);
                level.setBlock(neighborPos, neighborState.setValue(PipeBlock.CONNECTIONS.get(clickedDir.getOpposite()), PipeConnection.PIPE), Block.UPDATE_ALL);
                PipeNetworkManager.get(serverLevel).onPipesConnected(pos, neighborPos, serverLevel, pipeBlock.getPipeType());
            }
            case PIPE -> {
                if (player != null && player.isShiftKeyDown()) return InteractionResult.PASS;
                BlockPos neighborPos = pos.relative(clickedDir);
                BlockState neighborState = level.getBlockState(neighborPos);
                BlockState oldState = state;
                BlockState oldNeighborState = neighborState;
                level.setBlock(pos, state.setValue(PipeBlock.CONNECTIONS.get(clickedDir), PipeConnection.NONE), Block.UPDATE_ALL);
                if (neighborState.getBlock() instanceof PipeBlock) {
                    level.setBlock(neighborPos, neighborState.setValue(PipeBlock.CONNECTIONS.get(clickedDir.getOpposite()), PipeConnection.NONE), Block.UPDATE_ALL);
                }
                PipeNetworkManager.get(serverLevel).onPipesDisconnected(pos, neighborPos, oldState, oldNeighborState, serverLevel, pipeBlock.getPipeType());
            }
            case EXTRACTOR, INSERTER -> {
                if (player == null || !player.isShiftKeyDown()) return InteractionResult.PASS;
                PipeBlockEntity be = (PipeBlockEntity)level.getBlockEntity(pos);
                if (be == null) return InteractionResult.FAIL;
                ItemStack stack = ItemStack.EMPTY;
                if (pipeBlock.getPipeType() == PipeType.ITEM) {
                    if (currentConnection == PipeConnection.EXTRACTOR) stack = new ItemStack(ModItems.ITEM_EXTRACTOR.get());
                    if (currentConnection == PipeConnection.INSERTER) stack = new ItemStack(ModItems.ITEM_INSERTER.get());
                } else if (pipeBlock.getPipeType() == PipeType.FLUID) {
                    if (currentConnection == PipeConnection.EXTRACTOR) stack = new ItemStack(ModItems.FLUID_EXTRACTOR.get());
                    if (currentConnection == PipeConnection.INSERTER) stack = new ItemStack(ModItems.FLUID_INSERTER.get());
                }
                if (!player.addItem(stack)) Block.popResource(level, pos, stack);
                be.setFace(clickedDir, PipeConnection.NONE);
                level.setBlock(pos, state.setValue(PipeBlock.CONNECTIONS.get(clickedDir), PipeConnection.NONE), Block.UPDATE_ALL);
                PipeNetworkManager.get(serverLevel).onAttachmentRemoved(pos, clickedDir, currentConnection, serverLevel);
            }
        }
        return InteractionResult.CONSUME;
    }
}