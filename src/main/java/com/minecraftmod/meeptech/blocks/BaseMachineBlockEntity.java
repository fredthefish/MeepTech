package com.minecraftmod.meeptech.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.minecraftmod.meeptech.logic.machine.MachineConfigData;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipe;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeType;
import com.minecraftmod.meeptech.logic.recipe.ModMachineRecipes;
import com.minecraftmod.meeptech.logic.ui.SlotType;
import com.minecraftmod.meeptech.logic.ui.SlotUIElement;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;
import com.minecraftmod.meeptech.machines.CombinedFluidHandler;
import com.minecraftmod.meeptech.machines.MachineProcessor;
import com.minecraftmod.meeptech.machines.CombinedFluidHandler.FluidTankWithData;
import com.minecraftmod.meeptech.network.MachineContainerData;
import com.minecraftmod.meeptech.registries.ModBlockEntities;
import com.minecraftmod.meeptech.ui.MachineMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BaseMachineBlockEntity extends BlockEntity implements MenuProvider, IFluidTankBlockEntity {
    private MachineConfigData machineConfigData;
    private MachineData machineData = null;
    private final ItemStackHandler inventory;
    private final List<FluidTankWithData> tanks;
    protected final HashMap<TrackedStat, Integer> machineInts = new HashMap<>();
    protected final MachineContainerData trackedData = new MachineContainerData(machineInts, this);
    private final MachineAutomationHandler automationHandler = new MachineAutomationHandler(this);
    private final IFluidHandler fluidHandler;
    private final IFluidHandler automationFluidHandler;
    private MachineRecipe currentRecipe = null;
    private String currentVanillaRecipe = null;
    private final Block block;

    public BaseMachineBlockEntity(BlockPos pos, BlockState state, Block block) {
        super(getBlockEntityType(block), pos, state);
        this.block = block;
        this.inventory = new ItemStackHandler() {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        this.tanks = new ArrayList<>();
        this.fluidHandler = new CombinedFluidHandler(tanks, false);
        this.automationFluidHandler = new CombinedFluidHandler(tanks, true);
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
        ListTag tanksTag = new ListTag();
        for (FluidTankWithData tank : tanks) {
            tanksTag.add(tank.tank().writeToNBT(registries, new CompoundTag()));
        }
        tag.put("FluidTanks", tanksTag);
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
        if (tag.contains("FluidTanks")) {
            tanks.clear();
            ListTag tanksTag = tag.getList("FluidTanks", Tag.TAG_COMPOUND);
            MachineData data = getMachineData();
            for (int i = 0; i < tanksTag.size(); i++) {
                FluidTank tank = new FluidTank(data.getTankCapacity()) {
                    @Override
                    protected void onContentsChanged() {
                        setChanged();
                    }
                };
                tanks.add(new FluidTankWithData(tank.readFromNBT(registries, tanksTag.getCompound(i)), data.getFluidSlots().get(i).getType()));
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
                int slotCount = machineData.getItemSlotCount();
                if (slotCount != inventory.getSlots()) {
                    inventory.setSize(slotCount);
                }
                int tankCount = machineData.getTankSlotCount();
                if (tankCount != tanks.size()) {
                    tanks.clear();
                    for (int i = 0; i < tankCount; i++) 
                        tanks.add(new FluidTankWithData(new FluidTank(machineData.getTankCapacity()), machineData.getFluidSlots().get(i).getType()));
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
    public List<FluidTankWithData> getFluidTanks() {
        return tanks;
    }
    @Override
    public FluidTank getTank(int index) {
        return tanks.get(index).tank();
    }
    @Override
    public IFluidHandler getFluidHandler() {
        return fluidHandler;
    }
    @Override
    public IFluidHandler getAutomationFluidHandler() {
        return automationFluidHandler;
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
            MachineProcessor.serverTick(level, entity);
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
