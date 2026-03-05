package com.minecraftmod.meeptech.ui;

import com.minecraftmod.meeptech.ModBlocks;
import com.minecraftmod.meeptech.ModDataComponents;
import com.minecraftmod.meeptech.ModItems;
import com.minecraftmod.meeptech.ModMenus;
import com.minecraftmod.meeptech.logic.BlueprintData;
import com.minecraftmod.meeptech.logic.ItemData;

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

        //Blueprint slot.
        this.addSlot(new SlotItemHandler(handler, 0, 16, 29) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (stack.is(ModItems.BLUEPRINT.get())) {
                    BlueprintData blueprintData = stack.get(ModDataComponents.BLUEPRINT_DATA.get());
                    return blueprintData.getMachineType() != null;
                }
                return false;
            }
            @Override
            public void setChanged() {
                super.setChanged();
                //TODO: UPDATE UI.
            }
        });

        //Material slot.
        this.addSlot(new SlotItemHandler(handler, 1, 16, 56));

        //Add inventory/hotbar.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
        }

        this.addDataSlot(selectedComponent);
        //TODO: UPDATE UI.
    }
    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.DRAFTING_STATION.get());
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        //TODO: IMPLEMENT
        return ItemStack.EMPTY;
    }
    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        ItemStack blueprintStack = this.inventory.getStackInSlot(0);
        if (buttonId == 1000) {
            if (blueprintStack.isEmpty()) return false;
            BlueprintData data = blueprintStack.get(ModDataComponents.BLUEPRINT_DATA.get());
            int selected = this.selectedComponent.get();
            ItemStack materialStack = this.inventory.getStackInSlot(1);
            if (data != null && selected >= 0) {
                if (materialStack.isEmpty()) {
                    //TODO: Set component to be empty.
                } else {
                    ItemData itemData = new ItemData(materialStack.getItem());
                    itemData.getMaterial();
                    //TODO: Set component to be material.
                }
            }
            return true;
        } else if (buttonId >= 0) {
            this.selectedComponent.set(buttonId);
            return true;
        }
        return false;
    }
    public int getSelectedComponent() {
        return this.selectedComponent.get();
    }
}
