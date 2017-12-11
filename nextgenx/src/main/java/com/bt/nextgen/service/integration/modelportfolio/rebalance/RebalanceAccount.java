package com.bt.nextgen.service.integration.modelportfolio.rebalance;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;

import java.math.BigDecimal;

public interface RebalanceAccount {

    AccountKey getAccount();

    BrokerKey getAdviser();

    BigDecimal getValue();

    Integer getAssetClassBreach();

    Integer getToleranceBreach();

    Integer getEstimatedBuys();

    Integer getEstimatedSells();

    String getSystemExclusionReason();

    String getUserExclusionReason();

    Boolean getUserExcluded();

    String getRebalDocId();

}