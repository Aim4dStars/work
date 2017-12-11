package com.bt.nextgen.api.userpreference.model;

/**
 * This enum contains the list of user preferences which may be stored in the user preference table
 */

public enum UserPreferenceEnum {
    DEFAULT_ROLE("defaultrole"),
    LAST_ACCESSED_ACCOUNTS("lastaccounts"),
    PUSH_PREFERENCES("pushpreferences"),
    LAST_ACCESSED_ACCOUNT("lastaccessedaccount"),
    FAVOURITE_INVESTMENTS("favouriteinvestments"),
    LAST_ACCESSED_FNUMBER("lastaccessedfnumber"),
    LAST_ACCESSED_ADVISER("lastaccessedadviser"),
    BENEFICIARY_REPORT_LAST_ACCESSED_ADVISER("beneficiary_lastaccessedadviser"),
    SUPER_CALCULATOR("supercalculator"),
    WRAP_ACCOUNT_ID("wrapaccountid"),// TODO: Remove once M# available in Avaloq
    UNKNOWN("unknown");

    private String preferenceKey;

    UserPreferenceEnum(String preferenceKey) {
        this.preferenceKey = preferenceKey;
    }

    public static UserPreferenceEnum fromString(String prefStr) {
        for (UserPreferenceEnum preference : UserPreferenceEnum.values()) {
            if (preference.getPreferenceKey().equalsIgnoreCase(prefStr)) {
                return preference;
            }
        }
        return UserPreferenceEnum.UNKNOWN;
    }

    public String getPreferenceKey() {
        return preferenceKey;
    }
}