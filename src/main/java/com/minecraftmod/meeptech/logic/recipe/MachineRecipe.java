package com.minecraftmod.meeptech.logic.recipe;

public abstract class MachineRecipe {
    String id;
    public MachineRecipe(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
}
