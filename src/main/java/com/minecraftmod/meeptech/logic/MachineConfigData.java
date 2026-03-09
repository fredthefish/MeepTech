package com.minecraftmod.meeptech.logic;

import java.util.List;

import com.minecraftmod.meeptech.ModModuleTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MachineConfigData(String moduleId, List<MachineConfigData> subLayers) {
    public static final Codec<MachineConfigData> CODEC = Codec.recursive("MachineConfigLayerData", self -> 
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("itemId").forGetter(MachineConfigData::moduleId),
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
}
