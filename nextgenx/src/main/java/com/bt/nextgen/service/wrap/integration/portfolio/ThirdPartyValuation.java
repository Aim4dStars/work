package com.bt.nextgen.service.wrap.integration.portfolio;

import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;

public class ThirdPartyValuation extends WrapAccountValuationImpl{

    private String thirdPartySource;

    public String getThirdPartySource() {
        return thirdPartySource;
    }

    public void setThirdPartySource(String thirdPartySource) {
        this.thirdPartySource = thirdPartySource;
    }
}
