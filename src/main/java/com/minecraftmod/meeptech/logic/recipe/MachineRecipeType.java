package com.minecraftmod.meeptech.logic.recipe;

public abstract class MachineRecipeType {
    private String id;
    public MachineRecipeType(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public abstract void addRecipe(MachineRecipe recipe);
}
