package com.bt.nextgen.api.draftaccount.util;

public class BsbFormatter {


    private static final int BSB_MIDPOINT = 3;
    public static String formatBsb(String bsb) {
        if (bsb != null && bsb.length()>BSB_MIDPOINT) {
            return String.format("%s-%s", bsb.substring(0, BSB_MIDPOINT), bsb.substring(BSB_MIDPOINT));
        }
        return null;
    }
}
