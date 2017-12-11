package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.core.security.api.model.ProductToggleEnum;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRoleType;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.PensionAccountDetail;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Service to update Super permissions for an account
 */
@Service
class SuperPermissionDtoServiceImpl implements SuperPermissionDtoService {

    @Autowired
    private AsimPermission asimPermission;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    private static final String SUPER_ACCOUNT_TYPE = "account.type.super";
    private static final String SUPER_ACCOUNT_DETAILS_VIEW = "account.super.details.view";
    private static final String SUPER_ACCOUNT_TAXPRESERVATION_VIEW = "account.super.taxpreservation.view";
    private static final String SUPER_ACCOUNT_CONTRIBUTION_VIEW = "account.super.contribution.view";
    private static final String SUPER_ACCOUNT_BENEFICIARIES_VIEW = "account.super.beneficiaries.view";
    private static final String SUPER_ACCOUNT_BENEFICIARIES_UPDATE = "account.super.beneficiaries.update";
    private static final String SUPER_GLOBAL_INSURANCE_VIEW = "account.super.insurance.view";
    private static final String SUPER_GLOBAL_INSURANCE_MENU_VIEW = "account.super.insurance.menu.view";
    private static final String SUPER_ACCOUNT_PENSION_VIEW = "account.super.pension.view";
    private static final String SUPER_ACCOUNT_PENSION_CREATE = "account.super.pension.create";
    private static final String SUPER_ACCOUNT_ACCUMULATION_ONEOFF_WITHDRAWAL_CREATE = "account.super.withdrawal.create";
    private static final String SUPER_ACCOUNT_ACCUMULATION_ONEOFF_WITHDRAWAL_VIEW = "account.super.withdrawal.view";
    private static final String SUPER_ACCOUNT_ACCUMULATION_ACCESSING_MENU_VIEW = "account.super.accessing.menu.view";

    private static final String SUPER_ACCOUNT_PENSION_COMMENCED_VIEW = "account.super.pension.commenced.view";
    private static final String SUPER_ACCOUNT_OVERVIEW_CONTRIBUTION_SUMMARY_VIEW = "account.super.contribution.overview";

    private static final String SUPER_COMMENCE_PENSION_VIEW = "account.super.pension.commencement.view";
    private static final String SUPER_COMMENCE_PENSION_UPDATE = "account.super.pension.commencement.update";

    private static final String SUPER_LINKED_ACCOUNTS_PENSION_VIEW = "account.super.pension.linkedbankaccounts.view";
    private static final String SUPER_LINKED_ACCOUNTS_ACCUMULATION_VIEW = "account.super.accum.linkedbankaccounts.view";

    private static final String SUPER_ACCOUNT_CONTRIBUTIONS_MENU_VIEW = "account.super.contributions.menu.view";
    private static final String SUPER_ACCOUNT_ROLLOVERS_MENU_VIEW = "account.super.rollovers.menu.view";
    private static final String SUPER_ACCOUNT_ROLLOVERS_INVESTOR_MENU_VIEW = "account.super.rollovers.investor.menu.view";

    private static final String SUPER_PERSONAL_TAX_DEDUCTION_VIEW = "account.super.personaltaxdeductionnotice.view";
    private static final String SUPER_PERSONAL_TAX_DEDUCTION_UPDATE = "account.super.personaltaxdeductionnotice.update";

    private static final String ACCOUNT_REPORT_VIEW = "account.report.view";
    private static final String PAYMENT_VIEW = "account.payment.transaction.view";
    private static final String SCHEDULED_TRANSACTIONS_VIEW = "account.transaction.scheduled.view";
    private static final String LINKED_ACCOUNT_MENU_VIEW = "account.linked.menu.view";

    private static final String SUPER_GLOBAL_INSURANCE_DOCUMENT_VIEW = "insurance.documentlibrary.view";

