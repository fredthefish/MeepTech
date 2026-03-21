package com.minecraftmod.meeptech.blocks.pipes;

import net.minecraft.util.StringRepresentable;

public enum PipeConnection implements StringRepresentable {
    NONE("none"),
    PIPE("pipe"),
    EXTRACTOR("extractor"),
    INSERTER("inserter");

    private final String name;
    PipeConnection(String name) {
        this.name = name;
    }
    @Override
    public String getSerializedName() {
        return name;
    }
}
