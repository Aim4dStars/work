package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.integration.xml.annotation.LazyServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.movemoney.ContributionType;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.btfin.panorama.service.integration.RecurringFrequency;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

@LazyServiceBean(expression = "doc_head_list/doc_head/freq_id/val!='5000'")
public class RecurringDepositDetailsImpl extends DepositDetailsImpl implements RecurringDepositDetails {
    private static final String XPATH_CONSTANT = "doc_head_list/doc_head/";

    @ServiceElement(xpath = XPATH_CONSTANT + "first_date/val", converter = DateTimeTypeConverter.class)
    private DateTime startDate;

    @ServiceElement(xpath = XPATH_CONSTANT + "end_date/val", converter = DateTimeTypeConverter.class)
    private DateTime endDate;

    @ServiceElement(xpath = XPATH_CONSTANT + "max_period_cnt/val")
    private Integer maxCount;

    private String positionId;
    private DateTime nextTransactionDate;

    public RecurringDepositDetailsImpl() {
        // default constructor
    }

    @SuppressWarnings("squid:S00107")
    public RecurringDepositDetailsImpl(MoneyAccountIdentifier moneyAccountIdentifier,
                                       PayAnyoneAccountDetails payAnyoneAccountDetails, BigDecimal depositAmount, CurrencyType currencyType,
                                       String description, DateTime transactionDate, ContributionType contributionType,
                                       RecurringFrequency recurringFrequency, DateTime endDate, Integer maxCount, List<ValidationError> warnings,
                                       String receiptNumber, String transactionSeq) {

        super(moneyAccountIdentifier, payAnyoneAccountDetails, depositAmount, currencyType, description, transactionDate,
                receiptNumber, null, contributionType, recurringFrequency, warnings, transactionSeq);

        this.endDate = endDate;
        this.maxCount = maxCount;
    }

    // depositutil v2
    @SuppressWarnings("squid:S00107")
    public RecurringDepositDetailsImpl(MoneyAccountIdentifier moneyAccountIdentifier,
            PayAnyoneAccountDetails payAnyoneAccountDetails, BigDecimal depositAmount, CurrencyType currencyType,
            String description, DateTime transactionDate, ContributionType contributionType,
            RecurringFrequency recurringFrequency, DateTime endDate, Integer maxCount) {

        this(moneyAccountIdentifier, payAnyoneAccountDetails, depositAmount, currencyType, description, transactionDate,
                contributionType, recurringFrequency, endDate, maxCount, null, null, null);
    }

    // for RIPs
    @SuppressWarnings("squid:S00107")
    public RecurringDepositDetailsImpl(MoneyAccountIdentifier moneyAccountIdentifier,
            PayAnyoneAccountDetails payAnyoneAccountDetails, BigDecimal depositAmount, CurrencyType currencyType,
            String description, DateTime transactionDate, String receiptNumber, DateTime depositDate,
            ContributionType contributionType, RecurringFrequency recurringFrequency, DateTime startDate, DateTime endDate,
            Integer maxCount, String positionId) {

        this(moneyAccountIdentifier, payAnyoneAccountDetails, depositAmount, currencyType, description, transactionDate,
                receiptNumber, depositDate, contributionType, recurringFrequency, startDate, endDate, maxCount, positionId, null);
    }

    // response from avaloq
    @SuppressWarnings("squid:S00107")
    public RecurringDepositDetailsImpl(MoneyAccountIdentifier moneyAccountIdentifier,
            PayAnyoneAccountDetails payAnyoneAccountDetails, BigDecimal depositAmount, CurrencyType currencyType,
            String description, DateTime transactionDate, String receiptNumber, DateTime depositDate,
            ContributionType contributionType, RecurringFrequency recurringFrequency, DateTime startDate, DateTime endDate,
            Integer maxCount, String positionId, String transactionSeq) {

        super(moneyAccountIdentifier, payAnyoneAccountDetails, depositAmount, currencyType, description, transactionDate,
                receiptNumber, depositDate, contributionType, recurringFrequency, transactionSeq);

        this.startDate = startDate;
        this.endDate = endDate;
        this.maxCount = maxCount;
        this.positionId = positionId;
    }

    /**
     * @return the startDate
     */
    @Override
    public DateTime getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     *            the startDate to set
     */
    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    @Override
    public DateTime getEndDate() {
        return endDate;
    }

    /**
     * @param endDate
     *            the endDate to set
     */
    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the maxCount
     */
    @Override
    public Integer getMaxCount() {
        return maxCount;
    }

    /**
     * @param maxCount
     *            the maxCount to set
     */
    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    @Override
    public DateTime getNextTransactionDate() {
        return nextTransactionDate;
    }

    @Override
    public void setNextTransactionDate(DateTime nextTransactionDate) {
        this.nextTransactionDate = nextTransactionDate;
    }
}
