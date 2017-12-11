package com.bt.nextgen.core.security.api.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.collection.LambdaCollections;
import com.bt.nextgen.api.profile.v1.service.ProfileUtil;
import com.bt.nextgen.core.security.UserRole;
import com.bt.nextgen.core.security.api.model.FunctionalRoleGroupEnum;
import com.bt.nextgen.core.security.api.model.JobAuthorizationGroupEnum;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.core.security.api.model.ProductToggleEnum;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.JobAuthorizationRole;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.btfin.panorama.core.security.integration.customer.UserGroup;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static com.bt.nextgen.service.avaloq.product.ProductLevel.WHITE_LABEL;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * This service retrieves the active profile's base permissions, and tacks on global feature toggles as well.
 */
@Service("permissionBaseService")
@Transactional(value = "springJpaTransactionManager")
@SuppressWarnings("squid:S1200")
public class PermissionBaseDtoServiceImpl implements PermissionBaseDtoService {
    private static final String GLOBAL_FEATURE_PREFIX = "feature.global.";

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionBaseDtoServiceImpl.class);

    private static final Collection<String> WPAC_BRANDED_INVESTOR_BROKER_KEYS = asList("DG.PBP", "DG.WPBWS", "STGWHS");

    private static final String LC_LINK_VIEW = "LC.view";

    private static final Logger log = LoggerFactory.getLogger(PermissionBaseDtoServiceImpl.class);

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private UserInformationIntegrationService userInformationService;

    @Autowired
    private FeatureTogglesService featureTogglesService;

    @Autowired
    private ProductIntegrationService productService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    private ProfileUtil profileUtil;

    @Override
    public PermissionsDto findOne(ServiceErrors serviceErrors) {
        if (userProfileService.isExistingAvaloqUser()) {
            final UserProfile userInfo = userProfileService.switchActiveProfile(profileUtil.getProfileId());
            LOGGER.info("Retrieving base permissions for user profile: {}", userInfo.getProfileId());
            return profilePermissions(userInfo, userProfileService.isEmulating(), serviceErrors);
        } else {
            LOGGER.info("User does not exist in ABS, returning minimal permissions.");
            PermissionsDto permissions = new PermissionsDto(false);
            setFeatureToggles(permissions, serviceErrors);
            return permissions;
        }
    }

    @Override
    public boolean hasBasicPermission(String permission) {
        return findOne(new ServiceErrorsImpl()).hasPermission(permission);
    }

    @Override
    public boolean hasProductPermission(String permission) {
        final UserProfile userInfo = userProfileService.getActiveProfile();
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Broker broker = brokerHelperService.getDealerGroupForIntermediary((JobProfile) userInfo, serviceErrors);
        return permission != null && "marketinformation.view".equals(permission) ? checkMarketViewPermission(userInfo,
                serviceErrors, broker) : false;
    }

    /**
     * Construct the global set of permissions for the current user's profile.
     * 
     * @param user
     *            user information containing primary Avaloq user role and functional roles.
     * @param emulating
     * @param serviceErrors
     * 
     * @return the JSON-friendly tree of permissions for the current profile.
     */
    private PermissionsDto profilePermissions(UserProfile user, boolean emulating, ServiceErrors serviceErrors) {
        final PermissionsDto permissionsDto = new PermissionsDto(false);
        setUserPermissions(permissionsDto, user, emulating, serviceErrors);
        setFeatureToggles(permissionsDto, serviceErrors);
        LOGGER.info("Retrieved base permissions");
        return permissionsDto;
    }

    /**
     * Construct fine-grained permissions given the provided user information.
     * 
     * @param permissionsDto
     *            holds the permission keys for the user
     * @param info
     *            the user's active profile information
     * @param emulating
     *            boolean to indicate if the user is currently in emulation mode
     * @param serviceErrors
     */
    private void setUserPermissions(PermissionsDto permissionsDto, UserProfile info, boolean emulating,
            ServiceErrors serviceErrors) {
        FunctionalRoleGroupEnum role = null;
        // Perform FR Override to differentiate Basic and Read Only roles
        if (info.getUserExperience() != UserExperience.ASIM
                && (info.getJobRole() == JobRole.PARAPLANNER || info.getJobRole() == JobRole.ASSISTANT
                        || info.getJobRole() == JobRole.INVESTMENT_MANAGER || info.getJobRole() == JobRole.DEALER_GROUP_MANAGER
                        || info.getJobRole() == JobRole.PRACTICE_MANAGER || info.getJobRole() == JobRole.PORTFOLIO_MANAGER)) {
            role = getFunctionalRoleOverride(info, serviceErrors);
        }

        List<FunctionalRole> frList = role != null ? userInformationService.getFunctionalRoleList(Arrays.asList(role.toString()),
                serviceErrors) : info.getFunctionalRoles();

        PermissionServiceUtil.setFunctionalPermissions(permissionsDto, frList, null, info.getJobRole());
        // setLCPermission() should be called before setNonFunctionalRolePermissions()
        setLCPermission(permissionsDto, info);
        setNonFunctionalRolePermissions(permissionsDto, info, emulating, serviceErrors);
        setUARPermission(permissionsDto);

        // This has to sit outside of setNonFunctionalRolePermissions() to reduce cyclomatic complexity
        setCorporateActionApprovalPermissions(permissionsDto, info, serviceErrors);
    }

    /**
     * Sets the permission tree with functional roles from a UR starting from the most restrictive permission to the least.
     * Inherited permissions apply to paraplanners and assistants and come from either adviser, dealer group or practice.
     * 
     * @param info
     *            userInfo
     * @param serviceErrors
     *            serviceErrors
     */
    private FunctionalRoleGroupEnum getFunctionalRoleOverride(UserProfile info, ServiceErrors serviceErrors) {
        FunctionalRoleGroupEnum role = null;
        // For support staff, if at least one attached OE is RO, base permissions will be RO
        BrokerUser brokerDetails = brokerService.getBrokerUser(info, serviceErrors);
        if (brokerDetails != null) {
            for (BrokerRole brokerRole : brokerDetails.getRoles()) {
                if (JobAuthorizationGroupEnum.getGroup(brokerRole.getAuthorizationRole()) == JobAuthorizationGroupEnum.READ_ONLY) {
                    role = PermissionServiceUtil.getFunctionalRoleGroup(info.getJobRole(), brokerRole.getAuthorizationRole());
                    LOGGER.info("User's role permissions are changing from default to {}.", role.name());
                    break;
                }
            }
        } else {
            // If no broker found (broker cache hasn't been refreshed) override default permissions with read only.
            role = PermissionServiceUtil.getFunctionalRoleGroup(info.getJobRole(), JobAuthorizationRole.Support_ReadOnly);
            LOGGER.info("Broker not found, defaulting permissions to read only: {}.", role.name());
        }
        return role;
    }

    /*
     * This is used to map permission keys where there is no corresponding Avaloq functional role.
     */
    private void setNonFunctionalRolePermissions(PermissionsDto permissionsDto, UserProfile info, boolean emulating,
            ServiceErrors serviceErrors) {

        final List<Product> dealerGroupProducts = getDealerGroupProducts(info, serviceErrors);
        setInvestmentOrdersPermission(permissionsDto);
        setInvestmentPreferencesPermission(permissionsDto);
        permissionsDto.setPermission("emulating", emulating);
        Broker dealerGroupBroker = null;
        if (Arrays.asList(JobRole.ADVISER, JobRole.PARAPLANNER, JobRole.ASSISTANT, JobRole.DEALER_GROUP_MANAGER).contains(
                info.getJobRole())) {
            dealerGroupBroker = brokerHelperService.getDealerGroupForIntermediary((JobProfile) info, serviceErrors);
        }
        boolean marketDataPermission = checkMarketViewPermission(info, serviceErrors, dealerGroupBroker);
        permissionsDto.setPermission("marketinformation.view", marketDataPermission);
        setMarketInformationRealtimePricePermission(permissionsDto, info.getJobRole());
        setTrackingPermission(permissionsDto);
        setCorporateActionsPermission(permissionsDto);
        setInsurancePermissions(permissionsDto, info.getJobRole(), dealerGroupProducts);
        setMenuHeaderPermission(permissionsDto);
        setProductPagePermissions(permissionsDto, dealerGroupProducts);
        setModelPermission(permissionsDto, serviceErrors);
        setModelConstructionTypePermission(permissionsDto, serviceErrors);

        JobRole role = info.getJobRole();
        switch (role) {
            case ADVISER:
                updateAdviserNonFunctionalRoles(permissionsDto, marketDataPermission, dealerGroupProducts);
                break;
            case INVESTOR:
                setInvestorNonFunctionalPermissions(permissionsDto);
                break;
            case ACCOUNTANT:
                setAccountantNonFunctionalPermissions(permissionsDto);
                break;
            case ACCOUNTANT_SUPPORT_STAFF:
                setAccountantSupportStaffNonFunctionalPermissions(permissionsDto);
                break;
            case PARAPLANNER:
                setProductPermissions(permissionsDto, dealerGroupProducts);
                setIntermediaryPermissions(permissionsDto);
                permissionsDto.setPermission("transitions.adviser.view", true);
                setClientListPermission(permissionsDto, dealerGroupBroker);
                break;
            case ASSISTANT:
                setProductPermissions(permissionsDto, dealerGroupProducts);
                setIntermediaryPermissions(permissionsDto);
                permissionsDto.setPermission("transitions.adviser.view", true);
                setClientListPermission(permissionsDto, dealerGroupBroker);
                break;
            case DEALER_GROUP_MANAGER:
                setDealerGroupPermissions(permissionsDto);
                permissionsDto.setPermission("transitions.adviser.view", true);
                break;
            case PRACTICE_MANAGER:
                setIntermediaryPermissions(permissionsDto);
                permissionsDto.setPermission("transitions.adviser.view", true);
                break;
            case PORTFOLIO_MANAGER:
                setPortfolioManagerPermissions(permissionsDto);
                break;
            case INVESTMENT_MANAGER:
                setInvestmentManagerPermissions(permissionsDto);
                break;
            default:
                // TODO - To check for other roles
                permissionsDto.setPermission("user.detail.intermediary.view", true);
                permissionsDto.setPermission("recent.accounts.view", false);
                permissionsDto.setPermission("products.termdeposit.csv.view", true);
                break;
        }
    }

    private void setModelPermission(PermissionsDto permissionsDto, ServiceErrors serviceErrors) {
        permissionsDto.setPermission("modelportfolio.tailored.super.edit", true);
        Broker broker = userProfileService.getInvestmentManager(serviceErrors);
        if (broker != null) {
            permissionsDto.setPermission("modelportfolio.tailored.super.edit",
                    BooleanUtils.isTrue(broker.isTmpSuperProductEnabled()));
        }
    }

    private void setModelConstructionTypePermission(PermissionsDto permissionsDto, ServiceErrors serviceErrors) {
        boolean fixedModelConstructionEnabled = true;
        boolean floatingModelConstructionEnabled = true;

        Broker broker = userProfileService.getInvestmentManager(serviceErrors);
        if (broker != null) {
            fixedModelConstructionEnabled = BooleanUtils.isTrue(broker.isTmpFixedConstructionEnabled());
            floatingModelConstructionEnabled = BooleanUtils.isTrue(broker.isTmpFloatingConstructionEnabled());
        }

        permissionsDto.setPermission("modelportfolio.tailored.fixed.edit", fixedModelConstructionEnabled);
        permissionsDto.setPermission("modelportfolio.tailored.floating.edit", floatingModelConstructionEnabled);
    }

    private void setClientListPermission(PermissionsDto permissionsDto, Broker dealerGroupBroker) {
        permissionsDto.setPermission("clientlist.supportstaff.view",
                null != dealerGroupBroker && "WPAC".equalsIgnoreCase(dealerGroupBroker.getParentEBIKey().getId()));
    }

    /**
     * Sets permission for items specific to the product pages
     * 
     * @param permissionsDto
     *            object of {@link PermissionsDto}
     * @param dealerGroupProducts
     *            - Dealer group products
     */
    private void setProductPagePermissions(PermissionsDto permissionsDto, List<Product> dealerGroupProducts) {
        final boolean hasProductViewPermission = permissionsDto.hasPermission("products.information.view");
        final boolean hasNonSuperWLProduct = isNotEmpty(filter(
                allOf(having(on(Product.class).isSuper(), is(false)),
                        having(on(Product.class).getProductLevel(), is(WHITE_LABEL))), dealerGroupProducts));

        // Permissions for BTCash and SMSF screens on product pages
        permissionsDto.setPermission("products.btcash.view", hasProductViewPermission && hasNonSuperWLProduct);
        permissionsDto.setPermission("products.smsf.view", hasProductViewPermission && hasNonSuperWLProduct);
    }

    /**
     * This method sets permission for LifeCentral & LifeCentral+ links.
     * 
     * @param permissionsDto
     *            object of {@link PermissionsDto}
     * @param userProfile
     *            object of {@link UserProfile}
     */
    private void setLCPermission(PermissionsDto permissionsDto, UserProfile userProfile) {
        // Set the LC.view to false by default
        permissionsDto.setPermission(LC_LINK_VIEW, false);

        if (featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle(FeatureToggles.LC_VIEW)) {
            // Get AdviserPPID from SAML Token; if not available and the user is emulating, retrieve it from UserProfileService
            final SamlToken samlToken = userProfileService.getSamlToken();
            if (StringUtils.isNotEmpty(samlToken.getPpId())) {
                LOGGER.info("The AdviserPPID from SAML token is: {}.", samlToken.getPpId());
                permissionsDto.setPermission(LC_LINK_VIEW, true);
            } else if (userProfileService.isEmulating()) {
                final JobRole jobRole = userProfile.getJobRole();
                if (JobRole.ADVISER.equals(jobRole)) {
                    final String ppId = userProfileService.getPpId();
                    if (StringUtils.isNotEmpty(ppId)) {
                        LOGGER.info("AdvierPPID retrieved from user profile. The AdviserPPID related to Adviser is: {}.", ppId);
                        permissionsDto.setPermission(LC_LINK_VIEW, true);
                    }
                }
            }
        }
    }

    private void setUARPermission(PermissionsDto permissionsDto) {
        log.info("uar feature flag: "+ featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle(FeatureToggles.UAR_VIEW));
        log.info("uar avaloq permission: "+ permissionsDto.hasPermission("client.uar.view"));
        if (featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle(FeatureToggles.UAR_VIEW) && permissionsDto.hasPermission("client.uar.view")) {
            permissionsDto.setPermission("uar.view", true);
        }
    }

    private static boolean isWestpacAdvisedInvestor(Broker dealerGroupBroker) {
        final ExternalBrokerKey externalBrokerKey = dealerGroupBroker.getExternalBrokerKey();
        if (externalBrokerKey != null) {
            return WPAC_BRANDED_INVESTOR_BROKER_KEYS.contains(externalBrokerKey.getId());
        }
        return false;
    }

    private void setInvestorNonFunctionalPermissions(PermissionsDto permissionsDto) {
        permissionsDto.setPermission("user.detail.investor.view", true);
        permissionsDto.setPermission("investor.view", true);
        permissionsDto.setPermission("recent.accounts.view", false);
        permissionsDto.setPermission("products.termdeposit.menu.view", false);
        isWPLUser(permissionsDto);
        isTermdepositProdcutCSV(permissionsDto);
    }

    private void isTermdepositProdcutCSV(PermissionsDto permissionDto) {
        if (!isWestpacAdvisedInvestor(userProfileService.getDealerGroupBroker())) {
            permissionDto.setPermission("products.termdeposit.csv.view", true);
        }

    }

    private void isWPLUser(PermissionsDto permissionDto) {
        List userGroup = userProfileService.getSamlToken().getUserGroup();
        if (Properties.getSafeBoolean("wpl.integration.enabled")) {
            for (int i = 0; i < userGroup.size(); i++) {
                if (userGroup.get(i).equals(UserGroup.WPL_USER)) {
                    permissionDto.setPermission("user.detail.investor.wplusermessage.view", true);
                    break;
                }
            }
        }
    }

    private void updateAdviserNonFunctionalRoles(PermissionsDto permissionsDto, boolean hasMarketDataPermission,
                                                 List<Product> dealerGroupProducts) {
        setIntermediaryPermissions(permissionsDto);
        setProductPermissions(permissionsDto, dealerGroupProducts);
        permissionsDto.setPermission("client.alerts.unread.view", true);
        permissionsDto.setPermission("clientlist.adviser.view", true);
        permissionsDto.setPermission("products.termdeposit.csv.view", true);
        if (hasMarketDataPermission) {
            permissionsDto.setPermission("marketinformation.depth.view", true);
        }

    }

    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1199"})
    public boolean checkMarketViewPermission(UserProfile userProfile, ServiceErrors serviceErrors, Broker dealerGroupBroker) {

        // IM will not get the market information access.
        switch (userProfile.getJobRole()) {
            case INVESTOR: {
                return checkInvestorViewMarketDataPermission(userProfile, serviceErrors);
            }
            case ADVISER:
            case PARAPLANNER:
            case ASSISTANT:
            case DEALER_GROUP_MANAGER:
                return checkBrokerViewMarketDataPermission(dealerGroupBroker);
            case ACCOUNTANT:
            case ACCOUNTANT_SUPPORT_STAFF:
                return true;
            default: {
                break;
            }
        }
        return false;
    }

    private boolean checkBrokerViewMarketDataPermission(Broker broker) {
        if (broker != null && BooleanUtils.isTrue(broker.canViewMarketData())) {
            return true;
        }
        return false;
    }

    private boolean checkInvestorViewMarketDataPermission(UserProfile userProfile, ServiceErrors serviceErrors) {
        List<Broker> advisersList = brokerHelperService.getAdviserListForInvestor(userProfile, serviceErrors);
        for (Broker broker : advisersList) {
            BrokerKey dealerGroupKey = broker.getDealerKey();
            if (dealerGroupKey != null) {
                Broker dealerGroup = brokerService.getBroker(dealerGroupKey, serviceErrors);
                if (dealerGroup != null && BooleanUtils.isTrue(dealerGroup.canViewMarketData())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setAccountantNonFunctionalPermissions(PermissionsDto permissionsDto) {
        setIntermediaryPermissions(permissionsDto);
        permissionsDto.setPermission("clientlist.adviser.view", true);
        permissionsDto.setPermission("clientlist.accountant.view", true);
        permissionsDto.setPermission("messages.accountant.view", true);
        permissionsDto.setPermission("documents.accountant.view", true);
        permissionsDto.setPermission("client.alerts.unread.view", true);
        permissionsDto.setPermission("products.termdeposit.csv.view", true);
        setMarketViewForAccountantandSupportStaff(permissionsDto);

    }

    private void setDealerGroupPermissions(PermissionsDto permissionsDto) {
        setIntermediaryPermissions(permissionsDto);
        permissionsDto.setPermission("intermediary.adviser.report", true);
        permissionsDto.setPermission("modelportfolio.tailored.view", true);
        permissionsDto.setPermission("tracking.view", true);
        permissionsDto.setPermission("products.termdeposit.csv.view", true);
    }

    private void setInvestmentManagerPermissions(PermissionsDto permissionsDto) {
        permissionsDto.setPermission("intermediary.adviser.report", true);
        permissionsDto.setPermission("modelportfolio.readymade.view", true);
        permissionsDto.setPermission("modelportfolio.rcti.view", true);
        permissionsDto.setPermission("tracking.view", true);
        permissionsDto.setPermission("products.termdeposit.csv.view", true);
    }

    private void setPortfolioManagerPermissions(PermissionsDto permissionsDto) {
        permissionsDto.setPermission("intermediary.adviser.report", true);
        permissionsDto.setPermission("modelportfolio.tailored.view", true);
        permissionsDto.setPermission("modelportfolio.rcti.view", false);
        permissionsDto.setPermission("tracking.view", true);
        permissionsDto.setPermission("products.termdeposit.csv.view", false);
        permissionsDto.setPermission("mda.intermediary.report.view", true);
    }

    private void setAccountantSupportStaffNonFunctionalPermissions(PermissionsDto permissionsDto) {
        setIntermediaryPermissions(permissionsDto);
        permissionsDto.setPermission("clientlist.accountant.view", true);
        permissionsDto.setPermission("messages.accountant.view", true);
        permissionsDto.setPermission("documents.accountant.view", true);
        permissionsDto.setPermission("products.termdeposit.csv.view", true);
        setMarketViewForAccountantandSupportStaff(permissionsDto);

    }
    private void setMarketViewForAccountantandSupportStaff(PermissionsDto permissionsDto){
        permissionsDto.setPermission("marketinformation.realtimeprice.view", true);
        permissionsDto.setPermission("marketinformation.view", true);
    }

    private void setIntermediaryPermissions(PermissionsDto permissionsDto) {
        permissionsDto.setPermission("user.detail.intermediary.view", true);
        permissionsDto.setPermission("recent.accounts.view", true);
        permissionsDto.setPermission("products.termdeposit.csv.view", true);

    }

    private void setInvestmentOrdersPermission(PermissionsDto permissionsDto) {
        if (permissionsDto.hasPermission("account.trade.create") || permissionsDto.hasPermission("account.order.view")) {
            permissionsDto.setPermission("account.investmentorders.menu.view", true);
        } else {
            permissionsDto.setPermission("account.investmentorders.menu.view", false);
        }
    }

    private void setInvestmentPreferencesPermission(PermissionsDto permissionsDto) {
        if (permissionsDto.hasPermission("client.intermediary.report.view")
                || permissionsDto.hasPermission("account.details.update")) {
            permissionsDto.setPermission("account.investmentpreferences.view", true);
        } else {
            permissionsDto.setPermission("account.investmentpreferences.view", false);
        }
    }

    private void setFeatureToggles(PermissionsDto permissionsDto, ServiceErrors serviceErrors) {
        final FeatureToggles toggles = featureTogglesService.findOne(serviceErrors);
        for (String name : toggles.getToggleNames()) {
            permissionsDto.setPermission(GLOBAL_FEATURE_PREFIX + name, toggles.getFeatureToggle(name));
        }
    }

    /**
     * Checks if a user has access to a product in their dealer group product list
     * 
     * @param permissionsDto
     */
    private void setProductPermissions(PermissionsDto permissionsDto, List<Product> dealerGroupProducts) {
        boolean superExists = false;
        if (isNotEmpty(dealerGroupProducts)) {
            for (Product product : dealerGroupProducts) {
                if (ProductToggleEnum.NEW_SMSF_INDIVIDUAL.getProductShortNameList().contains(product.getShortName())) {
                    permissionsDto.setPermission("account.application.newsmsf.individual.create", true);
                }

                else if (ProductToggleEnum.NEW_SMSF_CORPORATE.getProductShortNameList().contains(product.getShortName())) {
                    permissionsDto.setPermission("account.application.newsmsf.corporate.create", true);
                }

                else if (product.isSuper() && superForASIM()) {
                    permissionsDto.setPermission("account.application.super.create", true);
                    superExists = true;
                }
            }
        }

        // business screen Beneficiaries list permission is set thru FR (View_intermediary_reports) for intermediary
        // resetting it if DG doesn't have SUPER product
        if (!superExists) {
            permissionsDto.setPermission("business.beneficiaries.view", false);
        }

    }

    private boolean superForASIM() {
        if (userProfileService.getActiveProfile().getUserExperience() == UserExperience.ASIM) {
            return Properties.getSafeBoolean("feature.superforASIM");
        }
        return true;
    }

    private void togglePermissionKeys(Collection<String> keyList, PermissionsDto permissionsDto, Boolean toggle) {
        for (String key : keyList) {
            permissionsDto.setPermission(key, toggle);
        }
    }

    /**
     * Sets the "marketinformation.realtimeprice.view" permission.
     * <p>
     * A user has permission to view live prices in market information if they have permission to create trades and they are not
     * an investor.
     * 
     * @param permissionsDto
     * @param jobRole
     */
    private void setMarketInformationRealtimePricePermission(PermissionsDto permissionsDto, JobRole jobRole) {
        permissionsDto.setPermission("marketinformation.realtimeprice.view",
                jobRole != JobRole.INVESTOR && permissionsDto.hasPermission("account.trade.create"));
    }

    /**
     * Sets tracking permission
     * <p/>
     * Viewable if FR has intermediary adviser report permission
     *
     * @param permissionsDto
     */
    private void setTrackingPermission(PermissionsDto permissionsDto) {
        permissionsDto.setPermission("tracking.view", permissionsDto.hasPermission("intermediary.adviser.report") ||
                hasCorporateActionApprovalPermission(permissionsDto));
    }

    private boolean hasCorporateActionApprovalPermission(PermissionsDto permissionsDto) {
        return permissionsDto.hasPermission("corporateactions.approval.view") ||
                permissionsDto.hasPermission("corporateactions.approval.transact");
    }

    /**
     * Sets any corporate actions permission.
     * <p/>
     * Viewable if FR can submit trade, view adviser reports or view model portfolios
     *
     * @param permissionsDto object of {@link PermissionsDto}
     */
    private void setCorporateActionsPermission(PermissionsDto permissionsDto) {
        // ***** These mess will be cleaned up once the corporate action functional roles for DG, IM, Adv are setup correctly in
        // Avaloq.
        if (permissionsDto.hasPermission("account.order.view") || permissionsDto.hasPermission("intermediary.adviser.report")
                || permissionsDto.hasPermission("modelportfolio.view")) {
            permissionsDto.setPermission("corporateactions.view", true);
        }

        if (permissionsDto.hasPermission("account.trade.create") || permissionsDto.hasPermission("account.trade.submit")
                || permissionsDto.hasPermission("modelportfolio.upload") || permissionsDto.hasPermission("modelportfolio.create")) {
            permissionsDto.setPermission("corporateactions.view", true);
            permissionsDto.setPermission("corporateactions.transact", true);
        }

        if (userProfileService.isDealerGroup()) {
            permissionsDto.setPermission("corporateactions.dealergroup.view", true);
        } else if (userProfileService.isInvestmentManager()) {
            permissionsDto.setPermission("corporateactions.investmentmanager.view", true);
        }
    }

    /**
     * This method sets the base level insurance permissions for all {@link JobRole} other than INVESTMENT MANAGER
     *  @param permissionsDto object of {@link PermissionsDto}
     * @param jobRole        object of {@link JobRole}
     * @param dealerGroupProducts - List of dealer group products
     */
    private void setInsurancePermissions(PermissionsDto permissionsDto, JobRole jobRole, List<Product> dealerGroupProducts) {
        List<JobRole> jobRoleList = Arrays.asList(JobRole.INVESTMENT_MANAGER, JobRole.ACCOUNTANT,
                JobRole.ACCOUNTANT_SUPPORT_STAFF, JobRole.SERVICE_AND_OPERATION, JobRole.SERVICE_AND_OPERATION_LIMITED,
                JobRole.PORTFOLIO_MANAGER);
        // The insurance permissions are valid for all job role types other than INVESTMENT MANAGER
        // ACCOUNTANT AND ACCOUNTANT_SUPPORT_STAFF have been excluded here because DealergroupProducts are not applicable to them
        // Only Account level permission are applicable in this case
        if (!jobRoleList.contains(jobRole)) {
            boolean canViewInsurance = false;
            if (!userProfileService.isServiceOperator() && isNotEmpty(dealerGroupProducts)) {
                for (Product product : dealerGroupProducts) {
                    if (ProductToggleEnum.validateProductShortName(ProductToggleEnum.INSURANCE, product.getShortName())) {
                        canViewInsurance = true;
                        break;
                    }
                }
            }

            if (canViewInsurance == false) {
                togglePermissionKeys(FunctionalRole.View_Insurance_Account.getUiRoles(), permissionsDto, false);
                togglePermissionKeys(FunctionalRole.View_Insurance_Commissions.getUiRoles(), permissionsDto, false);

                final String[] businessReportKeys = {"account.insurance.businessreport.view", "account.insurance.applications.view"};
                final Collection<String> businessReportKeyList = Arrays.asList(businessReportKeys);
                togglePermissionKeys(businessReportKeyList, permissionsDto, false);
            }
        }

    }

    /**
     * Sets header/menu permissions.
     * <p/>
     *
     * @param permissionsDto object of {@link PermissionsDto}
     */
    private void setMenuHeaderPermission(PermissionsDto permissionsDto) {
        if (hasCorporateActionApprovalPermission(permissionsDto)) {
            permissionsDto.setPermission("messages.menu.view", false);
            permissionsDto.setPermission("help.menu.view", false);
        } else {
            permissionsDto.setPermission("messages.menu.view", true);
            permissionsDto.setPermission("help.menu.view", true);
        }
    }

    /**
     * Sets Trustee/IRG specific permissions.
     * <p/>
     *
     * @param permissionsDto object of {@link PermissionsDto}
     * @param info
     * @param serviceErrors
     */
    private void setCorporateActionApprovalPermissions(PermissionsDto permissionsDto, UserProfile info,
                                                       ServiceErrors serviceErrors) {
        boolean readOnlyPermission = permissionsDto.hasPermission("corporateactions.approval.view");
        boolean transactPermission  = permissionsDto.hasPermission("corporateactions.approval.transact");

        if (readOnlyPermission || transactPermission) {
            permissionsDto.setPermission("recent.accounts.view", false);
            permissionsDto.setPermission("user.detail.intermediary.view", false);

            // This line is required to turn off UI components in the UI kernel
            permissionsDto.setPermission("trustee.view", true);
            permissionsDto.setPermission("corporateactions.approval.view", true);

            List<String> roles = info.getUserRoles();

            if (roles.contains(UserRole.IRG_BASIC.getRole()) || roles.contains(UserRole.IRG_READ_ONLY.getRole())) {
                permissionsDto.setPermission("corporateactions.irg.approval.view", true);
                permissionsDto.setPermission("corporateactions.irg.approval.transact", transactPermission);
            } else {
                permissionsDto.setPermission("corporateactions.trustee.approval.view", true);
                permissionsDto.setPermission("corporateactions.trustee.approval.transact", transactPermission);
            }
        }
    }

    private List<Product> getDealerGroupProducts(UserProfile userProfile, ServiceErrors serviceErrors) {
        final List<Product> dealerGroupProducts = new ArrayList<>();
        // INVESTMENT_MANAGER, ACCOUNTANT, PORTFOLIO_MANAGER AND ACCOUNTANT_SUPPORT_STAFF are excluded because DealergroupProducts
        // are not applicable to them
        final List<JobRole> productExclusionJobRoleList = Arrays.asList(JobRole.INVESTMENT_MANAGER, JobRole.ACCOUNTANT,
                JobRole.ACCOUNTANT_SUPPORT_STAFF, JobRole.SERVICE_AND_OPERATION, JobRole.SERVICE_AND_OPERATION_LIMITED,
                JobRole.PORTFOLIO_MANAGER);
        if (!productExclusionJobRoleList.contains(userProfile.getJobRole())) {
            List<BrokerKey> brokerKeyList = new ArrayList<>();
            final List<Broker> brokerList = new ArrayList<>();
            // For Investors (basically ASIM), add all the advisers
            if (JobRole.INVESTOR.equals(userProfile.getJobRole())) {
                brokerList.addAll(brokerHelperService.getAdviserListForInvestor(userProfile, serviceErrors));
            } else {
                brokerList.addAll(brokerService.getBrokersForJob(userProfile, serviceErrors));
            }
            //Extract Broker Keys
            brokerKeyList  = LambdaCollections.with(brokerList).extract(Lambda.on(Broker.class).getDealerKey());
            dealerGroupProducts.addAll(productService.getDealerGroupProductList(brokerKeyList, serviceErrors));
        }
        return dealerGroupProducts;
    }
}
