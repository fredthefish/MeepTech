package com.minecraftmod.meeptech.blocks.pipes;

import java.util.EnumMap;
import java.util.Map;

import com.minecraftmod.meeptech.blocks.pipes.PipeBlockEntity.PipeType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class PipeBlock extends Block implements EntityBlock {
    private final PipeType type;
    public static final Map<Direction, EnumProperty<PipeConnection>> CONNECTIONS = new EnumMap<>(Direction.class);
    static {
        for (Direction dir : Direction.values()) {
            CONNECTIONS.put(dir, EnumProperty.create(dir.getSerializedName(), PipeConnection.class));
        }
    }
    public PipeBlock(Properties props, PipeType type) {
        super(props);
        this.type = type;
        BlockState defaultState = stateDefinition.any();
        for (EnumProperty<PipeConnection> prop : CONNECTIONS.values()) {
            defaultState = defaultState.setValue(prop, PipeConnection.NONE);
        }
        registerDefaultState(defaultState);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        CONNECTIONS.values().forEach(builder::add);
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state, type);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = defaultBlockState();
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        // Auto-connect from adjacent pipe.
        Direction placedAgainst = ctx.getClickedFace().getOpposite();
        BlockPos neighborPos = pos.relative(placedAgainst);
        BlockState neighborState = level.getBlockState(neighborPos);
        if (neighborState.getBlock() instanceof PipeBlock) {
            PipeConnection connection = neighborState.getValue(CONNECTIONS.get(placedAgainst.getOpposite()));
            if (connection != PipeConnection.NONE) return state;
            state = state.setValue(CONNECTIONS.get(placedAgainst), PipeConnection.PIPE);
        } else if (level instanceof ServerLevel serverLevel) {
            PipeNetworkManager.get(serverLevel).onPipeAdded(pos);
        }
        return state;
    }
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        for (Direction dir : Direction.values()) {
            if (state.getValue(CONNECTIONS.get(dir)) != PipeConnection.PIPE) continue;
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (!(neighborState.getBlock() instanceof PipeBlock)) continue;
            level.setBlock(neighborPos, neighborState.setValue(CONNECTIONS.get(dir.getOpposite()), PipeConnection.PIPE), Block.UPDATE_ALL);
            if (level instanceof ServerLevel serverLevel) {
                PipeNetworkManager.get(serverLevel).onPipesConnected(pos, neighborPos, serverLevel);
            }
        }
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.box(6/16f, 6/16f, 6/16f, 10/16f, 10/16f, 10/16f);
        for (Direction dir : Direction.values()) {
            if (state.getValue(CONNECTIONS.get(dir)) == PipeConnection.NONE) continue;
            shape = Shapes.or(shape, getArmShape(dir));
        }
        return shape;
    }
    private static VoxelShape getArmShape(Direction dir) {
        return switch (dir) {
            case DOWN  -> Shapes.box(6/16f, 0,     6/16f, 10/16f, 6/16f,  10/16f);
            case UP    -> Shapes.box(6/16f, 10/16f, 6/16f, 10/16f, 1f,    10/16f);
            case NORTH -> Shapes.box(6/16f, 6/16f, 0,     10/16f, 10/16f, 6/16f);
            case SOUTH -> Shapes.box(6/16f, 6/16f, 10/16f, 10/16f, 10/16f, 1f);
            case WEST  -> Shapes.box(0,     6/16f, 6/16f, 6/16f,  10/16f, 10/16f);
            case EAST  -> Shapes.box(10/16f, 6/16f, 6/16f, 1f,    10/16f, 10/16f);
        };
    }
    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
}
