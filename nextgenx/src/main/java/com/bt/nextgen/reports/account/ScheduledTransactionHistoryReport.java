package com.bt.nextgen.reports.account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.transaction.model.TransactionDto;
import com.bt.nextgen.api.transaction.service.TransactionDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.Renderable;

@Report("scheduledTransactionHistoryReport")
public class ScheduledTransactionHistoryReport extends AccountReport {
    private static final String DISCLAIMER_CONTENT = "DS-IP-0029";
    private static final String INFORMATION_CONTENT = "DS-IP-0057";
    private static final String TXN_FAILED_RETRY_CONTENT = "Ins-IP-0036";
    private static final String TXN_FAILED_RECURRING_CONTENT = "Ins-IP-0037";
    private static final String TXN_FAILED_FINAL_CONTENT = "Ins-IP-0071";

    // TODO switch over to transactiondtoservice/transactiondto when issues around these two classes have been fixed
    @Autowired
    private TransactionDtoService transactionService;

    @Autowired
    private ContentDtoService contentService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

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

    @ReportBean("scheduledTransactions")
    public TransactionDto getTransactions(Map<String, String> params) {

        String accountId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);

        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
        criteria.add(new ApiSearchCriteria(Attribute.PORTFOLIO_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.TRANSACTION_TYPE, SearchOperation.EQUALS, Attribute.SCHEDULED_TRANSACTIONS,
                OperationType.STRING));

        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        List<TransactionDto> resultList = transactionService.search(criteria, serviceErrors);
        TransactionDto desiredList = null;

        for (TransactionDto transactionDto : resultList) {

            if (EncodedString.toPlainText(transactionDto.getTransactionKey().getTransactionId())
                    .equals(EncodedString.toPlainText(params.get("transactionId")))) {
                if (transactionDto.getMetaType().equals("Payment")) {
                    transactionDto.setMetaTypeDisplay("Payment of");
                } else {
                    transactionDto.setMetaTypeDisplay("Deposit of");
                }
                this.populateMessages(transactionDto);
                transactionDto.setPayeeBsb(ApiFormatter.formatBsb(transactionDto.getPayeeBsb()));
                transactionDto.setPayerBsb(ApiFormatter.formatBsb(transactionDto.getPayerBsb()));

                desiredList = transactionDto;

            }
        }

        return desiredList;

    }

    @ReportBean("reportType")
    public String getReportName(Map<String, String> params) {
        return "Scheduled transaction history";
    }

    @ReportImage("paymentFromToIcon")
    public Renderable getPaymentFromToIcon(Map<String, String> params) throws JRException, IOException {
        String imageLocation = cmsService.getContent("paymentFromToIcon");
        return getRasterImage(imageLocation);
    }

    /**
     * This is a refactored method which populates error message based on the status and frequency of the scheduled transaction
     * @param transactionDto
     */
    private void populateMessages(TransactionDto transactionDto) {
        if (transactionDto.getTransactionStatus() != null && !transactionDto.getTransactionStatus().isEmpty()) {
            if ("SCHEDULED".equalsIgnoreCase(transactionDto.getTransactionStatus())) {
                transactionDto.setMessage("scheduled");
                transactionDto.setErrorMessage("");
            } else if ("REJECTED".equalsIgnoreCase(transactionDto.getTransactionStatus())) {
                transactionDto.setMessage("failed");
                if (transactionDto.getLastPayment() != null && transactionDto.getLastPayment().toDate().before(bankDateIntegrationService.getBankDate(new FailFastErrorsImpl()).toDate())) {
                    transactionDto.setErrorMessage(cmsService.getContent(TXN_FAILED_FINAL_CONTENT));
                } else if (transactionDto.getFrequency() != null && !transactionDto.getFrequency().isEmpty()) {
                    this.computeRejectedMessage(transactionDto);
                }
            } else if ("RETRYING".equalsIgnoreCase(transactionDto.getTransactionStatus())) {
                transactionDto.setMessage("failed and will retry");
                transactionDto.setErrorMessage(cmsService.getContent(TXN_FAILED_RETRY_CONTENT));
            }
        }
    }

    /**
     * This is a refactored method created to reduce the cyclomatic complexity of calling method populateMessages
     * @param transactionDto
     */
    private void computeRejectedMessage(TransactionDto transactionDto) {
        if ("Once".equalsIgnoreCase(transactionDto.getFrequency())) {
            transactionDto.setErrorMessage(cmsService.getContent(TXN_FAILED_FINAL_CONTENT));
        } else {
            String [] params = new String[1];
            switch (transactionDto.getFrequency()) {
                case "Weekly":
                    params[0] = "week";
                    break;
                case "Fortnightly":
                    params[0] = "fortnight";
                    break;
                case "Monthly":
                    params[0] = "month";
                    break;
                case "Quarterly":
                    params[0] = "quarter";
                    break;
                case "Yearly":
                    params[0] = "year";
                    break;
                default:
                    params[0] = "";
                    break;
            }
            transactionDto.setErrorMessage(cmsService.getDynamicContent(TXN_FAILED_RECURRING_CONTENT, params));
        }
    }

}