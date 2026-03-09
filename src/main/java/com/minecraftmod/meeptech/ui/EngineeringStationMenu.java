package com.minecraftmod.meeptech.ui;

import com.minecraftmod.meeptech.ModBlocks;
import com.minecraftmod.meeptech.ModMenus;
import com.minecraftmod.meeptech.ModTags;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
// import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class EngineeringStationMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    // private final IItemHandler inventory;

    public EngineeringStationMenu(int windowId, Inventory playerInv) {
        this(windowId, playerInv, new ItemStackHandler(3), ContainerLevelAccess.NULL);
    }
    public EngineeringStationMenu(int windowId, Inventory playerInv, ItemStackHandler handler, ContainerLevelAccess access) {
        super(ModMenus.ENGINEERING_STATION_MENU.get(), windowId);
        this.access = access;
        // this.inventory = handler;

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
        //TODO: IMPLEMENT.
        return ItemStack.EMPTY;
    }
}
