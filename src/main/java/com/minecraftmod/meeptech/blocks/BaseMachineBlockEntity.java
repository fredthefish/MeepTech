package com.minecraftmod.meeptech.blocks;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.logic.MachineType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class BaseMachineBlockEntity extends BlockEntity implements MenuProvider {
    protected final List<String> componentMaterials = new ArrayList<>();
    public final ItemStackHandler inventory = new ItemStackHandler(getInventorySize()) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    
    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract MachineType getMachineType();
    public int getInventorySize() {
        return getMachineType().getInventorySlots();
    };
    public int getComponentCount() {
        return getMachineType().getComponents().size();
    };
    public String getMachineTypeId() {
        return getMachineType().getId();
    };
    public List<String> getComponentMaterials() {
        return componentMaterials;
    }
    public void setComponentMaterials(List<String> materials) {
        this.componentMaterials.clear();
        this.componentMaterials.addAll(materials);
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), getComponentCount());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", this.inventory.serializeNBT(registries));
        ListTag matList = new ListTag();
        for (String material : this.componentMaterials) {
            matList.add(StringTag.valueOf(material));
        }
        tag.put("ComponentMaterials", matList);
    }
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Invenory")) this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("ComponentMaterials")) {
            this.componentMaterials.clear();
            ListTag matList = tag.getList("ComponentMaterials", Tag.TAG_STRING);
            for (int i = 0; i < matList.size(); i++) {
                this.componentMaterials.add(matList.getString(i));
            }
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
    public Component getDisplayName() {
        return Component.translatable(getMachineType().getTranslationKey());
    }
}
