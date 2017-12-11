package com.bt.nextgen.api.corporateaction.v1.model;

/**
 * Corporate action Dto object for IM
 */

public class ImCorporateActionDto extends CorporateActionBaseDto {
	public ImCorporateActionDto() {
		// Empty constructor
	}

	/**
	 * The corporate action dto constructor
	 *
	 * @param id     the document id/order number
	 * @param params the corporate action dto params object
	 */
	public ImCorporateActionDto(String id, CorporateActionDtoParams params) {
		super(id, params);
	}

	@Override
	public String getKey() {
		return null;
	}
}
