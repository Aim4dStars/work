package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRoleType;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SuperPermissionDtoServiceTest extends PermissionAccountDtoServiceBase {
    private static final String SUPER_ACCOUNT_TYPE = "account.type.super";
    private static final String SUPER_ACCOUNT_DETAILS_VIEW = "account.super.details.view";
    private static final String SUPER_ACCOUNT_TAXPRESERVATION_VIEW = "account.super.taxpreservation.view";
    private static final String SUPER_ACCOUNT_CONTRIBUTION_VIEW = "account.super.contribution.view";
    private static final String SUPER_ACCOUNT_BENEFICIARIES_AUTO_REV_VIEW = "account.super.beneficiaries.view";
    private static final String SUPER_ACCOUNT_BENEFICIARIES_AUTO_REV_UPDATE = "account.super.beneficiaries.update";
    private static final String SUPER_ACCOUNT_OVERVIEW_CONTRIBUTION_SUMMARY_VIEW = "account.super.contribution.overview";
    private static final String SUPER_COMMENCE_PENSION_VIEW = "account.super.pension.commencement.view";
    private static final String SUPER_COMMENCE_PENSION_UPDATE = "account.super.pension.commencement.update";
    private static final String SUPER_PERSONAL_TAX_DEDUCTION_VIEW = "account.super.personaltaxdeductionnotice.view";
    private static final String SUPER_PERSONAL_TAX_DEDUCTION_UPDATE = "account.super.personaltaxdeductionnotice.update";
    private static final String SUPER_LINKED_ACCOUNTS_PENSION_VIEW = "account.super.pension.linkedbankaccounts.view";
    private static final String SUPER_LINKED_ACCOUNTS_ACCUMULATION_VIEW = "account.super.accum.linkedbankaccounts.view";
    private static final String SUPER_ACCOUNT_ACCUMULATION_ONEOFF_WITHDRAWAL = "account.super.withdrawal.create";
    private static final String SUPER_ACCOUNT_ACCUMULATION_ACCESSING_MENU_VIEW = "account.super.accessing.menu.view";
    private static final String SUPER_ACCOUNT_ACCUMULATION_ONEOFF_WITHDRAWAL_CREATE = "account.super.withdrawal.create";
    private static final String SUPER_ACCOUNT_ACCUMULATION_ONEOFF_WITHDRAWAL_VIEW = "account.super.withdrawal.view";
    private static final String SUPER_ACCOUNT_CONTRIBUTIONS_MENU_VIEW = "account.super.contributions.menu.view";
    private static final String SUPER_ACCOUNT_CONTRIBUTIONS_INFO_MENU_VIEW = "account.super.contributions.info.menu.view";
    private static final String SUPER_GLOBAL_INSURANCE_MENU_VIEW = "account.super.insurance.menu.view";
    private static final String SUPER_PENSION_TRANSFER_MONEY_MENU_VIEW = "account.super.pension.transfermoney.menu.view";
    private static final String SUPER_ACCUMULATION_ACCESS_MENU_VIEW = "account.super.access.menu.view";
    private static final String SUPER_PENSION_PAYMENT_MENU_VIEW = "account.super.pension.payment.menu.view";
    private static final String SUPER_PENSION_COMMENCE_MENU_VIEW = "account.super.pension.commencement.menu.view";


    @InjectMocks
    private SuperPermissionDtoServiceImpl superPermissionService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private AsimPermission asimPermission;

    private WrapAccountDetail accountDetails;
    private ServiceErrors serviceErrors;
    private UserProfile profile;
    private UserProfile profileWithoutAccountView;
    private UserProfile profileWithoutTaxDeductionUpdate;
    private UserProfile profileWithoutCommencePensionUpdate;
    private UserProfile profileWithoutPaymentCreate;
    private PermissionsDto permission;

    @Before
    public void setup() {
        permission = new PermissionsDto();
        serviceErrors = new ServiceErrorsImpl();

        when(asimPermission.overrideValue(anyBoolean(), anyBoolean(), anyBoolean(), any(JobRoleType.class))).thenReturn(true);
        profileWithoutAccountView = getProfileWithNoViewAccountReportPermissions(JobRole.ADVISER, "job id 2", "client2", UserExperience.ADVISED);
        profileWithoutTaxDeductionUpdate = getProfileWithNoTaxDeductionUpdatePermissions(JobRole.ADVISER, "job id 2", "client2", UserExperience.ADVISED);
        profileWithoutCommencePensionUpdate = getProfileWithNoCommencePensionUpdatePermissions(JobRole.ADVISER, "job id 2", "client2", UserExperience.ADVISED);
        profileWithoutPaymentCreate = getProfileWithNoPaymentCreatePermissions(JobRole.ADVISER, "job id 2", "client2", UserExperience.ADVISED);
    }

    /**
     * Test case for super permissions
     *
     * @throws Exception
     */
    @Test
    public void testSuperAccumulation_Permissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        accountDetails = getSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.ACCUMULATION);

        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat(permission, notNullValue());

        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermission(SUPER_ACCOUNT_TYPE), equalTo(true));

        superAccountDetailsView("Accumulation Account with Account Reports View", profile, true);
        superAccountDetailsView("Accumulation Account without Account Reports View", profileWithoutAccountView, false);

        superTaxPreservationView("Accumulation Account with Tax Preservation View", profile, true);
        superTaxPreservationView("Accumulation Account without Tax Preservation View", profileWithoutAccountView, false);

        superContributionView("Accumulation Account with Contribution View", profile, true);
        superContributionView("Accumulation Account without Contribution View", profileWithoutAccountView, false);

        superBenefAutoRevView("Accumulation Account with AutoReversionary Beneficiaries View", profile, true);
        superBenefAutoRevView("Accumulation Account without AutoReversionary Beneficiaries View", profileWithoutAccountView, false);

        superBenefAutoRevUpdate("Accumulation Account and Update Beneficiaries Role", profile, true, true, true);
        superBenefAutoRevUpdate("Accumulation Account without Update Beneficiaries Role", profile, false, false, false);

        superLinkedAccountsPensionView("Accumulation Account with Account View Reports and LinkedPension", profile, false);
        superLinkedAccountsPensionView("Accumulation Account without Account View Reports and LinkedPension", profileWithoutAccountView, false);

        superLinkedAccountsAccumulationView("Accumulation Account with View Reports and LinkedAccumulation", profile, true);
        superLinkedAccountsPensionView("Accumulation Account and Non View Reports and LinkedAccumulation", profileWithoutAccountView, false);

        superAccOverviewContriSummary("Accumulation Account and Account Contribution Summary", profile, true);
        superAccOverviewContriSummary("Accumulation Account without Account Contribution Summary", profileWithoutAccountView, false);

        superTaxDeductionView("Accumulation Account with Tax deduction View", profile, true);
        superTaxDeductionView("Accumulation Account without Tax deduction View", profileWithoutAccountView, false);

        superTaxDeductionUpdate("Accumulation Account Tax Deduction Role", profile, true, true, true);
        superTaxDeductionUpdate("Accumulation Account Without Tax Deduction Role", profileWithoutTaxDeductionUpdate, false, false, false);

        superAccumulationOneOffWithdrawal("Accumulation Account with Payment role and view reports role", profile, true);
        superAccumulationOneOffWithdrawal("Accumulation Account without Payment role and with view reports role", profileWithoutPaymentCreate, false);
        superAccumulationOneOffWithdrawal("Accumulation Account without Payment role and without view reports role", profileWithoutAccountView, false);

        superAccumulationAccessingMenuView("Accumulation Account with view reports role", profile, true);
        superAccumulationAccessingMenuView("Accumulation Account without view reports role", profileWithoutAccountView, false);

        superAccumulationOneOffViewWithdrawal("Accumulation Account view withdrawal with Payment role and view reports role", profile, true);
        superAccumulationOneOffViewWithdrawal("Accumulation Account view withdrawal without Payment role and without view reports role", profileWithoutAccountView, false);

        superAccumulationOneOffCreateWithdrawal("Accumulation Account create withdrawal with Payment role and view reports role", profile, true);
        superAccumulationOneOffCreateWithdrawal("Accumulation Account create withdrawal without Payment role and with view reports role", profileWithoutPaymentCreate, false);
        superAccumulationOneOffCreateWithdrawal("Accumulation Account create withdrawal without Payment role and without view reports role", profileWithoutAccountView, false);

        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        assertThat(permission.hasPermission("account.payment.transaction.view"), equalTo(false));
        assertThat(permission.hasPermission("account.deposit.transaction.view"), equalTo(false));
        assertThat(permission.hasPermission("account.accountsbillers.menu.view"), equalTo(false));
        assertThat(permission.hasPermission("account.movemoney.menu.view"), equalTo(false));
        assertThat(permission.hasPermission("account.transaction.scheduled.view"), equalTo(true));
        assertThat(permission.hasPermission("account.linked.menu.view"), equalTo(true));
    }

    @Test
    public void testSuperAccumulation_Permissions_Investor() throws Exception {
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.ADVISED);
        accountDetails = getSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.ACCUMULATION);
        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);

        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));

        superBenefAutoRevUpdate("For Investor- Accumulation Account and Update Beneficiaries Role", profile, true, true, true);
        superBenefAutoRevUpdate("For Investor- Accumulation Account without Update Beneficiaries Role", profile, false, false, false);

        superAccountDetailsView("For Investor- Accumulation Account with Account Reports View", profile, true);
        superAccountDetailsView("For Investor- Accumulation Account without Account Reports View", profileWithoutAccountView, false);

        superTaxPreservationView("For Investor- Accumulation Account with Tax Preservation View", profile, true);
        superTaxPreservationView("For Investor- Accumulation Account without Tax Preservation View", profileWithoutAccountView, false);

        superContributionView("For Investor- Accumulation Account with Contribution View", profile, true);
        superContributionView("For Investor- Accumulation Account without Contribution View", profileWithoutAccountView, false);

        superLinkedAccountsPensionView("For Investor- Accumulation Account with Account View Reports and LinkedPension", profile, false);
        superLinkedAccountsPensionView("For Investor- Accumulation Account without Account View Reports and LinkedPension", profileWithoutAccountView, false);

        superLinkedAccountsAccumulationView("For Investor- Accumulation Account with View Reports and LinkedAccumulation", profile, true);
        superLinkedAccountsPensionView("For Investor- Accumulation Account and Non View Reports and LinkedAccumulation", profileWithoutAccountView, false);

        superBenefAutoRevView("For Investor- Accumulation Account with AutoReversionary Beneficiaries View", profile, true);
        superBenefAutoRevView("Accumulation Account without AutoReversionary Beneficiaries View", profileWithoutAccountView, false);

        superAccOverviewContriSummary("For Investor- Accumulation Account and Account Contribution Summary", profile, true);
        superAccOverviewContriSummary("For Investor- Accumulation Account without Account Contribution Summary", profileWithoutAccountView, false);

        superTaxDeductionUpdate("For Investor- Accumulation Account Tax Deduction Role", profile, true, true, true);
        superTaxDeductionUpdate("For Investor- Accumulation Account Without Tax Deduction Role", profileWithoutTaxDeductionUpdate, false, false, false);

        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        assertThat(permission.hasPermission("account.payment.transaction.view"), equalTo(false));
        assertThat(permission.hasPermission("account.deposit.transaction.view"), equalTo(false));
        assertThat(permission.hasPermission("account.accountsbillers.menu.view"), equalTo(false));
        assertThat(permission.hasPermission("account.movemoney.menu.view"), equalTo(false));
        assertThat(permission.hasPermission("account.transaction.scheduled.view"), equalTo(true));
        assertThat(permission.hasPermission("account.linked.menu.view"), equalTo(true));
    }

    @Test
    public void testSuperPensionCommenced_Permissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        accountDetails = getSuperPensionAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.PENSION, DateTime.parse("2016-01-01"));

        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat(permission, notNullValue());

        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermission(SUPER_ACCOUNT_TYPE), equalTo(true));

        superAccountDetailsView("Pension Account with Account Reports View", profile, true);
        superAccountDetailsView("Pension Account without Account Reports View", profileWithoutAccountView, false);

        superTaxPreservationView("Pension Account with Tax Preservation View", profile, true);
        superTaxPreservationView("Pension Account without Tax Preservation View", profileWithoutAccountView, false);

        superContributionView("Pension Account with Contribution View", profile, true);
        superContributionView("Pension Account without Contribution View", profileWithoutAccountView, false);

        superBenefAutoRevUpdate("Pension Account and Update Beneficiaries Role", profile, true, true, true);
        superBenefAutoRevUpdate("Pension Account without Update Beneficiaries Role", profile, false, false, false);

        superLinkedAccountsPensionView("Pension Account with Account View Reports and LinkedPension", profile, true);
        superLinkedAccountsPensionView("Pension Account without Account View Reports and LinkedPension", profileWithoutAccountView, false);

        superLinkedAccountsAccumulationView("Pension Account with View Reports and LinkedAccumulation", profile, false);
        superLinkedAccountsPensionView("Pension Account and Non View Reports and LinkedAccumulation", profileWithoutAccountView, false);

        superBenefAutoRevView("Pension Account with AutoReversionary Beneficiaries View", profile, true);
        superBenefAutoRevView("Pension Account without AutoReversionary Beneficiaries View", profileWithoutAccountView, false);

        superAccOverviewContriSummary("Pension Account and Account Contribution Summary", profile, false);
        superAccOverviewContriSummary("Pension Account without Account Contribution Summary", profileWithoutAccountView, false);

        superTaxDeductionView("Pension Account with Tax deduction View", profile, true);
        superTaxDeductionView("Pension Account without Tax deduction View", profileWithoutAccountView, false);

        superCommencePensionView("Non Commenced Pension Account and View account reports", profile, false);
        superCommencePensionView("Non Commenced Pension Account without View account reports", profileWithoutAccountView, false);

        superCommencePensionUpdate("Non Commenced Pension Account Update with CommencePensionRole", profile, true, true, false);
        superCommencePensionUpdate("Non Commenced Pension Account Update without CommencePensionRole", profileWithoutCommencePensionUpdate, false, false, false);

        superTaxDeductionUpdate("Pension Account with Tax Deduction Role", profile, true, true, false);
        superTaxDeductionUpdate("Pension Account Without Tax Deduction Role", profileWithoutTaxDeductionUpdate, false, false, false);

        superAccumulationOneOffWithdrawal("Non Accumulation Account with Payment role and view reports role", profile, false);
        superAccumulationOneOffWithdrawal("Non Accumulation Account with Payment role and without view reports role", profileWithoutAccountView, false);
        superAccumulationOneOffWithdrawal("Non Accumulation Account without Payment role and with view reports role", profileWithoutPaymentCreate, false);


        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        assertThat(permission.hasPermission("account.accountsbillers.menu.view"), equalTo(false));
        assertThat(permission.hasPermission("account.linked.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.movemoney.menu.view"), equalTo(false));
        assertThat(permission.hasPermission("account.transaction.scheduled.view"), equalTo(true));
        assertThat("Pension payments in menu is not visible", permission.hasPermission(SUPER_PENSION_PAYMENT_MENU_VIEW), is(false));
        assertThat("Commence your pension in menu is not visible", permission.hasPermission(SUPER_PENSION_COMMENCE_MENU_VIEW), is(false));
    }

    @Test
    public void testSuperPensionNotCommenced_Permissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        accountDetails = getSuperPensionAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.PENSION, null);

        superAccOverviewContriSummary("Non Commenced Pension Account and Account Contribution Summary", profile, false);
        superAccOverviewContriSummary("Non Commenced Pension Account without Account Contribution Summary", profileWithoutAccountView, false);

        superCommencePensionView("Non Commenced Pension Account and View account reports", profile, true);
        superCommencePensionView("Non Commenced Pension Account without View account reports", profileWithoutAccountView, false);

        superCommencePensionUpdate("Non Commenced Pension Account Update with CommencePensionRole", profile, true, true, true);
        superCommencePensionUpdate("Non Commenced Pension Account Update without CommencePensionRole", profileWithoutCommencePensionUpdate, false, false, false);

        superTaxDeductionView("Non Commenced Pension Account and View account reports", profile, true);
        superTaxDeductionView("Non Commenced Pension Account without View account reports", profileWithoutAccountView, false);

        superTaxDeductionUpdate("Non Commenced Pension Account Update with Tax Deduction Role", profile, true, true, true);
        superTaxDeductionUpdate("Non Commenced Pension Account Update without Tax Deduction Role", profileWithoutTaxDeductionUpdate, false, false, false);


        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermission("account.accountsbillers.menu.view"), equalTo(false));
        assertThat(permission.hasPermission("account.linked.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.movemoney.menu.view"), equalTo(false));
        assertThat(permission.hasPermission("account.transaction.scheduled.view"), equalTo(true));
        assertThat("Pension payments in menu is not visible", permission.hasPermission(SUPER_PENSION_PAYMENT_MENU_VIEW), is(false));
        assertThat("Commence your pension in menu is not visible", permission.hasPermission(SUPER_PENSION_COMMENCE_MENU_VIEW), is(false));
    }

    /**
     * This test case would test the scenario where Dealergroup is not authorised to sell insurance.
     */
    @Test
    public void insurancePermissionAtAccLevelWithInvalidProductOffering() {
        FunctionalRole[] roleList = new FunctionalRole[]{FunctionalRole.View_Insurance_Account, FunctionalRole.View_account_reports, FunctionalRole.View_Insurance_Commissions};
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED, roleList);
        accountDetails = getSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.PENSION);
        Broker broker = new BrokerImpl(BrokerKey.valueOf("broker2"), BrokerType.DEALER);
        when(brokerHelperService.getDealerGroupForInvestor(accountDetails, serviceErrors)).thenReturn(broker);

        List<Product> productList = new ArrayList<>();
        ProductImpl product = new ProductImpl();
        product.setShortName("PROD.OFFER.60f52dc6d17421eaf1632ac9");
        productList.add(product);
        when(productIntegrationService.getDealerGroupProductList(broker.getDealerKey(), serviceErrors)).thenReturn(productList);

        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Account level permission is false", permission.hasPermission("account.super.insurance.view"), is(false));
        assertThat("Account level menu permission is false", permission.hasPermission(SUPER_GLOBAL_INSURANCE_MENU_VIEW), is(false));
    }

    /**
     * Pension investors shouldn't see insurance tab
     */
    @Test
    public void insurancePermissionsForPensionInvestors() {
        FunctionalRole[] roleList = new FunctionalRole[]{FunctionalRole.View_Insurance_Account, FunctionalRole.View_account_reports, FunctionalRole.View_Insurance_Commissions};
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.ADVISED, roleList);
        accountDetails = getSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.PENSION);
        Broker broker = new BrokerImpl(BrokerKey.valueOf("broker2"), BrokerType.DEALER);
        when(brokerHelperService.getDealerGroupForInvestor(accountDetails, serviceErrors)).thenReturn(broker);

        List<Product> productList = new ArrayList<>();
        ProductImpl product = new ProductImpl();
        product.setShortName("PROD.OFFER.65f52dc6d17421eaf1632ac7");
        productList.add(product);
        when(productIntegrationService.getDealerGroupProductList(broker.getDealerKey(), serviceErrors)).thenReturn(productList);

        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Account level permission is true", permission.hasPermission("account.super.insurance.view"), is(true));
        assertThat("Account level menu permission is false", permission.hasPermission(SUPER_GLOBAL_INSURANCE_MENU_VIEW), is(false));
    }

    @Test
    public void insurancePermissionsForDirectSuperInvestors() {
        FunctionalRole[] roleList = new FunctionalRole[]{FunctionalRole.View_Insurance_Account, FunctionalRole.View_account_reports, FunctionalRole.View_Insurance_Commissions};
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.DIRECT, roleList);
        accountDetails = getSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.ACCUMULATION);
        Broker broker = new BrokerImpl(BrokerKey.valueOf("broker2"), BrokerType.DEALER);
        when(brokerHelperService.getDealerGroupForInvestor(accountDetails, serviceErrors)).thenReturn(broker);

        List<Product> productList = new ArrayList<>();
        ProductImpl product = new ProductImpl();
        product.setShortName("PROD.OFFER.65f52dc6d17421eaf1632ac7");
        productList.add(product);
        when(productIntegrationService.getDealerGroupProductList(broker.getDealerKey(), serviceErrors)).thenReturn(productList);

        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Account level permission is true", permission.hasPermission("account.super.insurance.view"), is(true));
        assertThat("Account level menu permission is true", permission.hasPermission(SUPER_GLOBAL_INSURANCE_MENU_VIEW), is(true));
    }

    @Test
    public void insurancePermissionsForDirectNonSuperInvestors() {
        FunctionalRole[] roleList = new FunctionalRole[]{FunctionalRole.View_Insurance_Account, FunctionalRole.View_account_reports, FunctionalRole.View_Insurance_Commissions};
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.DIRECT, roleList);
        accountDetails = getNonSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);

        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Account level permission is false", permission.hasPermission("account.super.insurance.view"), is(false));
        assertThat("Account level menu permission is false", permission.hasPermission(SUPER_GLOBAL_INSURANCE_MENU_VIEW), is(false));
    }

    /**
     * Accumulation investors should see insurance tab
     */
    @Test
    public void insurancePermissionsForSuperInvestors() {
        FunctionalRole[] roleList = new FunctionalRole[]{FunctionalRole.View_Insurance_Account, FunctionalRole.View_account_reports, FunctionalRole.View_Insurance_Commissions};
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.ADVISED, roleList);
        accountDetails = getSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.ACCUMULATION);
        Broker broker = new BrokerImpl(BrokerKey.valueOf("broker2"), BrokerType.DEALER);
        when(brokerHelperService.getDealerGroupForInvestor(accountDetails, serviceErrors)).thenReturn(broker);

        List<Product> productList = new ArrayList<>();
        ProductImpl product = new ProductImpl();
        product.setShortName("PROD.OFFER.65f52dc6d17421eaf1632ac7");
        productList.add(product);
        when(productIntegrationService.getDealerGroupProductList(broker.getDealerKey(), serviceErrors)).thenReturn(productList);

        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Account level permission is true", permission.hasPermission("account.super.insurance.view"), is(true));
        assertThat("Account level menu permission is true", permission.hasPermission(SUPER_GLOBAL_INSURANCE_MENU_VIEW), is(true));
    }

    /**
     * Pension investors shouldn't see insurance tab
     */
    @Test
    public void insurancePermissionsForNonSuperInvestors() {
        FunctionalRole[] roleList = new FunctionalRole[]{FunctionalRole.View_Insurance_Account, FunctionalRole.View_account_reports};
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.ADVISED, roleList);
        accountDetails = getNonSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        Broker broker = new BrokerImpl(BrokerKey.valueOf("broker2"), BrokerType.DEALER);
        when(brokerHelperService.getDealerGroupForInvestor(accountDetails, serviceErrors)).thenReturn(broker);

        List<Product> productList = new ArrayList<>();
        ProductImpl product = new ProductImpl();
        product.setShortName("PROD.OFFER.65f52dc6d17421eaf1632ac7");
        productList.add(product);
        when(productIntegrationService.getDealerGroupProductList(broker.getDealerKey(), serviceErrors)).thenReturn(productList);

        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Account level permission is false", permission.hasPermission("account.super.insurance.view"), is(true));
        assertThat("Account level menu permission is false", permission.hasPermission(SUPER_GLOBAL_INSURANCE_MENU_VIEW), is(true));
    }

    /**
     * This test case would test the scenario where Dealergroup is authorised to sell insurance.
     */
    @Test
    public void testInsurancePermissionAtAccLevelWithValidProductOffering() {
        FunctionalRole[] roleList = new FunctionalRole[]{FunctionalRole.View_Insurance_Account, FunctionalRole.View_account_reports, FunctionalRole.View_Insurance_Commissions};
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED, roleList);
        accountDetails = getSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.PENSION);
        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("broker2"), BrokerType.DEALER);
        when(brokerHelperService.getDealerGroupForInvestor(accountDetails, serviceErrors)).thenReturn(broker);

        final List<Product> productList = new ArrayList<>();
        ProductImpl product = new ProductImpl();
        product.setShortName("PROD.OFFER.65f52dc6d17421eaf1632ac7");
        productList.add(product);
        product = new ProductImpl();
        product.setShortName("PROD.OFFER.65F52DC6D");
        productList.add(product);
        when(productIntegrationService.getDealerGroupProductList(broker.getDealerKey(), serviceErrors)).thenReturn(productList);

        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Account level Insurance permission is true", permission.hasPermission("account.super.insurance.view"), is(true));
        assertThat("Insurance commission detail view permission is true", permission.hasPermission("account.insurance.commissiondetails.view"), is(true));
        assertThat("Account level menu permission is true", permission.hasPermission(SUPER_GLOBAL_INSURANCE_MENU_VIEW), is(true));
    }

    @Test
    public void testInsurancePermissionAtAccLevelWithNoProductOffering() {
        FunctionalRole[] roleList = new FunctionalRole[]{FunctionalRole.View_Insurance_Account, FunctionalRole.View_account_reports};
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED, roleList);
        accountDetails = getSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.PENSION);
        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("broker2"), BrokerType.DEALER);
        when(brokerHelperService.getDealerGroupForInvestor(accountDetails, serviceErrors)).thenReturn(broker);

        final List<Product> productList = new ArrayList<>();
        when(productIntegrationService.getDealerGroupProductList(broker.getDealerKey(), serviceErrors)).thenReturn(productList);

        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Account level Insurance permission is false", permission.hasPermission("account.super.insurance.view"), is(false));
        assertThat("Insurance commission detail view permission is false", permission.hasPermission("account.insurance.commissiondetails.view"), is(false));
        assertThat("Account level menu permission is false", permission.hasPermission(SUPER_GLOBAL_INSURANCE_MENU_VIEW), is(false));
    }

    @Test
    public void directSuperPermissionsPensionCommenced() {
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.DIRECT);
        accountDetails = getSuperPensionAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.PENSION, DateTime.parse("2016-01-01"));
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        permission.setPermission("account.payment.transaction.view", true);

        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Optimise super permission is false", permission.hasPermission("account.super.optimise.view"), is(false));
        assertThat("Maximise pension permission is true", permission.hasPermission("account.super.pension.maximise.view"), is(true));
        assertThat("Rollover permission is false", permission.hasPermission("account.super.rollovers.investor.menu.view"), is(false));
        assertThat("Linked accounts menu is visible", permission.hasPermission("account.linked.menu.view"), is(true));
        assertThat("Scheduled transactions menu is visible", permission.hasPermission("account.transaction.scheduled.view"), is(false));
        assertThat("Make a contribution is not visible", permission.hasPermission(SUPER_ACCOUNT_CONTRIBUTIONS_MENU_VIEW), is(false));
        assertThat("Make a contribution info only is not visible", permission.hasPermission(SUPER_ACCOUNT_CONTRIBUTIONS_INFO_MENU_VIEW), is(false));
        assertThat("Transfer money in menu is not visible", permission.hasPermission(SUPER_PENSION_TRANSFER_MONEY_MENU_VIEW), is(false));
        assertThat("Access you super in menu is not visible", permission.hasPermission(SUPER_ACCUMULATION_ACCESS_MENU_VIEW), is(false));
        assertThat("Tax & preservation in menu is not visible", permission.hasPermission(SUPER_ACCOUNT_TAXPRESERVATION_VIEW), is(false));
        assertThat("Pension payments in menu is visible", permission.hasPermission(SUPER_PENSION_PAYMENT_MENU_VIEW), is(true));
        assertThat("Commence your pension in menu is not visible", permission.hasPermission(SUPER_PENSION_COMMENCE_MENU_VIEW), is(false));
    }

    @Test
    public void directSuperPermissionsPensionCommenced_noPaymentPermission() {
        profile = getProfileWithNoPaymentCreatePermissions(JobRole.INVESTOR, "job id 1", "client1", UserExperience.DIRECT);
        accountDetails = getSuperPensionAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.PENSION, DateTime.parse("2016-01-01"));
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        permission.setPermission("account.payment.transaction.view", true);

        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Rollover permission is false", permission.hasPermission("account.super.rollovers.investor.menu.view"), is(false));
    }

    @Test
    public void directSuperPermissionsPensionNonCommenced() {
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.DIRECT);
        accountDetails = getSuperPensionAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.PENSION, null);
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        permission.setPermission("account.payment.transaction.view", true);

        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Optimise super permission is false", permission.hasPermission("account.super.optimise.view"), is(false));
        assertThat("Maximise pension permission is true", permission.hasPermission("account.super.pension.maximise.view"), is(true));
        assertThat("Rollover permission is true", permission.hasPermission("account.super.rollovers.investor.menu.view"), is(true));
        assertThat("Linked accounts menu is visible", permission.hasPermission("account.linked.menu.view"), is(true));
        assertThat("Scheduled transactions menu is visible", permission.hasPermission("account.transaction.scheduled.view"), is(false));
        assertThat("Make a payment is not visible", permission.hasPermission("account.payment.transaction.view"), is(false));
        assertThat("Make a contribution is not visible", permission.hasPermission(SUPER_ACCOUNT_CONTRIBUTIONS_MENU_VIEW), is(false));
        assertThat("Make a contribution info only is visible", permission.hasPermission(SUPER_ACCOUNT_CONTRIBUTIONS_INFO_MENU_VIEW), is(false));
        assertThat("Transfer money in menu is visible", permission.hasPermission(SUPER_PENSION_TRANSFER_MONEY_MENU_VIEW), is(true));
        assertThat("Access your super in menu is not visible", permission.hasPermission(SUPER_ACCUMULATION_ACCESS_MENU_VIEW), is(false));
        assertThat("Tax & preservation in menu is not visible", permission.hasPermission(SUPER_ACCOUNT_TAXPRESERVATION_VIEW), is(false));
        assertThat("Pension payments in menu is visible", permission.hasPermission(SUPER_PENSION_PAYMENT_MENU_VIEW), is(true));
        assertThat("Commence your pension in menu is visible", permission.hasPermission(SUPER_PENSION_COMMENCE_MENU_VIEW), is(true));
    }

    @Test
    public void directSuperPermissionsPensionCommencementPending() {
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.DIRECT);
        accountDetails = getPensionAccountPendingCommencement("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        permission.setPermission("account.payment.transaction.view", true);

        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Optimise super permission is false", permission.hasPermission("account.super.optimise.view"), is(false));
        assertThat("Maximise pension permission is true", permission.hasPermission("account.super.pension.maximise.view"), is(true));
        assertThat("Rollover/combine super permission is true", permission.hasPermission("account.super.rollovers.investor.menu.view"), is(true));
        assertThat("Linked accounts menu is visible", permission.hasPermission("account.linked.menu.view"), is(true));
        assertThat("Scheduled transactions menu is not visible", permission.hasPermission("account.transaction.scheduled.view"), is(false));
        assertThat("Make a payment is not visible", permission.hasPermission("account.payment.transaction.view"), is(false));
        assertThat("Make a contribution is not visible", permission.hasPermission(SUPER_ACCOUNT_CONTRIBUTIONS_MENU_VIEW), is(false));
        assertThat("Make a contribution info only is visible", permission.hasPermission(SUPER_ACCOUNT_CONTRIBUTIONS_INFO_MENU_VIEW), is(false));
        assertThat("Transfer money in menu is visible", permission.hasPermission(SUPER_PENSION_TRANSFER_MONEY_MENU_VIEW), is(true));
        assertThat("Access your super in menu is not visible", permission.hasPermission(SUPER_ACCUMULATION_ACCESS_MENU_VIEW), is(false));
        assertThat("Tax & preservation in menu is not visible", permission.hasPermission(SUPER_ACCOUNT_TAXPRESERVATION_VIEW), is(false));
        assertThat("Pension payments in menu is visible", permission.hasPermission(SUPER_PENSION_PAYMENT_MENU_VIEW), is(true));
        assertThat("Commence your pension in menu is visible", permission.hasPermission(SUPER_PENSION_COMMENCE_MENU_VIEW), is(true));
    }

    @Test
    public void directSuperPermissionsPensionNonCommenced_noPaymentPermission() {
        profile = getProfileWithNoPaymentCreatePermissions(JobRole.INVESTOR, "job id 1", "client1", UserExperience.DIRECT);
        accountDetails = getSuperPensionAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.PENSION, null);
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);

        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Rollover permission is false", permission.hasPermission("account.super.rollovers.investor.menu.view"), is(false));
        assertThat("Pension payments in menu is not visible", permission.hasPermission(SUPER_PENSION_PAYMENT_MENU_VIEW), is(false));
    }

    @Test
    public void directSuperPermissionsAccumulation_WithPaymentPermission() {
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.DIRECT);
        accountDetails = getSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits, AccountSubType.ACCUMULATION);
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());
        permission.setPermission("account.payment.transaction.view", true);

        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Optimise super permission is true", permission.hasPermission("account.super.optimise.view"), is(true));
        assertThat("Maximise pension permission is false", permission.hasPermission("account.super.pension.maximise.view"), is(false));
        assertThat("Rollover permission is true", permission.hasPermission("account.super.rollovers.investor.menu.view"), is(true));
        assertThat("Linked accounts menu is not visible", permission.hasPermission("account.linked.menu.view"), is(false));
        assertThat("Scheduled transactions menu is not visible", permission.hasPermission("account.transaction.scheduled.view"), is(false));
        assertThat("Make a payment is not visible", permission.hasPermission("account.payment.transaction.view"), is(false));
        assertThat("Make a contribution is not visible", permission.hasPermission(SUPER_ACCOUNT_CONTRIBUTIONS_MENU_VIEW), is(false));
        assertThat("Make a contribution info only is visible", permission.hasPermission(SUPER_ACCOUNT_CONTRIBUTIONS_INFO_MENU_VIEW), is(true));
        assertThat("Transfer money in menu is not visible", permission.hasPermission(SUPER_PENSION_TRANSFER_MONEY_MENU_VIEW), is(false));
        assertThat("Access you super in menu is visible", permission.hasPermission(SUPER_ACCUMULATION_ACCESS_MENU_VIEW), is(true));
        assertThat("Tax & preservation in menu is not visible", permission.hasPermission(SUPER_ACCOUNT_TAXPRESERVATION_VIEW), is(false));
    }

    @Test
    public void directSuperPermissionsAccumulation_NoPaymentPermission() {
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.DIRECT);
        accountDetails = getSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.No_Transaction, AccountSubType.ACCUMULATION);
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), profile.getUserExperience(), profile.getJobRole());

        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat("Optimise super permission is true", permission.hasPermission("account.super.optimise.view"), is(true));
        assertThat("Maximise pension permission is false", permission.hasPermission("account.super.pension.maximise.view"), is(false));
        assertThat("Rollover permission is true", permission.hasPermission("account.super.rollovers.menu.view"), is(false));
        assertThat("Linked accounts menu is not visible", permission.hasPermission("account.linked.menu.view"), is(false));
        assertThat("Scheduled transactions menu is not visible", permission.hasPermission("account.transaction.scheduled.view"), is(false));
        assertThat("Make a payment is not visible", permission.hasPermission("account.payment.transaction.view"), is(false));
        assertThat("Make a contribution is not visible", permission.hasPermission(SUPER_ACCOUNT_CONTRIBUTIONS_MENU_VIEW), is(false));
        assertThat("Make a contribution info only is visible", permission.hasPermission(SUPER_ACCOUNT_CONTRIBUTIONS_INFO_MENU_VIEW), is(true));
        assertThat("Transfer money in menu is not visible", permission.hasPermission(SUPER_PENSION_TRANSFER_MONEY_MENU_VIEW), is(false));
        assertThat("Access you super in menu is visible", permission.hasPermission(SUPER_ACCUMULATION_ACCESS_MENU_VIEW), is(true));
        assertThat("Tax & preservation in menu is not visible", permission.hasPermission(SUPER_ACCOUNT_TAXPRESERVATION_VIEW), is(false));
    }

    @Test
    public void testNonSuperAccountPermissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);

        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));
        superAccountDetailsView("Non Super Account with Account Reports View", profile, false);
        superAccountDetailsView("Non Super Account without Account Reports View", profileWithoutAccountView, false);

        superTaxPreservationView("Non Super Account with Tax Preservation View", profile, false);
        superTaxPreservationView("Non Super Account without Tax Preservation View", profileWithoutAccountView, false);

        superContributionView("Non Super Account with Contribution View", profile, false);
        superContributionView("Non Super Account without Contribution View", profileWithoutAccountView, false);

        superBenefAutoRevUpdate("Non Super Account and Update Beneficiaries Role", profile, true, true, false);
        superBenefAutoRevUpdate("Non Super Account without Update Beneficiaries Role", profile, false, false, false);

        superLinkedAccountsPensionView("Non Super Account with Account View Reports and LinkedPension", profile, false);
        superLinkedAccountsPensionView("Non Super Account without Account View Reports and LinkedPension", profileWithoutAccountView, false);

        superLinkedAccountsAccumulationView("Non Super Account with View Reports and LinkedAccumulation", profile, false);
        superLinkedAccountsPensionView("Non Super Account and Non View Reports and LinkedAccumulation", profileWithoutAccountView, false);

        superBenefAutoRevView("Non Super Account with AutoReversionary Beneficiaries View", profile, false);
        superBenefAutoRevView("Non Super Account without AutoReversionary Beneficiaries View", profileWithoutAccountView, false);

        superAccOverviewContriSummary("Non Super Account and Account Contribution Summary", profile, false);
        superAccOverviewContriSummary("Non Super Account without Account Contribution Summary", profileWithoutAccountView, false);

        superTaxDeductionView("Non Super Account with Tax deduction View", profile, false);
        superTaxDeductionView("Non Super Account without Tax deduction View", profileWithoutAccountView, false);

        superTaxDeductionUpdate("Non Super Account Tax Deduction Role", profile, true, true, false);
        superTaxDeductionUpdate("Non Super Account Without Tax Deduction Role", profileWithoutTaxDeductionUpdate, false, false, false);
    }


    public WrapAccountDetail getSuperAccount(String customerId, BrokerKey adviserKey, TransactionPermission userPermission, AccountSubType accSubType) {
        WrapAccountDetailImpl account = new WrapAccountDetailImpl();
        account.setAccountKey(AccountKey.valueOf("11918"));
        account.setAdviserKey(adviserKey);
        account.setAdviserPersonId(ClientKey.valueOf(customerId));
        account.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits));
        account.setAssociatedPersons(getAssociatedPersons(customerId, userPermission));
        account.setAccountStructureType(AccountStructureType.SUPER);
        account.setSuperAccountSubType(accSubType);
        return account;
    }

    public WrapAccountDetail getNonSuperAccount(String customerId, BrokerKey adviserKey, TransactionPermission userPermission) {
        WrapAccountDetailImpl account = new WrapAccountDetailImpl();
        account.setAccountKey(AccountKey.valueOf("11918"));
        account.setAdviserKey(adviserKey);
        account.setAdviserPersonId(ClientKey.valueOf(customerId));
        account.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits));
        account.setAssociatedPersons(getAssociatedPersons(customerId, userPermission));
        account.setAccountStructureType(AccountStructureType.SMSF);
        return account;
    }

    public PensionAccountDetailImpl getSuperPensionAccount(String customerId, BrokerKey adviserKey, TransactionPermission userPermission, AccountSubType accSubType, DateTime date) {
        PensionAccountDetailImpl account = new PensionAccountDetailImpl();
        account.setAccountKey(AccountKey.valueOf("11918"));
        account.setAdviserKey(adviserKey);
        account.setAdviserPersonId(ClientKey.valueOf(customerId));
        account.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits));
        account.setAssociatedPersons(getAssociatedPersons(customerId, userPermission));
        account.setAccountStructureType(AccountStructureType.SUPER);
        account.setSuperAccountSubType(accSubType);
        account.setCommenceDate(date);
        return account;
    }

    public PensionAccountDetailImpl getPensionAccountPendingCommencement(String customerId, BrokerKey adviserKey, TransactionPermission userPermission) {
        PensionAccountDetailImpl account = getSuperPensionAccount(customerId,adviserKey, userPermission, AccountSubType.PENSION, null);
        account.setCommencementPending(true);
        return account;
    }

    public WrapAccountDetail getCompanyRegistration(String customerId, BrokerKey adviserKey, TransactionPermission userPermission) {

        Set<TransactionPermission> permissionSet = new HashSet<>();
        permissionSet.add(TransactionPermission.Payments_Deposits);
        permissionSet.add(TransactionPermission.Company_Registration);
        permissionSet.add(TransactionPermission.Account_Maintenance);
        permissionSet.add(TransactionPermission.No_Transaction);
        permissionSet.add(TransactionPermission.Payments_Deposits);
        permissionSet.add(TransactionPermission.Payments_Deposits_To_Linked_Accounts);

        WrapAccountDetailImpl account = new WrapAccountDetailImpl();
        account.setAccountKey(AccountKey.valueOf("11918"));
        account.setAdviserKey(adviserKey);
        account.setAdviserPersonId(ClientKey.valueOf(customerId));
        account.setAdviserPermissions(permissionSet);
        account.setAssociatedPersons(getAssociatedPersons(customerId, userPermission));
        account.setAccountStructureType(AccountStructureType.SUPER);
        return account;
    }

    public void superAccountDetailsView(String infoStr, UserProfile profile, boolean expected) {
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_ACCOUNT_DETAILS_VIEW), equalTo(expected));
    }

    public void superTaxPreservationView(String infoStr, UserProfile profile, boolean expected) {
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_ACCOUNT_TAXPRESERVATION_VIEW), equalTo(expected));
    }

    public void superContributionView(String infoStr, UserProfile profile, boolean expected) {
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_ACCOUNT_CONTRIBUTION_VIEW), equalTo(expected));
    }

    public void superLinkedAccountsPensionView(String infoStr, UserProfile profile, boolean expected) {
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_LINKED_ACCOUNTS_PENSION_VIEW), equalTo(expected));
    }

    public void superLinkedAccountsAccumulationView(String infoStr, UserProfile profile, boolean expected) {
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_LINKED_ACCOUNTS_ACCUMULATION_VIEW), equalTo(expected));
    }

    public void superBenefAutoRevView(String infoStr, UserProfile profile, boolean expected) {
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_ACCOUNT_BENEFICIARIES_AUTO_REV_VIEW), equalTo(expected));
    }

    public void superBenefAutoRevUpdate(String infoStr, UserProfile profile, boolean asimPermissionValue,
                                        boolean permissionContainsRole, boolean expected) {
        when(asimPermission.overrideValue(permissionContainsRole, false, false, JobRoleType.INVESTOR)).thenReturn(asimPermissionValue);
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_ACCOUNT_BENEFICIARIES_AUTO_REV_UPDATE), equalTo(expected));
    }

    public void superAccOverviewContriSummary(String infoStr, UserProfile profile, boolean expected) {
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_ACCOUNT_OVERVIEW_CONTRIBUTION_SUMMARY_VIEW), equalTo(expected));
    }

    public void superTaxDeductionView(String infoStr, UserProfile profile, boolean expected) {
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_PERSONAL_TAX_DEDUCTION_VIEW), equalTo(expected));
    }

    public void superTaxDeductionUpdate(String infoStr, UserProfile profile, boolean asimPermissionValue,
                                        boolean permissionContainsRole, boolean expected) {
        when(asimPermission.overrideValue(permissionContainsRole, false, false, JobRoleType.INVESTOR)).thenReturn(asimPermissionValue);
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_PERSONAL_TAX_DEDUCTION_UPDATE), equalTo(expected));
    }

    public void superAccumulationOneOffWithdrawal(String infoStr, UserProfile profile, boolean expected) {
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_ACCOUNT_ACCUMULATION_ONEOFF_WITHDRAWAL), equalTo(expected));
    }

    public void superAccumulationAccessingMenuView(String infoStr, UserProfile profile, boolean expected) {
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_ACCOUNT_ACCUMULATION_ACCESSING_MENU_VIEW), equalTo(expected));
    }

    public void superAccumulationOneOffCreateWithdrawal(String infoStr, UserProfile profile, boolean expected) {
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_ACCOUNT_ACCUMULATION_ONEOFF_WITHDRAWAL_CREATE), equalTo(expected));
    }

    public void superAccumulationOneOffViewWithdrawal(String infoStr, UserProfile profile, boolean expected) {
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_ACCOUNT_ACCUMULATION_ONEOFF_WITHDRAWAL_VIEW), equalTo(expected));
    }

    public void superCommencePensionView(String infoStr, UserProfile profile, boolean expected) {
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_COMMENCE_PENSION_VIEW), equalTo(expected));
    }

    public void superCommencePensionUpdate(String infoStr, UserProfile profile, boolean asimPermissionValue,
                                           boolean permissionContainsRole, boolean expected) {
        when(asimPermission.overrideValue(permissionContainsRole, false, false, JobRoleType.INVESTOR)).thenReturn(asimPermissionValue);
        PermissionsDto permission = setupAccountAndProfile(profile);
        assertThat(infoStr, permission.hasPermission(SUPER_COMMENCE_PENSION_UPDATE), equalTo(expected));
    }

    public PermissionsDto setupAccountAndProfile(UserProfile profile) {
        permission = new PermissionsDto();
        PermissionServiceUtil.setFunctionalPermissions(permission, profile.getFunctionalRoles(), UserExperience.ADVISED, profile.getJobRole());
        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        return permission;
    }

    public void testIndirectSetSuperAccountPermission_whenSuperUserAccountHasLinkedAccountDepositPermission_thenContributionMenuPermissionIsSet() {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        accountDetails = getSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits_To_Linked_Accounts, AccountSubType.ACCUMULATION);
        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat(permission.hasPermission("account.super.contributions.menu.view"), equalTo(true));
    }

    public void testIndirectSetSuperAccountPermission_whenSuperUserAccountHasNoTransaction_thenContributionMenuPermissionIsSet() {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        accountDetails = getSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.No_Transaction, AccountSubType.ACCUMULATION);
        superPermissionService.setSuperPermissions(permission, accountDetails, profile.getJobRole(), serviceErrors);
        assertThat(permission.hasPermission("account.super.contributions.menu.view"), equalTo(false));
    }
}
