package com.minecraftmod.meeptech.ui;

import java.util.List;

import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.logic.machine.HeatSource;
import com.minecraftmod.meeptech.logic.machine.MachineAttribute;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.ui.SlotType;
import com.minecraftmod.meeptech.logic.ui.SlotUIElement;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;
import com.minecraftmod.meeptech.logic.ui.UIModuleType;
import com.minecraftmod.meeptech.network.MachineContainerData;
import com.minecraftmod.meeptech.registries.ModMenus;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class MachineMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final BaseMachineBlockEntity blockEntity;
    private final MachineContainerData machineContainerData;

    public MachineMenu(int windowId, Inventory playerInv, RegistryFriendlyByteBuf buf) {
        this(windowId, playerInv, getClientBlockEntity(playerInv, buf), ContainerLevelAccess.NULL);
    }
    private static BaseMachineBlockEntity getClientBlockEntity(Inventory playerInv, RegistryFriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity blockEntity = playerInv.player.level().getBlockEntity(pos);
        if (blockEntity instanceof BaseMachineBlockEntity machine) return machine;
        throw new IllegalStateException("Machine Block Entity not found on client at " + pos);
    }
    public MachineMenu(int windowId, Inventory playerInv, BaseMachineBlockEntity blockEntity, ContainerLevelAccess access) {
        super(ModMenus.MACHINE_MENU.get(), windowId);
        this.blockEntity = blockEntity;
        this.machineContainerData = blockEntity.getTrackedData();
        this.access = access;

        this.addDataSlots(machineContainerData);

        if (blockEntity != null) {
            if (this.blockEntity.getMachineData() != null) {
                MachineData machineData = this.blockEntity.getMachineData();
                List<SlotUIElement> uiSlots = machineData.getSlots();
                int i = 0;
                for (SlotUIElement slot : uiSlots) {
                    SlotItemHandler newSlot = new SlotItemHandler(blockEntity.getInventory(), i, slot.getX() + 1, slot.getY() + 1) {
                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            if (slot.getType() == SlotType.INPUT) {
                                return true;
                            } else if (slot.getType() == SlotType.INPUT_FUEL) {
                                MachineAttribute energySource = machineData.getEnergySource();
                                if (energySource instanceof HeatSource heatSource) {
                                    return stack.getBurnTime(RecipeType.SMELTING) > 0 
                                        || heatSource.getHeatType().getRecipe(List.of(stack), List.of()) != null;
                                }
                                return false;
                            }
                            return false;
                        }
                    };
                    this.addSlot(newSlot);
                    i++;
                }
            }
        }

        //Add inventory/hotbar.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 124 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 182));
        }
    }
    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, this.blockEntity.getBlock());
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            MachineData machineData = this.blockEntity.getMachineData();
            int slotCount = machineData.getSlotCount();
            if (index < slotCount) {
                if (!this.moveItemStackTo(stackInSlot, slotCount, slotCount + 36, true)) return ItemStack.EMPTY;
            } else {
                int start = machineData.getStartSlot(UIModuleType.Energy);
                int end = machineData.getStartSlot(UIModuleType.Recipe);
                if (!this.moveItemStackTo(stackInSlot, start, end, false)) {
                    start = machineData.getStartSlot(UIModuleType.Input);
                    end = machineData.getStartSlot(UIModuleType.Output);
                    if (!this.moveItemStackTo(stackInSlot, start, end, false)) return ItemStack.EMPTY;
                }
            }
            if (stackInSlot.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
            if (stackInSlot.getCount() == itemStack.getCount()) return ItemStack.EMPTY;
            slot.onTake(player, stackInSlot);
        }
        return itemStack;
    }
    public MachineData getMachineData() {
        return blockEntity.getMachineData();
    }
    public int getProgress() {
        return machineContainerData.getFromStat(TrackedStat.RecipeProgress);
    }
    public int getMaxProgress() {
        return machineContainerData.getFromStat(TrackedStat.RecipeMaxProgress);
    }
    public int getHeat() {
        return machineContainerData.getFromStat(TrackedStat.HeatLeft);
    }
}
