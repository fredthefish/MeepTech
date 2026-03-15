package com.minecraftmod.meeptech.blocks;

import com.minecraftmod.meeptech.items.MachineConfigData;
import com.minecraftmod.meeptech.registries.ModBlockEntities;
import com.minecraftmod.meeptech.registries.ModDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BaseMachineBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public BaseMachineBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BaseMachineBlockEntity(pos, state, this);
    }
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof BaseMachineBlockEntity blockEntity) {
            MachineConfigData data = stack.getOrDefault(ModDataComponents.MACHINE_CONFIG_DATA.get(), MachineConfigData.EMPTY);
            blockEntity.setConfigData(data);
        }
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BaseMachineBlockEntity machine) {
                ItemStack thisDrop = new ItemStack(this);
                MachineConfigData machineConfigData = machine.getConfigData();
                if (machineConfigData != null) {
                    thisDrop.set(ModDataComponents.MACHINE_CONFIG_DATA.get(), machineConfigData);
                    for (int i = 0; i < machine.getInventory().getSlots(); i++) {
                        ItemStack stack = machine.getInventory().getStackInSlot(i);
                        if (!stack.isEmpty()) Block.popResource(level, pos, stack);
                    }
                }
                Block.popResource(level, pos, thisDrop);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BaseMachineBlockEntity menuProvider) {
                if (menuProvider.getMachineData() != null) {
                    player.openMenu(menuProvider, pos);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    @SuppressWarnings("unchecked")
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
        BlockEntityType<A> type, BlockEntityType<E> checkedType, BlockEntityTicker<? super E> ticker) {
        return checkedType == type ? (BlockEntityTicker<A>)ticker : null;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, getBlockEntityType(), BaseMachineBlockEntity::tick);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }
    private BlockEntityType<BaseMachineBlockEntity> getBlockEntityType() {
        for (DeferredBlock<Block> deferredBlock : ModBlockEntities.HULL_BLOCK_ENTITIES.keySet()) {
            if (deferredBlock.get() == this) return ModBlockEntities.HULL_BLOCK_ENTITIES.get(deferredBlock).get();
        }
        return null;
    }
}
