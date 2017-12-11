package com.bt.nextgen.api.profile.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.profile.v1.service.ProfileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bt.nextgen.api.profile.model.HomePageEnum;
import com.bt.nextgen.api.profile.model.JobRoleConverter;
import com.bt.nextgen.api.profile.model.ProfileDetailsDto;
import com.bt.nextgen.api.profile.model.ProfileRoles;
import com.bt.nextgen.api.userpreference.model.UserPreferenceEnum;
import com.bt.nextgen.badge.service.BadgingService;
import com.bt.nextgen.core.repository.User;
import com.bt.nextgen.core.repository.UserPreference;
import com.bt.nextgen.core.repository.UserPreferenceRepository;
import com.bt.nextgen.core.repository.UserRepository;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditionsKey;
import com.bt.nextgen.service.integration.registration.repository.UserRoleTermsAndConditionsRepository;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.bt.nextgen.web.controller.HomePageController;

import static com.bt.nextgen.core.session.SessionUtils.ORIGINATING_SYSTEM;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.of;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * This service retrieves profile details for the current user's active role.
 */
@Deprecated
@Service("profileDetailsService")
@Transactional(value = "springJpaTransactionManager")
@SuppressWarnings({"squid:S1172"})
class ProfileDetailsDtoServiceImpl implements ProfileDetailsDtoService {

    /**
     * Job roles that don't have brokers attached to them.
     *
     * @see #setRoles(ProfileDetailsDto, UserProfile, ServiceErrors)
     */
    private static final Set<JobRole> NON_BROKER_ROLES = unmodifiableSet(
        of(JobRole.INVESTOR, JobRole.ACCOUNTANT, JobRole.ACCOUNTANT_SUPPORT_STAFF));

    /**
     * Comparator to sort Profile Role instances by role name.
     */
    private static final Comparator<ProfileRoles> BY_ROLE = new Comparator<ProfileRoles>() {
        @Override
        public int compare(ProfileRoles role1, ProfileRoles role2) {
            return role1.getRole().compareTo(role2.getRole());
        }
    };

    private static final int MAX_ADVISERS = 5;
    private static final Logger logger = getLogger(ProfileDetailsDtoServiceImpl.class);

    @Autowired
    private InvestorProfileService profileService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private BadgingService badgingService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    private UserRoleTermsAndConditionsRepository userRoleTncRepository;

    @Autowired
    private ProfileUtil profileUtil;

    private RequestContextHolder requestContextHolder;

    @Autowired
    @Qualifier("investorHomepageEvaluator")
    private InvestorHomepageEvaluator investorHomepageEvaluator;

    private static boolean isWestpacAdviser(Broker dealerGroupBroker) {
        final ExternalBrokerKey parentEbiKey = dealerGroupBroker.getParentEBIKey();
        //return parentEbiKey != null && "WPAC".equals(parentEbiKey.getId());
        return true;
    }

    @Override
    public boolean isWestpacAdviser() {
        //TODO - UPS REFACTOR1 - this call is adviser specific, we should consider passing the OE position in here.
        final Broker dealerGroupBroker = profileService.getDealerGroupBroker();
        //return dealerGroupBroker != null && isWestpacAdviser(dealerGroupBroker);
        return true;
    }

    @Override
    public boolean isWestpacBrandedAdviser() {
        //TODO - UPS REFACTOR1 - This is Adviser specific and should probably be in terms of the OE position of their current job.
        //return isWestpacBrandedAdviser(profileService.getDealerGroupBroker());
        return true;
    }

    private static boolean isWestpacBrandedAdviser(@Nullable Broker dealerGroupBroker) {
        //return dealerGroupBroker != null && isTrue(dealerGroupBroker.canRetrieveGcmData());
        return true;
    }


    private static boolean isOfflineApproval(@Nullable Broker dealerGroupBroker) {
        return dealerGroupBroker != null && isTrue(dealerGroupBroker.isOfflineApproval());
    }

    @Override
    public boolean isOfflineApproval() {
        //TODO - UPS REFACTOR1 - This is Adviser specific and should probably be in terms of the OE position of their current job.
        return isOfflineApproval(profileService.getDealerGroupBroker());
    }

