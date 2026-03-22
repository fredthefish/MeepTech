package com.minecraftmod.meeptech.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.ui.EngineeringStationMenu;
import com.minecraftmod.meeptech.ui.FluidTankMenu;
import com.minecraftmod.meeptech.ui.MachineMenu;
import com.minecraftmod.meeptech.ui.MaterialWorkstationMenu;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MeepTech.MODID);

    public static final Supplier<MenuType<MaterialWorkstationMenu>> MATERIAL_WORKSTATION_MENU = MENUS.register("material_workstation", () -> 
        IMenuTypeExtension.create((windowId, inv, data) -> new MaterialWorkstationMenu(windowId, inv)));
    public static final Supplier<MenuType<EngineeringStationMenu>> ENGINEERING_STATION_MENU = MENUS.register("engineering_station", () ->
        IMenuTypeExtension.create((windowId, inv, data) -> new EngineeringStationMenu(windowId, inv)));
    public static final Supplier<MenuType<MachineMenu>> MACHINE_MENU = MENUS.register("machine", () -> 
        IMenuTypeExtension.create(MachineMenu::new));
    public static final Supplier<MenuType<FluidTankMenu>> FLUID_TANK_MENU = MENUS.register("fluid_tank", () -> 
        IMenuTypeExtension.create(FluidTankMenu::new));
}
