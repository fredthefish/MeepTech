package com.minecraftmod.meeptech.Blocks;

import com.minecraftmod.meeptech.ModBlockEntities;
import com.minecraftmod.meeptech.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class MaterialWorkstationBlockEntity extends BlockEntity {
    public MaterialWorkstationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRIMITIVE_WORKSTATION_BE.get(), pos, state);
    }

    public final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            //Hammer slot.
            if (slot == 0) {
                return stack.is(ModItems.HAMMER.get());
            }
            //Output slot.
            if (slot == 2) {
                return false;
            }
            return super.isItemValid(slot, stack);
        }
    };
}
