package com.minecraftmod.meeptech.blocks;

import com.minecraftmod.meeptech.ModBlockEntities;
import com.minecraftmod.meeptech.ui.DesigningStationMenu;

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

public class DesigningStationBlockEntity extends BlockEntity implements MenuProvider {
    public DesigningStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DESIGNING_STATION_BE.get(), pos, state);
    }
    //Slot 0 is for the paper, Slot 1 is for the blueprint, remaining slots are for the component materials.
    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.meeptech.designing_station");
    }
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        return new DesigningStationMenu(windowId, playerInv, this.inventory, ContainerLevelAccess.create(this.level, this.worldPosition));
    }
}
