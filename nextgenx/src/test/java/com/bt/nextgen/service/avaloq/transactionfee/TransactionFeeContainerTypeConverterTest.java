package com.bt.nextgen.service.avaloq.transactionfee;

import com.bt.nextgen.service.integration.transactionfee.ContainerType;
import com.btfin.panorama.core.conversion.StaticCodeConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@RunWith(MockitoJUnitRunner.class)
public class TransactionFeeContainerTypeConverterTest {

    @InjectMocks
    TransactionFeeContainerTypeConverter transactionFeeContainerTypeConverter;

    @Mock
    StaticCodeConverter staticCodeConverter;

    @Before
    public void setup() {
        Mockito.when(
                staticCodeConverter.convert(Mockito.matches("code-that-will-be-found"), Mockito.matches("FEE_CONTAINER_TYPE")))
                .thenReturn("portf_dir");

        Mockito.when(
                staticCodeConverter.convert(Mockito.matches("code-that-will-not-be-found"),
                        Mockito.matches("FEE_CONTAINER_TYPE")))
                .thenReturn("code-that-will-not-be-found");
    }

    @Test
    public void testConvert_whenTheSourceIsConvertedAndIsAValidContainerTypeId_thenTheCorrectContainerTypeIsReturned() {
        ContainerType type = transactionFeeContainerTypeConverter.convert("code-that-will-be-found");
        assertThat(type, equalTo(ContainerType.DIRECT));
    }

    @Test
    public void testConvert_whenTheSourceIsConvertedAndIsNotAValidContainerTypeId_thenTheUNKNOWNContainerTypeIsReturned() {
        ContainerType type = transactionFeeContainerTypeConverter.convert("code-that-will-not-be-found");
        assertThat(type, equalTo(ContainerType.UNKNOWN));
    }

    @Test
    public void testConvert_whenTheSourceIsNull_thenNullIsReturned() {
        ContainerType type = transactionFeeContainerTypeConverter.convert(null);
        assertThat(type, nullValue());
    }

}
