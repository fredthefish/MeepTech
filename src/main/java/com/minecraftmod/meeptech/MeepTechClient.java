package com.minecraftmod.meeptech;

import java.util.function.Supplier;

import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.blocks.MachineBlockEntityRenderer;
import com.minecraftmod.meeptech.items.MachineItemClientExtension;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.ModMaterials;
import com.minecraftmod.meeptech.registries.ModBlockEntities;
import com.minecraftmod.meeptech.registries.ModBlocks;
import com.minecraftmod.meeptech.registries.ModItems;
import com.minecraftmod.meeptech.registries.ModMenus;
import com.minecraftmod.meeptech.ui.EngineeringStationScreen;
import com.minecraftmod.meeptech.ui.MaterialWorkstationScreen;
import com.minecraftmod.meeptech.ui.MachineScreen;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;

@Mod(value = MeepTech.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MeepTech.MODID, value = Dist.CLIENT)
public class MeepTechClient {
    public MeepTechClient(ModContainer container) {
    }
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.MATERIAL_WORKSTATION_MENU.get(), MaterialWorkstationScreen::new);
        event.register(ModMenus.ENGINEERING_STATION_MENU.get(), EngineeringStationScreen::new);
        event.register(ModMenus.MACHINE_MENU.get(), MachineScreen::new);
    }
    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (Supplier<BlockEntityType<BaseMachineBlockEntity>> blockEntity : ModBlockEntities.HULL_BLOCK_ENTITIES.values())
            event.registerBlockEntityRenderer(blockEntity.get(), MachineBlockEntityRenderer::new);
    }
    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        for (DeferredBlock<Block> block : ModBlocks.HULL_BLOCKS.values()) {
            event.registerItem(new MachineItemClientExtension(), block.get().asItem());
        }
    }
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for (Material material : ModMaterials.MATERIALS) {
            for (MaterialForm form : material.getGeneratedForms()) {
                event.register((ItemStack stack, int tintIndex) -> {
                    if (tintIndex == 0) return material.getColor();
                    return 0xFFFFFFFF;
                }, material.getForm(form));
            }
        }
        event.register((ItemStack stack, int tintIndex) -> {
            if (tintIndex == 1) return 0xFFBBBBBB;
            return 0xFFFFFFFF;
        }, ModItems.HAMMER.get());
    }
}