package com.minecraftmod.meeptech.blocks.pipes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemPipeTransfer {
    public static List<InserterTarget> getValidInserters(PipeNetwork network, ServerLevel level) {
        List<InserterTarget> valid = new ArrayList<>();
        for (PipeNetwork.FacePos inserterFace : network.getInserters()) {
            BlockPos inserterPos = inserterFace.pos();
            Direction inserterDir = inserterFace.face();
            if (!level.isLoaded(inserterPos)) continue;
            BlockPos destPos = inserterPos.relative(inserterDir);
            IItemHandler dest = level.getCapability(Capabilities.ItemHandler.BLOCK, destPos, inserterDir.getOpposite());
            if (dest == null) continue;
            if (!hasRoom(dest)) continue;
            valid.add(new InserterTarget(inserterPos, inserterDir, dest));
        }
        return valid;
    }
    private static boolean hasRoom(IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) return true;
            if (stack.getCount() < stack.getMaxStackSize()) return true;
        }
        return false;
    }
    public static Map<InserterTarget, Integer> distribute(int throughput, List<InserterTarget> inserters, IItemHandler source) {
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
    private static int simulateInserterCapacity(IItemHandler dest, IItemHandler source, int max) {
        int canTake = 0;
        for (int i = 0; i < source.getSlots() && canTake < max; i++) {
            ItemStack extractable = source.extractItem(i, max - canTake, true);
            if (extractable.isEmpty()) continue;
            ItemStack remainder = simulateInsert(dest, extractable);
            canTake += extractable.getCount() - remainder.getCount();
        }
        return canTake;
    }
    private static ItemStack simulateInsert(IItemHandler dest, ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (int i = 0; i < dest.getSlots() && !remaining.isEmpty(); i++) {
            remaining = dest.insertItem(i, remaining, true);
        }
        return remaining;
    }
    public static Map<InserterTarget, List<ItemStack>> simulate(Map<InserterTarget, Integer> allocations, IItemHandler source) {
        Map<InserterTarget, List<ItemStack>> result = new LinkedHashMap<>();
        int[] promised = new int[source.getSlots()];
        for (Map.Entry<InserterTarget, Integer> entry : allocations.entrySet()) {
            InserterTarget inserter = entry.getKey();
            int amount = entry.getValue();
            if (amount == 0) continue;
            List<ItemStack> toInsert = new ArrayList<>();
            int remaining = amount;
            for (int i = 0; i < source.getSlots() && remaining > 0; i++) {
                ItemStack extractable = source.extractItem(i, remaining, true);
                if (extractable.isEmpty()) continue;
                int available = extractable.getCount() - promised[i];
                if (available <= 0) continue;
                int take = Math.min(available, remaining);
                toInsert.add(extractable.copyWithCount(take));
                promised[i] += take;
                remaining -= take;
            }
            if (!toInsert.isEmpty()) {
                result.put(inserter, toInsert);
            }
        }
        return result;
    }
    public static List<ItemStack> extract(IItemHandler source, int total) {
        List<ItemStack> extracted = new ArrayList<>();
        int remaining = total;

        for (int i = 0; i < source.getSlots() && remaining > 0; i++) {
            ItemStack stack = source.extractItem(i, remaining, false);
            if (stack.isEmpty()) continue;
            extracted.add(stack);
            remaining -= stack.getCount();
        }

        return extracted;
    }
    public static void insert(Map<InserterTarget, List<ItemStack>> insertions, List<ItemStack> extracted, ServerLevel level, IItemHandler source) {
        List<ItemStack> pool = new ArrayList<>(extracted);
        for (Map.Entry<InserterTarget, List<ItemStack>> entry : insertions.entrySet()) {
            IItemHandler dest = entry.getKey().handler();
            List<ItemStack> toInsert = entry.getValue();
            for (ItemStack stack : toInsert) {
                int needed = stack.getCount();
                for (ItemStack poolStack : pool) {
                    if (needed <= 0) break;
                    if (!ItemStack.isSameItemSameComponents(poolStack, stack)) continue;
                    int take = Math.min(poolStack.getCount(), needed);
                    ItemStack inserting = poolStack.copyWithCount(take);
                    for (int i = 0; i < dest.getSlots() && !inserting.isEmpty(); i++) {
                        inserting = dest.insertItem(i, inserting, false);
                    }
                    poolStack.shrink(take - inserting.getCount());
                    needed -= take - inserting.getCount();
                }
            }
        }
        //Try to return uninserted items back to the source.
        for (ItemStack remaining : pool) {
            if (remaining.isEmpty()) continue;
            for (int i = 0; i < source.getSlots() && !remaining.isEmpty(); i++) {
                remaining = source.insertItem(i, remaining, false);
            }
        }

    }
    public record InserterTarget(BlockPos pos, Direction face, IItemHandler handler) {}
}