package com.minecraftmod.meeptech.ui;

import com.minecraftmod.meeptech.blocks.FluidTankBlockEntity;
import com.minecraftmod.meeptech.registries.ModBlocks;
import com.minecraftmod.meeptech.registries.ModMenus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidTankMenu extends AbstractContainerMenu {
    private final FluidTankBlockEntity blockEntity;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    public FluidTankMenu(int id, Inventory inv, FluidTankBlockEntity blockEntity, ContainerLevelAccess access) {
        super(ModMenus.FLUID_TANK_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.access = access;
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                //0 and 1 are shorts for the fluid. 2 and 3 are shorts for the capacity.
                return switch (index) {
                    case 0 -> blockEntity.getFluidAmountLow();
                    case 1 -> blockEntity.getFluidAmountHigh();
                    case 2 -> blockEntity.getCapacity() & 0xFFFF;
                    case 3 -> (blockEntity.getCapacity() >> 16) & 0xFFFF;
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {}
            @Override
            public int getCount() { return 4; }
        };
        addDataSlots(data);
        addPlayerInventoryAndHotbar(inv);
    }
    public FluidTankMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, (FluidTankBlockEntity) inv.player.level().getBlockEntity(buf.readBlockPos()), ContainerLevelAccess.NULL);
    }
    public int getFluidAmount() {
        return FluidTankBlockEntity.combineFluidAmount(
            data.get(1), data.get(0));
    }
    public int getCapacity() {
        return FluidTankBlockEntity.combineFluidAmount(
            data.get(3), data.get(2));
    }
    public FluidStack getFluid() {
        return blockEntity.getTank().getFluid();
    }
    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.FLUID_TANK.get());
    }
    private void addPlayerInventoryAndHotbar(Inventory inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9,
                    8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}