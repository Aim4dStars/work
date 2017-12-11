package com.bt.nextgen.api.registration.service;

import com.bt.nextgen.api.registration.model.InvestorDto;
import com.bt.nextgen.api.registration.model.RegistrationDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.repository.User;
import com.bt.nextgen.core.repository.UserRepository;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditionsKey;
import com.bt.nextgen.service.integration.registration.repository.UserRoleTermsAndConditionsRepository;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RegistrationDtoServiceImpl implements RegistrationDtoService {

    private static Logger logger = LoggerFactory.getLogger(RegistrationDtoServiceImpl.class);

    private final String PRIMARY = "Primary";
    private final String WORK = "Work";

    @Autowired
    private AccActivationIntegrationService accActivationIntegrationService;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private UserRepository userRepository;

	@Autowired
	private UserRoleTermsAndConditionsRepository userRoleTermsAndConditionsRepository;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;


    /**
     * Take account Id and construct wrapAccount to load account application details and return application investor registration
     *  detail as registration dto.
     * @param accountId  - input parameter account BP id
     * @param serviceErrors - Output parameter for service errors.
     * @return List <RegistrationDto>
     */
    @Override
    public List <RegistrationDto> getAccountApplicationStatus(String accountId, ServiceErrors serviceErrors)
    {
        List<WrapAccountIdentifier> lstWrapAccountIdentifier = new ArrayList<WrapAccountIdentifier>();
        WrapAccountIdentifier wrapAccountIdentifier = new WrapAccountIdentifierImpl();
        wrapAccountIdentifier.setBpId(accountId);
        lstWrapAccountIdentifier.add(wrapAccountIdentifier);
        UserProfile activeProfile = userProfileService.getActiveProfile();
        List<ApplicationDocument> lstApplicationDocument = accActivationIntegrationService.loadAccApplicationForPortfolio(lstWrapAccountIdentifier, activeProfile.getJobRole(),activeProfile.getClientKey(),serviceErrors);
        //load account details.
        WrapAccount wrapAccount = accountIntegrationService.loadWrapAccountWithoutContainers(
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId), serviceErrors);
        //load adviser details.
        BrokerUser adviser = brokerIntegrationService.getAdviserBrokerUser(wrapAccount.getAdviserPositionId(), serviceErrors);
        //getting log in user profile.
        UserProfile profile = userProfileService.getActiveProfile();
        ClientKey clientKey = profile.getClientKey();

        return toRegistrationModel(wrapAccount, lstApplicationDocument, adviser, clientKey, serviceErrors);
    }

    @Override
    public List <RegistrationDto> search(List <ApiSearchCriteria> criteria, ServiceErrors serviceErrors)
    {
        String accountId = criteria.get(0).getValue();
        List <RegistrationDto> registrationDtoList = getAccountApplicationStatus(accountId, serviceErrors);
        return registrationDtoList;
    }


    private  List <RegistrationDto> toRegistrationModel(WrapAccount wrapAccount, List<ApplicationDocument> lstApplicationDocument, BrokerUser adviser, ClientKey clientKey, ServiceErrors serviceErrors) {
        List <RegistrationDto> lstRegistrationDto = new ArrayList <RegistrationDto>();
        try {

            RegistrationDto registrationDto = new RegistrationDto(
                    new com.bt.nextgen.api.account.v1.model.AccountKey(wrapAccount.getAccountKey().getId())
            );
            registrationDto.setApplicationType(wrapAccount.getAccountStructureType());
            if(adviser != null) {
                registrationDto.setAdviserFullName(adviser.getFullName());
                registrationDto.setAdviserPhoneNumber(this.getPhone(adviser.getPhones(), this.WORK));
                registrationDto.setAdviserEmail(this.getEmail(adviser.getEmails()));
            }

            if (null != lstApplicationDocument) {
                for (ApplicationDocument applicationDocument : lstApplicationDocument) {

                    registrationDto.setApplicationReferenceNo(applicationDocument.getAppNumber());
                    registrationDto.setAppSubmitDate(applicationDocument.getAppSubmitDate());

                    List<AssociatedPerson> lstAssociatedPerson = applicationDocument.getPersonDetails();
                    InvestorDto investorDto = null;
                    List<InvestorDto> lstInvestor = new ArrayList<InvestorDto>();
                    for (AssociatedPerson associatedPerson : lstAssociatedPerson)
                    {
                        //checking logged in person is approver or non-approver
                        if (associatedPerson.getClientKey().getId().equals(clientKey.getId())) {
                            if (associatedPerson.isHasToAcceptTnC()) {
                                registrationDto.setApprover(true);
                            } else {
                                registrationDto.setApprover(false);
                            }
                        }
                        //getting only approvers details.
                        if(associatedPerson.isHasToAcceptTnC()) {
                            ClientDetail clientDetails = clientIntegrationService.loadClientDetails(associatedPerson.getClientKey(), serviceErrors);
                            if(clientDetails != null && clientDetails.getClientType().equals(ClientType.N)) {
                                investorDto = new InvestorDto();
                                investorDto.setInvestorName(clientDetails.getFullName());
                                investorDto.setInvestorMobile(this.getPhone(clientDetails.getPhones(), this.PRIMARY));
                                investorDto.setInvestorEmail(this.getEmail(clientDetails.getEmails()));

                                PersonRelationship personRelationship = associatedPerson.getPersonRel();
                                if (personRelationship.equals(PersonRelationship.AO)) {
                                    investorDto.setPrimary(true);
                                }
                                if (associatedPerson.isRegisteredOnline()) {
                                    investorDto.setRegistered(true);
                                }
                                if (associatedPerson.isHasApprovedTnC()) {
                                    investorDto.setApproved(true);
                                }
                                lstInvestor.add(investorDto);
                            }
                        }
                    }
                    registrationDto.setLstInvestors(lstInvestor);
                }
            }
            lstRegistrationDto.add(registrationDto);
            return lstRegistrationDto;

        } catch (Exception e) {
            ServiceError error = new ServiceErrorImpl();
            error.setException(e);
            error.setService("RegistrationDtoService");
            serviceErrors.addError(error);
            logger.error("Unable to populate registration dto." , e);
        }

        return lstRegistrationDto;
    }

    private String getEmail(List<Email> emails) {
        String email = null;
        if (CollectionUtils.isNotEmpty(emails)) {
            for (Email emailObj : emails) {
                if (emailObj.getType().getAddressType().equals(this.PRIMARY))
                    email = emailObj.getEmail();
            }
        }
        return email;
    }

    private String getPhone(List<Phone> phones, String type) {
        String phone = null;
        if (CollectionUtils.isNotEmpty(phones)) {
            for (Phone phoneObj : phones) {
                if (phoneObj.getType().getAddressType().equals(type))
                    phone = phoneObj.getNumber();
            }
        }
        return  phone;
    }

    /**
     * Update Term & Condition flag for non approvers to user repository.
	 * Approvers terms and conditions (investor only) are saved in avaloq.
     * @return boolean - if updated successfully then true otherwise false.
     */
    @Override
    @Transactional(value = "springJpaTransactionManager")
    public boolean updateTnCForNonAprrover() {
        boolean isUpdated = false;

		//User user = userRepository.loadUser(userProfileService.getGcmId());
		UserRoleTermsAndConditionsKey userRoleTncKey = new UserRoleTermsAndConditionsKey(userProfileService.getGcmId(), userProfileService.getActiveProfile().getProfileId());
		UserRoleTermsAndConditions userRoleTncs = userRoleTermsAndConditionsRepository.find(userRoleTncKey);


        if (userRoleTncs != null && !"Y".equalsIgnoreCase(userRoleTncs.getTncAccepted()))
        {
            logger.info("updating T&C flag for non approver {}", userRoleTncs.getUserRoleTermsAndConditionsKey().getGcmId());
			userRoleTncs.setTncAccepted("Y");
            //added date on which T&C is accepted for non approver
			userRoleTncs.setTncAcceptedOn(new Date());
			userRoleTncs.setVersion(1);
			userRoleTncs.setModifyDatetime(new Date());
            userRoleTermsAndConditionsRepository.save(userRoleTncs);
            isUpdated = true;
        }
		else
		{
            ServiceErrors serviceErrors = new ServiceErrorsImpl();
            User user = new User(userProfileService.getGcmId());
            logger.info("Adding new non approver with T&C flag {}",user.getUsername());
            List<BrokerIdentifier> positionKeys =(List<BrokerIdentifier>) brokerIntegrationService
                    .getAdvisersForUser(userProfileService.getActiveProfile(), serviceErrors);
            if(positionKeys != null && positionKeys.size() > 0) {
                //always adviser will be one
                BrokerUser adviserBrokerUser  = brokerIntegrationService
                        .getAdviserBrokerUser(positionKeys.get(0).getKey(), serviceErrors);
                user.setId(adviserBrokerUser.getBankReferenceId());
            }
            user.setFirstTimeLoggedIn(true);
            userRepository.update(user);

			userRoleTncs = new UserRoleTermsAndConditions();
			userRoleTncs.setTncAccepted("Y");
			userRoleTncs.setTncAcceptedOn(new Date());
			userRoleTncs.setVersion(1);
			userRoleTncs.setModifyDatetime(new Date());
			userRoleTermsAndConditionsRepository.save(userRoleTncs);

            isUpdated = true;
        }

        return isUpdated;
    }
}
