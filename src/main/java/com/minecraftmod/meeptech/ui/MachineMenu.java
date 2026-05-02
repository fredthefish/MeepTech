package com.minecraftmod.meeptech.ui;

import java.util.List;

import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.blocks.IFluidTankBlockEntity;
import com.minecraftmod.meeptech.logic.machine.EnergySource;
import com.minecraftmod.meeptech.logic.machine.EnergySourceType;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.ui.SlotType;
import com.minecraftmod.meeptech.logic.ui.SlotUIElement;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;
import com.minecraftmod.meeptech.logic.ui.UIModuleType;
import com.minecraftmod.meeptech.network.FluidTankSyncPayload;
import com.minecraftmod.meeptech.network.MachineContainerData;
import com.minecraftmod.meeptech.registries.ModMenus;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

public class MachineMenu extends AbstractContainerMenu implements IFluidMenu {
    private final ContainerLevelAccess access;
    private final BaseMachineBlockEntity blockEntity;
    private final MachineContainerData machineContainerData;
    private final FluidStack[] trackedFluids;
    private final Player player;

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
        this.machineContainerData = blockEntity.getTrackedData();
        this.access = access;
        this.player = playerInv.player;

        this.addDataSlots(machineContainerData);
        if (this.blockEntity.getMachineData() != null) {
            MachineData machineData = this.blockEntity.getMachineData();
            this.trackedFluids = new FluidStack[machineData.getTankSlotCount()];
            List<SlotUIElement> itemSlots = machineData.getItemSlots();
            int i = 0;
            for (SlotUIElement slot : itemSlots) {
                SlotItemHandler newSlot = new SlotItemHandler(blockEntity.getInventory(), i, slot.getX() + 1, slot.getY() + 1) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        if (slot.getType() == SlotType.INPUT) {
                            return true;
                        } else if (slot.getType() == SlotType.INPUT_FUEL) {
                            EnergySource energySource = machineData.getEnergySource();
                            if (energySource.getEnergySourceType() == EnergySourceType.Heat)
                                return stack.getBurnTime(RecipeType.SMELTING) > 0 
                                    || energySource.getEnergyType().getRecipe(List.of(stack), List.of(), List.of(), List.of()) != null;
                            return false;
                        }
                        return false;
                    }
                };
                this.addSlot(newSlot);
                i++;
            }
            List<SlotUIElement> fluidSlots = machineData.getFluidSlots();
            for (i = 0; i < fluidSlots.size(); i++) {
                trackedFluids[i] = FluidStack.EMPTY;
            }
        } else this.trackedFluids = new FluidStack[0];

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
        return stillValid(this.access, player, this.blockEntity.getBlock());
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            MachineData machineData = this.blockEntity.getMachineData();
            int slotCount = machineData.getItemSlotCount();
            if (index < slotCount) {
                if (!this.moveItemStackTo(stackInSlot, slotCount, slotCount + 36, true)) return ItemStack.EMPTY;
            } else {
                int start = machineData.getStartItemSlot(UIModuleType.Energy);
                int end = machineData.getStartItemSlot(UIModuleType.Recipe);
                if (!this.moveItemStackTo(stackInSlot, start, end, false)) {
                    start = machineData.getStartItemSlot(UIModuleType.Input);
                    end = machineData.getStartItemSlot(UIModuleType.Output);
                    if (!this.moveItemStackTo(stackInSlot, start, end, false)) return ItemStack.EMPTY;
                }
            }
            if (stackInSlot.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
            if (stackInSlot.getCount() == itemStack.getCount()) return ItemStack.EMPTY;
            slot.onTake(player, stackInSlot);
        }
        return itemStack;
    }
    public MachineData getMachineData() {
        return blockEntity.getMachineData();
    }
    public int getProgress() {
        return machineContainerData.getFromStat(TrackedStat.RecipeProgress);
    }
    public int getMaxProgress() {
        return machineContainerData.getFromStat(TrackedStat.RecipeMaxProgress);
    }
    public int getHeat() {
        return machineContainerData.getFromStat(TrackedStat.HeatLeft);
    }
    public FluidStack getFluidInTank(int index) {
        return trackedFluids[index];
    }
    public FluidTank getTank(int index) {
        return blockEntity.getTank(index);
    }
    public void setFluid(int index, FluidStack fluid) {
        blockEntity.getTank(index).setFluid(fluid);
    }
    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        for (int i = 0; i < trackedFluids.length; i++) {
            FluidStack current = blockEntity.getTank(i).getFluid();
            if (!FluidStack.matches(current, trackedFluids[i])) {
                trackedFluids[i] = current.copy();
                if (blockEntity.getLevel() instanceof ServerLevel serverLevel) {
                    for (ServerPlayer sp : serverLevel.players()) {
                        if (sp.containerMenu == this) {
                            PacketDistributor.sendToPlayer(sp, new FluidTankSyncPayload(blockEntity.getBlockPos(), i, current.copy()));
                        }
                    }
                }
            }
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    public <FluidBlockEntity extends BlockEntity & IFluidTankBlockEntity> FluidBlockEntity getBlockEntity() {
        return (FluidBlockEntity)blockEntity;
    }
    @Override
    public Player getPlayer() {
        return player;
    }
}
