package com.minecraftmod.meeptech.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;

public class FluidTankBlock extends Block implements EntityBlock {
    private final int capacity;
    public FluidTankBlock(Properties props, int capacity) {
        super(props);
        this.capacity = capacity;
    }
    public int getCapacity() {
        return capacity; 
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidTankBlockEntity(pos, state, capacity);
    }
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof FluidTankBlockEntity tankEntity)) return InteractionResult.PASS;
        ItemStack heldItem = player.getMainHandItem();
        if (FluidUtil.getFluidHandler(heldItem).isPresent()) {
            return tankEntity.useBucket(player, InteractionHand.MAIN_HAND);
        }
        if (level instanceof ServerLevel) {
            player.openMenu(tankEntity, pos);
        }
        return InteractionResult.CONSUME;
    }
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data != null && level.getBlockEntity(pos) instanceof FluidTankBlockEntity tankEntity) {
            tankEntity.loadAdditional(data.copyTag(), level.registryAccess());
        }
    }
}
