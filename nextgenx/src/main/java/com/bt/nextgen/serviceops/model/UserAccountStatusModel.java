package com.bt.nextgen.serviceops.model;

import org.joda.time.DateTime;

import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.core.security.UserAccountStatus;

public class UserAccountStatusModel 
{
	UserAccountStatus userAccountStatus;
	DateTime date;
	Iterable<ServiceError> errorList;
	
	public UserAccountStatus getUserAccountStatus() 
	{
		return userAccountStatus;
	}
	public void setUserAccountStatus(UserAccountStatus userAccountStatus) 
	{
		this.userAccountStatus = userAccountStatus;
	}
	public DateTime getDate() 
	{
		return date;
	}
	public void setDate(DateTime date) 
	{
		this.date = date;
	}
	public Iterable<ServiceError> getErrorList() {
		return errorList;
	}
	public void setErrorList(Iterable<ServiceError> errorList) {
		this.errorList = errorList;
	}
}
