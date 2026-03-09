package com.minecraftmod.meeptech.ui;

import java.util.List;

import com.minecraftmod.meeptech.ModBlocks;
import com.minecraftmod.meeptech.ModDataComponents;
import com.minecraftmod.meeptech.ModMenus;
import com.minecraftmod.meeptech.ModModuleTypes;
import com.minecraftmod.meeptech.ModTags;
import com.minecraftmod.meeptech.items.MachineConfigData;
import com.minecraftmod.meeptech.logic.MaterialItemData;
import com.minecraftmod.meeptech.logic.ModuleSlot;
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
    public void handleVirtualAction(EngineeringActionPacket.EngineeringAction action, List<Integer> path) {
        switch (action) {
            case EngineeringActionPacket.EngineeringAction.INSERT:
                Slot hullSlot = this.getSlot(0);
                Slot inputSlot = this.getSlot(1);
                ItemStack hull = hullSlot.getItem();
                ItemStack input = inputSlot.getItem();
                ModuleType type = ModModuleTypes.BASE_MODULE;
                MachineConfigData data;
                if (hull.has(ModDataComponents.MACHINE_CONFIG_DATA.get())) {
                    data = hull.get(ModDataComponents.MACHINE_CONFIG_DATA.get());
                } else {
                    data = type.getEmptyMachineConfigData();
                }
                int slotPath = path.get(0);
                ModuleSlot slot = data.getModuleType().getSubSlot(slotPath);
                ModuleSlotType slotType = slot.getType();
                if (!input.isEmpty()) {
                    if (ModuleType.itemFitsSlotType(input, slotType)) {
                        inputSlot.remove(1);
                        ModuleType inputModuleType = ModuleType.getModuleType(input);
                        if (inputModuleType != null) {
                            data.setSubLayer(slotPath, inputModuleType.getEmptyMachineConfigData());
                        } else {
                            //If it isn't a module, it must be a component. (Slot type check was already done.)
                            MaterialItemData itemData = new MaterialItemData(input.getItem());
                            data.setSubLayer(slotPath, ModuleType.getMaterialMachineConfigData(itemData.getMaterial().getId()));
                        }
                        hull.set(ModDataComponents.MACHINE_CONFIG_DATA.get(), data);
                        hullSlot.set(hull);
                    }
                }
                break;
            case EngineeringActionPacket.EngineeringAction.EXTRACT:
                break;
            case EngineeringActionPacket.EngineeringAction.SELECT:
                break;
        }
    }
}