    private static final String SUPER_ACCUMULATION_OPTIMISE_VIEW = "account.super.optimise.view";
    private static final String SUPER_PENSION_MAXIMISE_VIEW = "account.super.pension.maximise.view";
    private static final String SUPER_ACCUMULATION_CONTRIBUTIONS_INFO_MENU_VIEW = "account.super.contributions.info.menu.view";
    private static final String SUPER_PENSION_TRANSFER_MONEY_MENU_VIEW = "account.super.pension.transfermoney.menu.view";
    private static final String SUPER_ACCUMULATION_ACCESS_MENU_VIEW = "account.super.access.menu.view";
    private static final String SUPER_PENSION_PAYMENT_MENU_VIEW = "account.super.pension.payment.menu.view";
    private static final String SUPER_PENSION_COMMENCE_MENU_VIEW = "account.super.pension.commencement.menu.view";

    /**
     * Updates the Super account permissions
     *
     * @param permissionsDto - object of {@link PermissionsDto}
     * @param account        - object of {@link WrapAccountDetail}
     * @param jobRole        - Role of the user {@link JobRole}
     * @param serviceErrors  - object of {@link ServiceErrors}
     */
    @Override
    public void setSuperPermissions(PermissionsDto permissionsDto, WrapAccountDetail account, JobRole jobRole, ServiceErrors serviceErrors) {
        final boolean isDirectAccount = UserExperience.DIRECT.equals(brokerHelperService.getUserExperience(account, serviceErrors));

        if (AccountStructureType.SUPER.equals(account.getAccountStructureType())) {
            permissionsDto.setPermission(SUPER_ACCOUNT_TYPE, true);
            setSuperAccountPermissions(permissionsDto, account, jobRole, isDirectAccount);
        }

        setSuperInsurancePermissions(permissionsDto, account, jobRole, isDirectAccount, serviceErrors);
        setSuperInsuranceDocumentPermission(permissionsDto);
    }

    /**
     * Super account permission set based on super account type and view account reports
     * functional role.
     *
     * @param permissionsDto  - Permission Dto
     * @param account         - Account
     * @param jobRole         - Role of the user {@link JobRole}
     * @param isDirectAccount - Flag to determine if the account is DIRECT
     */
    private void setSuperAccountPermissions(PermissionsDto permissionsDto, WrapAccountDetail account, JobRole jobRole, boolean isDirectAccount) {
        final boolean viewAccountReportsRole = permissionContainsRole(permissionsDto, ACCOUNT_REPORT_VIEW);
        disablePermissionForClosedAndBlockedAccount(permissionsDto, account, FunctionalRole.Update_super_beneficiaries);

        // position before permissionContainsRoleWithNonAsimInvestorOverride is important
        final boolean superCreatePayment = permissionContainsRole(permissionsDto, "account.payment.linked.create");
        final boolean hasPaymentPermission = permissionContainsRole(permissionsDto, PAYMENT_VIEW);

        // setup basic super permissions
        permissionsDto.setPermission(SUPER_ACCOUNT_DETAILS_VIEW, viewAccountReportsRole);
        permissionsDto.setPermission(SUPER_ACCOUNT_TAXPRESERVATION_VIEW, viewAccountReportsRole);
        permissionsDto.setPermission(SUPER_ACCOUNT_CONTRIBUTION_VIEW, viewAccountReportsRole);

        updateAccumulationPermissions(permissionsDto, account, viewAccountReportsRole, superCreatePayment);
        updatePensionPermissions(permissionsDto, account, superCreatePayment, viewAccountReportsRole);
        updateBeneficiariesPermissions(permissionsDto, viewAccountReportsRole);
        updatePaymentPermissions(permissionsDto, account);
        // overrides for direct super investors
        if (JobRole.INVESTOR.equals(jobRole) && isDirectAccount) {
            updateDirectSuperPermissions(permissionsDto, account, hasPaymentPermission);
        }
    }

