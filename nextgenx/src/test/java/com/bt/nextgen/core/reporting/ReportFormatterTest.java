package com.bt.nextgen.core.reporting;

import org.hamcrest.core.IsEqual;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReportFormatterTest {

    @Test
    public void testFormatTelephoneNumber_ReturnWithSpace() throws Exception {
        assertThat(ReportFormatter.formatTelephoneNumber("0419123456"), IsEqual.equalTo("0419 123 456"));
        assertThat(ReportFormatter.formatTelephoneNumber("0894155000"), IsEqual.equalTo("(08) 9415 5000"));
        assertThat(ReportFormatter.formatTelephoneNumber("61419123456"), IsEqual.equalTo("+61 419 123 456"));
        assertThat(ReportFormatter.formatTelephoneNumber("123456789"), IsEqual.equalTo("+123 456 789"));
        assertThat(ReportFormatter.formatTelephoneNumber("1800123456"), IsEqual.equalTo("1800 123 456"));
        assertThat(ReportFormatter.formatTelephoneNumber("1300123456"), IsEqual.equalTo("1300 123 456"));
    }
}
