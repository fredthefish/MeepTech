package com.minecraftmod.meeptech.blocks.pipes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class PipeNetwork {
    private final UUID id;
    private final Set<BlockPos> pipes = new HashSet<>();
    private final Set<FacePos> extractors = new HashSet<>();
    private final Set<FacePos> inserters = new HashSet<>();
    private int topologyVersion = 0;

    public record FacePos(BlockPos pos, Direction face) {}

    public PipeNetwork(UUID id) { 
        this.id = id; 
    }
    public UUID getId() { 
        return id; 
    }
    public Set<BlockPos> getPipes() { 
        return pipes;
    }
    public Set<FacePos> getExtractors() { 
        return extractors; 
    }
    public Set<FacePos> getInserters() { 
        return inserters; 
    }
    public int getTopologyVersion() { 
        return topologyVersion; 
    }
    public void incrementTopologyVersion() { 
        topologyVersion++; 
    }
    public void addPipe(BlockPos pos) { 
        pipes.add(pos); 
    }
    public void removePipe(BlockPos pos) {
        pipes.remove(pos);
        extractors.removeIf(f -> f.pos().equals(pos));
        inserters.removeIf(f -> f.pos().equals(pos));
    }
    public void addExtractor(BlockPos pos, Direction face) {
        extractors.add(new FacePos(pos, face));
    }
    public void removeExtractor(BlockPos pos, Direction face) {
        extractors.remove(new FacePos(pos, face));
    }
    public void addInserter(BlockPos pos, Direction face) {
        inserters.add(new FacePos(pos, face));
    }
    public void removeInserter(BlockPos pos, Direction face) {
        inserters.remove(new FacePos(pos, face));
    }
}