package com.bt.nextgen.core.security.api.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.statements.permission.DocumentAccountPermission;
import com.bt.nextgen.api.subscriptions.model.SubscriptionDto;
import com.bt.nextgen.api.subscriptions.service.SubscriptionDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.security.AvaloqFunctionalRolePermissionEvaluator;
import com.bt.nextgen.core.security.api.model.FunctionalRoleGroupEnum;
import com.bt.nextgen.core.security.api.model.JobAuthorizationGroupEnum;
import com.bt.nextgen.core.security.api.model.PermissionAccountKey;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.BlockCode;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.JobAuthorizationRole;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.options.model.OptionValue;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.Account;
import com.btfin.panorama.service.integration.account.PersonRelation;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * This service retrieves the permissions (account blocks) pertaining to a given account id
 */
@Component("acctPermissionService")
@Transactional(value = "springJpaTransactionManager")
class PermissionAccountDtoServiceImpl implements PermissionAccountDtoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionAccountDtoServiceImpl.class);

    private static final String SMSF_VIEW_EXTERNAL_HOLDINGS_KEY = "account.portfolio.externalassets.view";
    private static final String SMSF_UPDATE_EXTERNAL_HOLDINGS_KEY = "account.portfolio.externalassets.update";
    private static final String SMSF_VIEW_BGL_KEY = "account.bgl.view";
    private static final String SMSF_VIEW_KEY = "account.smsf.view";
    private static final String SMSF_VIEW_CASH_CATEGORISATION_KEY = "account.portfolio.cashcategorisation.view";
    private static final String SMSF_ACCOUNT_ACCOUNTING_SOFTWARE_CONNECTION_TOGGLE = "account.accounting.software.connection.toggle";
    private static final String ASIM_ACCOUNT_PERMISSION = "account.asim.view";
    private static final String AUSTRALIA = "AU";

    @Resource(name = "userDetailsService")
    public AvaloqBankingAuthorityService userProfileService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private OptionsService optionService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private UserInformationIntegrationService userInformationService;

    @Autowired
    private SubscriptionDtoService subscriptionDtoService;

    @Autowired
    @Qualifier("rolePermissionEvaluator")
    private AvaloqFunctionalRolePermissionEvaluator rolePermissionEvaluator;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    private SuperPermissionDtoService superPermissionDtoService;

    @Autowired
    private InvestorPermissionHelper investorPermissionHelper;

    @Override
    public PermissionsDto find(PermissionAccountKey key, ServiceErrors serviceErrors) {
        LOGGER.info("Retrieving account permissions for accountId: " + EncodedString.toPlainText(key.getAccountId()));
        final AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        final WrapAccountDetail account = accountService.loadWrapAccountDetail(accountKey, serviceErrors);
        // TODO: Need to remove this once investigation is complete on Prod issue
        LOGGER.info("Id of WrapAccountDetail object is: " + account != null ? account.getAccountKey().getId() : "");
        final UserProfile userInfo = profileService.getActiveProfile();
        return accountPermissions(account, userInfo, serviceErrors);
    }

    /**
     * Construct the set of account-specific override permissions depending on the nature of the account provided.
     * <p>
     * Functional role permissions are assumed to apply and overriden with account specific rules.
     *
     * @param account  the account to be examined.
     * @param userInfo the user profile information.
     * @return a set of overriding permissions specific to the account.
     */
    private PermissionsDto accountPermissions(WrapAccountDetail account, UserProfile userInfo, ServiceErrors serviceErrors) {
        final PermissionsDto permissionsDto = new PermissionsDto(false);
        setAccountOverrides(permissionsDto, account, userInfo, serviceErrors);
        LOGGER.info("Retrieved account permissions");
        return permissionsDto;
    }

    /**
     * Construct override permission flags dependent upon the transaction authorities and block codes attached to the provided
     * account.
     *
     * @param account the account with authorities attached.
     */
    private void setAccountOverrides(PermissionsDto permissionsDto, Account account, UserProfile userInfo,
                                     ServiceErrors serviceErrors) {
        Assert.notNull(account);
        if (account instanceof WrapAccountDetail) {
            WrapAccountDetail wrapAccount = (WrapAccountDetail) account;
            final Map<JobAuthorizationGroupEnum, JobAuthorizationRole> rolePermissions = getRolePermissions(userInfo, account,
                    serviceErrors);
            final Set<TransactionPermission> permissionSet = getPermissionSet(wrapAccount, userInfo);

            setFunctionalRoleOverrides(permissionsDto, wrapAccount, rolePermissions, userInfo, serviceErrors);
            convertRoleToPermission(rolePermissions, permissionSet);
            setTransactionPermissionOverrides(permissionsDto, permissionSet);
            setAccountStatusAndBlockCodeOverrides(permissionsDto, wrapAccount, userInfo.getJobRole());

            // updateInvestorPermissions() sets ASIM permissions. It should be done before smsf/external holdings permissions
            if (JobRole.INVESTOR.equals(userInfo.getJobRole())) {
                investorPermissionHelper.updateInvestorPermissions(permissionsDto, wrapAccount, serviceErrors);
            }

            setExternalHoldingsPermission(permissionsDto, wrapAccount);
            setSmsfAccountingSoftwarePermission(permissionsDto, wrapAccount, userInfo);
            setPortfolioPreferencePermission(wrapAccount, permissionsDto, serviceErrors);
            setPortfolioIncomePreferencePermission(wrapAccount, permissionsDto, serviceErrors);
            final DocumentAccountPermission documentAccountPermission = new DocumentAccountPermission(permissionsDto,
                    wrapAccount);
            documentAccountPermission.applyPermissions();
            setBGLAccountPermissions(permissionsDto, wrapAccount);
            setAccountOptions(permissionsDto, account.getAccountKey(), serviceErrors);
            setMenuPermissions(permissionsDto, wrapAccount);
            superPermissionDtoService.setSuperPermissions(permissionsDto, wrapAccount, userInfo.getJobRole(), serviceErrors);
            permissionsDto.setPermission("account.drp.update", hasAustralianDomicileOwners(wrapAccount));
        } else {
            LOGGER.warn("Unable to set account overrides for class {}", account.getClass().getName());
        }
    }

    private void setMenuPermissions(PermissionsDto permissionsDto, WrapAccountDetail account) {
        setInvestmentOrdersPermission(permissionsDto);
        setRegularInvestmentPlanPermission(permissionsDto);
        AssetTransferPermissionUtil.retrieveAssetTransferPermission(permissionsDto, account);
        setInvestmentPreferencePermission(permissionsDto);
        setMoveMoneyPermissions(permissionsDto);
    }

    Map<JobAuthorizationGroupEnum, JobAuthorizationRole> getRolePermissions(UserProfile userInfo, Account account, ServiceErrors serviceErrors) {
        if (!UserExperience.ASIM.equals(userInfo.getUserExperience()) &&
                (JobRole.PARAPLANNER.equals(userInfo.getJobRole()) || JobRole.ASSISTANT.equals(userInfo.getJobRole()))) {
            return getRoleOverride(userInfo, account, serviceErrors);
        }
        // Only support staff can have their role overridden
        return null;
    }

    /**
     * Gets map of all inherited permissions for paraplanner/assistant. Permissions can come from linked advisers, dealer group or
     * practice.
     *
     * @param userInfo      userInfo
     * @param account       account
     * @param serviceErrors serviceErrors
     * @return map with the user's inherited permissions
     */
    private Map<JobAuthorizationGroupEnum, JobAuthorizationRole> getRoleOverride(UserProfile userInfo, Account account,
                                                                                 ServiceErrors serviceErrors) {
        final Map<JobAuthorizationGroupEnum, JobAuthorizationRole> roleMap = new HashMap<>();
        final List<Broker> brokerList = brokerService.getBrokersForJob(userInfo, serviceErrors);

        if (CollectionUtils.isEmpty(brokerList)) {
            // If no broker found (broker cache hasn't been refreshed) override default permissions with read only.
            roleMap.put(JobAuthorizationGroupEnum.READ_ONLY, JobAuthorizationRole.Support_ReadOnly);
            LOGGER.info("No broker user found, defaulting to read only permission.");
            return roleMap;
        }

        final BrokerUser brokerUser = brokerService.getBrokerUser(userInfo, serviceErrors);
        final Map<BrokerKey, BrokerRole> brokerRoles = Lambda.index(brokerUser.getRoles(), Lambda.on(BrokerRole.class).getKey());
        for (final Broker broker : brokerList) {
            // If this broker is an adviser, only apply the permission if this account belongs to the adviser
            // If this broker is DG or practice, apply the permission across all accounts
            final BrokerKey brokerKey = broker.getBrokerType().equals(BrokerType.ADVISER) ? ((WrapAccountDetail) account)
                    .getAdviserKey() : broker.getKey();
            roleMap.put(JobAuthorizationGroupEnum.getGroup(brokerRoles.get(brokerKey).getAuthorizationRole()),
                    brokerRoles.get(brokerKey).getAuthorizationRole());
        }
        return roleMap;
    }

    /**
     * Get permissions applicable to this particular account.
     *
     * @param account  account
     * @param userInfo userInfo
     * @return set of permissions to be joined with the UR permission
     */
    Set<TransactionPermission> getPermissionSet(WrapAccountDetail account, UserProfile userInfo) {
        final Set<TransactionPermission> permissionSet = new HashSet<>();
        ClientKey clientKey = null;
        if (userInfo.getJobRole().equals(JobRole.INVESTOR)) {
            clientKey = userInfo.getClientKey();
        } else if (userInfo.getJobRole().equals(JobRole.ADVISER) || userInfo.getJobRole().equals(JobRole.PARAPLANNER)
                || userInfo.getJobRole().equals(JobRole.ASSISTANT)) {
            clientKey = account.getAdviserPersonId();
            permissionSet.addAll(account.getAdviserPermissions());
        }
        if (null != clientKey) {
            final PersonRelation userDetails = account.getAssociatedPersons().get(clientKey);
            if (userDetails != null && userDetails.getPermissions() != null) {
                permissionSet.addAll(userDetails.getPermissions());
            }
        }

        if (permissionSet.contains(TransactionPermission.Company_Registration)) {
            permissionSet.remove(TransactionPermission.Company_Registration);
        }
        return permissionSet;
    }

    /**
     * Sets the permission tree with functional roles from a UR starting from the most restrictive permission to the least.
     * Inherited permissions apply to paraplanners and assistants and come from either adviser, dealer group or practice.
     *
     * @param permissionsDto  permission tree
     * @param rolePermissions map of inherited permissions
     * @param userInfo        userInfo
     * @param serviceErrors   serviceErrors
     */
    void setFunctionalRoleOverrides(PermissionsDto permissionsDto, WrapAccountDetail account,
                                    Map<JobAuthorizationGroupEnum, JobAuthorizationRole> rolePermissions, UserProfile userInfo,
                                    ServiceErrors serviceErrors) {
        if (rolePermissions == null) {
            PermissionServiceUtil.setFunctionalPermissions(permissionsDto, userInfo.getFunctionalRoles(),
                    brokerHelperService.getUserExperience(account, serviceErrors), userInfo.getJobRole());
        } else {
            for (final JobAuthorizationGroupEnum auth : JobAuthorizationGroupEnum.values()) {
                if (rolePermissions.containsKey(auth)) {
                    final FunctionalRoleGroupEnum role = PermissionServiceUtil.getFunctionalRoleGroup(userInfo.getJobRole(),
                            rolePermissions.get(auth));
                    final List<FunctionalRole> functionalRoles = userInformationService.getFunctionalRoleList(
                            Arrays.asList(role.toString()), serviceErrors);
                    PermissionServiceUtil.setFunctionalPermissions(permissionsDto, functionalRoles,
                            brokerHelperService.getUserExperience(account, serviceErrors), userInfo.getJobRole());
                    LOGGER.info("User's role permissions for this account are changing from default to {}.", role.name());
                    break;
                }
            }
        }
    }

    /**
     * Converts the user's functional role to the corresponding transaction permission to allow for permission overrides.
     *
     * @param permissionSet
     * @param rolePermissions
     */
    private void convertRoleToPermission(Map<JobAuthorizationGroupEnum, JobAuthorizationRole> rolePermissions,
                                         Set<TransactionPermission> permissionSet) {
        if (rolePermissions != null) {
            if (rolePermissions.containsKey(JobAuthorizationGroupEnum.READ_ONLY)
                    || rolePermissions.containsKey(JobAuthorizationGroupEnum.WITHOUT_CASH)) {
                permissionSet.add(TransactionPermission.No_Transaction);
            } else if (rolePermissions.containsKey(JobAuthorizationGroupEnum.WITH_CASH)) {
                permissionSet.add(TransactionPermission.Payments_Deposits);
            }
        }
    }

    /**
     * Overrides the permission tree starting from the most restrictive permission to the least.
     *
     * @param permissionsDto permission tree
     * @param permissionSet
     */
    private void setTransactionPermissionOverrides(PermissionsDto permissionsDto, Set<TransactionPermission> permissionSet) {
        // Account maintenance permission is applied in conjunction with the above
        if (permissionSet.contains(TransactionPermission.Account_Maintenance)) {
            setAccountMaintenancePermissions(permissionsDto, true);
        } else {
            setAccountMaintenancePermissions(permissionsDto, false);
        }
        for (final TransactionPermission permission : TransactionPermission.values()) {
            if (permissionSet.contains(permission)) {
                switch (permission) {
                    case No_Transaction:
                        setNoTransactionPermissions(permissionsDto);
                        break;
                    case Payments_Deposits_To_Linked_Accounts:
                        setLinkedAccountPermissions(permissionsDto);
                        break;
                    case Payments_Deposits:
                        setPayAnyonePermissions(permissionsDto);
                        break;
                    default:
                        break;
                }
                break;
            }
        }
    }

    private void setAccountStatusAndBlockCodeOverrides(PermissionsDto permissionsDto, WrapAccountDetail account, JobRole role) {
        if (account.getAccountStatus().equals(AccountStatus.CLOSE)) {
            final DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
            final String[] params = new String[]{formatter.print(account.getClosureDate())};
            final String closedMessage = JobRole.INVESTOR.equals(role) ? cmsService.getDynamicContent("Ins-OP-0034", params)
                    : cmsService.getDynamicContent("Ins-OP-0035", params);
            permissionsDto.setPermissionMessage("account.summary.bar.view", closedMessage);
            permissionsDto.setPermissionMessage("account.fee.block.view", cmsService.getContent("Err.IP-0350"));
            permissionsDto.setPermissionMessage("account.report.block.view", cmsService.getContent("Err.IP-0350"));
            permissionsDto.setPermission("account.closed", true);
            setPaymentDepositPermission(permissionsDto, "NO_TRANSACTION");
        } else if (account.getBlockedReason() != null) {
            for (final List<BlockCode> blocks : account.getBlockedReason().values()) {
                for (final BlockCode block : blocks) {
                    setAccountBlockOverridePermissions(permissionsDto, block, role);
                }
            }
        } else {
            permissionsDto.setPermission("account.summary.bar.view", true);
            permissionsDto.setPermission("account.fee.block.view", true);
            permissionsDto.setPermission("account.report.block.view", true);
        }
    }

    private void setAccountBlockOverridePermissions(PermissionsDto permissionsDto, BlockCode block, JobRole role) {
        boolean isBlocked = false;
        switch (block) {
            case Blocked_for_All:
                isBlocked = true;
                permissionsDto.setPermissionMessage("account.all.block.view", cmsService.getContent("Err.IP-0358"));
                setAccountMaintenancePermissions(permissionsDto, false);
                break;
            case Blocked_for_All_except_Interest_Payment_and_Tax:
                permissionsDto.setPermissionMessage("account.all.block.view", cmsService.getContent("Err.IP-0358"));
                setAccountMaintenancePermissions(permissionsDto, false);
                isBlocked = true;
                break;
            case Blocked_for_Trades_Buy_and_Sell_and_Outgoing_Payment:
            case Blocked_for_Trades_Buy_and_Outgoing_Payment:
            case Blocked_for_Outgoing_Payment:
                isBlocked = true;
                permissionsDto.setPermissionMessage("account.payment.block.view", cmsService.getContent("Err.IP-0358"));
                break;
            case Blocked_for_Trades_Buy_and_Sell:
            case Blocked_for_All_Trade_Sanctions:
                isBlocked = true;
                break;
            case Blocked_for_Incoming_Payment:
                isBlocked = true;
                permissionsDto.setPermissionMessage("account.deposit.block.view", cmsService.getContent("Err.IP-0358"));
                break;
            default:
        }

        if (isBlocked) {
            String blockedMessage = JobRole.INVESTOR.equals(role) ? cmsService.getContent("Ins-OP-0033") : cmsService.getContent("Err.IP-0276");
            permissionsDto.setPermissionMessage("account.summary.bar.view", blockedMessage);
            blockedMessage = JobRole.INVESTOR.equals(role) ? cmsService.getContent("Err.IP-0371") : cmsService.getContent("Err.IP-0349");
            permissionsDto.setPermissionMessage("account.fee.block.view", blockedMessage);
            permissionsDto.setPermissionMessage("account.report.block.view", blockedMessage);
        }
    }

    /**
     * Checks whether the portfolio/bp has a specific transact authority.
     *
     * @param portfolioId portfolio to test permission against
     * @param transaction transaction authority to check
     * @return
     */
    public boolean hasTransactionPermission(String portfolioId, String transaction) {
        boolean hasPermission = false;

        final PermissionAccountKey permissionAccountKey = new PermissionAccountKey(portfolioId);
        final PermissionsDto permissions = this.find(permissionAccountKey, new ServiceErrorsImpl());
        hasPermission = permissions.hasPermission(transaction);

        return hasPermission;
    }

    /**
     * Authorises a user to perform a specific transaction on an account. 1. Performs a check based on users functional role
     * permissions - can a user perform a specific action 2. Performs a check based on users account authorities (domain security)
     * - can a user perform a specific transaction on an account
     *
     * @param portfolioId    account to check against
     * @param functionalRole functional role that is required
     * @param authority      account authority that is required
     * @return
     */
    public boolean canTransact(String portfolioId, String functionalRole, String authority) {
        boolean hasAuthority = false;
        boolean hasFunctionalPermission;

        if (StringUtils.isBlank(portfolioId) || StringUtils.isBlank(functionalRole) || StringUtils.isBlank(authority)) {
            LOGGER.warn("Attempt to check permission, but with one or more missing parameters- portfolioId: "
                    + "{}, functionaRole: {}, authority: {}", portfolioId, functionalRole, authority);
            return false;
        }
        hasFunctionalPermission = rolePermissionEvaluator.hasPermission(null, null, functionalRole);
        if (hasFunctionalPermission) {
            hasAuthority = this.hasTransactionPermission(portfolioId, authority);
        }
        return hasAuthority && hasFunctionalPermission;
    }

    /**
     * Check user authority to perform a specific transaction on an account. The user is the currently logged in user. Performs a
     * check based on users account authorities (mapped as {@link com.bt.nextgen.service.avaloq.userinformation.FunctionalRole}) -
     * can a user perform a specific transaction on an account
     *
     * @param portfolioId account to check the authority on
     * @param authority   authority that is required (see {@link com.bt.nextgen.service.avaloq.userinformation.FunctionalRole} )
     * @return
     */
    @Override
    public boolean canTransact(String portfolioId, String authority) {
        boolean hasAuthority;

        if (StringUtils.isBlank(portfolioId) || StringUtils.isBlank(authority)) {
            LOGGER.warn("Attempt to check permission, but with one or more missing parameters- "
                    + "portfolioId: {}, authority: {}", portfolioId, authority);
            return false;
        }
        hasAuthority = this.hasTransactionPermission(portfolioId, authority);
        return hasAuthority;
    }

    /*
     * Add in permission keys which need to be overriden below
     */
    private void setPayAnyonePermissions(PermissionsDto permissionsDto) {
        permissionsDto.setPermission("account.payment.transaction.view", true);
        permissionsDto.setPermission("account.deposit.transaction.view", true);
        setPaymentDepositPermission(permissionsDto, "ANY_TRANSACTION");
    }

    private void setLinkedAccountPermissions(PermissionsDto permissionsDto) {
        permissionsDto.setPermission("account.payment.transaction.view", true);
        permissionsDto.setPermission("account.deposit.transaction.view", true);
        setPaymentDepositPermission(permissionsDto, "LINKED_TRANSACTION");
    }

    private void setNoTransactionPermissions(PermissionsDto permissionsDto) {
        permissionsDto.setPermission("account.payment.transaction.view", false);
        permissionsDto.setPermission("account.deposit.transaction.view", false);
        setPaymentDepositPermission(permissionsDto, "NO_TRANSACTION");
    }

    private void setAccountMaintenancePermissions(PermissionsDto permissionsDto, boolean permission) {
        setPaymentDepositPermission(permissionsDto, "NO_TRANSACTION");
        permissionsDto.setPermission("account.payee.view", permission);
    }

    private void setMoveMoneyPermissions(PermissionsDto permissionDto) {
        if (permissionDto.hasPermission("account.payment.transaction.view")
                || permissionDto.hasPermission("account.deposit.transaction.view")
                || permissionDto.hasPermission("account.accountsbillers.view")) {
            permissionDto.setPermission("account.movemoney.menu.view", true);
        } else {
            permissionDto.setPermission("account.movemoney.menu.view", false);
        }
    }

    private void setInvestmentOrdersPermission(PermissionsDto permissionsDto) {
        if (permissionsDto.hasPermission("account.trade.create") || permissionsDto.hasPermission("account.order.view")) {
            permissionsDto.setPermission("account.investmentorders.menu.view", true);
        } else {
            permissionsDto.setPermission("account.investmentorders.menu.view", false);
        }
    }

    private void setRegularInvestmentPlanPermission(PermissionsDto permissionsDto) {
        if (permissionsDto.hasPermission("option.regular.investment.supported")
                && permissionsDto.hasPermission("account.regular.investment.submit")) {
            permissionsDto.setPermission("account.regular.investment.menu.view", true);
        } else {
            permissionsDto.setPermission("account.regular.investment.menu.view", false);
        }
    }

    private void setInvestmentPreferencePermission(PermissionsDto permissionsDto) {
        if (permissionsDto.hasPermission("client.intermediary.report.view")
                || permissionsDto.hasPermission("account.details.update")) {
            permissionsDto.setPermission("account.investmentpreferences.view", true);
        } else {
            permissionsDto.setPermission("account.investmentpreferences.view", false);
        }
    }

    /**
     * Return account BGL view permission on this account. True if:<br>
     * <ul>
     * <li>the account structure is 'SMSF'</li>
     * </ul>
     *
     * @param permissionsDto current permission tree
     * @param account        account to check local permissions on
     */
    private void setBGLAccountPermissions(PermissionsDto permissionsDto, WrapAccountDetail account) {
        // If account is of type 'SMSF' 'account.bgl.view' is set to true. Then permission is enabled.
        // TODO: TechDebt we should have only one key for SMSF account view which will be used at multiple places Remove it after
        if (account.getAccountStructureType().equals(AccountStructureType.SMSF)) {
            permissionsDto.setPermission(SMSF_VIEW_BGL_KEY, true);
            permissionsDto.setPermission(SMSF_VIEW_KEY, true);
        } else {
            permissionsDto.setPermission(SMSF_VIEW_BGL_KEY, false);
            permissionsDto.setPermission(SMSF_VIEW_KEY, false);
        }
    }

    /**
     * @param permissionsDto current permission tree
     * @param account        account to check local permissions on
     */
    private void setExternalHoldingsPermission(PermissionsDto permissionsDto, WrapAccountDetail account) {
        // If account is not an 'SMSF' account then view and edit permission is explicitly disabled.
        if (!account.getAccountStructureType().equals(AccountStructureType.SMSF)) {
            permissionsDto.setPermission(SMSF_VIEW_CASH_CATEGORISATION_KEY, false);
        }
        // Enable External Assets for all account types except super
        if (account.getAccountStructureType().equals(AccountStructureType.SUPER)) {
            permissionsDto.setPermission(SMSF_VIEW_EXTERNAL_HOLDINGS_KEY, false);
            permissionsDto.setPermission(SMSF_UPDATE_EXTERNAL_HOLDINGS_KEY, false);
        }

        // Disable edit permission if the account is not open
        /*
         * if (!account.isOpen() || account.getBlockedReason() != null) { for (String uiRole :
         * FunctionalRole.Manage_External_Assets.getUiRoles()) { permissionsDto.setPermission(uiRole, false); } }
         */
        disablePermissionForClosedAndBlockedAccount(permissionsDto, account, FunctionalRole.Manage_External_Assets);
    }

    private void setSmsfAccountingSoftwarePermission(PermissionsDto permissionsDto, WrapAccountDetail account,
                                                     UserProfile userInfo) {
        // fundadmin and advised investor cannot connect/disconnect to/from accounting software
        if (!AccountStructureType.SMSF.equals(account.getAccountStructureType()) || subscribesToFundAdmin(account)
                || (JobRole.INVESTOR.equals(userInfo.getJobRole()) && !permissionsDto.hasPermission(ASIM_ACCOUNT_PERMISSION))) {
            permissionsDto.setPermission(SMSF_ACCOUNT_ACCOUNTING_SOFTWARE_CONNECTION_TOGGLE, false);
        }

        // Disable accounting software toggle if the account is not open
        disablePermissionForClosedAndBlockedAccount(permissionsDto, account, FunctionalRole.Connect_Disconnect_Accounting_Software_Feed);
    }

    private boolean subscribesToFundAdmin(WrapAccountDetail account) {
        final List<SubscriptionDto> subscriptionList = getFundAdminSubscriptionList(EncodedString.fromPlainText(account.getAccountKey().getId()).toString());
        if (isNotEmpty(subscriptionList)) {
            for (final SubscriptionDto dto : subscriptionList) {
                if ("FA".equalsIgnoreCase(dto.getServiceType()) &&
                        (ApplicationStatus.DONE.getStatus().equalsIgnoreCase(dto.getStatus()) ||
                                ApplicationStatus.DONE_GENERATING_DOC.getStatus().equalsIgnoreCase(dto.getStatus()))) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<SubscriptionDto> getFundAdminSubscriptionList(String accountId) {
        // Check fund admin subscription
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        final com.bt.nextgen.api.account.v2.model.AccountKey accKey = new com.bt.nextgen.api.account.v2.model.AccountKey(accountId);
        return subscriptionDtoService.search(accKey, serviceErrors);
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

    private void setPaymentDepositPermission(PermissionsDto permissionsDto, String authType) {
        switch (authType) {
            case "NO_TRANSACTION":
                permissionsDto.setPermission("account.payment.anyone.create", false);
                permissionsDto.setPermission("account.payment.bpay.create", false);
                permissionsDto.setPermission("account.payment.linked.create", false);
                permissionsDto.setPermission("account.deposit.linked.create", false);
                break;
            case "LINKED_TRANSACTION":
                permissionsDto.setPermission("account.payment.linked.create", true);
                permissionsDto.setPermission("account.deposit.linked.create", true);
                permissionsDto.setPermission("account.payment.anyone.create", false);
                permissionsDto.setPermission("account.payment.bpay.create", false);
                break;
            case "ANY_TRANSACTION":
                permissionsDto.setPermission("account.payment.anyone.create", true);
                permissionsDto.setPermission("account.payment.linked.create", true);
                permissionsDto.setPermission("account.deposit.linked.create", true);
                permissionsDto.setPermission("account.payment.bpay.create", true);
                break;
        }
    }

    private void setAccountOptions(PermissionsDto permissionsDto, AccountKey accountKey, ServiceErrors serviceErrors) {
        final Collection<OptionValue<Boolean>> accountOptions = optionService.getFeatures(accountKey, serviceErrors);
        for (final OptionValue<Boolean> accountOption : accountOptions) {
            if (accountOption.getValue()) {
                // only set true ones to minimise bandwidth
                permissionsDto.setPermission("option." + accountOption.getOptionValueKey().getOptionKey().getOptionName(), true);
            }
        }
    }

    private void setPortfolioPreferencePermission(WrapAccountDetail account, PermissionsDto permissionsDto, ServiceErrors serviceErrors) {
        // By default, these permissions are assumed to be ACTIVE.
        permissionsDto.setPermission("modelportfolio.preference.edit", true);
        permissionsDto.setPermission("modelportfolio.tailored.preference.edit", true);

        final Broker broker = brokerHelperService.getDealerGroupForInvestor(account, serviceErrors);
        if (broker != null) {
            permissionsDto.setPermission("modelportfolio.preference.edit", BooleanUtils.isTrue(broker.isMPPreferenceActive()));
            permissionsDto.setPermission("modelportfolio.tailored.preference.edit", BooleanUtils.isTrue(broker.isTMPPreferenceActive()));
        }
    }

    private void setPortfolioIncomePreferencePermission(WrapAccountDetail account, PermissionsDto permissionsDto,
                                                        ServiceErrors serviceErrors) {
        // By default, these permissions are assumed to be ACTIVE.
        permissionsDto.setPermission("modelportfolio.incomepreference.view", true);
        permissionsDto.setPermission("modelportfolio.tailored.incomepreference.view", true);

        final Broker broker = brokerHelperService.getDealerGroupForInvestor(account, serviceErrors);
        if (broker != null) {
            permissionsDto.setPermission("modelportfolio.incomepreference.view",
                    BooleanUtils.isTrue(broker.isRmpIncomePreferenceEnabled()));
            permissionsDto.setPermission("modelportfolio.tailored.incomepreference.view",
                    BooleanUtils.isTrue(broker.isTmpIncomePreferenceEnabled()));
        }
    }

    private boolean hasAustralianDomicileOwners(WrapAccountDetail account) {
        // Get addresses of all the owners
        final List<Address> addresses = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(account.getOwners())) {
            for (Client owner : account.getOwners()) {
                addresses.addAll(owner.getAddresses());
            }
            final List<Address> nonAustralianAddresses = Lambda.select(addresses, Matchers.allOf(
                    Lambda.having(Lambda.on(Address.class).isDomicile(), Matchers.equalTo(true)),
                    Lambda.having(Lambda.on(Address.class).getCountryCode(), Matchers.not(Matchers.equalToIgnoringCase(AUSTRALIA)))
            ));

            return CollectionUtils.isEmpty(nonAustralianAddresses);
        }
        return false;
    }
}