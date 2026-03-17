package com.minecraftmod.meeptech.datagen;

import java.util.List;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.blocks.OreBlock;
import com.minecraftmod.meeptech.blocks.OreStoneType;
import com.minecraftmod.meeptech.features.ModOreVeins;
import com.minecraftmod.meeptech.features.OreVein;
import com.minecraftmod.meeptech.features.OreVeinConfig;
import com.minecraftmod.meeptech.features.VeinCountPlacement;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.registries.ModFeatures;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModWorldgen {
    public static ResourceKey<ConfiguredFeature<?, ?>> configuredOreKey(OreVein vein) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, vein.getId() + "_ore_vein"));
    }
    public static ResourceKey<PlacedFeature> placedOreKey(OreVein vein) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, vein.getId() + "_ore_vein"));
    }
    public static ResourceKey<BiomeModifier> biomeModifierKey(OreVein vein) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, vein.getId() + "_ore_vein"));
    }

    public static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<?,?>> context) {
        for (OreVein vein : ModOreVeins.ORE_VEINS) {
            List<OreVeinConfig.WeightedTarget> targets = vein.getEntries().stream().map(entry -> {
                HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
                BlockState oreStoneState = blocks
                    .getOrThrow(ResourceKey.create(Registries.BLOCK, 
                        ResourceLocation.fromNamespaceAndPath(MeepTech.MODID,entry.material().getId() + "_" + MaterialForm.ORE.getId())))
                    .value().defaultBlockState().setValue(OreBlock.STONE_TYPE, OreStoneType.STONE);
                return new OreVeinConfig.WeightedTarget(entry.weight(), List.of(
                    new OreVeinConfig.TargetOreBlockState(Blocks.STONE.defaultBlockState(), oreStoneState.setValue(OreBlock.STONE_TYPE, OreStoneType.STONE)),
                    new OreVeinConfig.TargetOreBlockState(Blocks.DEEPSLATE.defaultBlockState(), oreStoneState.setValue(OreBlock.STONE_TYPE, OreStoneType.DEEPSLATE)),
                    new OreVeinConfig.TargetOreBlockState(Blocks.GRANITE.defaultBlockState(), oreStoneState.setValue(OreBlock.STONE_TYPE, OreStoneType.GRANITE)),
                    new OreVeinConfig.TargetOreBlockState(Blocks.DIORITE.defaultBlockState(), oreStoneState.setValue(OreBlock.STONE_TYPE, OreStoneType.DIORITE)),
                    new OreVeinConfig.TargetOreBlockState(Blocks.ANDESITE.defaultBlockState(), oreStoneState.setValue(OreBlock.STONE_TYPE, OreStoneType.ANDESITE)),
                    new OreVeinConfig.TargetOreBlockState(Blocks.TUFF.defaultBlockState(), oreStoneState.setValue(OreBlock.STONE_TYPE, OreStoneType.TUFF))));
            }).toList();
            context.register(configuredOreKey(vein), new ConfiguredFeature<>(ModFeatures.ORE_VEIN.get(), 
                new OreVeinConfig(targets, vein.getHorizontalRadius(), vein.getVerticalRadius(), vein.getDensity())));
        }
    }
    public static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        for (int i = 0; i < ModOreVeins.ORE_VEINS.size(); i++) {
            OreVein vein = ModOreVeins.ORE_VEINS.get(i);
            context.register(placedOreKey(vein), new PlacedFeature(configured.getOrThrow(configuredOreKey(vein)), List.of(
                CountPlacement.of(1), 
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(vein.getMinY()), VerticalAnchor.absolute(vein.getMaxY())),
                new VeinCountPlacement(i),
                BiomeFilter.biome()))
            );
        }
    }
    public static void bootstrapBiomeModifiers(BootstrapContext<BiomeModifier> context) {
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderSet<Biome> overworldBiomes = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);
        for (OreVein vein : ModOreVeins.ORE_VEINS) {
            context.register(biomeModifierKey(vein), new BiomeModifiers.AddFeaturesBiomeModifier(overworldBiomes,
                HolderSet.direct(placed.getOrThrow(placedOreKey(vein))), GenerationStep.Decoration.UNDERGROUND_ORES));
        }
    }
}
