package com.bt.nextgen.serviceops.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v3.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.ServiceOpsClientApplicationDto;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ApprovalTypeEnum;
import com.bt.nextgen.api.draftaccount.service.ServiceOpsClientApplicationDtoConverterService;
import com.bt.nextgen.api.draftaccount.service.ViewClientApplicationDetailsService;
import com.bt.nextgen.api.tracking.model.PersonInfo;
import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.api.tracking.service.AccountStatusService;
import com.bt.nextgen.api.tracking.service.InvestorStatusServiceForTechnicalSupport;
import com.bt.nextgen.api.tracking.service.TrackingDtoService;
import com.bt.nextgen.clients.web.model.ClientModel;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.RoleStatusActionMapping;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.repository.CisKeyClientApplicationRepository;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.core.repository.OnboardingStatusInterface;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.btfin.panorama.core.security.Roles;
import com.bt.nextgen.core.security.UserRole;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.CredentialService;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.core.web.model.Intermediaries;
import com.bt.nextgen.core.web.model.Investor;
import com.bt.nextgen.core.web.model.PersonInterface;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.UserSearchRequest;
import com.bt.nextgen.service.avaloq.UserSearchRequestModel;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.broker.BrokerUserImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.search.PersonSearchRequestImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementIntegrationServiceV6;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.account.WrapAccountDetailResponse;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.search.PersonResponse;
import com.bt.nextgen.service.integration.search.PersonSearchIntegrationService;
import com.bt.nextgen.service.integration.search.PersonSearchRequest;
import com.bt.nextgen.service.integration.search.ProfileUserRole;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.Person;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.integration.userprofile.ProfileIntegrationService;
import com.bt.nextgen.serviceops.controller.ServiceOpsConverter;
import com.bt.nextgen.serviceops.model.*;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.bt.nextgen.userauthority.web.Action;
import com.bt.nextgen.userdetails.web.UserDetailsConverter;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.util.StringUtil;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.*;


