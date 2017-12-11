package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.broker.model.BrokerKey;
import com.bt.nextgen.api.policy.model.PolicySummaryDto;
import com.bt.nextgen.api.policy.model.PolicyTrackingDto;
import com.bt.nextgen.api.userpreference.model.UserPreferenceDto;
import com.bt.nextgen.api.userpreference.model.UserPreferenceDtoKey;
import com.bt.nextgen.api.userpreference.service.UserPreferenceDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.util.StringUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerAnnotationHolder;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerUserImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PaymentType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyApplications;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyApplicationsImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.model.PolicySubType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTracking;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTrackingImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyType;
import com.bt.nextgen.service.avaloq.insurance.model.PremiumFrequencyType;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.bt.nextgen.web.controller.cash.util.Attribute;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class PolicySummaryDtoServiceImplTest {

    @InjectMocks
    private PolicySummaryDtoServiceImpl policySummaryDtoService;

    @Mock
    private PolicyIntegrationService policyIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private UserPreferenceDtoService userPreferenceDtoService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private PolicyUserPreferencesService policyUserPreferencesService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private PolicyUtility policyUtility;

    private List<PolicyTracking> policyTrackings = new ArrayList<>();

    @Before
    public void setUp() {

        PolicyTrackingImpl policyTracking1 = new PolicyTrackingImpl();
        PolicyTrackingImpl policyTracking2 = new PolicyTrackingImpl();

        policyTracking1.setFNumber("F0467807");
        policyTracking1.setPolicyNumber("CF000001");
        policyTracking1.setPolicyType(PolicyType.INCOME_LINKING_PLUS);
        policyTracking1.setPremium(new BigDecimal(0));
        policyTracking1.setProposedPremium(new BigDecimal(50));
        policyTracking1.setPaymentFrequency(PremiumFrequencyType.YEARLY);
        policyTracking1.setPolicyStatus(PolicyStatusCode.IN_FORCE);
        policyTracking1.setAccountNumber("254698789");

        policyTracking2.setFNumber("F0484681");
        policyTracking2.setPolicyNumber("CF142292");
        policyTracking2.setPolicyType(PolicyType.INCOME_PROTECTION);
        policyTracking2.setPremium(new BigDecimal(0));
        policyTracking2.setProposedPremium(new BigDecimal(0));
        policyTracking2.setPaymentFrequency(PremiumFrequencyType.MONTHLY);
        policyTracking2.setPolicyStatus(PolicyStatusCode.PROPOSAL);
        policyTracking2.setAccountNumber("254698789");


        policyTrackings.add(policyTracking1);
        policyTrackings.add(policyTracking2);

        List<Broker> brokers = new ArrayList<>();
        BrokerAnnotationHolder brokerAnnotationHolder = new BrokerAnnotationHolder();
        //brokers.add(brokerAnnotationHolder);
//Added brokerimpl to set Adviser PPid instead of using BrokerAnnotationHolder
        BrokerImpl broker = new BrokerImpl(com.bt.nextgen.service.integration.broker.BrokerKey.valueOf("12345"), BrokerType.ADVISER);
        broker.setAdviserPPid("123456");
        brokers.add(broker);

        BrokerUserImpl brokerUser = new BrokerUserImpl(UserKey.valueOf("201613675"));

        List<UserPreferenceDto> userPreferenceDtos = new ArrayList<>();
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setValue("F0467807");
        userPreferenceDtos.add(userPreferenceDto);

        Mockito.when(brokerIntegrationService.getBrokersForJob((JobProfileIdentifier) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject())).thenReturn(brokers);
        Mockito.when(brokerIntegrationService.getAdviserBrokerUser((com.bt.nextgen.service.integration.broker.BrokerKey) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject())).thenReturn(brokerUser);
        Mockito.when(brokerIntegrationService.getBroker((com.bt.nextgen.service.integration.broker.BrokerKey) Matchers.anyObject(),
                (ServiceErrors) Matchers.anyObject())).thenReturn(broker);
        Mockito.when(policyIntegrationService.getFNumbers(Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(policyTrackings);
        Mockito.when(userPreferenceDtoService.search((UserPreferenceDtoKey) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject())).thenReturn(userPreferenceDtos);
        Mockito.when(policyIntegrationService.getPoliciesForAdviser(Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(policyTrackings);
        Mockito.when(policyUserPreferencesService.findLastAccessedAdviser()).thenReturn("122CCEC4B0FB19C0");
        Mockito.when(policyUserPreferencesService.findLastAccessedFNumber()).thenReturn("F0467807");
        Mockito.when(policyUtility.getAdviserPpId(Matchers.anyString(),(ServiceErrors)Matchers.anyObject())).thenReturn("123456");
    }

    @Test
    public void testSearch() {
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers((ServiceErrors) Matchers.anyObject())).thenReturn(getAccountMap());
        Mockito.when(productIntegrationService.loadProductsMap((ServiceErrors) Matchers.anyObject())).thenReturn(getProductMap());

        List<ApiSearchCriteria> searchCriterias = new ArrayList<>();
        searchCriterias.add(new ApiSearchCriteria(Attribute.INSURANCE_FNUMBER, "F0467807"));
        List<PolicyTrackingDto> trackingDtos = policySummaryDtoService.search(searchCriterias, new ServiceErrorsImpl());
        Assert.assertNotNull(trackingDtos);
        Assert.assertTrue(trackingDtos.size() > 0);

        PolicySummaryDto trackingDto = (PolicySummaryDto) trackingDtos.get(0);
        PolicyTracking policyTracking = policyTrackings.get(0);

        Assert.assertEquals(policyTracking.getPolicyType().name(), trackingDto.getPolicyType());
        Assert.assertEquals(policyTracking.getPolicyStatus().name(), trackingDto.getPolicyStatus());
        Assert.assertEquals(policyTracking.getPaymentFrequency().name(), trackingDto.getPaymentFrequency());
        Assert.assertEquals(new BigDecimal(50), trackingDto.getPremium());
    }

    @Test
    public void testFind() {
        PolicyTrackingDto trackingDto = policySummaryDtoService.find(new BrokerKey("122CCEC4B0FB19C0"), new ServiceErrorsImpl());
        Assert.assertNotNull(trackingDto);
        Assert.assertEquals("F0467807", trackingDto.getLastSelectedFNumber());
        Assert.assertNotNull(trackingDto.getFNumberList());
        Assert.assertTrue(trackingDto.getFNumberList().size() > 0);
    }

    @Test
    public void testFindOne() {
        //Adviser
        Mockito.when(userProfileService.isAdviser()).thenReturn(true);

        PolicyTrackingDto trackingDto = policySummaryDtoService.findOne(new ServiceErrorsImpl());
        Assert.assertNotNull(trackingDto);
        Assert.assertEquals("F0467807", trackingDto.getLastSelectedFNumber());
        Assert.assertNotNull(trackingDto.getFNumberList());
        Assert.assertTrue(trackingDto.getFNumberList().size() > 0);

        //Support staff with single adviser
        Mockito.when(userProfileService.isAdviser()).thenReturn(false);
        trackingDto = policySummaryDtoService.findOne(new ServiceErrorsImpl());
        Assert.assertNotNull(trackingDto);
        Assert.assertEquals("F0467807", trackingDto.getLastSelectedFNumber());
        Assert.assertNotNull(trackingDto.getFNumberList());
        Assert.assertTrue(trackingDto.getFNumberList().size() > 0);

        //Support staff with multiple adviser
        List<Broker> brokers = new ArrayList<>();
        //Added brokerimpl to set Adviser PPid instead of using BrokerAnnotationHolder
        BrokerImpl broker1 = new BrokerImpl(com.bt.nextgen.service.integration.broker.BrokerKey.valueOf("12345"), BrokerType.ADVISER);
        broker1.setAdviserPPid("123456");

        BrokerImpl broker2 = new BrokerImpl(com.bt.nextgen.service.integration.broker.BrokerKey.valueOf("12346"), BrokerType.ADVISER);
        broker1.setAdviserPPid("123457");
        brokers.add(broker2);
        Mockito.when(brokerIntegrationService.getBrokersForJob((JobProfileIdentifier) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject())).thenReturn(brokers);

        Mockito.when(policyUtility.getAdviserPpId(Matchers.anyString(), (ServiceErrors)Matchers.anyObject())).thenReturn(null);
        trackingDto = policySummaryDtoService.findOne(new ServiceErrorsImpl());
        Assert.assertNotNull(trackingDto);
        Assert.assertTrue(trackingDto.getFNumberList().size() == 0);

        //Support staff with null list
        brokers = null;
        Mockito.when(brokerIntegrationService.getBrokersForJob((JobProfileIdentifier) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject())).thenReturn(brokers);

        trackingDto = policySummaryDtoService.findOne(new ServiceErrorsImpl());
        Assert.assertNotNull(trackingDto);
        Assert.assertTrue(trackingDto.getFNumberList().size() == 0);

        //Support staff with no advisers
        brokers = new ArrayList<>();
        Mockito.when(brokerIntegrationService.getBrokersForJob((JobProfileIdentifier) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject())).thenReturn(brokers);

        trackingDto = policySummaryDtoService.findOne(new ServiceErrorsImpl());
        Assert.assertNotNull(trackingDto);
        Assert.assertTrue(trackingDto.getFNumberList().size() == 0);
    }

    private List<PolicyApplications> getPolicyApplications() {
        List<PolicyApplications> policyApplications = new ArrayList<>();
        PolicyApplicationsImpl PolicyApplications1 = new PolicyApplicationsImpl();
        PolicyApplications1.setCustomerNumber("32658"); //2514260
        PolicyApplications1.setPolicyNumber("Y0202713");

        PolicyApplicationsImpl PolicyApplications2 = new PolicyApplicationsImpl();
        PolicyApplications2.setCustomerNumber("32658"); //2514260
        PolicyApplications2.setPolicyNumber("YF202713");

        PolicyApplicationsImpl PolicyApplications3 = new PolicyApplicationsImpl();
        PolicyApplications3.setCustomerNumber("32658"); //2514260
        PolicyApplications3.setPolicyNumber("C2000001");

        PolicyApplicationsImpl PolicyApplications4 = new PolicyApplicationsImpl();
        PolicyApplications4.setCustomerNumber("32658"); //2514260
        PolicyApplications4.setPolicyNumber("C2000002");

        PolicyApplicationsImpl PolicyApplications5 = new PolicyApplicationsImpl();
        PolicyApplications5.setCustomerNumber("32658"); //2514260
        PolicyApplications5.setPolicyNumber("C2000003");

        PolicyApplicationsImpl PolicyApplications6 = new PolicyApplicationsImpl();
        PolicyApplications6.setCustomerNumber("32658"); //2514260
        PolicyApplications6.setPolicyNumber("C2000004");


        PolicyApplicationsImpl PolicyApplications7 = new PolicyApplicationsImpl();
        PolicyApplications7.setCustomerNumber("2514260"); //2514260
        PolicyApplications7.setPolicyNumber("C2000005");

        PolicyApplicationsImpl PolicyApplications8 = new PolicyApplicationsImpl();
        PolicyApplications8.setCustomerNumber("32658"); //2514260
        PolicyApplications8.setPolicyNumber("C2000006");


        policyApplications.add(PolicyApplications1);
        policyApplications.add(PolicyApplications2);
        policyApplications.add(PolicyApplications3);
        policyApplications.add(PolicyApplications4);
        policyApplications.add(PolicyApplications5);
        policyApplications.add(PolicyApplications6);
        policyApplications.add(PolicyApplications7);
        policyApplications.add(PolicyApplications8);
        return policyApplications;
    }

    /**
     * Policy tracking screen - subsection api
     */
    @Test
    public void testSearchPolicyForCustomer() {
        List<PolicyTracking> customerPolicies = getCustomerPolicies();
        Mockito.when(policyIntegrationService.getPolicyByCustomerNumber(Matchers.anyList(), Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(customerPolicies);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers((ServiceErrors) Matchers.anyObject())).thenReturn(getAccountMap());
        Mockito.when(productIntegrationService.loadProductsMap((ServiceErrors) Matchers.anyObject())).thenReturn(getProductMap());
        Mockito.when(policyIntegrationService.getFNumbers(Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(policyTrackings);
        Mockito.when(policyIntegrationService.getRecentLivesInsured(Matchers.anyList(), (ServiceErrors) Matchers.anyObject())).thenReturn(getPolicyApplications());

        BrokerImpl broker = new BrokerImpl(com.bt.nextgen.service.integration.broker.BrokerKey.valueOf("12345"), BrokerType.ADVISER);
        broker.setAdviserPPid("123456");

        Mockito.when(brokerIntegrationService.getBroker((com.bt.nextgen.service.integration.broker.BrokerKey) Matchers.anyObject(),
                (ServiceErrors) Matchers.anyObject())).thenReturn(broker);

        List<ApiSearchCriteria> searchCriterias = new ArrayList<>();
        searchCriterias.add(new ApiSearchCriteria(Attribute.INSURANCE_FNUMBER, "F0467807"));
        searchCriterias.add(new ApiSearchCriteria(Attribute.INSURANCE_CUSTOMER_NUMBER, "7E3C27BF5455E73B0812128BB67B940633168E37702DCB02"));
        List<PolicyTrackingDto> trackingDtos = policySummaryDtoService.search(searchCriterias, new ServiceErrorsImpl());
        Assert.assertNotNull(trackingDtos);
        Assert.assertTrue(trackingDtos.size() > 0);
        Assert.assertEquals(7, trackingDtos.size());

        PolicySummaryDto trackingDto = (PolicySummaryDto) trackingDtos.get(0);
        PolicyTracking customerPolicy = customerPolicies.get(0);

        //WRAP account, yearly frequencey, premium and proposedpremium present
        Assert.assertEquals(customerPolicy.getPolicyNumber(), trackingDto.getPolicyNumber());
        Assert.assertEquals(customerPolicy.getPolicyType().name(), trackingDto.getPolicyType());
        Assert.assertEquals(customerPolicy.getPolicyStatus().name(), trackingDto.getPolicyStatus());
        Assert.assertEquals(customerPolicy.getPaymentFrequency().name(), trackingDto.getPaymentFrequency());
        Assert.assertEquals(new BigDecimal("1487.76"), trackingDto.getPremium());
        Assert.assertEquals(customerPolicy.getAccountNumber(), trackingDto.getAccountNumber());
        Assert.assertEquals(customerPolicy.getRenewalCommission(), trackingDto.getRenewalCommission());
        Assert.assertEquals(customerPolicy.getRenewalCalendarDay(), trackingDto.getRenewalCalenderDay());
        Assert.assertEquals(customerPolicy.getCommencementDate(), trackingDto.getCommencementDate());
        Assert.assertEquals(StringUtil.toProperCase(customerPolicy.getInstitutionName()), trackingDto.getAccountSubType());
        Assert.assertEquals(customerPolicy.getPaymentType().getValue(), trackingDto.getFundingAccount());
        Assert.assertNull(trackingDto.getEncodedAccountId());
        Assert.assertNull(trackingDto.getAccountType());

        //Direct cash payment method, Half Yearly frequencey, premium and proposedpremium present
        customerPolicy = customerPolicies.get(1);
        trackingDto = (PolicySummaryDto) trackingDtos.get(1);
        Assert.assertEquals(customerPolicy.getPolicyNumber(), trackingDto.getPolicyNumber());
        Assert.assertEquals(customerPolicy.getPolicyType().name(), trackingDto.getPolicyType());
        Assert.assertEquals(customerPolicy.getPolicyStatus().name(), trackingDto.getPolicyStatus());
        Assert.assertEquals(customerPolicy.getPaymentFrequency().name(), trackingDto.getPaymentFrequency());
        Assert.assertEquals(new BigDecimal("3745.52"), trackingDto.getPremium());
        Assert.assertEquals(customerPolicy.getAccountNumber(), trackingDto.getAccountNumber());
        Assert.assertEquals(customerPolicy.getRenewalCommission(), trackingDto.getRenewalCommission());
        Assert.assertEquals(customerPolicy.getRenewalCalendarDay(), trackingDto.getRenewalCalenderDay());
        Assert.assertEquals(customerPolicy.getCommencementDate(), trackingDto.getCommencementDate());
        Assert.assertEquals(StringUtil.toProperCase(customerPolicy.getInstitutionName()), trackingDto.getAccountSubType());
        Assert.assertEquals(customerPolicy.getPaymentType().getValue(), trackingDto.getFundingAccount());
        Assert.assertNull(trackingDto.getEncodedAccountId());
        Assert.assertNull(trackingDto.getAccountType());

        //Income protection policy type and business overhead sub policy type
        customerPolicy = customerPolicies.get(2);
        trackingDto = (PolicySummaryDto) trackingDtos.get(2);
        Assert.assertEquals(customerPolicy.getPolicyNumber(), trackingDto.getPolicyNumber());
        Assert.assertEquals(customerPolicy.getPolicySubType().name(), trackingDto.getPolicyType());

        //SUPER and pension category account
        customerPolicy = customerPolicies.get(3);
        trackingDto = (PolicySummaryDto) trackingDtos.get(3);
        Assert.assertEquals(customerPolicy.getPolicyNumber(), trackingDto.getPolicyNumber());
        Assert.assertEquals(customerPolicy.getPolicySubType().name(), trackingDto.getPolicyType());
        Assert.assertNotNull(trackingDto.getEncodedAccountId());
        Assert.assertEquals("Policy Super", trackingDto.getAccountName());
        Assert.assertEquals("BT Panorama Super", trackingDto.getProductName());
        Assert.assertEquals("254698789", trackingDto.getAccountNumber());
        Assert.assertEquals(AccountSubType.PENSION.getAccountType(), trackingDto.getAccountSubType());
        Assert.assertEquals(PaymentType.WCACC.getValue(), trackingDto.getFundingAccount());

        //SMSF account
        customerPolicy = customerPolicies.get(4);
        trackingDto = (PolicySummaryDto) trackingDtos.get(4);
        Assert.assertEquals(customerPolicy.getPolicyNumber(), trackingDto.getPolicyNumber());
        Assert.assertEquals(customerPolicy.getPolicySubType().name(), trackingDto.getPolicyType());
        Assert.assertNotNull(trackingDto.getEncodedAccountId());
        Assert.assertEquals("Policy SMSF", trackingDto.getAccountName());
        Assert.assertEquals("BT Panorama Investments", trackingDto.getProductName());
        Assert.assertEquals("254696789", trackingDto.getAccountNumber());
        Assert.assertEquals(AccountStructureType.SMSF.name(), trackingDto.getAccountSubType());
        Assert.assertEquals(PaymentType.WCACC.getValue(), trackingDto.getFundingAccount());
    }

    /**
     * Policy Business report screen,
     * account details are displayed only for panorama accounts
     * hyphen if panorama but account is not in bp_list
     * Other accounts are not displayed
     */
    @Test
    public void testSearchPolicyForAdviser() {
        List<PolicyTracking> adviserPolicies = getAdviserPolicies();
        Mockito.when(policyIntegrationService.getPoliciesForAdviser(Matchers.anyString(), Matchers.any(ServiceErrors.class))).thenReturn(adviserPolicies);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers((ServiceErrors) Matchers.anyObject())).thenReturn(getAccountMap());
        Mockito.when(productIntegrationService.loadProductsMap((ServiceErrors) Matchers.anyObject())).thenReturn(getProductMap());

        List<ApiSearchCriteria> searchCriterias = new ArrayList<>();
        searchCriterias.add(new ApiSearchCriteria(Attribute.INSURANCE_FNUMBER, "F0467807"));

        List<PolicyTrackingDto> trackingDtos = policySummaryDtoService.search(searchCriterias, new ServiceErrorsImpl());
        Assert.assertNotNull(trackingDtos);
        Assert.assertTrue(trackingDtos.size() > 0);
        Assert.assertEquals(2, trackingDtos.size()); //null Policy type are not included

        PolicySummaryDto trackingDto1 = (PolicySummaryDto) trackingDtos.get(0);
        PolicyTracking customerPolicy1 = adviserPolicies.get(0);
        Assert.assertEquals("Y0202713", trackingDto1.getPolicyNumber());
        Assert.assertEquals(PolicyType.TERM_LIFE.name(), trackingDto1.getPolicyType());
        Assert.assertEquals(customerPolicy1.getPolicyStatus().name(), trackingDto1.getPolicyStatus());
        Assert.assertEquals(customerPolicy1.getPaymentFrequency().name(), trackingDto1.getPaymentFrequency());
        Assert.assertEquals(new BigDecimal("1487.76"), trackingDto1.getPremium());
        Assert.assertNull(trackingDto1.getAccountNumber());
        Assert.assertEquals(customerPolicy1.getRenewalCommission(), trackingDto1.getRenewalCommission());
        Assert.assertEquals(customerPolicy1.getRenewalCalendarDay(), trackingDto1.getRenewalCalenderDay());
        Assert.assertEquals(customerPolicy1.getCommencementDate(), trackingDto1.getCommencementDate());
        Assert.assertNull(trackingDto1.getAccountSubType());
        Assert.assertEquals(customerPolicy1.getPaymentType().getValue(), trackingDto1.getFundingAccount());
        Assert.assertNull(trackingDto1.getEncodedAccountId());
        Assert.assertNull(trackingDto1.getAccountType());


        PolicySummaryDto trackingDto = (PolicySummaryDto) trackingDtos.get(1);
        PolicyTracking customerPolicy = adviserPolicies.get(1);

        //WRAP account, yearly frequencey, premium and proposedpremium present
        Assert.assertEquals("C2000002", trackingDto.getPolicyNumber());
        Assert.assertEquals(PolicySubType.BUSINESS_OVERHEAD.name(), trackingDto.getPolicyType());
        Assert.assertEquals(customerPolicy.getPolicyStatus().name(), trackingDto.getPolicyStatus());
        Assert.assertEquals(customerPolicy.getPaymentFrequency().name(), trackingDto.getPaymentFrequency());
        Assert.assertEquals(new BigDecimal("100.00"), trackingDto.getPremium());
        Assert.assertEquals(customerPolicy.getAccountNumber(), trackingDto.getAccountNumber());
        Assert.assertEquals(customerPolicy.getRenewalCommission(), trackingDto.getRenewalCommission());
        Assert.assertEquals(customerPolicy.getRenewalCalendarDay(), trackingDto.getRenewalCalenderDay());
        Assert.assertEquals(customerPolicy.getCommencementDate(), trackingDto.getCommencementDate());
        Assert.assertEquals(AccountSubType.PENSION.getName(), trackingDto.getAccountSubType());
        Assert.assertEquals(PensionType.TTR.name(), trackingDto.getPensionType());
        Assert.assertEquals(customerPolicy.getPaymentType().getValue(), trackingDto.getFundingAccount());
        Assert.assertNotNull(trackingDto.getEncodedAccountId());
        Assert.assertEquals("BT Panorama Super", trackingDto.getProductName());
    }


    private List<PolicyTracking> getAdviserPolicies() {
        List<PolicyTracking> policyTrackings = new ArrayList<>();

        PolicyTrackingImpl policyTracking1 = new PolicyTrackingImpl();
        //WRAP account, yearly frequencey, premium and proposedpremium present
        policyTracking1.setPolicyNumber("Y0202713");
        policyTracking1.setPolicyType(PolicyType.TERM_LIFE);
        policyTracking1.setPolicySubType(PolicySubType.OTHER);
        policyTracking1.setPolicyStatus(PolicyStatusCode.CANCELLED);
        policyTracking1.setAccountNumber("M04651847");
        policyTracking1.setInstitutionName("WRAP");
        policyTracking1.setPaymentType(PaymentType.WCACC);
        policyTracking1.setPremium(new BigDecimal("1437.76"));
        policyTracking1.setProposedPremium(new BigDecimal("50"));
        policyTracking1.setPaymentFrequency(PremiumFrequencyType.YEARLY);
        policyTracking1.setCommencementDate(new DateTime(2015, 1, 30, 0, 0));
        policyTracking1.setRenewalCommission(new BigDecimal("0.0"));
        policyTracking1.setRenewalCalendarDay(getRenewalDate(0, 30));

        //Income protection policy type and business overhead sub policy type
        PolicyTrackingImpl policyTracking4 = new PolicyTrackingImpl();
        policyTracking4.setPolicyNumber("C2000002");
        policyTracking4.setPolicyType(PolicyType.INCOME_PROTECTION);
        policyTracking4.setPolicySubType(PolicySubType.BUSINESS_OVERHEAD);
        policyTracking4.setPolicyStatus(PolicyStatusCode.IN_FORCE);
        policyTracking4.setPaymentType(PaymentType.WCACC);
        policyTracking4.setPremium(new BigDecimal("50.00"));
        policyTracking4.setProposedPremium(new BigDecimal("50"));
        policyTracking4.setPaymentFrequency(PremiumFrequencyType.MONTHLY);
        policyTracking4.setAccountNumber("254698789"); // SUPER account
        policyTracking4.setInstitutionName("PANORAMA");


        PolicyTrackingImpl policyTracking2 = new PolicyTrackingImpl();
        //discarded account
        policyTracking2.setPolicyNumber("C2000003");
        policyTracking2.setPolicyType(PolicyType.TERM_LIFE);
        policyTracking2.setPolicySubType(PolicySubType.OTHER);
        policyTracking2.setPolicyStatus(PolicyStatusCode.CANCELLED);
        policyTracking2.setAccountNumber("254696785"); // Discarded account
        policyTracking4.setInstitutionName("PANORAMA");
        policyTracking2.setPaymentType(PaymentType.WCACC);
        policyTracking2.setPremium(new BigDecimal("1437.76"));
        policyTracking2.setProposedPremium(new BigDecimal("50"));
        policyTracking2.setPaymentFrequency(PremiumFrequencyType.YEARLY);
        policyTracking2.setCommencementDate(new DateTime(2015, 1, 30, 0, 0));
        policyTracking2.setRenewalCommission(new BigDecimal("0.0"));
        policyTracking2.setRenewalCalendarDay(getRenewalDate(0, 30));


        List<PolicyTracking> advisersPolicies = new ArrayList<>();
        advisersPolicies.add(policyTracking1);
        advisersPolicies.add(policyTracking4);
        advisersPolicies.add(policyTracking2);

        return advisersPolicies;
    }


    private List<PolicyTracking> getCustomerPolicies() {
        List<PolicyTracking> policyTrackings = new ArrayList<>();

        PolicyTrackingImpl policyTracking1 = new PolicyTrackingImpl();
        //WRAP account, yearly frequencey, premium and proposedpremium present
        policyTracking1.setPolicyNumber("Y0202713");
        policyTracking1.setPolicyType(PolicyType.TERM_LIFE);
        policyTracking1.setPolicySubType(PolicySubType.OTHER);
        policyTracking1.setPolicyStatus(PolicyStatusCode.CANCELLED);
        policyTracking1.setAccountNumber("M04651847");
        policyTracking1.setInstitutionName("WRAP");
        policyTracking1.setPaymentType(PaymentType.WCACC);
        policyTracking1.setPremium(new BigDecimal("1437.76"));
        policyTracking1.setProposedPremium(new BigDecimal("50"));
        policyTracking1.setPaymentFrequency(PremiumFrequencyType.YEARLY);
        policyTracking1.setCommencementDate(new DateTime(2015, 1, 30, 0, 0));
        policyTracking1.setRenewalCommission(new BigDecimal("0.0"));
        policyTracking1.setRenewalCalendarDay(getRenewalDate(0, 30));


        //Direct cash payment method, Half Yearly frequencey, premium and proposedpremium present
        PolicyTrackingImpl policyTracking2 = new PolicyTrackingImpl();
        policyTracking2.setPolicyNumber("YF202713");
        policyTracking2.setPolicyType(PolicyType.TERM_LIFE);
        policyTracking2.setPolicySubType(PolicySubType.OTHER);
        policyTracking2.setPolicyStatus(PolicyStatusCode.CANCELLED);
        policyTracking2.setAccountNumber(null);
        policyTracking2.setInstitutionName(null);
        policyTracking2.setPaymentType(PaymentType.DICAS);
        policyTracking2.setPremium(new BigDecimal("1822.76"));
        policyTracking2.setProposedPremium(new BigDecimal("50"));
        policyTracking2.setPaymentFrequency(PremiumFrequencyType.HALF_YEARLY);
        policyTracking2.setCommencementDate(new DateTime(2015, 1, 30, 0, 0));
        policyTracking2.setRenewalCommission(new BigDecimal("0.0"));
        policyTracking2.setRenewalCalendarDay(getRenewalDate(0, 30));

        //Income protection policy type and business overhead sub policy type
        PolicyTrackingImpl policyTracking3 = new PolicyTrackingImpl();
        policyTracking3.setPolicyNumber("C2000001");
        policyTracking3.setPolicyType(PolicyType.INCOME_PROTECTION);
        policyTracking3.setPolicySubType(PolicySubType.BUSINESS_OVERHEAD);
        policyTracking3.setPolicyStatus(PolicyStatusCode.IN_FORCE);
        policyTracking3.setPaymentType(PaymentType.DICAS);
        policyTracking3.setPremium(new BigDecimal("1437.76"));
        policyTracking3.setProposedPremium(new BigDecimal("50"));
        policyTracking3.setPaymentFrequency(PremiumFrequencyType.YEARLY);

        //Income protection policy type and business overhead sub policy type
        PolicyTrackingImpl policyTracking4 = new PolicyTrackingImpl();
        policyTracking4.setPolicyNumber("C2000002");
        policyTracking4.setPolicyType(PolicyType.INCOME_PROTECTION);
        policyTracking4.setPolicySubType(PolicySubType.BUSINESS_OVERHEAD);
        policyTracking4.setPolicyStatus(PolicyStatusCode.IN_FORCE);
        policyTracking4.setPaymentType(PaymentType.WCACC);
        policyTracking4.setPremium(new BigDecimal("1437.76"));
        policyTracking4.setProposedPremium(new BigDecimal("50"));
        policyTracking4.setPaymentFrequency(PremiumFrequencyType.YEARLY);
        policyTracking4.setAccountNumber("254698789"); // SUPER account
        policyTracking4.setInstitutionName("PANORAMA");

        //SMSF account policy type
        PolicyTrackingImpl policyTracking5 = new PolicyTrackingImpl();
        policyTracking5.setPolicyNumber("C2000003");
        policyTracking5.setPolicyType(PolicyType.INCOME_PROTECTION);
        policyTracking5.setPolicySubType(PolicySubType.BUSINESS_OVERHEAD);
        policyTracking5.setPolicyStatus(PolicyStatusCode.IN_FORCE);
        policyTracking5.setPaymentType(PaymentType.WCACC);
        policyTracking5.setPremium(new BigDecimal("1437.76"));
        policyTracking5.setProposedPremium(new BigDecimal("50"));
        policyTracking5.setPaymentFrequency(PremiumFrequencyType.YEARLY);
        policyTracking5.setAccountNumber("254696789"); // SMSF account
        policyTracking5.setInstitutionName("PANORAMA");

        //Policy type, not valid in panorama - Code commented for validation
        PolicyTrackingImpl policyTracking6 = new PolicyTrackingImpl();
        policyTracking6.setPolicyNumber("C2000004");
        policyTracking6.setPolicySubType(PolicySubType.BUSINESS_OVERHEAD);
        policyTracking6.setPolicyStatus(PolicyStatusCode.IN_FORCE);
        policyTracking6.setPaymentType(PaymentType.WCACC);
        policyTracking6.setPremium(new BigDecimal("1437.76"));
        policyTracking6.setProposedPremium(new BigDecimal("50"));
        policyTracking6.setPaymentFrequency(PremiumFrequencyType.YEARLY);
        policyTracking6.setAccountNumber("254696789"); // SMSF account
        policyTracking6.setInstitutionName("PANORAMA");


        //Account Status Discarded example - should come up for application tracking screen
        PolicyTrackingImpl policyTracking7 = new PolicyTrackingImpl();
        policyTracking7.setPolicyNumber("C2000006");
        policyTracking7.setPolicySubType(PolicySubType.BUSINESS_OVERHEAD);
        policyTracking7.setPolicyStatus(PolicyStatusCode.IN_FORCE);
        policyTracking7.setPaymentType(PaymentType.WCACC);
        policyTracking7.setPremium(new BigDecimal("1437.76"));
        policyTracking7.setProposedPremium(new BigDecimal("50"));
        policyTracking7.setPaymentFrequency(PremiumFrequencyType.YEARLY);
        policyTracking7.setAccountNumber("254696785"); // SMSF account
        policyTracking7.setInstitutionName("PANORAMA");

        policyTrackings.add(policyTracking1);
        policyTrackings.add(policyTracking2);
        policyTrackings.add(policyTracking3);
        policyTrackings.add(policyTracking4);
        policyTrackings.add(policyTracking5);
        policyTrackings.add(policyTracking6);
        policyTrackings.add(policyTracking7);

        //panorama account - super account and IP
        return policyTrackings;
    }


    /**
     * paramter is the desired month and date, returns the datetime with proper year
     *
     * @param month
     *
     * @return
     */
    private DateTime getRenewalDate(int month, int dateOfMonth) {
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

    private Map<AccountKey, WrapAccount> getAccountMap() {

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();

        AccountKey accountKey1 = AccountKey.valueOf("32658"); //8A82B2403AE4ABCE1E585FA1AC269975BB84F3A8367B37F2
        AccountKey accountKey2 = AccountKey.valueOf("12364"); //5C27E369D8568B9B8294AAB906B1B97B2E047A856B071007
        AccountKey accountKey3 = AccountKey.valueOf("12365"); //

        ClientKey clientKey1 = ClientKey.valueOf("23659");
        ClientKey clientKey2 = ClientKey.valueOf("23859");
        ClientKey clientKey3 = ClientKey.valueOf("23855");

        PensionAccountDetailImpl pensionAccountDetail = new PensionAccountDetailImpl();
        WrapAccountImpl wrapAccount2 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount3 = new WrapAccountImpl();

        pensionAccountDetail.setAccountKey(accountKey1);
        pensionAccountDetail.setAccountNumber("254698789");
        pensionAccountDetail.setAccountName("Policy Super");
        pensionAccountDetail.setAccountStructureType(AccountStructureType.SUPER);
        pensionAccountDetail.setSuperAccountSubType(AccountSubType.PENSION);
        pensionAccountDetail.setPensionType(PensionType.TTR);
        pensionAccountDetail.setAccountStatus(AccountStatus.ACTIVE);
        pensionAccountDetail.setProductKey(ProductKey.valueOf("P123"));

        wrapAccount2.setAccountKey(accountKey2);
        wrapAccount2.setAccountNumber("254696789");
        wrapAccount2.setAccountName("Policy SMSF");
        wrapAccount2.setAccountStructureType(AccountStructureType.SMSF);
        wrapAccount2.setAccountStatus(AccountStatus.ACTIVE);
        wrapAccount2.setProductKey(ProductKey.valueOf("P456"));


        wrapAccount3.setAccountKey(accountKey3);
        wrapAccount3.setAccountNumber("254696785");
        wrapAccount3.setAccountName("Policy Company account type");
        wrapAccount3.setAccountStructureType(AccountStructureType.Company);
        wrapAccount3.setAccountStatus(AccountStatus.DISCARD);
        wrapAccount2.setProductKey(ProductKey.valueOf("P456"));


        accountMap.put(accountKey1, pensionAccountDetail);
        accountMap.put(accountKey2, wrapAccount2);
        accountMap.put(accountKey3, wrapAccount3);

        return accountMap;
    }

    private  Map<ProductKey, Product> getProductMap() {
        Map<ProductKey, Product> productMap = new HashMap<>();
        ProductImpl productSuper = new ProductImpl();
        productSuper.setProductName("BT Panorama Super");
        ProductImpl productInv = new ProductImpl();
        productInv.setProductName("BT Panorama Investments");

        productMap.put(ProductKey.valueOf("P123"), productSuper);
        productMap.put(ProductKey.valueOf("P456"), productInv);
        return productMap;
    }
}
