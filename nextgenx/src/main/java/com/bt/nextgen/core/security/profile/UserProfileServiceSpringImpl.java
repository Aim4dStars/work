package com.bt.nextgen.core.security.profile;

import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.NotAllowedException;
import com.bt.nextgen.core.security.UserProfileContext;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.broker.BrokerUserImpl;
import com.bt.nextgen.service.avaloq.client.TwoFASecuredClient;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.bt.nextgen.util.Environment;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.integration.userprofile.ProfileIntegrationService;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.SafiDeviceIdentifier;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The default implementation of the user service.
 */
@SuppressWarnings({"squid:S1172", "squid:S1166", "unchecked",
        "checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck",
        "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.CyclomaticComplexityCheck",
        "squid:ClassCyclomaticComplexity"})
@Component(UserProfileServiceSpringImpl.BEAN_NAME)
public class UserProfileServiceSpringImpl implements UserProfileService, ApplicationContextAware, AvaloqBankingAuthorityService,
        InvestorProfileService, UserNameChangeHolder {
    public static final String BEAN_NAME = "userDetailsService";

    public static final String BRAND_SILO = "brandSilo";

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileServiceSpringImpl.class);

    @Autowired
    private StaticIntegrationService staticService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private ProfileIntegrationService profileIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private UserInformationIntegrationService userInformationIntegrationService;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    private BrokerHelperService brokerHelperService;


    @Autowired
    private HttpSession httpSession;

    public static final String EMPTY_USERNAME = "";

    /*
     * ********************************************** Methods to support base profile management, emulating and detection of
     * emulation
     ************************************************/
    @Override
    public Profile getEffectiveProfile() {
        return Profile.fromCurrentSecurityContext();
    }

    @Override
    public Profile getBaseProfile() {
        return Profile.fromOriginalSecurityContext();
    }

    /*
     * **************************************************************** Methods for IP implementation of the UserProfileService
     * interface
     ******************************************************************/

    @Override
    public boolean isLoggedIn() {
        return !com.btfin.panorama.core.security.profile.Profile.isEmptyProfile(getEffectiveProfile());
    }

    @Override
    public com.btfin.panorama.core.security.profile.UserProfile getActiveProfile() {
        if (isLoggedIn()) {
            com.btfin.panorama.core.security.profile.Profile profile = getEffectiveProfile();
            if (profile != null && profile.getActiveProfile() == null)
                if (isEmulating())
                    loadProfileInformationForEmulation();
                else
                    loadProfileInformation();
            if (profile != null)
                return profile.getActiveProfile();

        }

        return null;
    }

    @Override
    public com.btfin.panorama.core.security.profile.UserProfile getActiveProfile(boolean flushProfile) {
        if (flushProfile == true) {
            flushActiveProfile();
        }

        return getActiveProfile();
    }

    private void flushActiveProfile() {
        com.btfin.panorama.core.security.profile.Profile profile = getEffectiveProfile();
        profile.setActiveProfile(null);
    }

    /**
     * Switch the user's active profile to the provided profile id
     *
     * @param profileId set the active profile for the profile id provided
     * @return
     */
    @Override
    public com.btfin.panorama.core.security.profile.UserProfile switchActiveProfile(String profileId) {
        if (StringUtils.isNotBlank(profileId) && !profileId.equalsIgnoreCase(getEffectiveProfile().getCurrentProfileId())) {
            LOGGER.info("Setting active profile to job id: {}", profileId);
            getEffectiveProfile().setCurrentProfileId(profileId);
            return getActiveProfile(true);
        }
        LOGGER.info("Getting first available job profile");
        return getActiveProfile();
    }

    @Override
    public List<JobProfile> getAvailableProfiles() {
        if (isLoggedIn()) {
            com.btfin.panorama.core.security.profile.Profile profile = getEffectiveProfile();
            if (profile != null && profile.getAvailableProfiles() == null)
                loadProfileInformation();

            if (profile != null)
                return getEffectiveProfile().getAvailableProfiles();

        }
        return null;
    }

    /*
     * ********************************************************* Convenience methods from UserProfileService interface
     * implementation
     ***********************************************************/
    @Override
    public Broker getDealerGroupBroker() {
        FindDealerGroup findDealerGroup = new FindDealerGroup(this);
        return findDealerGroup.getDealerGroupBroker();
    }

    @Override
    public String getDealerGroupBrandSilo() {
        FindDealerGroup findDealerGroup = new FindDealerGroup(this);
        return findDealerGroup.getDealerGroupBrandSilo();
    }

    @Override
    public Broker getInvestmentManager(ServiceErrors serviceErrors) {
        List<Broker> brokers = brokerIntegrationService.getBrokersForJob(getActiveProfile(), serviceErrors);
        for (Broker broker : brokers) {
            if (BrokerType.INVESTMENT_MANAGER.equals(broker.getBrokerType())
                    || BrokerType.DEALER.equals(broker.getBrokerType())
                    || BrokerType.PORTFOLIO_MANAGER.equals(broker.getBrokerType())) {
                return broker;
            }
        }
        return null;
    }

    @Override
    public SafiDeviceIdentifier getSafiDeviceIdentifier() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("You are not currently logged in");
        }
        return loadSafiDetails(getEffectiveProfile());
    }

    @Override
    public boolean isSafiDeviceActive() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("You are not currently logged in");
        }
        return loadSafiDetails(getEffectiveProfile()).isSafiDeviceActive();
    }

    @Override
    public void updateSafiDeviceStatus(boolean status) {
        clientIntegrationService.updateDeviceStatus(getActiveProfile().getClientKey(), status, new FailFastErrorsImpl());
        getEffectiveProfile().setSafiDeviceActive(status);
    }

    private com.btfin.panorama.core.security.profile.Profile loadSafiDetails(com.btfin.panorama.core.security.profile.Profile profile) {
        if (profile.getSafiDeviceId() == null) {
            TwoFASecuredClient clientDetail = clientIntegrationService.loadGenericClientDetails(getActiveProfile().getClientKey(), new FailFastErrorsImpl());
            profile.setSafiDeviceId(clientDetail.getSafiDeviceId());
            profile.setSafiDeviceActive(clientDetail.isSafiActive());
        }
        return profile;
    }

    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected Broker getDealerGroupForIntermediary() {
        Broker dealerGroup = null;
        JobProfile jobProfile = getActiveProfile();
        dealerGroup = brokerHelperService.getDealerGroupForIntermediary(jobProfile, new FailFastErrorsImpl());
        if (dealerGroup == null) {
            LOGGER.warn("This user does not have a dealer group");
        }
        return dealerGroup;
    }

    /**
     * @deprecated This method is only to support old functionality. With an investor having multiple advisers we should always
     * get dealer group information from Broker helper Service.
     */
    @Deprecated
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected Broker getDealerGroupForInvestor() {

        logExceptionToRetrieveCallTrace();
        Map<AccountKey, WrapAccount> accountMap = accountIntegrationService
                .loadWrapAccountWithoutContainers(new FailFastErrorsImpl());
        Collection<WrapAccount> accounts = accountMap.values();
        Broker dealerGroup = null;
        if (accounts != null && accounts.size() > 0) {
            for (WrapAccount account : accounts) {
                dealerGroup = brokerHelperService.getDealerGroupForInvestor(account, new FailFastErrorsImpl());
                if (null == dealerGroup.isDirectInvestment() || !dealerGroup.isDirectInvestment()) {
                    return dealerGroup;
                }

            }
        }
        if (dealerGroup == null) {
            LOGGER.warn("This user does not have a dealer group");
        }
        return dealerGroup;
    }

    /**
     * @deprecated This method is only to support old functionality. With an investor having multiple advisers we should always
     * get Adviser Position information from Broker helper Service.
     */
    @Override
    @Deprecated
    public Broker getAdviserForLoggedInInvestor() {
        logExceptionToRetrieveCallTrace();
        Map<AccountKey, WrapAccount> accountMap = accountIntegrationService
                .loadWrapAccountWithoutContainers(new FailFastErrorsImpl());
        Collection<WrapAccount> accounts = accountMap.values();
        Broker adviserPosition = null;
        if (accounts != null && accounts.size() > 0) {
            for (WrapAccount account : accounts) {

                adviserPosition = brokerHelperService.getAdviserForInvestor(account, new FailFastErrorsImpl());
                if (null == adviserPosition.isDirectInvestment() || !adviserPosition.isDirectInvestment()) {
                    return adviserPosition;
                }
            }

        }
        if (adviserPosition == null) {
            LOGGER.warn("No Adviser defined for user ");
        }
        return adviserPosition;
    }

    public String getUserName() {
        if (isLoggedIn()) {
            return getEffectiveProfile().getUserName();
        } else
            return "";
    }

    /**
     * Method to fetch the credential Id.
     */
    @Override
    public String getCredentialId(ServiceErrors serviceErrors) {
        if (isLoggedIn()) {
            return getEffectiveProfile().getCredentialId();

        }
        LOGGER.info("User is not logged in, returning emppty credential id.");
        return Attribute.EMPTY_STRING;
    }

    /*
     * *********************************************** Methods to support the Banking Authority Service
     **************************************************/

    /**
     * This is for use on the call to avaloq and is not for use in the application as it may return the underlying
     *
     * @return The users underlying job profile
     */
    @Override
    public JobProfileIdentifier getJobProfile() {
        if (isEmulating())
            return getBaseProfile().getActiveJobProfile();
        else
            return getEffectiveProfile().getActiveJobProfile();
    }

    @Override
    public JobProfileIdentifier getEmulatedJobProfile() {
        if (isEmulating())
            return getEffectiveProfile().getActiveJobProfile();
        else
            return null;
    }

    @Override
    public JobProfileIdentifier getActiveJobProfile() {
        return getEffectiveProfile().getActiveJobProfile();
    }

    @Override
    public SamlToken getSamlToken() {
        return getBaseProfile().getToken();
    }

    /*
     * *********************** DealerGroup methods to be Deprecated
     *
     *****************************************************/

    @Override
    public String getDealerGroup() {
        Broker dealerGroupBroker = getDealerGroupBroker();
        if (isLoggedIn() && dealerGroupBroker != null && dealerGroupBroker.getDealerKey() != null)
            return dealerGroupBroker.getPositionName();
        else
            return Attribute.EMPTY_STRING;
    }

    /*************************************************************************************
     * Services for initialisation
     *********************************************************/
    private void loadProfileInformation() {
        ServiceErrors errors = new ServiceErrorsImpl();
        if (getEffectiveProfile().getAvailableProfiles() == null || getEffectiveProfile().getAvailableProfiles().size() == 0)
            getEffectiveProfile().setAvailableProfiles(profileIntegrationService.loadAvailableJobProfiles(errors));
        LOGGER.debug("loadProfileInformation: current profile id {} ", getEffectiveProfile().getCurrentProfileId());
        setActiveProfile(getEffectiveProfile().getCurrentProfileId());
        setBrandSiloInSession();
    }

    /**
     * Brandsilo attribute is set in session as brandsilo needs to be set in all ESBHeader's and UserProfileSevice is not available in ESBHeaderInterceptor.
     *
     */
    private void setBrandSiloInSession(){

        if(null==httpSession.getAttribute(BRAND_SILO)) {
            httpSession.setAttribute(BRAND_SILO, getDealerGroupBrandSilo());
        }
    }

    private void loadProfileInformationForEmulation() {
        ServiceErrors errors = new ServiceErrorsImpl();
        LOGGER.debug("Loading emulating profile. SAML token is {}.", getSamlToken()==null ? "null" : "not null");
        if (getEffectiveProfile().getAvailableProfiles() == null) {
            BankingCustomerIdentifier identifier = new BrokerUserImpl(UserKey.valueOf(getGcmId()));
            LOGGER.debug("Loading job profile for user {}.", getGcmId());
            getEffectiveProfile()
                    .setAvailableProfiles(profileIntegrationService.loadAvailableJobProfilesForUser(identifier, errors));
        }
        setActiveProfile(getEffectiveProfile().getCurrentProfileId());
        setBrandSiloInSession();
    }

    private void setActiveProfile(String profileId) {
        UserInformation userInfo = null;
        JobProfile activeJobProfile = null;

        if (getEffectiveProfile() == null)
            throw new IllegalStateException("User has no effective profile so is not authenticated properly");

        activeJobProfile = getMatchingProfile(profileId, getEffectiveProfile().getAvailableProfiles());

        if (activeJobProfile == null)
            activeJobProfile = getFirstProfileFromActiveList(getEffectiveProfile().getAvailableProfiles());

        if (activeJobProfile == null)
            throw new IllegalStateException("Could not find the job profile for the user");

        getEffectiveProfile().setActiveJobProfile(activeJobProfile);
        userInfo = userInformationIntegrationService.loadUserInformation(getEffectiveProfile().getActiveJobProfile(), new ServiceErrorsImpl());
        if (userInfo == null)
            throw new IllegalStateException("No user information could be loaded for the user");

        LOGGER.debug("creating user profile");
        getEffectiveProfile().setActiveProfile(createUserProfile(userInfo, activeJobProfile));
    }

    private JobProfile getMatchingProfile(String profileId, List<JobProfile> jobProfileList) {
        JobProfile matchingProfile = null;
        if (profileId != null && jobProfileList != null) {
            for (JobProfile jobProfile : getEffectiveProfile().getAvailableProfiles()) {
                if (jobProfile.getProfileId().equals(profileId)) {
                    matchingProfile = jobProfile;
                    getEffectiveProfile().setActiveJobProfile(matchingProfile);
                }
            }
        }
        return matchingProfile;
    }

    private JobProfile getFirstProfileFromActiveList(List<JobProfile> jobProfileList) {
        JobProfile firstProfile = null;
        if (jobProfileList != null && jobProfileList.size() > 0) {
            firstProfile = jobProfileList.get(0);

        }
        return firstProfile;
    }

    private com.btfin.panorama.core.security.profile.UserProfile createUserProfile(UserInformation userInfo, JobProfile jobProfile) {
        UserProfile userProfile = new UserProfileAdapterImpl(userInfo, jobProfile);
        return userProfile;
    }

    @Override
    public String getGcmId() {
        return getEffectiveProfile().getGcmId();
    }

    @Override
    public String getUsername() {
        if (isLoggedIn()) {
            return getEffectiveProfile().getUserName();
        } else
            return EMPTY_USERNAME;
    }

    @Override
    public String getFirstName() {

        if (getActiveProfile().getJobRole().equals(JobRole.INVESTOR)) {
            Client client = getClient();
            return client != null ? client.getFirstName() : "";
        } else {
            BrokerUser intermediary = getBrokerUser();
            return intermediary != null ? intermediary.getFirstName() : "";
        }

    }

    @Override
    public String getLastName() {
        try {
            if (getActiveProfile().getJobRole().equals(JobRole.INVESTOR)) {
                Client client = getClient();
                return client != null ? client.getLastName() : "";
            } else {
                BrokerUser intermediary = getBrokerUser();
                return intermediary != null ? intermediary.getLastName() : "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String getFullName() {
        try {
            if (getActiveProfile().getJobRole().equals(JobRole.INVESTOR)) {
                Client client = getClient();
                return client != null ? client.getFirstName() + " " + client.getLastName() : "";
            } else {
                BrokerUser intermediary = getBrokerUser();
                return intermediary != null ? intermediary.getFirstName() + " " + intermediary.getLastName() : "";
            }
        } catch (Exception e) {
            LOGGER.warn("Error loading the user's name", e);
            return "";
        }
    }

    public Client getClient() {
        return clientIntegrationService.loadClientDetails(getActiveProfile().getClientKey(), new FailFastErrorsImpl());
    }

    public BrokerUser getBrokerUser() {
        return brokerIntegrationService.getBrokerUser(getActiveProfile(), new FailFastErrorsImpl());
    }

    @Override
    public String getUserId() {
        return getEffectiveProfile().getUserId();
    }

    @Override
    public boolean getLastLogin() {
        // TODO To implement SAMLService later which will return lastLogin attribute for registered user.
        return true;
    }

    private Broker getPosition() {
        Collection<Broker> myBroker = brokerIntegrationService.getBrokersForJob(getActiveProfile(), new FailFastErrorsImpl());
        if (myBroker != null && myBroker.size() > 0)
            return (Broker) (CollectionUtils.get(myBroker, 0));
        else
            return null;
    }

    @Override
    public String getPositionId() {
        try {
            Broker broker = getPosition();
            if (broker != null)
                return broker.getKey().getId();
            else
                return "";
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String getAvaloqId() {
        return getBaseProfile().getToken().getAvaloqId();
    }

    @Override
    public boolean isEmulating() {
        return getEffectiveProfile().isEmulating();
    }

    @Override
    public boolean isDealerGroup() {
        try {
            return getActiveProfile().getJobRole().equals(JobRole.DEALER_GROUP_MANAGER);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isParaPlanner() {
        try {
            return getActiveProfile().getJobRole().equals(JobRole.PARAPLANNER);
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public boolean isAdminAssistant() {
        try {
            return getActiveProfile().getJobRole().equals(JobRole.ASSISTANT);
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public boolean isInvestor() {
        try {
            return getActiveProfile().getJobRole().equals(JobRole.INVESTOR);
        } catch (Exception err) {
            return false;
        }
    }

    @Override
    public boolean isInvestmentManager() {
        try {
            return getActiveProfile().getJobRole().equals(JobRole.INVESTMENT_MANAGER);
        } catch (Exception err) {
            return false;
        }
    }

    @Override
    public boolean isAdviser() {
        try {
            return getActiveProfile().getJobRole().equals(JobRole.ADVISER);
        } catch (Exception err) {
            return false;
        }
    }

    @Override
    public boolean isAccountant() {
        return getActiveProfile().getJobRole().equals(JobRole.ACCOUNTANT);
    }

    @Override
    public boolean isAccountantSupport() {
        return getActiveProfile().getJobRole().equals(JobRole.ACCOUNTANT_SUPPORT_STAFF);
    }

    @Override
    public boolean isAdmin() {
        return getActiveProfile().getJobRole().equals(JobRole.IT_SUPPORT);
    }

    @Override
    public boolean isServiceOperator() throws IllegalStateException {
        return getEffectiveProfile().getPrimaryRole().equals(Roles.ROLE_SERVICE_OP);
    }

    @Override
    public boolean isPortfolioManager() {
        return getActiveProfile().getJobRole().equals(JobRole.PORTFOLIO_MANAGER);
    }

    @Override
    /**
     * @return the Site which the users SAML token has authority for
     */
    @Deprecated
    public Roles getPrimaryRole() {
        return getEffectiveProfile().getPrimaryRole();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        UserProfileContext.getContext().setProfileService(applicationContext.getBean(UserProfileService.class));
    }

    @Override
    public List<String> getOePositionIds(ServiceErrors serviceErrors) {
        BrokerUser brokerUser = brokerIntegrationService.getBrokerUser(UserKey.valueOf(getUserId()), serviceErrors);
        Collection<BrokerRole> brokerRoles = brokerUser.getRoles();
        List<String> positionIds = new ArrayList<String>();

        for (BrokerRole brokerRole : brokerRoles) {
            positionIds.add(brokerRole.getKey().getId());
        }
        return positionIds;
    }

    @Override
    public void setNewUserNameProvidedByUserForChange(String newUserName) {

        getEffectiveProfile().setNewUserName(newUserName);
    }

    @Override
    public String getNewUserNameProvidedByUserForChange() {

        return getEffectiveProfile().getUserName();
    }

    /**
     * This method is only created to trace which API are calling the deprecated methods thus they can be changed accordingly.
     */
    private void logExceptionToRetrieveCallTrace() {
        try {
            throw new NotAllowedException(ApiVersion.CURRENT_VERSION);
        } catch (NotAllowedException exception) {
            if (Environment.notProduction()) {
                LOGGER.warn("Method call is strictly prohibited as it is deprecated--{}", exception);
            }
        }
    }

    @Override
    public boolean isExistingAvaloqUser() {
        return StringUtils.isNotBlank(getGcmId());
    }

    @PostConstruct
    public void configure() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    public String getPpId() {
        final TwoFASecuredClient clientDetail = clientIntegrationService.
                loadGenericClientDetails(getActiveProfile().getClientKey(), new FailFastErrorsImpl());
        return clientDetail.getPpId();
    }



    @Override
    public String getBrandSiloForIntermediary() {
        return brokerHelperService.getBrandSiloForIntermediary(getActiveProfile(),new FailFastErrorsImpl());
    }

    @Override
    public String getBrandSiloForInvestor() {
        return brokerHelperService.getBrandSiloForInvestor(new FailFastErrorsImpl());
    }
}
