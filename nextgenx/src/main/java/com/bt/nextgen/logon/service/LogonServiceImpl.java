package com.bt.nextgen.logon.service;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialRequest;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialResponse;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.UserNameAliasCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.common.xsd.v1.Channel;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.common.xsd.v1.InvolvedParty;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.common.xsd.v1.PasswordCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.common.xsd.v1.UserCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.ActionCode;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.MaintainChannelAccessServicePasswordRequest;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.MaintainChannelAccessServicePasswordResponse;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.ObjectFactory;
import au.com.westpac.gn.common.xsd.identifiers.v1.CredentialIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.CredentialService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.gateway.AvaloqException;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerLoginManagementIntegrationService;
import com.bt.nextgen.service.group.customer.CustomerPasswordManagementIntegrationService;
import com.bt.nextgen.service.group.customer.CustomerUserNameManagementIntegrationService;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userinformation.UserPasswordDetail;
import com.bt.nextgen.service.prm.service.PrmService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

//import ns.btfin_com.nextgen.services.schemas.ObjectFactory;

@Service
@SuppressWarnings({"checkstyle:com.puppycrawl.tools.checkstyle.checks.sizes.ExecutableStatementCountCheck","squid:S00116"})
public class LogonServiceImpl implements LogonService {

	private static final Logger logger = LoggerFactory
			.getLogger(LogonServiceImpl.class);

	private static final String EAM_CREDENTIAL_SOURCE = "eam.credential.source.system";
	private static final String EAM_CREDENTIAL_CHANNEL = "eam.credential.channel";
	private static final String SVC_310_V5_ENABLED = "svc.310.v5.enabled";
	@Autowired
	private WebServiceProvider provider;

	@Autowired
	private UserProfileService profileService;

	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

	@Autowired
	private CredentialService credentialService;
	
	@Autowired
	private CustomerLoginManagementIntegrationService customerLoginManagementIntegrationService;
	
	@Autowired
    CustomerUserNameManagementIntegrationService customerUserNameManagement;
	
	@Autowired
    CustomerPasswordManagementIntegrationService customerPasswordManagement;

    @Autowired
    private UserInformationIntegrationService userInformationIntegrationService;

	@Autowired
	private PrmService prmService;

	@Autowired
	private FeatureTogglesService featureTogglesService;

	@Override
	public String modifyUserAlias(UserReset userReset, ServiceErrors serviceErrors) throws Exception 
	{
		//userReset.setNewUserName(userReset.getModifiedUsername());
		CustomerCredentialManagementInformation customerCredentialManagementInformation = customerUserNameManagement.updateUsername(userReset, serviceErrors);
		if(!serviceErrors.hasErrors())
		{
			if (customerCredentialManagementInformation.getServiceLevel().equals(Level.fromValue(Attribute.SUCCESS)))
			{
				return Attribute.SUCCESS_MESSAGE;
			}
			else
			{
				return customerCredentialManagementInformation.getServiceLevel();
			}
		}
		return null;
		
		
	}
	