    // setup super accumulation permissions
    private void updateAccumulationPermissions(PermissionsDto permissionsDto, WrapAccountDetail account, boolean viewAccountReportsRole, boolean superCreatePayment) {
        if (AccountSubType.ACCUMULATION.equals(account.getSuperAccountSubType())) {
            final boolean superCreateContribution = permissionContainsRole(permissionsDto, "account.deposit.linked.create");
            final boolean taxDeductionNoticeRole = permissionContainsRoleWithNonAsimInvestorOverride(permissionsDto, "account.super.notifypersonaldeduction");

            permissionsDto.setPermission(SUPER_ACCOUNT_CONTRIBUTIONS_MENU_VIEW, viewAccountReportsRole && superCreateContribution);
            permissionsDto.setPermission(SUPER_LINKED_ACCOUNTS_ACCUMULATION_VIEW, viewAccountReportsRole);
            permissionsDto.setPermission(SUPER_ACCOUNT_OVERVIEW_CONTRIBUTION_SUMMARY_VIEW, viewAccountReportsRole);
            permissionsDto.setPermission(SUPER_ACCOUNT_ROLLOVERS_MENU_VIEW, permissionsDto.hasPermission(PAYMENT_VIEW) && viewAccountReportsRole);

            permissionsDto.setPermission(SUPER_PERSONAL_TAX_DEDUCTION_VIEW, viewAccountReportsRole);
            permissionsDto.setPermission(SUPER_PERSONAL_TAX_DEDUCTION_UPDATE, taxDeductionNoticeRole);

            permissionsDto.setPermission(SUPER_ACCOUNT_ACCUMULATION_ACCESSING_MENU_VIEW, viewAccountReportsRole);
            permissionsDto.setPermission(SUPER_ACCOUNT_ACCUMULATION_ONEOFF_WITHDRAWAL_VIEW, viewAccountReportsRole);
            permissionsDto.setPermission(SUPER_ACCOUNT_ACCUMULATION_ONEOFF_WITHDRAWAL_CREATE, superCreatePayment && viewAccountReportsRole);
        }
    }

    // pension related permissions
    private void updatePensionPermissions(PermissionsDto permissionsDto, WrapAccountDetail account, boolean superCreatePayment, boolean viewAccountReportsRole) {
        if (AccountSubType.PENSION.equals(account.getSuperAccountSubType())) {
            final boolean commencePensionRole = permissionContainsRoleWithNonAsimInvestorOverride(permissionsDto, "account.super.pension.commence");
            final boolean taxDeductionNoticeRole = permissionContainsRoleWithNonAsimInvestorOverride(permissionsDto, "account.super.notifypersonaldeduction");
            final boolean hasPensionCommencedOrPending = pensionHasCommencedOrPending(account);

            permissionsDto.setPermission(SUPER_LINKED_ACCOUNTS_PENSION_VIEW, viewAccountReportsRole);

            permissionsDto.setPermission(SUPER_ACCOUNT_PENSION_VIEW, viewAccountReportsRole);
            permissionsDto.setPermission(SUPER_ACCOUNT_PENSION_CREATE, superCreatePayment && viewAccountReportsRole);

            permissionsDto.setPermission(SUPER_PERSONAL_TAX_DEDUCTION_VIEW, viewAccountReportsRole);
            permissionsDto.setPermission(SUPER_PERSONAL_TAX_DEDUCTION_UPDATE, !hasPensionCommencedOrPending && taxDeductionNoticeRole);

            permissionsDto.setPermission(SUPER_COMMENCE_PENSION_VIEW, viewAccountReportsRole && !hasPensionCommencedOrPending);
            permissionsDto.setPermission(SUPER_COMMENCE_PENSION_UPDATE, commencePensionRole && !hasPensionCommencedOrPending);
            permissionsDto.setPermission(SUPER_ACCOUNT_PENSION_COMMENCED_VIEW, viewAccountReportsRole && hasPensionCommencedOrPending);
        }
    }

    // beneficiaries related permissions
    private void updateBeneficiariesPermissions(PermissionsDto permissionsDto, boolean viewAccountReportsRole) {
        final boolean updateBeneficiariesRole = permissionContainsRoleWithNonAsimInvestorOverride(permissionsDto, "account.super.beneficiaries.manage");

        permissionsDto.setPermission(SUPER_ACCOUNT_BENEFICIARIES_VIEW, viewAccountReportsRole);
        permissionsDto.setPermission(SUPER_ACCOUNT_BENEFICIARIES_UPDATE, updateBeneficiariesRole);
    }

