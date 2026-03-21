package com.minecraftmod.meeptech.items;

import com.minecraftmod.meeptech.blocks.pipes.PipeBlock;
import com.minecraftmod.meeptech.blocks.pipes.PipeBlockEntity;
import com.minecraftmod.meeptech.blocks.pipes.PipeConnection;
import com.minecraftmod.meeptech.blocks.pipes.PipeNetworkManager;
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
        if (!(state.getBlock() instanceof PipeBlock)) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        ServerLevel serverLevel = (ServerLevel)level;
        PipeConnection currentConnection = state.getValue(PipeBlock.CONNECTIONS.get(face));
        switch (currentConnection) {
            case NONE -> {
                BlockPos neighborPos = pos.relative(face);
                BlockState neighborState = level.getBlockState(neighborPos);
                if (!(neighborState.getBlock() instanceof PipeBlock)) {
                    return InteractionResult.FAIL;
                }
                level.setBlock(pos, state.setValue(PipeBlock.CONNECTIONS.get(face), PipeConnection.PIPE), Block.UPDATE_ALL);
                level.setBlock(neighborPos, neighborState.setValue(PipeBlock.CONNECTIONS.get(face.getOpposite()), PipeConnection.PIPE), Block.UPDATE_ALL);
                PipeNetworkManager.get(serverLevel).onPipesConnected(pos, neighborPos, serverLevel);
            }
            case PIPE -> {
                BlockPos neighborPos = pos.relative(face);
                BlockState neighborState = level.getBlockState(neighborPos);
                level.setBlock(pos, state.setValue(PipeBlock.CONNECTIONS.get(face), PipeConnection.NONE), Block.UPDATE_ALL);
                if (neighborState.getBlock() instanceof PipeBlock) {
                    level.setBlock(neighborPos, neighborState.setValue(PipeBlock.CONNECTIONS.get(face.getOpposite()), PipeConnection.NONE), Block.UPDATE_ALL);
                }
                PipeNetworkManager.get(serverLevel).onPipesDisconnected(pos, neighborPos, serverLevel);
            }
            case EXTRACTOR, INSERTER -> {
                if (player == null || !player.isShiftKeyDown()) {
                    return InteractionResult.PASS;
                }
                PipeBlockEntity be = (PipeBlockEntity)level.getBlockEntity(pos);
                if (be == null) return InteractionResult.FAIL;
                PipeConnection attachment = be.getFace(face);
                ItemStack stack = ItemStack.EMPTY;
                if (attachment == PipeConnection.EXTRACTOR) stack = new ItemStack(ModItems.EXTRACTOR.get());
                if (attachment == PipeConnection.INSERTER) stack = new ItemStack(ModItems.INSERTER.get());
                if (!player.addItem(stack)) {
                    Block.popResource(level, pos, stack);
                }
                be.setFace(face, PipeConnection.NONE);
                level.setBlock(pos, state.setValue(PipeBlock.CONNECTIONS.get(face), PipeConnection.NONE), Block.UPDATE_ALL);
                PipeNetworkManager.get(serverLevel).onAttachmentRemoved(pos, face, attachment, serverLevel);
            }
        }
        return InteractionResult.CONSUME;
    }
}