	@Deprecated
	/**
	 * New method created according to the new design.
	 * @param credentialID
	 * @param modifiedUserName
	 * @return
	 * @throws Exception
	 */
	public String modifyUserAlias(String credentialID, String modifiedUserName) throws Exception
	{
		if(Properties.getSafeBoolean(SVC_310_V5_ENABLED)) {
			ModifyChannelAccessCredentialRequest requestPayload = new ModifyChannelAccessCredentialRequest();
		requestPayload.setRequestedAction(au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ActionCode.MODIFY_USER_ALIAS);
		au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.UserCredentialDocument userCredential = new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.UserCredentialDocument();
		au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.Channel channeltype =
				new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.Channel();
		channeltype.setChannelType(Properties.get(EAM_CREDENTIAL_CHANNEL));
		userCredential.setChannel(channeltype);
		UserNameAliasCredentialDocument userNameAlias = new UserNameAliasCredentialDocument();
		userNameAlias.setUserAlias(modifiedUserName);
		userCredential.setUserName(userNameAlias);
		CredentialIdentifier regIdentifier = new CredentialIdentifier();
		regIdentifier.setCredentialId(credentialID);
		userCredential.setCredentialIdentifier(regIdentifier);
		userCredential.setSourceSystem(Properties.get(EAM_CREDENTIAL_SOURCE));
		requestPayload.setUserCredential(userCredential);
		//TODO needs to check weather SamlToken needs to send or not
		////getSamlToken throws null pointer exception @see Profile.getOriginalAuth(): SecurityContextHolder.getContext().getAuthentication().getAuthorities()) throws NULL
		/*ModifyChannelAccessCredentialResponse response =
				(ModifyChannelAccessCredentialResponse) provider.sendWebServiceWithSecurityHeader(
					userSamlService.getSamlToken(),Attribute.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL, requestPayload);*/

			//todo: temporary solution for DEV, need to remove in integration
			ModifyChannelAccessCredentialResponse response = (ModifyChannelAccessCredentialResponse) provider.sendWebService(
					Attribute.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL, requestPayload);
			return getServiceSuccess(response.getServiceStatus());
		}
		else{
			//TODO : Remove v3 when integration available in all environments
			au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialRequest requestPayload = new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialRequest();
			requestPayload.setRequestedAction(au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ActionCode.MODIFY_USER_ALIAS);
			au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.UserCredentialDocument userCredential = new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.UserCredentialDocument();
			au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.Channel channeltype =
					new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.Channel();
			channeltype.setChannelType(Properties.get(EAM_CREDENTIAL_CHANNEL));
			userCredential.setChannel(channeltype);
			au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.UserNameAliasCredentialDocument userNameAlias = new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.UserNameAliasCredentialDocument();
			userNameAlias.setUserAlias(modifiedUserName);
			userCredential.setUserName(userNameAlias);
			CredentialIdentifier regIdentifier = new CredentialIdentifier();
			regIdentifier.setCredentialId(credentialID);
			userCredential.setCredentialIdentifier(regIdentifier);
			userCredential.setSourceSystem(Properties.get(EAM_CREDENTIAL_SOURCE));
			requestPayload.setUserCredential(userCredential);
			ModifyChannelAccessCredentialResponse response = (ModifyChannelAccessCredentialResponse) provider.sendWebService(
					Attribute.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL, requestPayload);
			return getServiceSuccess(response.getServiceStatus());
		}

	}

	@Deprecated
	public String updatePassword(String credentialID, UserReset userDetails,
			String requestedAction) {

		ObjectFactory of = new ObjectFactory();
		MaintainChannelAccessServicePasswordRequest request = of
				.createMaintainChannelAccessServicePasswordRequest();
		//TODO replaced with exact value of DeliveryMethod
		request.setPasswordDeliveryMethod("SMS");
		request.setRequestedAction(ActionCode.valueOf(requestedAction));

		UserCredentialDocument userCredentialDocument = new UserCredentialDocument();

		PasswordCredentialDocument newPasswordCredentialDocument = new PasswordCredentialDocument();
		newPasswordCredentialDocument.setPassword(userDetails.getNewpassword());
		userCredentialDocument.setNewPassword(newPasswordCredentialDocument);

		PasswordCredentialDocument currentPasswordCredentialDocument = new PasswordCredentialDocument();
		currentPasswordCredentialDocument
				.setPassword(userDetails.getPassword());
		userCredentialDocument
				.setCurrentPassword(currentPasswordCredentialDocument);

		CredentialIdentifier credentialIdentifier = new CredentialIdentifier();
		credentialIdentifier.setCredentialId(credentialID);
		userCredentialDocument.setInternalIdentifier(credentialIdentifier);

		Channel channel = new Channel();
		channel.setChannelType(Properties.get(Constants.EAM_CREDENTIAL_CHANNEL));
		userCredentialDocument.setChannel(channel);

		//TODO No clarity on EncryptionKey exact value
		userCredentialDocument.setEncryptionKey(new byte[] {}); 

		InvolvedParty involvedParty = new InvolvedParty();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId("");
		involvedPartyIdentifier
				.setIdentificationScheme(IdentificationScheme.CROSS_REFERENCE_ID);
		involvedParty.setInternalIdentifier(involvedPartyIdentifier);
		userCredentialDocument.setIsCredentialOf(involvedParty);

		userCredentialDocument.setStartDate(null);
		request.setUserCredential(userCredentialDocument);

		MaintainChannelAccessServicePasswordResponse newResponse = (MaintainChannelAccessServicePasswordResponse) provider
				.sendWebService(Attribute.GROUP_ESB_MAINTAIN_CHANNEL_ACCESS_CREDENTIAL, request);
		return getServiceSuccess(newResponse.getServiceStatus());
	}

