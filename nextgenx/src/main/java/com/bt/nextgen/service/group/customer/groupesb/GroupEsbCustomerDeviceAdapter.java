package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.MaintainMFADeviceArrangementResponse;

import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;

public class GroupEsbCustomerDeviceAdapter implements CustomerCredentialManagementInformation
{

	private MaintainMFADeviceArrangementResponse maintainMFADeviceArrangementResponse;

	public GroupEsbCustomerDeviceAdapter(MaintainMFADeviceArrangementResponse response) throws RuntimeException
	{
		if (response == null)
			throw new RuntimeException("The MaintainMFADeviceArrangementResponse Response was null");

		this.maintainMFADeviceArrangementResponse = response;
	}

	@Override
	public String getServiceLevel()
	{
		return maintainMFADeviceArrangementResponse.getServiceStatus().getStatusInfo().get(0).getLevel().toString();
	}

	@Override
	public String getServiceStatusErrorCode()
	{
		return maintainMFADeviceArrangementResponse.getServiceStatus()
			.getStatusInfo()
			.get(0)
			.getStatusDetail()
			.get(0)
			.getProviderErrorDetail()
			.get(0)
			.getProviderErrorCode();
	}

	@Override
	public String getServiceStatusErrorDesc()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceStatus()
	{
		return maintainMFADeviceArrangementResponse.getServiceStatus().toString();
	}

	@Override
	public String getServiceNegativeResponse()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNewPassword()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
