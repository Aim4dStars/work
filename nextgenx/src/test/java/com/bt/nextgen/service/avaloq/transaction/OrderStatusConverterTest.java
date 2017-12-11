package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by L067218 on 13/02/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderStatusConverterTest {

    @InjectMocks
    OrderStatusConverter converter;

    @Mock
    StaticIntegrationService staticIntegrationService;

    @Before
    public void setup() {

        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.ORDER_STATUS),
                Mockito.eq("101"), Mockito.any(ServiceErrors.class)))
                .thenReturn(new CodeImpl("101", "NSUBM", "Not Submitted", "nsubm"));

        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.ORDER_STATUS),
                Mockito.eq("52"), Mockito.any(ServiceErrors.class)))
                .thenReturn(null);

    }

    @Test
    public void testConvert_whenOrderTypeIsFound_thenReturnsTheConvertedOrderType() {
        String result = converter.convert("101");
        assertThat("Result " + result + " should be OrderType",
                result == "Not Submitted");
    }

    @Test
    public void testConvert_whenOrderTypeIsNotFound() {
        String result = converter.convert("52");
        assertThat("Result " + result + " should be OrderType",
                result == null);
    }


}









