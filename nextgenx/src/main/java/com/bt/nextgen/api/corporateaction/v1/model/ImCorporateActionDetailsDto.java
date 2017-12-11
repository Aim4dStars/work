package com.bt.nextgen.api.corporateaction.v1.model;

import java.util.List;

/**
 * Corporate action details Dto object.
 */
public class ImCorporateActionDetailsDto extends CorporateActionDetailsBaseDto {
	private List<ImCorporateActionPortfolioModelDto> portfolioModels;
    private List<CorporateActionAccountDetailsDto> accounts;

	public ImCorporateActionDetailsDto() {
		// Empty constructor
	}

	/**
	 * The CA details constructor
	 *
	 * @param params the CA details wrapper class of params
	 */
	public ImCorporateActionDetailsDto(CorporateActionDetailsDtoParams params) {
		super(params);
		this.portfolioModels = params.getPortfolioModels();
        this.accounts = params.getAccounts();
	}

	public List<ImCorporateActionPortfolioModelDto> getPortfolioModels() {
		return portfolioModels;
	}

    public List<CorporateActionAccountDetailsDto> getAccounts() {
        return accounts;
    }

	@Override
	public CorporateActionDtoKey getKey() {
		return null;
	}
}
