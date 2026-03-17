package com.minecraftmod.meeptech.blocks;

import net.minecraft.util.StringRepresentable;

public enum OreStoneType implements StringRepresentable {
    STONE("stone"),
    DEEPSLATE("deepslate");

    private final String name;
    OreStoneType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