    @Override
    public ProfileDetailsDto findOne(ServiceErrors serviceErrors) {

        logger.info("Loading profile details");
        ProfileDetailsDto profile = new ProfileDetailsDto();
        boolean existingUser = profileService.isExistingAvaloqUser();
        logger.info("Existing user in ABS: {}", existingUser);

        if (existingUser) {
            UserProfile activeProfile = profileService.switchActiveProfile(profileUtil.getProfileId());
            setNameAndIds(profile, activeProfile);
            setIntermediaryDetails(profile, activeProfile, serviceErrors);
            setRoles(profile, activeProfile, serviceErrors);
            profile.setLogoUrl(badgingService.getBadgeForCurrentUser(serviceErrors).getLogo());
            profile.setLogoTitle(badgingService.getBadgeForCurrentUser(serviceErrors).getBadgeName());
            Map<AccountKey, WrapAccount> accountMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);

            if (activeProfile.getJobRole() == JobRole.INVESTOR) {
                profile.setCanNavigate(false);
                investorHomepageEvaluator.setInvestorHomepageDetails(profile, accountMap, serviceErrors);
            } else {
                setDealerGroupDetails(profile);
                profile.setHomePage(getIntermediaryHomepage(profile, activeProfile));

                //all roles except for investors, by default can navigate the site
                profile.setCanNavigate(true);
            }

            profile.setWhatsNew(getWhatsNewStatus(activeProfile));

            //TODO: update after WPL integration
            profile.setReferringSystem("panorama");
            updateLastAccessedAccount(profile, accountMap, serviceErrors);
        } else {
            profile.setHomePage(HomePageEnum.DIRECT_ONBOARDING.toString());
            profile.setCanNavigate(true);
        }
        setOriginatingSystem(profile);