@Service
@Transactional
public class ServiceOpsServiceImpl implements ServiceOpsService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceOpsServiceImpl.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    private static final String SERVICE_OPS = "SERVICE_OPS";
    private static final String CSV_SEPARATOR = ",";
    private static final String CORP_TRUSTEE_DISPLAY_STATUS = "CorporateTrusteeEstablishmentinprogress";
    private static final String OFFLINE_STR = "Offline";

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ServiceOpsClientApplicationDtoConverterService serviceOpsClientApplicationDtoConverterService;

    @Autowired
    private PermittedClientApplicationRepository clientApplicationRepository;

    @Autowired
    private UserAccountStatusService userAccountStatusService;

    @Autowired
    private PersonSearchIntegrationService personSearchIntegrationService;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    private StaticIntegrationService staticService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private ProfileIntegrationService profileIntegrationService;

    @Autowired
    private UserInformationIntegrationService userInformationIntegrationService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private AccountStatusService accountStatusService;

    @Autowired
    private ViewClientApplicationDetailsService viewClientApplicationDetailsService;


    @Autowired
    private InvestorStatusServiceForTechnicalSupport investorStatusServiceForTechnicalSupport;

    @Autowired
    private TrackingDtoService trackingDtoService;


    @Autowired
    private CisKeyClientApplicationRepository cisKeyClientApplicationRepository;

    @Autowired
    private CustomerCredentialManagementIntegrationServiceV6 customerCredentialManagementIntegrationServiceV6;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private WrapAccountDetailDtoService wrapAccountDetailDtoService;

    @Autowired
    private ProductIntegrationService productService;

    private static List<IntermediariesModel> sortIntermediariesList(List<IntermediariesModel> intermediariesList) {
        if (intermediariesList != null) {
            Collections.sort(
                    intermediariesList, new Comparator<IntermediariesModel>() {
                        @Override
                        public int compare(IntermediariesModel intermediaryOne, IntermediariesModel intermediaryTwo) {
                            String lastNameOne = intermediaryOne.getLastName();
                            String lastNameTwo = intermediaryTwo.getLastName();
                            int compareValue = 0;
                            //check if last name is not null before comparison
                            if (lastNameOne != null && lastNameTwo != null) {
                                compareValue = lastNameOne.toUpperCase().compareTo(lastNameTwo.toUpperCase());
                                //if last name is same, then sort by first name
                                if (compareValue == 0) {
                                    //check if first name is not null before comparison
                                    if (intermediaryOne.getFirstName() != null && intermediaryTwo.getFirstName() != null) {
                                        compareValue = intermediaryOne.getFirstName().toUpperCase().compareTo(intermediaryTwo.getFirstName().toUpperCase());
                                    }
                                }
                            }
                            return compareValue;
                        }
                    });
        }

        return intermediariesList;
    }

    private static List<ClientModel> sortClientModelList(List<ClientModel> clientsList) {
        if (clientsList != null) {
            Collections.sort(
                    clientsList, new Comparator<ClientModel>() {

                        @Override
                        public int compare(ClientModel clientModelOne, ClientModel clientModelTwo) {
                            String lastNameOne = clientModelOne.getLastName();
                            String lastNameTwo = clientModelTwo.getLastName();
                            int compareValue = 0;
                            //check if last name is not null before comparison
                            if (lastNameOne != null && lastNameTwo != null) {
                                compareValue = lastNameOne.toUpperCase().compareTo(lastNameTwo.toUpperCase());
                                //if last name is same, then sort by first name
                                if (compareValue == 0) {
                                    //check if first name is not null before comparison
                                    if (clientModelOne.getFirstName() != null && clientModelTwo.getFirstName() != null) {
                                        compareValue = clientModelOne.getFirstName().toUpperCase().compareTo(clientModelTwo.getFirstName().toUpperCase());
                                    }
                                }
                            }

                            return compareValue;

                        }

                    });
        }

        return clientsList;
    }

    private static List<WrapAccountModel> sortWrapAccountModelList(List<WrapAccountModel> accountsList) {
        if (accountsList != null) {
            Collections.sort(
                    accountsList, new Comparator<WrapAccountModel>() {

                        @Override
                        public int compare(WrapAccountModel wrapAccountModelOne, WrapAccountModel wrapAccountModelTwo) {
                            String accountNameOne = wrapAccountModelOne.getAccountName();
                            String accountNameTwo = wrapAccountModelTwo.getAccountName();
                            int compareValue = 0;
                            //check if account name is not null before comparison
                            if (accountNameOne != null && accountNameTwo != null) {
                                compareValue = accountNameOne.toUpperCase().compareTo(accountNameTwo.toUpperCase());
                                //if account name is same, then sort by account id
                                if (compareValue == 0 && wrapAccountModelOne.getAccountId() != null && wrapAccountModelTwo.getAccountId() != null) {
                                    //check account id is not null before comparison
                                    compareValue = wrapAccountModelOne.getAccountId().compareTo(wrapAccountModelTwo.getAccountId());
                                }
                            }

                            return compareValue;

                        }

                    });
        }

        return accountsList;
    }

    @Override
    public ServiceOpsModel getUsers(String searchCriteria, String roleType) {
        logger.info("Searching for criteria {} ", searchCriteria);

        Code natPersonCode = staticService.loadCodeByName(CodeCategory.PERSON_TYPE, Constants.NATURAL_PERSON, new ServiceErrorsImpl());

        //TODO replace the blank string with roleType
        searchCriteria = StringUtil.convertToLikeSearch(searchCriteria);
        UserSearchRequest userSearchRequest = new UserSearchRequestModel();

        userSearchRequest.setSearchToken(searchCriteria);
        userSearchRequest.setRoleType(roleType);
        userSearchRequest.setCodeId(natPersonCode.getCodeId());

        PersonSearchRequest personSearchRequest = new PersonSearchRequestImpl(searchCriteria, roleType, natPersonCode.getCodeId());
        List<PersonResponse> personSearchResult = personSearchIntegrationService.searchUser(personSearchRequest, new ServiceErrorsImpl());
        List<PersonInterface> persons = null;

        if (personSearchResult != null && personSearchResult.size() > 0) {
            persons = UserDetailsConverter.toPersonInterfaceList(personSearchResult);
        }

        ServiceOpsModel serviceOpsModel = null;

        if (persons != null && !persons.isEmpty()) {
            Iterator<PersonInterface> itr = persons.iterator();
            serviceOpsModel = new ServiceOpsModel();
            List<IntermediariesModel> intermediariesList = new ArrayList<>();
            List<ClientModel> clientsList = new ArrayList<>();
            ClientModel clientModel;
            IntermediariesModel intermediariesModel;
            while (itr.hasNext()) {
                PersonInterface person = itr.next();
                if (person instanceof Investor) {
                    Investor investor = (Investor) person;
                    if (!isIDVAssociate(investor)) {
                        clientModel = ServiceOpsConverter.toClientModel(investor);
                        clientsList.add(clientModel);
                    }
                } else if ((person instanceof Intermediaries)) {
                    Intermediaries intermediaries = (Intermediaries) person;
                    intermediariesModel = ServiceOpsConverter.toIntermediariesModel(intermediaries);
                    intermediariesList.add(intermediariesModel);
                }
            }
            serviceOpsModel.setClients(clientsList);
            serviceOpsModel.setIntermediaries(intermediariesList);
        }

        return serviceOpsModel;
    }

    private boolean isIDVAssociate(Investor investor) {
        if (investor.isBeneficiary() || investor.isMember()) {
            return true;
        }
        return false;
    }

    private PersonResponse getAccountantCompanyName(String searchCriteria) {
        Code natPersonCode = staticService.loadCodeByName(CodeCategory.PERSON_TYPE, Constants.NATURAL_PERSON, new ServiceErrorsImpl());

        //TODO replace the blank string with roleType
        //searchCriteria = Format.convertToLikeSearch(searchCriteria);
        UserSearchRequest userSearchRequest = new UserSearchRequestModel();

        userSearchRequest.setSearchToken(searchCriteria);
        userSearchRequest.setRoleType("");
        userSearchRequest.setCodeId(natPersonCode.getCodeId());

        PersonSearchRequest personSearchRequest = new PersonSearchRequestImpl(searchCriteria, "", natPersonCode.getCodeId());
        List<PersonResponse> personSearchResult = personSearchIntegrationService.searchUser(personSearchRequest, new ServiceErrorsImpl());
        PersonResponse personResponse = null;
        if (!CollectionUtils.isEmpty(personSearchResult)) {
            //persons = UserDetailsConverter.toPersonInterfaceList(personSearchResult);
            personResponse = personSearchResult.get(0);
        }
        return personResponse;
    }

    private void setAccountantCompanyName(PersonResponse personResponse, ServiceOpsModel serviceOpsModel) {
        if (personResponse != null && !CollectionUtils.isEmpty(personResponse.getProfileUserRoles())) {
            for (ProfileUserRole profile : personResponse.getProfileUserRoles()) {
                if (("Accountant").equalsIgnoreCase(profile.getUserRole().name()) || ("ACCOUNTANT_SUPPORT_STAFF").equalsIgnoreCase(profile.getUserRole().name())) {
                    serviceOpsModel.setCompanyName(profile.getCompanyName());
                }
            }
        }
    }

    private void setJobProfile(ServiceOpsModel serviceOpsModel) {
        boolean terminated = true;
        if (!CollectionUtils.isEmpty(serviceOpsModel.getJobProfiles())) {
            for (JobProfile jobProfile : serviceOpsModel.getJobProfiles()) {
                if (jobProfile.getJobRole().name().equalsIgnoreCase("INVESTOR")) {
                    terminated = false; //Clients are not terminated
                    populateInvestorDetails(serviceOpsModel);
                } else {
                    this.populateDealerGroupForIntermediary(jobProfile, serviceOpsModel);
                    DateTime closeDate = populateRole(serviceOpsModel, jobProfile);
                    if (terminated && closeDate==null){ //verify if all roles has been terminated, then set serviceOpsModel object terminated field to true
                        terminated = false;
                    }
                    serviceOpsModel.setFilter("intermediary");
                    if (("Accountant").equalsIgnoreCase(jobProfile.getJobRole().name()) || ("ACCOUNTANT_SUPPORT_STAFF").equalsIgnoreCase(jobProfile.getJobRole().name())) {
                        PersonResponse personResponse = getAccountantCompanyName(serviceOpsModel.getGcmId());
                        setAccountantCompanyName(personResponse, serviceOpsModel);
                    }
                }
            }
            serviceOpsModel.setTerminatedFlag(terminated);
        }
    }

    //
    // Retrieve details for an individual person
    //
    @Override
    public ServiceOpsModel getUserDetail(String clientId, boolean isClientDetailPage, ServiceErrors serviceErrors) throws Exception {
        ClientKey clientKey = ClientKey.valueOf(clientId);
        IndividualDetailImpl client = (IndividualDetailImpl) clientIntegrationService.loadClientDetails(clientKey, serviceErrors);

        if (client == null) {
            logger.error("Failed to load user {} from avaloq", clientKey.getId());
            return new ServiceOpsModel();
        }
        // Convert Client model back into the service ops Model
        ServiceOpsModel serviceOpsModel = ServiceOpsConverter.toServiceOpsModel(client);
        List<Roles> roles= credentialService.getCredentialGroups(client.getCustomerId());
        logger.info("Credential Roles Recieved from EAM: {}",roles);
        if(roles!= null && roles.contains(Roles.ROLE_WPL))
            serviceOpsModel.setWestpacLive(true);
        logger.info("value of the westpac live property: {}",serviceOpsModel.isWestpacLive());
        if(roles!=null && roles.contains(Roles.ROLE_WIB))
            serviceOpsModel.setWib(true);
        logger.info("value of the wib property: {}",serviceOpsModel.isWib());
        // Retrieve the username or registration code from EAM
        String username = getUsernameForIndividual(client.getCustomerId());
        serviceOpsModel.setUserName(StringUtils.isEmpty(username) ? "" : username);

        if (StringUtils.isNotBlank(serviceOpsModel.getGcmId())) {
            BankingCustomerIdentifier identifier = new BrokerUserImpl(UserKey.valueOf(serviceOpsModel.getGcmId()));
            List<JobProfile> jobProfiles = profileIntegrationService.loadAvailableJobProfilesForUser(identifier, new ServiceErrorsImpl());
            serviceOpsModel.setJobProfiles(jobProfiles);

            List<WrapAccountModel> wrapAccountsForClient = findWrapAccountDetailsByGcm(serviceOpsModel.getGcmId());
            serviceOpsModel.setMigratedCustomer(isMigratedCustomer(wrapAccountsForClient));

        }
        //initialising the role
        serviceOpsModel.setRole("");
        setJobProfile(serviceOpsModel);

        UserAccountStatus userAccountStatus = populateUserAccountDetails(serviceOpsModel);

        if (isClientDetailPage) {
            populateClientDetailsPage(client, serviceOpsModel, userAccountStatus);
        }
        String userName = Constants.EMPTY_STRING;
        try {
            userName = credentialService.getUserName(serviceOpsModel.getGcmId(), serviceErrors);
        } catch (RuntimeException rex) {
            logger.error("GroupEsb retreiveChannelAccessCredential service call returned empty response, User is not Registered", rex);
        }
        serviceOpsModel.setUserName(userName);
        logger.info("clientId:{} gcm id: ", clientId, serviceOpsModel.getGcmId());

        return serviceOpsModel;
    }

    private boolean isMigratedCustomer(List<WrapAccountModel> wrapAccountsForClient) {
        WrapAccountModel wrapAccountModel= Lambda.selectFirst(wrapAccountsForClient, new LambdaMatcher<WrapAccountModel>() {
            @Override
            protected boolean matchesSafely(WrapAccountModel wrapAccountModel) {
                return wrapAccountModel.getMigrationKey() != null;
            }
        });

        return null != wrapAccountModel;
    }

    private void populateClientDetailsPage(IndividualDetailImpl client, ServiceOpsModel serviceOpsModel, UserAccountStatus userAccountStatus) {
        Map<Action, String> actionDropdown = new LinkedHashMap<>();
        for (Action action : getActionListForServiceOpsRoles(userAccountStatus, serviceOpsModel)) {
            actionDropdown.put(action, action.getName());
        }
        if (isServiceOpsSuperRole()) {
            //retrieve PPID from SVC311 version5 (only for Adviser)
            for (JobProfile jobProfile : serviceOpsModel.getJobProfiles()) {
                if (jobProfile.getJobRole().equals(JobRole.ADVISER)) {
                    String ppId = credentialService.getPPID(client.getCustomerId());
                    serviceOpsModel.setPpId(ppId);
                    actionDropdown.put(Action.UPDATE_PPID, "Update PPID");
                    break;
                }
            }

            if(CollectionUtils.isEmpty(serviceOpsModel.getMobilePhones()) || CollectionUtils.isEmpty(serviceOpsModel.getEmail())) {
                serviceOpsModel.setMandatoryDetailMissing(true);
                serviceOpsModel.setInformationMessage(cmsService.getContent("uim0140"));
            }

        }



        serviceOpsModel.setActionValues(actionDropdown);
    }

    private void populateInvestorDetails(ServiceOpsModel serviceOpsModel) {
        serviceOpsModel.setFilter("client");
        serviceOpsModel.setRole(Attribute.INVESTOR);
        this.populateDealerGroupForClients(serviceOpsModel);
        //No Need to show Onboarding Status and Reason for Failure field for Registered Users
        if (!serviceOpsModel.isAvaloqStatusReg()) {
            OnboardingStatusInterface onboardingStatus = investorStatusServiceForTechnicalSupport.getOnboardingStatusAndFailureMsg(serviceOpsModel.getGcmId());
            if (onboardingStatus != null) {
                serviceOpsModel.setOnboardingStatus(onboardingStatus.getStatus());
                serviceOpsModel.setOnboardingFailureReason(onboardingStatus.getFailureMsg());
            }
        }
    }

    private UserAccountStatus populateUserAccountDetails(ServiceOpsModel serviceOpsModel) {
        UserAccountStatusModel userAccountStatusModel = userAccountStatusService.lookupStatus(serviceOpsModel.getGcmId(), serviceOpsModel.getSafiDeviceId(),serviceOpsModel.isMigratedCustomer());
        serviceOpsModel.setAction(userAccountStatusModel.getUserAccountStatus().getValue());
        serviceOpsModel.setMessage(userAccountStatusModel.getUserAccountStatus().getValue());
        serviceOpsModel.setLoginStatus(userAccountStatusModel.getUserAccountStatus());
        return userAccountStatusModel.getUserAccountStatus();
    }

    /**
     * checkTerminated - method checks closedate time with current date time to determine if the profile is active or not
     *                  returns close date if terminated and null if its still active
     * @param profile
     * @return
     */
    private DateTime checkTerminated(JobProfile profile){
        boolean isTerminated = false;
        DateTime currDateTime = new DateTime();
        DateTime closeDateTime = null;
        if (profile.getCloseDate()!=null) {
            closeDateTime = profile.getCloseDate();
            isTerminated = (closeDateTime.compareTo(currDateTime)) > 0 ? false : true; //check if future date
        }
        return isTerminated ? closeDateTime : null;
    }

    private DateTime populateRole(ServiceOpsModel serviceOpsModel, JobProfile jobProfile) {
        String role = WordUtils.capitalize(jobProfile.getJobRole().name().toLowerCase().replace('_', ' '));
        DateTime closeDateTime = checkTerminated(jobProfile);
        String terminatedDate = "";
        if (closeDateTime!=null) {
            terminatedDate = " - Terminated (" + ApiFormatter.asShortDate(closeDateTime) + ")";
        }
        if (serviceOpsModel.getRole().isEmpty()) {
            serviceOpsModel.setRole(role + terminatedDate);
        } else {
            String roleList = serviceOpsModel.getRole() + ", " + role + terminatedDate ;
            serviceOpsModel.setRole(roleList);
        }
        return closeDateTime;
    }

    private void populateDealerGroupForIntermediary(JobProfile jobProfile, ServiceOpsModel serviceOpsModel) {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        /*BrokerUser brokerUser = brokerIntegrationService.getBrokerUser(clientKey, serviceErrors);*/

        try {
            /*JobProfileImpl userProfile = new JobProfileImpl();
            if (null != brokerUser)
			{
				userProfile.setJob(brokerUser.getJob());*/
            List<Broker> brokers = brokerIntegrationService.getBrokersForJob(jobProfile, serviceErrors);

            if (brokers != null) {
                Broker broker = brokers.iterator().next();
                if (null != broker && null != broker.getDealerKey()) {
                    Broker dgBroker = brokerIntegrationService.getBroker(broker.getDealerKey(), serviceErrors);
                    serviceOpsModel.setDealerGroupList(new HashSet<String>(Arrays.asList(dgBroker.getPositionName())));
                }

                if (null != broker && broker.getPracticeKey() != null) {
                    Broker practice = brokerIntegrationService.getBroker(broker.getPracticeKey(), serviceErrors);
                    serviceOpsModel.setPracticeName(practice.getPositionName());
                }
            }
            //}
        } catch (Exception e) {
            logger.error("Unable to determine the dealergroup for broker user", e);
        }
    }

    /**
     * Retrieve the corresponding username for the gcmid from EAM
     *
     * @param gcmId
     * @return userName for Individual
     */
    private String getUsernameForIndividual(String gcmId) {
        String username = "";

        try {
            username = credentialService.getUserName(gcmId, new ServiceErrorsImpl());
        } catch (Exception rex) {
            logger.error(
                    "GroupEsb retreiveChannelAccessCredential service call returned empty response for {}, User is not Registered", gcmId);
        }
        return username;
    }

    @Override
    public ServiceOpsModel getSortedUsers(String searchCriteria) {
        ServiceOpsModel serviceOpsModel = getUsers(searchCriteria, "");

        if (serviceOpsModel != null) {
            List<ClientModel> clientModelList = serviceOpsModel.getClients();
            List<IntermediariesModel> intermediariesModelList = serviceOpsModel.getIntermediaries();
            serviceOpsModel.setClients(sortClientModelList(clientModelList));
            serviceOpsModel.setIntermediaries(sortIntermediariesList(intermediariesModelList));
        }
        return serviceOpsModel;
    }

    @Override
    public ServiceOpsModel getSortedAccounts(String searchCriteriaParam, ServiceErrors serviceErrors) {
        List<WrapAccountModel> wrapAccountModels = new ArrayList<>();
        String searchCriteria = StringUtil.convertToLikeSearch(searchCriteriaParam);
        WrapAccountModel wrapAccountModel = null;
        IndividualDetailImpl client = null;
        ArrayList<String> ownerList = null;

        List<WrapAccount> accounts = accountService.searchWrapAccounts(searchCriteria, serviceErrors);
        if (accounts != null) {
            for (WrapAccount account : accounts) {
                if (AccountStatus.ACTIVE.equals(account.getAccountStatus())) {
                    wrapAccountModel = ServiceOpsConverter.convertToSimpleWrapAccountModel(account);
                    Person brokerUser = null;
                    if (account.getAdviserPersonId() != null) {
                        brokerUser = brokerIntegrationService.getPersonDetailsOfBrokerUser(ClientKey.valueOf(account.getAdviserPersonId().getId()), serviceErrors);
                    }
                    if (brokerUser != null) {
                        wrapAccountModel.setAdviserName(brokerUser.getFirstName() + " " + brokerUser.getLastName());
                    }
                    Product product = productService.getProductDetail(account.getProductKey(), serviceErrors);
                    if (product != null) {
                        wrapAccountModel.setProduct(product.getProductName());
                    }
                    ownerList = new ArrayList<>();
                    for (ClientKey clientKey : account.getApprovers()) {
                        client = (IndividualDetailImpl) clientIntegrationService.loadClientDetails(clientKey, serviceErrors);
                        if (client != null) {
                            ownerList.add(client.getFirstName() + " " + client.getLastName());
                        }
                    }
                    wrapAccountModel.setOwners(ownerList);
                    wrapAccountModels.add(wrapAccountModel);
                }
            }
        }
        ServiceOpsModel serviceOpsModel = new ServiceOpsModel();
        serviceOpsModel.setAccounts(sortWrapAccountModelList(wrapAccountModels));

        return serviceOpsModel;
    }

    /**
     * Check for current log in service ops user functional role and return
     * true if it has $UR_SERVICE_UI otherwise false
     *
     * @return boolean
     */
    @Override
    public boolean isServiceOpsSuperRole() {
        final List<String> roles = userProfileService.getActiveProfile().getUserRoles();
        for (String roleName : roles) {
            logger.info("Evaluating role: {}", roleName);
            UserRole userRole = UserRole.forAvaloqRole(roleName);
            if (userRole.equals(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC) ||
                    userRole.equals(UserRole.SERVICEOPS_ADMINISTRATOR_DG_RESTRICTED)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for current log in service ops user functional role and return
     * true if it has $UR_IT_SUPP_BASIC otherwise false
     *
     * @return boolean
     */
    @Override
    public boolean isServiceOpsITSupportRole() {
        final List<String> roles = userProfileService.getActiveProfile().getUserRoles();
        for (String roleName : roles) {
            logger.info("Evaluating role: {}", roleName);
            UserRole userRole = UserRole.forAvaloqRole(roleName);
            if (userRole.equals(UserRole.SERVICEOPS_IT_SUPPORT_BASIC)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Check for current log in service ops user functional role and return
     * a permission model for left nav options
     *
     * @return boolean
     */
    @Override
    public LeftNavPermissionModel getLeftNavPermissions() {
        LeftNavPermissionModel leftNavPermissionModel = new LeftNavPermissionModel();
        leftNavPermissionModel.setDocLibrary(isServiceOpsSuperRole());
        leftNavPermissionModel.setGcmHome(isServiceOpsITSupportRole());
        return leftNavPermissionModel;
    }

    @Override
    public boolean isServiceOpsRestricted() {
        return getAvailableRoles().contains(UserRole.SERVICEOPS_ADMINISTRATOR_DG_RESTRICTED.getRole());
    }

    /**
     * Returns all the available roles based on the profiles
     */
    private List<String> getAvailableRoles() {
        List<JobProfile> jobProfileList = userProfileService.getAvailableProfiles();
        ServiceErrors errors = new ServiceErrorsImpl();
        List<String> roles = new ArrayList<>();
        for (JobProfile jobProfile : jobProfileList) {
            if (jobProfile != null) {
                logger.info("Checking job {} with profile_id {} for user roles", jobProfile.getProfileId(), jobProfile.getJobRole().toString());
                UserInformationImpl permission = (UserInformationImpl) userInformationIntegrationService.getAvailableRoles(jobProfile, errors);
                roles = permission.getUserRoles();
                logger.info("User roles for profile: {}", roles);
            } else {
                logger.info("Service Operator {} has no functional role configured.", userProfileService.getUserId());
            }
        }
        return roles;
    }

    /**
     * Will return the Combined List of actions(Union) as per different roles of service operator.
     *
     * @param userAccountStatus,serviceOpsModel
     * @return
     */
    private List<Action> getActionListForServiceOpsRoles(UserAccountStatus userAccountStatus, ServiceOpsModel serviceOpsModel) {
        Set<Action> finalActionSet = new LinkedHashSet<>();

        List<JobProfile> jobProfileList = userProfileService.getAvailableProfiles();
        ServiceErrors errors = new ServiceErrorsImpl();

        for (JobProfile jobProfile : jobProfileList) {

            if (jobProfile != null) {
                logger.info("Checking job {} with profile_id {} for user roles", jobProfile.getProfileId(), jobProfile.getJobRole().toString());

                try {
                    UserInformationImpl permission = (UserInformationImpl) userInformationIntegrationService.getAvailableRoles(jobProfile, errors);
                    List<String> roles = permission.getUserRoles();

                    logger.info("User roles for profile: {}", roles);

                    for (String rolename : roles) {
                        logger.info("Evaluating role: {}", rolename);

                        UserRole userRole = UserRole.forAvaloqRole(rolename);
                        if (userRole.equals(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC) || userRole.equals(UserRole.SERVICEOPS_DECEASED_ESTATE_BASIC) || userRole.equals(UserRole.SERVICEOPS_EMULATOR_BASIC)) {
                            try {
                                //Will Enabled create Account Button for Unregistered users

                                if (!serviceOpsModel.isAvaloqStatusReg() && UserAccountStatus.ACCOUNT_CREATION_INCOMPLETE.equals(userAccountStatus)) {
                                    if (userRole.equals(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC)) {
                                        if (Attribute.INVESTOR.equals(serviceOpsModel.getRole())) {
                                            serviceOpsModel.setCanCreateAccount("true");
                                        } else {
                                            setCreateAccountStateForIntemediatry(serviceOpsModel);
                                        }
                                        return new ArrayList<>();
                                    } else {
                                        serviceOpsModel.setCanCreateAccount("false");
                                    }
                                } else {
                                    List<Action> actionList = getActionListBasedOnStatusRole(userRole, userAccountStatus.getGroup(),serviceOpsModel.isMigratedCustomer());
                                    if (actionList != null) {
                                        finalActionSet.addAll(actionList);
                                    }
                                }
                            } catch (Exception e) {
                                logger.error("There is no user role found for role id {}", userRole);
                            }
                        }

                        logger.info("Finished Evaluating role: {}", rolename);
                    }

                } catch (Exception e) {
                    logger.warn("Unable to retrieve avaloq user roles for user", e);

                }
            } else {
                logger.info("Service Operator {} has no jobs configured. Unable to set dropdown actions", userProfileService.getUserId());
            }

        }
        // Remove Action RESEND_REGISTRATION_EMAIL from actionDropdown for registered user
        if (serviceOpsModel.isAvaloqStatusReg() && !finalActionSet.isEmpty()) {
            finalActionSet.remove(Action.RESEND_REGISTRATION_EMAIL);
            finalActionSet.remove(Action.RESEND_EXISTING_REGISTRATION_CODE);
        }
        return new ArrayList<>(finalActionSet);
    }


    private void setCreateAccountStateForIntemediatry(ServiceOpsModel serviceOpsModel) {
        //Check for OE hierarchy
        Person brokerUser = brokerIntegrationService.getPersonDetailsOfBrokerUser(ClientKey.valueOf(serviceOpsModel.getClientId().plainText()), new ServiceErrorsImpl());
        if (brokerUser == null) {
            serviceOpsModel.setCanCreateAccount("false");
            serviceOpsModel.setInformationMessage(cmsService.getContent("Err.IP-0379"));
        } else {
            serviceOpsModel.setCanCreateAccount("true");
        }

    }


    /**
     * Will return the list of actions common as per Status of user and Role of Service operator.
     *
     * @param role
     * @param status
     * @return
     */

    private List<Action> getActionListBasedOnStatusRole(UserRole role, UserAccountStatus.Group status, boolean isMigratedCustomer) {
        List<Action> actionList = null;
        List<Action> roleActionList = RoleStatusActionMapping.getActionListForRole(role);
        if ((roleActionList != null && (!roleActionList.isEmpty()))) {
            actionList = new ArrayList<>();
            actionList.addAll(roleActionList);
            actionList.retainAll(RoleStatusActionMapping.getActionListForStatus(status));
            if(isMigratedCustomer){
                actionList.add(Action.PROVISION_MFA_DEVICE);
            }
        }
        return actionList;
    }

    private void populateDealerGroupForClients(ServiceOpsModel serviceOpsModel) {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        final String gcmId = serviceOpsModel.getGcmId();
        List<Broker> adviserList = brokerHelperService.getAdviserListForInvestor(getBankingCustomerIdentifier(gcmId), serviceErrors);
        Set<String> dealerGroupList = new HashSet<>();
        for (Broker adviser : adviserList) {
            if (adviser != null && adviser.getPracticeKey() != null) {
                Broker practise = brokerIntegrationService.getBroker(adviser.getPracticeKey(), serviceErrors);
                serviceOpsModel.setPracticeName(practise.getPositionName());
            }

            if (adviser != null && adviser.getDealerKey() != null) {
                Broker dealerGroup = brokerIntegrationService.getBroker(adviser.getDealerKey(), serviceErrors);
                if (null != dealerGroup && StringUtils.isNotBlank(dealerGroup.getPositionName())) {
                    dealerGroupList.add(dealerGroup.getPositionName());
                }
            }
        }
        serviceOpsModel.setDealerGroupList(dealerGroupList);
    }

    private BankingCustomerIdentifier getBankingCustomerIdentifier(final String gcmId) {
        BankingCustomerIdentifier bankingCustomerIdentifier = new BankingCustomerIdentifier() {
            @Override
            public String getBankReferenceId() {
                return gcmId;
            }

            @Override
            public UserKey getBankReferenceKey() {
                return UserKey.valueOf(gcmId);
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }
        };
        return bankingCustomerIdentifier;
    }

    @Override
    public ServiceOpsClientApplicationDto getFailedApplicationDetails(String applicationId) {
        Long clientAppId = getClientApplicationId(applicationId);
        logger.info("Client application id: {} for reference id : {}", clientAppId, applicationId);
        try {
            ClientApplication clientApplication = clientApplicationRepository.findByClientApplicationId(clientAppId);
            if (clientApplication.getClientApplicationForm().isDirectAccount()) {
                logger.debug("Returning null for failed application");
                return null;
            }
            logger.info("ClientApplication :{}", clientApplication);
            logger.info("OnboardingApplication : {}", clientApplication.getOnboardingApplication());
            ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = getFailedApplicationDto(clientApplication);
            if (serviceOpsClientApplicationDto != null)
                return serviceOpsClientApplicationDto;

        } catch (NoResultException ex) {
            logger.error("Returning null for NoResultException");
            return null;
        }

        logger.debug("Returning null for failed application");
        return null;
    }

    private ServiceOpsClientApplicationDto getFailedApplicationDto(ClientApplication clientApplication) {
        Map<String, ApplicationDocument> applicationDocumentMap = Collections.emptyMap();
        OnboardingApplicationStatus status = accountStatusService.getApplicationStatus(clientApplication, applicationDocumentMap);
        logger.info("Application status: {}", status);
        if (OnboardingApplicationStatus.failed.equals(status)) {
            ServiceErrors serviceErrors = new ServiceErrorsImpl();
            ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = serviceOpsClientApplicationDtoConverterService.convertToDto(
                    clientApplication, serviceErrors);
            if (serviceErrors.hasErrors()) {
                logger.error("ServiceErrors: {}", serviceErrors);
                throw new ServiceException(ApiVersion.CURRENT_VERSION, serviceErrors);
            } else {
                return serviceOpsClientApplicationDto;
            }
        }
        return null;
    }

    @Override
    public List<ServiceOpsClientApplicationDto> getFailedDirectApplications(String cisKey) {
        try {
            List<ClientApplication> clientApplications = getFailedClientApplications(cisKey);
            if(CollectionUtils.isNotEmpty(clientApplications)){
                return Lambda.convert(clientApplications, new Converter<ClientApplication, ServiceOpsClientApplicationDto>() {
                    @Override
                    public ServiceOpsClientApplicationDto convert(ClientApplication application) {
                        logger.info("ClientApplication :{}", application);
                        logger.info("OnboardingApplication : {}", application.getOnboardingApplication());
                        ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = getFailedApplicationDto(application);
                        return serviceOpsClientApplicationDto;
                    }
                });
            } else {
                logger.info("No failed applications found for cisKey: ", cisKey);
                return null;
            }
        } catch (NoResultException ex) {
            logger.error("Returning null for NoResultException", ex);
            return null;
        }
    }

    private List<ClientApplication> getFailedClientApplications(String cisKey) {
        List<ClientApplication> clientApplicationsForCisKey = cisKeyClientApplicationRepository.findClientApplicationsForCisKey(cisKey);
        List<ClientApplication> filteredClientApplications = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(clientApplicationsForCisKey)) {
            filteredClientApplications = Lambda.filter(new LambdaMatcher<ClientApplication>() {
                @Override
                protected boolean matchesSafely(ClientApplication application) {
                    OnboardingApplicationStatus applicationStatus = accountStatusService.getApplicationStatus(application, new HashMap<String, ApplicationDocument>());
                    return applicationStatus != null && OnboardingApplicationStatus.failed.equals(applicationStatus);
                }
            }, clientApplicationsForCisKey);
        }
        return filteredClientApplications;
    }

    @Override
    public void moveFailedApplicationToDraft(String applicationId) {
        ClientApplication clientApplication = clientApplicationRepository.findByClientApplicationId(getClientApplicationId(applicationId));
        clientApplicationRepository.save(createDraftCopy(clientApplication));
        clientApplicationRepository.save(markDeleted(clientApplication));
    }

    private ClientApplication markDeleted(ClientApplication clientApplication) {
        clientApplication.markDeleted();
        return clientApplication;
    }

    private ClientApplication createDraftCopy(ClientApplication clientApplication) {
        ClientApplication newClientApplication = new ClientApplication();
        newClientApplication.setAdviserPositionId(clientApplication.getAdviserPositionId());
        newClientApplication.setFormData(FilterJsonProperty.filter(clientApplication.getFormData(), FormDataConstants.FIELD_CORRELATION_ID));
        newClientApplication.setLastModifiedId(SERVICE_OPS);
        newClientApplication.setLastModifiedAt(new DateTime());
        newClientApplication.setProductId(clientApplication.getProductId());
        return newClientApplication;
    }

    @Override
    public String downloadCsvOfAllUnapprovedApplications(Date fromDate, Date toDate, ServiceErrors serviceErrors) throws IOException {
        StringBuilder csvContent = new StringBuilder().append(createCsvHeader());
        List<TrackingDto> trackingDtos = trackingDtoService.searchForUnapprovedApplications(fromDate, toDate, serviceErrors);
        if (trackingDtos != null && !trackingDtos.isEmpty()) {
            for (TrackingDto ca : trackingDtos) {
                if(ca.getStatus() != OnboardingApplicationStatus.active ) {
                    csvContent.append(createCsvRecord(ca));
                }
            }
        }
        return csvContent.toString();
    }

    private String createCsvRecord(TrackingDto ca) {
        synchronized (ca) {
            String adviserDealerGroup = "";
            String adviserFirstName = "";
            String adviserLastName = "";
            String adviserEmailId = "";
            String adviserMobilePhone = "";
            String adviserBusinessPhone = "";
            String lastModifiedId = "";
            String lastModifiedFirstName = "";
            String lastModifiedLastName = "";
            String onboardingTrackingId = "";

            PersonInfo adviser = ca.getAdviser();
            PersonInfo lastModifiedBy = ca.getLastModifiedBy();
            if (adviser != null) {
                adviserDealerGroup = adviser.getDealerGroupName();
                adviserFirstName = adviser.getFirstName();
                adviserLastName = adviser.getLastName();
                adviserEmailId = adviser.getEmailId();
                adviserMobilePhone = adviser.getMobilePhone();
                adviserBusinessPhone = adviser.getBusinessPhone();
            }
            if (lastModifiedBy != null) {
                lastModifiedId = lastModifiedBy.getId();
                lastModifiedFirstName = lastModifiedBy.getFirstName();
                lastModifiedLastName = lastModifiedBy.getLastName();
            }
            if (ca.getOnboardingApplicationKey() != null) {
                onboardingTrackingId = ca.getOnboardingApplicationKey().getId().toString();
            }

            String accountStatus = OnboardingApplicationStatus.smsfcorporateinProgress.equals(ca.getStatus())
                ? CORP_TRUSTEE_DISPLAY_STATUS : ca.getStatus().toString();
            String approvalType = ApprovalTypeEnum.OFFLINE == ca.getApprovalType() ? OFFLINE_STR : "";

            return new StringBuilder()
                    .append(filterForCsv(adviserDealerGroup)).append(CSV_SEPARATOR)
                    .append(filterForCsv(adviserFirstName)).append(CSV_SEPARATOR)
                    .append(filterForCsv(adviserLastName)).append(CSV_SEPARATOR)
                    .append(filterForCsv(adviserEmailId)).append(CSV_SEPARATOR)
                    .append(filterForCsv(adviserMobilePhone)).append(CSV_SEPARATOR)
                    .append(filterForCsv(adviserBusinessPhone)).append(CSV_SEPARATOR)
                    .append(filterForCsv(ca.getAccountType())).append(CSV_SEPARATOR)
                    .append(filterForCsv(ca.getDisplayName())).append(CSV_SEPARATOR)
                    .append(filterForCsv(ca.getReferenceNumber())).append(CSV_SEPARATOR)
                    .append(filterForCsv(onboardingTrackingId)).append(CSV_SEPARATOR)
                    .append(filterForCsv(accountStatus)).append(CSV_SEPARATOR)
                    .append(filterForCsv(approvalType)).append(CSV_SEPARATOR)
                    .append(filterForCsv(ca.getAccountId())).append(CSV_SEPARATOR)
                    .append(filterForCsv(dateTimeFormatter.print(ca.getLastModified()))).append(CSV_SEPARATOR)
                    .append(filterForCsv(lastModifiedId)).append(CSV_SEPARATOR)
                    .append(filterForCsv(lastModifiedFirstName)).append(CSV_SEPARATOR)
                    .append(filterForCsv(lastModifiedLastName)).append("\n").toString();
        }
    }

    private String filterForCsv(String s) {
        return s == null ? "" : StringEscapeUtils.escapeCsv(s);
    }

    private String createCsvHeader() {
        return new StringBuilder()
                .append("Dealer Group Name").append(CSV_SEPARATOR)
                .append("Adviser First Name").append(CSV_SEPARATOR)
                .append("Adviser Last Name").append(CSV_SEPARATOR)
                .append("Adviser Email Id").append(CSV_SEPARATOR)
                .append("Adviser Mobile Phone").append(CSV_SEPARATOR)
                .append("Adviser Business Phone").append(CSV_SEPARATOR)
                .append("Account Type").append(CSV_SEPARATOR)
                .append("Account Name").append(CSV_SEPARATOR)
                .append("Reference ID").append(CSV_SEPARATOR)
                .append("Onboarding Tracking ID").append(CSV_SEPARATOR)
                .append("Status").append(CSV_SEPARATOR)
                .append("Approval Type").append(CSV_SEPARATOR)
                .append("Account ID").append(CSV_SEPARATOR)
                .append("Last Modified Date").append(CSV_SEPARATOR)
                .append("Last Modified Id").append(CSV_SEPARATOR)
                .append("Last Modified First Name").append(CSV_SEPARATOR)
                .append("Last Modified Last Name").append("\n").toString();
    }

    public ClientApplicationDetailsDto getClientApplicationDetails(String clientApplicationId) {
        Long id = getClientApplicationId(clientApplicationId);
        return viewClientApplicationDetailsService.viewClientApplicationById(id, new ServiceErrorsImpl());
    }

    @Override
    public ClientApplicationDetailsDto getClientApplicationDetailsByAccountNumber(String accountNumber) {
        return viewClientApplicationDetailsService.viewClientApplicationByAccountNumber(accountNumber, new ServiceErrorsImpl());
    }

    @Override
    public List<ServiceOpsClientApplicationDto> getApprovedClientApplicationsByCISKey(String cisKey) {
        final ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        List<ClientApplication> clientApplicationsForCisKey = cisKeyClientApplicationRepository.findClientApplicationsForCisKey(cisKey);
        final List<TrackingDto> trackingDtos = trackingDtoService.getTrackingDtos(clientApplicationsForCisKey, true, true, serviceErrors);

        List<TrackingDto> filteredTrackingDtos = Lambda.filter(new LambdaMatcher<TrackingDto>() {
            @Override
            protected boolean matchesSafely(TrackingDto trackingDto) {
                return OnboardingApplicationStatus.awaitingApproval.equals(trackingDto.getStatus()) ||  OnboardingApplicationStatus.active.equals(trackingDto.getStatus());
            }
        }, trackingDtos);

        if (CollectionUtils.isNotEmpty(filteredTrackingDtos)) {
                List<ServiceOpsClientApplicationDto> serviceOpsClientApplicationDtos = Lambda.convert(filteredTrackingDtos, new Converter<TrackingDto, ServiceOpsClientApplicationDto>() {
                    @Override
                    public ServiceOpsClientApplicationDto convert(TrackingDto trackingDto) {
                            final ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = serviceOpsClientApplicationDtoConverterService
                                    .convertToDto(trackingDto, ServiceOpsClientApplicationStatus.APPROVED);
                            return serviceOpsClientApplicationDto;
                    }
                });
                return serviceOpsClientApplicationDtos;
            }
        return null;
    }

    private Long getClientApplicationId(String applicationId) {
        Long id = 0l;
        if (applicationId.startsWith("R")) {
            applicationId = applicationId.substring(1, applicationId.length());
        }
        while (applicationId.startsWith("0")) {
            applicationId = applicationId.substring(1, applicationId.length());
        }
        id = Long.parseLong(applicationId);
        return id;
    }

    @Override
    public int countOfApplicationIdsForUnapprovedApplications(
            Date fromDate, Date toDate) {
        // TODO Auto-generated method stub
        return trackingDtoService.countUnapprovedApplications(fromDate, toDate);
    }

    @Override
    public List<WrapAccountModel> findWrapAccountDetail(String bpNumber) {
        List<WrapAccountModel> wrapAccountModels = new ArrayList<>();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf(bpNumber);
        WrapAccountDetail account = accountService.loadWrapAccountDetailByAccountNo(accountKey, serviceErrors);
        if (null != account) {
            wrapAccountModels.add(ServiceOpsConverter.convertToWrapAccountModel(account));
        }
        if (serviceErrors.hasErrors()) {
            logger.error("ServiceErrors: {}", serviceErrors);
        }
        return wrapAccountModels;
    }

    public List<WrapAccountModel> findWrapAccountDetailsByGcm(final String gcmId) {
        List<WrapAccountModel> wrapAccountModels = new ArrayList<>();
        BankingCustomerIdentifier bankingCustomerIdentifier = new BankingCustomerIdentifier() {
            @Override
            public String getBankReferenceId() {
                return gcmId;
            }

            @Override
            public UserKey getBankReferenceKey() {
                return UserKey.valueOf(gcmId);
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }
        };
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        WrapAccountDetailResponse wrapAccountDetailResponse = accountService.loadWrapAccountDetailByGcm(bankingCustomerIdentifier, serviceErrors);
        List<WrapAccountDetail> wrapAccounts = wrapAccountDetailResponse.getWrapAccountDetails();
        if (null != wrapAccounts) {
            for (WrapAccountDetail wrapAccountDetail : wrapAccounts) {
                wrapAccountModels.add(ServiceOpsConverter.convertToWrapAccountModel(wrapAccountDetail));
            }
        }
        if (serviceErrors.hasErrors()) {
            logger.error("ServiceErrors: {}", serviceErrors);
        }
        return wrapAccountModels;
    }

    @Override
    public boolean updatePPID(String ppid, String clientID, ServiceErrors serviceErrors) {
        ClientKey clientKey = ClientKey.valueOf(clientID);

        try {
            //updating in Avaloq
            clientIntegrationService.updatePPID(clientKey, ppid, serviceErrors);

            if (serviceErrors.hasErrors()) {
                return false;
            }

            //updating EAM
            Client client = clientIntegrationService.loadClientDetails(clientKey, serviceErrors);
            logger.info("Getting CredentialID From Eam For Client {} ", client.getGcmId());
            String credentialId = credentialService.getCredentialId(client.getGcmId(), serviceErrors);
            return customerCredentialManagementIntegrationServiceV6.updatePPID(ppid, credentialId, serviceErrors);

        } catch (Exception e) {
            logger.error("There was Some Error in Updating ppid For Client ", e);
            return false;
        }
    }

    @Override
    public boolean updatePreference(String preference, String clientID, ServiceErrors serviceErrors) {
        WrapAccountDetailDto wrapAccountDetailDto = new WrapAccountDetailDto();
        wrapAccountDetailDto.setStatementPref(staticService.loadCodeByName(CodeCategory.STMT_CORRESPONDENCE_PREF , preference.toUpperCase() ,serviceErrors).getCodeId());
        wrapAccountDetailDto.setKey(new com.bt.nextgen.api.account.v3.model.AccountKey(clientID));
        wrapAccountDetailDto.setModificationSeq(getAccountDetail(clientID , serviceErrors).getWrapAccountDetail().getModificationSeq());
        wrapAccountDetailDtoService.update(wrapAccountDetailDto , serviceErrors);
        if(!serviceErrors.hasErrors())
        return true;

        return false;
    }

    private static final class FilterJsonProperty {
        private FilterJsonProperty() {
        }

        static String filter(String json, String propertyToFilter) {
            try {
                if (json == null) {
                    return null;
                }
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> jsonMap = mapper.readValue(
                        json, new TypeReference<Map<String, Object>>() {
                        });
                filterMap(jsonMap, propertyToFilter);
                return mapper.writeValueAsString(jsonMap);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        private static void filterMap(Map<String, Object> jsonMap, String propertyToFilter) {
            for (Object o : jsonMap.values()) {
                if (o instanceof Map) {
                    filterMap((Map) o, propertyToFilter);
                }
                if (o instanceof List) {
                    filterList((List) o, propertyToFilter);
                }
            }
            jsonMap.remove(propertyToFilter);
        }

        private static void filterList(List<Object> jsonList, String propertyToFilter) {
            for (Object o : jsonList) {
                if (o instanceof Map) {
                    filterMap((Map) o, propertyToFilter);
                }
                if (o instanceof List) {
                    filterList((List) o, propertyToFilter);
                }
            }
        }
    }

    /**
     * Retrieve details of a BP for displaying in accountDetail page
     * @param accountId
     * @return
     * @throws Exception
     */
    @Override
    public ServiceOpsModel getAccountDetail(String accountId, ServiceErrors serviceErrors) {
        final List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS, accountId,
                ApiSearchCriteria.OperationType.STRING));

        WrapAccountDetailDto wrapAccountDetail = wrapAccountDetailDtoService.search(criteria, serviceErrors);
        ServiceOpsModel serviceOpsModel = new ServiceOpsModel();
        if (wrapAccountDetail.getBsb() != null) {
            wrapAccountDetail.setBsb(ApiFormatter.formatBsb(wrapAccountDetail.getBsb()));
        }
        if (wrapAccountDetail.getRegisteredSinceDate() != null) {
            serviceOpsModel.setRegisteredSince(ApiFormatter.asShortDate(wrapAccountDetail.getRegisteredSinceDate()));
        }

        if(null != wrapAccountDetail.getStatementPref()){
            serviceOpsModel.setResolvedPref(staticService.loadCode(CodeCategory.STMT_CORRESPONDENCE_PREF ,wrapAccountDetail.getStatementPref(),serviceErrors).getName());
        }

        if(null != wrapAccountDetail.getCmaStatementPref()){
            serviceOpsModel.setCmaStatementPref(staticService.loadCode(CodeCategory.STMT_CORRESPONDENCE_PREF ,wrapAccountDetail.getCmaStatementPref(),serviceErrors).getName());
        }

        serviceOpsModel.setWrapAccountDetail(wrapAccountDetail);
        serviceOpsModel = ServiceOpsConverter.convertWrapAccountDtoToLinkedClientModel(serviceOpsModel);
        return serviceOpsModel;

    }
}