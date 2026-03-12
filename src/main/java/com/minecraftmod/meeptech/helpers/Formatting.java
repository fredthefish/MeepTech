package com.minecraftmod.meeptech.helpers;

import java.util.Locale;

public class Formatting {
    public static String doubleSigFigFormat(double value, int sigFigs, int maxDecimals) {
        if (value == 0) {
            return String.format(Locale.US, "%." + Math.min(Math.max(0, sigFigs - 1), maxDecimals) + "f", value);
        }
        int digitsBeforeDecimal = (int)Math.floor(Math.log10(Math.abs(value))) + 1;
        int decimalPlaces = Math.min(Math.max(0, sigFigs - digitsBeforeDecimal), maxDecimals);
        return String.format(Locale.US, "%." + decimalPlaces + "f", value);
    }
}
