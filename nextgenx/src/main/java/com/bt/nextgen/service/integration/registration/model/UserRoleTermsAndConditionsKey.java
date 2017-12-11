package com.bt.nextgen.service.integration.registration.model;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserRoleTermsAndConditionsKey implements Serializable
{
	@Column(name="USER_ID")
	private String gcmId;

	@Column(name="PROFILE_ID")
	private String jobProfileId;


	public UserRoleTermsAndConditionsKey()
	{

	}

	public UserRoleTermsAndConditionsKey(String gcmId, String jobProfileId)
	{
		this.gcmId = gcmId;
		this.jobProfileId = jobProfileId;
	}


	public String getGcmId() {
		return gcmId;
	}

	public void setGcmId(String gcmId) {
		this.gcmId = gcmId;
	}

	public String getJobProfileId() {
		return jobProfileId;
	}

	public void setJobProfileId(String jobProfileId) {
		this.jobProfileId = jobProfileId;
	}
}
