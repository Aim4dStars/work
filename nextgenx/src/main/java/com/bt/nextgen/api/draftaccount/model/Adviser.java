package com.bt.nextgen.api.draftaccount.model;

import org.apache.commons.lang3.StringUtils;

public class Adviser
{
	private String fullName;
    private String corporateName;
	private String businessPhone;
	private String email;

    public Adviser(String fullName, String corporateName, String businessPhone, String email)
	{
		this.fullName = fullName;
        this.corporateName = corporateName;
		this.businessPhone = businessPhone;
		this.email = email;
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

    public String getCorporateName() {
        if (!StringUtils.isEmpty(corporateName)) {
            return corporateName;
        }
        return fullName;
    }

	public String getBusinessPhone()
	{
		return businessPhone;
	}

	public void setBusinessPhone(String businessPhone)
	{
		this.businessPhone = businessPhone;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

}
