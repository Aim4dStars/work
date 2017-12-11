package com.bt.nextgen.service.integration.modelportfolio.rebalance;

import com.bt.nextgen.service.integration.ips.IpsIdentifier;
import org.joda.time.DateTime;

import java.util.List;

public interface ModelPortfolioRebalance extends IpsIdentifier {
    
    String getIpsStatus();

    DateTime getLastRebalanceDate();
    
    String getUserName();

    Integer getTotalAccountsCount();

    Integer getTotalRebalancesCount();

    List<ModelPortfolioRebalanceTrigger> getRebalanceTriggers();

    Boolean getSubmitInProgress();
}
