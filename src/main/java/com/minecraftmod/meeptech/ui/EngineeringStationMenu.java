package com.minecraftmod.meeptech.ui;

import java.util.List;

import com.minecraftmod.meeptech.ModBlocks;
import com.minecraftmod.meeptech.ModDataComponents;
import com.minecraftmod.meeptech.ModMenus;
import com.minecraftmod.meeptech.ModTags;
import com.minecraftmod.meeptech.items.MachineConfigData;
import com.minecraftmod.meeptech.logic.MaterialItemData;
import com.minecraftmod.meeptech.logic.ModuleSlotType;
import com.minecraftmod.meeptech.logic.ModuleType;
import com.minecraftmod.meeptech.network.EngineeringActionPacket;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class EngineeringStationMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public EngineeringStationMenu(int windowId, Inventory playerInv) {
        this(windowId, playerInv, new ItemStackHandler(3), ContainerLevelAccess.NULL);
    }
    public EngineeringStationMenu(int windowId, Inventory playerInv, ItemStackHandler handler, ContainerLevelAccess access) {
        super(ModMenus.ENGINEERING_STATION_MENU.get(), windowId);
        this.access = access;

        //Machine Slot.
        this.addSlot(new SlotItemHandler(handler, 0, 41, 148) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (stack.is(ModTags.HULL_TAG)) return true;
                return false;
            }
        });
        //Input Slot.
        this.addSlot(new SlotItemHandler(handler, 1, 95, 148));
        //Output Slot.
        this.addSlot(new SlotItemHandler(handler, 2, 149, 148) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        //Add inventory/hotbar.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 23 + j * 18, 170 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 23 + i * 18, 228));
        }
    }
    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.ENGINEERING_STATION.get());
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            if (index < 3) {
                if (!this.moveItemStackTo(stackInSlot, 3, 39, true)) return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, 2, false)) return ItemStack.EMPTY;
            }
            if (stackInSlot.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            if (stackInSlot.getCount() == itemStack.getCount()) return ItemStack.EMPTY;
        }

        return itemStack;
    }
    public void handleVirtualAction(EngineeringActionPacket.EngineeringAction action, List<Integer> path) {
        switch (action) {
            case EngineeringActionPacket.EngineeringAction.INSERT:
                Slot editSlot = this.getSlot(0);
                Slot inputSlot = this.getSlot(1);
                ItemStack edit = editSlot.getItem();
                ItemStack input = inputSlot.getItem();
                ModuleType type = ModuleType.getModuleType(edit);
                MachineConfigData mainData = (edit.has(ModDataComponents.MACHINE_CONFIG_DATA.get())) ? 
                edit.get(ModDataComponents.MACHINE_CONFIG_DATA.get()) : type.getEmptyMachineConfigData();
                MachineConfigData data = mainData;
                ModuleSlotType slotType = null;
                int layer = 0;
                while (path.size() >= layer) {
                    if (path.size() > layer) {
                        slotType = data.getModuleType().getSubSlot(path.get(layer)).getType();
                        data = data.getSubLayer(path.get(layer));

                        layer++;
                    } else {
                        if (data.isEmpty() && !input.isEmpty()) {
                            System.out.println("MEAPEENATION");
                            System.out.println(data);
                            if (ModuleType.itemFitsSlotType(input, slotType)) {
                                ModuleType inputModuleType = ModuleType.getModuleType(input);
                                inputSlot.remove(1);
                                if (inputModuleType != null) {
                                    mainData = MachineConfigData.changeSubLayer(mainData, path, inputModuleType.getEmptyMachineConfigData());
                                } else {
                                    //If it isn't a module, it must be a component.
                                    MaterialItemData itemData = new MaterialItemData(input.getItem());
                                    mainData = MachineConfigData.changeSubLayer(mainData, path, ModuleType.getMaterialMachineConfigData(itemData.getMaterial().getId()));
                                }
                                edit = edit.copy();
                                edit.set(ModDataComponents.MACHINE_CONFIG_DATA.get(), mainData);
                                editSlot.set(edit);
                                this.broadcastChanges();
                                return;
                            }
                        }
                    }
                }
                break;
            case EngineeringActionPacket.EngineeringAction.EXTRACT:
                editSlot = this.getSlot(0);
                edit = editSlot.getItem();
                Slot outputSlot = this.getSlot(2);
                ItemStack output = outputSlot.getItem();
                type = ModuleType.getModuleType(edit);
                mainData = (edit.has(ModDataComponents.MACHINE_CONFIG_DATA.get())) ? 
                    edit.get(ModDataComponents.MACHINE_CONFIG_DATA.get()) : type.getEmptyMachineConfigData();
                data = mainData;
                layer = 0;
                while (path.size() >= layer) {
                    if (path.size() > layer) {
                        data = data.getSubLayer(path.get(layer));
                        layer++;
                    } else {
                        if (!data.isEmpty()) {
                            if (!data.hasSubLayers()) {
                                ItemStack outputItem = data.getItemStack(type.getType());
                                if (output.isEmpty() || (output.getCount() <= output.getMaxStackSize() + 1 && output.getItem().equals(outputItem.getItem()))) {
                                    if (!output.isEmpty()) output.grow(1);
                                    else outputSlot.set(outputItem);
                                    mainData = MachineConfigData.changeSubLayer(mainData, path, MachineConfigData.EMPTY);
                                    edit = edit.copy();
                                    edit.set(ModDataComponents.MACHINE_CONFIG_DATA.get(), mainData);
                                    editSlot.set(edit);
                                    //TODO: Remove data component from slot if empty.
                                    this.broadcastChanges();
                                    return;
                                }
                            }
                        }
                    }
                }
                break;
        }
    }
}
