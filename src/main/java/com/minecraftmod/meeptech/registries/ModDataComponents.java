package com.minecraftmod.meeptech.registries;

import java.util.function.Supplier;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.blocks.OreStoneType;
import com.minecraftmod.meeptech.logic.machine.MachineConfigData;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MeepTech.MODID);

    // public static final Supplier<DataComponentType<BlueprintData>> BLUEPRINT_DATA = 
    //     DATA_COMPONENTS.registerComponentType("blueprint_data", builder -> builder.persistent(BlueprintData.CODEC).networkSynchronized(BlueprintData.STREAM_CODEC));
    public static final Supplier<DataComponentType<MachineConfigData>> MACHINE_CONFIG_DATA =
        DATA_COMPONENTS.registerComponentType("machine_config_data", builder -> 
        builder.persistent(MachineConfigData.CODEC).networkSynchronized(MachineConfigData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<OreStoneType>> STONE_TYPE = 
        DATA_COMPONENTS.register("stone_type", () -> 
        DataComponentType.<OreStoneType>builder().persistent(OreStoneType.CODEC).networkSynchronized(OreStoneType.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID_CELL_CONTENT = 
        DATA_COMPONENTS.register("fluid_cell_content", () -> DataComponentType.<SimpleFluidContent>builder()
                .persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC).build());

}
