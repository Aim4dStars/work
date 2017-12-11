package com.bt.nextgen.api.cashratehistory.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

/**
 * Created by L072457 on 30/12/2014.
 */
public class CashRateHistoryDto extends BaseDto {

    private String rateDate;
    private String rate;
    private boolean isCurrentRate;

    public CashRateHistoryDto() {
    }

    public CashRateHistoryDto(String rateDate, String rate, boolean isCurrentRate) {
        this.rateDate = rateDate;
        this.rate = rate;
        this.isCurrentRate = isCurrentRate;
    }

    public String getRateDate() {
        return rateDate;
    }

    public void setRateDate(String rateDate) {
        this.rateDate = rateDate;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public boolean isCurrentRate() {
        return isCurrentRate;
    }

    public void setCurrentRate(boolean isCurrentRate) {
        this.isCurrentRate = isCurrentRate;
    }
}
