package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.api.account.v3.model.DirectOffer;
import com.bt.nextgen.api.account.v3.util.AccountProductsHelper;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.hamcrest.MatcherAssert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.bt.nextgen.service.integration.account.AccountStructureType.SMSF;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvestorPermissionHelperTest extends PermissionAccountDtoServiceBase {

    @InjectMocks
    private InvestorPermissionHelperImpl service;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    AccountProductsHelper accountProductsHelper;

    private WrapAccountDetail accountDetails;
    private ServiceErrors serviceErrors;
    private PermissionsDto permissions;

    @Before
    public void setUp() {
        serviceErrors = new ServiceErrorsImpl();
        final List<FunctionalRole> functionalRoles = Arrays.asList(FunctionalRole.View_Client_Orders, FunctionalRole.View_account_payee_billers,
                FunctionalRole.Trade_entry, FunctionalRole.View_account_reports, FunctionalRole.View_client_messages, FunctionalRole.Submit_trade_to_executed);
        permissions = new PermissionsDto();
        PermissionServiceUtil.setFunctionalPermissions(permissions, functionalRoles, UserExperience.DIRECT, JobRole.INVESTOR);
        permissions.setPermission("account.payment.transaction.view", true);
        permissions.setPermission("account.deposit.transaction.view", true);
    }

    @Test
    public void test_permissionForDirectInvestor() throws Exception {
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);

        service.updateInvestorPermissions(permissions, accountDetails, serviceErrors);
        MatcherAssert.assertThat(permissions, notNullValue());
        MatcherAssert.assertThat(permissions.hasPermission("account.direct.view"), equalTo(true));
        MatcherAssert.assertThat(permissions.hasPermission("account.trade.entry"), equalTo(true));
        MatcherAssert.assertThat(permissions.hasPermission("account.trade.create"), equalTo(true));
        MatcherAssert.assertThat(permissions.hasPermission("account.trade.submit"), equalTo(true));
        MatcherAssert.assertThat(permissions.hasPermission("account.order.view"), equalTo(true));
    }

    @Test
    public void testUpdateInvestorPermissions_forDirectActive() throws Exception {
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        when(accountProductsHelper.getSubscriptionType(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(DirectOffer.ACTIVE.getSubscriptionType());

        WrapAccountDetailImpl accountDetails = new WrapAccountDetailImpl();
        service.updateInvestorPermissions(permissions, accountDetails, serviceErrors);

        assertThat(permissions.hasPermission("account.order.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.trade.entry"), equalTo(true));
        assertThat(permissions.hasPermission("account.report.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.activity.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.payment.transaction.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.deposit.transaction.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.transaction.scheduled.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.accountsbillers.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.fee.schedule.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.investment.orders.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.investment.buy.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.investment.sell.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.investment.find.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.linked.menu.view"), equalTo(false));
        assertThat(permissions.hasPermission("account.accountsbillers.menu.view"), equalTo(true));
    }

    @Test
    public void testUpdateInvestorPermissions_forDirectSimple() throws Exception {
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        when(accountProductsHelper.getSubscriptionType(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(DirectOffer.SIMPLE.getSubscriptionType());
        WrapAccountDetailImpl accountDetails = new WrapAccountDetailImpl();

        service.updateInvestorPermissions(permissions, accountDetails, serviceErrors);
        assertThat(permissions.hasPermission("account.order.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.trade.entry"), equalTo(true));
        assertThat(permissions.hasPermission("account.report.view"), equalTo(true));
        assertThat(permissions.hasPermission("intermediary.messages.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.activity.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.transaction.scheduled.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.fee.schedule.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.investment.orders.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.investment.buy.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.investment.find.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.invest.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.withdraw.view"), equalTo(true));

        assertThat(permissions.hasPermission("account.payment.transaction.view"), equalTo(false));
        assertThat(permissions.hasPermission("account.accountsbillers.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.linked.menu.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.accountsbillers.menu.view"), equalTo(false));
        assertThat(permissions.hasPermission("account.investment.sell.view"), equalTo(false));
        assertThat(permissions.hasPermission("account.deposit.transaction.view"), equalTo(false));
    }

    @Test
    public void testUpdateInvestorPermissions_forDirectUndecided() throws Exception {
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        when(accountProductsHelper.getSubscriptionType(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(DirectOffer.UNDECIDED.getSubscriptionType());
        WrapAccountDetailImpl accountDetails = new WrapAccountDetailImpl();

        service.updateInvestorPermissions(permissions, accountDetails, serviceErrors);
        assertThat(permissions.hasPermission("account.order.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.trade.entry"), equalTo(true));
        assertThat(permissions.hasPermission("account.report.view"), equalTo(true));
        assertThat(permissions.hasPermission("intermediary.messages.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.report.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.investment.orders.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.investment.buy.view"), equalTo(true));

        assertThat(permissions.hasPermission("account.activity.view"), equalTo(false));
        assertThat(permissions.hasPermission("account.payment.transaction.view"), equalTo(false));
        assertThat(permissions.hasPermission("account.deposit.transaction.view"), equalTo(false));
        assertThat(permissions.hasPermission("account.accountsbillers.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.linked.menu.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.accountsbillers.menu.view"), equalTo(false));
        assertThat(permissions.hasPermission("account.fee.schedule.view"), equalTo(false));
        assertThat(permissions.hasPermission("account.investment.sell.view"), equalTo(false));
    }

    @Test
    public void testUpdateInvestorPermissions_forAsimNonSMSF() throws Exception {
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), TransactionPermission.Payments_Deposits);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ASIM);

        service.updateInvestorPermissions(permissions, accountDetails, serviceErrors);
        assertThat(permissions.hasPermission("account.asim.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.investment.find.view"), equalTo(false));
    }

    @Test
    public void testUpdateInvestorPermissions_forAsimSMSF() throws Exception {
        accountDetails = getNonBlockedAccount("client1", BrokerKey.valueOf("broker1"), SMSF, TransactionPermission.Payments_Deposits);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ASIM);

        service.updateInvestorPermissions(permissions, accountDetails, serviceErrors);
        assertThat(permissions.hasPermission("account.asim.view"), equalTo(true));
        assertThat(permissions.hasPermission("account.investment.find.view"), equalTo(true));
    }

    @Test
    public void testShouldDisplayActivity_whenMigratedOnCurrentFinYear_thenReturnFalse() {
        DateTime migrationDate2017 = new DateTime(2017, 11, 1, 12, 0);
        DateTime executionDateBeginningFinYear = new DateTime(2017, 7, 1, 0, 0);
        DateTime executionDateMidFinYear = new DateTime(2017, 11, 15, 12, 0);
        DateTime executionDateEndFinYear = new DateTime(2018, 6, 30, 23, 59);

        WrapAccountDetail accountDetail = createMigratedWrapAccount(migrationDate2017);

        boolean shouldDisplayBeginningFinYear = service.shouldDisplayActivity(accountDetail, executionDateBeginningFinYear);
        boolean shouldDisplayMidFinYear = service.shouldDisplayActivity(accountDetail, executionDateMidFinYear);
        boolean shouldDisplayEndFinYear = service.shouldDisplayActivity(accountDetail, executionDateEndFinYear);

        assertThat(shouldDisplayBeginningFinYear, is(false));
        assertThat(shouldDisplayMidFinYear, is(false));
        assertThat(shouldDisplayEndFinYear, is(false));
    }

    @Test
    public void testShouldDisplayActivity_whenMigratedBeforeCurrentFinYear_thenReturnTrue() {
        DateTime migrationDate2017 = new DateTime(2017, 11, 1, 12, 0);
        DateTime executionDateBeginningNextFinYear = new DateTime(2018, 7, 1, 0, 0);
        DateTime executionDateMidNextFinYear = new DateTime(2018, 11, 15, 12, 0);
        DateTime executionDateEndNextFinYear = new DateTime(2019, 6, 30, 23, 59);
        DateTime executionDateFutureFinYear = new DateTime(2020, 1, 1, 0, 0);

        WrapAccountDetail accountDetail = createMigratedWrapAccount(migrationDate2017);

        boolean shouldDisplayBeginningFinYear = service.shouldDisplayActivity(accountDetail, executionDateBeginningNextFinYear);
        boolean shouldDisplayMidFinYear = service.shouldDisplayActivity(accountDetail, executionDateMidNextFinYear);
        boolean shouldDisplayEndFinYear = service.shouldDisplayActivity(accountDetail, executionDateEndNextFinYear);
        boolean shouldDisplayFutureFinYear = service.shouldDisplayActivity(accountDetail, executionDateFutureFinYear);

        assertThat(shouldDisplayBeginningFinYear, is(true));
        assertThat(shouldDisplayMidFinYear, is(true));
        assertThat(shouldDisplayEndFinYear, is(true));
        assertThat(shouldDisplayFutureFinYear, is(true));
    }

    /**
     * This scenario should never happen since an account migration date should not be in the future
     */
    @Test
    public void testShouldDisplayActivity_whenMigratedAfterCurrentFinYear_thenReturnTrue() {
        DateTime migrationDate2018 = new DateTime(2018, 1, 1, 0, 0);
        DateTime executionDateBefore2018 = new DateTime(2017, 1, 1, 0, 0);


        WrapAccountDetail accountDetail = createMigratedWrapAccount(migrationDate2018);

        boolean shouldDisplayActivity = service.shouldDisplayActivity(accountDetail, executionDateBefore2018);

        assertThat(shouldDisplayActivity, is(true));
    }

    @Test
    public void testShouldDisplayActivity_whenAccountNotMigrated_thenReturnTrue() {
        WrapAccountDetail accountDetail = new WrapAccountDetailImpl();
        boolean shouldDisplayActivity = service.shouldDisplayActivity(accountDetail, DateTime.now());

        assertThat(shouldDisplayActivity, is(true));
    }

    private WrapAccountDetail createMigratedWrapAccount(DateTime migratedDate) {
        WrapAccountDetailImpl accountDetail = new WrapAccountDetailImpl();
        accountDetail.setMigrationKey("migrationKey");
        accountDetail.setMigrationDate(migratedDate);
        return accountDetail;
    }
}
