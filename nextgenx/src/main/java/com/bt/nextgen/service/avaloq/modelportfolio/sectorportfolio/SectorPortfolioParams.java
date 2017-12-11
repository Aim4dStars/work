package com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum SectorPortfolioParams implements AvaloqParameter {
    PARAM_INVESTMENT_MANAGER_ID("i_im_id", AvaloqType.PARAM_ID),
    PARAM_MPF_LIST("i_mpf_list", AvaloqType.PARAM_ID);
   
    private String param;
    private AvaloqType type;

    private SectorPortfolioParams(String param, AvaloqType type)
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
