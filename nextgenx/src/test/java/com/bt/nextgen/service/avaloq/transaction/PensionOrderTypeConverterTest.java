package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by L067218 on 14/02/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class PensionOrderTypeConverterTest {

    @InjectMocks
    PensionOrderTypeConverter converter;

    @Mock
    StaticIntegrationService staticIntegrationService;

    @Before
    public void setup() {

        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.ORDER_TYPE),
                Mockito.eq("1125"), Mockito.any(ServiceErrors.class)))
                .thenReturn(new CodeImpl("1125", "PAY#SUPER_PENS_ONEOFF", "Super One-Off Pension Payment", "pay#super_pens_oneoff"));

        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.ORDER_TYPE),
                Mockito.eq("1122"), Mockito.any(ServiceErrors.class)))
                .thenReturn(new CodeImpl("1122", "PAYMENT.PAY#SUPER_OTH_OUT", "Payment.Super Others Out", "pay.pay#super_oth_out"));

    }


    @Test
    public void testConvert_whenNoOrderTypeIsFound() {
        String result = converter.convert("1122");
        assertThat("Result " + result + " should be null", result == null);
    }

    @Test
    public void testConvert_whenOrderTypeIsFound() {
        String result = converter.convert("1125");
        assertThat("Result " + result + " should be PensionPayment",
                result == "Pension payment");
    }
}







