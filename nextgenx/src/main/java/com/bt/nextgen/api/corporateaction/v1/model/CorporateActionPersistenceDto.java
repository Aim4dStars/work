package com.bt.nextgen.api.corporateaction.v1.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import com.fasterxml.jackson.annotation.JsonView;
import org.joda.time.DateTime;

import java.util.List;


public class CorporateActionPersistenceDto extends BaseDto implements KeyedDto<CorporateActionDtoKey> {
	@JsonView(JsonViews.Write.class)
	private CorporateActionDtoKey key;

	@JsonView(JsonViews.Write.class)
	private DateTime closeDate;

	@JsonView(JsonViews.Write.class)
	private List<CorporateActionOptionDto> options;

	@JsonView(JsonViews.Write.class)
	private List<CorporateActionAccountDetailsDto> accounts;

	@JsonView(JsonViews.Write.class)
	private List<ImCorporateActionPortfolioModelDto> portfolioModels;

	@JsonView(JsonViews.Write.class)
	private String ipsId;

	private boolean bulkSave = false;

	private CorporateActionResponseCode status;

	public CorporateActionPersistenceDto() {
		// Empty constructor
	}

	public CorporateActionPersistenceDto(String id,
										 DateTime closeDate,
										 List<CorporateActionOptionDto> options,
										 List<CorporateActionAccountDetailsDto> accounts,
										 List<ImCorporateActionPortfolioModelDto> portfolioModels,
										 String ipsId,
										 boolean bulkSave) {
		this.key = new CorporateActionDtoKey(id, null, ipsId, false);
		this.options = options;
		this.accounts = accounts;
		this.portfolioModels = portfolioModels;
		this.closeDate = closeDate;
		this.ipsId = ipsId;
		this.bulkSave = bulkSave;
	}

	public CorporateActionPersistenceDto(CorporateActionResponseCode responseCode) {
		this.status = responseCode;
	}

	public List<CorporateActionOptionDto> getOptions() {
		return options;
	}

	public List<CorporateActionAccountDetailsDto> getAccounts() {
		return accounts;
	}

	public List<ImCorporateActionPortfolioModelDto> getPortfolioModels() {
		return portfolioModels;
	}

	public DateTime getCloseDate() {
		return closeDate;
	}

	public String getIpsId() {
		return ipsId;
	}

	public boolean isBulkSave() {
		return bulkSave;
	}

	public CorporateActionResponseCode getStatus() {
		return status;
	}

	@Override
	public CorporateActionDtoKey getKey() {
		return key;
	}
}
