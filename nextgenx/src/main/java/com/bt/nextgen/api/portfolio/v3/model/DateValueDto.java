package com.bt.nextgen.api.portfolio.v3.model;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class DateValueDto extends BaseDto {
    private DateTime date;
    private BigDecimal value;

    public DateValueDto(DateTime date, BigDecimal value) {
        this.date = date;
        this.value = value;
    }

    public DateTime getDate() {
        return date;
    }

    public BigDecimal getValue() {
        return value;
    }

}
