package com.minecraftmod.meeptech.ui;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.blocks.IFluidTankBlockEntity;
import com.minecraftmod.meeptech.items.FluidCellItem;
import com.minecraftmod.meeptech.network.FluidCellActionPacket.FluidCellAction;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public interface IFluidMenu {
    public <FluidBlockEntity extends BlockEntity & IFluidTankBlockEntity> FluidBlockEntity getBlockEntity();
    public Player getPlayer();

    public default <MenuType extends AbstractContainerMenu & IFluidMenu> void handleCellClick(MenuType menu, int tankIndex) {
        ItemStack carried = menu.getCarried();
        if (carried.isEmpty()) return;
        if (!FluidUtil.getFluidHandler(carried).isPresent()) return;
        IItemHandler playerInventory = new InvWrapper(menu.getPlayer().getInventory());
        FluidActionResult fillResult = FluidUtil.tryEmptyContainerAndStow(carried, menu.getBlockEntity().getTank(tankIndex), 
            playerInventory, menu.getBlockEntity().getTank(tankIndex).getCapacity(), menu.getPlayer(), true);
        if (fillResult.isSuccess()) {
            menu.setCarried(fillResult.getResult());
            return;
        }
        FluidActionResult drainResult = FluidUtil.tryFillContainerAndStow(carried, menu.getBlockEntity().getTank(tankIndex), 
            playerInventory, menu.getBlockEntity().getTank(tankIndex).getCapacity(), menu.getPlayer(), true);
        if (drainResult.isSuccess()) {
            menu.setCarried(drainResult.getResult());
        }
    }
    public default <MenuType extends AbstractContainerMenu & IFluidMenu> void handleFluidCellClick(FluidCellAction action, boolean shift, MenuType menu, int tankIndex) {
        ItemStack carried = menu.getCarried();
        if (!(carried.getItem() instanceof FluidCellItem cellItem)) return;
        FluidTank tank = menu.getBlockEntity().getTank(tankIndex);
        if (action == FluidCellAction.EXTRACT_INTO_CELL) {
            if (cellItem.isFull(carried)) {
                handleFluidCellClick(FluidCellAction.INSERT_INTO_TANK, shift, menu, tankIndex);
                return;
            }
            if (tank.isEmpty()) return;
            if (!shift) {
                ItemStack singleCell = carried.copyWithCount(1);
                ItemStack result = fillOneCell(singleCell, tank, cellItem);
                if (result != null) {
                    carried.shrink(1);
                    if (carried.isEmpty()) {
                        menu.setCarried(result);
                    } else {
                        if (!menu.getPlayer().getInventory().add(result)) menu.getPlayer().drop(result, false);
                        menu.setCarried(carried);
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
                menu.setCarried(filledCells.get(0));
                for (int i = 1; i < filledCells.size(); i++) {
                    if (!menu.getPlayer().getInventory().add(filledCells.get(i))) menu.getPlayer().drop(filledCells.get(i), false);
                }
                for (ItemStack remaining : remainingCells) {
                    if (!menu.getPlayer().getInventory().add(remaining)) menu.getPlayer().drop(remaining, false);
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
                    if (carried.isEmpty()) menu.setCarried(result);
                    else {
                        if (!menu.getPlayer().getInventory().add(result)) menu.getPlayer().drop(result, false);
                        menu.setCarried(carried);
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
                menu.setCarried(resultCells.get(0));
                for (int i = 1; i < resultCells.size(); i++) {
                    if (!menu.getPlayer().getInventory().add(resultCells.get(i))) {
                        menu.getPlayer().drop(resultCells.get(i), false);
                    }
                }
            }
        }
        menu.getBlockEntity().setChanged();
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
}
