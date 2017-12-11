package com.bt.nextgen.core.repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonValue;


public enum OnboardingPartyDisplayStatus {
    NOTIFICATION_SENT("Email successfully sent", OnboardingPartyStatus.NotificationSent),
    SERVER_FAILURE("ServerFailure", OnboardingPartyStatus.ServerFailure),
    ONLINE_ACCESS_REGISTRATION_FAILED("Failed online access registration", OnboardingPartyStatus.OnlineAccessRegistrationFailed),
    DEVICE_REGISTRATION_FAILED("Failed user device registration", OnboardingPartyStatus.DeviceRegistrationFailed),
    EXISTING_PANORAMA_ONLINE_USER("ExistingPanoramaOnlineUser", OnboardingPartyStatus.ExistingPanoramaOnlineUser),
    NOTIFICATION_SENT_TO_EXISTING_ONLINE_USER("Email successfully sent to existing online user", OnboardingPartyStatus.NotificationSentToExistingOnlineUser),
    FAILED_EMAIL("Failed Email", OnboardingPartyStatus.CustomerNotificationFailed),
    CUSTOMER_CREATION_PENDING("Client creation pending", null); // the 'default'.

    private @Nonnull String string;
    private @Nullable OnboardingPartyStatus onboardingPartyStatus;

    OnboardingPartyDisplayStatus(@Nonnull String string, @Nullable OnboardingPartyStatus onboardingPartyStatus) {
        this.string = string;
        this.onboardingPartyStatus = onboardingPartyStatus;
    }

    @Override
    public String toString() {
        return string;
    }

    @JsonValue
    public String getValue() {
        return toString();
    }

    /**
     * Given a @OnboardingPartyStatus, produce the equivalent @OnboardingPartyDisplayStatus for it.
     * It is guaranteed to exist.
     */
    public static @Nonnull OnboardingPartyDisplayStatus convertFromOnboardingPartyStatus(
            @Nullable OnboardingPartyStatus onboardingPartyStatus) {
        for (OnboardingPartyDisplayStatus onboardingPartyDisplayStatus : OnboardingPartyDisplayStatus.values()) {
            if (onboardingPartyStatus == onboardingPartyDisplayStatus.onboardingPartyStatus) {
                return onboardingPartyDisplayStatus;
            }
        }
        return CUSTOMER_CREATION_PENDING; // New corp SMSF users may not have had their status updated yet
    }
}
