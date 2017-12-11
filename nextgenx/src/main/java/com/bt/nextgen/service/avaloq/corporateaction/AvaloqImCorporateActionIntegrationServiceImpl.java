package com.bt.nextgen.service.avaloq.corporateaction;

import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.reports.service.ReportGenerationServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.corporateaction.CorporateAction;
import com.bt.nextgen.service.integration.corporateaction.ImCorporateActionIntegrationService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Avaloq implementation of corporate action integration service
 */
@Service
public class AvaloqImCorporateActionIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
		ImCorporateActionIntegrationService {
	private static final Logger logger = LoggerFactory.getLogger(AvaloqImCorporateActionIntegrationServiceImpl.class);

	@Autowired
	private AvaloqExecute avaloqExecute;

	@Autowired
	private Validator validator;

	/**
	 * Load voluntary corporate actions
	 *
	 * @param startDate     the start date of search (optional)
	 * @param endDate       the to date of search (optional)
	 * @param serviceErrors the service errors object
	 * @return
	 */
	@Override
	public List<CorporateAction> loadVoluntaryCorporateActions(final String imId, final DateTime startDate, final DateTime endDate,
															   final String portfolioModelId, final ServiceErrors serviceErrors) {
		logger.info("Loading voluntary corporate actions");
		return new IntegrationSingleOperation<List<CorporateAction>>("loadVoluntaryCorporateActionsForDgIm", serviceErrors) {
			@Override
			public List<CorporateAction> performOperation() {
				final AvaloqReportRequest req = new AvaloqReportRequest(Template.CORPORATE_ACTIONS_VOLUNTARY_IM.getName());
				req.forInvestmentManagerOeId(imId);
				if (startDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_DEADLINE_DATE_FROM, startDate);
				}
				if (endDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_DEADLINE_DATE_TO, endDate);
				}
				if (StringUtils.isNotEmpty(portfolioModelId)) {
					req.forInvestmentPolicyStatementId(portfolioModelId);
				}
				return validateCorporateActionResponse(avaloqExecute.executeReportRequestToDomain(req, CorporateActionResponseImpl.class,
						serviceErrors), serviceErrors);
			}
		}.run();
	}

	/**
	 * Load mandatory corporate actions
	 *
	 * @param startDate     the start date of search (optional)
	 * @param endDate       the to date of search (optional)
	 * @param serviceErrors the service errors object
	 * @return
	 */
	@Override
	public List<CorporateAction> loadMandatoryCorporateActions(final String imId, final DateTime startDate, final DateTime endDate,
															   final String portfolioModelId,
															   final ServiceErrors serviceErrors) {
		logger.info("Loading mandatory corporate actions");
		return new IntegrationSingleOperation<List<CorporateAction>>("loadMandatoryCorporateActions", serviceErrors) {
			@Override
			public List<CorporateAction> performOperation() {
				final AvaloqReportRequest req = new AvaloqReportRequest(Template.CORPORATE_ACTIONS_MANDATORY_IM.getName());
				req.forInvestmentManagerOeId(imId);
				if (startDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_EXTERNAL_DATE_FROM, startDate);
				}
				if (endDate != null) {
					req.forDateTime(ReportGenerationServiceImpl.PARAM_EXTERNAL_DATE_TO, endDate);
				}
				if (StringUtils.isNotEmpty(portfolioModelId)) {
					req.forInvestmentPolicyStatementId(portfolioModelId);
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
}
