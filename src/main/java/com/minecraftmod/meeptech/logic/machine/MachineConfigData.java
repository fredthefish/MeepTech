package com.minecraftmod.meeptech.logic.machine;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.ModMaterials;
import com.minecraftmod.meeptech.logic.module.ModModuleTypes;
import com.minecraftmod.meeptech.logic.module.ModuleType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

public record MachineConfigData(String moduleSlotType, String itemId, String materialId, int upgradeSlots, List<MachineConfigData> subLayers) {
    public static final MachineConfigData EMPTY = new MachineConfigData("", "", "", 0, new ArrayList<>());

    public static final Codec<MachineConfigData> CODEC = Codec.recursive("MachineConfigLayerData", self -> 
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("moduleSlotType", "").forGetter(MachineConfigData::moduleSlotType),
            Codec.STRING.optionalFieldOf("itemId", "").forGetter(MachineConfigData::itemId),
            Codec.STRING.optionalFieldOf("materialId", "").forGetter(MachineConfigData::materialId),
            Codec.INT.optionalFieldOf("upgradeSlots", 0).forGetter(MachineConfigData::upgradeSlots),
            self.listOf().optionalFieldOf("subLayers", new ArrayList<>()).forGetter(MachineConfigData::subLayers)
        ).apply(instance, MachineConfigData::new)));

    public static final StreamCodec<RegistryFriendlyByteBuf, MachineConfigData> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    public ModuleType getModuleType() {
        if (!itemId.isEmpty()) return ModModuleTypes.getModuleType(itemId);
        else if (!moduleSlotType.isEmpty() && !materialId.isEmpty()) return ModModuleTypes.getModuleType(moduleSlotType);
        return null;
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
            if (newSubLayers.size() > index) newSubLayers.set(index, subLayer);
            else newSubLayers.add(subLayer);
            return new MachineConfigData(original.moduleSlotType, original.itemId, original.materialId, original.upgradeSlots, newSubLayers);
        } else {
            MachineConfigData oldSubLayer = original.getSubLayer(index);
            MachineConfigData newSubLayer = changeSubLayer(oldSubLayer, path.subList(1, path.size()), subLayer);
            newSubLayers.set(index, newSubLayer);
            return new MachineConfigData(original.moduleSlotType, original.itemId, original.materialId, original.upgradeSlots, newSubLayers);
        }
    }
    public static MachineConfigData moveUpgradeSlot(MachineConfigData original, List<Integer> path, boolean add) {
        MachineConfigData updated = moveUpgradeSlotRecursive(original, path, add);
        return new MachineConfigData(updated.moduleSlotType, updated.itemId, updated.materialId, updated.upgradeSlots + (add ? -1 : 1), updated.subLayers);
    }
    private static MachineConfigData moveUpgradeSlotRecursive(MachineConfigData original, List<Integer> path, boolean add) {
        ArrayList<MachineConfigData> newSubLayers = new ArrayList<>(original.subLayers);
        int index = path.get(0);
        if (path.size() == 1) {
            MachineConfigData subLayer = newSubLayers.get(index);
            newSubLayers.set(index, 
                new MachineConfigData(subLayer.moduleSlotType, subLayer.itemId, subLayer.materialId, subLayer.upgradeSlots + (add ? 1 : -1), subLayer.subLayers));
            return new MachineConfigData(original.moduleSlotType, original.itemId, original.materialId, original.upgradeSlots, newSubLayers);
        } else {
            MachineConfigData oldSubLayer = original.getSubLayer(index);
            MachineConfigData newSubLayer = moveUpgradeSlotRecursive(oldSubLayer, path.subList(1, path.size()), add);
            newSubLayers.set(index, newSubLayer);
            return new MachineConfigData(original.moduleSlotType, original.itemId, original.materialId, original.upgradeSlots, newSubLayers);
        }
    }
    public void setSubLayerCount(int count) {
        subLayers.clear();
        for (int i = 0; i < count; i++) {
            subLayers.add(MachineConfigData.EMPTY);
        }
    }
    public boolean isEmpty() {
        return this.itemId.isEmpty() && this.materialId.isEmpty() && this.subLayers.isEmpty();
    }
    public boolean hasSubLayers() {
        for (MachineConfigData subLayer : subLayers) {
            if (!subLayer.isEmpty()) return true;
        }
        return false;
    }
    public boolean isComponent() {
        return !moduleSlotType.isEmpty() && !materialId.isEmpty();
    }
    public Item getItem() {
        if (!itemId.isEmpty()) return ModModuleTypes.getModuleType(itemId).getItem();
        if (!materialId.isEmpty() && !moduleSlotType.isEmpty()) {
            MaterialForm form = ModModuleTypes.getModuleSlotType(moduleSlotType).getMaterialForm();
            return ModMaterials.getMaterial(materialId).getForm(form);
        }
        return null;
    }
    public MachineData toMachineData() {
        if (allSlotsFilled()) {
            return new MachineData(this);
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
