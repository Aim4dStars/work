package com.bt.nextgen.reports.insurance;

import com.bt.nextgen.api.policy.model.PolicySummaryDto;
import com.bt.nextgen.api.policy.service.PolicyUtility;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PaymentType;
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
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
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
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test file for Insurance account list csv
 */
@RunWith(MockitoJUnitRunner.class)
public class InsuranceAccountListCsvReportTest {

    @InjectMocks
    InsuranceAccountListCsvReport insuranceAccountListCsvReport;

    @Mock
    private PolicyIntegrationService policyIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private PolicyUtility policyUtility;

    private Map<String, Object> params;
    private List<PolicyTracking> policyTrackings = new ArrayList<>();

    @Before
    public void setUp() {
        params = new HashMap<>();
        params.put("brokerid", "2E4667DDF8C648F8");

        setPolicyTrackings();
        Mockito.when(policyIntegrationService.getFNumbers(Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(policyTrackings);
        List<PolicyTracking> adviserPolicies = getAdviserPolicies();
        List<PolicyTracking> adviserPoliciesF0484681 = getAdviserPolicies_F0484681();
        Mockito.when(policyIntegrationService.getPoliciesForAdviser(Matchers.eq("F0467807"), Matchers.any(ServiceErrors.class))).thenReturn(adviserPolicies);
        Mockito.when(policyIntegrationService.getPoliciesForAdviser(Matchers.eq("F0484681"), Matchers.any(ServiceErrors.class))).thenReturn(adviserPoliciesF0484681);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers((ServiceErrors) Matchers.anyObject())).thenReturn(getAccountMap());
        Mockito.when(productIntegrationService.loadProductsMap((ServiceErrors) Matchers.anyObject())).thenReturn(getProductMap());
        Mockito.when(policyUtility.getAdviserPpId(Matchers.anyString(), (ServiceErrors)Matchers.anyObject())).thenReturn("123456");
        UserProfile userProfile = Mockito.mock(UserProfile.class);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfile);

    }


    @Test
    public void testreportType()
    {
        String reportType = insuranceAccountListCsvReport.getReportType(params);
        Assert.assertNotNull(reportType);
        Assert.assertEquals("Panorama Insurance Account List", reportType);
    }

    @Test
    public void testDefaultValue()
    {
        String defaultValue = insuranceAccountListCsvReport.getDefaultValue(params);
        Assert.assertNotNull(defaultValue);
        Assert.assertEquals("-", defaultValue);
    }


    @Test
    public void testretrieveInsuranceAccountList() {
        Collection<PolicySummaryDto> policySummaryDtos = insuranceAccountListCsvReport.retrieveInsuranceAccountList(params);
        List policies = new ArrayList(policySummaryDtos);
        Assert.assertNotNull(policies);
        Assert.assertTrue(policies.size() > 0);
        Assert.assertEquals(4, policies.size()); //null Policy type are not included



        //Policy1 from  getAdviserPolicies() method
        PolicySummaryDto policyDto3 = (PolicySummaryDto) policies.get(0);
        Assert.assertEquals("Y0202713", policyDto3.getPolicyNumber());
        Assert.assertNull(policyDto3.getAccountNumber());
        Assert.assertEquals("F0467807", policyDto3.getFnumber());
        Assert.assertEquals("Term Life", policyDto3.getPolicyType());
        Assert.assertEquals("Cancelled", policyDto3.getPolicyStatus());
        Assert.assertEquals(new BigDecimal("1487.76"), policyDto3.getPremium());
        Assert.assertEquals("Yearly", policyDto3.getPaymentFrequency());
        Assert.assertEquals(new BigDecimal("10.00"),policyDto3.getRenewalCommission());
        Assert.assertEquals("30 Jan 2015",policyDto3.getCsvCommencementDate());
        Assert.assertNull(policyDto3.getRenewalCalenderDay());
        Assert.assertNull(policyDto3.getAccountNumber());
        Assert.assertNull(policyDto3.getAccountName());
        Assert.assertNull(policyDto3.getAccountType());

        //Policy2 from  getAdviserPolicies() method
        PolicySummaryDto policyDto2 = (PolicySummaryDto) policies.get(1);
        Assert.assertEquals("C2000002", policyDto2.getPolicyNumber());
        Assert.assertEquals("F0467807", policyDto2.getFnumber());
        Assert.assertEquals("Business Overheads", policyDto2.getPolicyType());
        Assert.assertEquals("In force", policyDto2.getPolicyStatus());
        Assert.assertEquals(new BigDecimal("100.00"), policyDto2.getPremium());
        Assert.assertEquals("Monthly", policyDto2.getPaymentFrequency());
        Assert.assertNull(policyDto2.getRenewalCommission());
        Assert.assertEquals("-",policyDto2.getCsvCommencementDate());
        Assert.assertNull(policyDto2.getRenewalCalenderDay());
        Assert.assertEquals("254698789", policyDto2.getAccountNumber());
        Assert.assertEquals("Policy Super", policyDto2.getAccountName());
        Assert.assertEquals("Pension (TTR)", policyDto2.getAccountType());
        Assert.assertEquals("BT Panorama Super", policyDto2.getProductName());

        //Policy3 from  getAdviserPolicies_F0484681() method
        PolicySummaryDto policyDto4 = (PolicySummaryDto) policies.get(2);
        Assert.assertEquals("Y0202713", policyDto4.getPolicyNumber());
        Assert.assertNull(policyDto4.getAccountNumber());
        Assert.assertEquals("F0484681", policyDto4.getFnumber());
        Assert.assertEquals("Term Life", policyDto4.getPolicyType());
        Assert.assertEquals("Proposal", policyDto4.getPolicyStatus());
        Assert.assertEquals(new BigDecimal("1487.76"), policyDto4.getPremium());
        Assert.assertEquals("Yearly", policyDto4.getPaymentFrequency());
        Assert.assertEquals(new BigDecimal("0.00"),policyDto4.getRenewalCommission());
        Assert.assertEquals("Pending",policyDto4.getCsvCommencementDate());
        Assert.assertNull(policyDto4.getRenewalCalenderDay());
        Assert.assertNull(policyDto4.getAccountNumber());
        Assert.assertNull(policyDto4.getAccountName());
        Assert.assertNull(policyDto4.getAccountType());

        //Policy4 from  getAdviserPolicies_F0484681() method
        //Business Overheads - policytype, Panorama SMSF - account type,
        PolicySummaryDto policyDto1 = (PolicySummaryDto) policies.get(3);
        Assert.assertEquals("C2000003", policyDto1.getPolicyNumber());
        Assert.assertEquals("F0484681", policyDto1.getFnumber());
        Assert.assertEquals("Business Overheads", policyDto1.getPolicyType());
        Assert.assertEquals("In force", policyDto1.getPolicyStatus());
        Assert.assertEquals(new BigDecimal("100.00"), policyDto1.getPremium());
        Assert.assertEquals("Monthly", policyDto1.getPaymentFrequency());
        Assert.assertNull(policyDto1.getRenewalCommission());
        Assert.assertEquals("-",policyDto1.getCsvCommencementDate());
        Assert.assertNull(policyDto1.getRenewalCalenderDay());
        Assert.assertEquals("254696789", policyDto1.getAccountNumber());
        Assert.assertEquals("Policy SMSF", policyDto1.getAccountName());
        Assert.assertEquals("SMSF", policyDto1.getAccountType());
        Assert.assertEquals("BT Panorama Investments", policyDto1.getProductName());

    }

