package com.minecraftmod.meeptech;

import com.minecraftmod.meeptech.blocks.MachineBlockEntityRenderer;
import com.minecraftmod.meeptech.items.MachineItemClientExtension;
import com.minecraftmod.meeptech.ui.EngineeringStationScreen;
import com.minecraftmod.meeptech.ui.MaterialWorkstationScreen;
import com.minecraftmod.meeptech.ui.MachineScreen;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@Mod(value = MeepTech.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MeepTech.MODID, value = Dist.CLIENT)
public class MeepTechClient {
    public MeepTechClient(ModContainer container) {
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        ModMaterials.initializeMaterials();
        ModModuleTypes.initializeModuleTypes();
    }
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.MATERIAL_WORKSTATION_MENU.get(), MaterialWorkstationScreen::new);
        event.register(ModMenus.ENGINEERING_STATION_MENU.get(), EngineeringStationScreen::new);
        event.register(ModMenus.MACHINE_MENU.get(), MachineScreen::new);
    }
    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.BASE_MACHINE_BE.get(), MachineBlockEntityRenderer::new);
    }
    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new MachineItemClientExtension(), ModBlocks.BRICK_HULL_ITEM);
    }
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((ItemStack stack, int tintIndex) -> {
            if (tintIndex == 1) return 0xFFBBBBBB;
            return 0xFFFFFFFF;
        }, ModItems.HAMMER.get());
    }
}
