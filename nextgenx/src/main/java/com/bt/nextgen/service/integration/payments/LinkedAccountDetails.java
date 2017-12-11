package com.bt.nextgen.service.integration.payments;

import com.btfin.panorama.service.integration.account.BankAccount;

@Deprecated
public interface LinkedAccountDetails extends BankAccount
{
	public boolean isPrimary();
	
	public void setPrimary(boolean isPrimary);

}
