package com.bt.nextgen.api.draftaccount.model;

import java.util.Collection;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;

public class SendEmailDto extends BaseDto implements KeyedDto<ClientKey> {

    private ClientKey clientKey;
    private String status;
    private Long clientApplicationId;
	private Collection <BrokerIdentifier> adviserIds;
    private String role;

    public SendEmailDto(Long clientApplicationId, String clientId) {
        clientKey = ClientKey.valueOf(clientId);
        this.clientApplicationId = clientApplicationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public ClientKey getKey() {
        return clientKey;
    }

    public Long getClientApplicationId() {
        return clientApplicationId;
    }

	/**
	 * @return the adviserIds
	 */
	public Collection <BrokerIdentifier> getAdviserIds()
	{
		return adviserIds;
	}

	/**
	 * @param adviserIds the adviserIds to set
	 */
	public void setAdviserIds(Collection <BrokerIdentifier> adviserIds)
	{
		this.adviserIds = adviserIds;
	}

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
