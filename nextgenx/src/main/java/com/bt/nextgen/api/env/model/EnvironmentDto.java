package com.bt.nextgen.api.env.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

public class EnvironmentDto extends BaseDto
{
	private String environment;

	private String cmsHostForAem;

	private String livePersonId;

    private DateTime bankDate;

    private Integer bankTimeOffsetInMillis;

	private String addressValidationQasApi;

    @JsonProperty("appDKey")
	private String appDynamicsKey;

	private String provisioToken;

	private String provisioHost;

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getCmsHostForAem() {
		return cmsHostForAem;
	}

	public void setCmsHostForAem(String cmsHostForAem) {
		this.cmsHostForAem = cmsHostForAem;
	}

	public String getLivePersonId() {
		return livePersonId;
	}

	public void setLivePersonId(String livePersonId) {
		this.livePersonId = livePersonId;
	}

    public DateTime getBankDate() {
        return bankDate;
    }

    public void setBankDate(DateTime bankDate) {
        this.bankDate = bankDate;
    }

	public Integer getBankTimeOffsetInMillis() {
		return bankTimeOffsetInMillis;
	}

	public void setBankTimeOffsetInMillis(Integer bankTimeOffsetInMillis) {
		this.bankTimeOffsetInMillis = bankTimeOffsetInMillis;
	}

	public String getAddressValidationQasApi() {
		return addressValidationQasApi;
	}

	public void setAddressValidationQasApi(String addressValidationQasApi) {
		this.addressValidationQasApi = addressValidationQasApi;
	}

	public String getAppDynamicsKey() {
		return appDynamicsKey;
	}

    public void setAppDynamicsKey(String appDynamicsKey) {
        this.appDynamicsKey = appDynamicsKey;
    }

    public String getProvisioToken() {
        return provisioToken;
    }

    public void setProvisioToken(String provisioToken) {
		this.provisioToken = provisioToken;
	}

	public String getProvisioHost() {
		return provisioHost;
	}

	public void setProvisioHost(String provisioHost) {
		this.provisioHost = provisioHost;
	}
}
