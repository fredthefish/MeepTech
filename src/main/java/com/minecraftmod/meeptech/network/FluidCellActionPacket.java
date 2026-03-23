package com.minecraftmod.meeptech.network;

import com.minecraftmod.meeptech.ui.FluidTankMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FluidCellActionPacket(BlockPos pos, FluidCellAction action, boolean shift) implements CustomPacketPayload {
    public static final Type<FluidCellActionPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("meeptech", "fluid_cell_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidCellActionPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, FluidCellActionPacket::pos, NeoForgeStreamCodecs.enumCodec(FluidCellAction.class), FluidCellActionPacket::action,
            ByteBufCodecs.BOOL, FluidCellActionPacket::shift, FluidCellActionPacket::new);
    @Override
    public Type<? extends CustomPacketPayload> type() { 
        return TYPE; 
    }
    public static void handle(FluidCellActionPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player.containerMenu instanceof FluidTankMenu menu) {
                menu.handleFluidCellClick(packet.action(), packet.shift());
            }
        });
    }
    public enum FluidCellAction { EXTRACT_INTO_CELL, INSERT_INTO_TANK }
}