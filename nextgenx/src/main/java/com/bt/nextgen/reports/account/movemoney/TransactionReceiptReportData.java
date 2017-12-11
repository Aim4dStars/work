package com.bt.nextgen.reports.account.movemoney;

import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.web.ApiFormatter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Receipt data for the payments/deposits
 */
public class TransactionReceiptReportData {

    private String paymentId;
    private String depositType;
    private String frequency;
    private Boolean isRecurring;
    private DateTime repeatEndDate;
    private String endRepeat;
    private BigInteger endRepeatNumber;
    private String transactionType;
    private BigDecimal amount;
    private String fromPayerName;
    private String fromPayerBsb;
    private String fromPayerAccount;
    private String toPayeeName;
    private String toPayeeBsb;
    private String toPayeeAccount;
    private String toPayeeBillerCode;
    private String toPayeeCrn;
    private DateTime transactionDate;
    private String receiptNumber;
    private String description;

    public TransactionReceiptReportData(DepositDto depositDto) {
        this.paymentId = depositDto.getPaymentId();
        this.depositType = depositDto.getDepositType();
        this.frequency = depositDto.getFrequency();
        this.isRecurring = depositDto.getIsRecurring();
        if (depositDto.getRepeatEndDate() != null) {
            this.repeatEndDate = DateTime.parse(depositDto.getRepeatEndDate(), DateTimeFormat.forPattern(ReportFormat.SHORT_DATE.getFormat()));
        }
        this.endRepeat = depositDto.getEndRepeat();
        this.endRepeatNumber = depositDto.getEndRepeatNumber() != null ? new BigInteger(depositDto.getEndRepeatNumber()) : null;
        this.amount = depositDto.getAmount();
        this.fromPayerName = depositDto.getFromPayDto().getAccountName();
        this.fromPayerBsb = depositDto.getFromPayDto().getCode();
        this.fromPayerAccount = depositDto.getFromPayDto().getAccountId();
        this.toPayeeName = depositDto.getToPayeeDto().getAccountName();
        this.toPayeeBsb = depositDto.getToPayeeDto().getCode();
        this.toPayeeAccount = depositDto.getToPayeeDto().getAccountId();
        if (depositDto.getTransactionDate() != null) {
            this.transactionDate = DateTime.parse(depositDto.getTransactionDate(), DateTimeFormat.forPattern(ReportFormat.SHORT_DATE.getFormat()));
        }
        this.receiptNumber = depositDto.getReceiptNumber();
        this.description = depositDto.getDescription();
        this.transactionType = "Deposit";
    }

    public TransactionReceiptReportData(PaymentDto paymentDto) {
        this.paymentId = paymentDto.getPaymentId();
        this.frequency = paymentDto.getFrequency();
        this.isRecurring = paymentDto.getIsRecurring();
        this.repeatEndDate = paymentDto.getRepeatEndDate();
        this.endRepeat = paymentDto.getEndRepeat();
        this.endRepeatNumber = paymentDto.getEndRepeatNumber();
        this.amount = paymentDto.getAmount();
        this.fromPayerName = paymentDto.getFromPayDto().getAccountName();
        this.fromPayerBsb = paymentDto.getFromPayDto().getCode();
        this.fromPayerAccount = paymentDto.getFromPayDto().getAccountId();
        this.toPayeeName = paymentDto.getToPayeeDto().getAccountName();
        this.toPayeeBsb = paymentDto.getToPayeeDto().getCode();
        this.toPayeeAccount = paymentDto.getToPayeeDto().getAccountId();
        if ("BPAY".equals(paymentDto.getToPayeeDto().getPayeeType())) {
            this.toPayeeBillerCode = paymentDto.getToPayeeDto().getCode();
            this.toPayeeCrn = paymentDto.getToPayeeDto().getCrn();
        }
        this.transactionDate = paymentDto.getTransactionDate();
        this.receiptNumber = paymentDto.getReceiptNumber();
        this.description = paymentDto.getDescription();
        this.transactionType = "Payment";
    }


    public String getPaymentId() {
        return paymentId;
    }

    public String getDepositType() {
        return depositType;
    }

    public String getFrequency() {
        return frequency;
    }


    public String getRepeatEndDate() {
        if (this.repeatEndDate != null) {
            final String endDateStr = ReportFormatter.format(ReportFormat.SHORT_DATE, this.repeatEndDate);
            return this.endRepeatNumber != null && this.endRepeatNumber.intValue() > 0 ?
                    "Ends after " + this.endRepeatNumber + " repeats on " + endDateStr :
                    "Ends on " + endDateStr;
        } else {
            return "No end date";
        }
    }

    public String getEndRepeat() {
        return endRepeat;
    }

    public BigInteger getEndRepeatNumber() {
        return endRepeatNumber;
    }

    public Boolean getRecurring() {
        return isRecurring;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, this.amount);
    }

    public String getTransactionDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, this.transactionDate);
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

    public String getToPayeeName() {
        return toPayeeName;
    }

    public String getToPayeeBsb() {
        return  ApiFormatter.formatBsb(toPayeeBsb);
    }

    public String getToPayeeAccount() {
        return toPayeeAccount;
    }

    public String getToPayeeBillerCode() {
        return toPayeeBillerCode;
    }

    public String getToPayeeCrn() {
        return toPayeeCrn;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public String getDescription() {
        return description;
    }
}
