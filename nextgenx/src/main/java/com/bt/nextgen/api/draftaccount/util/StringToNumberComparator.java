package com.bt.nextgen.api.draftaccount.util;

public class StringToNumberComparator {

    public static boolean compare(String first, String second) {
        return Long.parseLong(first) ==Long.parseLong(second);
    }
}
