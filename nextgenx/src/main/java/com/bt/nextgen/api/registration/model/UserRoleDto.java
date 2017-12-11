package com.bt.nextgen.api.registration.model;


import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;

public class UserRoleDto implements KeyedDto<JobProfileIdentifier>
{
	private String gcmId;

	private String jobProfileId;

	private String accepted;

	private String version;

	private boolean transactionStatus = false;

	private JobProfileIdentifier jobProfileIdentifier;

	public boolean isTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(boolean transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	@Override
	public JobProfileIdentifier getKey() {
		return jobProfileIdentifier;
	}

	@Override
	public String getType()
	{
		return "UserRoleDto";
	}

	public String getJobProfileId() {
		return jobProfileId;
	}

	public void setJobProfileId(String jobProfileId) {
		this.jobProfileId = jobProfileId;
	}

	public String getAccepted() {
		return accepted;
	}

	public void setAccepted(String accepted) {
		this.accepted = accepted;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setGcmId(String gcmId) {
		this.gcmId = gcmId;
	}

	public String getGcmId() {
		return gcmId;
	}
}