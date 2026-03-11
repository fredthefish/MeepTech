package com.minecraftmod.meeptech.blocks;

import com.minecraftmod.meeptech.ModBlockEntities;
import com.minecraftmod.meeptech.items.MachineConfigData;
import com.minecraftmod.meeptech.logic.machine.MachineData;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BaseMachineBlockEntity extends BlockEntity {
    private MachineConfigData machineConfigData;
    private MachineData machineData;
    //TODO: OTHER STUFF (RECIPES/PROGRESS/MAX PROGRESS/HEAT/ETC)
    
    public BaseMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASE_MACHINE_BE.get(), pos, state);
    }
    private final ItemStackHandler inventory = new ItemStackHandler() {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public MachineConfigData getConfigData() {
        return machineConfigData;
    }
    public void setConfigData(MachineConfigData machineConfigData) {
        this.machineConfigData = machineConfigData;
        this.machineData = machineConfigData.toMachineData();
        int slotCount = machineData.getSlotCount();
        if (slotCount != inventory.getSlots()) {
            inventory.setSize(slotCount);
        }
        setChanged();
    }
    public MachineData getMachineData() {
        return machineData;
    }
    public ItemStackHandler getInventory() {
        return inventory;
    }
}
