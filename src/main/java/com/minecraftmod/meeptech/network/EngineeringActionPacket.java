package com.minecraftmod.meeptech.network;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record EngineeringActionPacket(EngineeringAction action, List<Integer> path) implements CustomPacketPayload {
    public static final Type<EngineeringActionPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("meeptech", "engineering_action"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EngineeringActionPacket> STREAM_CODEC = StreamCodec.composite(
        NeoForgeStreamCodecs.enumCodec(EngineeringAction.class), EngineeringActionPacket::action, 
        ByteBufCodecs.INT.apply(ByteBufCodecs.list()), EngineeringActionPacket::path,
        EngineeringActionPacket::new);
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public enum EngineeringAction {
        INSERT,
        EXTRACT,
        ADD_UPGRADE_SLOT,
        REMOVE_UPGRADE_SLOT,
        INSERT_UPGRADE,
        EXTRACT_UPGRADE
    }
}