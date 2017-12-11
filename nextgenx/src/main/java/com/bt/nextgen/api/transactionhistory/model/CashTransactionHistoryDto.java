package com.bt.nextgen.api.transactionhistory.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.bt.nextgen.service.integration.transactionhistory.TransactionSubType;
import org.joda.time.DateTime;

import java.math.BigDecimal;

import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.Matchers.notNullValue;

public class CashTransactionHistoryDto extends BaseDto {
    private TransactionHistory transaction;
    private String categoryType;

    public CashTransactionHistoryDto(TransactionHistory transaction) {
        this.transaction = transaction;
    }

    public CashTransactionHistoryDto(TransactionHistory transaction, String categoryType) {
        this(transaction);
        this.categoryType = categoryType;
    }

    public String getDocDescription() {
        return transaction.getDocDescription();
    }

    public boolean getCleared() {
        return transaction.isCleared();
    }

    // CSV report generation isCleared - method & attribute name same
    public Boolean getUnCleared() {
        return Boolean.valueOf(getCleared());
    }

    public boolean getSystemTransaction() {
        return transaction.isSystemTransaction();
    }

    public Integer getEvtId() {
        return transaction.getEvtId();
    }

    public BigDecimal getBalance() {
        return transaction.getBalance();
    }

    public String getAccountId() {
        // this should not be sent as raw text to the user. JS apps need
        // to update so we can encode this value.
        return transaction.getAccountId();
    }

    public String getDocId() {
        return transaction.getDocId();
    }

    public String getMetaType() {
        if (transaction.getMetaType() != null) {
            return transaction.getMetaType().toString();
        }
        return null;
    }

    public String getOrderType() {
        return transaction.getOrderType();
    }

    public DateTime getEffectiveDate() {
        return transaction.getEffectiveDate();
    }

    public DateTime getValDate() {
        return transaction.getValDate();
    }

    public String getPayerName() {
        return transaction.getPayerName();
    }

    public String getPayeeName() {
        return transaction.getPayeeName();
    }

    public String getPayerBsb() {
        return ApiFormatter.formatBsb(transaction.getPayerBsb());
    }

    public String getPayeeBsb() {
        return ApiFormatter.formatBsb(transaction.getPayeeBsb());
    }

    public String getPayeeAccount() {
        return transaction.getPayeeAccount();
    }

    public String getPayeeBillerCode() {
        return transaction.getPayeeBillerCode();
    }

    public String getPayeeCustrRef() {
        return transaction.getPayeeCustrRef();
    }

    public String getPayerAccount() {
        return transaction.getPayerAccount();
    }

    public DateTime getClearDate() {
        return transaction.getClearDate();
    }

    public BigDecimal getNetAmount() {
        return transaction.getAmount();
    }

    public BigDecimal getReceiptAmount() {
        if (transaction.getNetAmount() != null) {
            // direction of the movement is implied by the transaction description (eg "payment" implies negative)
            return transaction.getNetAmount().abs();
        }
        return null;
    }

    public String getDescriptionFirst() {
        return transaction.getBookingText();
    }

    public String getDescriptionSecond() {
        return transaction.getTransactionDescription();
    }

    public String getTransactionType() {
        return transaction.getBTOrderType() != null ? transaction.getBTOrderType().getDisplayId() : "";
    }

    public String getTransactionCode() {
        // Transaction type that does not pass through the BT Order type enum, which is suseptable to "unknown"
        // data from avaloq being hidden from the ui.
        return transaction.getTransactionType();
    }

    public String getReceiptId() {
        return EncodedString.fromPlainText(transaction.getDocId()).toString();
    }

    public String getCashCategoryType() {
        return categoryType;
    }

    public String getDebitOrCredit() {
        return transaction.getAmount().compareTo(BigDecimal.ZERO) < 0 ? "DEBIT" : "CREDIT";
    }

    public BigDecimal getCoprSeqNr() {
        return transaction.getCoprSeqNr();
    }

    public String getOrigin() {
        return transaction.getOrigin() == null ? null : transaction.getOrigin().getName();
    }

    public String getDetailDescription() {
        StringBuilder detailDescription = new StringBuilder();
        if (transaction.getBTOrderType() != null) {
            if (transaction.isReversal()) {
                detailDescription.append("Reversal - ");
            }
            detailDescription.append(transaction.getBTOrderType().getDisplayName());
        }
        return detailDescription.toString();
    }

    /* Returns the transaction subtype for contributions */
    public String getTransactionSubType() {
        String transactionSubTypeDescription = null;
        if (BTOrderType.CONTRIBUTION.getDisplayId().equals(getTransactionType())) {
            final TransactionSubType transactionSubType = selectFirst(transaction.getTransactionSubTypes(), notNullValue());
            transactionSubTypeDescription = transactionSubType != null ? transactionSubType.getTransactionSubTypeDescription() : null;
        }
        return transactionSubTypeDescription;
    }

    public String getThirdPartySystem() {
        return transaction.getThirdPartySystem();
    }
}