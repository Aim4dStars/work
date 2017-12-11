package com.bt.nextgen.api.rollover.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class ReceivedContributionDto extends BaseDto {

    private String contributionId;
    private String description;
    private BigDecimal amount;
    private DateTime paymentDate;
    private String contributionStatus;

    public ReceivedContributionDto() {
        super();
    }

    public ReceivedContributionDto(String contributionId, String description, BigDecimal amount, DateTime paymentDate,
            String contributionStatus) {
        super();
        this.contributionId = contributionId;
        this.description = description;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.contributionStatus = contributionStatus;
    }

    public String getContributionId() {
        return contributionId;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public DateTime getPaymentDate() {
        return paymentDate;
    }

    public String getContributionStatus() {
        return contributionStatus;
    }

}
