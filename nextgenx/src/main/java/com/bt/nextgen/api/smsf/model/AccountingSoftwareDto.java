package com.bt.nextgen.api.smsf.model;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class AccountingSoftwareDto extends BaseDto implements KeyedDto <AccountKey>
{
	private AccountKey key;
	private String softwareName;
	private String softwareDisplayName;
	private String feedStatus;
	private boolean accountantLinked;
    private List<DomainApiErrorDto> warnings;
    private boolean status;
	private String authorisationType;
	private boolean fundAuthorised;

	@Override
	public AccountKey getKey()
	{
		return key;

	}

	public void setKey(AccountKey key)
	{
		this.key = key;
	}

	public String getSoftwareName()
	{
		return softwareName;
	}

	public void setSoftwareName(String softwareName)
	{
		this.softwareName = softwareName;
	}


	public String getSoftwareDisplayName() {
		return softwareDisplayName;
	}

	public void setSoftwareDisplayName(String softwareDisplayName) {
		this.softwareDisplayName = softwareDisplayName;
	}

	public String getFeedStatus()
	{
		return feedStatus;
	}

	public void setFeedStatus(String feedStatus)
	{
		this.feedStatus = feedStatus;
	}

    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<DomainApiErrorDto> warnings) {
        this.warnings = warnings;
    }

    public boolean isAccountantLinked() {
        return accountantLinked;
    }

    public void setAccountantLinked(boolean accountantLinked) {
        this.accountantLinked = accountantLinked;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


	public String getAuthorisationType() {
		return authorisationType;
	}

	public void setAuthorisationType(String authorisationType) {
		this.authorisationType = authorisationType;
	}

	public boolean isFundAuthorised() {
		return fundAuthorised;
	}

	public void setFundAuthorised(boolean fundAuthorised) {
		this.fundAuthorised = fundAuthorised;
	}
}