package com.minecraftmod.meeptech.features;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class OreVeinFeature extends Feature<OreVeinConfig> {
    public OreVeinFeature() {
        super(OreVeinConfig.CODEC);
    }
    @Override
    public boolean place(FeaturePlaceContext<OreVeinConfig> context) {
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        OreVeinConfig config = context.config();
        int xRad = config.getXRad();
        int yRad = config.getYRad();
        float density = config.getDensity();
        boolean placed = false;
        for (int x = -xRad / 2; x <= xRad / 2; x++) {
            for (int y = -yRad / 2; y <= yRad / 2; y++) {
                for (int z = -xRad / 2; z <= xRad / 2; z++) {
                    double dist = ((double)(x*x) / (xRad*xRad)) + ((double)(y*y) / (yRad*yRad)) + ((double)(z*z) / (xRad + xRad));
                    if (dist > 1) continue;
                    if (random.nextFloat() > density) continue;
                    BlockPos pos = origin.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    OreVeinConfig.WeightedTarget chosen = pickWeighted(config.getTargets(), random);
                    if (chosen == null) continue;
                    for (OreVeinConfig.TargetOreBlockState target : chosen.targets()) {
                        if (current.is(target.target().getBlock())) {
                            level.setBlock(pos, target.ore(), 2);
                            placed = true;
                            break;
                        }
                    }
                }
            }
        }
        return placed;
    }
    private OreVeinConfig.WeightedTarget pickWeighted(List<OreVeinConfig.WeightedTarget> targets, RandomSource random) {
        float total = 0;
        for (OreVeinConfig.WeightedTarget target : targets) total += target.weight();
        float roll = random.nextFloat() * total;
        for (OreVeinConfig.WeightedTarget target : targets) {
            roll -= target.weight();
            if (roll <= 0) return target;
        }
        return targets.isEmpty() ? null : targets.getLast();
    }
}
