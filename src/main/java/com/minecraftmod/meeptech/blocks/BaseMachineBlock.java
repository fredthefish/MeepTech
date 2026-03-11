package com.minecraftmod.meeptech.blocks;

import com.minecraftmod.meeptech.ModDataComponents;
import com.minecraftmod.meeptech.items.MachineConfigData;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BaseMachineBlock extends Block implements EntityBlock {
    //TODO: BLOCK ENTITY RENDER
    public BaseMachineBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BaseMachineBlockEntity(pos, state);
    }
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof BaseMachineBlockEntity blockEntity) {
            MachineConfigData data = stack.getOrDefault(ModDataComponents.MACHINE_CONFIG_DATA.get(), MachineConfigData.EMPTY);
            blockEntity.setConfigData(data);
        }
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BaseMachineBlockEntity machine) {
                ItemStack thisDrop = new ItemStack(this);
                thisDrop.set(ModDataComponents.MACHINE_CONFIG_DATA.get(), machine.getConfigData());
                Block.popResource(level, pos, thisDrop);
                for (int i = 0; i < machine.getInventory().getSlots(); i++) {
                    ItemStack stack = machine.getInventory().getStackInSlot(i);
                    if (!stack.isEmpty()) Block.popResource(level, pos, stack);
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
