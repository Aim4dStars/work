package com.bt.nextgen.service.avaloq.fundpaymentnotice;

import com.bt.nextgen.reports.service.ReportGenerationServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNotice;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNoticeIntegrationService;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNoticeRequest;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNoticeResponse;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author L070354
 *
 * Implementation class for the FundPaymentNoticeIntegrationService
 */

@Service
public class FundPaymentNoticeIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
	FundPaymentNoticeIntegrationService
{

	private static final Logger logger = LoggerFactory.getLogger(FundPaymentNoticeIntegrationServiceImpl.class);

	@Autowired
	private AvaloqExecute avaloqExecute;
	@Autowired
	private AvaloqGatewayHelperService webserviceClient;

	/**
	 * This method will load all Fund Payment Notices through Avaloq Service <b>BTFG$UI_FUND_NOTICE.DOC#DET</b> 
	 *
	 * @param request - The Request Object for the Fund Payment Notice
	 * @param serviceErrors- Errors
	 * @return response - The response object
	 */
	public List<FundPaymentNotice> getFundPaymentNoticeDetails(FundPaymentNoticeRequest request, ServiceErrors serviceErrors)
	{

		logger.debug("Entered getFundPaymentNoticeDetails Method");

		AvaloqReportRequest reportRequest = new AvaloqReportRequest(Template.FUND_PAYMENT_NOTICE.getName()).forDateTimeOptional(
			ReportGenerationServiceImpl.PARAM_EX_DATE_FROM,
			request.getStartDate())
			.forDateTimeOptional(ReportGenerationServiceImpl.PARAM_EX_DATE_TO, request.getEndDate())
			.forAssetsOptional(request.getAssetIds());

		FundPaymentNoticeResponse response = avaloqExecute.executeReportRequestToDomain(reportRequest,
			FundPaymentNoticeResponseImpl.class,
			serviceErrors);
		return response != null ? response.getFundPaymentNotice() : new ArrayList<FundPaymentNotice>();

	}

}
