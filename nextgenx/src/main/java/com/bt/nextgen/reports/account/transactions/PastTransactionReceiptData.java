package com.bt.nextgen.reports.account.transactions;

import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.web.ApiFormatter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Receipt data for past transactions
 */
public class PastTransactionReceiptData {

    private final String detailDescription;
    private final BigDecimal receiptAmount;
    private final String payerName;
    private final String payerBsb;
    private final String payerAccount;
    private final String payeeName;
    private final String payeeBsb;
    private final String payeeAccount;
    private final String payeeBillerCode;
    private final String payeeCustrRef;
    private final DateTime effectiveDate;
    private final DateTime valDate;
    private final String descriptionSecond;
    private final String docId;

    public PastTransactionReceiptData(CashTransactionHistoryDto cashTransactionHistoryDto) {
        this.detailDescription = cashTransactionHistoryDto.getDetailDescription();
        this.receiptAmount = cashTransactionHistoryDto.getReceiptAmount();
        this.payerName = cashTransactionHistoryDto.getPayerName();
        this.payerBsb = cashTransactionHistoryDto.getPayerBsb();
        this.payerAccount = cashTransactionHistoryDto.getPayerAccount();
        this.payeeName = cashTransactionHistoryDto.getPayeeName();
        this.payeeBsb = cashTransactionHistoryDto.getPayeeBsb();
        this.payeeAccount = cashTransactionHistoryDto.getPayeeAccount();
        this.payeeBillerCode = cashTransactionHistoryDto.getPayeeBillerCode();
        this.payeeCustrRef = cashTransactionHistoryDto.getPayeeCustrRef();
        this.effectiveDate = cashTransactionHistoryDto.getEffectiveDate();
        this.valDate = cashTransactionHistoryDto.getValDate();
        this.descriptionSecond = cashTransactionHistoryDto.getDescriptionSecond();
        this.docId = cashTransactionHistoryDto.getDocId();
    }

    public String getDetailDescription() {
        return detailDescription;
    }

    public String getReceiptAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, this.receiptAmount);
    }

    public String getPayerName() {
        return payerName;
    }

    public String getPayerBsb() {
        return ApiFormatter.formatBsb(this.payerBsb);
    }

    public String getPayerAccount() {
        return payerAccount;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public String getPayeeBsb() {
        return ApiFormatter.formatBsb(this.payeeBsb);
    }

    public String getPayeeAccount() {
        return payeeAccount;
    }

    public String getPayeeBillerCode() {
        return payeeBillerCode;
    }

    public String getPayeeCustrRef() {
        return payeeCustrRef;
    }

    public String getEffectiveDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, this.effectiveDate);
    }

    public String getValDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, this.valDate);
    }

    public String getDescriptionSecond() {
        return descriptionSecond;
    }

    public String getDocId() {
        return docId;
    }
}
