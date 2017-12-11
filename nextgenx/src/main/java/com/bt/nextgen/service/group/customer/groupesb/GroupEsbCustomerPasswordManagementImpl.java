package com.bt.nextgen.service.group.customer.groupesb;

import javax.annotation.Resource;

import au.com.westpac.gn.channelmanagement.services.passwordmanagement.common.xsd.v1.Channel;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.common.xsd.v1.PasswordCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.common.xsd.v1.UserCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.ActionCode;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.MaintainChannelAccessServicePasswordRequest;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.MaintainChannelAccessServicePasswordResponse;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.ObjectFactory;
import au.com.westpac.gn.common.xsd.identifiers.v1.CredentialIdentifier;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.service.ErrorHandlerUtil;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerPasswordManagementIntegrationService;
import com.bt.nextgen.service.group.customer.CustomerPasswordUpdateRequest;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.web.controller.cash.util.Attribute;

@Service
public class GroupEsbCustomerPasswordManagementImpl  implements CustomerPasswordManagementIntegrationService
{
	private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerPasswordManagementImpl.class);
	
	@Autowired
	private WebServiceProvider provider;

    @Autowired
    private CmsService cmsService;

	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

	public static final String HALGM_CHARSET = "CP1252";

	/**
	 *  Generic method to call the service svc0247 to update the user password either its forgot password(before logging in) request or the change request password(after logging in).
	 *  ActionCode: UPDATE_PASSWORD (Used for change request password(after logging in))
	 *  ActionCode: SET_PASSWORD (Used for forgot password(before logging in))
	 *  UserReset: contains the passwords.
	 *  person.getOracleUser() is the oracle user Id or Avaloq Id  or avaloq customer number.
	 * @throws Exception 
	 */
	@Override
	public CustomerCredentialManagementInformation updatePassword(CustomerPasswordUpdateRequest passwordRequest, ServiceErrors serviceErrors)
	{
		logger.info("GroupEsbCustomerPasswordManagementImpl.updatePassword() : Updating the User Password");
		logger.info("Requested action is {}", passwordRequest.getRequestedAction());

		MaintainChannelAccessServicePasswordRequest request = createMaintainChannelAccessServicePasswordRequest(passwordRequest,serviceErrors);
		CorrelatedResponse correlatedResponse =  provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
												 userSamlService.getSamlToken(),Attribute.GROUP_ESB_MAINTAIN_CHANNEL_ACCESS_CREDENTIAL, request, serviceErrors);
		MaintainChannelAccessServicePasswordResponse response = (MaintainChannelAccessServicePasswordResponse)correlatedResponse.getResponseObject();
		logger.info("LogonServiceImpl.updatePassword() : Successfully returning service status.");
		CustomerCredentialManagementInformation result = new GroupEsbCustomerPasswordAdapter(response);
        if(!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE))
        {
            ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_247, correlatedResponse.getCorrelationIdWrapper(), cmsService);
        }
        return result;
	}
	
    public MaintainChannelAccessServicePasswordRequest createMaintainChannelAccessServicePasswordRequest(CustomerPasswordUpdateRequest passwordUpdateRequest,ServiceErrors serviceErrors)
    {
		ObjectFactory of = new ObjectFactory();
		MaintainChannelAccessServicePasswordRequest request = of.createMaintainChannelAccessServicePasswordRequest();
		request.setRequestedAction(ActionCode.fromValue(passwordUpdateRequest.getRequestedAction()));

		UserCredentialDocument userCredentialDocument = new UserCredentialDocument();

		if (ActionCode.RESET_PASSWORD.equals(ActionCode.fromValue(passwordUpdateRequest.getRequestedAction())))
		{
			request.setPasswordDeliveryMethod(ServiceConstants.VOICE);
		}
		else
		{
			PasswordCredentialDocument newPasswordCredentialDocument = new PasswordCredentialDocument();
			newPasswordCredentialDocument.setPassword(passwordUpdateRequest.getConfirmPassword());
			userCredentialDocument.setNewPassword(newPasswordCredentialDocument);

			//should this be in the request or does the nature of the request make this true?
			if (ActionCode.fromValue(passwordUpdateRequest.getRequestedAction()).equals(ActionCode.UPDATE_PASSWORD))
			{
				PasswordCredentialDocument currentPasswordCredentialDocument = new PasswordCredentialDocument();
				currentPasswordCredentialDocument.setPassword(passwordUpdateRequest.getPassword());
				userCredentialDocument.setCurrentPassword(currentPasswordCredentialDocument);
			}

			if (passwordUpdateRequest != null && passwordUpdateRequest.getHalgm() != null)
			{
				userCredentialDocument.setEncryptionKey(encodeHalgmForTransmission(passwordUpdateRequest.getHalgm(),
					serviceErrors));
			}
		}

		CredentialIdentifier credentialIdentifier = new CredentialIdentifier();
		credentialIdentifier.setCredentialId(passwordUpdateRequest.getCredentialId());
		userCredentialDocument.setInternalIdentifier(credentialIdentifier);

		Channel channel = new Channel();
		channel.setChannelType(Properties.get(ServiceConstants.EAM_CREDENTIAL_CHANNEL));
		userCredentialDocument.setChannel(channel);

		request.setUserCredential(userCredentialDocument);
		request.setReason(ServiceConstants.REASON);
		
		return request;
    }

    private byte[] encodeHalgmForTransmission(String htmlEscapedHalgm, ServiceErrors errors)
    {
        // Remove html encoding as the client sends the halgm back in encoded form
        //removed by AB as this is no longer HTML escaped
        //String decodedOriginalHalgmValue = StringEscapeUtils.unescapeHtml4(htmlEscapedHalgm);
        byte[] base64DecodedHalgmValue = null;
        try{
        // Base64 Decode the halgm -- since jaxb is going to encoded it again during marshalling
            base64DecodedHalgmValue = Base64.decodeBase64(htmlEscapedHalgm.getBytes(HALGM_CHARSET));
        }catch(Exception encodingError){
            errors.addError(new ServiceErrorImpl("Could not encode the halgm for transmission"));
        }

        // The user Model expects the halgm in string format
        //String strBase64DecodedHalgmValue = new String(base64DecodedHalgmValue, HALGM_CHARSET);


        return base64DecodedHalgmValue;
    }
    
}