    /**
     * Fnumber service mock
     * two fnumbers retrieved for the adviser
     */
    private void setPolicyTrackings() {
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
    }

    /**
     * SearchPolicyByAdviser service mock for fnumber F0467807
     * @return
     */
    private List<PolicyTracking> getAdviserPolicies() {
        List<PolicyTracking> policyTrackings = new ArrayList<>();

        //Policy1
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
        policyTracking1.setRenewalCommission(new BigDecimal("10.0"));
        policyTracking1.setRenewalCalendarDay(getRenewalDate(0, 30));

        //Policy2
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


        //Policy5 - discarded (not present in the output list
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

    /**
     * SearchPolicyByAdviser service mock for fnumber F0484681
     * @return
     */

    private List<PolicyTracking> getAdviserPolicies_F0484681() {
        List<PolicyTracking> policyTrackings = new ArrayList<>();

        //Policy3
        PolicyTrackingImpl policyTracking1 = new PolicyTrackingImpl();
        //WRAP account, yearly frequencey, premium and proposedpremium present
        policyTracking1.setPolicyNumber("Y0202713");
        policyTracking1.setPolicyType(PolicyType.TERM_LIFE);
        policyTracking1.setPolicySubType(PolicySubType.OTHER);
        policyTracking1.setPolicyStatus(PolicyStatusCode.PROPOSAL);
        policyTracking1.setAccountNumber("M04651847");
        policyTracking1.setInstitutionName("WRAP");
        policyTracking1.setPaymentType(PaymentType.WCACC);
        policyTracking1.setPremium(new BigDecimal("1437.76"));
        policyTracking1.setProposedPremium(new BigDecimal("50"));
        policyTracking1.setPaymentFrequency(PremiumFrequencyType.YEARLY);
        policyTracking1.setCommencementDate(new DateTime(2015, 1, 30, 0, 0));
        policyTracking1.setRenewalCommission(new BigDecimal("0.0"));
        policyTracking1.setRenewalCalendarDay(getRenewalDate(0, 30));

        //Policy4
        //Income protection policy type and business overhead sub policy type
        PolicyTrackingImpl policyTracking4 = new PolicyTrackingImpl();
        policyTracking4.setPolicyNumber("C2000003");
        policyTracking4.setPolicyType(PolicyType.INCOME_PROTECTION);
        policyTracking4.setPolicySubType(PolicySubType.BUSINESS_OVERHEAD);
        policyTracking4.setPolicyStatus(PolicyStatusCode.IN_FORCE);
        policyTracking4.setPaymentType(PaymentType.WCACC);
        policyTracking4.setPremium(new BigDecimal("50.00"));
        policyTracking4.setProposedPremium(new BigDecimal("50"));
        policyTracking4.setPaymentFrequency(PremiumFrequencyType.MONTHLY);
        policyTracking4.setAccountNumber("254696789"); // SMSF account
        policyTracking4.setInstitutionName("PANORAMA");

        List<PolicyTracking> advisersPolicies = new ArrayList<>();
        advisersPolicies.add(policyTracking1);
        advisersPolicies.add(policyTracking4);

        return advisersPolicies;
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

        //Discarded account
        wrapAccount3.setAccountKey(accountKey3);
        wrapAccount3.setAccountNumber("254696785");
        wrapAccount3.setAccountName("Policy Company account type");
        wrapAccount3.setAccountStructureType(AccountStructureType.Company);
        wrapAccount3.setAccountStatus(AccountStatus.DISCARD);
        wrapAccount3.setProductKey(ProductKey.valueOf("P456"));


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