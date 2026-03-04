package com.minecraftmod.meeptech.ui;

import java.util.List;

import com.minecraftmod.meeptech.ModBlocks;
import com.minecraftmod.meeptech.ModDataComponents;
import com.minecraftmod.meeptech.ModItems;
import com.minecraftmod.meeptech.ModMachineTypes;
import com.minecraftmod.meeptech.ModMenus;
import com.minecraftmod.meeptech.logic.BlueprintData;
import com.minecraftmod.meeptech.logic.MachineType;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class DesigningStationMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final DataSlot selectedMachine = DataSlot.standalone();
    private final IItemHandler inventory;

    public DesigningStationMenu(int windowId, Inventory playerInv) {
        this(windowId, playerInv, new ItemStackHandler(2), ContainerLevelAccess.NULL);
    }
    public DesigningStationMenu(int windowId, Inventory playerInv, ItemStackHandler handler, ContainerLevelAccess access) {
        super(ModMenus.DESIGNING_STATION_MENU.get(), windowId);
        this.access = access;
        this.inventory = handler;
        this.selectedMachine.set(-1);

        this.addSlot(new SlotItemHandler(handler, 0, 16, 16) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.BLUEPRINT.get()) || stack.is(Items.PAPER);
            }
            @Override
            public void setChanged() {
                super.setChanged();
                setupResultSlot();
            }
        });
        this.addSlot(new SlotItemHandler(handler, 1, 16, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
            @Override
            public void onTake(Player player, ItemStack stack) {
                DesigningStationMenu.this.inventory.extractItem(0, 1, false);
                setupResultSlot();
                super.onTake(player, stack);
            }
        });
        this.addDataSlot(selectedMachine);

        //Add inventory/hotbar.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
        }

        setupResultSlot();
    }
    public int getSelectedMachine() {
        return this.selectedMachine.get();
    }
    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.DESIGNING_STATION.get());
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            if (index < 2) {
                if (!this.moveItemStackTo(stackInSlot, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stackInSlot.is(Items.PAPER) || stackInSlot.is(ModItems.BLUEPRINT.get())) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }
            if (stackInSlot.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
            if (stackInSlot.getCount() == itemStack.getCount()) return ItemStack.EMPTY;
            slot.onTake(player, stackInSlot);
        }
        return itemStack;
    }
    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId >= 0 && buttonId < ModMachineTypes.MACHINE_TYPES.size()) {
            if (buttonId == this.selectedMachine.get()) {
                this.selectedMachine.set(-1);
            } else {
                this.selectedMachine.set(buttonId);
            }
            setupResultSlot();
            return true;
        }
        return false;
    }
    private void setupResultSlot() {
        ItemStack paperSlot = this.inventory.getStackInSlot(0);
        int machineIndex = this.selectedMachine.get();
        if (!paperSlot.isEmpty() && (paperSlot.is(Items.PAPER) || paperSlot.is(ModItems.BLUEPRINT))) {
            if (machineIndex >= 0) {
                MachineType machineType = ModMachineTypes.MACHINE_TYPES.get(machineIndex);
                ItemStack blueprint = new ItemStack(ModItems.BLUEPRINT.get(), 1);
                blueprint.set(ModDataComponents.BLUEPRINT_DATA.get(), new BlueprintData(machineType.getId(), List.of()));
                ((ItemStackHandler)this.inventory).setStackInSlot(1, blueprint);
            } else {
                ItemStack blueprint = new ItemStack(ModItems.BLUEPRINT.get(), 1);
                ((ItemStackHandler)this.inventory).setStackInSlot(1, blueprint);
            }
        } else {
            ((ItemStackHandler)this.inventory).setStackInSlot(1, ItemStack.EMPTY);
        }
    }
}
