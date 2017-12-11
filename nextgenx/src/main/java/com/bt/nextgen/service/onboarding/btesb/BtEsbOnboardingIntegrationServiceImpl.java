/**
 * 
 */
package com.bt.nextgen.service.onboarding.btesb;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.onboarding.ProvisionMFADeviceRequest;
import com.bt.nextgen.service.onboarding.ProvisionMFAMobileDeviceResponse;
import com.bt.nextgen.service.onboarding.ProvisionMFAMobileDeviceResponseAdapter;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.onboarding.CreateAccountRequest;
import com.bt.nextgen.service.onboarding.CreateAccountResponse;
import com.bt.nextgen.service.onboarding.FirstTimeRegistrationRequest;
import com.bt.nextgen.service.onboarding.FirstTimeRegistrationResponse;
import com.bt.nextgen.service.onboarding.OnboardingIntegrationService;
import com.bt.nextgen.service.onboarding.ResendRegistrationEmailRequest;
import com.bt.nextgen.service.onboarding.ValidatePartyRequest;
import com.bt.nextgen.service.onboarding.ValidatePartyResponse;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ProvisionMFAMobileDeviceRequestMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ProvisionOnlineAccessRequestMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ValidatePartyRegistrationRequestMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ProvisionMFAMobileDeviceResponseMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ProvisionOnlineAccessResponseMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartyRegistrationResponseMsgType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.ValidatePartySMSOneTimePasswordChallengeRequestMsgType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author L055011
 *
 */
@Service
public class BtEsbOnboardingIntegrationServiceImpl implements OnboardingIntegrationService
{

	private static final Logger logger = LoggerFactory.getLogger(BtEsbOnboardingIntegrationServiceImpl.class);
	
	@Autowired
    private WebServiceProvider provider;
	
	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

    @Resource(name = "serverAuthorityService")
    private BankingAuthorityService serverSamlService;
	
	private static BtEsbRequestBuilder requestBuilder = new BtEsbRequestBuilder();
	
	/**
	 * method that verifies the first time registration/forgot password credentials
	 * @param registrationRequest
	 * @return FirstTimeRegistrationResponse
	 * 
	 */
	@Override
	public FirstTimeRegistrationResponse validateRegistration(FirstTimeRegistrationRequest registrationRequest) 
	{
		ValidatePartySMSOneTimePasswordChallengeRequestMsgType request = requestBuilder.build(registrationRequest);
		ValidatePartySMSOneTimePasswordChallengeResponseMsgType response = (ValidatePartySMSOneTimePasswordChallengeResponseMsgType) provider
				.sendWebServiceWithSecurityHeader(serverSamlService.getSamlToken(), Attribute.ONBOARDING_KEY, request);
		return new ValidatePartyAndSmsAdapter(response);
	}


	@Override
	public ValidatePartyResponse validateParty(ValidatePartyRequest credentialsRequest) {
		ValidatePartyRegistrationRequestMsgType request = requestBuilder.buildValidatePartyRequest(credentialsRequest);
		ValidatePartyRegistrationResponseMsgType response = (ValidatePartyRegistrationResponseMsgType) provider.sendWebServiceWithSecurityHeader(serverSamlService.getSamlToken(), Attribute.BT_ESB_VALIDATE_PARTY_KEY, request);
		return new ValidatePartyAndSmsAdapter(response);
	}

	/**
	 * method used for advisers on boarding
	 * @param createAccountRequest
	 * @return CreateAccountResponse
	 * 
	 */
	@Override
	public CreateAccountResponse processAdvisers(CreateAccountRequest createAccountRequest)
	{
        ProvisionOnlineAccessRequestMsgType request = requestBuilder.build(createAccountRequest);
        ProvisionOnlineAccessResponseMsgType response = (ProvisionOnlineAccessResponseMsgType) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
				Attribute.ADVISOR_ONBOARDING_KEY, request);
		logger.debug("Recevied process adviser response message with status: {}", response.getStatus().value());
		return new ProcessAdvisersAdapter(response);
	}

	/**
	 * method used for investors onboarding
	 * @param createAccountRequest
	 * @return CreateAccountResponse
	 */
	@Override
	public CreateAccountResponse processInvestors(ResendRegistrationEmailRequest createAccountRequest)
	{
		ProvisionOnlineAccessRequestMsgType request = requestBuilder.buildInvestorProcessRequest(createAccountRequest);
		ProvisionOnlineAccessResponseMsgType response = (ProvisionOnlineAccessResponseMsgType)provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
			Attribute.INVESTOR_ONBOARDING_KEY, request);

		CreateAccountResponse result = new ProcessInvestorAdapter(response);
		logger.debug("Recevied process investor response message with status :{}", response.getStatus().value());
		return result;
	}

	@Override
	public FirstTimeRegistrationResponse validateRegistrationDetails(FirstTimeRegistrationRequest RegistartionRequest)
	{
		ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ValidatePartySMSOneTimePasswordChallengeRequestMsgType request =
				requestBuilder.buildRegistrationDetails(RegistartionRequest);
		ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType response =
				(ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType) provider
						.sendWebServiceWithSecurityHeader(serverSamlService.getSamlToken(), Attribute.BT_ESB_VALIDATE_PARTY_SMS_KEY, request);
		return new ValidatePartyAndSmsAdapter(response);
	}

    /**
     * Provision MFA device for investors who will initially have the mobiles missing(Eg:Migrated customers) and later updated through Avaloq
     * @param provisionMFADeviceRequest
     * @return
     */
    @Override
    public ProvisionMFAMobileDeviceResponse provisionMFADevice(ProvisionMFADeviceRequest provisionMFADeviceRequest) {
        ProvisionMFAMobileDeviceRequestMsgType provisionMFAMobileDeviceRequestMsgType = requestBuilder.buildProvisionMFADeviceRequest(provisionMFADeviceRequest);
        ProvisionMFAMobileDeviceResponseMsgType provisionMFAMobileDeviceResponseMsgType = (ProvisionMFAMobileDeviceResponseMsgType)provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                Attribute.PROVISION_MFA_DEVICE,provisionMFAMobileDeviceRequestMsgType);
        logger.info("Recieved response for MFA Device set up with status : {}" + provisionMFAMobileDeviceResponseMsgType.getStatus().value());
        return new ProvisionMFAMobileDeviceResponseAdapter(provisionMFAMobileDeviceResponseMsgType);
    }


}