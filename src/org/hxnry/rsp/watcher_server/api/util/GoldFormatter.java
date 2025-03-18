package org.hxnry.rsp.watcher_server.api.util;

import java.text.DecimalFormat;

public class GoldFormatter {

    private static final DecimalFormat df = new DecimalFormat("#.##");

    public static String gpFormatter(double num) {

        boolean isNegative = false;

        if(num < 0) {
            isNegative = true;
        }

        double number = (num < 0 ? Math.abs(num) : num);

        if (number >= 1000000000) {
            return (!isNegative ? df.format((number / 1000000000)) + "B" :  "-" + df.format(number / 1000000000) + "B");
        }
        if (number >= 1000000) {
            return (!isNegative ? df.format((number / 1000000)) + "M" : "-" + df.format(number / 1000000) + "M");
        }
        if (number >= 1000) {
            return (!isNegative ? df.format((number / 1000)) + "K" : "-" + df.format(number / 1000) + "K");
        }
        return (int) number + "gp";
    }

    public static int roundAmountToStake(int number) {
        return Math.round(number / 100000) * 100000;
    }

}

