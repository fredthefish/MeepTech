package com.minecraftmod.meeptech.blocks;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;

public class FluidTankBlock extends Block implements EntityBlock {
    private final int capacity;
    public FluidTankBlock(Properties props, int capacity) {
        super(props);
        this.capacity = capacity;
    }
    public int getCapacity() {
        return capacity; 
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidTankBlockEntity(pos, state, capacity);
    }
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof FluidTankBlockEntity tankEntity)) return InteractionResult.PASS;
        ItemStack heldItem = player.getMainHandItem();
        if (FluidUtil.getFluidHandler(heldItem).isPresent()) {
            return tankEntity.useBucket(player, InteractionHand.MAIN_HAND);
        }
        if (level instanceof ServerLevel) {
            player.openMenu(tankEntity, pos);
        }
        return InteractionResult.CONSUME;
    }
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data != null && level.getBlockEntity(pos) instanceof FluidTankBlockEntity tankEntity) {
            tankEntity.loadAdditional(data.copyTag(), level.registryAccess());
        }
    }
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof FluidTankBlockEntity tankEntity && !tankEntity.getTank().isEmpty()) {
            for (ItemStack stack : drops) {
                if (stack.is(this.asItem())) {
                    stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tankEntity.saveWithId(params.getLevel().registryAccess())));
                }
            }
        }
        return drops;
    }
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null) return;
        CompoundTag tag = data.copyTag();
        if (!tag.contains("tank")) return;
        CompoundTag tankTag = tag.getCompound("tank");
        if (!tankTag.contains("Fluid")) return;
        CompoundTag fluidTag = tankTag.getCompound("Fluid");
        int amount = fluidTag.getInt("amount");
        if (amount <= 0) return;
        String fluidId = fluidTag.getString("id");
        ResourceLocation fluidLoc = ResourceLocation.tryParse(fluidId);
        if (fluidLoc == null) return;
        Fluid fluid = BuiltInRegistries.FLUID.get(fluidLoc);
        Component fluidName = fluid.getFluidType().getDescription();
        tooltip.add(Component.translatable("tooltip.meeptech.fluid_tank.fluid", fluidName, amount, capacity));
    }
}