        logger.info("Successfully loaded profile details");
        return profile;
    }

    /**
     * Set the originating system based on channel
     *
     * @param profile
     * @return
     */
    private ProfileDetailsDto setOriginatingSystem(ProfileDetailsDto profile) {
        String originatingSystem = (String) getSessionObject().getAttribute(ORIGINATING_SYSTEM);

        if (!StringUtils.isEmpty(originatingSystem) && HomePageController.CHANNEL_WESTPAC_LIVE.equalsIgnoreCase(originatingSystem)) {
            profile.setOriginatingSystem(HomePageController.CHANNEL_WESTPAC_LIVE);
        }

        return profile;
    }

    public HttpSession getSessionObject() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        return session;
    }

    /**
     * Update the account id in the profile to the user's last accessed account if the details exist
     *
     * @param profile
     * @param serviceErrors
     */
    private void updateLastAccessedAccount(ProfileDetailsDto profile, Map<AccountKey, WrapAccount> accountMap,
                                           ServiceErrors serviceErrors) {
        final UserPreference lastAccessedAccountDetails = userPreferenceRepository
            .find(profile.getId(), UserPreferenceEnum.LAST_ACCESSED_ACCOUNT.getPreferenceKey());

        if (lastAccessedAccountDetails != null) {
            String accountId = lastAccessedAccountDetails.getValue();
            WrapAccount account = Lambda
                .selectFirst(accountMap, Lambda.having(Lambda.on(WrapAccount.class).getAccountKey().getId(), Matchers.is(accountId)));

            // Set last accessed account to profile if it is still active.
            if (account != null && AccountStatus.ACTIVE == account.getAccountStatus()) {
                profile.setAccountId(EncodedString.fromPlainText(accountId).toString());
            }
        }
    }

    /**
     * Set intermediary related details
     *
     * @param profile
     * @param activeProfile
     * @param serviceErrors
     */
    private void setIntermediaryDetails(ProfileDetailsDto profile, UserProfile activeProfile, ServiceErrors serviceErrors) {
        final JobRole activeRole = activeProfile.getJobRole();
        profile.setIntermediary(activeRole != JobRole.INVESTOR);
        profile.setIntermediaryCount(getIntermediaryCount(activeProfile, serviceErrors));
        profile.setClientMessage(activeRole != JobRole.INVESTOR && activeRole != JobRole.INVESTMENT_MANAGER);
    }

    /**
     * Set the user's name and relevant id's
     *
     * @param profile
     * @param activeProfile
     */
    private void setNameAndIds(ProfileDetailsDto profile, UserProfile activeProfile) {
        profile.setName(profileService.getFullName());
        profile.setId(activeProfile.getBankReferenceId());
        profile.setPersonId(
            activeProfile.getClientKey() != null ? EncodedString.fromPlainText(activeProfile.getClientKey().getId()).toString() : null);

        final String positionId = profileService.getPositionId();
        if (StringUtils.isNotBlank(positionId)) {
            profile.setPositionId(EncodedString.fromPlainText(positionId).toString());
        }

        profile.setUserExperience(activeProfile.getUserExperience() != null ? activeProfile.getUserExperience().name() : null);
    }

    /**
     * Set details related which relate to the dealer group
     *
     * @param profile
     */
    private void setDealerGroupDetails(ProfileDetailsDto profile) {
        //TODO - UPS REFACTOR1 - This needs to be account specific or expect a list
        final Broker dealerGroupBroker = profileService.getDealerGroupBroker();
        if (dealerGroupBroker != null) {
            profile.setDealerGroupName(dealerGroupBroker.getPositionName());
            profile.setWestpacAdviser(isWestpacAdviser(dealerGroupBroker));
            profile.setWestpacBrandedAdviser(isWestpacBrandedAdviser(dealerGroupBroker));
            profile.setOfflineApproval(isOfflineApproval(dealerGroupBroker));
        }
    }


    private void setRoles(ProfileDetailsDto profileDto, UserProfile activeProfile, ServiceErrors serviceErrors) {
        final List<JobProfile> availableProfiles = profileService.getAvailableProfiles();
        final int profileCount = availableProfiles.size();
        logger.info("Retrieving all roles for this user [{}]", profileCount);
        final List<ProfileRoles> roles = new ArrayList<>(profileCount);
        for (JobProfile profile : availableProfiles) {
            final ProfileRoles role = new ProfileRoles();
            role.setRole(JobRoleConverter.valueOf(profile.getJobRole().name()).toString());
            final UserExperience experience = profile.getUserExperience();
            role.setUserExperience(experience == null ? null : experience.name());

            //Added for TnC Status
            UserRoleTermsAndConditionsKey key = new UserRoleTermsAndConditionsKey(profileService.getEffectiveProfile().getGcmId(),
                profile.getProfileId());
            UserRoleTermsAndConditions userRole = userRoleTncRepository.find(key);
            if (userRole != null) {
                final boolean accepted = "Y".equalsIgnoreCase(userRole.getTncAccepted());
                role.setTncStatus(accepted ? "accepted" : "notaccepted");
            }

            final String profileId = profile.getProfileId();
            final boolean active = activeProfile.getProfileId().equalsIgnoreCase(profileId);
            role.setProfileId(EncodedString.fromPlainText(profileId).toString());
            role.setActive(active);
            logger.trace("Job Profile ID: {}; role: {}; active: {}; experience: {}", profileId, profile.getJobRole(), active, experience);

            //TODO: need to change the condition once get the details of context switching for accountant
            if (!NON_BROKER_ROLES.contains(profile.getJobRole())) {
                List<Broker> brokerList = brokerService.getBrokersForJob(profile, serviceErrors);
                role.setCount(brokerList.size());
                role.setNames(getRoleNames(profile.getJobRole(), brokerList, serviceErrors));
            }
            roles.add(role);
        }
        sort(roles, BY_ROLE);
        profileDto.setRoles(roles);
        logger.info("Retrieved {} role(s)", roles.size());
    }

    private List<String> getRoleNames(JobRole role, List<Broker> brokerList, ServiceErrors serviceErrors) {
        List<String> nameList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(brokerList)) {
            Broker firstAssociatedBroker = brokerList.get(0);
            switch (role) {
                case ADVISER:
                    // get Dealer group name
                    final Broker dealerGroup = brokerService.getBroker(firstAssociatedBroker.getParentKey(), serviceErrors);
                    nameList.add(dealerGroup.getPositionName());
                    break;
                case DEALER_GROUP_MANAGER:
                case PRACTICE_MANAGER:
                    // get position name [there should be only 1 associated broker]
                    nameList.add(firstAssociatedBroker.getPositionName());
                    break;
                case PARAPLANNER:
                case ASSISTANT:
                    nameList.addAll(getSupportRoleNames(brokerList, serviceErrors));
                    break;
                default:
                    // Add position name
                    nameList.add(firstAssociatedBroker.getPositionName());
            }
        }
        return nameList;
    }

    /**
     * Get the first broker role names which are supported by this job. A Paraplanner/Assistant should either be linked to DG/Practice(DG
     * Support) or Advisers(Adviser Support).
     *
     * @param brokerList
     * @param serviceErrors
     * @return
     */
    private List<String> getSupportRoleNames(List<Broker> brokerList, ServiceErrors serviceErrors) {
        List<String> nameList = new ArrayList<>();
        int count = 0;
        for (Broker broker : brokerList) {
            if (broker.getBrokerType() == BrokerType.DEALER || broker.getBrokerType() == BrokerType.PRACTICE) {
                // If linked to DG or Practice, get position name
                nameList.add(broker.getPositionName());
            }

            if (broker.getBrokerType() == BrokerType.ADVISER) {
                BrokerUser brokerUser = brokerService.getAdviserBrokerUser(broker.getKey(), serviceErrors);
                nameList.add(brokerUser.getFirstName() + " " + brokerUser.getLastName());
                count++;
                if (count >= MAX_ADVISERS) {
                    break;
                }
            }
        }
        return nameList;
    }

    // Determine the landing page for intermediaries.
    // Default key is terms and conditions - if they have not already been agreed to
    private String getIntermediaryHomepage(ProfileDetailsDto profile, UserProfile activeProfile) {
        UserRoleTermsAndConditionsKey key = new UserRoleTermsAndConditionsKey(profileService.getEffectiveProfile().getGcmId(),
            activeProfile.getProfileId());
        UserRoleTermsAndConditions userRole = userRoleTncRepository.find(key);

        if (userRole == null || !"Y".equalsIgnoreCase(userRole.getTncAccepted())) {
            profile.setCanNavigate(false);
            return HomePageEnum.INTERMEDIARY_TERMS_AND_CONDITIONS.toString();
        }

        profile.setCanNavigate(true);
        return getIntermediaryHomepageAfterTncs(activeProfile.getJobRole());
    }

    @SuppressWarnings("squid:MethodCyclomaticComplexity")
    private String getIntermediaryHomepageAfterTncs(JobRole jobRole) {
        switch (jobRole) {
            case ADVISER:
            case ASSISTANT:
            case PARAPLANNER:
                return HomePageEnum.ACT_DASHBOARD.toString();
            case ACCOUNTANT:
            case ACCOUNTANT_SUPPORT_STAFF:
                return HomePageEnum.CLIENT_LIST.toString();
            case INVESTMENT_MANAGER:
                return HomePageEnum.MODEL_LIST.toString();
            case DEALER_GROUP_MANAGER:
            case PRACTICE_MANAGER:
                return HomePageEnum.MONITOR_DASHBOARD.toString();
            case TRUSTEE:
                return HomePageEnum.TRUSTEE_CORPORATE_ACTIONS.toString();
            default:
                return HomePageEnum.OTHER.toString();
        }
    }

    private int getIntermediaryCount(UserProfile activeProfile, ServiceErrors serviceErrors) {
        logger.info("Retrieving the number of advisers linked to the user");
        if (activeProfile.getJobRole() == JobRole.INVESTOR || activeProfile.getJobRole() == JobRole.ACCOUNTANT
            || activeProfile.getJobRole() == JobRole.ACCOUNTANT_SUPPORT_STAFF) {
            return 0;
        }
        Collection<BrokerIdentifier> adviserList = brokerService.getAdvisersForUser(activeProfile, serviceErrors);
        return adviserList != null ? adviserList.size() : 0;
    }

    private boolean getWhatsNewStatus(UserProfile activeProfile) {
        logger.info("Retrieving what's new viewed status for the user");
        if (activeProfile.getJobRole() == JobRole.INVESTMENT_MANAGER || (activeProfile.getJobRole() == JobRole.INVESTOR && !Properties
            .getSafeBoolean("whats.new.display.investor")) || (activeProfile.getJobRole() != JobRole.INVESTOR && !Properties
            .getSafeBoolean("whats.new.display.intermediary"))) {
            return false;
        }

        User user = userRepository.loadUser(activeProfile.getBankReferenceId());
        if (user == null) {
            user = userRepository.newUser(activeProfile.getBankReferenceId());
            userRepository.update(user);
            return true;
        }
        if (StringUtils.isBlank(user.getWhatsNewVersion())) {
            return true;
        } else {
            return user.getWhatsNewVersion().equalsIgnoreCase(Properties.getString("version")) ? false : true;
        }
    }
}
