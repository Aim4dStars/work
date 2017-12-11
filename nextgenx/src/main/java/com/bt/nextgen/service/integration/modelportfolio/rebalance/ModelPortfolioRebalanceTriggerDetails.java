package com.bt.nextgen.service.integration.modelportfolio.rebalance;

import org.joda.time.DateTime;

public interface ModelPortfolioRebalanceTriggerDetails {

    DateTime getTranasactionDate();
    
    String getTrigger();

    Integer getTotalAccountsCount();

    Integer getTotalRebalancesCount();

}
