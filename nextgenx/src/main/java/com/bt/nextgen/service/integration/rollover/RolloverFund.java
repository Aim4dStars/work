package com.bt.nextgen.service.integration.rollover;

import java.math.BigDecimal;

public interface RolloverFund {

    public String getFundId();

    public String getFundUsi();

    public String getFundName();

    public String getFundAbn();

    public BigDecimal getAmount();

    public RolloverType getRolloverType();

}
