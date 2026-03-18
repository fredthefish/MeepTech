package com.minecraftmod.meeptech.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.minecraftmod.meeptech.registries.ModPlacementModifiers;
import com.minecraftmod.meeptech.registries.ModServerConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class VeinCountPlacement extends PlacementModifier {
    public static final Map<ChunkPos, Set<Integer>> selectedVeins = new HashMap<>();
    public static final MapCodec<VeinCountPlacement> CODEC = Codec.INT.fieldOf("vein_index").xmap(VeinCountPlacement::new, p -> p.veinIndex);
    private final int veinIndex;
    public VeinCountPlacement(int veinIndex) {
        this.veinIndex = veinIndex;
    }
    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        if (selectedVeins.size() > 1000) selectedVeins.clear(); //Prevents memory leaks.
        int maxVeins = ModServerConfig.INSTANCE.veinsPerChunk.get();
        Set<Integer> selected = selectedVeins.computeIfAbsent(chunkPos, chunk -> {
            //Position and seed is used as random source.
            RandomSource chunkRandom = RandomSource.create(ChunkPos.asLong(chunk.x, chunk.z) ^ context.getLevel().getSeed());
            int totalVeins = ModOreVeins.ORE_VEINS.size();
            //Fisher-Yates shuffling.
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < totalVeins; i++) indices.add(i);
            for (int i = 0; i < Math.min(maxVeins, totalVeins); i++) {
                int j = i + chunkRandom.nextInt(totalVeins - i);
                Collections.swap(indices, i, j);
            }
            return new HashSet<>(indices.subList(0, Math.min(maxVeins, totalVeins)));
        });
        return selected.contains(veinIndex) ? Stream.of(pos) : Stream.empty();
    }
    @Override
    public PlacementModifierType<?> type() {
        return ModPlacementModifiers.VEIN_COUNT.get();
    }
    public static void clearCache() {
        selectedVeins.clear();
    }
}
