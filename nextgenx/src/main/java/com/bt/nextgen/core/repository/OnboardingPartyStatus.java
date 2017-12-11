package com.bt.nextgen.core.repository;

public enum OnboardingPartyStatus {
    //TODO: see OnboardingParty.java for why these are are CamelCase for now.
    // refactor as part of US21070: TCI – upgrade Server library versions J2EE to v7 and JPA to v2.1
    NotificationSent("NotificationSent"),
    ServerFailure("ServerFailure"),
    OnlineAccessRegistrationFailed("OnlineAccessRegistrationFailed"),
    DeviceRegistrationFailed("DeviceRegistrationFailed"),
    CustomerNotificationFailed("CustomerNotificationFailed"),
    ExistingPanoramaOnlineUser("ExistingPanoramaOnlineUser"),
    NotificationSentToExistingOnlineUser("NotificationSentToExistingOnlineUser");

    private String string;
    OnboardingPartyStatus(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

}
