package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.api.subscriptions.model.SubscriptionDto;
import com.bt.nextgen.api.subscriptions.service.SubscriptionDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.security.AvaloqFunctionalRolePermissionEvaluator;
import com.bt.nextgen.core.security.api.model.JobAuthorizationGroupEnum;
import com.bt.nextgen.core.security.api.model.PermissionAccountKey;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRoleType;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.BlockCode;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.JobAuthorizationRole;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.options.model.CategoryType;
import com.bt.nextgen.service.integration.options.model.OptionValue;
import com.bt.nextgen.service.integration.options.model.OptionValueKey;
import com.bt.nextgen.service.integration.options.model.ToggleOptionValueImpl;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PermissionAccountDtoServiceTest extends PermissionAccountDtoServiceBase {

    @InjectMocks
    private PermissionAccountDtoServiceImpl service;

    @Mock
    AccountIntegrationService accountService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private CmsService cmsService;

    @Mock
    private UserInformationIntegrationService userInformationService;

    @Mock
    private AvaloqFunctionalRolePermissionEvaluator rolePermissionEvaluator;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private Broker directBroker;

    @Mock
    private OptionsService optionService;

    @Mock
    SubscriptionDtoService subscriptionDtoService;

    @Mock
    private AsimPermission asimPermission;

    @Mock
    private SuperPermissionDtoService superPermissionDtoService;

    @Spy
    @InjectMocks
    private InvestorPermissionHelperImpl investorPermissionHelper;

    private PermissionAccountKey accountKey;
    private WrapAccountDetail accountDetails;
    private ServiceErrors serviceErrors;
    private UserProfile profile;
    private BrokerUser broker;
    private List<SubscriptionDto> subsList;
    private List<Broker> brokerList;

    private static final String SMSF_VIEW_BGL_KEY = "account.bgl.view";
    private static final String SMSF_VIEW_KEY = "account.smsf.view";
    private static final String SMSF_VIEW_CASH_CATEGORISATION_KEY = "account.portfolio.cashcategorisation.view";
    private static final String SMSF_ACCOUNT_ACCOUNTING_SOFTWARE_CONNECTION_TOGGLE = "account.accounting.software.connection.toggle";


    @Before
    public void setup() {
        accountKey = new PermissionAccountKey(EncodedString.fromPlainText("11918").toString());
        serviceErrors = new ServiceErrorsImpl();
        subsList = getDoneSubscriptionList(ApplicationStatus.DONE.getStatus());

        final ArrayList<OptionValue<Boolean>> options = new ArrayList<>();
        options.add(new ToggleOptionValueImpl(OptionValueKey.valueOf(CategoryType.STRUCTURE, "SMSF", "test.accountOption"), true));
        options.add(new ToggleOptionValueImpl(OptionValueKey.valueOf(CategoryType.PRODUCT, "SUP", "test.productOption"), true));

        when(subscriptionDtoService.search(any(com.bt.nextgen.api.account.v2.model.AccountKey.class), any(ServiceErrors.class))).thenReturn(subsList);
        when(optionService.getFeatures(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(options);
        when(asimPermission.overrideValue(anyBoolean(), anyBoolean(), anyBoolean(), any(JobRoleType.class))).thenReturn(true);
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
    }

    @Test
    public void testSearch_containsBlockAllPermissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), null);
        accountDetails = getBlockedAccount(BlockCode.Blocked_for_All, "client1", BrokerKey.valueOf("broker1"),
                TransactionPermission.Payments_Deposits);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);

        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("account.payment.bpay"), equalTo(false));
        assertThat(permission.hasPermission("account.payment.anyone"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), equalTo("Err.IP-0276"));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
        assertThat(permission.hasPermission("account.closed"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.all.block.view"), equalTo("Err.IP-0276"));
    }

    @Test
    public void testSearch_containsBlockAllExceptInterestPermissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), null);
        accountDetails = getBlockedAccount(BlockCode.Blocked_for_All_except_Interest_Payment_and_Tax, "client1",
                BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);

        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("account.payment.bpay"), equalTo(false));
        assertThat(permission.hasPermission("account.payment.anyone"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), equalTo("Err.IP-0276"));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
        assertThat(permission.hasPermission("account.closed"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.all.block.view"), equalTo("Err.IP-0276"));
    }

    @Test
    public void testSearch_containsBlockForOutgoingPermissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), null);
        accountDetails = getBlockedAccount(BlockCode.Blocked_for_Trades_Buy_and_Outgoing_Payment, "client1",
                BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);

        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("account.payment.bpay"), equalTo(false));
        assertThat(permission.hasPermission("account.payment.anyone"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), equalTo("Err.IP-0276"));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
        assertThat(permission.hasPermission("account.closed"), equalTo(false));
        assertThat(permission.hasPermission("account.all.block.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsBlockForBuyAndSellPermissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), null);
        accountDetails = getBlockedAccount(BlockCode.Blocked_for_Trades_Buy_and_Sell, "client1", BrokerKey.valueOf("broker1"),
                TransactionPermission.Payments_Deposits);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);

        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("account.payment.bpay"), equalTo(false));
        assertThat(permission.hasPermission("account.payment.anyone"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), equalTo("Err.IP-0276"));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
        assertThat(permission.hasPermission("account.closed"), equalTo(false));
        assertThat(permission.hasPermission("account.all.block.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsBlockForTradeSanctionsPermissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), null);
        accountDetails = getBlockedAccount(BlockCode.Blocked_for_All_Trade_Sanctions, "client1", BrokerKey.valueOf("broker1"),
                TransactionPermission.Payments_Deposits);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);

        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("account.payment.bpay"), equalTo(false));
        assertThat(permission.hasPermission("account.payment.anyone"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), equalTo("Err.IP-0276"));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
        assertThat(permission.hasPermission("account.closed"), equalTo(false));
        assertThat(permission.hasPermission("account.all.block.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsBlockForIncomingPermissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), null);
        accountDetails = getBlockedAccount(BlockCode.Blocked_for_Incoming_Payment, "client1", BrokerKey.valueOf("broker1"),
                TransactionPermission.Payments_Deposits);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);

        assertThat(permission, notNullValue());
        assertThat(permission.hasPermissionMessage("account.deposit.block.view"), equalTo("Err.IP-0276"));
        assertThat(permission.hasPermission("account.payment.anyone"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), equalTo("Err.IP-0276"));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
        assertThat(permission.hasPermission("account.closed"), equalTo(false));
        assertThat(permission.hasPermission("account.all.block.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsBlockForTradeOutgoingPermissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), null);
        accountDetails = getBlockedAccount(BlockCode.Blocked_for_Trades_Buy_and_Sell_and_Outgoing_Payment, "client1",
                BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);

        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("account.payment.bpay"), equalTo(false));
        assertThat(permission.hasPermission("account.payment.anyone"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), equalTo("Err.IP-0276"));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
        assertThat(permission.hasPermission("account.closed"), equalTo(false));
        assertThat(permission.hasPermission("account.all.block.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsBlockForOutgoingPaymentPermissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), null);
        accountDetails = getBlockedAccount(BlockCode.Blocked_for_Outgoing_Payment, "client1", BrokerKey.valueOf("broker1"),
                TransactionPermission.Payments_Deposits);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("account.payment.bpay"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.payment.block.view"), equalTo("Err.IP-0276"));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), equalTo("Err.IP-0276"));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
        assertThat(permission.hasPermission("account.closed"), equalTo(false));
        assertThat(permission.hasPermission("account.all.block.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsAdviserNoBlockPermissions() throws Exception {
        profile = getProfileWithNoInvestmentOrderPermissions(JobRole.ADVISER, "job id 1", "client1");
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), null);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.Add_remove_update_adviser_role_on_user,
                FunctionalRole.Make_a_BPAYPay_Anyone_Payment);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class)))
                .thenReturn(roleList);
        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), nullValue());
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(false));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
        assertThat(permission.hasPermission("account.closed"), equalTo(false));
    }

    @Test
    public void testSearch_containsClosedAccountPermissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), null);
        accountDetails = getClosedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getDynamicContent(Mockito.anyString(), any(String[].class))).thenReturn("Err.IP-0350");
        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("account.payment.bpay"), equalTo(false));
        assertThat(permission.hasPermission("account.payment.anyone"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), equalTo("Err.IP-0350"));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
        assertThat(permission.hasPermission("account.closed"), equalTo(true));
    }

    @Test
    public void testSearch_containsParaplannerReadOnlyPermissionsByAdviser() throws Exception {
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.PARAPLANNER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Support_ReadOnly);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.Trade_entry,
                FunctionalRole.Add_remove_update_adviser_role_on_user, FunctionalRole.Make_a_BPAYPay_Anyone_Payment,
                FunctionalRole.View_Client_Orders);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class)))
                .thenReturn(roleList);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), nullValue());
        assertThat(permission.hasPermission("account.deposit.linked.create"), equalTo(false));
        assertThat(permission.hasPermission("account.payee.view"), equalTo(false));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsParaplannerReadOnlyPermissionsByDealerGroup() throws Exception {
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.PARAPLANNER, BrokerKey.valueOf("broker3"), JobAuthorizationRole.Supervisor_ReadOnly);
        brokerList = Arrays.asList(getBroker(BrokerType.DEALER, "broker3"));
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.Trade_entry,
                FunctionalRole.Add_remove_update_adviser_role_on_user, FunctionalRole.View_Client_Orders,
                FunctionalRole.Make_a_BPAYPay_Anyone_Payment);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(brokerList);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class)))
                .thenReturn(roleList);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), nullValue());
        assertThat(permission.hasPermission("account.deposit.linked.create"), equalTo(false));
        assertThat(permission.hasPermission("account.payee.view"), equalTo(false));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsParaplannerNoMatchingBroker() throws Exception {
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.PARAPLANNER, BrokerKey.valueOf("nobroker"), JobAuthorizationRole.Support_ReadOnly);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.Add_remove_update_adviser_role_on_user,
                FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class)))
                .thenReturn(roleList);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), nullValue());
        assertThat(permission.hasPermission("account.deposit.linked.create"), equalTo(false));
        assertThat(permission.hasPermission("account.payee.view"), equalTo(false));
        assertThat(permission.hasPermission("account.application.create"), equalTo(false));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsParaplanner_noBrokerFound() throws Exception {
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.Add_remove_update_adviser_role_on_user,
                FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(null);
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class)))
                .thenReturn(roleList);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), nullValue());
        assertThat(permission.hasPermission("account.deposit.linked.create"), equalTo(false));
        assertThat(permission.hasPermission("account.payee.view"), equalTo(false));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsParaplannerReadOnlyPermissionsByAccount() throws Exception {
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.PARAPLANNER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Support_With_Cash);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.No_Transaction);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.Add_remove_update_adviser_role_on_user,
                FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class)))
                .thenReturn(roleList);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), nullValue());
        assertThat(permission.hasPermission("account.deposit.linked.create"), equalTo(false));
        assertThat(permission.hasPermission("account.payee.view"), equalTo(false));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsParaplannerReadOnlyPermissionsByAccountWithMaintenance() throws Exception {
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.PARAPLANNER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Supervisor_With_Cash);
        accountDetails = getNonBlockedAccountWithAccountMaintenance("client1", BrokerKey.valueOf("broker1"),
                TransactionPermission.Account_Maintenance);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.Add_remove_update_adviser_role_on_user,
                FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class)))
                .thenReturn(roleList);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), nullValue());
        assertThat(permission.hasPermission("account.deposit.linked.create"), equalTo(false));
        assertThat(permission.hasPermission("account.payee.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsAdminWithCashPermissionsByAdviserAndAccount() throws Exception {
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.PARAPLANNER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Support_With_Cash);
        brokerList = Arrays.asList(getBroker(BrokerType.ADVISER, "broker1"));
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.Trade_entry,
                FunctionalRole.Add_remove_update_adviser_role_on_user, FunctionalRole.Make_a_BPAYPay_Anyone_Payment,
                FunctionalRole.View_Client_Orders);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(brokerList);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class)))
                .thenReturn(roleList);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), nullValue());
        assertThat(permission.hasPermission("account.deposit.linked.create"), equalTo(true));
        assertThat(permission.hasPermission("account.payment.bpay.create"), equalTo(true));
        assertThat(permission.hasPermission("account.payee.view"), equalTo(false));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsParaplannerLinkedDepositPermissionsByAccount() throws Exception {
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.PARAPLANNER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Support_With_Cash);
        brokerList = Arrays.asList(getBroker(BrokerType.ADVISER, "broker1"));
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"),
                TransactionPermission.Payments_Deposits_To_Linked_Accounts);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.View_Client_Orders,
                FunctionalRole.Add_remove_update_adviser_role_on_user, FunctionalRole.Make_a_BPAYPay_Anyone_Payment,
                FunctionalRole.Trade_entry);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(brokerList);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class)))
                .thenReturn(roleList);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), nullValue());
        assertThat(permission.hasPermission("account.deposit.linked.create"), equalTo(true));
        assertThat(permission.hasPermission("account.payment.anyone.create"), equalTo(false));
        assertThat(permission.hasPermission("account.payment.bpay.create"), equalTo(false));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsParaplannerLinkedDepositPermissionsByAdviser() throws Exception {
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.PARAPLANNER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Support_Without_Cash);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.Add_remove_update_adviser_role_on_user,
                FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class)))
                .thenReturn(roleList);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), nullValue());
        assertThat(permission.hasPermission("account.deposit.linked.create"), equalTo(false));
        assertThat(permission.hasPermission("account.payment.anyone.create"), equalTo(false));
        assertThat(permission.hasPermission("account.payment.bpay.create"), equalTo(false));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
    }

    @Test
    public void testSearch_containsAdviserFullPermission() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Supervisor_Transact);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.Add_remove_update_adviser_role_on_user,
                FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class)))
                .thenReturn(roleList);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("default"), equalTo(false));
        assertThat(permission.hasPermissionMessage("account.summary.bar.view"), nullValue());
        assertThat(permission.hasPermission("account.deposit.linked.create"), equalTo(true));
        assertThat(permission.hasPermission("account.payment.anyone.create"), equalTo(true));
        assertThat(permission.hasPermission("account.payment.bpay.create"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentorders.menu.view"), equalTo(true));
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(false));
    }

    @Test
    public void test_noTradePermissionForRegularInvestor() throws Exception {
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.ADVISED,
                FunctionalRole.Make_a_payment_linked_accounts,
                FunctionalRole.Trade_entry, FunctionalRole.View_Client_Orders,
                FunctionalRole.Submit_trade_to_executed, FunctionalRole.Purchase_Term_Deposits,
                FunctionalRole.Submit_RIP);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Supervisor_Transact);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class)))
                .thenReturn(UserExperience.ADVISED);

        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("account.direct.view"), equalTo(false));
        assertThat(permission.hasPermission("account.trade.entry"), equalTo(false));
        assertThat(permission.hasPermission("account.trade.create"), equalTo(false));
        assertThat(permission.hasPermission("account.trade.submit"), equalTo(false));
        assertThat(permission.hasPermission("account.order.view"), equalTo(false));
        assertThat(permission.hasPermission("account.regular.investment.submit"), equalTo(false));
        assertThat(permission.hasPermission("account.payment.linked.create"), equalTo(true));
        assertThat(permission.hasPermission("account.deposit.linked.create"), equalTo(true));
    }

    @Test
    public void testSearch_whenAssetTranserRole_thenContainsPermissions() throws Exception {
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.ASIM,
                FunctionalRole.Submit_Inspecie_Transfer);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Supervisor_Transact);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class)))
                .thenReturn(UserExperience.ASIM);

        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
    }

    @Test
    public void testSearch_whenUpdateAccountRole_thenContainsPermissions() throws Exception {
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.ASIM,
                FunctionalRole.View_intermediary_reports);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Supervisor_Transact);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class)))
                .thenReturn(UserExperience.ADVISED);

        PermissionsDto permission = service.find(accountKey, serviceErrors);
        assertThat(permission, notNullValue());
        assertThat(permission.hasPermission("account.investmentpreferences.view"), equalTo(true));
    }

    @Test
    public void test_hasTransactionPermission() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Supervisor_Transact);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.Add_remove_update_adviser_role_on_user,
                FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class)))
                .thenReturn(roleList);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        boolean permission = service.hasTransactionPermission(accountKey.getAccountId(),
                "account.deposit.linked.create");
        assertThat(permission, equalTo(true));
    }

    @Test
    public void test_canTransactionPermission() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Supervisor_Transact);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.Add_remove_update_adviser_role_on_user,
                FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(broker);
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class))).thenReturn(roleList);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        boolean permission = service.canTransact(accountKey.getAccountId(), "account.deposit.linked.create");
        assertThat(permission, equalTo(true));
    }

    @Test
    public void test_canTransactionFunctionalRolePermission() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Supervisor_Transact);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.Create_a_complaint_feedback, FunctionalRole.Add_remove_update_adviser_role_on_user,
                FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(broker);
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class))).thenReturn(roleList);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        when(rolePermissionEvaluator.hasPermission(null, null, "$FR_PAY_UI_LINKED_ACC_MNG")).thenReturn(true);
        boolean permission = service.canTransact(accountKey.getAccountId(), "$FR_PAY_UI_LINKED_ACC_MNG", "account.deposit.linked.create");
        assertThat(permission, equalTo(true));
    }

    @Test
    public void test_canTransactionFunctionalRolePermission_missingParams() throws Exception {
        boolean permission = service.canTransact(null, "$FR_PAY_UI_LINKED_ACC_MNG", "account.deposit.linked.create");
        assertThat(permission, equalTo(false));
    }

    @Test
    public void test_canTransactionPermission_missingParams() throws Exception {
        boolean permission = service.canTransact(null, "account.deposit.linked.create");
        assertThat(permission, equalTo(false));
    }

    @Test
    public void testFilterInvestorPermissions_whenAdviserRole_thenPermissionsRetained() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED, FunctionalRole.View_Client_Orders,
                FunctionalRole.Trade_entry, FunctionalRole.View_client_messages);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker1"), JobAuthorizationRole.Supervisor_Transact);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(Mockito.any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class)))
                .thenReturn(UserExperience.ADVISED);

        PermissionsDto permissions = new PermissionsDto();
        service.setFunctionalRoleOverrides(permissions, null, null, profile, null);
        Assert.assertThat(permissions.hasPermission("account.order.view"), equalTo(true));
        Assert.assertThat(permissions.hasPermission("account.trade.entry"), equalTo(true));
        Assert.assertThat(permissions.hasPermission("intermediary.messages.view"), equalTo(true));
        investorPermissionHelper.updateInvestorPermissions(permissions, accountDetails, null);
        Assert.assertThat(permissions.hasPermission("account.order.view"), equalTo(true));
        Assert.assertThat(permissions.hasPermission("account.trade.entry"), equalTo(true));
        Assert.assertThat(permissions.hasPermission("intermediary.messages.view"), equalTo(true));
    }

    @Test
    public void testGetRolePermissions_whenAdviser_thenRoleNotOverridden() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);

        Map<JobAuthorizationGroupEnum, JobAuthorizationRole> rolePermissions = service.getRolePermissions(profile, accountDetails,
                serviceErrors);
        assertThat(rolePermissions, nullValue());
    }

    @Test
    public void testGetRolePermissions_whenASIM_thenRoleNotOverridden() throws Exception {
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ASIM);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);

        Map<JobAuthorizationGroupEnum, JobAuthorizationRole> rolePermissions = service.getRolePermissions(profile, accountDetails,
                serviceErrors);
        assertThat(rolePermissions, nullValue());
    }

    @Test
    public void testGetRolePermissions_whenParaplanner_thenRoleOverridden() throws Exception {
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);

        Map<JobAuthorizationGroupEnum, JobAuthorizationRole> rolePermissions = service.getRolePermissions(profile, accountDetails,
                serviceErrors);
        assertThat(rolePermissions, notNullValue());
    }

    @Test
    public void testSuper_CompanyRegistrationPermissions() throws Exception {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), null);
        accountDetails = getCompanyRegistration("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Company_Registration);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetails);
        final Set<TransactionPermission> permissionSet = service.getPermissionSet(accountDetails, profile);

        assertThat(permissionSet, notNullValue());
        assertThat(permissionSet.contains(TransactionPermission.Company_Registration), equalTo(false));
        assertThat(permissionSet.contains(TransactionPermission.Account_Maintenance), equalTo(true));
        assertThat(permissionSet.contains(TransactionPermission.No_Transaction), equalTo(true));
        assertThat(permissionSet.contains(TransactionPermission.Payments_Deposits), equalTo(true));
        assertThat(permissionSet.contains(TransactionPermission.Payments_Deposits_To_Linked_Accounts), equalTo(true));
    }

    /**
     * This test case would test the scenario where Dealergroup has Model-related permissions
     */
    @Test
    public void testPermissionAtAccLevelForModel() {
        profile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1", UserExperience.ADVISED);
        broker = getBrokerUser(JobRole.ACCOUNTANT, BrokerKey.valueOf("broker2"), JobAuthorizationRole.Support_ReadOnly);
        accountDetails = getSuperAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits,
                AccountSubType.PENSION);
        List<FunctionalRole> roleList = getFunctionalRoleList(FunctionalRole.View_Insurance_Account,
                FunctionalRole.View_account_reports, FunctionalRole.View_Insurance_Commissions);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(broker);
        when(cmsService.getContent((String) anyObject())).thenReturn("Err.IP-0276");
        when(userInformationService.getFunctionalRoleList(any(List.class), any(ServiceErrors.class))).thenReturn(roleList);

        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("broker2"), BrokerType.DEALER);
        broker.setMPPreferenceActive(Boolean.TRUE);
        broker.setTMPPreferenceActive(Boolean.FALSE);
        broker.setRmpIncomePreferenceEnabled(Boolean.TRUE);
        broker.setTmpIncomePreferenceEnabled(Boolean.FALSE);
        when(brokerHelperService.getDealerGroupForInvestor(accountDetails, serviceErrors)).thenReturn(broker);

        PermissionsDto permissionsDto = service.find(accountKey, serviceErrors);

        assertThat("RMP Preference is set", permissionsDto.hasPermission("modelportfolio.preference.edit"), Matchers.is(true));
        assertThat("TMP Preference is NOT set", permissionsDto.hasPermission("modelportfolio.tailored.preference.edit"),
                Matchers.is(false));

        assertThat("RMP Income preference is enabled", permissionsDto.hasPermission("modelportfolio.incomepreference.view"),
                Matchers.is(true));
        assertThat("TMP Income preference is not enabled",
                permissionsDto.hasPermission("modelportfolio.tailored.incomepreference.view"), Matchers.is(false));
    }

    /**
     * Tests for the SMSF accounting software permissions
     */
    @Test
    public void testSearch_SmsfAccountingSoftwarePermission_investorNonSMSF() {
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.ADVISED, FunctionalRole.Connect_Disconnect_Accounting_Software_Feed);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), JobAuthorizationRole.Support_ReadOnly);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), AccountStructureType.Individual, TransactionPermission.Payments_Deposits);

        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(broker);

        PermissionsDto permissionsDto = service.find(accountKey, serviceErrors);

        assertThat("Accounting software permission is false", permissionsDto.hasPermission(SMSF_ACCOUNT_ACCOUNTING_SOFTWARE_CONNECTION_TOGGLE), Matchers.is(false));
    }

    @Test
    public void testSearch_SmsfAccountingSoftwarePermission_investor_SMSF() {
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.ADVISED, FunctionalRole.Connect_Disconnect_Accounting_Software_Feed);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), JobAuthorizationRole.Support_ReadOnly);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), AccountStructureType.SMSF, TransactionPermission.Payments_Deposits);

        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(broker);

        PermissionsDto permissionsDto = service.find(accountKey, serviceErrors);

        assertThat("View SMSF is true", permissionsDto.hasPermission(SMSF_VIEW_KEY), Matchers.is(true));
        assertThat("View BGL is true", permissionsDto.hasPermission(SMSF_VIEW_BGL_KEY), Matchers.is(true));
        assertThat("View cash categorisation is true", permissionsDto.hasPermission(SMSF_VIEW_CASH_CATEGORISATION_KEY), Matchers.is(false));
        assertThat("Accounting software permission is false", permissionsDto.hasPermission(SMSF_ACCOUNT_ACCOUNTING_SOFTWARE_CONNECTION_TOGGLE), Matchers.is(false));
    }

    @Test
    public void testSearch_SmsfAccountingSoftwarePermission_investor_SMSF_ASIM() {
        profile = getProfile(JobRole.INVESTOR, "job id 1", "client1", UserExperience.ASIM, FunctionalRole.Connect_Disconnect_Accounting_Software_Feed);
        broker = getBrokerUser(JobRole.ADVISER, BrokerKey.valueOf("broker2"), JobAuthorizationRole.Support_ReadOnly);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), AccountStructureType.SMSF, TransactionPermission.Payments_Deposits);

        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.ASIM);
        when(brokerService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(broker);
        when(subscriptionDtoService.search(any(com.bt.nextgen.api.account.v2.model.AccountKey.class), any(ServiceErrors.class))).thenReturn(null);

        PermissionsDto permissionsDto = service.find(accountKey, serviceErrors);

        assertThat("View SMSF is true", permissionsDto.hasPermission(SMSF_VIEW_KEY), Matchers.is(true));
        assertThat("View BGL is true", permissionsDto.hasPermission(SMSF_VIEW_BGL_KEY), Matchers.is(true));
        assertThat("View cash categorisation is true", permissionsDto.hasPermission(SMSF_VIEW_CASH_CATEGORISATION_KEY), Matchers.is(false));
        assertThat("Accounting software permission is true", permissionsDto.hasPermission(SMSF_ACCOUNT_ACCOUNTING_SOFTWARE_CONNECTION_TOGGLE), Matchers.is(true));
    }


    @Test
    public void testSearch_SmsfAccountingSoftwarePermission_adviserNoFASubscription() {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED, FunctionalRole.Connect_Disconnect_Accounting_Software_Feed);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), AccountStructureType.SMSF, TransactionPermission.Payments_Deposits);

        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(subscriptionDtoService.search(any(com.bt.nextgen.api.account.v2.model.AccountKey.class), any(ServiceErrors.class))).thenReturn(null);

        PermissionsDto permissionsDto = service.find(accountKey, serviceErrors);

        assertThat("View SMSF is true", permissionsDto.hasPermission(SMSF_VIEW_KEY), Matchers.is(true));
        assertThat("View BGL is true", permissionsDto.hasPermission(SMSF_VIEW_BGL_KEY), Matchers.is(true));
        assertThat("View cash categorisation is true", permissionsDto.hasPermission(SMSF_VIEW_CASH_CATEGORISATION_KEY), Matchers.is(false));
        assertThat("Accounting software permission is true", permissionsDto.hasPermission(SMSF_ACCOUNT_ACCOUNTING_SOFTWARE_CONNECTION_TOGGLE), Matchers.is(true));
    }

    @Test
    public void testSearch_SmsfAccountingSoftwarePermission_adviserWithFASubscription() {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED, FunctionalRole.Connect_Disconnect_Accounting_Software_Feed);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), AccountStructureType.SMSF, TransactionPermission.Payments_Deposits);
        subsList = getDoneSubscriptionList(ApplicationStatus.DONE_GENERATING_DOC.getStatus());

        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(subscriptionDtoService.search(any(com.bt.nextgen.api.account.v2.model.AccountKey.class), any(ServiceErrors.class))).thenReturn(subsList);

        PermissionsDto permissionsDto = service.find(accountKey, serviceErrors);

        assertThat("View SMSF is true", permissionsDto.hasPermission(SMSF_VIEW_KEY), Matchers.is(true));
        assertThat("View BGL is true", permissionsDto.hasPermission(SMSF_VIEW_BGL_KEY), Matchers.is(true));
        assertThat("View cash categorisation is true", permissionsDto.hasPermission(SMSF_VIEW_CASH_CATEGORISATION_KEY), Matchers.is(false));
        assertThat("Accounting software permission is false", permissionsDto.hasPermission(SMSF_ACCOUNT_ACCOUNTING_SOFTWARE_CONNECTION_TOGGLE), Matchers.is(false));
    }

    @Test
    public void drpPermissions_australianDomicile() {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED, FunctionalRole.Connect_Disconnect_Accounting_Software_Feed);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), AccountStructureType.SMSF, TransactionPermission.Payments_Deposits);
        subsList = getDoneSubscriptionList(ApplicationStatus.DONE_GENERATING_DOC.getStatus());

        Address australianAddress = mock(Address.class);
        when(australianAddress.isDomicile()).thenReturn(true);
        when(australianAddress.getCountryCode()).thenReturn("au");

        Client owner = mock(Client.class);
        when(owner.getAddresses()).thenReturn(Collections.singletonList(australianAddress));
        ((WrapAccountDetailImpl) accountDetails).setOwners(Collections.singletonList(owner));

        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);

        PermissionsDto permissionsDto = service.find(accountKey, serviceErrors);

        assertThat("Update DRP is true", permissionsDto.hasPermission("account.drp.update"), Matchers.is(true));
    }

    @Test
    public void drpPermissions_nonAustralianDomicile() {
        profile = getProfile(JobRole.ADVISER, "job id 1", "client1", UserExperience.ADVISED, FunctionalRole.Connect_Disconnect_Accounting_Software_Feed);
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), AccountStructureType.SMSF, TransactionPermission.Payments_Deposits);
        subsList = getDoneSubscriptionList(ApplicationStatus.DONE_GENERATING_DOC.getStatus());


        Address australianAddress = mock(Address.class);
        when(australianAddress.isDomicile()).thenReturn(true);
        when(australianAddress.getCountryCode()).thenReturn("cad");

        Client owner = mock(Client.class);
        when(owner.getAddresses()).thenReturn(Collections.singletonList(australianAddress));
        ((WrapAccountDetailImpl) accountDetails).setOwners(Collections.singletonList(owner));

        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetails);
        when(userProfileService.getActiveProfile()).thenReturn(profile);

        PermissionsDto permissionsDto = service.find(accountKey, serviceErrors);

        assertThat("Update DRP is false", permissionsDto.hasPermission("account.drp.update"), Matchers.is(false));
    }
}

