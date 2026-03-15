package com.minecraftmod.meeptech.helpers;

import java.util.Locale;

public class Formatting {
    public static String doubleFormatting(double value) {
        int sigFigs = 3;
        int maxDecimals = 2;
        if (value == 0) {
            return String.format(Locale.US, "%." + Math.min(Math.max(0, sigFigs - 1), maxDecimals) + "f", value);
        }
        int digitsBeforeDecimal = (int)Math.floor(Math.log10(Math.abs(value))) + 1;
        int decimalPlaces = Math.min(Math.max(0, sigFigs - digitsBeforeDecimal), maxDecimals);
        return String.format(Locale.US, "%." + decimalPlaces + "f", value);
    }
}
