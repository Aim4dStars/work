package com.bt.nextgen.serviceops.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.CredentialService;
import com.btfin.panorama.core.util.StringUtil;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.search.PersonSearchRequestImpl;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.search.PersonResponse;
import com.bt.nextgen.service.integration.search.PersonSearchIntegrationService;
import com.bt.nextgen.service.integration.search.PersonSearchRequest;
import com.bt.nextgen.service.onboarding.CreateAccountRequest;
import com.bt.nextgen.service.onboarding.CreateAccountRequestModel;
import com.bt.nextgen.service.onboarding.CreateAccountResponse;
import com.bt.nextgen.service.onboarding.CreateInvestorAccountRequestModel;
import com.bt.nextgen.service.onboarding.OnboardingIntegrationService;
import com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;

/**
 * UserAccountStatusServiceImpl is the implementation to get UserAccountStatus(ACCOUNT_CREATION_INCOMPLETE,UNREGISTERED, ACTIVE,BLOCKED)
 * from EAM. Only ACCOUNT_CREATION_INCOMPLETE is mapped to Error Code: 309,00008, everything else directly comes from 'LifeCycleStatus'
 * element. Refer to response file 'CredentialResponseFromEAM.xml' for more details.
 */

@Service
public class UserAccountStatusServiceImpl implements UserAccountStatusService {
    private static final Logger logger = LoggerFactory.getLogger(UserAccountStatusServiceImpl.class);

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private OnboardingIntegrationService btEsbService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    private PersonSearchIntegrationService personSearch;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private StaticIntegrationService staticService;

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Returns an UserAccountStatus that can be used to on board client to complete the respective operation.
     * <p/>
     * Currently the service rely on CredentialResponseFromEAM.xml response file End point not available now
     *
     *
     * @param userId unique id to identify the user
     * @param migratedCustomer
     * @return one of UserAccountStatus enum constant
     */
    @Override
    public UserAccountStatusModel lookupStatus(String userId, String safiDeviceId, boolean migratedCustomer) {
        UserAccountStatusModel userAccountStatusModel = credentialService.lookupStatus(userId, new ServiceErrorsImpl());
        logger.info("Status of user {} as from EAM: {}", userId, userAccountStatusModel.getUserAccountStatus().toString());

        //Updated by AB to not rely on the safi MFA service (272) to get phone status
        //boolean deviceExist = deviceArrangementService.isDeviceDetailsFound(safiDeviceId);
        if (!StringUtil.isNotNullorEmpty(safiDeviceId) && !migratedCustomer) {
            userAccountStatusModel.setUserAccountStatus(UserAccountStatus.ACCOUNT_CREATION_INCOMPLETE);
            logger.info("Safi device_id for user {} is null. Overriding users EAM account status to ACCOUNT_CREATION_INCOMPLETE", userId);
        }
        return userAccountStatusModel;
    }

    @Override
    public String createAccount(ServiceOpsModel serviceOps) {
        CreateAccountResponse response = null;
        //TODO Change the conditions after proper implementation of roles for serviceOperators
        if (serviceOps.getRole() != null && Attribute.INVESTOR.equalsIgnoreCase(serviceOps.getRole())) {
            response = btEsbService.processInvestors(getAccountRequestForInvestor(serviceOps));
        } else {
            response = btEsbService.processAdvisers(getCreateAccountRequest(serviceOps));
        }

        if (isErrorResponse(response)) {
            for (ServiceError serviceError : response.getServiceErrors().getErrorList()) {
                String messagid = serviceError.getId();
                if (StringUtils.isEmpty(messagid)) {
                    messagid = Constants.UNKNOWN;
                }
                logger.debug("Service error message id {}", messagid);
                if (messagid != Constants.UNKNOWN) {
                    return cmsService.getDynamicContent(ValidationErrorCode.ERROR_MSG_WITH_CORRELATIONID, new String[]{messagid});
                }
            }
        }
        return Attribute.SUCCESS_MESSAGE;
    }

    private boolean isErrorResponse(CreateAccountResponse response) {
        return response != null && response.getServiceErrors() != null && response.getServiceErrors().hasErrors();
    }

    /**
     * Create request for an adviser creation in esb.
     *
     * @param serviceOps
     * @return
     */
    private CreateAccountRequest getCreateAccountRequest(ServiceOpsModel serviceOps) {
        String email = getPrimaryEmail(serviceOps.getEmail()).getEmail();
        String mobileNo = serviceOps.getPrimaryMobileNumber();
        logger.info("Creating account for {} {} - email: {} gcm ID:{} with mobile number {}", serviceOps.getFirstName(),
            serviceOps.getLastName(), email, serviceOps.getGcmId(), mobileNo);
        CreateAccountRequest createAccountRequest = null;

        if (isAllDetailsAvailable(serviceOps, email, mobileNo)) {
            createAccountRequest = new CreateAccountRequestModel();
            createAccountRequest.setFirstName(serviceOps.getFirstName());
            createAccountRequest.setLastName(serviceOps.getLastName());
            createAccountRequest.setPrimaryEmailAddress(email);
            createAccountRequest.setPrimaryMobileNumber(mobileNo);
            createAccountRequest.setCustomerIdentifiers(buildCustomerIdentifiers(serviceOps));
        } else {
            throw new IllegalArgumentException("Individual details are not complete, required details are coming blank");
        }
        return createAccountRequest;
    }

