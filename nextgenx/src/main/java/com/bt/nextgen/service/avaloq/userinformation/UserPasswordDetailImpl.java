package com.bt.nextgen.service.avaloq.userinformation;

import com.bt.nextgen.service.integration.user.CISKey;
import org.joda.time.DateTime;

import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.user.UserKey;
import com.btfin.panorama.core.security.integration.userinformation.UserPasswordDetail;

public class UserPasswordDetailImpl implements UserPasswordDetail
{
	private BankingCustomerIdentifier customerId;
	private DateTime lastPasswordChange;
    private CISKey cisKey;

	public UserPasswordDetailImpl(BankingCustomerIdentifier customerId)
	{
		this.customerId=customerId;
	}


	@Override public DateTime getLastPasswordChanged()
	{
		return lastPasswordChange;
	}

	@Override public void setLastPasswordChanged(DateTime dateTime)
	{
		this.lastPasswordChange = dateTime;
	}

	@Override public String getBankReferenceId()
	{
		return customerId.getBankReferenceId();
	}

	@Override public UserKey getBankReferenceKey()
	{
		return customerId.getBankReferenceKey();
	}

    @Override
    public CISKey getCISKey() {
        return cisKey;
    }

}
