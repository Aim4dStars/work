package com.bt.nextgen.service.wrap.integration.income;

import com.bt.nextgen.service.avaloq.income.DividendIncomeImpl;

/**
 * Created by L062605 on 6/11/2017.
 */
public class ThirdPartyDividendIncomeImpl extends DividendIncomeImpl {

    private String thirdPartySource;

    public String getThirdPartySource() {
        return thirdPartySource;
    }

    public void setThirdPartySource(String thirdPartySource) {
        this.thirdPartySource = thirdPartySource;
    }
}
