package com.bt.nextgen.reports.account.valuation;

public abstract class AbstractValuationReportData implements ValuationReportData {
    public String getCode() {
        return "";
    }

    public String getName() {
        return "";
    }

    public String getRate() {
        return "";
    }

    public String getDollarGain() {
        return "";
    }

    public String getPercentGain() {
        return "";
    }

    public String getBalance() {
        return "";
    }

    public String getAllocationPercent() {
        return "";
    }

    public String getLastPrice() {
        return "";
    }

    public String getLastPriceDate() {
        return "";
    }

    public String getQuantity() {
        return "";
    }

    public String getAverageCost() {
        return "";
    }

    public Boolean getExternalAsset() {
        return false;
    }

    public String getMaturityInstruction() {
        return "";
    }

    public String getMaturityDetail() {
        return "";
    }

    public String getTermDetail() {
        return "";
    }

    public String getAssetBrandClass() {
        return "";
    }

    public String getSource() {
        return "";
    }

    public String getHinType() {
        return null;
    }
}
