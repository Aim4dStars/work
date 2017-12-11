package com.bt.nextgen.api.modelportfolio.v2.model.defaultparams;

import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.Broker;

public class DealerParameterKey {
    private String accountType;
    private String portfolioType;
    private BrokerKey dealerKey;

    public DealerParameterKey(String accountType, String portfolioType, Broker dealer) {
        this.accountType = accountType;
        this.portfolioType = portfolioType;
        if (dealer != null) {
            this.dealerKey = dealer.getKey();
        }
    }

    public String getAccountType() {
        return accountType;
    }

    public String getPortfolioType() {
        return portfolioType;
    }

    public BrokerKey getBrokerKey() {
        return dealerKey;
    }

}