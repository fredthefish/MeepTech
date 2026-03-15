package com.minecraftmod.meeptech.registries;

import java.util.function.Supplier;

import com.minecraftmod.meeptech.MeepTech;
// import com.minecraftmod.meeptech.items.BlueprintData;
import com.minecraftmod.meeptech.items.MachineConfigData;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MeepTech.MODID);

    // public static final Supplier<DataComponentType<BlueprintData>> BLUEPRINT_DATA = 
    //     DATA_COMPONENTS.registerComponentType("blueprint_data", builder -> builder.persistent(BlueprintData.CODEC).networkSynchronized(BlueprintData.STREAM_CODEC));
    public static final Supplier<DataComponentType<MachineConfigData>> MACHINE_CONFIG_DATA =
        DATA_COMPONENTS.registerComponentType("machine_config_data", builder -> 
        builder.persistent(MachineConfigData.CODEC).networkSynchronized(MachineConfigData.STREAM_CODEC));
}
