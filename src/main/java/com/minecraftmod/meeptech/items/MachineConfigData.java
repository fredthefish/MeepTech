package com.minecraftmod.meeptech.items;

import java.util.List;

import com.minecraftmod.meeptech.ModModuleTypes;
import com.minecraftmod.meeptech.logic.ModuleType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MachineConfigData(String moduleId, String materialId, List<MachineConfigData> subLayers) {
    public static final MachineConfigData EMPTY = new MachineConfigData("", "", List.of());

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
    public void setSubLayer(int index, MachineConfigData subLayer) {
        if (subLayers.size() > index) {
            subLayers.set(index, subLayer);
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
}
