package Recycling.RecyclingData;

import java.text.DecimalFormat;

/**
 * Created by JHarder on 12/1/15.
 */
public final class RecHelper {
    /* Double formatting helper function */
    public static String formatDoubleAmount(double amount, int sigFigs) {
        String formatString = "0";
        if (sigFigs > 0) {
            formatString = formatString + ".";
            for (int i = 0; i < sigFigs; i++)
                formatString = formatString + "0";
        }
        DecimalFormat myFormat = new DecimalFormat(formatString);
        String myDoubleString = myFormat.format(amount);
        return myDoubleString;
    }


    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
