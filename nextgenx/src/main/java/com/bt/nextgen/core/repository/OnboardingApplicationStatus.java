package com.bt.nextgen.core.repository;

import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Nonnull;

/**
 * Onboarding statuses returned for the Javascript client.
 * See also {@link com.bt.nextgen.draftaccount.repository.ClientApplicationStatus}.
 * That enum must be 100% contained within here.
 * We don't / won't / can't subclass an enum.
 * @see <a href="https://en.wikipedia.org/wiki/Liskov_substitution_principle">Liskov Substitution Principle</a>
 * See the TODO below for why we must have the enum name match what is stored in the db.
 */
public enum OnboardingApplicationStatus {
    // these ones are only converted by this.convertFromClientApplicationStatus()
    draft(ClientApplicationStatus.draft.toString()),
    deleted(ClientApplicationStatus.deleted.toString()),
    processing(ClientApplicationStatus.processing.toString()),
    active(ClientApplicationStatus.active.toString()),
    // Applicable only for offline applications
    offlineDocUpload("docUploaded"),
    //
    // TODO: use @Convert(converter=OnboardingApplicationStatusConverter.class) when we are using >j2ee 6.0 and >JPA 2.0
    // OnboardingApplicationStatusConverter implements javax.persistence.AttributeConverter<OnboardingApplicationStatus, String> {
    // There are two methods:
    // public String convertToDatabaseColumn(OnboardingApplicationStatus attribute){...}
    // public YourEnum convertToEntityAttribute(String dbString) {...}
    failed("failed"),
    failedEmail("failedEmail"),
    awaitingApproval("awaitingApproval"),
    awaitingApprovalOffline("awaitingApprovalOffline"),
    withdrawn("withdrawn"),
    //
    smsfinProgress("smsfinProgress"),
    smsfcorporateinProgress("smsfcorporateinProgress"),
    /**
     * These ESB statuses are transformed into other statuses in this enum.
     * Don't be tempted to subclass, that's not possible, and also doesn't make sense.
     * @see <a href="https://en.wikipedia.org/wiki/Liskov_substitution_principle">Liskov Substitution Principle</a>
     */
    ApplicationCreationFailed("ApplicationCreationFailed"),
    PartyCreationFailed("PartyCreationFailed"),
    ServerFailure("ServerFailure"),
    ApplicationCreationInProgress("ApplicationCreationInProgress"),
    PartyCreationInProgress("PartyCreationInProgress");

    private String status;

    private OnboardingApplicationStatus(final String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }

    @JsonValue
    final String value() {
        return toString();
    }

    /**
     * Given a @ClientApplicationStatus, produce the equivalent @OnboardingApplicationStatus for it.
     * It is guaranteed to exist.
     * See also {@link com.bt.nextgen.draftaccount.repository.ClientApplicationStatus}.
     */
    public static @Nonnull OnboardingApplicationStatus convertFromClientApplicationStatus(@Nonnull ClientApplicationStatus clientApplicationStatus) {
        return OnboardingApplicationStatus.valueOf(clientApplicationStatus.toString().trim());
    }

}
