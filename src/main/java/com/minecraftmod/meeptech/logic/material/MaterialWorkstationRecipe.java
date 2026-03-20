package com.minecraftmod.meeptech.logic.material;

public class MaterialWorkstationRecipe {
    private final MaterialForm inputForm;
    private final MaterialForm outputForm;
    private final int inputAmount;
    private final int outputAmount;
    private final int maxTier;

    public MaterialWorkstationRecipe(MaterialForm inputForm, MaterialForm outputForm, int inputAmount, int outputAmount, int maxMaterialTier) {
        this.inputForm = inputForm;
        this.outputForm = outputForm;
        this.inputAmount = inputAmount;
        this.outputAmount = outputAmount;
        this.maxTier = maxMaterialTier;
    }
    public MaterialForm getInputForm() {
        return inputForm;
    }
    public MaterialForm getOutputForm() {
        return outputForm;
    }
    public int getInputAmount() {
        return inputAmount;
    }
    public int getOutputAmount() {
        return outputAmount;
    }
    public int getMaxTier() {
        return maxTier;
    }
}