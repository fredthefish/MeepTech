package com.minecraftmod.meeptech.ui;

import java.util.ArrayList;

import com.minecraftmod.meeptech.ModBlocks;
import com.minecraftmod.meeptech.ModMenus;
import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.ui.SlotType;
import com.minecraftmod.meeptech.logic.ui.SlotUIElement;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class MachineMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final BaseMachineBlockEntity blockEntity;

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
        this.access = access;

        if (blockEntity != null) {
            if (this.blockEntity.getMachineData() != null) {
                MachineData machineData = this.blockEntity.getMachineData();
                ArrayList<SlotUIElement> uiSlots = machineData.getSlots();
                int i = 0;
                for (SlotUIElement slot : uiSlots) {
                    SlotItemHandler newSlot = new SlotItemHandler(blockEntity.getInventory(), i, slot.getX(), slot.getY()) {
                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return slot.getType() == SlotType.INPUT;
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
        return stillValid(this.access, player, ModBlocks.BRICK_HULL.get());
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        //TODO
        return ItemStack.EMPTY;
    }
    public MachineData getMachineData() {
        return blockEntity.getMachineData();
    }
}
