package com.minecraftmod.meeptech;

import net.neoforged.neoforge.registries.DeferredRegister;

// import com.minecraftmod.meeptech.items.BlueprintItem;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MeepTech.MODID);

    public static final DeferredItem<Item> HAMMER = ITEMS.registerSimpleItem("iron_hammer", new Item.Properties()
        .durability(256)
    );
    public static final DeferredItem<Item> IRON_PLATE = ITEMS.registerSimpleItem("iron_plate");
    public static final DeferredItem<Item> BRICK_PLATE = ITEMS.registerSimpleItem("brick_plate");
    // public static final DeferredItem<Item> BLUEPRINT = ITEMS.register("blueprint", () -> new BlueprintItem(new Item.Properties()));
}
