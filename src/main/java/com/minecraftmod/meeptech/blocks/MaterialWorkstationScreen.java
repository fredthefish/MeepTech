package com.minecraftmod.meeptech.blocks;

import com.minecraftmod.meeptech.logic.MaterialWorkstationRecipe;
import com.minecraftmod.meeptech.logic.MaterialWorkstationRecipes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MaterialWorkstationScreen extends AbstractContainerScreen<MaterialWorkstationMenu> {
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/material_workstation.png");

    public MaterialWorkstationScreen(MaterialWorkstationMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 176, 166);

        ItemStack input = this.menu.getSlot(0).getItem();
        MaterialWorkstationRecipes recipes = MaterialWorkstationRecipes.getAvailableForms(input);
        if (recipes != null) {
            int startX = x + 42;
            int startY = y + 15;
    
            int columns = 5;
            int i = 0;
            for (MaterialWorkstationRecipe recipe : recipes.getRecipes()) {
                int buttonX = startX + (i % columns) * 18;
                int buttonY = startY + (i / columns) * 18;
                guiGraphics.renderItem(new ItemStack(recipes.getMaterial().getForm(recipe.getOutputForm())), buttonX, buttonY);
                i++;
            }
        }
    }
    @Override 
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        ItemStack input = this.menu.getSlot(0).getItem();
        MaterialWorkstationRecipes recipes = MaterialWorkstationRecipes.getAvailableForms(input);
        if (recipes != null) {
            int startX = x + 42;
            int startY = y + 15;
    
            int columns = 5;
            for (int i = 0; i < recipes.getRecipes().size(); i++) {
                int buttonX = startX + (i % columns) * 18;
                int buttonY = startY + (i / columns) * 18;
                if (mouseX >= buttonX && mouseX < buttonX + 18 && mouseY >= buttonY && mouseY < buttonY + 18) {
                    this.minecraft.gameMode.handleInventoryButtonClick((this.menu.containerId), i);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}