package com.minecraftmod.meeptech;

import java.util.List;

import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.logic.BlueprintData;
import com.minecraftmod.meeptech.logic.Material;
import com.minecraftmod.meeptech.ui.DesigningStationScreen;
import com.minecraftmod.meeptech.ui.DraftingStationScreen;
import com.minecraftmod.meeptech.ui.EngineeringStationScreen;
import com.minecraftmod.meeptech.ui.MaterialWorkstationScreen;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
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
            ItemProperties.register(ModItems.BLUEPRINT.get(), ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "blueprint_status"), 
            (stack, level, entity, seed) -> {
                BlueprintData data = stack.get(ModDataComponents.BLUEPRINT_DATA.get());
                if (data != null && data.machineId() != null && !data.machineId().isEmpty()) {
                    if (data.getMaterialList() != null && !data.getMaterialList().isEmpty()) return 2; //Full blueprint.
                    return 1; //Empty machine blueprint.   
                }
                return 0; //No machine assigned.
            })
        );
    }
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.MATERIAL_WORKSTATION_MENU.get(), MaterialWorkstationScreen::new);
        event.register(ModMenus.DESIGNING_STATION_MENU.get(), DesigningStationScreen::new);
        event.register(ModMenus.DRAFTING_STATION_MENU.get(), DraftingStationScreen::new);
        event.register(ModMenus.ENGINEERING_STATION_MENU.get(), EngineeringStationScreen::new);
    }
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (level != null && pos != null && level.getBlockEntity(pos) instanceof BaseMachineBlockEntity machine) {
                List<String> materials = machine.getComponentMaterials();
                if (tintIndex >= 0 && tintIndex < materials.size()) {
                    String materialName = materials.get(tintIndex);
                    Material material = ModMaterials.getMaterial(materialName);
                    return material.getColor();
                }
            }
            return 0xFFFFFFFF;
        }, ModBlocks.PRIMITIVE_SMELTER.get());
    }
}
