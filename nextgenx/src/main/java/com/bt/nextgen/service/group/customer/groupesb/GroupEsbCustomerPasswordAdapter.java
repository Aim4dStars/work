package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.MaintainChannelAccessServicePasswordResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;

import com.bt.nextgen.core.service.ErrorConstants;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.web.validator.ValidationErrorCode;

public class GroupEsbCustomerPasswordAdapter implements CustomerCredentialManagementInformation
{
    private MaintainChannelAccessServicePasswordResponse maintainChannelAccessCredentialResponse;

    public GroupEsbCustomerPasswordAdapter(MaintainChannelAccessServicePasswordResponse response) throws RuntimeException
    {
    	 if(response == null)
             throw new RuntimeException("The MaintainChannelAccessServicePasswordResponse Response was null");
    	 
    	this.maintainChannelAccessCredentialResponse = response;
    }

	@Override
	public String getServiceLevel() 
	{
		return maintainChannelAccessCredentialResponse.getServiceStatus().getStatusInfo().get(0).getLevel().toString();
	}

	@Override
	public String getServiceStatusErrorCode() 
	{
		return maintainChannelAccessCredentialResponse.getServiceStatus().getStatusInfo().get(0).getStatusDetail().get(0).getProviderErrorDetail().get(0).getProviderErrorCode();
	}

	@Override
	public String getServiceStatusErrorDesc() 
	{
		return null;
	}

	@Override
	public String getServiceStatus() 
	{
		return maintainChannelAccessCredentialResponse.getServiceStatus().toString();
	}
	
	@Override
	public String getServiceNegativeResponse()
	{
		ServiceStatus status = maintainChannelAccessCredentialResponse.getServiceStatus();
		if (status != null && status.getStatusInfo() != null && status.getStatusInfo().size() > 0)
		{
			Level level = status.getStatusInfo().get(0).getLevel();
			switch (level)
			{
				case ERROR:
					if(status.getStatusInfo().get(0).getStatusDetail().get(0).getProviderErrorDetail().get(0).getProviderErrorCode().equalsIgnoreCase(ErrorConstants.ALIAS_IN_USE_FAULT))
					{
						return ValidationErrorCode.USER_NAME_NOT_UNIQUE;	
					}
					else
					{
						return ValidationErrorCode.ERROR_IN_REGISTRATION;
					}
				case WARNING:
				case INFORMATION:
				default:
					return ValidationErrorCode.ERROR_IN_REGISTRATION;
			}
		}
		return ValidationErrorCode.ERROR_IN_REGISTRATION;
	}

	@Override
	public String getNewPassword()
	{
		return maintainChannelAccessCredentialResponse.getUserCredential().getNewPassword().getPassword();
	}
}
