package com.bt.nextgen.service.integration.corporateaction;

import com.bt.nextgen.service.ServiceErrors;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Corporate action integration service
 */
public interface ImCorporateActionIntegrationService {
	/**
	 * Loads a list of voluntary corporate actions for a given date range for an investment manager/dealer group
	 * (this may need to be tweaked to support DG)
	 *
	 * @param imId             investment manager ID
	 * @param startDate        the start date of search (optional)
	 * @param endDate          the to date of search (optional)
	 * @param serviceErrors    the service errors object
	 * @param portfolioModelId the portfolio model id (ips_inc_id)
	 * @param serviceErrors    the service errors object
	 * @return a list of corporate action objects.  Returns null if no results.
	 */
	List<CorporateAction> loadVoluntaryCorporateActions(final String imId, final DateTime startDate, final DateTime endDate,
														final String portfolioModelId, final ServiceErrors serviceErrors);

	/**
	 * Loads a list of mandatory corporate actions for a given date range for an investment manager/dealer group
	 * (this may need to be tweaked to support DG)
	 *
	 * @param imId             investment manager ID
	 * @param startDate        the start date of search (optional)
	 * @param endDate          the to date of search (optional)
	 * @param serviceErrors    the service errors object
	 * @param portfolioModelId the portfolio model id (ips_inc_id)
	 * @param serviceErrors    the service errors object
	 * @return a list of corporate action objects.  Returns null if no results.
	 */
	List<CorporateAction> loadMandatoryCorporateActions(final String imId, final DateTime startDate, final DateTime endDate,
														final String portfolioModelId,
														final ServiceErrors serviceErrors);
}
