package com.minecraftmod.meeptech.ui;

import com.minecraftmod.meeptech.ModBlocks;
import com.minecraftmod.meeptech.ModItems;
import com.minecraftmod.meeptech.ModMenus;
import com.minecraftmod.meeptech.logic.material.MaterialWorkstationRecipe;
import com.minecraftmod.meeptech.logic.material.MaterialWorkstationRecipes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class MaterialWorkstationMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public MaterialWorkstationMenu(int windowId, Inventory playerInv) {
        this(windowId, playerInv, new ItemStackHandler(3), ContainerLevelAccess.NULL);
    }
    public MaterialWorkstationMenu(int windowId, Inventory playerInv, IItemHandler handler, ContainerLevelAccess access) {
        super(ModMenus.MATERIAL_WORKSTATION_MENU.get(), windowId);
        this.access = access;

        this.addSlot(new SlotItemHandler(handler, 0, 16, 21));
        this.addSlot(new SlotItemHandler(handler, 1, 16, 51) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.HAMMER.get());
            }
        });
        this.addSlot(new SlotItemHandler(handler, 2, 145, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        //Add inventory/hotbar.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        Slot inputSlot = this.getSlot(0);
        Slot hammerSlot = this.getSlot(1);
        Slot outputSlot = this.getSlot(2);

        ItemStack input = inputSlot.getItem();
        MaterialWorkstationRecipes recipes = MaterialWorkstationRecipes.getAvailableForms(input);

        boolean craftAll = buttonId >= 1000;
        buttonId = craftAll ? buttonId - 1000 : buttonId;
        
        if (buttonId >= 0 && buttonId < recipes.getRecipes().size()) {
            MaterialWorkstationRecipe recipe = recipes.getRecipes().get(buttonId);
            ItemStack hammer = hammerSlot.getItem();
            if (hammer.is(ModItems.HAMMER.get()) && input.getCount() >= recipe.getInputAmount()) {
                ItemStack currentOutput = outputSlot.getItem();
                ItemStack recipeResult = new ItemStack(recipes.getMaterial().getForm(recipe.getOutputForm()), recipe.getOutputAmount());
                if (currentOutput.isEmpty() || 
                    (ItemStack.isSameItemSameComponents(currentOutput, recipeResult) 
                    && currentOutput.getCount() + recipeResult.getCount() <= currentOutput.getMaxStackSize())) {
                    
                    if (craftAll) {
                        int hammerHealth = hammer.getMaxDamage() - hammer.getDamageValue();
                        int inputCrafts = input.getCount() / recipe.getInputAmount();
                        int outputCrafts = currentOutput.isEmpty() 
                            ? (recipeResult.getMaxStackSize() / recipe.getOutputAmount()) 
                            : (currentOutput.getMaxStackSize() - currentOutput.getCount()) / recipe.getOutputAmount();
                        int crafts = Math.min(Math.min(hammerHealth, inputCrafts), outputCrafts);

                        inputSlot.remove(crafts * recipe.getInputAmount());
                        if (currentOutput.isEmpty()) outputSlot.set(new ItemStack(recipeResult.getItem(), 
                            currentOutput.getCount() + recipeResult.getCount() * crafts));
                        else currentOutput.grow(recipeResult.getCount() * crafts);
                        if (player.level() instanceof ServerLevel serverLevel) {
                            hammer.hurtAndBreak(crafts, serverLevel, null, (item) -> {});
                            if (hammer.getDamageValue() >= hammer.getMaxDamage()) {
                                hammerSlot.set(ItemStack.EMPTY);
                            }
                            serverLevel.playSound(null, player.blockPosition(), SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 0.5F, 1.5F);
                            serverLevel.playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.5F, 1.5F);
                        }
                    } else {
                        inputSlot.remove(recipe.getInputAmount());
                        if (currentOutput.isEmpty()) outputSlot.set(recipeResult);
                        else currentOutput.grow(recipeResult.getCount());
                        if (player.level() instanceof ServerLevel serverLevel) {
                            hammer.hurtAndBreak(1, serverLevel, null, (item) -> {});
                            if (hammer.getDamageValue() >= hammer.getMaxDamage()) {
                                hammerSlot.set(ItemStack.EMPTY);
                            }
                            serverLevel.playSound(null, player.blockPosition(), SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 0.5F, 1.5F);
                        }
                    }
                }
            }
        }
        return false;
    }
    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.MATERIAL_WORKSTATION.get());
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            //Slot is from within the material workstation.
            if (index < 3) {
                if (!this.moveItemStackTo(stackInSlot, 3, 39, true)) return ItemStack.EMPTY;
            } else {
                if (stackInSlot.is(ModItems.HAMMER.get())) {
                    if (!this.moveItemStackTo(stackInSlot, 1, 2, false)) return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (stackInSlot.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
            if (stackInSlot.getCount() == itemStack.getCount()) return ItemStack.EMPTY;
            slot.onTake(player, stackInSlot);
        }
        return itemStack;
    }
}
