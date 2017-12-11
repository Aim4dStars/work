package com.bt.nextgen.core.service;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.LifeCycleStatus;
import com.btfin.panorama.core.security.Roles;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.CredentialRequest;
import com.bt.nextgen.service.group.customer.CredentialRequestModel;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.bt.nextgen.service.group.customer.CustomerLoginManagementIntegrationService;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The default implementation of the user service.
 * Only ACCOUNT_CREATION_INCOMPLETE is mapped to Error Code: [309,00008,309,00010] everything else directly comes
 * from 'LifeCycleStatus' element. Refer to response file 'CredentialResponseFromEAM.xml' for more details.
 */
@SuppressWarnings({"squid:S1200","squid:S00116","squid:MethodCyclomaticComplexity","checkstyle:com.puppycrawl.tools.checkstyle.checks.sizes.ExecutableStatementCountCheck","squid:S1142","checkstyle:com.puppycrawl.tools.checkstyle.checks.whitespace.TypecastParenPadCheck"})
@Component("credentialService")
public class CredentialServiceImpl implements CredentialService
{
	//errorCode 309,00008 or 309,00010 means account creation incomplete refer to CredentialResponseFromEAM.xml
	private final String ACCOUNT_CREATION_INCOMPLETE_ERROR_CODE1 = "309,00008";
	private final String ACCOUNT_CREATION_INCOMPLETE_ERROR_CODE2 = "309,00010";
	private static final Logger logger = LoggerFactory.getLogger(CredentialServiceImpl.class);

	@Autowired
	private CustomerLoginManagementIntegrationService customerLoginManagement;
	
	@Override
	public String getUserName(String gcmId, ServiceErrors serviceErrors) throws Exception
	{
		CredentialRequest credentialRequest  = new CredentialRequestModel();
		credentialRequest.setBankReferenceId(gcmId);
		CustomerCredentialInformation customerCredentialInformation = customerLoginManagement.getCustomerInformation(credentialRequest, serviceErrors);
		return customerCredentialInformation.getUsername();
	}
	
	@Override
	public UserAccountStatusModel lookupStatus(String userId, ServiceErrors serviceErrors)
	{
		logger.info("CredentialServiceImpl.lookupStatus(): userId-{}", userId);
		CustomerCredentialInformation customerCredentialInformation = null;
		CredentialRequest credentialRequest  = new CredentialRequestModel();
		UserAccountStatusModel userAccountStatusModel = new UserAccountStatusModel();
		credentialRequest.setBankReferenceId(userId);
		try
		{
			customerCredentialInformation = customerLoginManagement.getCustomerInformation(credentialRequest, serviceErrors);
		}
		catch(RuntimeException rex)
		{
			logger.error("GroupEsb retreiveChannelAccessCredential service call returned empty response");
			userAccountStatusModel.setUserAccountStatus(UserAccountStatus.ACCOUNT_CREATION_INCOMPLETE);
			return userAccountStatusModel;
		}
		
		if (customerCredentialInformation.getStatusInfo().equals(ACCOUNT_CREATION_INCOMPLETE_ERROR_CODE1) || customerCredentialInformation.getStatusInfo().equals(
				ACCOUNT_CREATION_INCOMPLETE_ERROR_CODE2))
		{
			logger.info("CredentialServiceImpl.lookupStatus(): Status Code-{}", customerCredentialInformation.getStatusInfo());
			userAccountStatusModel.setUserAccountStatus(UserAccountStatus.ACCOUNT_CREATION_INCOMPLETE);
			return userAccountStatusModel;
		}

		userAccountStatusModel.setUserAccountStatus(customerCredentialInformation.getPrimaryStatus());
		userAccountStatusModel.setDate(customerCredentialInformation.getDate());
		logger.info("CredentialServiceImpl.lookupStatus(): Status Code-{}", customerCredentialInformation.getUserAccountStatus());
		return userAccountStatusModel;
	}
	
    public UserAccountStatus mostImportantStatus(List< LifeCycleStatus> lifeCycleStatusList)
    {
        List<UserAccountStatus> statuses = getUserAccountStatusList(lifeCycleStatusList);
        Collections.sort(statuses);
        return statuses.get(0);

    }

