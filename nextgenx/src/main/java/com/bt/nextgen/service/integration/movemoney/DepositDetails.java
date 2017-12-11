package com.bt.nextgen.service.integration.movemoney;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.btfin.panorama.service.integration.RecurringFrequency;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface for holding all the details of Deposit Transaction
 */
public interface DepositDetails {
    public MoneyAccountIdentifier getMoneyAccountIdentifier();

    public PayAnyoneAccountDetails getPayAnyoneAccountDetails();

    public String getDepositId();

    public String getTransactionSeq();

    public DepositStatus getStatus();

    public BigDecimal getDepositAmount();

    public String getDescription();

    public DateTime getTransactionDate();

    public ContributionType getContributionType();

    public String getPayerBsb();

    public String getPayerAccount();

    public String getPayerName();

    public String getPayeeMoneyAccount();

    public RecurringFrequency getRecurringFrequency();

    public OrderType getOrderType();

    public CurrencyType getCurrencyType();

    public String getReceiptNumber();

    public DateTime getDepositDate();

    public List<ValidationError> getErrors();

    public List<ValidationError> getWarnings();
}
