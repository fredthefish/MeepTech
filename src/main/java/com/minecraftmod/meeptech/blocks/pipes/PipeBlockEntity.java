package com.minecraftmod.meeptech.blocks.pipes;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.minecraftmod.meeptech.blocks.pipes.ItemPipeTransfer.InserterTarget;
import com.minecraftmod.meeptech.registries.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class PipeBlockEntity extends BlockEntity {
    private final Map<Direction, PipeConnection> faces = new EnumMap<>(Direction.class);
    private final int tickOffset;
    public PipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIPE_BE.get(), pos, state);
        for (Direction dir : Direction.values()) {
            faces.put(dir, PipeConnection.NONE);
        }
        tickOffset = Math.abs((pos.getX() + pos.getY() + pos.getZ()) % 5);
    }
    public PipeConnection getFace(Direction dir) {
        return faces.get(dir);
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
    public static void tick(Level level, BlockPos pos, BlockState state, PipeBlockEntity entity) {
        if (level == null || level.isClientSide()) return;
        if (level.getGameTime() % 5 != entity.tickOffset) return;
        ServerLevel serverLevel = (ServerLevel) level;
        for (Direction dir : Direction.values()) {
            PipeConnection attachment = entity.faces.get(dir);
            if (attachment != PipeConnection.EXTRACTOR) continue;
            tickExtractor(dir, attachment, serverLevel, pos, serverLevel);
        }
    }
    private static void tickExtractor(Direction dir, PipeConnection attachment, ServerLevel serverLevel, BlockPos worldPosition, ServerLevel level) {
        BlockPos sourcePos = worldPosition.relative(dir);
        IItemHandler source = serverLevel.getCapability(Capabilities.ItemHandler.BLOCK, sourcePos, dir.getOpposite());
        if (source == null) return;
        PipeNetwork network = PipeNetworkManager.get(serverLevel).getNetwork(worldPosition);
        if (network == null) return;
        List<InserterTarget> validInserters = ItemPipeTransfer.getValidInserters(network, serverLevel);
        if (validInserters.isEmpty()) return;
        int throughput = 8;
        Map<InserterTarget, Integer> allocations = ItemPipeTransfer.distribute(throughput, validInserters, source);
        if (allocations.isEmpty()) return;
        Map<InserterTarget, List<ItemStack>> simulatedInsertions = ItemPipeTransfer.simulate(allocations, source);
        int totalExtraction = simulatedInsertions.values().stream().flatMap(List::stream).mapToInt(ItemStack::getCount).sum();
        if (totalExtraction == 0) return;
        List<ItemStack> extracted = ItemPipeTransfer.extract(source, totalExtraction);
        ItemPipeTransfer.insert(simulatedInsertions, extracted, level, source);
    }
}
