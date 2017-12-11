package com.bt.nextgen.core.security.api.service;


import com.bt.nextgen.api.account.v3.model.DirectOffer;
import com.bt.nextgen.api.account.v3.util.AccountProductsHelper;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.bt.nextgen.service.integration.account.AccountStructureType.SMSF;

/**
 * Helper service to setup/update the investor permissions for an account
 */
@Component
class InvestorPermissionHelperImpl implements InvestorPermissionHelper {

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    private AccountProductsHelper accountProductsHelper;

    private static final String ASIM_ACCOUNT_VIEW = "account.asim.view";

    // Direct permissions
    private static final String DIRECT_ACCOUNT_VIEW = "account.direct.view";

    private static final String VIEW_INVESTMENT_ORDERS = "account.investment.orders.view";
    private static final String FIND_AN_INVESTMENT = "account.investment.find.view";
    private static final String BUY_AN_INVESTMENT = "account.investment.buy.view";
    private static final String SELL_AN_INVESTMENT = "account.investment.sell.view";

    private static final String VIEW_LINKED_ACCOUNTS = "account.linked.menu.view";
    private static final String VIEW_PAYMENT = "account.payment.transaction.view";
    private static final String VIEW_DEPOSIT = "account.deposit.transaction.view";
    private static final String VIEW_WITHDRAW = "account.withdraw.view";
    private static final String VIEW_INVEST_MORE = "account.invest.view";

    private static final String VIEW_ACCOUNTS_AND_BILLERS = "account.accountsbillers.menu.view";
    private static final String VIEW_FEE_SCHEDULE = "account.fee.schedule.view";
    private static final String VIEW_SCHEDULED_TRANSACTIONS = "account.transaction.scheduled.view";
    private static final String VIEW_ACTIVITIES = "account.activity.view";
    private static final String VIEW_TD_CALCULATOR = "account.termdeposit.calculator.view";

    /**
     * Updates the investor permissions
     *
     * @param permissionsDto - object of {@link PermissionsDto}
     * @param account        - object of {@link WrapAccountDetail}
     * @param serviceErrors  - object of{@link ServiceErrors}
     */
    @Override
    public void updateInvestorPermissions(PermissionsDto permissionsDto, WrapAccountDetail account, ServiceErrors serviceErrors) {
        permissionsDto.setPermission(VIEW_ACTIVITIES, shouldDisplayActivity(account, DateTime.now()));

        final UserExperience userExperience = brokerHelperService.getUserExperience(account, serviceErrors);

        // Add the ASIM user permission
        if (UserExperience.ASIM.equals(userExperience)) {
            permissionsDto.setPermission(ASIM_ACCOUNT_VIEW, true);
            permissionsDto.setPermission(VIEW_TD_CALCULATOR, true);
            permissionsDto.setPermission(FIND_AN_INVESTMENT, SMSF.equals(account.getAccountStructureType()));
        }
        if (UserExperience.DIRECT.equals(userExperience)) {
            setDirectBasePermissions(permissionsDto);
            // filter direct permissions, currently all Direct Super are treated as ACTIVE
            filterDirectPermissions(permissionsDto, accountProductsHelper.getSubscriptionType(account, serviceErrors));
        }
    }

    protected boolean shouldDisplayActivity(WrapAccountDetail account, DateTime currentTime) {
        if (StringUtils.isNotBlank(account.getMigrationKey())) {
            DateTime migrationDate = account.getMigrationDate();
            DateTime currentFinancialYearStartDate = ApiFormatter.parseDate(DateUtil.getFinYearStartDate(currentTime.toDate()));
            DateTime currentFinancialYearEndDate = ApiFormatter.parseDate(DateUtil.getFinYearEndDate(currentTime.toDate()));

            // Migrated accounts can't see the activity menu during the financial year on which they were migrated.
            return !(migrationDate.isAfter(currentFinancialYearStartDate) && migrationDate.isBefore(currentFinancialYearEndDate));
        }

        return true;
    }

    /**
     * Sets up the basic direct user permissions
     *
     * @param permissionsDto - object of {@link PermissionsDto}
     */
    private void setDirectBasePermissions(PermissionsDto permissionsDto) {
        // for investor hamburger menu
        permissionsDto.setPermission(DIRECT_ACCOUNT_VIEW, true);
        permissionsDto.setPermission(FIND_AN_INVESTMENT, true);
        permissionsDto.setPermission(VIEW_INVESTMENT_ORDERS, true);
        permissionsDto.setPermission(BUY_AN_INVESTMENT, true);
        permissionsDto.setPermission(SELL_AN_INVESTMENT, true);
    }

    /**
     * Filters the permissions for direct investors
     *
     * @param permissionsDto   - object of {@link PermissionsDto}
     * @param subscriptionType - Direct subscription type (active/simple/undecided)
     */
    // TODO: Remove this hack after permissions model update from Avaloq
    private static void filterDirectPermissions(PermissionsDto permissionsDto, String subscriptionType) {
        if (DirectOffer.SIMPLE.getSubscriptionType().equals(subscriptionType)) {
            // Remove permissions: payment, account & billers
            permissionsDto.prunePermission(VIEW_PAYMENT);
            // Replace accounts and billers with linked accounts
            permissionsDto.prunePermission(VIEW_ACCOUNTS_AND_BILLERS);
            permissionsDto.setPermission(VIEW_LINKED_ACCOUNTS, true);
            // Remove Deposit and add Invest
            permissionsDto.prunePermission(VIEW_DEPOSIT);
            permissionsDto.setPermission(VIEW_INVEST_MORE, true);
            // Remove Sell and add Withdraw
            permissionsDto.prunePermission(SELL_AN_INVESTMENT);
            permissionsDto.setPermission(VIEW_WITHDRAW, true);
        } else if (DirectOffer.UNDECIDED.getSubscriptionType().equals(subscriptionType)) {
            // Remove permissions: payment, deposit, account & billers, fee, scheduled transaction, sell, activity
            permissionsDto.prunePermission(VIEW_PAYMENT);
            permissionsDto.prunePermission(VIEW_DEPOSIT);
            permissionsDto.prunePermission(VIEW_ACCOUNTS_AND_BILLERS);
            permissionsDto.prunePermission(VIEW_FEE_SCHEDULE);
            permissionsDto.prunePermission(VIEW_SCHEDULED_TRANSACTIONS);
            permissionsDto.prunePermission(SELL_AN_INVESTMENT);
            permissionsDto.prunePermission(VIEW_ACTIVITIES);
            permissionsDto.setPermission(VIEW_LINKED_ACCOUNTS, true);
        }
    }
}
