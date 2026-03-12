package com.minecraftmod.meeptech.network;

import java.util.HashMap;
import java.util.List;

import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;

import net.minecraft.world.inventory.ContainerData;

public class MachineContainerData implements ContainerData {
    private HashMap<TrackedStat, Integer> machineInts;
    BaseMachineBlockEntity entity;

    public MachineContainerData(HashMap<TrackedStat, Integer> machineInts, BaseMachineBlockEntity entity) {
        this.machineInts = machineInts;
        this.entity = entity;
    }
    @Override
    public int get(int index) {
        TrackedStat key = getVariableKey(index);
        Integer result = getFromStat(key);
        return result;
    }
    @Override
    public void set(int index, int value) {
        TrackedStat key = getVariableKey(index);
        setFromStat(key, value);
    }
    @Override
    public int getCount() {
        MachineData machineData = entity.getMachineData();
        if (machineData != null) {
            return machineInts.size();
        }
        return 0;
    }
    private TrackedStat getVariableKey(int index) {
        MachineData machineData = entity.getMachineData();
        List<TrackedStat> trackedStats = machineData.getTrackedStats();
        if (index >= 0 && index < trackedStats.size()) {
            return trackedStats.get(index);
        }
        return null;
    }
    public int getFromStat(TrackedStat stat) {
        Integer result = machineInts.get(stat);
        if (result == null) {
            setFromStat(stat, 0);
            return 0;
        }
        return result;
    }
    public void setFromStat(TrackedStat stat, int value) {
        machineInts.put(stat, value);
    }
    public void setup() {
        MachineData machineData = entity.getMachineData();
        if (machineData != null) {
            for (TrackedStat stat : machineData.getTrackedStats()) {
                setFromStat(stat, 0);
            }
        }
    }
}
