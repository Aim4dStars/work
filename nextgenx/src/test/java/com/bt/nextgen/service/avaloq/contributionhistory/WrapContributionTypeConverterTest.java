package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.wrap.model.Contribution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WrapContributionTypeConverterTest {

    @InjectMocks
    private WrapContributionTypeConverter wrapContributionTypeConverter;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    private List<Contribution> contributions = new ArrayList<>();

    @Before
    public void init() throws Exception {


        Contribution contribution = new Contribution();
        contribution.setClientId("M00000027");
        contribution.setContributionType("Concessional");
        contribution.setContributionSubType("Personal injury");
        contribution.setContributionAmount(new BigDecimal("543.56"));

        //ContributionType Null
        Contribution contribution1 = new Contribution();
        contribution1.setClientId("M00000027");
        contribution1.setContributionType("Non-concessional");
        contribution1.setContributionSubType(null);
        contribution1.setContributionAmount(new BigDecimal("143.36"));

        //ContributionType not found in WrapContributionTypeMapping
        Contribution contribution2 = new Contribution();
        contribution2.setClientId("M00000027");
        contribution2.setContributionType("Non-concessional");
        contribution2.setContributionSubType("First Home Saver Account");
        contribution2.setContributionAmount(new BigDecimal("13.36"));

        contributions.add(contribution);
        contributions.add(contribution1);
        contributions.add(contribution2);

        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.SUPER_CONTRIBUTIONS_TYPE), eq("pers_injury_nclaim"), any(ServiceErrors.class))).thenReturn(getContributionCode1());


    }
    private Code getContributionCode1() {
        Code code = new CodeImpl("63","PERS_INJURY_NCLAIM","Structured Settlement or Personal Injury - Not Claim Personal Tax Deduction");
        ((CodeImpl) code).addField("btfg$ui_name", "Structured Settlement");
        return code;
    }

    @Test
    public void toContributionHistory(){
        ContributionHistory contributionHistory = wrapContributionTypeConverter.toContributionHistory(contributions,staticIntegrationService);
        assertThat(contributionHistory, notNullValue());
        assertThat(contributionHistory.getContributionSummariesByType().size(), is(1));
        ContributionSummaryByType contributionSummaryByType = contributionHistory.getContributionSummariesByType().get(0);
        assertThat(contributionSummaryByType.getAmount(), is(new BigDecimal("543.56")));
        assertThat(contributionSummaryByType.getContributionClassification(), is(ContributionClassification.CONCESSIONAL));
        assertThat(contributionSummaryByType.getContributionType().getId(), is("pers_injury_nclaim"));
        assertThat(contributionSummaryByType.getContributionType().getLabel(), is("Structured Settlement"));
    }
}
