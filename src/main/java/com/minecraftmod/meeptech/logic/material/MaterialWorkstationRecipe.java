package com.minecraftmod.meeptech.logic.material;

public class MaterialWorkstationRecipe {
    private final MaterialForm inputForm;
    private final MaterialForm outputForm;
    private final int inputAmount;
    private final int outputAmount;

    public MaterialWorkstationRecipe(MaterialForm inputForm, MaterialForm outputForm, int inputAmount, int outputAmount) {
        this.inputForm = inputForm;
        this.outputForm = outputForm;
        this.inputAmount = inputAmount;
        this.outputAmount = outputAmount;
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
}