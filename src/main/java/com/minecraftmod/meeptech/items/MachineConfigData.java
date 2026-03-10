package com.minecraftmod.meeptech.items;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.ModMaterials;
import com.minecraftmod.meeptech.ModModuleTypes;
import com.minecraftmod.meeptech.logic.ModuleType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record MachineConfigData(String moduleId, String materialId, List<MachineConfigData> subLayers) {
    public static final MachineConfigData EMPTY = new MachineConfigData("", "", new ArrayList<>());

    public static final Codec<MachineConfigData> CODEC = Codec.recursive("MachineConfigLayerData", self -> 
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("itemId", "").forGetter(MachineConfigData::moduleId),
            Codec.STRING.optionalFieldOf("materialId", "").forGetter(MachineConfigData::materialId),
            self.listOf().fieldOf("subLayers").forGetter(MachineConfigData::subLayers)
        ).apply(instance, MachineConfigData::new)));

    public static final StreamCodec<RegistryFriendlyByteBuf, MachineConfigData> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    public ModuleType getModuleType() {
        return ModModuleTypes.getModuleType(moduleId);
    }
    public MachineConfigData getSubLayer(int index) {
        if (subLayers.size() > index) {
            return subLayers.get(index);
        }
        return null;
    }
    public static MachineConfigData changeSubLayer(MachineConfigData original, List<Integer> path, MachineConfigData subLayer) {
        ArrayList<MachineConfigData> newSubLayers = new ArrayList<>(original.subLayers);
        int index = path.get(0);
        if (path.size() == 1) {
            newSubLayers.set(index, subLayer);
            return new MachineConfigData(original.moduleId, original.materialId, newSubLayers);
        } else {
            MachineConfigData oldSubLayer = original.getSubLayer(index);
            MachineConfigData newSubLayer = changeSubLayer(oldSubLayer, path.subList(1, path.size()), subLayer);
            newSubLayers.set(index, newSubLayer);
            return new MachineConfigData(original.moduleId, original.materialId, newSubLayers);
        }
    }
    public void setSubLayerCount(int count) {
        subLayers.clear();
        for (int i = 0; i < count; i++) {
            subLayers.add(null);
        }
    }
    public boolean isEmpty() {
        return this.moduleId.isEmpty() && this.materialId.isEmpty() && this.subLayers.isEmpty();
    }
    public boolean hasSubLayers() {
        for (MachineConfigData subLayer : subLayers) {
            if (!subLayer.isEmpty()) return true;
        }
        return false;
    }
    public ItemStack getItemStack(ModuleType type) {
        if (!moduleId.isEmpty()) return new ItemStack(ModModuleTypes.getModuleType(moduleId).getItem());
        if (!materialId.isEmpty()) return new ItemStack(ModMaterials.getMaterial(materialId).getForm(type.getMaterialForm()));
        return ItemStack.EMPTY;
    }
    public MachineConfigData copy() {
        return new MachineConfigData(moduleId, materialId, new ArrayList<MachineConfigData>(subLayers));
    }
    //TODO: IMPLEMENT SYSTEM TO READ ENTIRE DATA AND SEE IF THE MACHINE HAS ALL REQUIRED SLOTS FILLED.
}
