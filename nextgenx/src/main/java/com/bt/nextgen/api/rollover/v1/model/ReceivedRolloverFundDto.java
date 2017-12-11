package com.bt.nextgen.api.rollover.v1.model;

import com.bt.nextgen.service.integration.rollover.RolloverContributionStatus;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class ReceivedRolloverFundDto extends RolloverFundDto {

    private DateTime dateReceived;

    private String contributionStatus;

    public ReceivedRolloverFundDto(String accountId, String fundId, String fundName, String fundAbn, String fundUsi,
            String rolloverType, BigDecimal estimatedAmount) {
        super(accountId, fundId, fundName, fundAbn, fundUsi, rolloverType, estimatedAmount);
    }

    public ReceivedRolloverFundDto(RolloverFundDto fund, RolloverContributionStatus status, DateTime receivedDate) {
        super(fund.getAccountId(), fund.getFundId(), fund.getFundName(), fund.getFundAbn(), fund.getFundUsi(), fund
                .getRolloverType(), fund.getAmount());
        this.dateReceived = receivedDate;
        this.contributionStatus = status != null ? status.name() : null;
    }

    public DateTime getDateReceived() {
        return dateReceived;
    }

    public String getContributionStatus() {
        return contributionStatus;
    }

}
