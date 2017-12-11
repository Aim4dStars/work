package com.bt.nextgen.api.draftaccount.util;

public class ReferenceNumberFormatter {

    public static String formatReferenceNumber(Long clientApplicationId) {
        if(clientApplicationId == null){
            return "";
        }
        return String.format("R%09d", clientApplicationId);
    }
}