    // Payment related permissions
    private void updatePaymentPermissions(PermissionsDto permissionsDto, WrapAccountDetail account) {
        // Remove Payments(for accumulation) and Deposits menu item permissions
        permissionsDto.setPermission(PAYMENT_VIEW, permissionsDto.hasPermission(PAYMENT_VIEW) && !AccountSubType.ACCUMULATION.equals(account.getSuperAccountSubType()));
        permissionsDto.prunePermission("account.deposit.transaction.view");
        permissionsDto.prunePermission("account.movemoney.menu.view");
        // Replace accounts and billers with linked accounts
        permissionsDto.setPermission(LINKED_ACCOUNT_MENU_VIEW, true);
        permissionsDto.prunePermission("account.accountsbillers.menu.view");
    }

    // Direct super related overrides
    private void updateDirectSuperPermissions(PermissionsDto permissionsDto, WrapAccountDetail account, boolean hasPaymentPermission) {
        final boolean superAccumulationAccountSubType = AccountSubType.ACCUMULATION.equals(account.getSuperAccountSubType());
        final boolean superPensionAccountSubType = AccountSubType.PENSION.equals(account.getSuperAccountSubType());

        if (superAccumulationAccountSubType) {
            permissionsDto.setPermission(LINKED_ACCOUNT_MENU_VIEW, false);
            permissionsDto.setPermission(SUPER_ACCUMULATION_OPTIMISE_VIEW, true);
            permissionsDto.setPermission(SUPER_ACCUMULATION_CONTRIBUTIONS_INFO_MENU_VIEW, true);
            permissionsDto.setPermission(SUPER_ACCUMULATION_ACCESS_MENU_VIEW, true);
            permissionsDto.setPermission(SUPER_ACCOUNT_ROLLOVERS_INVESTOR_MENU_VIEW, hasPaymentPermission);
        }
        if (superPensionAccountSubType) {
            final boolean commencePensionRole = permissionContainsRoleWithNonAsimInvestorOverride(permissionsDto, "account.super.pension.commence");
            boolean hasPensionCommenced = hasPensionCommenced(account);

            permissionsDto.setPermission(LINKED_ACCOUNT_MENU_VIEW, true);
            permissionsDto.setPermission(SUPER_PENSION_PAYMENT_MENU_VIEW, permissionsDto.hasPermission(SUPER_ACCOUNT_PENSION_CREATE));
            permissionsDto.setPermission(SUPER_PENSION_MAXIMISE_VIEW, true);
            permissionsDto.setPermission(SUPER_PENSION_TRANSFER_MONEY_MENU_VIEW, !hasPensionCommenced);
            permissionsDto.setPermission(SUPER_PENSION_COMMENCE_MENU_VIEW, commencePensionRole && !hasPensionCommenced);
            permissionsDto.setPermission(SUPER_ACCOUNT_ROLLOVERS_INVESTOR_MENU_VIEW, hasPaymentPermission && !hasPensionCommenced);
        }

        permissionsDto.setPermission(PAYMENT_VIEW, false);
        permissionsDto.setPermission(SCHEDULED_TRANSACTIONS_VIEW, false);
        permissionsDto.setPermission(SUPER_ACCOUNT_CONTRIBUTIONS_MENU_VIEW, false);
        permissionsDto.setPermission(SUPER_ACCOUNT_TAXPRESERVATION_VIEW, false);
    }

    // Check if the pension has commenced
    private boolean pensionHasCommencedOrPending(WrapAccountDetail account) {
        if (account instanceof PensionAccountDetail) {
            final PensionAccountDetail pensionAccount = (PensionAccountDetail) account;
            final DateTime commencementDate = pensionAccount.getCommenceDate();
            return pensionAccount.isCommencementPending() || (commencementDate != null && commencementDate.isBeforeNow());
        }

        return false;
    }

    private boolean hasPensionCommenced(WrapAccountDetail account) {
        if (account instanceof PensionAccountDetail) {
            final DateTime commencementDate = ((PensionAccountDetail) account).getCommenceDate();
            return commencementDate != null && commencementDate.isBeforeNow();
        }
        return false;
    }

    private boolean permissionContainsRole(PermissionsDto permissionsDto, String roleName) {
        return permissionsDto.hasPermission(roleName);
    }

    private boolean permissionContainsRoleWithNonAsimInvestorOverride(PermissionsDto permissionsDto, String roleName) {
        return asimPermission.overrideValue(permissionContainsRole(permissionsDto, roleName), false, false, JobRoleType.INVESTOR);
    }

