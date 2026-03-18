package com.minecraftmod.meeptech.features;

import java.util.List;

import com.minecraftmod.meeptech.registries.ModServerConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
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
        int radiusScale = ModServerConfig.INSTANCE.veinRadius.get();
        int xRad = config.getXRad() * radiusScale;
        int yRad = config.getYRad() * radiusScale;
        int chunkX = SectionPos.blockToSectionCoord(origin.getX());
        int chunkZ = SectionPos.blockToSectionCoord(origin.getZ());
        int minChunkX = SectionPos.sectionToBlockCoord(chunkX);
        int minChunkZ = SectionPos.sectionToBlockCoord(chunkZ);
        int maxChunkX = minChunkX + 15;
        int maxChunkZ = minChunkZ + 15;

        float density = config.getDensity();
        boolean placed = false;
        for (int x = -xRad; x <= xRad; x++) {
            for (int y = -yRad; y <= yRad; y++) {
                for (int z = -xRad; z <= xRad; z++) {
                    double dist = ((double)(x*x) / (xRad*xRad)) + ((double)(y*y) / (yRad*yRad)) + ((double)(z*z) / (xRad + xRad));
                    if (dist > 1) continue;
                    if (random.nextFloat() > density) continue;
                    BlockPos pos = origin.offset(x, y, z);
                    if (pos.getX() < minChunkX || pos.getX() > maxChunkX || pos.getZ() < minChunkZ || pos.getZ() > maxChunkZ) continue;
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
