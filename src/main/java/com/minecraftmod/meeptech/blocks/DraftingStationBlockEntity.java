package com.minecraftmod.meeptech.blocks;

import com.minecraftmod.meeptech.ModBlockEntities;
import com.minecraftmod.meeptech.ui.DraftingStationMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class DraftingStationBlockEntity extends BlockEntity implements MenuProvider {
    public DraftingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRAFTING_STATION_BE.get(), pos, state);
    }
    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.meeptech.drafting_station");
    }
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        return new DraftingStationMenu(windowId, playerInv, this.inventory, ContainerLevelAccess.create(this.level, this.worldPosition));
    }
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", this.inventory.serializeNBT(registries));
    }
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
    }
}
