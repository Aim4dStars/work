package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.policy.model.PolicySummaryDto;
import com.bt.nextgen.api.policy.model.PolicyTrackingDto;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyType;
import ns.btfin_com.product.insurance.lifeinsuranceservice.policy.v4_2.PremiumFrequencyType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PolicySummaryDtoConverterTest {

    @Test
    public void testGetPolicySummaryCsvDtos() {
        List<PolicyTrackingDto> policyTrackingDtos = new ArrayList<>();

        PolicySummaryDto policySummaryDto1 = new PolicySummaryDto();
        PolicySummaryDto policySummaryDto2 = new PolicySummaryDto();
        PolicySummaryDto policySummaryDto3 = new PolicySummaryDto();

        policySummaryDto1.setPolicyType(PolicyType.BUSINESS_OVERHEAD.name());
        policySummaryDto1.setPolicyStatus(PolicyStatusCode.DECLINED.name());
        policySummaryDto1.setPaymentFrequency(PremiumFrequencyType.MONTHLY.name());
        policySummaryDto1.setPensionType(PensionType.TTR.name());
        policySummaryDto1.setAccountType("BT Panorama Super");
        policySummaryDto1.setAccountSubType("Super");

        policySummaryDto2.setPolicyType(PolicyType.ADVICE_SERVICE_FEE.name());
        policySummaryDto2.setPolicyStatus(PolicyStatusCode.PROPOSAL.name());
        policySummaryDto2.setPaymentFrequency(PremiumFrequencyType.MONTHLY.name());
        policySummaryDto2.setPensionType(PensionType.DEATH_BENEFITS.name());
        policySummaryDto2.setAccountType("BT Panorama Super");
        policySummaryDto2.setAccountSubType("Super");

        policySummaryDto3.setPolicyType(PolicyType.INCOME_LINKING_PLUS.name());
        policySummaryDto3.setPolicyStatus(PolicyStatusCode.CANCELLED.name());
        policySummaryDto3.setPaymentFrequency(PremiumFrequencyType.MONTHLY.name());
        policySummaryDto3.setPensionType(PensionType.DEATH_BENEFITS.name());
        policySummaryDto3.setAccountType("BT Panorama Super");
        policySummaryDto3.setAccountSubType("Super");

        policyTrackingDtos.add(policySummaryDto1);
        policyTrackingDtos.add(policySummaryDto2);
        policyTrackingDtos.add(policySummaryDto3);

        List<PolicySummaryDto> policySummaryDtos = PolicySummaryDtoConverter.getPolicySummaryCsvDtos(policyTrackingDtos);
        Assert.assertNotNull(policySummaryDtos);

        PolicySummaryDto policyBusinessOverhead = (PolicySummaryDto) policySummaryDtos.get(0);
        Assert.assertEquals("Business Overheads", policyBusinessOverhead.getPolicyType());
        Assert.assertEquals("Declined", policyBusinessOverhead.getPolicyStatus());
        Assert.assertEquals("Monthly", policyBusinessOverhead.getPaymentFrequency());
        Assert.assertEquals("Super (TTR)", policyBusinessOverhead.getAccountType());
        Assert.assertNull(policyBusinessOverhead.getRenewalCalenderDay());
        Assert.assertEquals("-", policyBusinessOverhead.getCsvCommencementDate());

        PolicySummaryDto policyAdviceServiceFees = (PolicySummaryDto) policySummaryDtos.get(1);
        Assert.assertEquals("Advice Service Fee", policyAdviceServiceFees.getPolicyType());
        Assert.assertEquals("Proposal", policyAdviceServiceFees.getPolicyStatus());
        Assert.assertEquals("Monthly", policyAdviceServiceFees.getPaymentFrequency());
        Assert.assertEquals("Super", policyAdviceServiceFees.getAccountType());
        Assert.assertNull(policyAdviceServiceFees.getRenewalCalenderDay());
        Assert.assertEquals("Pending", policyAdviceServiceFees.getCsvCommencementDate());

        PolicySummaryDto policyIncomeLinkingPlus = (PolicySummaryDto) policySummaryDtos.get(2);
        Assert.assertEquals("Income Linking Plus", policyIncomeLinkingPlus.getPolicyType());
        Assert.assertEquals("Cancelled", policyIncomeLinkingPlus.getPolicyStatus());
        Assert.assertEquals("Monthly", policyIncomeLinkingPlus.getPaymentFrequency());
        Assert.assertEquals("Super", policyIncomeLinkingPlus.getAccountType());
        Assert.assertNull(policyIncomeLinkingPlus.getRenewalCalenderDay());
    }

    @Test
    public void testSetAccountTypeAttributeWhenAccountTypeIsPensionTTRTaxed() {
        PolicySummaryDto policySummaryDto = new PolicySummaryDto();
        policySummaryDto.setPensionType(PensionType.TTR.getValue());
        policySummaryDto.setAccountSubType("Pension");
        String accountType = PolicySummaryDtoConverter.setAccountTypeAttribute(policySummaryDto);
        Assert.assertEquals(accountType, "Pension (TTR)");
    }

    @Test
    public void testSetAccountTypeAttributeWhenAccountTypeIsPensionTTRRetirement() {
        PolicySummaryDto policySummaryDto = new PolicySummaryDto();
        policySummaryDto.setPensionType(PensionType.TTR_RETIR_PHASE.getValue());
        policySummaryDto.setAccountSubType("Pension");
        String accountType = PolicySummaryDtoConverter.setAccountTypeAttribute(policySummaryDto);
        Assert.assertEquals(accountType, "Pension (TTR_RETIR_PHASE)");
    }

    @Test
    public void testSetAccountTypeAttributeWhenAccountTypeIsPension() {
        PolicySummaryDto policySummaryDto = new PolicySummaryDto();
        policySummaryDto.setAccountSubType("Pension");
        String accountType = PolicySummaryDtoConverter.setAccountTypeAttribute(policySummaryDto);
        Assert.assertEquals(accountType, "Pension");
    }

    @Test
    public void testSetAccountTypeAttributeWhenAccountTypeIsSMSF() {
        PolicySummaryDto policySummaryDto = new PolicySummaryDto();
        policySummaryDto.setAccountSubType("SMSF");
        String accountType = PolicySummaryDtoConverter.setAccountTypeAttribute(policySummaryDto);
        Assert.assertEquals(accountType, "SMSF");
    }
}
