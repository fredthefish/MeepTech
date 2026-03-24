package com.minecraftmod.meeptech.blocks;

import com.minecraftmod.meeptech.registries.ModBlockEntities;
import com.minecraftmod.meeptech.ui.FluidTankMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class FluidTankBlockEntity extends BlockEntity implements MenuProvider, IFluidTankBlockEntity {
    private final FluidTank tank;
    private final int capacity;
    public FluidTankBlockEntity(BlockPos pos, BlockState state, int capacity) {
        super(ModBlockEntities.FLUID_TANK_BE.get(), pos, state);
        this.capacity = capacity;
        this.tank = new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(),
                        getBlockState(), Block.UPDATE_CLIENTS);
                }
            }
        };
    }
    @Override
    public FluidTank getTank(int index) {
        return tank; 
    }
    public int getCapacity() {
        return capacity; 
    }
    public InteractionResult useBucket(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        IItemHandler playerInventory = new InvWrapper(player.getInventory());
        FluidActionResult fillResult = FluidUtil.tryEmptyContainerAndStow(heldItem, tank, playerInventory, tank.getCapacity(), player, true);
        if (fillResult.isSuccess()) {
            player.setItemInHand(hand, fillResult.getResult());
            return InteractionResult.SUCCESS;
        }
        FluidActionResult drainResult = FluidUtil.tryFillContainerAndStow(heldItem, tank, playerInventory, tank.getCapacity(), player, true);
        if (drainResult.isSuccess()) {
            player.setItemInHand(hand, drainResult.getResult());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("tank", tank.writeToNBT(registries, new CompoundTag()));
    }
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("tank")) {
            tank.readFromNBT(registries, tag.getCompound("tank"));
        }
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        loadAdditional(pkt.getTag(), registries);
    }
    public int getFluidAmountLow() {
        return tank.getFluidAmount() & 0xFFFF;
    }
    public int getFluidAmountHigh() {
        return (tank.getFluidAmount() >> 16) & 0xFFFF;
    }
    public static int combineFluidAmount(int high, int low) {
        return (high << 16) | (low & 0xFFFF);
    }
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new FluidTankMenu(id, inv, this, ContainerLevelAccess.create(this.level, this.worldPosition));
    }
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.meeptech.fluid_tank");
    }
}
