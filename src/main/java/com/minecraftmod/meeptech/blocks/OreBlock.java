package com.minecraftmod.meeptech.blocks;

import java.util.List;

import com.minecraftmod.meeptech.registries.ModDataComponents;
import com.minecraftmod.meeptech.registries.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;

public class OreBlock extends Block {
    public static final EnumProperty<OreStoneType> STONE_TYPE = EnumProperty.create("stone_type", OreStoneType.class);
    public OreBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(STONE_TYPE, OreStoneType.STONE));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STONE_TYPE);
    }
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        OreStoneType stoneType = state.getValue(STONE_TYPE);
        for (ItemStack drop : drops) {
            if (drop.is(ModTags.ORE_TAG)) {
                drop.set(ModDataComponents.STONE_TYPE.get(), stoneType);
            }
        }
        return drops;
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        OreStoneType stoneType = ctx.getItemInHand().get(ModDataComponents.STONE_TYPE.get());
        return defaultBlockState().setValue(STONE_TYPE,
            stoneType != null ? stoneType : OreStoneType.STONE);
    }
    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(this);
        stack.set(ModDataComponents.STONE_TYPE.get(), state.getValue(STONE_TYPE));
        return stack;
    }
}
