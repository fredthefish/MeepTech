package com.minecraftmod.meeptech.features;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.block.state.BlockState;

public class OreVeinConfig implements FeatureConfiguration {
    private final List<WeightedTarget> targets;
    private final int xRad;
    private final int yRad;
    private final float density;

    public OreVeinConfig(List<WeightedTarget> targets, int xRad, int yRad, float density) {
        this.targets = targets;
        this.xRad = xRad;
        this.yRad = yRad;
        this.density = density;
    }
    public List<WeightedTarget> getTargets() {
        return targets;
    }
    public int getXRad() {
        return xRad;
    }
    public int getYRad() {
        return yRad;
    }
    public float getDensity() {
        return density;
    }

    public static final Codec<OreVeinConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        WeightedTarget.CODEC.listOf().fieldOf("targets").forGetter(OreVeinConfig::getTargets),
        Codec.INT.fieldOf("width").forGetter(OreVeinConfig::getXRad),
        Codec.INT.fieldOf("height").forGetter(OreVeinConfig::getYRad),
        Codec.FLOAT.fieldOf("density").forGetter(OreVeinConfig::getDensity)
    ).apply(inst, OreVeinConfig::new));

    public record TargetOreBlockState(BlockState target, BlockState ore) {
        public static final Codec<TargetOreBlockState> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BlockState.CODEC.fieldOf("target").forGetter(TargetOreBlockState::target),
            BlockState.CODEC.fieldOf("ore").forGetter(TargetOreBlockState::ore)
        ).apply(inst, TargetOreBlockState::new));
    }
    public record WeightedTarget(float weight, List<TargetOreBlockState> targets) {
        public static final Codec<WeightedTarget> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.FLOAT.fieldOf("weight").forGetter(WeightedTarget::weight),
            TargetOreBlockState.CODEC.listOf().fieldOf("targets").forGetter(WeightedTarget::targets)
        ).apply(inst, WeightedTarget::new));
    }
}