    private void disablePermissionForClosedAndBlockedAccount(PermissionsDto permissionsDto, WrapAccountDetail account,
                                                             FunctionalRole... functionalRoles) {
        if (!account.isOpen() || account.getBlockedReason() != null) {
            for (final FunctionalRole role : functionalRoles) {
                for (final String uiRole : role.getUiRoles()) {
                    permissionsDto.setPermission(uiRole, false);
                }
            }
        }
    }

    /**
     * This method set the account level insurance permissions
     *
     * @param permissionsDto    - object of {@link PermissionsDto}
     * @param wrapAccountDetail - object of {@link WrapAccountDetail}
     * @param jobRole           - Job role of the user {@link JobRole}
     * @param isDirectAccount   - Flag to determine if the account is DIRECT
     * @param serviceErrors     - object of{@link ServiceErrors}
     */
    private void setSuperInsurancePermissions(PermissionsDto permissionsDto, WrapAccountDetail wrapAccountDetail,
                                              JobRole jobRole, boolean isDirectAccount, ServiceErrors serviceErrors) {
        // If the product type is INSURANCE, set the FR Key to true (provided insurance feature toggle is enabled)
        // Only if the base level super insurance permission is true, validate
        if (permissionsDto.hasPermission(SUPER_GLOBAL_INSURANCE_VIEW)) {
            // Set the default to false - this would be incase of an accountant wherein the account is not having
            // access to the dealergroup which is designated to sell insurance
            permissionsDto.setPermission(SUPER_GLOBAL_INSURANCE_VIEW, false);

            // Set insurance permission if account is non-direct or direct super
            if (!isDirectAccount || AccountStructureType.SUPER.equals(wrapAccountDetail.getAccountStructureType())) {
                // Get the Dealergroup (broker) for the current account
                final Broker broker = brokerHelperService.getDealerGroupForInvestor(wrapAccountDetail, serviceErrors);
                if (broker != null && BrokerType.DEALER.equals(broker.getBrokerType())) {
                    setInsurancePermission(permissionsDto, broker, serviceErrors);
                }

                // Set separate permission for the menu item, available to non-investors & non-pension investors only
                final boolean isPensionInvestor = JobRole.INVESTOR.equals(jobRole) && AccountSubType.PENSION.equals(wrapAccountDetail.getSuperAccountSubType());
                permissionsDto.setPermission(SUPER_GLOBAL_INSURANCE_MENU_VIEW, permissionsDto.hasPermission(SUPER_GLOBAL_INSURANCE_VIEW) && !isPensionInvestor);
            }
        }
    }

    /**
     * This method set the insurance permissions, for the dealer-group
     *
     * @param permissionsDto - object of {@link PermissionsDto}
     * @param broker         - Dealer group for the account, See:{@link Broker}
     * @param serviceErrors  - object of{@link ServiceErrors}
     */
    private void setInsurancePermission(PermissionsDto permissionsDto, Broker broker, ServiceErrors serviceErrors) {
        final List<Product> dealerGroupProducts = productIntegrationService.getDealerGroupProductList(broker.getDealerKey(), serviceErrors);
        if (isNotEmpty(dealerGroupProducts)) {
            for (final Product product : dealerGroupProducts) {
                if (ProductToggleEnum.validateProductShortName(ProductToggleEnum.INSURANCE, product.getShortName())) {
                    permissionsDto.setPermission(SUPER_GLOBAL_INSURANCE_VIEW, permissionsDto.hasPermission(ACCOUNT_REPORT_VIEW));
                    break;
                }
            }
        }
    }

    /**
     * Sets up the Insurance document library permissions
     *
     * @param permissionsDto - object of {@link PermissionsDto}
     */
    private void setSuperInsuranceDocumentPermission(PermissionsDto permissionsDto) {
        final boolean insuranceDocumentsFeature = Properties.getSafeBoolean("feature.insurance.documentlibrary");
        permissionsDto.setPermission(SUPER_GLOBAL_INSURANCE_DOCUMENT_VIEW, insuranceDocumentsFeature);
    }
}
