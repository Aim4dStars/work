package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.service.CashTransactionHistoryReportService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.bt.nextgen.core.web.ApiFormatter;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.Renderable;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 * Java bean class for past transaction receipt pdf generation
 *
 * @deprecated use {@link com.bt.nextgen.reports.account.transactions.PastTransactionReceiptV2} instead.
 */
@Report("pastTransactionReceipt")
@Deprecated
public class PastTransactionReceipt extends AccountReport {

    private static final String DISCLAIMER_CONTENT = "DS-IP-0029";
    private static final String INFORMATION_CONTENT = "DS-IP-0057";

    @Autowired
    private CashTransactionHistoryReportService pastTransactionReportService;

    @Autowired
    private ContentDtoService contentService;

    @Autowired
    private UserProfileService userProfileService;

    @ReportBean("pastTransactionDto")
    public CashTransactionHistoryDto retrievePastTransaction(Map<String, String> params) {
        String accountId = params.get("account-id");
        String direction = params.get("direction");
        String startDate = params.get("startDate");
        String endDate = params.get("endDate");
        String receiptNo = EncodedString.toPlainText(params.get("receiptNo")).toString();

        DateTime startDateTime = parseDate(startDate);
        DateTime endDateTime = parseDate(endDate);

        return pastTransactionReportService.retrievePastTransaction(accountId, direction, startDateTime, endDateTime, receiptNo);
    }

    @ReportBean("moreInformation")
    public String getMoreInfo(Map<String, String> params) {
        String adviser = "your adviser";

        if (userProfileService.getActiveProfile().getJobRole() == JobRole.INVESTOR) {
            adviser = getAccount(params).iterator().next().getAdviserName();
        }

        return cmsService.getDynamicContent(INFORMATION_CONTENT, new String[] { adviser });
    }

    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, String> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    @ReportBean("reportType")
    public String getReportName(Map<String, String> params) {
        return "Past Transaction";
    }

    @ReportImage("paymentFromToIcon")
    public Renderable getPaymentFromToIcon(Map<String, String> params) throws JRException, IOException {
        String imageLocation = cmsService.getContent("paymentFromToIcon");
        return getRasterImage(imageLocation);
    }

    private DateTime parseDate(String date) {
        DateTime dateTime = new DateTime();
        if (date.length() > 14) {
            dateTime = ApiFormatter.parseDateTimeToDayMonthDateYearPattern(date);
        }

        return dateTime;
    }
}
