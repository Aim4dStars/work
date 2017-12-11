/**
 * 
 */
package com.bt.nextgen.service.onboarding;

import com.bt.nextgen.service.ServiceErrors;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ProvisionMFAMobileDeviceRequestMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ProvisionMFAMobileDeviceResponseMsgType;
/**
 * @author L055011
 *
 */
public interface OnboardingIntegrationService 
{
	FirstTimeRegistrationResponse validateRegistration(FirstTimeRegistrationRequest request);

	ValidatePartyResponse validateParty(ValidatePartyRequest request);
	
	CreateAccountResponse processAdvisers(CreateAccountRequest request);
	
	CreateAccountResponse processInvestors(ResendRegistrationEmailRequest createAccountRequest);

	FirstTimeRegistrationResponse validateRegistrationDetails(FirstTimeRegistrationRequest request);

    ProvisionMFAMobileDeviceResponse provisionMFADevice(ProvisionMFADeviceRequest provisionMFAMobileDeviceRequestMsg);
}
