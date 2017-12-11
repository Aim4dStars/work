package com.bt.nextgen.service.integration.cashcategorisation.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.cashcategorisation.builder.MemberContributionConverter;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationAction;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import com.bt.nextgen.service.integration.cashcategorisation.model.CategorisableCashTransaction;
import com.bt.nextgen.service.integration.cashcategorisation.model.CategorisableCashTransactionImpl;
import com.bt.nextgen.service.integration.cashcategorisation.model.Contribution;
import com.btfin.abs.trxservice.cashcat.v1_0.CashCatRsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service("CashCategorisationIntegrationServiceImpl")
public class CashCategorisationIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
	CashCategorisationIntegrationService
{
	@Autowired
	private AvaloqGatewayHelperService webServiceClient;

	@Autowired
	private AvaloqExecute avaloqExecute;


	/**
	 * Saves Member level contributions against a single
	 *
	 * @param action
	 * @param cashTransToSplit
	 * @return
	 */
	@Override
	public TransactionStatus saveOrUpdate(final CashCategorisationAction action,
										  final CategorisableCashTransaction cashTransToSplit)
	{
		return new AbstractAvaloqIntegrationService.IntegrationSingleOperation <TransactionStatus>("saveOrUpdate",
				new ServiceErrorsImpl())
		{
			@Override
			public TransactionStatus performOperation()
			{
				CashCatRsp rsp = webServiceClient.sendToWebService(MemberContributionConverter.toCashCategorisationRequest(action,
								cashTransToSplit),
						AvaloqOperation.CASH_CAT_REQ,
						new ServiceErrorsImpl());

				TransactionStatus status = MemberContributionConverter.toCashCategorisationResponse(rsp);

				return status;
			}
		}.run();
	}


	@Override
	public List <Contribution> loadCashContributionsForTransaction(String depositId, ServiceErrors serviceErrors)
	{
		AvaloqReportRequest req = new AvaloqReportRequest(Template.CASH_CONTRIBUTIONS_FOR_DOC.getName());

		if (depositId != null)
		{
			req = req.forDocumentIdList(Collections.singletonList(depositId));
		}

		final CategorisableCashTransactionImpl contributions = avaloqExecute.executeReportRequestToDomain(req,
															CategorisableCashTransactionImpl.class, serviceErrors);

		return (ArrayList <Contribution>)contributions.getContributionSplit();
	}


	@Override
	public List <Contribution> loadCashContributionsForAccount(AccountKey accountKey, Date financialYearDate, CashCategorisationType category, ServiceErrors serviceErrors)
	{
		AvaloqReportRequest req = new AvaloqReportRequest(Template.CASH_CONTRIBUTIONS_FOR_BP.getName());

		if (financialYearDate != null && accountKey != null)
		{
			req = req.forBpNrListVal(Collections.singletonList(accountKey.getId()));
			req = req.forFinancialYear(financialYearDate);
		}

		if (category != null)
		{
			req = req.forTransactionCategory("21");
		}

		final CategorisableCashTransactionImpl contributions = avaloqExecute.executeReportRequestToDomain(req,
															CategorisableCashTransactionImpl.class, serviceErrors);

		return (ArrayList <Contribution>)contributions.getContributionSplit();
	}
}