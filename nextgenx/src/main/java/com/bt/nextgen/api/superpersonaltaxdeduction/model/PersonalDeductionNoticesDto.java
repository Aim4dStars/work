package com.bt.nextgen.api.superpersonaltaxdeduction.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by L067218 on 7/10/2016.
 */
public class PersonalDeductionNoticesDto extends BaseDto {
    private BigDecimal totalNotifiedAmount;

    private BigDecimal maxAmountAvailable;

    private List<NoticeDetailsDto> notices;


    public BigDecimal getTotalNotifiedAmount() {
        return totalNotifiedAmount;
    }

    public void setTotalNotifiedAmount(BigDecimal totalNotifiedAmount) {
        this.totalNotifiedAmount = totalNotifiedAmount;
    }

    public BigDecimal getMaxAmountAvailable() {
        return maxAmountAvailable;
    }

    public void setMaxAmountAvailable(BigDecimal maxAmountAvailable) {
        this.maxAmountAvailable = maxAmountAvailable;
    }

    public List<NoticeDetailsDto> getNotices() {
        return notices;
    }

    public void setNotices(List<NoticeDetailsDto> notices) {
        this.notices = notices;
    }
}
