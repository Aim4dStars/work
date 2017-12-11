package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum ModelPortfolioRebalanceParams implements AvaloqParameter{
    PARAM_INVESTMENT_MANAGER_ID("im_id", AvaloqType.PARAM_ID),
    PARAM_IPS_ID("ips_id", AvaloqType.PARAM_ID),
    PARAM_DOC_ID("doc_id", AvaloqType.PARAM_ID),
    PARAM_DOC_DET_LIST("doc_list_id", AvaloqType.PARAM_ID);
   
    private String param;
    private AvaloqType type;

    private ModelPortfolioRebalanceParams(String param, AvaloqType type)
    {
        this.param = param;
        this.type = type;
    }

    public String getName()
    {
        return param;
    }

    @Override
    public String getParamName() {
        return param;
    }

    @Override
    public AvaloqType getParamType() {
        return type;
    }
}
