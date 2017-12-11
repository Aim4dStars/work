package com.bt.nextgen.service.integration.ips;

import java.math.BigDecimal;
import java.util.List;

public interface InvestmentPolicyStatementInterface extends IpsIdentifier {
    public String getInvestmentName();

    public String getCode();

    public String getApirCode();

    public String getAssetClassId();

    public String getInvestmentStyleId();

    public BigDecimal getPercentage();

    public BigDecimal getMinInitInvstAmt();

    public String getInvestmentManagerPersonId();

    public Boolean getTaxAssetDomicile();

    public List<IpsFee> getFeeList();

    BigDecimal getWeightedIcr();
}
