package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PensionPaymentOrIndexationTypeConverterTest {

    @InjectMocks
    PensionPaymentOrIndexationTypeConverter converter;

    @Mock
    StaticIntegrationService staticIntegrationService;

    @Before
    public void setup() {
        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.PENSION_PAYMENT_OR_INDEXATION_TYPE),
                Mockito.eq("pens_annu_max"), Mockito.any(ServiceErrors.class)))
                .thenReturn(new CodeImpl(null, null, null, "pens_annu_max"));
        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.PENSION_PAYMENT_OR_INDEXATION_TYPE),
                Mockito.eq("fixed_amount"), Mockito.any(ServiceErrors.class)))
                .thenReturn(new CodeImpl(null, null, null, "fixed_amount"));
    }

    @Test
    public void testConvert_whenNoPensionPaymentTypeIsFound_thenReturnsTheConvertedIndexationType() {
        Enum<?> result = converter.convert("fixed_amount");
        assertThat("Result " + result + " should be IndexationType.DOLLAR", result == IndexationType.DOLLAR);
    }

    @Test
    public void testConvert_whenPensionPaymentTypeIsFound_thenReturnsTheConvertedPensionPaymentType() {
        Enum<?> result = converter.convert("pens_annu_max");
        assertThat("Result " + result + " should be PensionPaymentType.MAXIMUM_AMOUNT",
                result == PensionPaymentType.MAXIMUM_AMOUNT);
    }
}
