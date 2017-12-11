package com.bt.nextgen.supportStaff.web;

import com.bt.nextgen.core.web.model.AddressModel;

public interface SupportStaffInterface
{
	public String getDob();

	public void setDob(String dob);

	public String getFirstName();

	public void setFirstName(String firstName);

	public String getLastName();

	public void setLastName(String lastName);

	public String getUsername();

	public void setUsername(String username);

	public String getPhoneNr();

	public void setPhoneNr(String phoneNr);

	public String getMobile();

	public void setMobile(String mobile);

	public String getEmail();

	public void setEmail(String email);

	public String getFaxNr();

	public void setFaxNr(String faxNr);

	public String getSuppStaffType();

	public void setSuppStaffType(String suppStaffType);

	public String getPermissionType();

	public void setPermissionType(String permissionType);

	public String getPermissionDesc();

	public void setPermissionDesc(String permissionDesc);

	public String getClientId();

	public void setClientId(String clientId);

	public AddressModel getAddress();

	public void setAddress(AddressModel address);
	
	public String getGender();
	
	public void setGender(String gender);
	
	

}