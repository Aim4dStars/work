package com.bt.nextgen.service.integration.corporateaction;

import java.util.List;

/**
 * Corporate action response interface
 */
public interface CorporateActionResponse {
	/**
	 * Return a list of corporate actions
	 *
	 * @return list of corporate actions
	 */
	List<CorporateAction> getCorporateActions();
}
