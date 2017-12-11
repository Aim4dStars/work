package com.bt.nextgen.api.superannuation.caps.model;


import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class ContributionCapDto extends BaseDto
{
    private String contributionClassification;

    private String contributionClassificationLabel;

    private BigDecimal amount;


    public String getContributionClassification() {
        return contributionClassification;
    }

    public void setContributionClassification(String contributionClassification) {
        this.contributionClassification = contributionClassification;
    }

    public String getContributionClassificationLabel() {
        return contributionClassificationLabel;
    }

    public void setContributionClassificationLabel(String contributionClassificationLabel) {
        this.contributionClassificationLabel = contributionClassificationLabel;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}