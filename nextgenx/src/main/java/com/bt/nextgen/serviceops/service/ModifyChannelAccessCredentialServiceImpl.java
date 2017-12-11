package com.bt.nextgen.serviceops.service;

import javax.annotation.Resource;
import com.btfin.panorama.core.security.aes.AESEncryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerUserNameManagementIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;

/**
 * @see 'ModifyChannelAccessCredentialResponse.xml'
 */

@Service
public class ModifyChannelAccessCredentialServiceImpl implements ModifyChannelAccessCredentialService
{
	@Autowired
	private WebServiceProvider provider;

	@Autowired
	private CustomerUserNameManagementIntegrationService customerUserNameManagementIntegrationService;

	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

	@Autowired
	private AESEncryptService aesEncryptService;


	/**
	 * Block user access based on credentialId.
	 */
	@Override
	public boolean blockUserAccess(String credentialId, ServiceErrors serviceErrors)
	{
		CustomerCredentialManagementInformation customerCredentialManagementInformation = customerUserNameManagementIntegrationService.blockUser(credentialId,
			serviceErrors);
		if (Attribute.SUCCESS_MESSAGE.equalsIgnoreCase(customerCredentialManagementInformation.getServiceLevel()))
		{
			return true;
		}
		return false;
	}

	/**
	 * Unblock User access based on credentialId.
	 */
	@Override
	public boolean unblockUserAccess(String credentialId, ServiceErrors serviceErrors)
	{
		CustomerCredentialManagementInformation customerCredentialManagementInformation = customerUserNameManagementIntegrationService.unblockUser(credentialId,
			false,
			serviceErrors);

		if (Attribute.SUCCESS_MESSAGE.equalsIgnoreCase(customerCredentialManagementInformation.getServiceLevel()))
		{

			return true;
		}
		return false;
	}
	
	/*private ModifyChannelAccessCredentialRequest createRequestForBlock(String credentialId, ActionCode actionCode)
	{
		ModifyChannelAccessCredentialRequest req = createRequest(credentialId, actionCode, false);
		LifeCycleStatus lifeCycleStatus = new LifeCycleStatus();
		lifeCycleStatus.setStatus("LCKD_P_AC");
		req.getUserCredential().setLifecycleStatus(lifeCycleStatus);
		return req;
	}*/
	
	/**
	 *  Creates request for blocking and unblocking a User based on ActionCode and credentialId, if password reset is required set  isResetPassword to true.
	 * @param credentialId credentialId
	 * @param actionCode ActionCode
	 * @param isResetPassword True if password reset is required.
	 * @return an object of ModifyChannelAccessCredentialRequest.
	 */
	/*private ModifyChannelAccessCredentialRequest createRequest(String credentialId, ActionCode actionCode, boolean isResetPassword)
	{
		ObjectFactory of = new ObjectFactory();
		ModifyChannelAccessCredentialRequest request = of.createModifyChannelAccessCredentialRequest();
		if (isResetPassword)
		{
			request.setResetPassword(Boolean.TRUE);
			request.setPasswordDeliveryMethod("VOICE");
		}
		UserCredentialDocument userCredential = new UserCredentialDocument();
		Channel channel = new Channel();
		channel.setChannelType("ONL");
		userCredential.setChannel(channel);
		userCredential.setSourceSystem(Properties.get("eam.credential.source.system"));
		CredentialIdentifier credentialIdentifier = new CredentialIdentifier();
		credentialIdentifier.setCredentialId(credentialId);
		userCredential.setLifecycleStatusReason("User requested action");
		userCredential.setCredentialIdentifier(credentialIdentifier);
		request.setRequestedAction(actionCode);
		request.setUserCredential(userCredential);

		return request;
	}*/
	/**
	 * This method is similar to unblockUserAccess(...) method call with only difference it resets the password and returns the decrypted password.
	 * It sets the reset password in request to true and password delivery method to VOICE.
	 * 
	 * @param credentialId credentialId.
	 * @return result The decrypted password which has been reset otherwise returns null.
	 */
	@Override
	public String unblockUserAccessWithResetPassword(String credentialId, ServiceErrors serviceErrors)
	{
		String result = null;
		CustomerCredentialManagementInformation customerCredentialManagementInformation = customerUserNameManagementIntegrationService.unblockUser(credentialId,
			true,
			serviceErrors);

		if (Attribute.SUCCESS_MESSAGE.equalsIgnoreCase(customerCredentialManagementInformation.getServiceLevel()))
		{
			try {
				result = aesEncryptService.decrypt(customerCredentialManagementInformation.getNewPassword());
			} catch (Exception e) {
				throw new IllegalStateException("error decrypting password", e);
			}
		}
		return result;
	}
	
	/**
	 * Checks the status of ModifyChannelAccessCredentialResponse, if StatusInfo Level value is Success it returns true otherwise false.
	 * @param response ModifyChannelAccessCredentialResponse response.
	 * @return the status of ModifyChannelAccessCredentialResponse.
	 */
	/*private boolean getStatus(ModifyChannelAccessCredentialResponse response)
	{
		for (StatusInfo statusInfo : response.getServiceStatus().getStatusInfo())
		{
			return statusInfo.getLevel().value().equalsIgnoreCase("Success");
		}

		return false;
	}
	*/

}
