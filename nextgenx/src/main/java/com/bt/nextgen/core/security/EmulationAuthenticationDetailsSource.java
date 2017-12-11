package com.bt.nextgen.core.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.group.customer.CredentialRequest;
import com.bt.nextgen.service.group.customer.CredentialRequestModel;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.bt.nextgen.service.group.customer.CustomerLoginManagementIntegrationService;

@Component
public class EmulationAuthenticationDetailsSource implements AuthenticationDetailsSource<EmulationRequestInfo, Profile>
{
	private static final Logger logger = LoggerFactory.getLogger(EmulationAuthenticationDetailsSource.class);
	
	@Autowired
	private CustomerLoginManagementIntegrationService customerLoginService;
	

	@Override
	public Profile buildDetails(EmulationRequestInfo context) 
	{
		logger.info("Building profile for emulated user - gcm_id: {}", context.getGcmId());
   		
		CredentialRequest request = new CredentialRequestModel();
		request.setBankReferenceId(context.getGcmId());
		CustomerCredentialInformation custInfo = customerLoginService.getCustomerInformation(request,new FailFastErrorsImpl());
		
		//TODO: Retrieve the profile id from the service here
		
   		return new Profile(custInfo, context.getProfileId());	
	}

}
