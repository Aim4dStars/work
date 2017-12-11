package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.List;

import org.joda.time.DateTime;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListResult;
import com.bt.nextgen.service.ServiceErrors;

public interface CorporateActionServices {
	/**
	 * Concurrently load voluntary corporate actions for investment accounts and super accounts
	 *
	 * @param startDate     the search start date
	 * @param endDate       the search end date
	 * @param accountIds    the optional account ID's
	 * @param serviceErrors service errors object
	 * @return voluntary corporate action list result
	 */
	CorporateActionListResult loadVoluntaryCorporateActions(DateTime startDate, DateTime endDate, List<String> accountIds,
															ServiceErrors serviceErrors);

	/**
	 * Load voluntary corporate actions for DG/IM
	 *
	 * @param startDate        the search start date
	 * @param endDate          the search end date
	 * @param portfolioModelId the portfolio ID
	 * @param serviceErrors    service errors object
	 * @return voluntary corporate action list result
	 */
	CorporateActionListResult loadVoluntaryCorporateActionsForIm(String imId, DateTime startDate, DateTime endDate, String portfolioModelId,
																 ServiceErrors serviceErrors);

	/**
	 * Concurrently load mandatory corporate actions for investment accounts and super accounts
	 *
	 * @param startDate     the search start date
	 * @param endDate       the search end date
	 * @param accountIds    the optional account ID's
	 * @param serviceErrors service errors object
	 * @return mandatory corporate action list result
	 */
	CorporateActionListResult loadMandatoryCorporateActions(DateTime startDate, DateTime endDate, List<String> accountIds,
															ServiceErrors serviceErrors);

	/**
	 * Load mandatory corporate actions for investment accounts and super accounts
	 *
	 * @param startDate        the search start date
	 * @param endDate          the search end date
	 * @param portfolioModelId the portfolio ID
	 * @param serviceErrors    service errors object
	 * @return mandatory corporate action list result
	 */
	CorporateActionListResult loadMandatoryCorporateActionsForIm(String imId, DateTime startDate, DateTime endDate, String portfolioModelId,
																 ServiceErrors serviceErrors);

	/**
	 * Load voluntary corporate actions for trustee/IRG approval
	 *
	 * @param startDate     the search start date
	 * @param endDate       the search end date
	 * @param serviceErrors service errors object
	 * @return voluntary corporate action list result
	 */
	CorporateActionListResult loadVoluntaryCorporateActionsForApproval(DateTime startDate, DateTime endDate, ServiceErrors serviceErrors);

	/**
	 * Concurrently load corporate action details and related accounts
	 *
	 * @param orderNumber   CA order number
	 * @param summaryOnly   return CA summary only.  ie. no account details
	 * @param serviceErrors service errors
	 * @return CorporateActionContext
	 */
	CorporateActionContext loadCorporateActionDetailsContext(String orderNumber, Boolean summaryOnly, ServiceErrors serviceErrors);

	/**
	 * Concurrently load corporate action details and related IM accounts
	 *
	 * @param imId          investment manager/dealer group ID
	 * @param orderNumber   CA order number
	 * @param serviceErrors service errors
	 * @return CorporateActionContext
	 */
	CorporateActionContext loadCorporateActionDetailsContextForIm(String imId, String orderNumber, ServiceErrors serviceErrors);
}
