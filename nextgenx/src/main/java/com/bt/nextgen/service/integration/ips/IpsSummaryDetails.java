package com.bt.nextgen.service.integration.ips;

import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioStatus;

public interface IpsSummaryDetails {

    /**
     * The Investment policy statement id.
     */
    public IpsKey getModelKey();

    public String getModelName();

    public String getModelCode();

    public String getApirCode();

    /**
     * List of model status: New, Pending, Open, Closed to New, Suspended, Terminated as specified by btfg$ips_status
     */
    public ModelPortfolioStatus getStatus();

    /**
     * Order-id of the last IPS update.
     */
    public String getIpsOrderId();

    /**
     * Order-id of the last Model update.
     */
    public String getModelOrderId();

    public BrokerKey getInvestmentManagerId();

    public String getAccountType();

    public ConstructionType getModelConstruction();

}
