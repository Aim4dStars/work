package com.bt.nextgen.service.integration.corporateaction;

import java.util.List;

import org.joda.time.DateTime;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;

/**
 * Corporate action integration service
 */
public interface CorporateActionIntegrationService {
	/**
	 * Loads a list of voluntary corporate actions for a given date range.
	 *
	 * @param startDate     the start date of search (optional)
	 * @param endDate       the to date of search (optional)
	 * @param accountIds    a list of account ID's (optional)
	 * @param serviceErrors the service errors object
	 * @return a list of corporate action objects.  Returns null if no results.
	 */
	List<CorporateAction> loadVoluntaryCorporateActions(DateTime startDate, DateTime endDate, List<String> accountIds,
														ServiceErrors serviceErrors);

	/**
	 * Loads a list of voluntary corporate actions for super for a given date range.
	 *
	 * @param startDate     the start date of search (optional)
	 * @param endDate       the to date of search (optional)
	 * @param accountIds    a list of account ID's (optional)
	 * @param serviceErrors the service errors object
	 * @return a list of corporate action objects.  Returns null if no results.
	 */
	List<CorporateAction> loadVoluntaryCorporateActionsForSuper(DateTime startDate, DateTime endDate, List<String> accountIds,
																ServiceErrors serviceErrors);

	/**
	 * Loads a list of voluntary corporate actions applicable to trustee and IRG for a given date range.
	 *
	 * @param startDate     the start date of search (optional)
	 * @param endDate       the to date of search (optional)
	 * @param serviceErrors the service errors object
	 * @return a list of corporate action objects.  Returns null if no results.
	 */
	List<CorporateAction> loadVoluntaryCorporateActionsForApproval(final DateTime startDate, final DateTime endDate,
																   final ServiceErrors serviceErrors);

	/**
	 * Loads a list of mandatory corporate actions for a given date range.
	 *
	 * @param startDate     the start date of search (optional)
	 * @param endDate       the to date of search (optional)
	 * @param serviceErrors the service errors object
	 * @return a list of corporate action objects.  Returns null if no results.
	 */
	List<CorporateAction> loadMandatoryCorporateActions(DateTime startDate, DateTime endDate, List<String> accountId,
														ServiceErrors serviceErrors);

	/**
	 * Loads a list of mandatory corporate actions for super for a given date range.
	 *
	 * @param startDate     the start date of search (optional)
	 * @param endDate       the to date of search (optional)
	 * @param serviceErrors the service errors object
	 * @return a list of corporate action objects.  Returns null if no results.
	 */
	List<CorporateAction> loadMandatoryCorporateActionsForSuper(DateTime startDate, DateTime endDate, List<String> accountId,
																ServiceErrors serviceErrors);

	/**
	 * Load corporate action summary for a given order number.
	 *
	 * @param orderNumber   the order number
	 * @param serviceErrors the service error object
	 * @return corporate action response object
	 */
	CorporateActionDetailsResponse loadCorporateActionDetails(String orderNumber, ServiceErrors serviceErrors);

	/**
	 * Loads a list of corporate action's accounts for selected corporate action.
	 *
	 * @param orderNumber   the order number
	 * @param serviceErrors the service errors object
	 * @return a list of corporate action objects.  Returns null if no results.
	 */
	List<CorporateActionAccount> loadCorporateActionAccountsDetails(String orderNumber, ServiceErrors serviceErrors);

	/**
	 * Loads a list of corporate action's accounts for selected corporate action for the IM/DG.
	 *
	 * @param imId          the IM/DG ID
	 * @param orderNumber   the order number
	 * @param serviceErrors the service errors object
	 * @return a list of corporate action objects.  Returns null if no results.
	 */
	List<CorporateActionAccount> loadCorporateActionAccountsDetailsForIm(final String imId, final String orderNumber,
																		 final ServiceErrors serviceErrors);

	/**
	 * Loads a list of corporate action account's transaction details
	 *
	 * @param orderNumbers  List of the order numbers including cascade oder number in case of multiblock
	 * @param serviceErrors the service errors object
	 * @return a list of corporate action objects.  Returns null if no results.
	 */
	List<CorporateActionTransactionDetails> loadCorporateActionTransactionDetails(List<String> orderNumbers, ServiceErrors serviceErrors);

	/**
	 * Loads a list of corporate action account's transaction details for IM
	 *
	 * @param imId          the IM/DG ID
	 * @param orderNumbers  List of the order numbers including cascade oder number in case of multiblock
	 * @param serviceErrors the service errors object
	 * @return a list of corporate action objects.  Returns null if no results.
	 */
	List<CorporateActionTransactionDetails> loadCorporateActionTransactionDetailsForIm(final String imId,
																					   final List<String> orderNumbers,
																					   final ServiceErrors serviceErrors);

	/**
	 * Retrieve count for pending corporate events.
	 *
	 * @param accountsIds   List of the order numbers including cascade oder number in case of multiblock
	 * @param serviceErrors the service errors object
	 * @return corporate action object.
	 */
	CorporateAction getCountForPendingCorporateEvents(List<AccountKey> accountsIds, DateTime fromDate,
													  DateTime toDate, ServiceErrors serviceErrors);

	/**
	 * Load DRP Corporate Actions
	 *
	 * @param startDate     the ex start date
	 * @param endDate       the ex end date
	 * @param accountIds    the account ID's of interest
	 * @param serviceErrors the service errors bject
	 * @return a list of corporate actions for the given account.  Null if no result.
	 */
	List<CorporateAction> loadDrpCorporateActions(final DateTime startDate, final DateTime endDate, final List<String> accountIds,
												  final ServiceErrors serviceErrors);
}
