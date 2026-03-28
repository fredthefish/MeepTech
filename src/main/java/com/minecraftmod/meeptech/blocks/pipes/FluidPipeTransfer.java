package com.minecraftmod.meeptech.blocks.pipes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.minecraftmod.meeptech.blocks.IFluidTankBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidPipeTransfer {
    public static List<InserterTarget> getValidInserters(PipeNetwork network, ServerLevel level) {
        List<InserterTarget> valid = new ArrayList<>();
        for (PipeNetwork.FacePos inserterFace : network.getInserters()) {
            BlockPos inserterPos = inserterFace.pos();
            Direction inserterDir = inserterFace.face();
            if (!level.isLoaded(inserterPos)) continue;
            BlockPos destPos = inserterPos.relative(inserterDir);
            if (level.getBlockEntity(destPos) instanceof IFluidTankBlockEntity tankEntity) {
                IFluidHandler dest = tankEntity.getAutomationFluidHandler();
                if (dest == null) continue;
                if (!hasRoom(dest)) continue;
                valid.add(new InserterTarget(inserterPos, inserterDir, dest));
            }
        }
        return valid;
    }
    private static boolean hasRoom(IFluidHandler handler) {
        for (int i = 0; i < handler.getTanks(); i++) {
            FluidStack stack = handler.getFluidInTank(i);
            int accepted = handler.fill(stack.copyWithAmount(1), IFluidHandler.FluidAction.SIMULATE);
            if (accepted > 0) return true;
        }
        for (int i = 0; i < handler.getTanks(); i++) {
            if (handler.getFluidInTank(i).isEmpty() && handler.getTankCapacity(i) > 0) return true;
        }
        return false;
    }
    public static Map<InserterTarget, Integer> distribute(int throughput, List<InserterTarget> inserters, IFluidHandler source) {
        Map<InserterTarget, Integer> allocations = new LinkedHashMap<>();
        for (InserterTarget inserter : inserters) {
            allocations.put(inserter, 0);
        }
        int remaining = throughput;
        List<InserterTarget> eligible = new ArrayList<>(inserters);
        while (remaining > 0 && !eligible.isEmpty()) {
            int share = remaining / eligible.size();
            int leftover = remaining % eligible.size();
            if (share == 0 && leftover == 0) break;
            List<InserterTarget> stillEligible = new ArrayList<>();
            int unallocated = 0;
            for (int i = 0; i < eligible.size(); i++) {
                InserterTarget inserter = eligible.get(i);
                int allocated = share + (i < leftover ? 1 : 0);
                int canTake = simulateInserterCapacity(inserter.handler(), source, allocated);
                allocations.merge(inserter, canTake, Integer::sum);
                if (canTake < allocated) {
                    unallocated += allocated - canTake;
                } else {
                    stillEligible.add(inserter);
                }
            }
            remaining = unallocated;
            eligible = stillEligible;
        }
        return allocations;
    }
    private static int simulateInserterCapacity(IFluidHandler dest, IFluidHandler source, int max) {
        int canTake = 0;
        for (int i = 0; i < source.getTanks() && canTake < max; i++) {
            FluidStack inTank = source.getFluidInTank(i);
            if (inTank.isEmpty()) continue;
            int toTry = Math.min(inTank.getAmount(), max - canTake);
            FluidStack drainable = source.drain(inTank.copyWithAmount(toTry), IFluidHandler.FluidAction.SIMULATE);
            if (drainable.isEmpty()) continue;
            int accepted = dest.fill(drainable, IFluidHandler.FluidAction.SIMULATE);
            canTake += accepted;
        }
        return canTake;
    }
    public static Map<InserterTarget, List<FluidStack>> simulate(Map<InserterTarget, Integer> allocations, IFluidHandler source) {
        Map<InserterTarget, List<FluidStack>> result = new LinkedHashMap<>();
        Map<FluidStack, Integer> drainable = new LinkedHashMap<>(); //List of drainable slots and the remaining amount for each slot.
        for (int i = 0; i < source.getTanks(); i++) {
            FluidStack inTank = source.getFluidInTank(i);
            if (inTank.isEmpty()) continue;
            FluidStack test = source.drain(inTank.copyWithAmount(1), IFluidHandler.FluidAction.SIMULATE);
            if (!test.isEmpty()) drainable.put(inTank, inTank.getAmount());
        }
        for (Map.Entry<InserterTarget, Integer> entry : allocations.entrySet()) {
            InserterTarget inserter = entry.getKey();
            int amount = entry.getValue();
            if (amount == 0) continue;
            List<FluidStack> toInsert = new ArrayList<>();
            int remaining = amount;
            for (FluidStack slot : drainable.keySet()) {
                if (remaining <= 0) break;
                if (drainable.get(slot) <= 0) continue;
                int take = Math.min(drainable.get(slot), remaining);
                toInsert.add(slot.copyWithAmount(take));
                drainable.put(slot, drainable.get(slot) - take);
                remaining -= take;
            }
            if (!toInsert.isEmpty()) {
                result.put(inserter, toInsert);
            }
        }
        return result;
    }
    public static List<FluidStack> extract(IFluidHandler source, int total) {
        List<FluidStack> extracted = new ArrayList<>();
        int remaining = total;
        for (int i = 0; i < source.getTanks() && remaining > 0; i++) {
            FluidStack inTank = source.getFluidInTank(i);
            if (inTank.isEmpty()) continue;
            FluidStack drained = source.drain(inTank.copyWithAmount(Math.min(inTank.getAmount(), remaining)), FluidAction.EXECUTE);
            if (drained.isEmpty()) continue;
            extracted.add(drained);
            remaining -= drained.getAmount();
        }
        return extracted;
    }
    public static void insert(Map<InserterTarget, List<FluidStack>> insertions, List<FluidStack> extracted, ServerLevel level, IFluidHandler source) {
        List<FluidStack> pool = new ArrayList<>(extracted);
        for (Map.Entry<InserterTarget, List<FluidStack>> entry : insertions.entrySet()) {
            IFluidHandler dest = entry.getKey().handler();
            List<FluidStack> toInsert = entry.getValue();
            for (FluidStack stack : toInsert) {
                int needed = stack.getAmount();
                for (FluidStack poolStack : pool) {
                    if (needed <= 0) break;
                    if (!FluidStack.isSameFluidSameComponents(poolStack, stack)) continue;
                    int take = Math.min(poolStack.getAmount(), needed);
                    int filled = dest.fill(poolStack.copyWithAmount(take), IFluidHandler.FluidAction.EXECUTE);
                    poolStack.shrink(filled);
                    needed -= filled;
                }
            }
        }
        //Try to return uninserted items back to the source.
        for (FluidStack remaining : pool) {
            if (remaining.isEmpty()) continue;
            source.fill(remaining, IFluidHandler.FluidAction.EXECUTE);
        }
    }
    public record InserterTarget(BlockPos pos, Direction face, IFluidHandler handler) {}
}