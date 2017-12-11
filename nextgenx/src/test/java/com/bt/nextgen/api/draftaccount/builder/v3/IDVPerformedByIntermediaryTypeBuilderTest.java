package com.bt.nextgen.api.draftaccount.builder.v3;

import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.user.CISKey;
import ns.btfin_com.party.intermediary.v1_1.IntermediaryOrganisationType;
import ns.btfin_com.party.intermediary.v1_1.IntermediaryType;
import ns.btfin_com.party.intermediary.v1_1.OrganisationUnitType;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.party.v3_0.IDVPerformedByIntermediaryType;
import ns.btfin_com.party.v3_0.IndividualPartyType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests for the {@code IDVPerformedByIntermediaryTypeBuilder} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class IDVPerformedByIntermediaryTypeBuilderTest {

    private IDVPerformedByIntermediaryTypeBuilder builder;

    @Mock
    private BrokerUser user;

    @Mock
    private Broker dealer;

    @Before
    public void initBuilder() {
        builder = new IDVPerformedByIntermediaryTypeBuilder();
    }

    @Test
    public void intermediary() throws Exception {
        when(user.getCISKey()).thenReturn(CISKey.valueOf("912345678"));
        when(user.getBankReferenceId()).thenReturn("048838232328");
        when(user.getFirstName()).thenReturn("Brian");
        when(user.getLastName()).thenReturn("Broker");
        when(dealer.getPositionName()).thenReturn("Dealer Group");

        final IDVPerformedByIntermediaryType intermediary = builder.intermediary(user, dealer);
        final IndividualPartyType individual = intermediary.getIntermediaryDetails();
        assertThat(individual.getCustomerNumber(), is("048838232328"));
        assertThat(individual.getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.BT_PANORAMA));
        assertThat(individual.getPartyDetails().getGivenName(), is("Brian"));
        assertThat(individual.getPartyDetails().getLastName(), is("Broker"));

        final IntermediaryOrganisationType organisation = intermediary.getOrganisation();
        List<OrganisationUnitType> units = organisation.getOrganisationUnit();
        assertThat(units.size(), is(1));
        assertThat(units.get(0).getOrganisationUnitName(), is("Dealer Group"));
    }
}
