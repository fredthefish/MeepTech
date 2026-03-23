package com.minecraftmod.meeptech.registries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.blocks.EngineeringStationBlockEntity;
import com.minecraftmod.meeptech.blocks.FluidTankBlockEntity;
import com.minecraftmod.meeptech.blocks.MaterialWorkstationBlockEntity;
import com.minecraftmod.meeptech.blocks.pipes.PipeBlockEntity;
import com.minecraftmod.meeptech.blocks.FluidTankBlock;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MeepTech.MODID);
    public static final Map<DeferredBlock<Block>, Supplier<BlockEntityType<BaseMachineBlockEntity>>> HULL_BLOCK_ENTITIES = new HashMap<>();

    public static final Supplier<BlockEntityType<MaterialWorkstationBlockEntity>> MATERIAL_WORKSTATION_BE = 
        BLOCK_ENTITY_TYPES.register("primitive_workstation_be", () -> BlockEntityType.Builder.of(
            MaterialWorkstationBlockEntity::new,
            ModBlocks.MATERIAL_WORKSTATION.get()
        ).build(null));
    public static final Supplier<BlockEntityType<EngineeringStationBlockEntity>> ENGINEERING_STATION_BE = 
        BLOCK_ENTITY_TYPES.register("engineering_station_be", () -> BlockEntityType.Builder.of(
            EngineeringStationBlockEntity::new,
            ModBlocks.ENGINEERING_STATION.get()
        ).build(null));
    public static final Supplier<BlockEntityType<PipeBlockEntity>> PIPE_BE = 
        BLOCK_ENTITY_TYPES.register("pipe_be", () -> BlockEntityType.Builder.of(PipeBlockEntity::new, ModBlocks.ITEM_PIPE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidTankBlockEntity>> FLUID_TANK_BE = 
        BLOCK_ENTITY_TYPES.register("fluid_tank", () -> BlockEntityType.Builder.of((pos, state) -> {
            if (state.getBlock() instanceof FluidTankBlock tankBlock) return new FluidTankBlockEntity(pos, state, tankBlock.getCapacity());
            return new FluidTankBlockEntity(pos, state, 8000);
        },  ModBlocks.FLUID_TANK.get()).build(null));
    static {
        for (DeferredBlock<Block> block : ModBlocks.HULL_BLOCKS.values()) {
            Supplier<BlockEntityType<BaseMachineBlockEntity>> hullBlockEntity = BLOCK_ENTITY_TYPES.register(block.getId().getPath() + "_be", 
                () -> BlockEntityType.Builder.of((pos, state) -> new BaseMachineBlockEntity(pos, state, block.get()), block.get()).build(null));
            HULL_BLOCK_ENTITIES.put(block, hullBlockEntity);
        }
    }
}
