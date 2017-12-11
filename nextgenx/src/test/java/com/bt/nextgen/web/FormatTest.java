package com.bt.nextgen.web;

import com.bt.nextgen.core.domain.Money;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.core.web.Format;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static com.bt.nextgen.service.avaloq.AvaloqUtils.asRate;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class FormatTest {
     @Test
    public void testAsRate() {
        assertThat(asRate(new BigDecimal("0.1000")), equalTo("10.00%"));
        assertThat(asRate(new BigDecimal("0.023923")), equalTo("2.39%"));
        assertThat(asRate(new BigDecimal("0.023093")), equalTo("2.30%"));
    }

    @Test
    public void testAsCurrencyFormat() throws Exception {
        assertThat(Format.asCurrency(new BigDecimal("1234567890.12")), IsEqual.equalTo("$1,234,567,890.12"));
        assertThat(Format.asCurrency(new BigDecimal("1234567899.12")), IsEqual.equalTo("$1,234,567,899.12"));
        assertThat(Format.asCurrency(new Money(".12")), IsEqual.equalTo("$0.12"));
        assertThat(Format.asCurrency(".12"), IsEqual.equalTo("$0.12"));
    }



}
