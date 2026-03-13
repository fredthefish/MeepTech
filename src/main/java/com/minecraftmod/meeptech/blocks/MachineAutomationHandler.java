package com.minecraftmod.meeptech.blocks;

import com.minecraftmod.meeptech.logic.ui.SlotType;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class MachineAutomationHandler implements IItemHandler {
    private final BaseMachineBlockEntity entity;
    public MachineAutomationHandler(BaseMachineBlockEntity entity) {
        this.entity = entity;
    }
    @Override
    public int getSlots() {
        return entity.getInventory().getSlots();
    }
    @Override
    public ItemStack getStackInSlot(int slot) {
        return entity.getInventory().getStackInSlot(slot);
    }
    @Override
    public int getSlotLimit(int slot) {
        return entity.getInventory().getSlotLimit(slot);
    }
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        SlotType slotType = entity.getSlotType(slot);
        if (slotType != SlotType.OUTPUT) return entity.getInventory().insertItem(slot, stack, simulate);
        return stack; //Ouput cannot be inserted into.
    }
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        SlotType slotType = entity.getSlotType(slot);
        if (slotType == SlotType.OUTPUT) return entity.getInventory().extractItem(slot, amount, simulate);
        return ItemStack.EMPTY; //Input cannot be extracted from.
    }
    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        SlotType slotType = entity.getSlotType(slot);
        return (slotType != SlotType.OUTPUT) && entity.getInventory().isItemValid(slot, stack);
    }
}
