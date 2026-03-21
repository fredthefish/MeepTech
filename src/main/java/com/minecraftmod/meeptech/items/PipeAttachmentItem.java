package com.minecraftmod.meeptech.items;

import com.minecraftmod.meeptech.blocks.pipes.PipeBlock;
import com.minecraftmod.meeptech.blocks.pipes.PipeBlockEntity;
import com.minecraftmod.meeptech.blocks.pipes.PipeConnection;
import com.minecraftmod.meeptech.blocks.pipes.PipeNetworkManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PipeAttachmentItem extends Item {
    private final PipeConnection type;
    public PipeAttachmentItem(PipeConnection type, Properties properties) {
        super(properties);
        this.type = type;
    }
    public PipeConnection getType() {
        return type;
    }
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos clickedPos = ctx.getClickedPos();
        Direction clickedFace = ctx.getClickedFace();
        BlockState clickedState = level.getBlockState(clickedPos);
        // Clicked from a pipe.
        if (clickedState.getBlock() instanceof PipeBlock) {
            return tryAttach(level, clickedPos, clickedFace, ctx);
        }
        // Clicked from an inventory adjacent to a pipe.
        BlockPos adjacentPipePos = clickedPos.relative(clickedFace);
        BlockState adjacentState = level.getBlockState(adjacentPipePos);
        if (adjacentState.getBlock() instanceof PipeBlock) {
            return tryAttach(level, adjacentPipePos, clickedFace.getOpposite(), ctx);
        }
        return InteractionResult.PASS;
    }
    private InteractionResult tryAttach(Level level, BlockPos pipePos, Direction face, UseOnContext ctx) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        ServerLevel serverLevel = (ServerLevel) level;
        BlockState pipeState = level.getBlockState(pipePos);
        PipeConnection currentConnection = pipeState.getValue(PipeBlock.CONNECTIONS.get(face));
        if (currentConnection != PipeConnection.NONE) return InteractionResult.FAIL;
        // BlockPos inventoryPos = pipePos.relative(face);
        // if (serverLevel.getCapability(Capabilities.ItemHandler.BLOCK, inventoryPos, face.getOpposite()) == null) return InteractionResult.FAIL;
        PipeBlockEntity be = (PipeBlockEntity) level.getBlockEntity(pipePos);
        if (be == null) return InteractionResult.FAIL;
        be.setFace(face, type);
        level.setBlock(pipePos, pipeState.setValue(PipeBlock.CONNECTIONS.get(face), type), Block.UPDATE_ALL);
        PipeNetworkManager.get(serverLevel).onAttachmentAdded(pipePos, face, type, serverLevel);
        Player player = ctx.getPlayer();
        if (player != null && !player.isCreative()) {
            player.getItemInHand(ctx.getHand()).shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}
