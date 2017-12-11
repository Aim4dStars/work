package com.bt.nextgen.service.integration.corporateaction;

import java.util.List;

/**
 * Corporate action summary response interface
 */
public interface CorporateActionDetailsResponse {
	/**
	 * Return a list of corporate action summaries
	 *
	 * @return list of corporate action summarys
	 */
	List<CorporateActionDetails> getCorporateActionDetailsList();
}
