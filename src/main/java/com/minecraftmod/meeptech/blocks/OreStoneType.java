package com.minecraftmod.meeptech.blocks;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum OreStoneType implements StringRepresentable {
    STONE("stone"),
    DEEPSLATE("deepslate"),
    GRANITE("granite"),
    DIORITE("diorite"),
    ANDESITE("andesite"),
    TUFF("tuff");

    private final String name;
    OreStoneType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
    public static final Codec<OreStoneType> CODEC = StringRepresentable.fromEnum(OreStoneType::values);

    public static final StreamCodec<ByteBuf, OreStoneType> STREAM_CODEC =
    ByteBufCodecs.STRING_UTF8.map(
        stoneType -> OreStoneType.valueOf(stoneType.toUpperCase()),
        OreStoneType::getSerializedName
    );
}
