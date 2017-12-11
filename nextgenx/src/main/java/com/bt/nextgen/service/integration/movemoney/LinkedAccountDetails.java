package com.bt.nextgen.service.integration.movemoney;

import com.btfin.panorama.service.integration.account.BankAccount;

public interface LinkedAccountDetails extends BankAccount
{
	public boolean isPrimary();
	
	public void setPrimary(boolean isPrimary);

}
