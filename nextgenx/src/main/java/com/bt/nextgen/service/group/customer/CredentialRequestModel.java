package com.bt.nextgen.service.group.customer;

import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;

public class CredentialRequestModel implements CredentialRequest
{
	private String gcmId;
    private UserKey userKey;
    private CISKey cisKey;


	public String getBankReferenceId()
	{
		return gcmId;
	}

    @Override
    public UserKey getBankReferenceKey() {
        if(null== userKey){
            userKey=UserKey.valueOf(gcmId);
        }
        return userKey;
    }

    @Override
    public CISKey getCISKey() {
        return cisKey;
    }

    public void setBankReferenceId(String gcmId)
	{
		this.gcmId = gcmId;
	}
}
