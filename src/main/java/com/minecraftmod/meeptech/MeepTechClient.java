package com.minecraftmod.meeptech;

import com.minecraftmod.meeptech.ui.EngineeringStationScreen;
import com.minecraftmod.meeptech.ui.MaterialWorkstationScreen;
import com.minecraftmod.meeptech.ui.MachineScreen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = MeepTech.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MeepTech.MODID, value = Dist.CLIENT)
public class MeepTechClient {
    public MeepTechClient(ModContainer container) {
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        ModMaterials.initializeMaterials();
        ModModuleTypes.initializeModuleTypes();
        ModModuleData.initializeModuleData();
    }
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.MATERIAL_WORKSTATION_MENU.get(), MaterialWorkstationScreen::new);
        event.register(ModMenus.ENGINEERING_STATION_MENU.get(), EngineeringStationScreen::new);
        event.register(ModMenus.MACHINE_MENU.get(), MachineScreen::new);
    }
}
