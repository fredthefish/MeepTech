package com.minecraftmod.meeptech;

import com.minecraftmod.meeptech.logic.material.ModMaterials;
import com.minecraftmod.meeptech.logic.module.ModModuleTypes;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = MeepTech.MODID)
public class ModSetup {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModMaterials.initializeMaterials();
            ModModuleTypes.initializeModuleTypes();
        });
    }
}
