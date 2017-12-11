package com.bt.nextgen.api.client.util;

public class GenderMapperUtil {

    public static String getGenderFromGCMGenderCode(String gender) {
        switch (gender) {
            case "M":
                return "male";
            case "F":
                return "female";
            case "U":
                return "other";
            default:
                throw new IllegalArgumentException("Gender mapping doesn't exist");
        }
    }
}
