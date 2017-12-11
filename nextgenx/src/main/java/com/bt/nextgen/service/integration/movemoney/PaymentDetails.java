package com.bt.nextgen.service.integration.movemoney;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.btfin.panorama.service.integration.RecurringFrequency;
import org.joda.time.DateTime;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.account.AccountKey;

public interface PaymentDetails {

    MoneyAccountIdentifier getMoneyAccount();

    PayAnyoneAccountDetails getPayAnyoneBeneficiary();

    BpayBiller getBpayBiller();

    BigDecimal getAmount();

    CurrencyType getCurrencyType();

    String getReceiptNumber();

    String getBenefeciaryInfo();

    Date getTransactionDate();

    String getPayeeName();

    Date getPaymentDate();

    // Position Id Used for cancellation of the Standing order payments
    String getPositionId();

    //Doc Id is used for saved payments
    String getDocId();

    RecurringFrequency getRecurringFrequency();

    Date getEndDate();

    BigInteger getMaxCount();

    Date getStartDate();

    DateTime getNextTransactionDate();

    IndexationType getIndexationType();

    BigDecimal getIndexationAmount();

    WithdrawalType getWithdrawalType();

    PensionPaymentType getPensionPaymentType();

    AccountKey getAccountKey();

    List<ValidationError> getErrors();

    List<ValidationError> getWarnings();

    PaymentActionType getPaymentAction();

    String getTransactionSeqNo();

    String getBusinessChannel();

    String getClientIp();

    String getModificationSeq();
}
