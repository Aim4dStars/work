package com.bt.nextgen.api.version.model;

public enum MobilePlatform {

    ANDROID("android"),
    IOS("ios"),
    WINDOWS("windows"),
    COMMON("common"),
    INVALID("invalid");

    private String platformName;

    MobilePlatform(String platform) {
        this.platformName = platform;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public static MobilePlatform forPlatform(String platformName) {
        for (MobilePlatform mobilePlatform : MobilePlatform.values()) {
            if (mobilePlatform.getPlatformName().equals(platformName)) {
                return mobilePlatform;
            }
        }
        return MobilePlatform.INVALID;
    }

}