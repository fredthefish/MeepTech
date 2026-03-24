package com.minecraftmod.meeptech.network;

import com.minecraftmod.meeptech.ui.MachineMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FluidTankSyncPayload(BlockPos pos, int tankIndex, FluidStack fluid) implements CustomPacketPayload {
    public static final Type<FluidTankSyncPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath("meeptech", "fluid_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidTankSyncPayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, FluidTankSyncPayload::pos,
        ByteBufCodecs.INT, FluidTankSyncPayload::tankIndex,
        FluidStack.OPTIONAL_STREAM_CODEC, FluidTankSyncPayload::fluid,
        FluidTankSyncPayload::new);
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public void handleOnClient(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.containerMenu instanceof MachineMenu menu) {
                menu.setFluid(tankIndex, fluid);
            }
        });
    }
}