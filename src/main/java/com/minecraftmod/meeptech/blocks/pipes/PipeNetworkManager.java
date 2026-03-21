package com.minecraftmod.meeptech.blocks.pipes;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import com.minecraftmod.meeptech.MeepTech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

public class PipeNetworkManager extends SavedData {
    private static final String DATA_NAME = MeepTech.MODID + "_pipe_networks";

    private final Map<UUID, PipeNetwork> networks = new HashMap<>();
    private final Map<BlockPos, UUID> pipeToNetwork = new HashMap<>();

    public static PipeNetworkManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(PipeNetworkManager::new, PipeNetworkManager::load), DATA_NAME);
    }
    public PipeNetwork getNetwork(BlockPos pipePos) {
        UUID id = pipeToNetwork.get(pipePos);
        return id != null ? networks.get(id) : null;
    }
    public Map<UUID, PipeNetwork> getNetworks() {
        return networks;
    }
    public void onPipeAdded(BlockPos pos) {
        PipeNetwork network = new PipeNetwork(UUID.randomUUID());
        network.addPipe(pos);
        networks.put(network.getId(), network);
        pipeToNetwork.put(pos, network.getId());
        setDirty();
    }
    public void onPipeRemoved(BlockPos pos, BlockState oldState, ServerLevel level) {
        PipeNetwork network = getNetwork(pos);
        if (network == null) return;
        List<BlockPos> neighbors = getConnectedPipeNeighbors(pos, oldState);
        network.removePipe(pos);
        pipeToNetwork.remove(pos);
        if (network.getPipes().isEmpty()) {
            networks.remove(network.getId());
            setDirty();
            return;
        }
        handlePotentialSplit(network, neighbors, level);
        setDirty();
    }
    private List<BlockPos> getConnectedPipeNeighbors(BlockPos pos, BlockState state) {
        List<BlockPos> neighbors = new ArrayList<>();
        if (!(state.getBlock() instanceof PipeBlock)) return neighbors;
        for (Direction dir : Direction.values()) {
            if (state.getValue(PipeBlock.CONNECTIONS.get(dir)) != PipeConnection.PIPE) continue;
            neighbors.add(pos.relative(dir));
        }
        return neighbors;
    }
    public void onPipesConnected(BlockPos posA, BlockPos posB, ServerLevel level) {
        PipeNetwork netA = getNetwork(posA);
        PipeNetwork netB = getNetwork(posB);
        if (netA == null) {
            onPipeAdded(posA);
            netA = getNetwork(posA);
        }
        if (netB == null) {
            onPipeAdded(posB);
            netB = getNetwork(posB);
        }
        if (netA.getId().equals(netB.getId())) return;
        //Merge smaller into larger.
        PipeNetwork larger = netA.getPipes().size() >= netB.getPipes().size() ? netA : netB;
        PipeNetwork smaller = larger == netA ? netB : netA;
        for (BlockPos pipe : smaller.getPipes()) {
            larger.addPipe(pipe);
            pipeToNetwork.put(pipe, larger.getId());
        }
        for (PipeNetwork.FacePos extractor : smaller.getExtractors()) {
            larger.addExtractor(extractor.pos(), extractor.face());
        }
        for (PipeNetwork.FacePos inserter : smaller.getInserters()) {
            larger.addInserter(inserter.pos(), inserter.face());
        }
        networks.remove(smaller.getId());
        larger.incrementTopologyVersion();
        setDirty();
    }
    public void onPipesDisconnected(BlockPos posA, BlockPos posB, ServerLevel level) {
        PipeNetwork network = getNetwork(posA);
        if (network == null) return;
        List<BlockPos> neighborsOfA = getConnectedPipeNeighbors(posA, level).stream().filter(n -> !n.equals(posB)).toList();
        handlePotentialSplit(network, neighborsOfA, level);
        network.incrementTopologyVersion();
        setDirty();
    }
    public void onAttachmentAdded(BlockPos pos, Direction face, PipeConnection type, ServerLevel level) {
        PipeNetwork network = getNetwork(pos);
        if (network == null) return;
        if (type == PipeConnection.EXTRACTOR) {
            network.addExtractor(pos, face);
        } else if (type == PipeConnection.INSERTER) {
            network.addInserter(pos, face);
        }
        network.incrementTopologyVersion();
        setDirty();
    }
    public void onAttachmentRemoved(BlockPos pos, Direction face, PipeConnection type, ServerLevel level) {
        PipeNetwork network = getNetwork(pos);
        if (network == null) return;
        if (type == PipeConnection.EXTRACTOR) {
            network.removeExtractor(pos, face);
        } else if (type == PipeConnection.INSERTER) {
            network.removeInserter(pos, face);
        }
        network.incrementTopologyVersion();
        setDirty();
    }
    private void handlePotentialSplit(PipeNetwork network, List<BlockPos> neighbors, ServerLevel level) {
        if (neighbors.size() <= 1) {
            network.incrementTopologyVersion();
            return;
        }
        Set<BlockPos> reachable = bfs(neighbors.get(0), network, level);
        boolean allReachable = neighbors.stream().allMatch(reachable::contains);
        if (allReachable) {
            network.incrementTopologyVersion();
            return;
        }
        Set<BlockPos> assigned = new HashSet<>(reachable);
        List<Set<BlockPos>> subgroups = new ArrayList<>();
        subgroups.add(reachable);
        for (BlockPos neighbor : neighbors) {
            if (assigned.contains(neighbor)) continue;
            Set<BlockPos> subgroup = bfs(neighbor, network, level);
            subgroups.add(subgroup);
            assigned.addAll(subgroup);
        }
        Set<BlockPos> largest = subgroups.stream()
            .max(Comparator.comparingInt(Set::size))
            .orElseThrow();

        network.getPipes().retainAll(largest);
        network.getExtractors().removeIf(f -> !largest.contains(f.pos()));
        network.getInserters().removeIf(f -> !largest.contains(f.pos()));
        network.incrementTopologyVersion();
        for (Set<BlockPos> subgroup : subgroups) {
            if (subgroup == largest) continue;
            PipeNetwork newNetwork = new PipeNetwork(UUID.randomUUID());
            for (BlockPos pipe : subgroup) {
                newNetwork.addPipe(pipe);
                pipeToNetwork.put(pipe, newNetwork.getId());
                PipeBlockEntity be = (PipeBlockEntity) level.getBlockEntity(pipe);
                if (be == null) continue;
                for (Direction dir : Direction.values()) {
                    PipeConnection attachment = be.getFace(dir);
                    if (attachment == PipeConnection.EXTRACTOR) {
                        newNetwork.addExtractor(pipe, dir);
                    } else if (attachment == PipeConnection.INSERTER) {
                        newNetwork.addInserter(pipe, dir);
                    }
                }
            }
            networks.put(newNetwork.getId(), newNetwork);
        }
    }
    private Set<BlockPos> bfs(BlockPos start, PipeNetwork network, ServerLevel level) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!visited.add(current)) continue;
            if (!network.getPipes().contains(current)) continue;
            List<BlockPos> connectedNeighbors = getConnectedPipeNeighbors(current, level);
            for (BlockPos neighbor : connectedNeighbors) {
                if (!visited.contains(neighbor)) queue.add(neighbor);
            }
        }
        return visited;
    }
    private List<BlockPos> getConnectedPipeNeighbors(BlockPos pos, ServerLevel level) {
        List<BlockPos> neighbors = new ArrayList<>();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof PipeBlock)) return neighbors;
        for (Direction dir : Direction.values()) {
            if (state.getValue(PipeBlock.CONNECTIONS.get(dir)) != PipeConnection.PIPE) continue;
            neighbors.add(pos.relative(dir));
        }
        return neighbors;
    }
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag networkList = new ListTag();
        for (PipeNetwork network : networks.values()) {
            CompoundTag netTag = new CompoundTag();
            netTag.putUUID("id", network.getId());
            ListTag pipeList = new ListTag();
            for (BlockPos pos : network.getPipes()) {
                CompoundTag pipeTag = new CompoundTag();
                pipeTag.put("pos", NbtUtils.writeBlockPos(pos));
                pipeList.add(pipeTag);
            }
            netTag.put("pipes", pipeList);
            ListTag extractorList = new ListTag();
            for (PipeNetwork.FacePos fp : network.getExtractors()) {
                CompoundTag fpTag = new CompoundTag();
                fpTag.put("pos", NbtUtils.writeBlockPos(fp.pos()));
                fpTag.putString("face", fp.face().getSerializedName());
                extractorList.add(fpTag);
            }
            netTag.put("extractors", extractorList);
            ListTag inserterList = new ListTag();
            for (PipeNetwork.FacePos fp : network.getInserters()) {
                CompoundTag fpTag = new CompoundTag();
                fpTag.put("pos", NbtUtils.writeBlockPos(fp.pos()));
                fpTag.putString("face", fp.face().getSerializedName());
                inserterList.add(fpTag);
            }
            netTag.put("inserters", inserterList);
            networkList.add(netTag);
        }
        tag.put("networks", networkList);
        return tag;
    }
    public static PipeNetworkManager load(CompoundTag tag, HolderLookup.Provider registries) {
        PipeNetworkManager manager = new PipeNetworkManager();
        ListTag networkList = tag.getList("networks", Tag.TAG_COMPOUND);
        for (int i = 0; i < networkList.size(); i++) {
            CompoundTag netTag = networkList.getCompound(i);
            PipeNetwork network = new PipeNetwork(netTag.getUUID("id"));
            ListTag pipeList = netTag.getList("pipes", Tag.TAG_COMPOUND);
            for (int j = 0; j < pipeList.size(); j++) {
                BlockPos pos = NbtUtils.readBlockPos(pipeList.getCompound(j), "pos").get();
                network.addPipe(pos);
                manager.pipeToNetwork.put(pos, network.getId());
            }
            ListTag extractorList = netTag.getList("extractors", Tag.TAG_COMPOUND);
            for (int j = 0; j < extractorList.size(); j++) {
                CompoundTag fpTag = extractorList.getCompound(j);
                BlockPos pos = NbtUtils.readBlockPos(fpTag, "pos").get();
                Direction face = Direction.byName(fpTag.getString("face"));
                network.addExtractor(pos, face);
            }
            ListTag inserterList = netTag.getList("inserters", Tag.TAG_COMPOUND);
            for (int j = 0; j < inserterList.size(); j++) {
                CompoundTag fpTag = inserterList.getCompound(j);
                BlockPos pos = NbtUtils.readBlockPos(fpTag, "pos").get();
                Direction face = Direction.byName(fpTag.getString("face"));
                network.addInserter(pos, face);
            }
            manager.networks.put(network.getId(), network);
        }
        return manager;
    }
}