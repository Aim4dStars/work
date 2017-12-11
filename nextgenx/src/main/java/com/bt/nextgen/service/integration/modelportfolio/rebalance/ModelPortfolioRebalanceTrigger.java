package com.bt.nextgen.service.integration.modelportfolio.rebalance;

import com.bt.nextgen.service.avaloq.modelportfolio.TriggerStatus;
import org.joda.time.DateTime;

import java.util.List;

public interface ModelPortfolioRebalanceTrigger {

    String getTriggerType();

    DateTime getMostRecentTriggerDate();

    Integer getTotalAccountsCount();

    Integer getTotalRebalancesCount();

    List<ModelPortfolioRebalanceTriggerDetails> getRebalanceTriggerDetails();

    TriggerStatus getStatus();

}
