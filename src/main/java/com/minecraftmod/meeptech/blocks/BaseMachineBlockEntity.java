package com.minecraftmod.meeptech.blocks;

import java.util.HashMap;
import java.util.List;

import com.minecraftmod.meeptech.ModBlockEntities;
import com.minecraftmod.meeptech.items.MachineConfigData;
import com.minecraftmod.meeptech.logic.machine.EnergySourceType;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipe;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeType;
import com.minecraftmod.meeptech.logic.ui.SlotType;
import com.minecraftmod.meeptech.logic.ui.SlotUIElement;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;
import com.minecraftmod.meeptech.logic.ui.UIModuleType;
import com.minecraftmod.meeptech.network.MachineContainerData;
import com.minecraftmod.meeptech.ui.MachineMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BaseMachineBlockEntity extends BlockEntity implements MenuProvider {
    private MachineConfigData machineConfigData;
    private MachineData machineData = null;
    private final ItemStackHandler inventory = new ItemStackHandler() {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    protected final HashMap<TrackedStat, Integer> machineInts = new HashMap<>();
    protected final MachineContainerData trackedData = new MachineContainerData(machineInts, this);
    private final MachineAutomationHandler automationHandler = new MachineAutomationHandler(this);
    private MachineRecipe currentRecipe = null;

    public BaseMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASE_MACHINE_BE.get(), pos, state);
    }
    @Override
    public Component getDisplayName() {
        return Component.translatable(machineData.getBase().getTranslationKey()).append(" ")
            .append(Component.translatable(machineData.getType().getTranslationKey()));
    }
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        return new MachineMenu(windowId, playerInv, this, ContainerLevelAccess.create(this.level, this.worldPosition));
    }
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", this.inventory.serializeNBT(registries));
        if (this.machineConfigData != null) {
            MachineConfigData.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), machineConfigData)
                .ifSuccess(encoded -> tag.put("MachineConfig", encoded));
        }
        CompoundTag intsTag = new CompoundTag();
        for (TrackedStat stat : machineInts.keySet()) {
            if (stat != null) {
                intsTag.putInt(stat.toString(), machineInts.get(stat));
            }
        }
        tag.put("MachineInts", intsTag);
        if (currentRecipe != null) {
            tag.putString("MachineRecipe", currentRecipe.getId());
        }
    }
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        if (tag.contains("MachineConfig")) {
            MachineConfigData.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("MachineConfig"))
                .ifSuccess(parsed -> {
                    this.setConfigData(parsed);
                });
        }
        if (tag.contains("MachineInts")) {
            CompoundTag intsTag = tag.getCompound("MachineInts");
            for (String key : intsTag.getAllKeys()) {
                this.machineInts.put(TrackedStat.valueOf(key), intsTag.getInt(key));
            }
        }
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag, registries);
        return compoundTag;
    }
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    public MachineConfigData getConfigData() {
        return machineConfigData;
    }
    public void setConfigData(MachineConfigData machineConfigData) {
        if (!machineConfigData.isEmpty()) {
            this.machineConfigData = machineConfigData;
            setChanged();
            setMachineData(machineConfigData);
        }
    }
    public void setMachineData(MachineConfigData machineConfigData) {
        if (!machineConfigData.isEmpty()) {
            this.machineData = machineConfigData.toMachineData();
            if (machineData != null) {
                int slotCount = machineData.getSlotCount();
                if (slotCount != inventory.getSlots()) {
                    inventory.setSize(slotCount);
                }
                setChanged();
            }
            if (this.level != null && this.level.isClientSide()) {
                this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }
    public MachineData getMachineData() {
        if (machineData == null) {
            if (getConfigData() != null) {
                this.machineData = machineConfigData.toMachineData();
            }
        }
        return machineData;
    }
    public ItemStackHandler getInventory() {
        return inventory;
    }
    public void setMachineInt(TrackedStat key, int value) {
        trackedData.setFromStat(key, value);
    }
    public Integer getMachineInt(TrackedStat key) {
        return trackedData.getFromStat(key);
    }
    public MachineContainerData getTrackedData() {
        return trackedData;
    }
    public static void tick(Level level, BlockPos pos, BlockState state, BaseMachineBlockEntity entity) {
        if (level.isClientSide()) {
            if (entity.getMachineInt(TrackedStat.HeatLeft) > 0) {
                if (level.random.nextDouble() < 0.1) {
                    level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1, 1, false);
                }
            }
        } else {
            MachineData data = entity.getMachineData();
            if (data == null) return;
            boolean updated = false;
            if (data.getType().getEnergySource() == EnergySourceType.Heat) {
                int progress = entity.getMachineInt(TrackedStat.RecipeProgress);
                int maxProgress = entity.getMachineInt(TrackedStat.RecipeMaxProgress);
                int heat = entity.getMachineInt(TrackedStat.HeatLeft);
                int fuelSlot = data.getStartSlot(UIModuleType.Energy);
                if (heat > 0) {
                    heat--;
                    updated = true;
                }
                ItemStack fuelStack = entity.inventory.getStackInSlot(fuelSlot);
                if (heat == 0 && !fuelStack.isEmpty()) {
                    //TODO: MAKE IT ANOTHER RECIPE TYPE.
                    if (fuelStack.getItem() == Items.COAL) {
                        entity.inventory.extractItem(fuelSlot, 1, false);
                        heat += 1600;
                        updated = true;
                    }
                }
                int inputSlot = data.getStartSlot(UIModuleType.Input);
                MachineRecipeType recipeType = data.getType().getRecipeType();
                if (recipeType != null) {
                    ItemStack input = entity.getInventory().getStackInSlot(inputSlot);
                    int outputSlot = data.getStartSlot(UIModuleType.Output);
                    if (maxProgress == 0 && recipeType.validInput(input)) {
                        MachineRecipe recipe = recipeType.getRecipe(List.of(input));
                        ItemStack output = entity.inventory.getStackInSlot(outputSlot);
                        ItemStack recipeOutput = recipe.getOutputItems().getFirst();
                        int inputCount = recipe.inputsForConsumption(List.of(input)).get(input.getItem());
                        if (output.isEmpty() 
                        || (output.getItem().equals(recipeOutput.getItem()) && output.getCount() <= output.getMaxStackSize() + recipeOutput.getCount())) {
                            entity.inventory.extractItem(inputSlot, inputCount, false);
                            entity.setCurrentRecipe(recipe);
                            maxProgress = recipe.getTime();
                            updated = true;
                        }
                    }
                    if (maxProgress > 0) {
                        if (heat > 0) {
                            progress++;
                            updated = true;
                        } else if (progress > 0) {
                            progress -= 10;
                            if (progress < 0) progress = 0;
                            updated = true;
                        }
                    }
                    if (progress >= maxProgress && maxProgress > 0) {
                        MachineRecipe recipe = entity.getCurrentRecipe();
                        ItemStack recipeOutput = recipe.getOutputItems().getFirst();
                        entity.inventory.insertItem(outputSlot, recipeOutput, false);
                        progress = 0;
                        maxProgress = 0;
                        entity.setCurrentRecipe(null);
                        updated = true;
                    }
                }
                if (updated) {
                    entity.setMachineInt(TrackedStat.HeatLeft, heat);
                    entity.setMachineInt(TrackedStat.RecipeProgress, progress);
                    entity.setMachineInt(TrackedStat.RecipeMaxProgress, maxProgress);
                    entity.setChanged();
                }
            }
        }
    }
    public SlotType getSlotType(int slotIndex) {
        machineData = getMachineData();
        List<SlotUIElement> slots = machineData.getSlots();
        if (slots.size() > slotIndex) return slots.get(slotIndex).getType();
        return null;
    }
    public MachineAutomationHandler getAutomationHandler() {
        return this.automationHandler;
    }
    public MachineRecipe getCurrentRecipe() {
        return currentRecipe;
    }
    public void setCurrentRecipe(MachineRecipe recipe) {
        currentRecipe = recipe;
    }
}
