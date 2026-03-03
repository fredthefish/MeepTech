package com.minecraftmod.meeptech.Blocks;

import com.minecraftmod.meeptech.ModBlocks;
import com.minecraftmod.meeptech.ModItems;
import com.minecraftmod.meeptech.ModMenus;

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

public class MaterialWorkstationMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final DataSlot selectedForm = DataSlot.standalone();

    public MaterialWorkstationMenu(int windowId, Inventory playerInv) {
        this(windowId, playerInv, new ItemStackHandler(3), ContainerLevelAccess.NULL);
    }
    public MaterialWorkstationMenu(int windowId, Inventory playerInv, IItemHandler handler, ContainerLevelAccess access) {
        super(ModMenus.MATERIAL_WORKSTATION_MENU.get(), windowId);
        this.access = access;

        this.addSlot(new SlotItemHandler(handler, 0, 16, 21));
        this.addSlot(new SlotItemHandler(handler, 1, 16, 51) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.HAMMER.get());
            }
        });
        this.addSlot(new SlotItemHandler(handler, 2, 145, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addDataSlot(selectedForm);

        //Add inventory/hotbar.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        this.selectedForm.set(buttonId);

        //TODO: Update crafting logic.

        return true;
    }
    public int getSelectedForm() {
        return this.selectedForm.get();
    }
    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.MATERIAL_WORKSTATION.get());
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        //TODO: Implement.
        return ItemStack.EMPTY;
    }
}
