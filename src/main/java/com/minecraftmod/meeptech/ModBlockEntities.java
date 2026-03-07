package com.minecraftmod.meeptech;

import java.util.function.Supplier;

import com.minecraftmod.meeptech.blocks.DesigningStationBlockEntity;
import com.minecraftmod.meeptech.blocks.DraftingStationBlockEntity;
import com.minecraftmod.meeptech.blocks.MaterialWorkstationBlockEntity;
import com.minecraftmod.meeptech.blocks.machines.PrimitiveSmelterBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MeepTech.MODID);

    public static final Supplier<BlockEntityType<MaterialWorkstationBlockEntity>> MATERIAL_WORKSTATION_BE = 
        BLOCK_ENTITY_TYPES.register("primitive_workstation_be", () -> BlockEntityType.Builder.of(
            MaterialWorkstationBlockEntity::new,
            ModBlocks.MATERIAL_WORKSTATION.get()
    ).build(null));
    public static final Supplier<BlockEntityType<DesigningStationBlockEntity>> DESIGNING_STATION_BE = 
        BLOCK_ENTITY_TYPES.register("designing_station_be", () -> BlockEntityType.Builder.of(
            DesigningStationBlockEntity::new,
            ModBlocks.DESIGNING_STATION.get()
    ).build(null));
    public static final Supplier<BlockEntityType<DraftingStationBlockEntity>> DRAFTING_STATION_BE = 
        BLOCK_ENTITY_TYPES.register("drafting_station_be", () -> BlockEntityType.Builder.of(
            DraftingStationBlockEntity::new,
            ModBlocks.DRAFTING_STATION.get()
    ).build(null));
    public static final Supplier<BlockEntityType<PrimitiveSmelterBlockEntity>> PRIMITIVE_SMELTER_BE =
        BLOCK_ENTITY_TYPES.register("primitive_smelter_be", () -> BlockEntityType.Builder.of(
            PrimitiveSmelterBlockEntity::new,
            ModBlocks.PRIMITIVE_SMELTER.get()
    ).build(null));
}
