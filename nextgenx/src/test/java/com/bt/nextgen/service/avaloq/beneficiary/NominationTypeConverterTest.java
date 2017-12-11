package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.integration.code.Code;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Integration test for {@link NominationTypeConverter}
 * Created by M035995 on 19/07/2016.
 */
public class NominationTypeConverterTest extends BaseSecureIntegrationTest {

    @Autowired
    private NominationTypeConverter nominationTypeConverter;

    @Test
    public void testNominationConverterWithValidNominationId() {
        // Test nomination - binding
        Code code = nominationTypeConverter.convert("4");
        assertThat("Nomination Type : Internal Id:- ", code.getIntlId(), is(equalTo("nomn_bind_nlaps_trustd")));
        assertThat("Nomination Type : Code Id :- ", code.getCodeId(), is(equalTo("4")));
        assertThat("Nomination Type : User Id :- ", code.getUserId(), is(equalTo("NOMN_BIND_NLAPS_TRUSTD")));
        assertThat("Nomination Type : Name :- ", code.getName(), is(equalTo("Australian Superannuation Death Benefits" +
                " Nomination - Binding - Non Lapsing (Trust Deed)")));
        assertThat("Nomination Type : Category :- ", code.getCategory(), is(equalTo("btfg$au_sa_death_benf")));

        // Test nomination - auto-reversionary
        code = nominationTypeConverter.convert("21");
        assertThat("Nomination Type : Internal Id:- ", code.getIntlId(), is(equalTo("nomn_auto_revsnry")));
        assertThat("Nomination Type : Code Id :- ", code.getCodeId(), is(equalTo("21")));
        assertThat("Nomination Type : User Id :- ", code.getUserId(), is(equalTo("NOMN_AUTO_REVSNRY")));
        assertThat("Nomination Type : Name :- ", code.getName(), is(equalTo("Australian Superannuation Death Benefits" +
                " Nomination - Automatic Reversionary")));
        assertThat("Nomination Type : Category :- ", code.getCategory(), is(equalTo("btfg$au_sa_death_benf")));

        // Test nomination - non-binding
        code = nominationTypeConverter.convert("2");
        assertThat("Nomination Type : Internal Id:- ", code.getIntlId(), is(equalTo("nomn_nbind_sis")));
        assertThat("Nomination Type : Code Id :- ", code.getCodeId(), is(equalTo("2")));
        assertThat("Nomination Type : User Id :- ", code.getUserId(), is(equalTo("NOMN_NBIND_SIS")));
        assertThat("Nomination Type : Name :- ", code.getName(), is(equalTo("Australian Superannuation Death Benefits " +
                "Nomination - Non Binding (SIS)")));
        assertThat("Nomination Type : Category :- ", code.getCategory(), is(equalTo("btfg$au_sa_death_benf")));
    }

}
