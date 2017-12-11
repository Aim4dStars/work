package com.bt.nextgen.reports.account.movemoney;

import com.bt.nextgen.api.movemoney.v3.model.DepositDto;
import com.bt.nextgen.api.transaction.model.TransactionDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.web.ApiFormatter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class ReceiptReportData {
    private String depositId;
    private String depositType;
    private String fromPayerName;
    private String fromPayerBsb;
    private String fromPayerAccount;
    private String frequency;
    private DateTime nextPaymentDate;
    private DateTime startDate;
    private DateTime endDate;
    private BigDecimal depositAmount;

    public ReceiptReportData(DepositDto deposit) {
        this.depositId = deposit.getKey().getDepositId();
        this.startDate = deposit.getTransactionDate();
        this.depositAmount = deposit.getAmount();
        this.depositType = deposit.getDepositType();
        this.fromPayerName = deposit.getFromPayDto().getAccountName();
        this.fromPayerBsb = deposit.getFromPayDto().getCode();
        this.fromPayerAccount = deposit.getFromPayDto().getAccountId();
        this.frequency = deposit.getFrequency();
        this.endDate = deposit.getRepeatEndDate();
    }

    public ReceiptReportData(TransactionDto transaction) {
        this.depositId = transaction.getStordPosId();
        this.nextPaymentDate = transaction.getNextDueDate();
        this.startDate = transaction.getFirstPayment();
        this.depositAmount = transaction.getNetAmount();
        this.depositType = transaction.getContributionType();
        this.fromPayerName = transaction.getPayer();
        this.fromPayerBsb = transaction.getPayerBsb();
        this.fromPayerAccount = transaction.getPayerAccount();
        this.frequency = transaction.getFrequency();
        this.endDate = transaction.getLastPayment();
    }

    public String getDepositId() {
        return depositId;
    }

    public String getAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, this.depositAmount);
    }

    public String getTransactionDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, this.startDate);
    }

    public String getDepositType() {
        return depositType;
    }

    public String getFromPayerName() {
        return fromPayerName;
    }

    public String getFromPayerBsb() {
        return ApiFormatter.formatBsb(this.fromPayerBsb);
    }

    public String getFromPayerAccount() {
        return fromPayerAccount;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getRepeatEndDate() {
        if (this.endDate != null) {
            return ReportFormatter.format(ReportFormat.SHORT_DATE, this.endDate);
        } else {
            return "No end date";
        }
    }

    public String getPaymentDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, this.nextPaymentDate);
    }
}
