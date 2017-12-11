package com.bt.nextgen.service.avaloq.insurance;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.insurance.model.CommissionState;
import com.bt.nextgen.service.avaloq.insurance.model.CommissionStructureType;
import com.bt.nextgen.service.avaloq.insurance.model.PaymentType;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyApplications;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.model.PolicySubType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTracking;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyUnderWritingNotesImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyUnderwriting;
import com.bt.nextgen.service.avaloq.insurance.model.PremiumFrequencyType;
import com.bt.nextgen.service.avaloq.insurance.service.InsuranceIntegrationServiceImpl;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class PolicyIntegrationServiceImplTest extends BaseSecureIntegrationTest
{
    @Autowired
    InsuranceIntegrationServiceImpl insuranceIntegrationService;

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "ADVISER")
    public void testSearchFNumberForPPID425059904()
    {
        List<PolicyTracking> fnumberList = insuranceIntegrationService.getFNumbers("425059904", new ServiceErrorsImpl());
        assertNotNull(fnumberList);
        assertThat(1, Is.is(fnumberList.size()));
        assertThat("F0484682", Is.is(fnumberList.get(0).getFNumber()));
    }

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "ADVISER")
    public void testSearchFNumberForPPID675056129()
    {
        List<PolicyTracking> fnumberList = insuranceIntegrationService.getFNumbers("675056129", new ServiceErrorsImpl());
        assertNotNull(fnumberList);
        assertThat(11, Is.is(fnumberList.size()));
        assertThat("F0239134", Is.is(fnumberList.get(0).getFNumber()));
        assertThat("F0239135", Is.is(fnumberList.get(1).getFNumber()));
        assertThat("F0239136", Is.is(fnumberList.get(2).getFNumber()));
        assertThat("F0239137", Is.is(fnumberList.get(3).getFNumber()));
        assertThat("F0239138", Is.is(fnumberList.get(4).getFNumber()));
        assertThat("F0239139", Is.is(fnumberList.get(5).getFNumber()));
        assertThat("F0239140", Is.is(fnumberList.get(6).getFNumber()));
        assertThat("F0239141", Is.is(fnumberList.get(7).getFNumber()));
        assertThat("F0239142", Is.is(fnumberList.get(8).getFNumber()));
        assertThat("F0239143", Is.is(fnumberList.get(9).getFNumber()));
        assertThat("F0239144", Is.is(fnumberList.get(10).getFNumber()));
    }

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "ADVISER")
    public void testSearchFNumberForPPID735208629()
    {
        List<PolicyTracking> fnumberList = insuranceIntegrationService.getFNumbers("735208629", new ServiceErrorsImpl());
        assertNotNull(fnumberList);
        assertThat(4, Is.is(fnumberList.size()));
        assertThat("F0484682", Is.is(fnumberList.get(0).getFNumber()));
        assertThat("F0183441", Is.is(fnumberList.get(1).getFNumber()));
        assertThat("F0177914", Is.is(fnumberList.get(2).getFNumber()));
        assertThat("F0239134", Is.is(fnumberList.get(3).getFNumber()));
    }

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "ADVISER")
    public void testSearchFNumberForError()
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<PolicyTracking> fnumberList = insuranceIntegrationService.getFNumbers("735258629", serviceErrors);
        assertNotNull(fnumberList);
        assertTrue(fnumberList.isEmpty());
        assertTrue(serviceErrors.hasErrors());
    }

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "adviser", customerId = "M034010", profileId ="561" , jobRole="ADVISER", jobId = "89436")
    public void testSearchByPolicyDetails()
    {
        List<Policy> result = insuranceIntegrationService.retrievePolicyByPolicyNumber("C0159786", new ServiceErrorsImpl());

        assertNotNull(result);
        assertEquals(1, result.size());

        Policy policy = result.get(0);

        assertEquals("C0159786", policy.getPolicyNumber());
        assertEquals(PolicyType.INCOME_PROTECTION, policy.getPolicyType());
        assertEquals(PolicySubType.AGREED_VALUE, policy.getPolicySubType());
        assertEquals(PolicyStatusCode.IN_FORCE, policy.getStatus());
        assertEquals(PremiumFrequencyType.MONTHLY, policy.getPolicyFrequency());
        assertEquals("140.2", policy.getPremium().toString());
        assertEquals(1, policy.getOwners().size());
        assertEquals(1, policy.getBeneficiaries().size());
        assertEquals(CommissionStructureType.UP_FRONT, policy.getCommissionStructure());
        assertEquals("B0159786A", policy.getPortfolioNumber());
        assertEquals(Boolean.FALSE, policy.isSharedPolicy());
        assertEquals(new DateTime("2016-04-13"), policy.getCommencementDate());
        assertEquals(getRenewalDate(3,13), policy.getRenewalCalendarDay());
        assertEquals(new DateTime("2016-04-13"), policy.getPaidToDate());
        assertEquals("11.0", policy.getRenewalPercent().toString());
        assertEquals(CommissionState.OPT_IN, policy.getCommissionState());
        assertEquals("0.0", policy.getDialDown().toString());
        assertEquals(1, policy.getPolicyLifes().size());
    }

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "adviser", customerId = "M034010", profileId ="561" , jobRole="ADVISER", jobId = "89436")
    public void testSearchByPolicyDetailsError()
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<Policy> result = insuranceIntegrationService.retrievePolicyByPolicyNumber("C5698745", serviceErrors);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertTrue(serviceErrors.hasErrors());
    }


    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "ADVISER")
    public void testSearchByAccountNumber()
    {
        List<Policy> result = insuranceIntegrationService.retrievePoliciesByAccountNumber("44853", new ServiceErrorsImpl());

        assertNotNull(result);
        assertEquals(1, result.size());

        Policy policy = result.get(0);

        assertEquals("C0159786", policy.getPolicyNumber());
        assertEquals(PolicyType.INCOME_PROTECTION, policy.getPolicyType());
        assertEquals(PolicySubType.AGREED_VALUE, policy.getPolicySubType());
        assertEquals(PolicyStatusCode.IN_FORCE, policy.getStatus());
        assertEquals(PremiumFrequencyType.MONTHLY, policy.getPolicyFrequency());
        assertEquals("140.2", policy.getPremium().toString());
        assertEquals(1, policy.getOwners().size());
        assertEquals(1, policy.getBeneficiaries().size());
        assertEquals(CommissionStructureType.UP_FRONT, policy.getCommissionStructure());
        assertEquals("B0159786A", policy.getPortfolioNumber());
        assertNull(policy.getParentPolicyNumber());
        assertEquals(Boolean.FALSE, policy.isSharedPolicy());
        assertEquals(new DateTime("2016-04-13"), policy.getCommencementDate());
        assertEquals(getRenewalDate(3,13), policy.getRenewalCalendarDay());
        assertEquals(new DateTime("2016-04-13"), policy.getPaidToDate());
        assertEquals("11.0", policy.getRenewalPercent().toString());
        assertEquals(CommissionState.OPT_IN, policy.getCommissionState());
        assertEquals("0.0", policy.getDialDown().toString());
        assertEquals(1, policy.getPolicyLifes().size());
    }

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "ADVISER")
    public void testSearchPolicyByCustomerNumner()
    {
        List <String> fnumbers = new ArrayList<>();
        fnumbers.add("425059904");
        List<PolicyTracking> result = insuranceIntegrationService.getPolicyByCustomerNumber(fnumbers, "12345", new ServiceErrorsImpl());

        PolicyTracking policy = result.get(0);
        assertEquals("CF436983", policy.getPolicyNumber());
        assertEquals(PolicyType.INCOME_PROTECTION, policy.getPolicyType());
        assertEquals(PolicySubType.AGREED_VALUE, policy.getPolicySubType());
        assertEquals(PolicyStatusCode.CANCELLED, policy.getPolicyStatus());
        assertEquals("M04651847", policy.getAccountNumber());
        assertEquals("ASGARD", policy.getInstitutionName());
        assertEquals(PaymentType.WCACC, policy.getPaymentType());
        assertEquals(new BigDecimal("5805.03"), policy.getPremium());
        assertEquals(new BigDecimal("0.0"), policy.getProposedPremium());
        assertEquals(PremiumFrequencyType.YEARLY, policy.getPaymentFrequency());
        assertEquals(new DateTime(2015, 1, 30, 0, 0), policy.getCommencementDate());
        assertEquals(new BigDecimal("0.0"), policy.getRenewalCommission());
        assertEquals(getRenewalDate(0,30), policy.getRenewalCalendarDay());//parameter zero for January month


        policy = result.get(1);
        assertEquals("CL930284", policy.getPolicyNumber());
        assertEquals(PolicyType.INCOME_LINKING_PLUS, policy.getPolicyType());
        assertEquals(PolicySubType.AGREED_VALUE, policy.getPolicySubType());
        assertEquals(PolicyStatusCode.CANCELLED, policy.getPolicyStatus());
        assertNotNull(policy.getAccountNumber());
        assertNotNull(policy.getInstitutionName());
        assertEquals(PaymentType.WCACC, policy.getPaymentType());
        assertEquals(new BigDecimal("1092.41"), policy.getPremium());
        assertEquals(new BigDecimal("0.0"), policy.getProposedPremium());
        assertEquals(PremiumFrequencyType.YEARLY, policy.getPaymentFrequency());
        assertEquals(new DateTime(2015, 1, 30, 0, 0), policy.getCommencementDate());
        assertEquals(new BigDecimal("0.0"), policy.getRenewalCommission());
        assertEquals(getRenewalDate(0,30), policy.getRenewalCalendarDay());//parameter zero for January month

    }

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "ADVISER")
    public void testSearchPolicyByCustomerNumnerError()
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List <String> fnumbers = new ArrayList<>();
        fnumbers.add("425069904");
        List<PolicyTracking> result = insuranceIntegrationService.getPolicyByCustomerNumber(fnumbers, "65984", serviceErrors);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertTrue(serviceErrors.hasErrors());

    }

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "ADVISER")
    public void testGetPoliciesForAdviser()
    {
        List<PolicyTracking> result = insuranceIntegrationService.getPoliciesForAdviser("12345", new ServiceErrorsImpl());
        Assert.assertNotNull(result);

        PolicyTracking policy = result.get(0);
        assertEquals("CF000001", policy.getPolicyNumber());
        assertEquals(PolicyType.INCOME_LINKING_PLUS, policy.getPolicyType());
        assertEquals(PolicySubType.AGREED_VALUE, policy.getPolicySubType());
        assertEquals(PolicyStatusCode.PROPOSAL, policy.getPolicyStatus());
        assertEquals("120006374", policy.getAccountNumber());
        assertEquals(PaymentType.WCACC, policy.getPaymentType());
        assertEquals(new BigDecimal("0.0"), policy.getPremium());
        assertEquals(new BigDecimal("99.25"), policy.getProposedPremium());
        assertEquals(PremiumFrequencyType.YEARLY, policy.getPaymentFrequency());
        assertEquals(getRenewalDate(9,1), policy.getRenewalCalendarDay());//parameter zero for January month


        policy = result.get(1);
        assertEquals("C0000001", policy.getPolicyNumber());
        assertEquals(PolicyType.INCOME_PROTECTION, policy.getPolicyType());
        assertEquals(PolicySubType.AGREED_VALUE, policy.getPolicySubType());
        assertEquals(PolicyStatusCode.PROPOSAL, policy.getPolicyStatus());
        assertEquals("400001194", policy.getAccountNumber());
        assertEquals(PaymentType.WCACC, policy.getPaymentType());
        assertEquals(new BigDecimal("0.0"), policy.getPremium());
        assertEquals(new BigDecimal("308.0"), policy.getProposedPremium());
        assertEquals(PremiumFrequencyType.MONTHLY, policy.getPaymentFrequency());
        assertEquals(getRenewalDate(9,1), policy.getRenewalCalendarDay());//parameter zero for January month

    }

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "ADVISER")
    public void testGetPoliciesForAdviserError()
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<PolicyTracking> result = insuranceIntegrationService.getPoliciesForAdviser("32658", serviceErrors);
        Assert.assertNotNull(result);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertTrue(serviceErrors.hasErrors());
    }

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "ADVISER")
    public void testSearchUnderwritingNotes()
    {
        PolicyUnderwriting result = insuranceIntegrationService.getUnderwritingNotes("CF436983", new ServiceErrorsImpl());
        assertNotNull(result);

        assertNotNull(result.getPolicyDetails());
        PolicyTracking policyTracking = result.getPolicyDetails().get(0);
        assertEquals("CL930284", policyTracking.getPolicyNumber());
        assertEquals(PolicyType.TERM_LIFE, policyTracking.getPolicyType());
        assertEquals(PolicyStatusCode.PROPOSAL, policyTracking.getPolicyStatus());

        assertNotNull(result.getUnderWritingNotes());
        PolicyUnderWritingNotesImpl policyUnderWritingNotes = result.getUnderWritingNotes().get(0);
        assertEquals("STANDARD RATES", policyUnderWritingNotes.getCodeDescription());
        assertEquals("STANDARD RATES", policyUnderWritingNotes.getUnderwritingDetails());
    }

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "ADVISER")
    public void testGetRecentLivesInsured()
    {
        List <String> fnumbers = new ArrayList<>();
        fnumbers.add("425059904");
        List<PolicyApplications> result = insuranceIntegrationService.getRecentLivesInsured(fnumbers, new ServiceErrorsImpl());
        assertNotNull(result);

        PolicyApplications application1 = result.get(0);
        assertEquals("CF436983", application1.getPolicyNumber());
        assertEquals("2796462", application1.getCustomerNumber());
        assertEquals(new BigDecimal("0000180.60"), application1.getTotalPremium());
        assertEquals("STACIE", application1.getInsuredPersonGivenName());
        assertEquals("HURLEY", application1.getInsuredPersonLastName());

        PolicyApplications application2 = result.get(1);
        assertEquals("CF436986", application2.getPolicyNumber());
        assertEquals("3159635", application2.getCustomerNumber());
        assertEquals(new BigDecimal("0000211.92"), application2.getTotalPremium());
        assertEquals("KADN", application2.getInsuredPersonGivenName());
        assertEquals("DENNIS", application2.getInsuredPersonLastName());
    }

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "notification", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "ADVISER")
    public void testGetRecentLivesInsuredError()
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List <String> fnumbers = new ArrayList<>();
        fnumbers.add("425062904");
        List<PolicyApplications> result = insuranceIntegrationService.getRecentLivesInsured(fnumbers, serviceErrors);
        Assert.assertNotNull(result);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertTrue(serviceErrors.hasErrors());
    }

    /**
     * paramter is the desired month and date, returns the datetime with proper year
     * @param month
     * @return
     */
    private DateTime getRenewalDate(int month, int dateOfMonth)
    {
        GregorianCalendar today = new GregorianCalendar();
        GregorianCalendar date = new GregorianCalendar();
        date.set(Calendar.DATE, dateOfMonth);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MILLISECOND, 0);
        if (date.getTime().compareTo(today.getTime()) < 0) {
            date.set(Calendar.YEAR, new org.joda.time.DateTime().getYear() + 1);
        }
        return new DateTime(date.getTime());
    }
}