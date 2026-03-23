package com.minecraftmod.meeptech.ui;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.blocks.FluidTankBlockEntity;
import com.minecraftmod.meeptech.items.FluidCellItem;
import com.minecraftmod.meeptech.network.FluidCellActionPacket.FluidCellAction;
import com.minecraftmod.meeptech.registries.ModBlocks;
import com.minecraftmod.meeptech.registries.ModMenus;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class FluidTankMenu extends AbstractContainerMenu {
    private final FluidTankBlockEntity blockEntity;
    private final Player player;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    public FluidTankMenu(int id, Inventory inv, FluidTankBlockEntity blockEntity, ContainerLevelAccess access) {
        super(ModMenus.FLUID_TANK_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.access = access;
        this.player = inv.player;
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
    public void handleCellClick() {
        ItemStack carried = getCarried();
        if (carried.isEmpty()) return;
        if (!FluidUtil.getFluidHandler(carried).isPresent()) return;
        IItemHandler playerInventory = new InvWrapper(player.getInventory());
        FluidActionResult fillResult = FluidUtil.tryEmptyContainerAndStow(carried, blockEntity.getTank(), 
            playerInventory, blockEntity.getTank().getCapacity(), player, true);
        if (fillResult.isSuccess()) {
            setCarried(fillResult.getResult());
            return;
        }
        FluidActionResult drainResult = FluidUtil.tryFillContainerAndStow(carried, blockEntity.getTank(), 
            playerInventory, blockEntity.getTank().getCapacity(), player, true);
        if (drainResult.isSuccess()) {
            setCarried(drainResult.getResult());
        }
    }
    public void handleFluidCellClick(FluidCellAction action, boolean shift) {
        ItemStack carried = getCarried();
        if (!(carried.getItem() instanceof FluidCellItem cellItem)) return;
        FluidTank tank = blockEntity.getTank();
        if (action == FluidCellAction.EXTRACT_INTO_CELL) {
            if (cellItem.isFull(carried)) {
                handleFluidCellClick(FluidCellAction.INSERT_INTO_TANK, shift);
                return;
            }
            if (tank.isEmpty()) return;
            if (!shift) {
                ItemStack singleCell = carried.copyWithCount(1);
                ItemStack result = fillOneCell(singleCell, tank, cellItem);
                if (result != null) {
                    carried.shrink(1);
                    if (carried.isEmpty()) {
                        setCarried(result);
                    } else {
                        if (!player.getInventory().add(result)) player.drop(result, false);
                        setCarried(carried);
                    }
                }
            } else {
                int cellCount = carried.getCount();
                List<ItemStack> filledCells = new ArrayList<>();
                List<ItemStack> remainingCells = new ArrayList<>();
                for (int i = 0; i < cellCount; i++) {
                    ItemStack singleCell = carried.copyWithCount(1);
                    ItemStack result = fillOneCell(singleCell, tank, cellItem);
                    if (result != null) {
                        boolean merged = false;
                        for (ItemStack filled : filledCells) {
                            if (ItemStack.isSameItemSameComponents(filled, result) && filled.getCount() < filled.getMaxStackSize()) {
                                filled.grow(1);
                                merged = true;
                                break;
                            }
                        }
                        if (!merged) filledCells.add(result);
                    } else {
                        remainingCells.add(singleCell);
                    }
                }
                if (filledCells.isEmpty()) return;
                setCarried(filledCells.get(0));
                for (int i = 1; i < filledCells.size(); i++) {
                    if (!player.getInventory().add(filledCells.get(i))) player.drop(filledCells.get(i), false);
                }
                for (ItemStack remaining : remainingCells) {
                    if (!player.getInventory().add(remaining)) player.drop(remaining, false);
                }
            }
        } else {
            FluidStack cellFluid = cellItem.getFluid(carried);
            if (cellFluid.isEmpty()) return;
            if (!shift) {
                ItemStack singleCell = carried.copyWithCount(1);
                ItemStack result = drainOneCell(singleCell, tank, cellItem);
                if (result != null) {
                    carried.shrink(1);
                    if (carried.isEmpty()) setCarried(result);
                    else {
                        if (!player.getInventory().add(result)) player.drop(result, false);
                        setCarried(carried);
                    }
                }
            } else {
                int cellCount = carried.getCount();
                List<ItemStack> resultCells = new ArrayList<>();
                for (int i = 0; i < cellCount; i++) {
                    ItemStack singleCell = carried.copyWithCount(1);
                    ItemStack result = drainOneCell(singleCell, tank, cellItem);
                    ItemStack toReturn = result != null ? result : singleCell;
                    boolean merged = false;
                    for (ItemStack existing : resultCells) {
                        if (ItemStack.isSameItemSameComponents(existing, toReturn) && existing.getCount() < existing.getMaxStackSize()) {
                            existing.grow(1);
                            merged = true;
                            break;
                        }
                    }
                    if (!merged) resultCells.add(toReturn.copyWithCount(1));
                }
                if (resultCells.isEmpty()) return;
                setCarried(resultCells.get(0));
                for (int i = 1; i < resultCells.size(); i++) {
                    if (!player.getInventory().add(resultCells.get(i))) {
                        player.drop(resultCells.get(i), false);
                    }
                }
            }
        }
        blockEntity.setChanged();
    }
    private ItemStack fillOneCell(ItemStack cell, FluidTank tank, FluidCellItem cellItem) {
        IFluidHandlerItem cellHandler = FluidUtil.getFluidHandler(cell).orElse(null);
        if (cellHandler == null) return null;
        FluidStack tankFluid = tank.getFluid();
        if (tankFluid.isEmpty()) return null;
        int cellSpace = cellItem.getCapacity() - cellItem.getFluidAmount(cell);
        if (cellSpace <= 0) return null;
        FluidStack cellFluid = cellItem.getFluid(cell);
        if (!cellFluid.isEmpty() && !FluidStack.isSameFluidSameComponents(cellFluid, tankFluid)) return null;
        int toTransfer = Math.min(cellSpace, tankFluid.getAmount());
        FluidStack drained = tank.drain(toTransfer, IFluidHandler.FluidAction.EXECUTE);
        if (drained.isEmpty()) return null;
        cellHandler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
        return cellHandler.getContainer();
    }
    private ItemStack drainOneCell(ItemStack cell, FluidTank tank, FluidCellItem cellItem) {
        IFluidHandlerItem cellHandler = FluidUtil.getFluidHandler(cell).orElse(null);
        if (cellHandler == null) return null;
        FluidStack cellFluid = cellItem.getFluid(cell);
        if (cellFluid.isEmpty()) return null;
        if (!tank.isEmpty() && !FluidStack.isSameFluidSameComponents(tank.getFluid(), cellFluid)) return null;
        int toTransfer = Math.min(cellFluid.getAmount(), tank.getCapacity() - tank.getFluidAmount());
        if (toTransfer <= 0) return null;
        FluidStack toFill = cellFluid.copyWithAmount(toTransfer);
        int filled = tank.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
        if (filled == 0) return null;
        cellHandler.drain(filled, IFluidHandler.FluidAction.EXECUTE);
        return cellHandler.getContainer();
    }
    public BlockPos getBlockEntityPos() {
        return blockEntity.getBlockPos();
    }
}