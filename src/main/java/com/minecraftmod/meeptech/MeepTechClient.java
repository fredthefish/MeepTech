package com.minecraftmod.meeptech;

import com.minecraftmod.meeptech.logic.BlueprintData;
import com.minecraftmod.meeptech.ui.DesigningStationScreen;
import com.minecraftmod.meeptech.ui.MaterialWorkstationScreen;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
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
        ModMaterials.InitializeMaterials();
        ModMachineComponents.InitializeMachineComponents();
        ModMachineTypes.InitializeMachineTypes();

        event.enqueueWork(() -> 
            ItemProperties.register(ModItems.BLUEPRINT.get(), ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "blueprint_has_machine_type"), 
            (stack, level, entity, seed) -> {
                BlueprintData data = stack.get(ModDataComponents.BLUEPRINT_DATA.get());
                if (data != null && data.machineId() != null && !data.machineId().isEmpty()) return 1; //Machine assigned.
                return 0; //No machine assigned.
            })
        );
    }
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.MATERIAL_WORKSTATION_MENU.get(), MaterialWorkstationScreen::new);
        event.register(ModMenus.DESIGNING_STATION_MENU.get(), DesigningStationScreen::new);
    }
}
