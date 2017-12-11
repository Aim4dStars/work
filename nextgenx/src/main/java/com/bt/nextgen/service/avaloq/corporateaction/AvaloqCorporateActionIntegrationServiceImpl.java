package com.bt.nextgen.service.avaloq.corporateaction;

import java.util.List;

import com.btfin.panorama.core.validation.Validator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.reports.service.ReportGenerationServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateAction;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetailsResponse;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * Avaloq implementation of corporate action integration service
 */
@Service
public class AvaloqCorporateActionIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
		CorporateActionIntegrationService {
	@Autowired
	private AvaloqExecute avaloqExecute;

	@Autowired
	private Validator validator;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CorporateAction> loadVoluntaryCorporateActions(final DateTime startDate, final DateTime endDate,
															   final List<String> accountIds,
															   final ServiceErrors serviceErrors) {
		return loadVoluntaryCorporateActionsCommon(startDate, endDate, accountIds, Template.CORPORATE_ACTIONS_VOLUNTARY, serviceErrors);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CorporateAction> loadVoluntaryCorporateActionsForSuper(final DateTime startDate, final DateTime endDate,
																	   final List<String> accountIds,
																	   final ServiceErrors serviceErrors) {
		return loadVoluntaryCorporateActionsCommon(startDate, endDate, accountIds, Template.CORPORATE_ACTIONS_VOLUNTARY_SUPER,
				serviceErrors);
	}

	private List<CorporateAction> loadVoluntaryCorporateActionsCommon(final DateTime startDate, final DateTime endDate,
																	  final List<String> accountIds, final Template template,
																	  final ServiceErrors serviceErrors) {
		return new IntegrationSingleOperation<List<CorporateAction>>(template.name(), serviceErrors) {
			@Override
			public List<CorporateAction> performOperation() {
				final AvaloqReportRequest req = new AvaloqReportRequest(template.getName());
				if (startDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_DEADLINE_DATE_FROM, startDate);
				}
				if (endDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_DEADLINE_DATE_TO, endDate);
				}
				if (accountIds != null && !accountIds.contains(null)) {
					req.forBpList(accountIds);
				}

				return validateCorporateActionResponse(avaloqExecute.executeReportRequestToDomain(req, CorporateActionResponseImpl.class,
						serviceErrors), serviceErrors);
			}
		}.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CorporateAction> loadVoluntaryCorporateActionsForApproval(final DateTime startDate, final DateTime endDate,
																		  final ServiceErrors serviceErrors) {
		return new IntegrationSingleOperation<List<CorporateAction>>(Template.CORPORATE_ACTIONS_VOLUNTARY_TRUSTEE.name(), serviceErrors) {
			@Override
			public List<CorporateAction> performOperation() {
				final AvaloqReportRequest req = new AvaloqReportRequest(Template.CORPORATE_ACTIONS_VOLUNTARY_TRUSTEE.getName());
				if (startDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_DEADLINE_DATE_FROM, startDate);
				}
				if (endDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_DEADLINE_DATE_TO, endDate);
				}

				return validateCorporateActionResponse(avaloqExecute.executeReportRequestToDomain(req, CorporateActionResponseImpl.class,
						serviceErrors), serviceErrors);
			}
		}.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CorporateAction> loadMandatoryCorporateActions(final DateTime startDate, final DateTime endDate,
															   final List<String> accountIds,
															   final ServiceErrors serviceErrors) {
		return loadMandatoryCorporateActionsCommon(startDate, endDate, accountIds, Template.CORPORATE_ACTIONS_MANDATORY, serviceErrors);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CorporateAction> loadMandatoryCorporateActionsForSuper(final DateTime startDate, final DateTime endDate,
																	   final List<String> accountIds,
																	   final ServiceErrors serviceErrors) {
		return loadMandatoryCorporateActionsCommon(startDate, endDate, accountIds, Template.CORPORATE_ACTIONS_MANDATORY_SUPER,
				serviceErrors);
	}

	private List<CorporateAction> loadMandatoryCorporateActionsCommon(final DateTime startDate, final DateTime endDate,
																	  final List<String> accountIds, final Template template,
																	  final ServiceErrors serviceErrors) {
		return new IntegrationSingleOperation<List<CorporateAction>>("loadMandatoryCorporateActions", serviceErrors) {
			@Override
			public List<CorporateAction> performOperation() {
				final AvaloqReportRequest req = new AvaloqReportRequest(template.getName());
				if (startDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_EXTERNAL_DATE_FROM, startDate);
				}

				if (endDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_EXTERNAL_DATE_TO, endDate);
				}

				if (accountIds != null && !accountIds.contains(null)) {
					req.forBpList(accountIds);
				}

				return validateCorporateActionResponse(avaloqExecute.executeReportRequestToDomain(req, CorporateActionResponseImpl.class,
						serviceErrors), serviceErrors);
			}
		}.run();
	}

	private List<CorporateAction> validateCorporateActionResponse(CorporateActionResponseImpl response, ServiceErrors serviceErrors) {
		if (response.getCorporateActions() != null) {
			for (CorporateAction ca : response.getCorporateActions()) {
				validator.validate(ca, serviceErrors);
			}
		}

		return response.getCorporateActions();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public CorporateActionDetailsResponse loadCorporateActionDetails(final String orderNumber, final ServiceErrors serviceErrors) {
		return new IntegrationSingleOperation<CorporateActionDetailsResponse>("loadCorporateActionSummary", serviceErrors) {
			@Override
			public CorporateActionDetailsResponse performOperation() {
				final AvaloqReportRequest req = new AvaloqReportRequest(Template.CORPORATE_ACTION_DETAILS.getName());
				req.forDocId(orderNumber);

				return validateCorporateActionDetails(
						avaloqExecute.executeReportRequestToDomain(req, CorporateActionDetailsResponseImpl.class,
								serviceErrors), serviceErrors);
			}
		}.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CorporateActionAccount> loadCorporateActionAccountsDetails(final String orderNumber, final ServiceErrors serviceErrors) {
		return new IntegrationSingleOperation<List<CorporateActionAccount>>("loadCorporateActionAccountsDetails", serviceErrors) {
			@Override
			public List<CorporateActionAccount> performOperation() {
				final AvaloqReportRequest req = new AvaloqReportRequest(Template.CORPORATE_ACTION_ACCOUNTS.getName());
				req.forDocId(orderNumber);

				final CorporateActionAccountResponseImpl response =
						avaloqExecute.executeReportRequestToDomain(req, CorporateActionAccountResponseImpl.class,
								serviceErrors);
				return validate(response, serviceErrors);
			}
		}.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CorporateActionAccount> loadCorporateActionAccountsDetailsForIm(final String imId, final String orderNumber,
																				final ServiceErrors serviceErrors) {
		return new IntegrationSingleOperation<List<CorporateActionAccount>>("loadCorporateActionAccountsDetailsForIm", serviceErrors) {
			@Override
			public List<CorporateActionAccount> performOperation() {
				final AvaloqReportRequest req = new AvaloqReportRequest(Template.CORPORATE_ACTION_ACCOUNTS_IM.getName());
				req.forDocId(orderNumber);
				req.forInvestmentManagerOeId(imId);

				final CorporateActionAccountResponseImpl response =
						avaloqExecute.executeReportRequestToDomain(req, CorporateActionAccountResponseImpl.class,
								serviceErrors);
				return validate(response, serviceErrors);
			}
		}.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CorporateActionTransactionDetails> loadCorporateActionTransactionDetails(final List<String> orderNumbers,
																						 final ServiceErrors serviceErrors) {
		return new IntegrationSingleOperation<List<CorporateActionTransactionDetails>>("CorporateActionTransactionDetails", serviceErrors) {
			@Override
			public List<CorporateActionTransactionDetails> performOperation() {
				final AvaloqReportRequest req = new AvaloqReportRequest(Template.CORPORATE_ACTION_PARTICIPATION.getName());
				req.forRefDocListId(orderNumbers);

				final CorporateActionTransactionDetailsResponseImpl response =
						avaloqExecute.executeReportRequestToDomain(req, CorporateActionTransactionDetailsResponseImpl.class,
								serviceErrors);
				return validateCorporateActionTransactionDetails(response, serviceErrors);
			}
		}.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CorporateActionTransactionDetails> loadCorporateActionTransactionDetailsForIm(final String imId,
																							  final List<String> orderNumbers,
																							  final ServiceErrors serviceErrors) {
		return new IntegrationSingleOperation<List<CorporateActionTransactionDetails>>("CorporateActionTransactionDetailsForIm",
				serviceErrors) {
			@Override
			public List<CorporateActionTransactionDetails> performOperation() {
                final AvaloqReportRequest req = new AvaloqReportRequest(Template.CORPORATE_ACTION_PARTICIPATION_IM.getName());
				req.forRefDocListId(orderNumbers);
				req.forInvestmentManagerOeId(imId);

				final CorporateActionTransactionDetailsResponseImpl response =
						avaloqExecute.executeReportRequestToDomain(req, CorporateActionTransactionDetailsResponseImpl.class,
								serviceErrors);
				return validateCorporateActionTransactionDetails(response, serviceErrors);
			}
		}.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CorporateAction> loadDrpCorporateActions(final DateTime startDate, final DateTime endDate, final List<String> accountIds,
														 final ServiceErrors serviceErrors) {
		return new IntegrationSingleOperation<List<CorporateAction>>("loadDrpCorporateActions", serviceErrors) {
			@Override
			public List<CorporateAction> performOperation() {
				final AvaloqReportRequest req = new AvaloqReportRequest(Template.CORPORATE_ACTIONS_DRP_LIST.getName());
				if (startDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_EX_DATE_FROM, startDate);
				}
				if (endDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_EX_DATE_TO, endDate);
				}
				if (accountIds != null && !accountIds.contains(null)) {
					req.forBpList(accountIds);
				}
				return validateCorporateActionResponse(avaloqExecute.executeReportRequestToDomain(req, CorporateActionResponseImpl.class,
						serviceErrors), serviceErrors);
			}
		}.run();
	}

	/* Retrieve count for pending corporate events.
	 *
	 * @param accountsIds   List of the order numbers including cascade oder number in case of multiblock
	 * @param serviceErrors the service errors object
	 * @return corporate action object.
	 */
	@Override
	public CorporateAction getCountForPendingCorporateEvents(final List<AccountKey> accountsIds, final DateTime fromDate,
															 final DateTime toDate, final ServiceErrors serviceErrors) {
		return new IntegrationSingleOperation<CorporateAction>("getCountForPendingCorporateEvents", serviceErrors) {
			@Override
			public CorporateAction performOperation() {
				final AvaloqReportRequest req = new AvaloqReportRequest(Template.CORPORATE_ACTION_PENDING_COUNT.getName());
				req.forBpList(extract(accountsIds, on(AccountKey.class).getId()));

				if (fromDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_DEADLINE_DATE_FROM, fromDate);
				}
				if (toDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_DEADLINE_DATE_TO, toDate);
				}

				final CorporateActionImpl corporateAction =
						avaloqExecute.executeReportRequestToDomain(req, CorporateActionImpl.class,
								serviceErrors);

				return corporateAction;
			}
		}.run();
	}

	private CorporateActionDetailsResponse validateCorporateActionDetails(CorporateActionDetailsResponseImpl cas,
																		  ServiceErrors serviceErrors) {
		validator.validate(cas, serviceErrors);

		return cas;
	}

	private List<CorporateActionAccount> validate(CorporateActionAccountResponseImpl response, ServiceErrors serviceErrors) {
		if (response.getCorporateActionAccounts() != null) {
			for (CorporateActionAccount ca : response.getCorporateActionAccounts()) {
				validator.validate(ca, serviceErrors);
			}
		}

		return response.getCorporateActionAccounts();
	}

	private List<CorporateActionTransactionDetails> validateCorporateActionTransactionDetails(
			CorporateActionTransactionDetailsResponseImpl response, ServiceErrors serviceErrors) {
		if (response.getCorporateActionTransactionDetails() != null) {
			for (CorporateActionTransactionDetails ca : response.getCorporateActionTransactionDetails()) {
				validator.validate(ca, serviceErrors);
			}
		}

		return response.getCorporateActionTransactionDetails();
	}
}
