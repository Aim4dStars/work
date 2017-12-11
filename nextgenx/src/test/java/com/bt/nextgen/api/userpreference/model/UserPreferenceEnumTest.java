package com.bt.nextgen.api.userpreference.model;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Created by M035995 on 23/11/2016.
 */
public class UserPreferenceEnumTest {

    @Test
    public void testTotalUserPreferences() {
        assertThat("UserPreferences enum size", UserPreferenceEnum.values().length, is(equalTo(11)));
    }

    @Test
    public void testUserPreferenceKeys() {
        assertThat("UserPreference DEFAULT_ROLE ", UserPreferenceEnum.fromString("defaultrole"), is(equalTo(UserPreferenceEnum.DEFAULT_ROLE)));
        assertThat("UserPreference LAST_ACCESSED_ACCOUNTS ", UserPreferenceEnum.fromString("lastaccounts"), is(equalTo(UserPreferenceEnum.LAST_ACCESSED_ACCOUNTS)));
        assertThat("UserPreference PUSH_PREFERENCES ", UserPreferenceEnum.fromString("pushpreferences"), is(equalTo(UserPreferenceEnum.PUSH_PREFERENCES)));
        assertThat("UserPreference LAST_ACCESSED_ACCOUNT ", UserPreferenceEnum.fromString("lastaccessedaccount"), is(equalTo(UserPreferenceEnum.LAST_ACCESSED_ACCOUNT)));
        assertThat("UserPreference FAVOURITE_INVESTMENTS ", UserPreferenceEnum.fromString("favouriteinvestments"), is(equalTo(UserPreferenceEnum.FAVOURITE_INVESTMENTS)));
        assertThat("UserPreference LAST_ACCESSED_FNUMBER ", UserPreferenceEnum.fromString("lastaccessedfnumber"), is(equalTo(UserPreferenceEnum.LAST_ACCESSED_FNUMBER)));
        assertThat("UserPreference LAST_ACCESSED_ADVISER ", UserPreferenceEnum.fromString("lastaccessedadviser"), is(equalTo(UserPreferenceEnum.LAST_ACCESSED_ADVISER)));
        assertThat("UserPreference BENEFICIARY_REPORT_LAST_ACCESSED_ADVISER ", UserPreferenceEnum.fromString("beneficiary_lastaccessedadviser"),
                is(equalTo(UserPreferenceEnum.BENEFICIARY_REPORT_LAST_ACCESSED_ADVISER)));
        assertThat("UserPreference SUPER_CALCULATOR", UserPreferenceEnum.fromString("supercalculator"), is(equalTo(UserPreferenceEnum.SUPER_CALCULATOR)));
        assertThat("UserPreference UNKNOWN", UserPreferenceEnum.fromString(null), is(equalTo(UserPreferenceEnum.UNKNOWN)));
    }

}
