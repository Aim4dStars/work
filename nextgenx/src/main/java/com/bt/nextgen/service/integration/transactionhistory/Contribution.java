package com.bt.nextgen.service.integration.transactionhistory;

import java.math.BigDecimal;

public interface Contribution {
    String getContributionSubType();

    BigDecimal getContributionAmount();

    String getContributionDescription();
}
