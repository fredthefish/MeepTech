package com.minecraftmod.meeptech.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.minecraftmod.meeptech.ModMaterials;
import com.minecraftmod.meeptech.ModModuleTypes;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.module.ModuleType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

public record MachineConfigData(String moduleSlotType, String moduleId, String materialId, List<MachineConfigData> subLayers) {
    public static final MachineConfigData EMPTY = new MachineConfigData("", "", "", new ArrayList<>());

    public static final Codec<MachineConfigData> CODEC = Codec.recursive("MachineConfigLayerData", self -> 
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("moduleSlotType", "").forGetter(MachineConfigData::moduleSlotType),
            Codec.STRING.optionalFieldOf("itemId", "").forGetter(MachineConfigData::moduleId),
            Codec.STRING.optionalFieldOf("materialId", "").forGetter(MachineConfigData::materialId),
            self.listOf().fieldOf("subLayers").forGetter(MachineConfigData::subLayers)
        ).apply(instance, MachineConfigData::new)));

    public static final StreamCodec<RegistryFriendlyByteBuf, MachineConfigData> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    public ModuleType getModuleType() {
        return ModModuleTypes.getModuleType(moduleId);
    }
    public String getMaterial() {
        return materialId;
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
            return new MachineConfigData(original.moduleSlotType, original.moduleId, original.materialId, newSubLayers);
        } else {
            MachineConfigData oldSubLayer = original.getSubLayer(index);
            MachineConfigData newSubLayer = changeSubLayer(oldSubLayer, path.subList(1, path.size()), subLayer);
            newSubLayers.set(index, newSubLayer);
            return new MachineConfigData(original.moduleSlotType, original.moduleId, original.materialId, newSubLayers);
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
    public Item getItem() {
        if (!moduleId.isEmpty()) return ModModuleTypes.getModuleType(moduleId).getItem();
        if (!materialId.isEmpty() && !moduleSlotType.isEmpty()) {
            MaterialForm form = ModModuleTypes.getModuleSlotType(moduleSlotType).getMaterialForm();
            return ModMaterials.getMaterial(materialId).getForm(form);
        }
        return null;
    }
    public HashMap<ArrayList<String>, String> toHashMap() {
        return toHashMap(this, new ArrayList<>());
    }
    private static HashMap<ArrayList<String>, String> toHashMap(MachineConfigData layer, ArrayList<String> path) {
        HashMap<ArrayList<String>, String> hashMap = new HashMap<>();
        if (layer != null) {
            ModuleType type = layer.getModuleType();
            if (!layer.materialId.isEmpty()) {
                hashMap.put(new ArrayList<>(path), layer.moduleSlotType);
                path.add(layer.moduleSlotType);
                hashMap.put(new ArrayList<>(path), layer.materialId);
            } else if (!layer.moduleId.isEmpty()) {
                hashMap.put(new ArrayList<>(path), type.getId());
                path.add(type.getId());
                for (int i = 0; i < type.getSubSlotCount(); i++) {
                    HashMap<ArrayList<String>, String> subLayerHashMap = toHashMap(layer.getSubLayer(i), path);
                    hashMap.putAll(subLayerHashMap);
                }
            }
        }
        return hashMap;
    }
    public MachineData toMachineData() {
        if (allSlotsFilled()) {
            return new MachineData(toHashMap());
        }
        return null;
    }
    public boolean allSlotsFilled() {
        return allSlotsFilled(this);
    }
    private static boolean allSlotsFilled(MachineConfigData layer) {
        if (layer == null) return false;
        if (layer.isEmpty()) return false;
        ModuleType type = layer.getModuleType();
        if (type != null) {
            boolean allSlotsFilled = true;
            for (int i = 0; i < type.getSubSlotCount(); i++) {
                allSlotsFilled &= allSlotsFilled(layer.getSubLayer(i));
            }
            return allSlotsFilled;
        } else if (layer.materialId != null && layer.moduleSlotType != null) {
            return true;
        } else {
            return false;
        }
    }
}
