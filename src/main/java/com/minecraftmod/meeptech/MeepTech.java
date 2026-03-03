package com.minecraftmod.meeptech;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.fml.ModContainer;

@Mod(MeepTech.MODID)
public class MeepTech {
    public static final String MODID = "meeptech";

    public MeepTech(IEventBus modEventBus, ModContainer modContainer) {
        Items.ITEMS.register(modEventBus);
        Blocks.BLOCKS.register(modEventBus);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        
    }
}
