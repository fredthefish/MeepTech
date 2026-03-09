package com.minecraftmod.meeptech.items;

import java.util.List;

import com.minecraftmod.meeptech.ModMachineTypes;
import com.minecraftmod.meeptech.logic.MachineType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record BlueprintData(String machineId, List<String> materialIds) {
    public static final Codec<BlueprintData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("machine_id").forGetter(BlueprintData::machineId),
        Codec.STRING.listOf().fieldOf("materials").forGetter(BlueprintData::materialIds)
    ).apply(instance, BlueprintData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlueprintData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, BlueprintData::machineId, ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), BlueprintData::materialIds, BlueprintData::new);
    
    public String getMachineId() {
        return machineId;
    }
    public List<String> getMaterialList() {
        return materialIds;
    }
    public void setMaterialList(List<String> materialIds) {
        this.materialIds.clear();
        this.materialIds.addAll(materialIds);
    }
    public MachineType getMachineType() {
        if (machineId == null) return null;
        String machineId = this.machineId();
        for (MachineType machineType : ModMachineTypes.MACHINE_TYPES) {
            if (machineType.getId().equals(machineId)) return machineType;
        }
        return null;
    }
}
