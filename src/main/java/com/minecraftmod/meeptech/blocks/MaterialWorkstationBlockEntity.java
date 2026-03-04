package com.minecraftmod.meeptech.blocks;

import com.minecraftmod.meeptech.ModBlockEntities;
import com.minecraftmod.meeptech.ui.MaterialWorkstationMenu;

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

public class MaterialWorkstationBlockEntity extends BlockEntity implements MenuProvider {
    public MaterialWorkstationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MATERIAL_WORKSTATION_BE.get(), pos, state);
    }

    //Slot 0 is the input, Slot 1 is the hammer, Slot 2 is the output.
    public final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.meeptech.material_workstation");
    }
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        return new MaterialWorkstationMenu(windowId, playerInv, this.inventory, ContainerLevelAccess.create(this.level, this.worldPosition));
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