	/**
	 *  Generic method to call the service svc0247 to update the user password either its forgot password(before logging in) request or the change request password(after logging in).
	 *  ActionCode: UPDATE_PASSWORD (Used for change request password(after logging in))
	 *  ActionCode: SET_PASSWORD (Used for forgot password(before logging in))
	 *  UserReset: contains the passwords.
	 *  GcmId is the nine digit id.
	 */
	@Override
	public String updatePassword(UserReset userDetails, ServiceErrors serviceErrors)
	{
		CustomerCredentialManagementInformation customerCredentialManagementInformation = customerPasswordManagement.updatePassword(userDetails, serviceErrors);
		
		if (customerCredentialManagementInformation.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE))
		{
			logger.info("LogonServiceImpl.updatePassword() : Successfully returning service status.");
			try
			{
                logger.debug("LogonServiceImpl.updatePassword() :before invoking the notifyPasswordChange");
                UserPasswordDetail userInformation = userInformationIntegrationService.notifyPasswordChange(profileService.getActiveProfile(),serviceErrors);
                if(userInformation!=null && userInformation.getBankReferenceId()!=null)
                    logger.info("LogonServiceImpl.updatePassword(): notifyPasswordChange service execution completed for user {} ",userInformation.getBankReferenceId());
			}
			catch (AvaloqException e)
			{
				logger.error("Getting error response from avaloq while sending password update information for {}",
					userDetails.getCredentialId());
			}

			if (featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle(FeatureToggles.PRM_VIEW)) {
				prmService.triggerForgotPasswordPrmEvent();
			}


			return Attribute.SUCCESS_MESSAGE;
		}
		else
		{
			logger.info("LogonServiceImpl.updatePassword() : Error, returning service status.");
			return customerCredentialManagementInformation.getServiceLevel();
		}
	}
	
	//TODO : remove the below method as it has been replaced by updatePassword(UserReset userDetails, ServiceErrors serviceErrors) method
	/**
	 *  Generic method to call the service svc0247 to update the user password either its forgot password(before logging in) request or the change request password(after logging in).
	 *  ActionCode: UPDATE_PASSWORD (Used for change request password(after logging in))
	 *  ActionCode: SET_PASSWORD (Used for forgot password(before logging in))
	 *  UserReset: contains the passwords.
	 *  person.getOracleUser() is the oracle user Id or Avalow Id  or avaloq customer number.
	 * @throws Exception 
	 */
	/*@Override
	@Deprecated
	public String updatePassword(UserReset userDetails, ActionCode requestedAction) throws Exception 
	{
		logger.info("LogonServiceImpl.updatePassword() : Fetching the person details");
		logger.info("Requested action is {}", requestedAction);
		Person person = profileService.getPerson();
		String credentialId = "";
		
		try
		{
			logger.info("The oracle user id of the user is {}", person.getOracleUser());
			credentialId = credentialService.getCredentialId(person.getOracleUser());
		}
		catch(Exception ex)
		{
			logger.info("Problem in retrieving credential Id");
		}
		

		MaintainChannelAccessServicePasswordRequest request = new MaintainChannelAccessServicePasswordRequest();
		request.setRequestedAction(requestedAction);

		UserCredentialDocument userCredentialDocument = new UserCredentialDocument();

		PasswordCredentialDocument newPasswordCredentialDocument = new PasswordCredentialDocument();
		newPasswordCredentialDocument.setPassword(userDetails.getNewPassword());
		userCredentialDocument.setNewPassword(newPasswordCredentialDocument);

		if(requestedAction.equals(ActionCode.UPDATE_PASSWORD))
		{
			PasswordCredentialDocument currentPasswordCredentialDocument = new PasswordCredentialDocument();
			currentPasswordCredentialDocument.setPassword(userDetails.getPassword());
			userCredentialDocument.setCurrentPassword(currentPasswordCredentialDocument);
		}

		CredentialIdentifier credentialIdentifier = new CredentialIdentifier();
		credentialIdentifier.setCredentialId(credentialId);
		userCredentialDocument.setInternalIdentifier(credentialIdentifier);

		Channel channel = new Channel();
		channel.setChannelType(Properties.get(EAM_CREDENTIAL_CHANNEL));
		userCredentialDocument.setChannel(channel);
		
		userCredentialDocument.setEncryptionKey(userDetails.getHalgmInBytes()); 

		request.setUserCredential(userCredentialDocument);

		MaintainChannelAccessServicePasswordResponse response = (MaintainChannelAccessServicePasswordResponse) provider
				.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),Attribute.GROUP_ESB_MAINTAIN_CHANNEL_ACCESS_CREDENTIAL, request);
		
		if (response.getServiceStatus().getStatusInfo().get(0).getLevel().equals(Level.fromValue(Attribute.SUCCESS)))
		{
			logger.info("LogonServiceImpl.updatePassword() : Successfully returning service status.");
			return Attribute.SUCCESS_MESSAGE;
		}
		else
		{
			logger.info("LogonServiceImpl.updatePassword() : Error, returning service status.");
			return ErrorHandlerUtil.parsePasswordServiceNegativeResponse(response.getServiceStatus(), new ServiceErrorsImpl(), null);
		}
	}*/
	
	/**
	 * To Change the password after all validation checks.
	 * 
	 	 *      java.lang.String)
	 * @param credentialID
	 *            - User name.
	 * @param newPassword
	 *            - Password to set.
	 * @return String - "SUCCESS" or "FAIL" or "NULL".
	 * 
	 */
	//TODO To be removed.
	@Deprecated
	public String changePassword(String credentialID, String newPassword) 
	{
		ObjectFactory of = new ObjectFactory();
		MaintainChannelAccessServicePasswordRequest request = of
				.createMaintainChannelAccessServicePasswordRequest();
		request.setRequestedAction(ActionCode.SET_PASSWORD);
		request.setReason("Forgetten password is going to be changed after validations");
		request.setPasswordDeliveryMethod("delivery method ??");

		UserCredentialDocument userCredentialDocument = new UserCredentialDocument();

		PasswordCredentialDocument newPasswordCredentialDocument = new PasswordCredentialDocument();
		newPasswordCredentialDocument.setPassword(newPassword);
		userCredentialDocument.setNewPassword(newPasswordCredentialDocument);

		PasswordCredentialDocument currentPasswordCredentialDocument = new PasswordCredentialDocument();
		currentPasswordCredentialDocument
				.setPassword("we dont have current password??");
		userCredentialDocument
				.setCurrentPassword(currentPasswordCredentialDocument);

		userCredentialDocument
				.setCredentialType("what is credential type would be???");

		CredentialIdentifier credentialIdentifier = new CredentialIdentifier();
		credentialIdentifier.setCredentialId(credentialID);
		userCredentialDocument.setInternalIdentifier(credentialIdentifier);

		Channel channel = new Channel();
		channel.setChannelType("Channel Type ??");
		userCredentialDocument.setChannel(channel);

		userCredentialDocument.setEncryptionKey(new byte[] {}); // do we need
																// that

		InvolvedParty involvedParty = new InvolvedParty();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId("");
		involvedPartyIdentifier
				.setIdentificationScheme(IdentificationScheme.CROSS_REFERENCE_ID);
		involvedPartyIdentifier.setSourceSystem("source system ???");
		involvedParty.setInternalIdentifier(involvedPartyIdentifier);
		userCredentialDocument.setIsCredentialOf(involvedParty);

		userCredentialDocument.setStartDate(null);
		request.setUserCredential(userCredentialDocument);

		MaintainChannelAccessServicePasswordResponse newResponse = (MaintainChannelAccessServicePasswordResponse) provider
				.sendWebService("stub", request);
		ServiceStatus serviceStatus = newResponse.getServiceStatus();
		for (StatusInfo statusInfo : serviceStatus.getStatusInfo()) 
		{
			logger.info("statusInfo: ===>>>>>" + statusInfo.getLevel().value());
			return statusInfo.getLevel().value();
		}
		return null;

	}

	/**
	 * To validate the user details.<BR/>
	 * Particularly before generating the SMS Code for password reset.
	 * 
	 * @param credentialID
	 *            - User name.
	 * @param lastName
	 *            - Last name of the user.
	 * @param postCode
	 *            - Postal Code.
	 * @return String - <B>SUCCESS</B>, if valid user. <B>FAIL</B> if user details are invalid. <B>LOCKED</B>, if account is locked.
	 * 
	 */
	@Override
	public String validateUser(String credentialID,String lastName, int postCode)
	{
		// TODO Need to change the implementation once EAM service WSDL is published.
		// Need to validate the error code and TAM_OP to find out whether the account is valid or invalid or locked.
		// Need to find 2FA attempts.
		String tamOperation = validatewithEAM(credentialID, lastName, postCode);
		switch (tamOperation)
		{
		case "login_success":
			logger.info("Valid user");
			return Attribute.SUCCESS_MESSAGE;
		case "auth_info":
			logger.info("Account locked");
			return Attribute.ACCOUNT_LOCKED_MESSAGE;
		default:
			logger.info("Invalid user");
			return Attribute.FAILURE_MESSAGE;
		}
	}
	
	/**
	 * Dummy method will be removed once EAM integration is done. 
	 * @param credentialID
	 * @param lastName
	 * @param postCode
	 * @return String
	 */
	private String validatewithEAM(String credentialID,String lastName, int postCode){
		if ((credentialID.equals("adviser") && lastName.equals("adviser") && postCode == 1111)
				|| (credentialID.equals("investor")	&& lastName.equals("investor") && postCode == 1111)) 
		{
			return new String("login_success");
		}
		else if (credentialID.equals("test1"))
		{
			return new String("auth_info");
		}
		return new String("auth_failure");
	}
	
	/**
	 * Needs to be removed.
	 * @param credentialID
	 * @param lastName
	 * @param postCode
	 * @param smsCode
	 * @return String	 
	 */
	public String verifySmsCode(String credentialID,String lastName, int postCode, String smsCode)
	{
		//TODO: This will be removed once get the actual implementation with SAFI service	
		if (StringUtils.isBlank(credentialID) || StringUtils.isBlank(lastName))
		{
			return Attribute.FAILURE_MESSAGE;
		}
			
		if ((credentialID.equals("adviser") && lastName.equals("adviser") && postCode == 1111)
				|| (credentialID.equals("investor")	&& lastName.equals("investor") && postCode == 1111)) 
		{
			if (smsCode == null)
			{
				// smsCode will be null if 2FA is not enabled.
				return Attribute.SUCCESS_MESSAGE;
			}
			else 
			{
				if(smsCode.equals("111111"))
				{
					return Attribute.SUCCESS_MESSAGE;
				}
				else if(smsCode.equals("222222"))
				{
					return Attribute.ERROR_MESSAGE;
				}
				else
				{
					return "";
				}
			}
		}
		return Attribute.FAILURE_MESSAGE; 
	}
	
	/**
	 * Private method to check whether the service response status is SUCCESS or not.
	 * 
	 * @param status ServiceStatus
	 *
	 * @return boolean
	 */
	private String getServiceSuccess(ServiceStatus status)
	{
		if (null != status && null != status.getStatusInfo() && status.getStatusInfo().size() > 0)
		{
			Level level = status.getStatusInfo().get(0).getLevel();
			switch (level)
			{
			case ERROR:
				return status.getStatusInfo().get(0).
						getStatusDetail().get(0).getProviderErrorDetail().get(0).
						getProviderErrorCode();
			default:
				break;
			}			
		}
		return Attribute.SUCCESS_MESSAGE;
	}
}
