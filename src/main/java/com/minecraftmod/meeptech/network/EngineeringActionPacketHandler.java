package com.minecraftmod.meeptech.network;

import com.minecraftmod.meeptech.ui.EngineeringStationMenu;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EngineeringActionPacketHandler {
    public static void handle(EngineeringActionPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.containerMenu instanceof EngineeringStationMenu menu) {
                menu.handleVirtualAction(packet.action(), packet.path());
            }
        });
    }
}
