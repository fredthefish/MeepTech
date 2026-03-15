package com.minecraftmod.meeptech.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.minecraftmod.meeptech.ModBlockEntities;
import com.minecraftmod.meeptech.ModMachineRecipes;
import com.minecraftmod.meeptech.items.MachineConfigData;
import com.minecraftmod.meeptech.logic.machine.EnergySourceType;
import com.minecraftmod.meeptech.logic.machine.HeatSource;
import com.minecraftmod.meeptech.logic.machine.MachineAttribute;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.recipe.MachineHeatRecipe;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipe;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeHeatType;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeStandardType;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeType;
import com.minecraftmod.meeptech.logic.recipe.MachineStandardRecipe;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.DeferredBlock;

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
    private String currentVanillaRecipe = null;
    private final Block block;

    public BaseMachineBlockEntity(BlockPos pos, BlockState state, Block block) {
        super(getBlockEntityType(block), pos, state);
        this.block = block;
    }
    private static BlockEntityType<BaseMachineBlockEntity> getBlockEntityType(Block block) {
        for (DeferredBlock<Block> deferredBlock : ModBlockEntities.HULL_BLOCK_ENTITIES.keySet()) {
            if (deferredBlock.get() == block) return ModBlockEntities.HULL_BLOCK_ENTITIES.get(deferredBlock).get();
        }
        return null;
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
            tag.putString("CurrentRecipeType", currentRecipe.getType().getId());
            tag.putString("CurrentRecipe", currentRecipe.getId());
        }
        if (currentVanillaRecipe != null) {
            tag.putString("CurrentVanillaRecipe", currentVanillaRecipe);
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
        if (tag.contains("CurrentRecipe")) {
            String recipeTypeId = tag.getString("CurrentRecipeType");
            MachineRecipeType recipeType = ModMachineRecipes.getRecipeType(recipeTypeId);
            String recipeId = tag.getString("CurrentRecipe");
            this.currentRecipe = recipeType.getRecipe(recipeId);
        }
        if (tag.contains("CurrentVanillaRecipe")) {
            currentVanillaRecipe = tag.getString("CurrentVanillaRecipe");
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
                } else if (heat == 0) {
                    MachineAttribute energyType = data.getEnergySource();
                    if (energyType instanceof HeatSource heatSource) {
                        MachineRecipeHeatType heatType = heatSource.getHeatType();
                        if (heatType == ModMachineRecipes.SOLID_FUEL) {
                            ItemStack fuelStack = entity.inventory.getStackInSlot(fuelSlot);
                            if (!fuelStack.isEmpty() && heatType.validInput(fuelStack)) {
                                MachineHeatRecipe recipe = heatType.getRecipe(fuelStack);
                                if (recipe != null) {
                                    entity.inventory.extractItem(fuelSlot, 1, false);
                                    heat += (int)((double)recipe.getHeat() / data.getMachineSpeed());
                                    updated = true;
                                } else if (fuelStack.getBurnTime(RecipeType.SMELTING) > 0) {
                                    heat += (int)((double)fuelStack.getBurnTime(RecipeType.SMELTING) / data.getMachineSpeed());
                                    entity.inventory.extractItem(fuelSlot, 1, false);
                                    updated = true;
                                }
                            }
                        }
                    }
                }
                int inputSlot = data.getStartSlot(UIModuleType.Input);
                MachineRecipeType recipeType = data.getType().getRecipeType();
                if (recipeType != null && recipeType instanceof MachineRecipeStandardType standardType) {
                    int outputSlot = data.getStartSlot(UIModuleType.Output);
                    if (heat > 0) {
                        ItemStack input = entity.getInventory().getStackInSlot(inputSlot);
                        if (maxProgress == 0 && standardType.validInput(input)) {
                            MachineStandardRecipe recipe = standardType.getRecipe(List.of(input));
                            ItemStack output = entity.inventory.getStackInSlot(outputSlot);
                            ItemStack recipeOutput = recipe.getOutputItems().getFirst();
                            int inputCount = recipe.inputsForConsumption(List.of(input)).get(input.getItem());
                            if (output.isEmpty() 
                            || (output.getItem().equals(recipeOutput.getItem()) && output.getCount() <= output.getMaxStackSize() + recipeOutput.getCount())) {
                                entity.inventory.extractItem(inputSlot, inputCount, false);
                                entity.setCurrentRecipe(recipe);
                                maxProgress = (int)((double)recipe.getTime() / data.getMachineSpeed());
                                updated = true;
                            }
                        } else if (maxProgress == 0 && standardType == ModMachineRecipes.SMELTER) {
                            SingleRecipeInput recipeInput = new SingleRecipeInput(input);
                            Optional<RecipeHolder<SmeltingRecipe>> furnaceRecipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, recipeInput, level);
                            if (furnaceRecipe.isPresent()) {
                                SmeltingRecipe recipe = furnaceRecipe.get().value();
                                ItemStack output = entity.inventory.getStackInSlot(outputSlot);
                                ItemStack recipeOutput = recipe.getResultItem(level.registryAccess());
                                if (output.isEmpty() 
                                || (output.getItem().equals(recipeOutput.getItem()) && output.getCount() <= output.getMaxStackSize() + recipeOutput.getCount())) {
                                    entity.inventory.extractItem(inputSlot, 1, false);
                                    entity.setCurrentVanillaRecipe(furnaceRecipe.get());
                                    maxProgress = (int)((double)recipe.getCookingTime() / data.getMachineSpeed());
                                    updated = true;
                                }
                            }
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
                        if (recipe != null) {
                            if (recipe instanceof MachineStandardRecipe standardRecipe) {
                                ItemStack recipeOutput = standardRecipe.getOutputItems().getFirst();
                                entity.inventory.insertItem(outputSlot, recipeOutput, false);
                                progress = 0;
                                maxProgress = 0;
                                entity.setCurrentRecipe(null);
                                updated = true;
                            }
                        } else {
                            String vanillaRecipeId = entity.getCurrentVanillaRecipe();
                            Optional<RecipeHolder<?>> vanillaRecipeHolder = level.getRecipeManager().byKey(ResourceLocation.parse(vanillaRecipeId));
                            if (vanillaRecipeHolder.isPresent()) {
                                Recipe<?> vanillaRecipe = vanillaRecipeHolder.get().value();
                                if (vanillaRecipe.getType() == RecipeType.SMELTING) {
                                    ItemStack recipeOutput = vanillaRecipe.getResultItem(level.registryAccess());
                                    entity.inventory.insertItem(outputSlot, recipeOutput, false);
                                    progress = 0;
                                    maxProgress = 0;
                                    entity.setCurrentVanillaRecipe(null);
                                    updated = true;
                                }
                            }
                        }
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
    public String getCurrentVanillaRecipe() {
        return currentVanillaRecipe;
    }
    public void setCurrentRecipe(MachineRecipe recipe) {
        currentRecipe = recipe;
    }
    public void setCurrentVanillaRecipe(RecipeHolder<?> recipe) {
        if (recipe == null) currentVanillaRecipe = null;
        else currentVanillaRecipe = recipe.id().getNamespace() + ":" + recipe.id().getPath();
    }
    public Block getBlock() {
        return block;
    }
}
