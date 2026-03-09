package com.minecraftmod.meeptech.ui;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.ModBlocks;
import com.minecraftmod.meeptech.ModDataComponents;
import com.minecraftmod.meeptech.ModItems;
import com.minecraftmod.meeptech.ModMenus;
import com.minecraftmod.meeptech.items.BlueprintData;
import com.minecraftmod.meeptech.logic.MachineType;
import com.minecraftmod.meeptech.logic.Material;
import com.minecraftmod.meeptech.logic.MaterialItemData;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class DraftingStationMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final DataSlot selectedComponent = DataSlot.standalone();
    private final IItemHandler inventory;

    public DraftingStationMenu(int windowId, Inventory playerInv) {
        this(windowId, playerInv, new ItemStackHandler(2), ContainerLevelAccess.NULL);
    }
    public DraftingStationMenu(int windowId, Inventory playerInv, ItemStackHandler handler, ContainerLevelAccess access) {
        super(ModMenus.DRAFTING_STATION_MENU.get(), windowId);
        this.access = access;
        this.inventory = handler;
        this.selectedComponent.set(-1);

        //Blueprint slot.
        this.addSlot(new SlotItemHandler(handler, 0, 16, 29) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (stack.is(ModItems.BLUEPRINT.get())) {
                    if (!stack.has(ModDataComponents.BLUEPRINT_DATA.get())) return false;
                    BlueprintData blueprintData = stack.get(ModDataComponents.BLUEPRINT_DATA.get());
                    return blueprintData.getMachineType() != null;
                }
                return false;
            }
        });

        //Material slot.
        this.addSlot(new SlotItemHandler(handler, 1, 16, 56));

        //Add inventory/hotbar.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 124 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 182));
        }

        this.addDataSlot(selectedComponent);
    }
    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.DRAFTING_STATION.get());
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            if (index < 2) {
                if (!this.moveItemStackTo(stackInSlot, 2, 38, true)) return ItemStack.EMPTY;
            } else {
                if (stackInSlot.is(ModItems.BLUEPRINT.get())) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) return ItemStack.EMPTY;
                } else {
                    if (!this.moveItemStackTo(stackInSlot, 1, 2, false)) return ItemStack.EMPTY;
                }
            }
            if (stackInSlot.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            if (stackInSlot.getCount() == itemStack.getCount()) return ItemStack.EMPTY;
        }

        return itemStack;
    }
    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        ItemStack blueprint = this.inventory.getStackInSlot(0);
        if (buttonId == 1000) {
            if (blueprint.isEmpty()) return false;
            BlueprintData data = blueprint.get(ModDataComponents.BLUEPRINT_DATA.get());
            int selected = this.selectedComponent.get();
            ItemStack materialStack = this.inventory.getStackInSlot(1);
            if (data != null && selected >= 0) {
                if (materialStack.isEmpty()) {
                    List<String> materialIds = new ArrayList<String>(data.getMaterialList());
                    if (materialIds != null && !materialIds.isEmpty()) {
                        materialIds.set(selected, "");
                        boolean eachIsEmpty = true;
                        for (String eachMaterial : materialIds) {
                            eachIsEmpty &= eachMaterial.isEmpty();
                        }
                        if (eachIsEmpty) materialIds.clear();
                        MachineType machineType = data.getMachineType();
                        ItemStack newBlueprint = new ItemStack(ModItems.BLUEPRINT.get(), blueprint.getCount());
                        newBlueprint.set(ModDataComponents.BLUEPRINT_DATA.get(), new BlueprintData(machineType.getId(), materialIds));
                        ((ItemStackHandler)this.inventory).setStackInSlot(0, newBlueprint);
                    }
                } else {
                    MachineType machineType = data.getMachineType();
                    MaterialItemData itemData = new MaterialItemData(materialStack.getItem());
                    Material material = itemData.getMaterial();
                    if (material != null) {
                        if (material.getId() != null) {
                            List<String> materialIds = new ArrayList<String>(data.getMaterialList());
                            if (materialIds.isEmpty()) {
                                for (int i = 0; i < machineType.getComponents().size(); i++) {
                                    materialIds.add("");
                                }
                            }
                            materialIds.set(selected, material.getId());
                            ItemStack newBlueprint = new ItemStack(ModItems.BLUEPRINT.get(), blueprint.getCount());
                            newBlueprint.set(ModDataComponents.BLUEPRINT_DATA.get(), new BlueprintData(machineType.getId(), materialIds));
                            ((ItemStackHandler)this.inventory).setStackInSlot(0, newBlueprint);
                        }
                    }
                }
            }
            return true;
        } else if (buttonId >= 0) {
            if (blueprint.isEmpty()) return false;
            BlueprintData data = blueprint.get(ModDataComponents.BLUEPRINT_DATA.get());
            MachineType type = data.getMachineType();
            if (this.selectedComponent.get() >= type.getComponents().size()) return false;
            if (this.selectedComponent.get() == buttonId) this.selectedComponent.set(-1);
            else this.selectedComponent.set(buttonId);
            return true;
        }
        return false;
    }
    public int getSelectedComponent() {
        return this.selectedComponent.get();
    }
}
