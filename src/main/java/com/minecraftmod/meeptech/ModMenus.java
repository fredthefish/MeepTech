package com.minecraftmod.meeptech;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

import com.minecraftmod.meeptech.ui.MaterialWorkstationMenu;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MeepTech.MODID);

    public static final Supplier<MenuType<MaterialWorkstationMenu>> MATERIAL_WORKSTATION_MENU = MENUS.register("material_workstation", () -> 
        IMenuTypeExtension.create((windowId, inv, data) -> new MaterialWorkstationMenu(windowId, inv)));
}
