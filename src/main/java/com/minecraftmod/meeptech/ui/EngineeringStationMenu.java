package com.minecraftmod.meeptech.ui;

import java.util.List;

import com.minecraftmod.meeptech.logic.machine.MachineBase;
import com.minecraftmod.meeptech.logic.machine.MachineConfigData;
import com.minecraftmod.meeptech.logic.material.MaterialItemData;
import com.minecraftmod.meeptech.logic.module.ModuleSlotType;
import com.minecraftmod.meeptech.logic.module.ModuleType;
import com.minecraftmod.meeptech.network.EngineeringActionPacket;
import com.minecraftmod.meeptech.registries.ModBlocks;
import com.minecraftmod.meeptech.registries.ModDataComponents;
import com.minecraftmod.meeptech.registries.ModMenus;
import com.minecraftmod.meeptech.registries.ModTags;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
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
                int hullTier = ((MachineBase)mainData.getModuleType().getAttribute()).getModuleTier();
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
                            ModuleType moduleType = ModuleType.getModuleType(input);
                            int moduleTier = hullTier;
                            if (moduleType != null) moduleTier = moduleType.getModuleTier();
                            if (slotType.itemFitsSlotType(input) && input.getCount() >= edit.getCount() && moduleTier <= hullTier) {
                                inputSlot.remove(edit.getCount());
                                if (moduleType != null) {
                                    mainData = MachineConfigData.changeSubLayer(mainData, path, moduleType.getEmptyMachineConfigData());
                                } else {
                                    //If it isn't a module, it must be a component.
                                    MaterialItemData itemData = new MaterialItemData(input.getItem());
                                    mainData = MachineConfigData.changeSubLayer(mainData, path, 
                                        ModuleType.getMaterialMachineConfigData(slotType, itemData.getMaterial().getId()));
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
                                Item outputItem = data.getItem();
                                int upgradeSlots = data.upgradeSlots();
                                if (output.isEmpty() || (output.getCount() <= output.getMaxStackSize() + edit.getCount() && output.getItem().equals(outputItem))) {
                                    if (!output.isEmpty()) output.grow(edit.getCount());
                                    else outputSlot.set(new ItemStack(outputItem, edit.getCount()));
                                    mainData = MachineConfigData.changeSubLayer(mainData, path, MachineConfigData.EMPTY);
                                    mainData = mainData.setUpgradeSlots(mainData.upgradeSlots() + upgradeSlots);
                                    edit = edit.copy();
                                    edit.set(ModDataComponents.MACHINE_CONFIG_DATA.get(), mainData);
                                    editSlot.set(edit);
                                    this.broadcastChanges();
                                    return;
                                }
                            }
                        }
                    }
                }
                break;
            case EngineeringActionPacket.EngineeringAction.ADD_UPGRADE_SLOT:
                editSlot = this.getSlot(0);
                edit = editSlot.getItem();
                data = edit.get(ModDataComponents.MACHINE_CONFIG_DATA.get());
                data = MachineConfigData.addUpgradeSlot(data, path);
                edit = edit.copy();
                edit.set(ModDataComponents.MACHINE_CONFIG_DATA.get(), data);
                editSlot.set(edit);
                this.broadcastChanges();
                break;
            case EngineeringActionPacket.EngineeringAction.REMOVE_UPGRADE_SLOT:
                editSlot = this.getSlot(0);
                edit = editSlot.getItem();
                data = edit.get(ModDataComponents.MACHINE_CONFIG_DATA.get());
                data = MachineConfigData.removeUpgradeSlot(data, path);
                edit = edit.copy();
                edit.set(ModDataComponents.MACHINE_CONFIG_DATA.get(), data);
                editSlot.set(edit);
                this.broadcastChanges();
                break;
            case EngineeringActionPacket.EngineeringAction.INSERT_UPGRADE:
                editSlot = this.getSlot(0);
                inputSlot = this.getSlot(1);
                edit = editSlot.getItem();
                input = inputSlot.getItem();
                type = ModuleType.getModuleType(edit);
                mainData = (edit.has(ModDataComponents.MACHINE_CONFIG_DATA.get())) ? 
                    edit.get(ModDataComponents.MACHINE_CONFIG_DATA.get()) : type.getEmptyMachineConfigData();
                hullTier = ((MachineBase)mainData.getModuleType().getAttribute()).getModuleTier();
                data = mainData;
                layer = 0;
                while (path.size() >= layer) {
                    if (path.size() > layer) {
                        type = data.getModuleType();
                        data = data.getSubLayer(path.get(layer));
                        layer++;
                    } else {
                        if ((data == null || data.isEmpty()) && !input.isEmpty()) {
                            slotType = type.getUpgradeType();
                            ModuleType moduleType = ModuleType.getModuleType(input);
                            int moduleTier = hullTier;
                            if (moduleType != null) moduleTier = moduleType.getModuleTier();
                            if (slotType.itemFitsSlotType(input) && input.getCount() >= edit.getCount() && moduleTier <= hullTier) {
                                inputSlot.remove(edit.getCount());
                                if (moduleType != null) {
                                    mainData = MachineConfigData.changeSubLayer(mainData, path, moduleType.getEmptyMachineConfigData());
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
            case EngineeringActionPacket.EngineeringAction.EXTRACT_UPGRADE:
                editSlot = this.getSlot(0);
                edit = editSlot.getItem();
                outputSlot = this.getSlot(2);
                output = outputSlot.getItem();
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
                                Item outputItem = data.getItem();
                                int upgradeSlots = data.upgradeSlots();
                                if (output.isEmpty() || (output.getCount() <= output.getMaxStackSize() + edit.getCount() && output.getItem().equals(outputItem))) {
                                    if (!output.isEmpty()) output.grow(edit.getCount());
                                    else outputSlot.set(new ItemStack(outputItem, edit.getCount()));
                                    mainData = MachineConfigData.changeSubLayer(mainData, path, MachineConfigData.EMPTY);
                                    mainData = mainData.setUpgradeSlots(mainData.upgradeSlots() + upgradeSlots);
                                    edit = edit.copy();
                                    edit.set(ModDataComponents.MACHINE_CONFIG_DATA.get(), mainData);
                                    editSlot.set(edit);
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
