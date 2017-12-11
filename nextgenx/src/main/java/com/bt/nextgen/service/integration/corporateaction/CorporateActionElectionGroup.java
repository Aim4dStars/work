package com.bt.nextgen.service.integration.corporateaction;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;

import java.util.List;

public interface CorporateActionElectionGroup {
	String getOrderNumber();

	String getIpsId();

	void setIpsId(String ipsId);

	/**
	 * @deprecated
	 * To be removed as it is only used for test cases
	 */
	@Deprecated
	CorporateActionSelectedOptionDto getSelectedOption();

	List<CorporateActionPosition> getPositions();

	List<CorporateActionOption> getOptions();

	List<CorporateActionValidationError> getValidationErrors();
}
