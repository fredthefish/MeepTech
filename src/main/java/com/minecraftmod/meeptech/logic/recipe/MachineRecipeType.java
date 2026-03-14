package com.minecraftmod.meeptech.logic.recipe;

import java.util.List;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public abstract class MachineRecipeType {
    private String id;
    private DeferredItem<Item> iconItem;
    public MachineRecipeType(String id, DeferredItem<Item> iconItem) {
        this.id = id;
        this.iconItem = iconItem;
    }
    public String getId() {
        return id;
    }
    public Item getIcon() {
        return iconItem.get();
    }
    public abstract void addRecipe(MachineRecipe recipe);
    public abstract List<MachineRecipe> getRecipes();
    public abstract MachineRecipe getRecipe(String recipe);
}
