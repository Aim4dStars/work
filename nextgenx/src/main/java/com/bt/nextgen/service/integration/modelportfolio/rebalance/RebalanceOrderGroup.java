package com.bt.nextgen.service.integration.modelportfolio.rebalance;

import com.bt.nextgen.service.integration.broker.BrokerKey;
import org.joda.time.DateTime;

import java.util.List;

public interface RebalanceOrderGroup {

    public String getModelName();

    public String getModelSymbol();

    public BrokerKey getAdviser();

    public DateTime getRebalanceDate();

    public List<RebalanceOrderDetails> getOrderDetails();

    public String getRebalDetDocId();

}