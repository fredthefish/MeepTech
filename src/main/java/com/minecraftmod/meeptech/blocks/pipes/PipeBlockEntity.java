package com.minecraftmod.meeptech.blocks.pipes;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.minecraftmod.meeptech.blocks.IFluidTankBlockEntity;
import com.minecraftmod.meeptech.registries.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class PipeBlockEntity extends BlockEntity {
    private final Map<Direction, PipeConnection> faces = new EnumMap<>(Direction.class);
    private final int tickOffset;
    private final PipeType pipeType;
    public PipeBlockEntity(BlockPos pos, BlockState state, PipeType pipeType) {
        super(getEntityType(pipeType), pos, state);
        this.pipeType = pipeType;
        for (Direction dir : Direction.values()) {
            faces.put(dir, PipeConnection.NONE);
        }
        tickOffset = Math.abs((pos.getX() + pos.getY() + pos.getZ()) % 5);
    }
    private static BlockEntityType<PipeBlockEntity> getEntityType(PipeType type) {
        if (type == PipeType.ITEM) return ModBlockEntities.ITEM_PIPE_BE.get();
        if (type == PipeType.FLUID) return ModBlockEntities.FLUID_PIPE_BE.get();
        return null;
    }
    public PipeConnection getFace(Direction dir) {
        return faces.get(dir);
    }
    public PipeType getPipeType() {
        return pipeType;
    }
    public void setFace(Direction dir, PipeConnection connection) {
        faces.put(dir, connection);
        setChanged();
    }
    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag facesTag = new CompoundTag();
        for (Direction dir : Direction.values()) {
            PipeConnection attachment = getFace(dir);
            CompoundTag faceTag = new CompoundTag();
            faceTag.putString("type", attachment.getSerializedName());
            facesTag.put(dir.getSerializedName(), faceTag);
        }
        tag.put("faces", facesTag);
    }
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("faces")) {
            CompoundTag facesTag = tag.getCompound("faces");
            for (Direction dir : Direction.values()) {
                if (facesTag.contains(dir.getSerializedName())) {
                    CompoundTag faceTag = facesTag.getCompound(dir.getSerializedName());
                    PipeConnection type = PipeConnection.valueOf(faceTag.getString("type").toUpperCase());
                    faces.put(dir, type);
                }
            }
        }
    }
    public static void tickItem(Level level, BlockPos pos, BlockState state, PipeBlockEntity entity) {
        if (level == null || level.isClientSide()) return;
        if (level.getGameTime() % 5 != entity.tickOffset) return;
        ServerLevel serverLevel = (ServerLevel) level;
        for (Direction dir : Direction.values()) {
            PipeConnection attachment = entity.faces.get(dir);
            if (attachment != PipeConnection.EXTRACTOR) continue;
            tickItemExtractor(dir, attachment, serverLevel, pos, serverLevel);
        }
    }
    private static void tickItemExtractor(Direction dir, PipeConnection attachment, ServerLevel serverLevel, BlockPos worldPosition, ServerLevel level) {
        BlockPos sourcePos = worldPosition.relative(dir);
        IItemHandler source = serverLevel.getCapability(Capabilities.ItemHandler.BLOCK, sourcePos, dir.getOpposite());
        if (source == null) return;
        PipeNetwork network = PipeNetworkManager.get(serverLevel).getNetwork(worldPosition);
        if (network == null) return;
        List<ItemPipeTransfer.InserterTarget> validInserters = ItemPipeTransfer.getValidInserters(network, serverLevel);
        if (validInserters.isEmpty()) return;
        int throughput = 8;
        Map<ItemPipeTransfer.InserterTarget, Integer> allocations = ItemPipeTransfer.distribute(throughput, validInserters, source);
        if (allocations.isEmpty()) return;
        Map<ItemPipeTransfer.InserterTarget, List<ItemStack>> simulatedInsertions = ItemPipeTransfer.simulate(allocations, source);
        int totalExtraction = simulatedInsertions.values().stream().flatMap(List::stream).mapToInt(ItemStack::getCount).sum();
        if (totalExtraction == 0) return;
        List<ItemStack> extracted = ItemPipeTransfer.extract(source, totalExtraction);
        ItemPipeTransfer.insert(simulatedInsertions, extracted, level, source);
    }
    public static void tickFluid(Level level, BlockPos pos, BlockState state, PipeBlockEntity entity) {
        if (level == null || level.isClientSide()) return;
        if (level.getGameTime() % 5 != entity.tickOffset) return;
        ServerLevel serverLevel = (ServerLevel) level;
        for (Direction dir : Direction.values()) {
            PipeConnection attachment = entity.faces.get(dir);
            if (attachment != PipeConnection.EXTRACTOR) continue;
            tickFluidExtractor(dir, attachment, serverLevel, pos, serverLevel);
        }
    }
    private static void tickFluidExtractor(Direction dir, PipeConnection attachment, ServerLevel serverLevel, BlockPos worldPosition, ServerLevel level) {
        BlockPos sourcePos = worldPosition.relative(dir);
        IFluidHandler source;
        if (serverLevel.getBlockEntity(sourcePos) instanceof IFluidTankBlockEntity tankEntity) source = tankEntity.getAutomationFluidHandler();
        else source = serverLevel.getCapability(Capabilities.FluidHandler.BLOCK, sourcePos, dir.getOpposite());
        if (source == null) return;
        PipeNetwork network = PipeNetworkManager.get(serverLevel).getNetwork(worldPosition);
        if (network == null) return;
        List<FluidPipeTransfer.InserterTarget> validInserters = FluidPipeTransfer.getValidInserters(network, serverLevel);
        if (validInserters.isEmpty()) return;
        int throughput = 500;
        Map<FluidPipeTransfer.InserterTarget, Integer> allocations = FluidPipeTransfer.distribute(throughput, validInserters, source);
        if (allocations.isEmpty()) return;
        Map<FluidPipeTransfer.InserterTarget, List<FluidStack>> simulatedInsertions = FluidPipeTransfer.simulate(allocations, source);
        int totalExtraction = simulatedInsertions.values().stream().flatMap(List::stream).mapToInt(FluidStack::getAmount).sum();
        if (totalExtraction == 0) return;
        List<FluidStack> extracted = FluidPipeTransfer.extract(source, totalExtraction);
        FluidPipeTransfer.insert(simulatedInsertions, extracted, level, source);
    }
    public enum PipeType {
        ITEM("item"),
        FLUID("fluid");
        String name;
        private PipeType(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return name;
        }
        public static PipeType fromString(String name) {
            for (PipeType type : values()) {
                if (type.name.equals(name)) return type;
            }
            return null;
        }
    }
}