    /**
     * Create request for an investor creation in esb, will populate investor and adviser details in request
     *
     * @param serviceOps
     * @return
     */
    private ResendRegistrationEmailRequest getAccountRequestForInvestor(ServiceOpsModel serviceOps) {
        String email = getPrimaryEmail(serviceOps.getEmail()).getEmail();
        String mobileNo = serviceOps.getPrimaryMobileNumber();
        logger.info("Creating account for {} {} - email: {} gcm ID:{} with mobile number {}", serviceOps.getFirstName(),
            serviceOps.getLastName(), email, serviceOps.getGcmId(), mobileNo);

        ResendRegistrationEmailRequest createAccountRequest = null;
        if (isAllDetailsAvailable(serviceOps, email, mobileNo)) {
            createAccountRequest = new CreateInvestorAccountRequestModel();
            createAccountRequest.setInvestorFirstName(serviceOps.getFirstName());
            createAccountRequest.setInvestorLastName(serviceOps.getLastName());
            createAccountRequest.setInvestorPrimaryContactNumber(mobileNo);
            createAccountRequest.setInvestorPrimaryEmailAddress(email);
            createAccountRequest.setCustomerIdentifiers(buildCustomerIdentifiers(serviceOps));
            setAdviserDetails(createAccountRequest, getAdviserPositionId(serviceOps.getGcmId()));
        } else {
            throw new IllegalArgumentException("Investor details are not complete, required details are coming blank");
        }
        return createAccountRequest;
    }

    private boolean isAllDetailsAvailable(ServiceOpsModel serviceOps, String email, String mobileNo) {
        return StringUtil.isNotNullorEmpty(serviceOps.getFirstName()) && StringUtil.isNotNullorEmpty(serviceOps.getLastName())
            && StringUtil.isNotNullorEmpty(serviceOps.getGcmId()) && StringUtil.isNotNullorEmpty(email) && StringUtil.isNotNullorEmpty(mobileNo);
    }

    private Map<CustomerNoAllIssuerType, String> buildCustomerIdentifiers(ServiceOpsModel userDetails) {
        Map<CustomerNoAllIssuerType, String> customerIdentifiers = new HashMap<>();
        customerIdentifiers.put(CustomerNoAllIssuerType.BT_PANORAMA, userDetails.getGcmId());
        if (Attribute.INVESTOR.equalsIgnoreCase(userDetails.getRole())) {
            customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC_LEGACY, userDetails.getCisId());
            customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC, userDetails.getWestpacCustomerNumber());
        }
        return customerIdentifiers;
    }

    /**
     * Find adviserPositionId corresponding to an investor
     *
     * @return
     */
    private String getAdviserPositionId(String investorGcmId) {
        String advPositionId = null;
        //prepare request
        Code naturalPersonCode = staticService.loadCodeByName(CodeCategory.PERSON_TYPE, Constants.NATURAL_PERSON, new ServiceErrorsImpl());
        PersonSearchRequest request = new PersonSearchRequestImpl();
        request.setSearchToken(investorGcmId);
        request.setRoleType(Roles.ROLE_INVESTOR.name());
        request.setPersonTypeId(naturalPersonCode.getCodeId());

        List<PersonResponse> searchResult = personSearch.searchUser(request, new ServiceErrorsImpl());
        if (searchResult != null) {
            for (PersonResponse person : searchResult) {
                if (investorGcmId.equals(person.getGcmId())) {
                    advPositionId = person.getAdviserPersonId();
                }
            }
        }
        return advPositionId;
    }

    /**
     * Set adviser details.
     *
     * @param createAccountRequest
     * @param advPositionId
     */
    private void setAdviserDetails(ResendRegistrationEmailRequest createAccountRequest, String advPositionId) {
        BrokerUser adviserBrokerUser = brokerIntegrationService.getAdviserBrokerUser(
            com.bt.nextgen.service.integration.broker.BrokerKey.valueOf(advPositionId), new ServiceErrorsImpl());
        if (adviserBrokerUser != null) {
            IndividualDetailImpl individualDetails = (IndividualDetailImpl) clientIntegrationService.loadClientDetails(
                adviserBrokerUser.getClientKey(), new ServiceErrorsImpl());
            createAccountRequest.setAdviserFirstName(individualDetails.getFirstName());
            createAccountRequest.setAdviserLastName(individualDetails.getLastName());
            createAccountRequest.setAdviserOracleUserId(individualDetails.getCustomerId());
            Phone phone = getPrimaryPhone(individualDetails.getPhones());
            if (phone != null) {
                createAccountRequest.setAdviserPrimaryContactNumber(phone.getNumber());
            }
            createAccountRequest.setAdviserPrimaryEmailAddress(getPrimaryEmail(individualDetails.getEmails()).getEmail());
        } else {
            throw new IllegalStateException("Unable to locate the adviser for this investor");
        }
    }

    /**
     * Return the primary phone from a list of phones
     *
     * @param phones
     * @return
     */
    private Phone getPrimaryPhone(List<Phone> phones) {
        Phone primaryPhone = null;
        if (phones != null && phones.size() > 0) {
            for (Phone phone : phones) {
                if (AddressMedium.MOBILE_PHONE_PRIMARY.equals(phone.getType())) {
                    primaryPhone = phone;
                    break;
                }
            }
            if (primaryPhone == null) {
                primaryPhone = phones.get(0);
            }
        }
        return primaryPhone;
    }

    /**
     * Returns the first primary email address from a list of Email objects
     *
     * @param emails
     * @return
     */
    private Email getPrimaryEmail(List<Email> emails) {
        Email primaryEmail = null;
        if (emails != null && emails.size() > 0) {
            for (Email email : emails) {
                if (AddressMedium.EMAIL_PRIMARY.equals(email.getType())) {
                    primaryEmail = email;
                    break;
                }
            }
            if (primaryEmail == null) {
                primaryEmail = emails.get(0);
            }
        }
        return primaryEmail;
    }
}
