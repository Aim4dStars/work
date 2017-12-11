package com.bt.nextgen.service.wrap.integration.portfolio;

import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;

public class WrapTermDepositHoldingImpl extends TermDepositHoldingImpl implements ThirdPartyAccountHolding {

    private String thirdPartySource;

    public String getThirdPartySource() {
        return thirdPartySource;
    }

    public void setThirdPartySource(String thirdPartySource) {
        this.thirdPartySource = thirdPartySource;
    }
}