    public List<UserAccountStatus> getUserAccountStatusList(List< LifeCycleStatus> lifeCycleStatusList)
    {
        List<UserAccountStatus> statuses = new ArrayList<UserAccountStatus>();
			for ( LifeCycleStatus lifeCycleStatus : lifeCycleStatusList)
			{
				String status = lifeCycleStatus.getStatus();
				statuses.add(UserAccountStatus.valueOf(status.toUpperCase()));
		}

        logger.debug("Found {} different statuses for the user", statuses.size());
        return statuses;
    }

	public UserAccountStatus mostImportantStatusV3(List<au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.LifeCycleStatus> lifeCycleStatusList)
	{
		List<UserAccountStatus> statuses = getUserAccountStatusListV3(lifeCycleStatusList);
		Collections.sort(statuses);
		return statuses.get(0);

	}

	public List<UserAccountStatus> getUserAccountStatusListV3(List<au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.LifeCycleStatus> lifeCycleStatusList)
	{
		List<UserAccountStatus> statuses = new ArrayList<UserAccountStatus>();
			for (au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.LifeCycleStatus lifeCycleStatus : lifeCycleStatusList)
			{
				String status = lifeCycleStatus.getStatus();
				statuses.add(UserAccountStatus.valueOf(status.toUpperCase()));
			}
		logger.debug("Found {} different statuses for the user", statuses.size());
		return statuses;
	}


	@Override
	public String getCredentialId(String customerNumber, ServiceErrors serviceErrors) throws Exception
	{
		logger.info("CredentialServiceImpl.getCredentialId()");

		CredentialRequest credentialRequest = new CredentialRequestModel();
		credentialRequest.setBankReferenceId(customerNumber);
		CustomerCredentialInformation customerCredentialInformation = customerLoginManagement.getCustomerInformation(credentialRequest,
			serviceErrors);
		logger.info("Successfully returning Credential Id.");
		return customerCredentialInformation.getCredentialId();
	}

    @Override
    @SuppressWarnings(" squid:S00112  ")
    public String getZnumberForInvestor(String customerNumber) throws Exception {
        logger.info("CredentialServiceImpl.getZnumberForInvestor()");

        CredentialRequest credentialRequest = new CredentialRequestModel();
        credentialRequest.setBankReferenceId(customerNumber);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        CustomerCredentialInformation customerCredentialInformation = customerLoginManagement.getDirectCustomerInformation(
                credentialRequest, serviceErrors);
        boolean isInvestor = false;
        for (Roles role : customerCredentialInformation.getCredentialGroups()) {
            if (Roles.ROLE_INVESTOR.equals(role)) {
                isInvestor = true;
                break;
            }
        }
        if (isInvestor) {
            logger.info("Successfully returning z-number.");
            return customerCredentialInformation.getUserReferenceId();
        } else {
            logger.warn("Not an investor, returning empty z-number.");
            return Attribute.EMPTY_STRING;
        }
    }

	@Override
	public String getCISKey(String clientID) {
		CredentialRequest credentialRequest = new CredentialRequestModel();
		credentialRequest.setBankReferenceId(clientID);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerCredentialInformation customerCredentialInformation = customerLoginManagement.getCustomerInformation(
				credentialRequest, serviceErrors);

		return  customerCredentialInformation.getCISKey().getId();
	}

	@Override
	public List<Roles> getCredentialGroups(String clientID) {
		CredentialRequest credentialRequest = new CredentialRequestModel();
		credentialRequest.setBankReferenceId(clientID);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List<Roles> roles = customerLoginManagement.getCredentialGroups(
				credentialRequest, serviceErrors);
		return  roles;
	}

	@Override
	public String getPPID(String clientID) {
		CredentialRequest credentialRequest = new CredentialRequestModel();
		credentialRequest.setBankReferenceId(clientID);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		String PPID = customerLoginManagement.getPPID(credentialRequest,serviceErrors);
		return PPID;
	}

}
