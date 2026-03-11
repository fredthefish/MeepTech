package com.minecraftmod.meeptech.blocks;

import com.minecraftmod.meeptech.ModBlockEntities;
import com.minecraftmod.meeptech.items.MachineConfigData;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.ui.MachineMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BaseMachineBlockEntity extends BlockEntity implements MenuProvider {
    private MachineConfigData machineConfigData;
    private MachineData machineData = null;
    private final ItemStackHandler inventory = new ItemStackHandler() {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    //TODO: OTHER STUFF (RECIPES/PROGRESS/MAX PROGRESS/HEAT/ETC)
    
    public BaseMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASE_MACHINE_BE.get(), pos, state);
    }
    @Override
    public Component getDisplayName() {
        return Component.translatable(machineData.getBase().getTranslationKey()).append(" ")
            .append(Component.translatable(machineData.getType().getTranslationKey()));
    }
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        return new MachineMenu(windowId, playerInv, this, ContainerLevelAccess.create(this.level, this.worldPosition));
    }
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", this.inventory.serializeNBT(registries));
        if (this.machineConfigData != null) {
            MachineConfigData.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), machineConfigData)
                .ifSuccess(encoded -> tag.put("MachineConfig", encoded));
        }
    }
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        if (tag.contains("MachineConfig")) {
            MachineConfigData.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("MachineConfig"))
                .ifSuccess(parsed -> {
                    this.setConfigData(parsed);
                });
        }
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag, registries);
        return compoundTag;
    }
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    public MachineConfigData getConfigData() {
        return machineConfigData;
    }
    public void setConfigData(MachineConfigData machineConfigData) {
        if (!machineConfigData.isEmpty()) {
            this.machineConfigData = machineConfigData;
            setChanged();
            setMachineData(machineConfigData);
        }
    }
    public void setMachineData(MachineConfigData machineConfigData) {
        if (!machineConfigData.isEmpty()) {
            this.machineData = machineConfigData.toMachineData();
            if (machineData != null) {
                int slotCount = machineData.getSlotCount();
                if (slotCount != inventory.getSlots()) {
                    inventory.setSize(slotCount);
                }
                setChanged();
            }
        }
    }
    public MachineData getMachineData() {
        if (machineData == null) {
            if (getConfigData() != null) {
                setMachineData(machineConfigData);
            }
        }
        return machineData;
    }
    public ItemStackHandler getInventory() {
        return inventory;
    }
}
