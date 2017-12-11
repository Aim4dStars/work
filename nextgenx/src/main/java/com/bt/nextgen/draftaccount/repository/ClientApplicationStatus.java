package com.bt.nextgen.draftaccount.repository;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Nonnull;

/**
 * Client application status, mostly for nextgen internals. See also
 * {@link com.bt.nextgen.core.repository.OnboardingApplicationStatus}. This enum must be 100% contained within
 * OnboardingApplicationStatus. We don't / won't / can't subclass an enum.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Liskov_substitution_principle">Liskov Substitution Principle</a>
 */
public enum ClientApplicationStatus {
    /**
     * See {@link com.bt.nextgen.draftaccount.repository.ClientApplication} for why these for now must match
     * the names in the database.
     */
    draft("draft"),
    deleted("deleted"),
    processing("processing"),
    active("active"),
    docuploaded("docuploaded");

    private String status;

    private ClientApplicationStatus(final String status) {
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
     * See {@link com.bt.nextgen.draftaccount.repository.ClientApplication} for why these are lowercase.
     */
    public static ClientApplicationStatus jsonConvert(@Nonnull String jsonNodeString) {
        return valueOf(jsonNodeString.trim().toLowerCase());
    }
}
