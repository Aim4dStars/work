package com.bt.nextgen.serviceops.util;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserAccountStatusJaxb
{
	com.btfin.panorama.core.security.UserAccountStatus userAccountStatus;

	public com.btfin.panorama.core.security.UserAccountStatus getUserAccountStatus()
	{
		return userAccountStatus;
	}

	public void setUserAccountStatus(com.btfin.panorama.core.security.UserAccountStatus userAccountStatus)
	{
		this.userAccountStatus = userAccountStatus;
	}
}
