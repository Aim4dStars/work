package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v1.model.DepositDto;
import com.bt.nextgen.api.account.v1.service.DepositDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.Renderable;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

@Report("depositPdfReceipt")
public class DepositReceipt extends AccountReport
{

	private static final String DISCLAIMER_CONTENT = "DS-IP-0029";

	@Autowired
	private DepositDtoService depositService;

	@Autowired
	private ContentDtoService contentService;

	@ReportBean("disclaimer")
	public String getDisclaimer(Map <String, String> params)
	{
		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
		ContentDto content = contentService.find(key, serviceErrors);
		return content.getContent();
	}

	@ReportBean("depositReceipt")
	public DepositDto getTransactions(Map <String, String> params)
	{
		DepositDto resultList = new DepositDto();

		String receiptNo = EncodedString.toPlainText(params.get("receiptNo")).toString();

		Map <String, DepositDto> mapDeposits = depositService.loadDepositReciepts();

		DepositDto depositObj = mapDeposits.get(receiptNo);

        if (StringUtils.isNotBlank(depositObj.getFrequency()))
		{

			if ((!depositObj.getEndRepeat().equals("setDate")) && (!depositObj.getEndRepeat().equals("setNumber")))
			{

				depositObj.setRepeatEndDate("No end date");
			}
		}

		else
		{
			depositObj.setRepeatEndDate("");
		}

		depositObj.setUpdatedBsbFromPayDto(ApiFormatter.formatBsb(depositObj.getFromPayDto().getCode()));
		depositObj.setUpdatedBsbToPayteeDto(ApiFormatter.formatBsb(depositObj.getToPayteeDto().getCode()));

		resultList = depositObj;

		return resultList;
	}

	/*@Override
	public Collection <? > getData()
	{
		return null;
	}*/

	@ReportBean("reportType")
	public String getReportName(Map <String, String> params)
	{
		return "Deposit Receipt";
	}

	@ReportImage("paymentFromToIcon")
	public Renderable getPaymentFromToIcon(Map <String, String> params) throws JRException, IOException
	{

        String imageLocation = cmsService.getContent("paymentFromToIcon");
        return getRasterImage(imageLocation);
	}

}
