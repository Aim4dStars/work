package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.api.beneficiary.service.BeneficiaryDtoServiceImpl;
import com.bt.nextgen.api.policy.model.PolicyDto;
import com.bt.nextgen.api.policy.model.PolicySummaryDto;
import com.bt.nextgen.api.policy.model.PolicyTrackingDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.beneficiary.RelationshipType;
import com.bt.nextgen.service.avaloq.client.ClientImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualImpl;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitOptionStatus;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitOptionType;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitOptionsImpl;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitType;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitsImpl;
import com.bt.nextgen.service.avaloq.insurance.model.CommissionState;
import com.bt.nextgen.service.avaloq.insurance.model.CommissionStructureType;
import com.bt.nextgen.service.avaloq.insurance.model.OccupationClass;
import com.bt.nextgen.service.avaloq.insurance.model.PersonImpl;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyLifeImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.model.PolicySubType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTracking;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTrackingImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyType;
import com.bt.nextgen.service.avaloq.insurance.model.PremiumFrequencyType;
import com.bt.nextgen.service.avaloq.insurance.model.PremiumType;
import com.bt.nextgen.service.avaloq.insurance.model.TPDBenefitDefinitionCode;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PolicyDtoConverterTest {

    PolicyDtoConverter policyDtoConverter;

    @Mock
    BeneficiaryDtoServiceImpl beneficiaryDtoService;


    private Policy createTermLifePolicy() {
        PolicyImpl policy = new PolicyImpl();
        policy.setPolicyType(PolicyType.TERM_LIFE);
        policy.setPolicySubType(PolicySubType.ALL);
        policy.setPolicyNumber("44433332");
        policy.setPolicyFrequency(PremiumFrequencyType.MONTHLY);
        policy.setAccountNumber("254686789");
        policy.setPremium(new BigDecimal(2600.00));
        policy.setProposedPremium(new BigDecimal(200.00));
        policy.setStatus(PolicyStatusCode.IN_FORCE);

        PersonImpl owner1 = new PersonImpl();
        owner1.setBeneficiaryContribution(new BigDecimal(4000));
        owner1.setGivenName("Eric");
        owner1.setLastName("Lee");
        List<PersonImpl> personList = new ArrayList<>();
        personList.add(owner1);
        policy.setOwners(personList);

        PersonImpl beneficiary1 = new PersonImpl();
        beneficiary1.setBeneficiaryContribution(new BigDecimal(5000));
        beneficiary1.setGivenName("Elsie");
        beneficiary1.setLastName("Lee");
        List<PersonImpl> beneficiariesList = new ArrayList<>();
        policy.setBeneficiaries(beneficiariesList);

        policy.setCommissionStructure(CommissionStructureType.UP_FRONT);
        policy.setPortfolioNumber("999000000");
        policy.setParentPolicyNumber("10000001");
        policy.setSharedPolicy(new Boolean(false));
        policy.setCommencementDate(new DateTime(2015, 5, 15, 2, 3));
        policy.setRenewalCalendarDay(new DateTime(2016, 5, 15, 2, 3));
        policy.setPaidToDate(new DateTime(2017, 5, 15, 2, 3));
        policy.setRenewalPercent(new BigDecimal(74));
        policy.setCommissionState(CommissionState.OPT_IN);
        policy.setDialDown(new BigDecimal(0.67));
        policy.setBenefitPeriodFactor("44");
        policy.setBenefitPeriodTerm("4");
        policy.setWaitingPeriod(3);

        PolicyLifeImpl policyLife = new PolicyLifeImpl();
        policyLife.setGivenName("James");
        policyLife.setLastName("Lee");
        String[] addresses = {"33 Pitt Street", "Sydney", "Australia"};
        policyLife.setAddresses(Arrays.asList(addresses));
        policyLife.setDateOfBirth(new DateTime(1990, 9, 15, 2, 3));
        policyLife.setSmokingStatus(Boolean.FALSE);
        policyLife.setCity("Sydney");
        policyLife.setCountryCode("AU");
        policyLife.setState("NSW");

        BenefitsImpl benefits = new BenefitsImpl();
        benefits.setBenefitType(BenefitType.INCOME_PROTECTION);
        benefits.setPremiumType(PremiumType.LEVEL55);
        benefits.setCommencementDate(new DateTime(2015, 9, 15, 2, 3));
        benefits.setOccupationClass(OccupationClass.A);
        benefits.setProposedSumInsured(new BigDecimal(40000005));
        benefits.setSumInsured(new BigDecimal(500000001));
        benefits.setTpdDefinition(TPDBenefitDefinitionCode.ANY_OCCUPATION);
        List<BenefitsImpl> benefitsList = new ArrayList<>();
        benefitsList.add(benefits);

        BenefitOptionsImpl benefitOptions = new BenefitOptionsImpl();
        benefitOptions.setBenefitOptions(BenefitOptionType.LIVING_BUSINESS_COVER);
        benefitOptions.setBenefitOptionStatus(BenefitOptionStatus.InForce);
        List<BenefitOptionsImpl> benefitOptionsList = new ArrayList<>();
        benefitOptionsList.add(benefitOptions);

        policyLife.setBenefits(benefitsList);
        policyLife.setBenefitOptions(benefitOptionsList);
        List<PolicyLifeImpl> policyLifes = new ArrayList<>();

        policy.setPolicyLifes(policyLifes);
        policy.setIPIncomeRatioPercent(new BigDecimal(0.4));

        return policy;
    }

    private List<com.bt.nextgen.api.beneficiary.model.BeneficiaryDto> getAvaloqBeneficiaries() {
        //Mock Beneficiaries returned from avaloq
        List<Beneficiary> beneficiaries = new ArrayList<>();
        Beneficiary beneficiary1 = new Beneficiary();
        beneficiary1.setFirstName("Abhinav");
        beneficiary1.setLastName("Gupta");
        beneficiary1.setAllocationPercent("100");
        beneficiary1.setRelationshipType(RelationshipType.LPR.getAvaloqInternalId());
        beneficiaries.add(beneficiary1);
        List<com.bt.nextgen.api.beneficiary.model.BeneficiaryDto> beneficiaryDtoList = new ArrayList<>();
        com.bt.nextgen.api.beneficiary.model.BeneficiaryDto avaloqBeneficiaryDetails = new com.bt.nextgen.api.beneficiary.model.BeneficiaryDto();
        avaloqBeneficiaryDetails.setTotalAllocationPercent("100");
        avaloqBeneficiaryDetails.setBeneficiaries(beneficiaries);
        beneficiaryDtoList.add(avaloqBeneficiaryDetails);
        return beneficiaryDtoList;
    }

    @Test
    public void testDtoGivenNameMappingIsCorrect() {
        Policy policy = createTermLifePolicy();
        policy.getOwners().get(0).setGivenName("Westpac Securities Administrat");
        List<Policy> policyList = new ArrayList<>();
        policyList.add(policy);
        Mockito.when(beneficiaryDtoService.getBeneficiaryDetails(Mockito.any(com.bt.nextgen.api.account.v3.model.AccountKey.class), Mockito.any(ServiceErrors.class), Matchers.anyString())).thenReturn(getAvaloqBeneficiaries());
        List<PolicyDto> policyDtoList = policyDtoConverter.toPolicyDto("12354", policyList, beneficiaryDtoService);
        assertNotNull(policyDtoList);
        assertThat(1, is(policyDtoList.size()));
        assertNotNull(policyDtoList.get(0));
        assertThat(1, is(policyDtoList.get(0).getOwners().size()));
        assertThat("Westpac Securities Administration Limited", is(policyDtoList.get(0).getOwners().get(0).getGivenName()));
    }


    @Test
    public void testDtoGivenNameCAPSMappingIsCorrect() {
        Policy policy = createTermLifePolicy();
        policy.getOwners().get(0).setGivenName("WESTPAC SECURITIES ADMINISTRAT");
        List<Policy> policyList = new ArrayList<>();
        policyList.add(policy);
        Mockito.when(beneficiaryDtoService.getBeneficiaryDetails(Mockito.any(com.bt.nextgen.api.account.v3.model.AccountKey.class), Mockito.any(ServiceErrors.class), Matchers.anyString())).thenReturn(getAvaloqBeneficiaries());
        List<PolicyDto> policyDtoList = policyDtoConverter.toPolicyDto("12354", policyList, beneficiaryDtoService);
        assertNotNull(policyDtoList);
        assertThat(1, is(policyDtoList.size()));
        assertNotNull(policyDtoList.get(0));
        assertThat(1, is(policyDtoList.get(0).getOwners().size()));
        assertThat("Westpac Securities Administration Limited", is(policyDtoList.get(0).getOwners().get(0).getGivenName()));
    }

    @Test
    public void testDtoPremiumAmountMappingIsCorrect() {
        Policy policy = createTermLifePolicy();
        List<Policy> policyList = new ArrayList<>();
        policyList.add(policy);
        Mockito.when(beneficiaryDtoService.getBeneficiaryDetails(Mockito.any(com.bt.nextgen.api.account.v3.model.AccountKey.class), Mockito.any(ServiceErrors.class), Matchers.anyString())).thenReturn(getAvaloqBeneficiaries());
        List<PolicyDto> policyDtoList = policyDtoConverter.toPolicyDto("12354", policyList, beneficiaryDtoService);
        assertNotNull(policyDtoList);
        assertThat(1, is(policyDtoList.size()));
        assertNotNull(policyDtoList.get(0));
        assertThat("2800", is(policyDtoList.get(0).getPremium()));
    }

    @Before
    public void setup() {
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();

        AccountKey accountKey1 = AccountKey.valueOf("12354");
        AccountKey accountKey2 = AccountKey.valueOf("12364");
        AccountKey accountKey3 = AccountKey.valueOf("12384");
        AccountKey accountKey4 = AccountKey.valueOf("12385");

        ClientKey clientKey1 = ClientKey.valueOf("23659");
        ClientKey clientKey2 = ClientKey.valueOf("23859");
        ClientKey clientKey3 = ClientKey.valueOf("23759");

        WrapAccountImpl wrapAccount1 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount2 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount3 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount4 = new WrapAccountImpl();

        wrapAccount1.setAccountKey(accountKey1);
        wrapAccount1.setAccountNumber("254698789");
        wrapAccount1.setAccountName("Super acct");
        wrapAccount1.setAccountStructureType(AccountStructureType.SUPER);
        wrapAccount1.setSuperAccountSubType(AccountSubType.ACCUMULATION);
        wrapAccount1.setProductKey(ProductKey.valueOf("P123"));
        List<ClientKey> owners1 = new ArrayList<>();
        owners1.add(clientKey1);
        wrapAccount1.setAccountOwners(owners1);
        List<ClientKey> approver1 = new ArrayList<>();
        approver1.add(clientKey2);
        wrapAccount1.setApprovers(approver1);

        wrapAccount2.setAccountKey(accountKey2);
        wrapAccount2.setAccountName("SMSF acct");
        wrapAccount2.setAccountNumber("254696789");
        wrapAccount2.setAccountStructureType(AccountStructureType.SMSF);
        wrapAccount2.setProductKey(ProductKey.valueOf("P456"));
        List<ClientKey> owners2 = new ArrayList<>();
        owners2.add(clientKey1);
        owners2.add(clientKey2);
        wrapAccount2.setAccountOwners(owners2);

        wrapAccount3.setAccountKey(accountKey3);
        wrapAccount3.setAccountName("company acct");
        wrapAccount3.setAccountNumber("254686789");
        wrapAccount3.setAccountStructureType(AccountStructureType.Company);
        wrapAccount3.setProductKey(ProductKey.valueOf("P456"));
        List<ClientKey> owners3 = new ArrayList<>();
        owners3.add(clientKey2);
        owners3.add(clientKey3);
        wrapAccount3.setAccountOwners(owners3);

        wrapAccount4.setAccountKey(accountKey4);
        wrapAccount4.setAccountName("Joint acct");
        wrapAccount4.setAccountNumber("254686589");
        wrapAccount4.setAccountStructureType(AccountStructureType.Joint);
        List<ClientKey> owners4 = new ArrayList<>();
        owners4.add(clientKey3);
        wrapAccount4.setAccountOwners(owners4);

        accountMap.put(accountKey1, wrapAccount1);
        accountMap.put(accountKey2, wrapAccount2);
        accountMap.put(accountKey3, wrapAccount3);
        accountMap.put(accountKey4, wrapAccount4);

        Map<ProductKey, Product> productMap = new HashMap<>();
        ProductImpl productSuper = new ProductImpl();
        productSuper.setProductName("BT Panorama Super");
        ProductImpl productInv = new ProductImpl();
        productInv.setProductName("BT Panorama Investments");

        productMap.put(ProductKey.valueOf("P123"), productSuper);
        productMap.put(ProductKey.valueOf("P456"), productInv);

        policyDtoConverter = new PolicyDtoConverter(accountMap, productMap);
    }

    @Test
    public void testGetAccountNumbersWithLinkedAccounts() {

        Collection<AccountDto> linkedAccounts = policyDtoConverter.getAccountNumbersWithLinkedAccounts("12354", getClientMap());
        assertNotNull(linkedAccounts);
        Assert.assertEquals(3, linkedAccounts.size());
        List<AccountDto> accountDtoList = new ArrayList<>(linkedAccounts);

        Assert.assertEquals(accountDtoList.get(0).getAccountName(), "Joint acct");
        Assert.assertEquals(accountDtoList.get(0).getAccountNumber(), "254686589");
        Assert.assertEquals(accountDtoList.get(0).getAccountType(), "Joint");
        Assert.assertEquals(accountDtoList.get(0).getAccountTypeDescription(), "Joint");

        Assert.assertEquals(accountDtoList.get(1).getAccountName(), "company acct");
        Assert.assertEquals(accountDtoList.get(1).getAccountNumber(), "254686789");
        Assert.assertEquals(accountDtoList.get(1).getAccountType(), "Company");
        Assert.assertEquals(accountDtoList.get(1).getAccountTypeDescription(), "Company");

        Assert.assertEquals(accountDtoList.get(2).getAccountName(), "SMSF acct");
        Assert.assertEquals(accountDtoList.get(2).getAccountNumber(), "254696789");
        Assert.assertEquals(accountDtoList.get(2).getAccountType(), "SMSF");
        Assert.assertEquals(accountDtoList.get(2).getAccountTypeDescription(), "SMSF");
    }

    private Map<ClientKey, Client> getClientMap() {
        final ClientKey clientKey1 = ClientKey.valueOf("23659");
        final ClientKey clientKey2 = ClientKey.valueOf("23859");
        final ClientKey clientKey3 = ClientKey.valueOf("23759");
        final ClientKey clientKey4 = ClientKey.valueOf("23749");

        final List<ClientKey> associatedClients1 = new ArrayList<>();
        associatedClients1.add(clientKey2);
        associatedClients1.add(clientKey4);
        ClientImpl client1 = new IndividualImpl();
        client1.setClientKey(clientKey1);
        client1.setAssociatedPersonKeys(associatedClients1);

        final List<ClientKey> associatedClients2 = new ArrayList<>();
        ClientImpl client2 = new IndividualImpl();
        client2.setClientKey(clientKey2);
        client2.setAssociatedPersonKeys(associatedClients2);

        final List<ClientKey> associatedClients3 = new ArrayList<>();
        associatedClients3.add(clientKey2);
        ClientImpl client3 = new IndividualImpl();
        client3.setClientKey(clientKey3);
        client3.setAssociatedPersonKeys(associatedClients3);

        final List<ClientKey> associatedClients4 = new ArrayList<>();
        ClientImpl client4 = new IndividualImpl();
        client4.setClientKey(clientKey4);
        client4.setAssociatedPersonKeys(associatedClients4);

        Map<ClientKey, Client> clientMap = new HashMap<>();
        clientMap.put(clientKey1, client1);
        clientMap.put(clientKey2, client2);
        clientMap.put(clientKey3, client3);
        clientMap.put(clientKey4, client4);

        return clientMap;
    }

    @Test
    public void testPaymentMethodForSuperAccount() {
        Policy policy = createTermLifePolicy();
        List<Policy> policyList = new ArrayList<>();
        policyList.add(policy);
        List<PolicyDto> policyDtoList = policyDtoConverter.toPolicyDto("12384", policyList, null);
        assertNotNull(policyDtoList);
        assertThat(1, is(policyDtoList.size()));
        assertNotNull(policyDtoList.get(0));
        Assert.assertEquals(policyDtoList.get(0).getPaymentMethod(), "Panorama Investments");
    }

    @Test
    public void testPaymentMethodForNonSuperAccount() {
        Policy policy = createTermLifePolicy();
        ((PolicyImpl) policy).setAccountNumber("254698789");
        List<Policy> policyList = new ArrayList<>();
        policyList.add(policy);
        List<PolicyDto> policyDtoList = policyDtoConverter.toPolicyDto("12354", policyList, null);
        assertNotNull(policyDtoList);
        assertThat(1, is(policyDtoList.size()));
        assertNotNull(policyDtoList.get(0));
        Assert.assertEquals(policyDtoList.get(0).getPaymentMethod(), "Panorama Super");
    }

    public PolicyTracking createTermLifeTrackingPolicy(String accountNumber) {
        PolicyTrackingImpl policyTracking = new PolicyTrackingImpl();
        policyTracking.setAccountNumber(accountNumber);
        policyTracking.setPolicyType(PolicyType.TERM_LIFE);
        policyTracking.setPolicySubType(PolicySubType.ALL);
        policyTracking.setPolicyStatus(PolicyStatusCode.IN_FORCE);
        policyTracking.setPaymentFrequency(PremiumFrequencyType.MONTHLY);
        policyTracking.setPolicyNumber("44433332");
        return policyTracking;
    }

    @Test
    public void testProductNameForSuper() {
        PolicyTracking policyTracking = createTermLifeTrackingPolicy("254698789");
        List<PolicyTracking> policyTrackingList = new ArrayList<>();
        policyTrackingList.add(policyTracking);
        List<PolicyTrackingDto> policyTrackingDtoList = policyDtoConverter.policyTrackingDetailDtos(policyTrackingList, true);
        assertNotNull(policyTrackingDtoList);
        assertThat(1, is(policyTrackingDtoList.size()));
        assertNotNull(policyTrackingDtoList.get(0));
        Assert.assertEquals(((PolicySummaryDto) policyTrackingDtoList.get(0)).getProductName(), "BT Panorama Super");
    }

    @Test
    public void testProductNameForInv() {
        PolicyTracking policyTracking = createTermLifeTrackingPolicy("254686789");
        List<PolicyTracking> policyTrackingList = new ArrayList<>();
        policyTrackingList.add(policyTracking);
        List<PolicyTrackingDto> policyTrackingDtoList = policyDtoConverter.policyTrackingDetailDtos(policyTrackingList, true);
        assertNotNull(policyTrackingDtoList);
        assertThat(1, is(policyTrackingDtoList.size()));
        assertNotNull(policyTrackingDtoList.get(0));
        Assert.assertEquals(((PolicySummaryDto) policyTrackingDtoList.get(0)).getProductName(), "BT Panorama Investments");
    }


    public PolicyImpl createIncomeProtectionPolicy(String accountNumber) {
        PolicyImpl policy = new PolicyImpl();
        policy.setAccountNumber("254686789");
        policy.setPolicyType(PolicyType.INCOME_PROTECTION);
        policy.setPolicySubType(PolicySubType.BUSINESS_OVERHEAD);
        policy.setStatus(PolicyStatusCode.IN_FORCE);
        policy.setPolicyFrequency(PremiumFrequencyType.MONTHLY);
        policy.setPolicyNumber("44433332");
        return policy;
    }

    @Test
    public void testPolicyNameForBusinessOverhead() {
        PolicyImpl policy = createIncomeProtectionPolicy("254698789");
        List<Policy> policyList = new ArrayList<>();
        policyList.add(policy);
        List<PolicyDto> policyDtoList = policyDtoConverter.toPolicyDto("12354", policyList, null);
        assertNotNull(policyDtoList);
        assertThat(1, is(policyDtoList.size()));
        assertNotNull(policyDtoList.get(0));
        assertThat(PolicyType.BUSINESS_OVERHEAD, is(policyDtoList.get(0).getPolicyType()));
    }


    public PolicyTracking createIncomeProtectionTrackingPolicy(String accountNumber) {
        PolicyTrackingImpl policyTracking = new PolicyTrackingImpl();
        policyTracking.setAccountNumber("254686789");
        policyTracking.setPolicyType(PolicyType.INCOME_PROTECTION);
        policyTracking.setPolicySubType(PolicySubType.BUSINESS_OVERHEAD);
        policyTracking.setPolicyStatus(PolicyStatusCode.IN_FORCE);
        policyTracking.setPaymentFrequency(PremiumFrequencyType.MONTHLY);
        policyTracking.setPolicyNumber("44433332");
        return policyTracking;
    }

    @Test
    public void testPolicySubTypeForTracking() {
        PolicyTracking policyTracking = createIncomeProtectionTrackingPolicy("254698789");
        List<PolicyTracking> policyTrackingList = new ArrayList<>();
        policyTrackingList.add(policyTracking);
        List<PolicyTrackingDto> policyTrackingDtoList = policyDtoConverter.policyTrackingDetailDtos(policyTrackingList, true);
        assertNotNull(policyTrackingDtoList);
        assertThat(1, is(policyTrackingDtoList.size()));
        assertNotNull(policyTrackingDtoList.get(0));
        assertThat(PolicyType.BUSINESS_OVERHEAD.name(), equalTo(((PolicySummaryDto) policyTrackingDtoList.get(0)).getPolicyType()));
    }


}
