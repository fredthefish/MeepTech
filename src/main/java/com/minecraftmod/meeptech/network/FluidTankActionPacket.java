package com.minecraftmod.meeptech.network;

import com.minecraftmod.meeptech.ui.FluidTankMenu;
import com.minecraftmod.meeptech.ui.MachineMenu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FluidTankActionPacket(int tank) implements CustomPacketPayload {
    public static final Type<FluidTankActionPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("meeptech", "fluid_tank_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidTankActionPacket> STREAM_CODEC =
        StreamCodec.composite(ByteBufCodecs.INT, FluidTankActionPacket::tank, FluidTankActionPacket::new);
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static void handle(FluidTankActionPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.containerMenu instanceof FluidTankMenu menu) {
                menu.handleCellClick(menu, packet.tank());
            }
            if (player.containerMenu instanceof MachineMenu menu) {
                menu.handleCellClick(menu, packet.tank());
            }
        });
    }
}