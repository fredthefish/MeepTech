package com.minecraftmod.meeptech;

import java.util.function.Supplier;

import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.blocks.MachineBlockEntityRenderer;
import com.minecraftmod.meeptech.blocks.OreStoneType;
import com.minecraftmod.meeptech.blocks.pipes.PipeBlockEntityRenderer;
import com.minecraftmod.meeptech.blocks.pipes.PipeSprites;
import com.minecraftmod.meeptech.items.MachineItemClientExtension;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.ModMaterials;
import com.minecraftmod.meeptech.registries.ModBlockEntities;
import com.minecraftmod.meeptech.registries.ModBlocks;
import com.minecraftmod.meeptech.registries.ModDataComponents;
import com.minecraftmod.meeptech.registries.ModFluidTypes;
import com.minecraftmod.meeptech.registries.ModItems;
import com.minecraftmod.meeptech.registries.ModMenus;
import com.minecraftmod.meeptech.ui.EngineeringStationScreen;
import com.minecraftmod.meeptech.ui.FluidTankScreen;
import com.minecraftmod.meeptech.ui.MaterialWorkstationScreen;
import com.minecraftmod.meeptech.ui.MachineScreen;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
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
        event.register(ModMenus.FLUID_TANK_MENU.get(), FluidTankScreen::new);
    }
    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (Supplier<BlockEntityType<BaseMachineBlockEntity>> blockEntity : ModBlockEntities.HULL_BLOCK_ENTITIES.values())
            event.registerBlockEntityRenderer(blockEntity.get(), MachineBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ITEM_PIPE_BE.get(), PipeBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FLUID_PIPE_BE.get(), PipeBlockEntityRenderer::new);
    }
    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        for (DeferredBlock<Block> block : ModBlocks.HULL_BLOCKS.values()) {
            event.registerItem(new MachineItemClientExtension(), block.get().asItem());
        }
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/fluid/thick_still");
            }
            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/fluid/thick_flowing");
            }
            @Override
            public int getTintColor() {
                return 0x88DDDDDD;
            }
        }, ModFluidTypes.STEAM_TYPE.get());
    }
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        for (Material material : ModMaterials.MATERIALS) {
            if (material.hasForm(MaterialForm.HULL)) {
                event.register((BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) -> {
                    if (tintIndex == 0) {
                        return material.getColor();
                    }
                    return 0xFFFFFFFF;
                }, ModBlocks.HULL_BLOCKS.get(material).get());
            }
            if (material.hasForm(MaterialForm.ORE)) {
                event.register((BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) -> {
                    if (tintIndex == 0) {
                        return material.getColor();
                    }
                    return 0xFFFFFFFF;
                }, ModBlocks.ORE_BLOCKS.get(material).get());
            }
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
        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                FluidStack fluidStack = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
                if (!fluidStack.isEmpty()) return IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
            }
            return 0xFFFFFFFF; // default white (no tint)
        }, ModItems.FLUID_CELL.get());
    }
    @SubscribeEvent
    public static void registerItemProperties(FMLClientSetupEvent event) {
        for (DeferredBlock<Block> oreBlock : ModBlocks.ORE_BLOCKS.values()) {
            ItemProperties.register(oreBlock.get().asItem(),
                ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "stone_type"),
                (stack, level, entity, seed) -> {
                    OreStoneType stoneType = stack.get(ModDataComponents.STONE_TYPE.get());
                    if (stoneType == null) return 0f;
                    return stoneType.ordinal();
                }
            );
        }
    }
    @SubscribeEvent
    public static void onModelsBaked(ModelEvent.BakingCompleted event) {
        PipeSprites.init(event);
    }
}