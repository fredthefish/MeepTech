package com.minecraftmod.meeptech.network;

import com.minecraftmod.meeptech.MeepTech;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MeepTech.MODID)
public class ModNetworking {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(EngineeringActionPacket.TYPE, EngineeringActionPacket.STREAM_CODEC, EngineeringActionPacket::handle);
        registrar.playToServer(FluidTankActionPacket.TYPE, FluidTankActionPacket.STREAM_CODEC, FluidTankActionPacket::handle);
        registrar.playToServer(FluidCellActionPacket.TYPE, FluidCellActionPacket.STREAM_CODEC, FluidCellActionPacket::handle);
        registrar.playToClient(FluidTankSyncPayload.TYPE, FluidTankSyncPayload.STREAM_CODEC, FluidTankSyncPayload::handleOnClient);
    }
}
