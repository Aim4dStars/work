package com.bt.nextgen.service.integration.modelportfolio.rebalance;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.modelportfolio.RebalanceAction;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.IpsKey;

import java.util.List;

public interface ModelPortfolioRebalanceIntegrationService {

    public List<ModelPortfolioRebalance> loadModelPortfolioRebalances(BrokerKey investmentManagerKey,
            ServiceErrors serviceErrors);

    public List<RebalanceAccount> loadModelPortfolioRebalanceAccounts(IpsKey ipsKey, ServiceErrors serviceErrors);

    public ModelPortfolioRebalance updateModelPortfolioRebalance(BrokerKey investmentManagerKey, IpsKey ipsKey,
            RebalanceAction action, ServiceErrors serviceErrors);

    public ModelPortfolioRebalance submitModelPortfolioRebalance(BrokerKey broker, IpsKey ipsKey, ServiceErrors serviceErrors);

    void updateRebalanceExclusions(BrokerKey broker, IpsKey ipsKey, List<RebalanceExclusion> exclusions,
            ServiceErrors serviceErrors);

    public List<RebalanceOrderGroup> loadRebalanceOrdersForIps(IpsKey ipsKey, ServiceErrors serviceErrors);

    public List<RebalanceOrderGroup> loadRebalanceOrders(List<String> docIds, ServiceErrors serviceErrors);

}
