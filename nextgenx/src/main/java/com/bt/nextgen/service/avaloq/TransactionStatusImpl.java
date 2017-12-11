package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.service.integration.TransactionStatus;

public class TransactionStatusImpl implements TransactionStatus
{
	private boolean isSuccessful;

	@Override
	public boolean isSuccessful()
	{
		// TODO Auto-generated method stub
		return isSuccessful;
	}

	@Override
	public void setSuccessful(boolean isSuccessful)
	{
		this.isSuccessful = isSuccessful;

	}
	
	private String status;

	@Override
	public String getStatus()
	{
		return status;
	}

	@Override
	public void setStatus(String status)
	{
		this.status=status;
	}

	

}
