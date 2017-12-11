package com.bt.nextgen.api.corporateaction.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

/**
 * This class consists of parameters being passed from the API to the DTO service
 */
public class CorporateActionElectionDetailsBaseDto extends BaseDto implements KeyedDto<CorporateActionDtoKey> {
	private CorporateActionDtoKey key;
	private List<CorporateActionOptionDto> options;
	private CorporateActionResponseCode status;
	private Integer successCount;
	private Integer totalCount;
	private String message;

	public CorporateActionElectionDetailsBaseDto() {
		// Empty constructor
	}

	/**
	 * The main constructor
	 *
	 * @param orderNumber the order number (CA ID)
	 * @param options     the options list from the front-end
	 */
	public CorporateActionElectionDetailsBaseDto(String orderNumber,
												 List<CorporateActionOptionDto> options) {
		this.key = new CorporateActionDtoKey(orderNumber);
		this.options = options;
	}

	public CorporateActionElectionDetailsBaseDto(CorporateActionResponseCode status, Integer successCount, Integer totalCount,
												 String message) {
		this.status = status;
		this.successCount = successCount;
		this.totalCount = totalCount;
		this.message = message;
	}

	/**
	 * Get the options list
	 *
	 * @return options
	 */
	public List<CorporateActionOptionDto> getOptions() {
		return options;
	}

	@Override
	public CorporateActionDtoKey getKey() {
		return key;
	}

	/**
	 * The submit election status
	 *
	 * @return submit election status
	 */
	public CorporateActionResponseCode getStatus() {
		return status;
	}

	/**
	 * The error message from submission if any
	 *
	 * @return error message string
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * The number of submitted accounts
	 *
	 * @return number of submitted accounts
	 */
	public Integer getSuccessCount() {
		return successCount;
	}

	/**
	 * The number of total accounts submitted
	 *
	 * @return number of total accounts submitted
	 */
	public Integer getTotalCount() {
		return totalCount;
	}
}
