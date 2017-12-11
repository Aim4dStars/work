package com.bt.nextgen.service.integration;

public interface TransactionStatus
{
	public boolean isSuccessful();

	public void setSuccessful(boolean isSuccessful);

	public String getStatus();

	public void setStatus(String status);
}
