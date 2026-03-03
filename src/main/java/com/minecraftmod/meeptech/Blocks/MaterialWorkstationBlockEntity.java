package com.minecraftmod.meeptech.Blocks;

import com.minecraftmod.meeptech.ModBlockEntities;

import net.minecraft.core.BlockPos;
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
}
