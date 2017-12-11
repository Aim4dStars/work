package com.bt.nextgen.serviceops.service;


import javax.annotation.Resource;

import com.btfin.panorama.core.security.aes.AESEncryptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.gateway.AvaloqException;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerPasswordManagementIntegrationService;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;

/**
 * @see 'ResetPasswordServiceResponse.xml'
 */
@Service
public class ResetPasswordServiceImpl implements ResetPasswordService
{
	@Autowired
	private WebServiceProvider provider;

	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	CustomerPasswordManagementIntegrationService customerPasswordManagement;

    @Autowired
    private UserInformationIntegrationService userInformationIntegrationService;

	private static final Logger logger = LoggerFactory.getLogger(ResetPasswordServiceImpl.class);

	@Autowired
	private AESEncryptService aesEncryptService;

	/**
	 * @param credentialId
	 * @return temporary password generated for the user, otherwise returns null value
	 */
	@Override
	public String resetPassword(String credentialId, final String gcmId, ServiceErrors serviceErrors)

	{
		UserReset resetPasswordRequest = new UserReset();
		resetPasswordRequest.setRequestedAction(ServiceConstants.RESET_PASSWORD);
		resetPasswordRequest.setCredentialId(credentialId);
		CustomerCredentialManagementInformation customerCredentialManagementInformation = customerPasswordManagement.updatePassword(resetPasswordRequest,
			serviceErrors);
		if (Attribute.SUCCESS_MESSAGE.equalsIgnoreCase(customerCredentialManagementInformation.getServiceLevel()))
		{
			logger.info("ResetPasswordServiceImpl.resetPassword() : Successfully returning service status.");
			String result = null;
			//TODO need clarification about personId, for now sending userID only
			try
			{
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
                logger.debug("ResetPasswordServiceImpl.resetPassword() :before invoking the notifyPasswordChange");
                userInformationIntegrationService.notifyPasswordChange(bankingCustomerIdentifier,serviceErrors);
                logger.info("ResetPasswordServiceImpl.resetPassword(): notifyPasswordChange service execution completed for gcm {}",gcmId);

			}
			catch (AvaloqException e)
			{
				logger.error("Getting error response from avaloq while sending password update information for {}", credentialId);
			}

			try {
				result = aesEncryptService.decrypt(customerCredentialManagementInformation.getNewPassword());
			} catch (Exception e) {
				logger.error("error decrypting password", e);
				throw new IllegalStateException("error decrypting password");
			}
			return result;
		}
		else
		{
			logger.info("ResetPasswordServiceImpl.resetPassword() : Error, returning service status.");
			return null;
		}
	}
}
