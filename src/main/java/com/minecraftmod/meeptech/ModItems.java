package com.minecraftmod.meeptech;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MeepTech.MODID);

    public static final DeferredItem<Item> HAMMER = ITEMS.register("iron_hammer", () -> new Item(new Item.Properties()
        .durability(1024)
    ));
}
