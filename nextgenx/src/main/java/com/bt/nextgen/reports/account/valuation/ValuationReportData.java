package com.bt.nextgen.reports.account.valuation;

import java.util.List;

public interface ValuationReportData {
    public List<ValuationReportData> getChildren();

    public String getCode();

    public String getName();

    public String getRate();

    public String getDollarGain();

    public String getPercentGain();

    public String getBalance();

    public String getAllocationPercent();

    public String getLastPrice();

    public String getLastPriceDate();

    public String getQuantity();

    public String getAverageCost();

    public Boolean getExternalAsset();

    public String getMaturityInstruction();

    public String getMaturityDetail();

    public String getTermDetail();

    public String getAssetBrandClass();

    public String getSource();
}
