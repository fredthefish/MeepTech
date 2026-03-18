package com.minecraftmod.meeptech.registries;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.features.VeinCountPlacement;
import com.minecraftmod.meeptech.logic.material.ModMaterials;
import com.minecraftmod.meeptech.logic.module.ModModuleTypes;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = MeepTech.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModMaterials.initializeMaterials();
            ModModuleTypes.initializeModuleTypes();
        });
    }
    @SubscribeEvent
    public static void onModConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == ModServerConfig.SPEC) VeinCountPlacement.clearCache();
    }
    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() != ModServerConfig.SPEC) return;
        ModConfigSpec.IntValue veinRadius = ModServerConfig.INSTANCE.veinRadius;
        ModConfigSpec.IntValue veinsPerChunk = ModServerConfig.INSTANCE.veinsPerChunk;
        ModConfigSpec.BooleanValue isInitialized = ModServerConfig.INSTANCE.initializedFromCommon;
        if (!isInitialized.get()) {
            veinRadius.set(ModCommonConfig.INSTANCE.defaultVeinRadius.get());
            veinsPerChunk.set(ModCommonConfig.INSTANCE.defaultVeinsPerChunk.get());
            isInitialized.set(true);
        }
    }
}
