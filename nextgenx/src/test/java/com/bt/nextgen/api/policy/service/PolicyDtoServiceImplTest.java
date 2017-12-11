package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.api.beneficiary.service.BeneficiaryDtoService;
import com.bt.nextgen.api.beneficiary.service.BeneficiaryDtoServiceImpl;
import com.bt.nextgen.api.policy.model.BeneficiaryDto;
import com.bt.nextgen.api.policy.model.BenefitsDto;
import com.bt.nextgen.api.policy.model.BenefitsTypeDto;
import com.bt.nextgen.api.policy.model.LifeInsureDto;
import com.bt.nextgen.api.policy.model.PolicyDto;
import com.bt.nextgen.api.policy.model.PolicyKey;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.beneficiary.RelationshipType;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitOptionStatus;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitOptionType;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitOptionsImpl;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitType;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitsImpl;
import com.bt.nextgen.service.avaloq.insurance.model.OccupationClass;
import com.bt.nextgen.service.avaloq.insurance.model.PersonImpl;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyLifeImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.model.PolicySubType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyType;
import com.bt.nextgen.service.avaloq.insurance.model.PremiumFrequencyType;
import com.bt.nextgen.service.avaloq.insurance.model.PremiumType;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class PolicyDtoServiceImplTest {

    @InjectMocks
    PolicyDtoServiceImpl policyDtoService;

    @Mock
    PolicyIntegrationService policyIntegrationService;

    @Mock
    AccountIntegrationService accountIntegrationService;

    @Mock
    BeneficiaryDtoServiceImpl beneficiaryDetailsIntegrationService;

    @Mock
    PolicyUtility policyUtility;

    @Mock
    private BeneficiaryDtoService beneficiaryDtoService;


    List<Policy> policyDetails = new ArrayList<>();
    List<PolicyLifeImpl> policyLife = new ArrayList<>();


    @Before
    public void setUp() {
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();

        AccountKey accountKey1 = AccountKey.valueOf("32658"); //8A82B2403AE4ABCE1E585FA1AC269975BB84F3A8367B37F2
        AccountKey accountKey2 = AccountKey.valueOf("12364"); //5C27E369D8568B9B8294AAB906B1B97B2E047A856B071007
        AccountKey accountKey3 = AccountKey.valueOf("12384"); //42ACA82DACF8B0547E00EF153BC16B43D72E88F99509AB3A

        ClientKey clientKey1 = ClientKey.valueOf("23659");
        ClientKey clientKey2 = ClientKey.valueOf("23859");
        ClientKey clientKey3 = ClientKey.valueOf("23759");

        WrapAccountImpl wrapAccount1 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount2 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount3 = new WrapAccountImpl();

        wrapAccount1.setAccountKey(accountKey1);
        wrapAccount1.setAccountNumber("254698789");
        wrapAccount1.setAccountStructureType(AccountStructureType.SUPER);
        List<ClientKey> owners1 = new ArrayList<>();
        owners1.add(clientKey1);
        wrapAccount1.setAccountOwners(owners1);

        wrapAccount2.setAccountKey(accountKey2);
        wrapAccount2.setAccountNumber("254696789");
        wrapAccount2.setAccountStructureType(AccountStructureType.SMSF);
        List<ClientKey> owners2 = new ArrayList<>();
        owners2.add(clientKey1);
        owners2.add(clientKey2);
        wrapAccount2.setAccountOwners(owners2);

        wrapAccount3.setAccountKey(accountKey3);
        wrapAccount3.setAccountNumber("254686789");
        wrapAccount3.setAccountStructureType(AccountStructureType.Company);
        List<ClientKey> owners3 = new ArrayList<>();
        owners3.add(clientKey2);
        owners3.add(clientKey3);
        wrapAccount3.setAccountOwners(owners3);

        accountMap.put(accountKey1, wrapAccount1);
        accountMap.put(accountKey2, wrapAccount2);
        accountMap.put(accountKey3, wrapAccount3);


        PolicyImpl insurance1 = new PolicyImpl();
        PolicyImpl insurance2 = new PolicyImpl();
        PolicyImpl insurance3 = new PolicyImpl();

        List<PersonImpl> owners = new ArrayList<>();
        PersonImpl person1 = new PersonImpl();
        person1.setGivenName("Steve");
        person1.setLastName("Smith");
        person1.setBeneficiaryContribution(new BigDecimal(100));
        owners.add(person1);

        List<PersonImpl> beneficiary = new ArrayList<>();
        beneficiary.add(person1);

        PolicyLifeImpl policyLife1 = new PolicyLifeImpl();
        PolicyLifeImpl policyLife2 = new PolicyLifeImpl();
        PolicyLifeImpl policyLife3 = new PolicyLifeImpl();

        List<BenefitsImpl> benefits = new ArrayList<>();
        BenefitsImpl benefit1 = new BenefitsImpl();
        benefit1.setBenefitType(BenefitType.DEATH);
        benefit1.setOccupationClass(OccupationClass.H);
        benefit1.setPremiumType(PremiumType.OTHER);
        BenefitsImpl benefit2 = new BenefitsImpl();
        benefit2.setBenefitType(BenefitType.TPD);
        benefits.add(benefit1);
        benefits.add(benefit2);

        List<BenefitOptionsImpl> benefitOptions = new ArrayList<>();

        BenefitOptionsImpl benefitOptions1 = new BenefitOptionsImpl();
        BenefitOptionsImpl benefitOptions2 = new BenefitOptionsImpl();

        //BenefitOptionType are set through BenefitType enum code logic, The values entered here are relevant only for benefitoptionstatus flag
        benefitOptions1.setBenefitOptions(BenefitOptionType.DEATH_BUSINESS_COVER);
        benefitOptions1.setBenefitOptionStatus(BenefitOptionStatus.InForce);
        benefitOptions2.setBenefitOptions(BenefitOptionType.TPD_BUSINESS_COVER);
        benefitOptions2.setBenefitOptionStatus(BenefitOptionStatus.Cancelled);
        BenefitOptionType deathBusinessCover = BenefitOptionType.DEATH_BUSINESS_COVER;
        deathBusinessCover.setStatus(true);
        benefitOptions.add(benefitOptions1);
        benefitOptions.add(benefitOptions2);

        policyLife1.setGivenName("Steve");
        policyLife1.setLastName("Smith");
        policyLife1.setBenefits(benefits);
        policyLife1.setBenefitOptions(benefitOptions);

        policyLife2.setGivenName("Mark");
        policyLife2.setLastName("Parker");
        policyLife2.setBenefits(benefits);
        policyLife2.setBenefitOptions(benefitOptions);
        policyLife3.setGivenName("Oberian");
        policyLife3.setBenefits(benefits);
        policyLife3.setBenefitOptions(benefitOptions);
        policyLife.add(policyLife1);
        policyLife.add(policyLife2);
        policyLife.add(policyLife3);


        List<PolicyLifeImpl> policyLifeBOs = new ArrayList<>();
        PolicyLifeImpl policyLifeBO = new PolicyLifeImpl();
        List<BenefitOptionsImpl> benefitOptionsEmpty = new ArrayList<>();
        policyLifeBO.setBenefitOptions(benefitOptionsEmpty);
        List<BenefitsImpl> benefitsBO = new ArrayList<>();
        BenefitsImpl benefit = new BenefitsImpl();
        benefit.setBenefitType(BenefitType.BUSINESS_OVERHEAD);
        benefitsBO.add(benefit);
        policyLifeBO.setGivenName("Mark");
        policyLifeBO.setLastName("Smith");
        policyLifeBO.setBenefits(benefitsBO);

        policyLifeBOs.add(policyLifeBO);


        insurance1.setPolicyNumber("CF506182");
        insurance1.setPolicyType(PolicyType.INCOME_PROTECTION);
        insurance1.setPolicySubType(PolicySubType.BUSINESS_OVERHEAD);
        insurance1.setAccountNumber("254698789");
        insurance1.setPolicyFrequency(PremiumFrequencyType.YEARLY);
        insurance1.setOwners(owners);
        insurance1.setStatus(PolicyStatusCode.IN_FORCE);
        insurance1.setPolicyLifes(policyLifeBOs);


        insurance2.setPolicyNumber("CL898278");
        insurance2.setPolicyType(PolicyType.TERM_LIFE);
        insurance2.setAccountNumber("254696789");
        insurance2.setPolicyFrequency(PremiumFrequencyType.MONTHLY);
        insurance2.setOwners(owners);
        insurance2.setBeneficiaries(beneficiary);
        insurance2.setStatus(PolicyStatusCode.IN_FORCE);
        insurance2.setPolicyLifes(policyLife);

        insurance3.setPolicyNumber("CM287468");
        insurance3.setPolicyType(PolicyType.STAND_ALONE_TPD);
        insurance3.setAccountNumber("254686789");
        insurance3.setPolicyFrequency(PremiumFrequencyType.MONTHLY);
        insurance3.setOwners(owners);
        insurance3.setStatus(PolicyStatusCode.IN_FORCE);
        insurance3.setBeneficiaries(beneficiary);
        insurance3.setPolicyLifes(policyLife);

        policyDetails.add(insurance1);
        policyDetails.add(insurance2);
        policyDetails.add(insurance3);

        List<Beneficiary> beneficiaries = new ArrayList<>();
        Beneficiary beneficiary1 = new Beneficiary();
        beneficiary1.setFirstName("Abhinav");
        beneficiary1.setLastName("Gupta");
        beneficiary1.setAllocationPercent("100");
        beneficiary1.setRelationshipType(RelationshipType.SPOUSE.getName());
        beneficiaries.add(beneficiary1);
        List<com.bt.nextgen.api.beneficiary.model.BeneficiaryDto> beneficiaryDetailList = getBeneficiaryDto(null, "100", beneficiaries);

        PolicyDtoConverter dtoConverter = new PolicyDtoConverter(accountMap);

        Mockito.when(policyIntegrationService.retrievePoliciesByAccountNumber(Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(policyDetails);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers((ServiceErrors) Matchers.anyObject())).thenReturn(accountMap);
        Mockito.when(beneficiaryDetailsIntegrationService.getBeneficiaryDetails(Mockito.any(com.bt.nextgen.api.account.v3.model.AccountKey.class), Mockito.any(ServiceErrors.class), Matchers.anyString())).thenReturn(beneficiaryDetailList);
        Mockito.when(policyUtility.getPolicyDtoConverter((ServiceErrors) Matchers.anyObject())).thenReturn(dtoConverter);

    }


    private static List<com.bt.nextgen.api.beneficiary.model.BeneficiaryDto> getBeneficiaryDto(DateTime dateTime, String totalAllocation, List<Beneficiary> beneficiaryList) {
        List<com.bt.nextgen.api.beneficiary.model.BeneficiaryDto> beneficiaryDtos = new ArrayList<>();
        com.bt.nextgen.api.beneficiary.model.BeneficiaryDto beneficiaryDto = new com.bt.nextgen.api.beneficiary.model.BeneficiaryDto();
        beneficiaryDto.setBeneficiariesLastUpdatedTime(dateTime);
        beneficiaryDto.setTotalAllocationPercent(totalAllocation);
        beneficiaryDto.setBeneficiaries(beneficiaryList);
        beneficiaryDtos.add(beneficiaryDto);
        return beneficiaryDtos;
    }

    @Test
    public void testSearchWithValidAccountNumbers() {
        PolicyKey policyKey = new PolicyKey();
        policyKey.setAccountId("7E3C27BF5455E73B0812128BB67B940633168E37702DCB02");
        List<PolicyDto> policyDtos = policyDtoService.search(policyKey, new ServiceErrorsImpl());

        Assert.assertNotNull(policyDtos);
        Assert.assertEquals(3, policyDtos.size());

        PolicyDto policyDto1 = policyDtos.get(0);//1st policy

        Assert.assertEquals(PolicyType.BUSINESS_OVERHEAD, policyDto1.getPolicyType());//Check for BUSINESS_OVERHEAD policy
        Assert.assertEquals("CF506182", policyDto1.getPolicyNumber());
        Assert.assertEquals(PremiumFrequencyType.YEARLY.name(), policyDto1.getPolicyFrequency());
        Assert.assertEquals("Panorama Super", policyDto1.getPaymentMethod());

        Assert.assertNull(policyDto1.getNominatedBenificiaries());

        LifeInsureDto lifeInsureDto = (LifeInsureDto) policyDto1.getPersonBenefitDetails().get(0);
        Assert.assertEquals("Mark", lifeInsureDto.getGivenName());

        BenefitsDto benefitsDto1 = lifeInsureDto.getBenefits().get(0);
        Assert.assertEquals(BenefitType.BUSINESS_OVERHEAD, benefitsDto1.getBenefitType());
        Assert.assertEquals(0, benefitsDto1.getBenefitOptions().size());
        //BenefitOptionDto benefitOptionDto = benefitsDto1.getBenefitOptions().get(0);

/*
        LifeInsureDto lifeInsureDto1 = (LifeInsureDto)policyDto1.getPersonBenefitDetails().get(1);
        Assert.assertEquals("Oberian", lifeInsureDto1.getGivenName());
*/

        PolicyDto policyDto2 = policyDtos.get(1);//2nd policy

        Assert.assertEquals(PolicyType.TERM_LIFE, policyDto2.getPolicyType());
        Assert.assertEquals("CL898278", policyDto2.getPolicyNumber());
        Assert.assertEquals(PremiumFrequencyType.MONTHLY.name(), policyDto2.getPolicyFrequency());
        //Assert.assertEquals("Panorama Investment", policyDto2.getPaymentMethod());

        Assert.assertNotNull(policyDto2.getNominatedBenificiaries());

        lifeInsureDto = (LifeInsureDto) policyDto2.getPersonBenefitDetails().get(0);
        Assert.assertEquals("Mark", lifeInsureDto.getGivenName());

        BenefitsDto benefitsDto = lifeInsureDto.getBenefits().get(0);
        Assert.assertEquals(BenefitType.DEATH, benefitsDto.getBenefitType());
        Assert.assertEquals("DEATH_BUSINESS_COVER", benefitsDto.getBenefitOptions().get(0).getBenefitOptionType());
        Assert.assertTrue(benefitsDto.getBenefitOptions().get(0).isStatus());
        BenefitsTypeDto benefitsTypeDto = benefitsDto.getBenefits().get(0);
        Assert.assertEquals("Other", benefitsTypeDto.getOccupationClass());
        Assert.assertEquals(PremiumType.OTHER, benefitsTypeDto.getPremiumStructure());


        BenefitsDto benefitsDto2 = lifeInsureDto.getBenefits().get(1);
        Assert.assertEquals(BenefitType.TPD, benefitsDto2.getBenefitType());
        Assert.assertEquals(3, benefitsDto2.getBenefitOptions().size());//Received required BenefitOption where service sending one //WAIVER_LIFE_PREMIUM removed as termlife and super account
        Assert.assertFalse(benefitsDto2.getBenefitOptions().get(0).isStatus());

        PolicyDto policyDto3 = policyDtos.get(2);//3rd policy

        Assert.assertEquals(PolicyType.STAND_ALONE_TPD, policyDto3.getPolicyType());
        Assert.assertEquals("CM287468", policyDto3.getPolicyNumber());
        Assert.assertEquals(PremiumFrequencyType.MONTHLY.name(), policyDto3.getPolicyFrequency());
        //Assert.assertEquals("Panorama Investment", policyDto3.getPaymentMethod());

        Assert.assertNotNull(policyDto2.getNominatedBenificiaries());

        lifeInsureDto = (LifeInsureDto) policyDto3.getPersonBenefitDetails().get(0);
        Assert.assertEquals("Mark", lifeInsureDto.getGivenName());

        BenefitsDto benefitsDto3 = lifeInsureDto.getBenefits().get(1);
        Assert.assertEquals(BenefitType.TPD, benefitsDto3.getBenefitType());
        Assert.assertEquals(0, benefitsDto3.getBenefitOptions().size());//No benefitoptions incase of Stand Alone TPD

    }

    @Test
    public void testSearchWithInvalidAccountNumber() {
        PolicyImpl insurance = new PolicyImpl();
        insurance.setPolicyNumber("CF506182");
        insurance.setPolicyType(PolicyType.INCOME_PROTECTION);
        insurance.setPolicySubType(PolicySubType.BUSINESS_OVERHEAD);
        insurance.setAccountNumber("254686789");
        insurance.setStatus(PolicyStatusCode.IN_FORCE);
        insurance.setPolicyFrequency(PremiumFrequencyType.YEARLY);
        insurance.setPolicyLifes(policyLife);
        policyDetails.add(insurance);

        Mockito.when(policyIntegrationService.retrievePoliciesByAccountNumber(Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(policyDetails);

        PolicyKey policyKey = new PolicyKey();
        policyKey.setAccountId("7E3C27BF5455E73B0812128BB67B940633168E37702DCB02");
        List<PolicyDto> policyDtos = policyDtoService.search(policyKey, new ServiceErrorsImpl());
        Assert.assertNotNull(policyDtos);
        Assert.assertEquals(4, policyDtos.size());
    }

    @Test
    public void testSearchWithPolicyType() {
        // Test case to test benefit type (INCOME_PROTECTION_PLUS) for policy type INCOME_PROTECTION_PLUS
        List<Policy> policyDetails = new ArrayList<>();
        List<PolicyLifeImpl> policyLife = new ArrayList<>();
        PolicyLifeImpl policyLife1 = new PolicyLifeImpl();
        List<BenefitOptionsImpl> benefitOptions = new ArrayList<>();
        BenefitOptionsImpl benefitOptions1 = new BenefitOptionsImpl();
        benefitOptions1.setBenefitOptionStatus(BenefitOptionStatus.InForce);
        benefitOptions1.setBenefitOptions(BenefitOptionType.ACCIDENT);
        benefitOptions.add(benefitOptions1);
        BenefitsImpl benefit1 = new BenefitsImpl();
        List<BenefitsImpl> benefits = new ArrayList<>();
        benefit1.setBenefitType(BenefitType.INCOME_PROTECTION);
        benefits.add(benefit1);
        policyLife1.setBenefits(benefits);
        policyLife1.setBenefitOptions(benefitOptions);
        policyLife.add(policyLife1);

        PolicyImpl insurance = new PolicyImpl();
        insurance.setPolicyNumber("CF506182");
        insurance.setPolicyType(PolicyType.INCOME_PROTECTION_PLUS);
        insurance.setAccountNumber("254686789");
        insurance.setStatus(PolicyStatusCode.IN_FORCE);
        insurance.setPolicyFrequency(PremiumFrequencyType.YEARLY);
        insurance.setPolicyLifes(policyLife);
        policyDetails.add(insurance);

        Mockito.when(policyIntegrationService.retrievePoliciesByAccountNumber(Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(policyDetails);

        PolicyKey policyKey = new PolicyKey();
        policyKey.setAccountId("7E3C27BF5455E73B0812128BB67B940633168E37702DCB02");
        List<PolicyDto> policyDtos = policyDtoService.search(policyKey, new ServiceErrorsImpl());
        Assert.assertNotNull(policyDtos);
        Assert.assertEquals(1, policyDtos.size());
        PolicyDto policyDto2 = policyDtos.get(0);

        Assert.assertEquals(PolicyType.INCOME_PROTECTION_PLUS, policyDto2.getPolicyType());
        Assert.assertEquals("CF506182", policyDto2.getPolicyNumber());
        Assert.assertEquals(PremiumFrequencyType.YEARLY.name(), policyDto2.getPolicyFrequency());
        Assert.assertEquals("254686789", policyDto2.getAccountNumber());

        LifeInsureDto lifeInsureDto = (LifeInsureDto) policyDto2.getPersonBenefitDetails().get(0);
        BenefitsDto benefitsDto = lifeInsureDto.getBenefits().get(0);
        Assert.assertEquals(BenefitType.INCOME_PROTECTION_PLUS, benefitsDto.getBenefitType());
        Assert.assertEquals("ACCIDENT", benefitsDto.getBenefitOptions().get(0).getBenefitOptionType());
        Assert.assertTrue(benefitsDto.getBenefitOptions().get(0).isStatus());
    }


    @Test
    public void testSearchPolicywithBeneficiariesForSuperAccount() {
        // Test case to test Beneficiaries returned via CLOAS
        List<Policy> policyDetails = new ArrayList<>();
        PolicyImpl insurance = new PolicyImpl();
        insurance.setPolicyNumber("CF506182");
        insurance.setPolicyType(PolicyType.TERM_LIFE);
        insurance.setAccountNumber("254698789");
        insurance.setStatus(PolicyStatusCode.IN_FORCE);
        insurance.setPolicyFrequency(PremiumFrequencyType.YEARLY);
        policyDetails.add(insurance);

        //Mock Beneficiaries returned from avaloq
        List<Beneficiary> beneficiaries = new ArrayList<>();
        Beneficiary beneficiary1 = new Beneficiary();
        beneficiary1.setFirstName("Abhinav");
        beneficiary1.setLastName("Gupta");
        beneficiary1.setAllocationPercent("100");
        beneficiary1.setRelationshipType(RelationshipType.LPR.getAvaloqInternalId());
        beneficiaries.add(beneficiary1);
        List<com.bt.nextgen.api.beneficiary.model.BeneficiaryDto> avaloqBeneficiaryDetailsList = getBeneficiaryDto(null, "100", beneficiaries);

        Mockito.when(policyIntegrationService.retrievePoliciesByAccountNumber(Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(policyDetails);
        Mockito.when(beneficiaryDetailsIntegrationService.getBeneficiaryDetails(Mockito.any(com.bt.nextgen.api.account.v3.model.AccountKey.class), Mockito.any(ServiceErrors.class), Matchers.anyString())).thenReturn(avaloqBeneficiaryDetailsList);

        PolicyKey policyKey = new PolicyKey();
        policyKey.setAccountId("8A82B2403AE4ABCE1E585FA1AC269975BB84F3A8367B37F2"); // Mock Account Id = 12384 set in account map (super account)
        List<PolicyDto> policyDtos = policyDtoService.search(policyKey, new ServiceErrorsImpl());
        Assert.assertNotNull(policyDtos);
        Assert.assertEquals(1, policyDtos.size());
        PolicyDto policyDto2 = policyDtos.get(0);

        Assert.assertEquals(PolicyType.TERM_LIFE, policyDto2.getPolicyType());
        Assert.assertEquals("CF506182", policyDto2.getPolicyNumber());
        Assert.assertEquals(PremiumFrequencyType.YEARLY.name(), policyDto2.getPolicyFrequency());
        Assert.assertEquals("254698789", policyDto2.getAccountNumber());

    }

    @Test
    public void testSearchPolicywithBeneficiariesForNonSuperAccount() {
        // Test case to test Beneficiaries returned via avaloq for super accounts
        List<Policy> policyDetails = new ArrayList<>();
        PersonImpl person1 = new PersonImpl();
        person1.setBeneficiaryContribution(new BigDecimal(100));
        person1.setGivenName("Steve");
        person1.setLastName("Smith");
        List<PersonImpl> beneficiaryDetails = new ArrayList<>();
        beneficiaryDetails.add(person1);

        PolicyImpl insurance2 = new PolicyImpl();
        insurance2.setPolicyNumber("CF506183");
        insurance2.setPolicyType(PolicyType.TERM_LIFE);
        insurance2.setAccountNumber("254696789");
        insurance2.setStatus(PolicyStatusCode.IN_FORCE);
        insurance2.setPolicyFrequency(PremiumFrequencyType.YEARLY);
        insurance2.setBeneficiaries(beneficiaryDetails);

        policyDetails.add(insurance2);

        //Mock Beneficiaries returned from avaloq
        List<Beneficiary> beneficiaries = new ArrayList<>();
        Beneficiary beneficiary1 = new Beneficiary();
        beneficiary1.setFirstName("Abhinav");
        beneficiary1.setLastName("Gupta");
        beneficiary1.setAllocationPercent("100");
        beneficiary1.setRelationshipType(RelationshipType.CHILD.getAvaloqInternalId());
        beneficiaries.add(beneficiary1);
        List<com.bt.nextgen.api.beneficiary.model.BeneficiaryDto> avaloqBeneficiaryDetailsList = getBeneficiaryDto(null, "100", beneficiaries);

        Mockito.when(policyIntegrationService.retrievePoliciesByAccountNumber(Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(policyDetails);
        Mockito.when(beneficiaryDetailsIntegrationService.getBeneficiaryDetails(Mockito.any(com.bt.nextgen.api.account.v3.model.AccountKey.class), Mockito.any(ServiceErrors.class), Matchers.anyString())).thenReturn(avaloqBeneficiaryDetailsList);

        PolicyKey policyKey = new PolicyKey();
        policyKey.setAccountId("5C27E369D8568B9B8294AAB906B1B97B2E047A856B071007"); // Mock Account Id = 32658 set in account map (super account)
        List<PolicyDto> policyDtos = policyDtoService.search(policyKey, new ServiceErrorsImpl());
        Assert.assertNotNull(policyDtos);
        Assert.assertEquals(1, policyDtos.size());
        PolicyDto policyDto2 = policyDtos.get(0);

        Assert.assertEquals(PolicyType.TERM_LIFE, policyDto2.getPolicyType());
        Assert.assertEquals("CF506183", policyDto2.getPolicyNumber());
        Assert.assertEquals(PremiumFrequencyType.YEARLY.name(), policyDto2.getPolicyFrequency());
        Assert.assertEquals("254696789", policyDto2.getAccountNumber());
        Assert.assertEquals(1, policyDto2.getNominatedBenificiaries().size());
        BeneficiaryDto person = (BeneficiaryDto) policyDto2.getNominatedBenificiaries().get(0);
        Assert.assertEquals("Steve", person.getGivenName());
        Assert.assertEquals("Smith", person.getLastName());
        Assert.assertEquals(new BigDecimal(100), person.getBeneficiaryContribution());
    }

    @Test
    public void testFindPolicywithBeneficiariesForSuperAccount() {
        // Test case to test Beneficiaries returned via CLOAS
        List<Policy> policyDetails = new ArrayList<>();
        PolicyImpl insurance = new PolicyImpl();
        insurance.setPolicyNumber("CF506182");
        insurance.setPolicyType(PolicyType.TERM_LIFE);
        insurance.setAccountNumber("254698789");
        insurance.setStatus(PolicyStatusCode.IN_FORCE);
        insurance.setPolicyFrequency(PremiumFrequencyType.YEARLY);
        policyDetails.add(insurance);

        //Mock Beneficiaries returned from avaloq
        List<Beneficiary> beneficiaries = new ArrayList<>();
        Beneficiary beneficiary1 = new Beneficiary();
        beneficiary1.setFirstName("Abhinav");
        beneficiary1.setLastName("Gupta");
        beneficiary1.setAllocationPercent("100");
        beneficiary1.setRelationshipType(RelationshipType.CHILD.getAvaloqInternalId());
        beneficiaries.add(beneficiary1);
        List<com.bt.nextgen.api.beneficiary.model.BeneficiaryDto> avaloqBeneficiaryDetailsList = getBeneficiaryDto(null, "100", beneficiaries);

        Mockito.when(policyIntegrationService.retrievePolicyByPolicyNumber(Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(policyDetails);
        Mockito.when(beneficiaryDetailsIntegrationService.getBeneficiaryDetails(Mockito.any(com.bt.nextgen.api.account.v3.model.AccountKey.class), Mockito.any(ServiceErrors.class), Matchers.anyString())).thenReturn(avaloqBeneficiaryDetailsList);

        PolicyKey policyKey = new PolicyKey();
        policyKey.setAccountId("8A82B2403AE4ABCE1E585FA1AC269975BB84F3A8367B37F2"); // Mock Account Id = 12384 set in account map (super account)
        PolicyDto policyDto2 = policyDtoService.find(policyKey, new ServiceErrorsImpl());
        Assert.assertNotNull(policyDto2);
/*
        Assert.assertEquals(1, policyDtos.size());
        PolicyDto policyDto2 = policyDtos.get(0);
*/

        Assert.assertEquals(PolicyType.TERM_LIFE, policyDto2.getPolicyType());
        Assert.assertEquals("CF506182", policyDto2.getPolicyNumber());
        Assert.assertEquals("-", policyDto2.getPortfolioNumber());
        Assert.assertEquals(PremiumFrequencyType.YEARLY.name(), policyDto2.getPolicyFrequency());
        Assert.assertEquals("254698789", policyDto2.getAccountNumber());
    }

    @Test
    public void testFindPolicywithBeneficiariesForNonSuperAccount() {
        // Test case to test Beneficiaries returned via avaloq for super accounts
        List<Policy> policyDetails = new ArrayList<>();
        PersonImpl person1 = new PersonImpl();
        person1.setBeneficiaryContribution(new BigDecimal(100));
        person1.setGivenName("Steve");
        person1.setLastName("Smith");
        List<PersonImpl> beneficiaryDetails = new ArrayList<>();
        beneficiaryDetails.add(person1);

        PolicyImpl insurance2 = new PolicyImpl();
        insurance2.setPolicyNumber("CF506183");
        insurance2.setPolicyType(PolicyType.TERM_LIFE);
        insurance2.setAccountNumber("254696789");
        insurance2.setPortfolioNumber("B0000005A");
        insurance2.setStatus(PolicyStatusCode.IN_FORCE);
        insurance2.setPolicyFrequency(PremiumFrequencyType.YEARLY);
        insurance2.setBeneficiaries(beneficiaryDetails);

        policyDetails.add(insurance2);

        //Mock Beneficiaries returned from avaloq
        List<Beneficiary> beneficiaries = new ArrayList<>();
        Beneficiary beneficiary1 = new Beneficiary();
        beneficiary1.setFirstName("Abhinav");
        beneficiary1.setLastName("Gupta");
        beneficiary1.setAllocationPercent("100");
        beneficiary1.setRelationshipType(RelationshipType.CHILD.getAvaloqInternalId());
        beneficiaries.add(beneficiary1);
        List<com.bt.nextgen.api.beneficiary.model.BeneficiaryDto> avaloqBeneficiaryDetailsList = getBeneficiaryDto(null, "100", beneficiaries);

        Mockito.when(policyIntegrationService.retrievePolicyByPolicyNumber(Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(policyDetails);
        Mockito.when(beneficiaryDetailsIntegrationService.getBeneficiaryDetails(Mockito.any(com.bt.nextgen.api.account.v3.model.AccountKey.class), Mockito.any(ServiceErrors.class), Matchers.anyString())).thenReturn(avaloqBeneficiaryDetailsList);

        PolicyKey policyKey = new PolicyKey();
        policyKey.setAccountId("5C27E369D8568B9B8294AAB906B1B97B2E047A856B071007"); // Mock Account Id = 32658 set in account map (super account)
        PolicyDto policyDto2 = policyDtoService.find(policyKey, new ServiceErrorsImpl());
        Assert.assertNotNull(policyDto2);

        Assert.assertEquals(PolicyType.TERM_LIFE, policyDto2.getPolicyType());
        Assert.assertEquals("CF506183", policyDto2.getPolicyNumber());
        Assert.assertEquals(PremiumFrequencyType.YEARLY.name(), policyDto2.getPolicyFrequency());
        Assert.assertEquals("254696789", policyDto2.getAccountNumber());
        Assert.assertEquals("B0000005-A", policyDto2.getPortfolioNumber());
        Assert.assertEquals(1, policyDto2.getNominatedBenificiaries().size());
        BeneficiaryDto person = (BeneficiaryDto) policyDto2.getNominatedBenificiaries().get(0);
        Assert.assertEquals("Steve", person.getGivenName());
        Assert.assertEquals("Smith", person.getLastName());
        Assert.assertEquals(new BigDecimal(100), person.getBeneficiaryContribution());
    }
}
