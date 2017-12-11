package com.bt.nextgen.api.product.v1.service;

import com.bt.nextgen.api.product.v1.model.ProductFeeComponentDto;
import com.btfin.panorama.service.avaloq.product.FeeType;
import com.bt.nextgen.service.integration.product.ProductFeeComponent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by F058391 on 26/07/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductFeeComponentDtoConverterTest {

    @InjectMocks
    ProductFeeComponentDtoConverter productFeeComponentDtoConverter;

    @Test
    public void shouldReturnAnEmptyObjectIfFeeTypeIsNull() {
        final ProductFeeComponent productFeeComponent = mock(ProductFeeComponent.class);
        when(productFeeComponent.getFeeType()).thenReturn(null);
        when(productFeeComponent.getCapMax()).thenReturn(BigDecimal.ONE);
        final ProductFeeComponentDto productFeeComponentDto = productFeeComponentDtoConverter.convert(productFeeComponent);
        Assert.assertNull(productFeeComponentDto.getCapMax());
    }

    @Test
    public void shouldReturnPopulatedFeeComponentIfFeeTypeIsNotNull() {
        final ProductFeeComponent productFeeComponent = mock(ProductFeeComponent.class);
        when(productFeeComponent.getFeeType()).thenReturn(FeeType.ADMINISTRATION_FEE);
        when(productFeeComponent.getCapMax()).thenReturn(BigDecimal.ONE);
        final ProductFeeComponentDto productFeeComponentDto = productFeeComponentDtoConverter.convert(productFeeComponent);
        assertThat(productFeeComponentDto.getFeeType(), is(FeeType.ADMINISTRATION_FEE.name()));
        assertThat(productFeeComponentDto.getCapMax(), is(BigDecimal.ONE));
    }
}