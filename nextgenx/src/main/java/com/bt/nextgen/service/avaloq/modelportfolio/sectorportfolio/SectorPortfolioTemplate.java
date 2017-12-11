package com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "squid:S1171", "squid:S1948" })
public enum SectorPortfolioTemplate implements AvaloqTemplate {

    SECTOR_PORTFOLIOS_FOR_IM("BTFG$UI_MPF_LIST.IM#DET", new ArrayList<AvaloqParameter>() {

        {
            add(SectorPortfolioParams.PARAM_INVESTMENT_MANAGER_ID);
        }
    }), SECTOR_PORTFOLIOS("BTFG$UI_MPF_LIST.MPF#DET", new ArrayList<AvaloqParameter>() {
        {
            add(SectorPortfolioParams.PARAM_MPF_LIST);
        }
    });

    private List<AvaloqParameter> validParams;
    private String templateName;

    SectorPortfolioTemplate(String templateName, List<AvaloqParameter> validParams) {
        this.templateName = templateName;
        this.validParams = validParams;
    }

    @Override
    public String getTemplateName() {
        return this.templateName;
    }

    @Override
    public List<AvaloqParameter> getValidParamters() {
        return this.validParams;
    }
}