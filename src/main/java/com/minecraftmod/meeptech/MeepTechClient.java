package com.minecraftmod.meeptech;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = MeepTech.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MeepTech.MODID, value = Dist.CLIENT)
public class MeepTechClient {
    public MeepTechClient(ModContainer container) {
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }
}
