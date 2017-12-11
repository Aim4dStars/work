package com.bt.nextgen.api.corporateaction.v1.model;

/**
 * Corporate action Dto object.
 */

public class CorporateActionDto extends CorporateActionBaseDto {
	private String accountId;

	public CorporateActionDto() {
		super();
	}

	/**
	 * The corporate action dto constructor
	 *
	 * @param id     the document id/order number
	 * @param params the corporate action dto params object
	 */
	public CorporateActionDto(String id, CorporateActionDtoParams params) {
		super(id, params);
		this.accountId = params.getAccountId();
	}

	/**
	 * The account ID this CA is applicable to (from search)
	 *
	 * @return
	 */
	public String getAccountId() {
		return accountId;
	}

	@Override
	public String getKey() {
		return null;
	}
}
