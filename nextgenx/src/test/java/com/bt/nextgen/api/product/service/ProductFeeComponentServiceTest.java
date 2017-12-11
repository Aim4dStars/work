package com.bt.nextgen.api.product.service;

import com.bt.nextgen.api.product.model.ProductFeeComponentDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.avaloq.product.FeeType;
import com.bt.nextgen.service.avaloq.product.ProductFeeComponentImpl;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductFeeComponent;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductFeeComponentServiceTest {

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private ProductFeeComponentDtoConverter feeComponentDtoConverter;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private Product product;

    @InjectMocks
    private ProductFeeComponentServiceImpl productFeeComponentService;

    @Test
    public void getProductFeeComponentDto_shouldReturnAProductFeeComponentDtoWithTheSameFeeType() throws Exception {
        ProductFeeComponent licenseeAdviceFee = productFeeComponent(FeeType.LICENSEE_ADVICE_FEE);
        ProductFeeComponentDto mockLicenseeAdviceFee = mock(ProductFeeComponentDto.class);
        when(feeComponentDtoConverter.convert(licenseeAdviceFee)).thenReturn(mockLicenseeAdviceFee);

        ProductFeeComponent ongoingAdviceFee = productFeeComponent(FeeType.ONGOING_ADVICE_FEE);
        ProductFeeComponentDto mockOngoingAdviceFee = mock(ProductFeeComponentDto.class);
        when(feeComponentDtoConverter.convert(ongoingAdviceFee)).thenReturn(mockOngoingAdviceFee);

        when(productIntegrationService.getProductFeeComponents(product, serviceErrors)).thenReturn(asList(licenseeAdviceFee, ongoingAdviceFee));

        List<ProductFeeComponentDto> productFeeComponentDtos = productFeeComponentService.getProductFeeComponents(product, serviceErrors);
        assertThat(productFeeComponentDtos, contains(mockLicenseeAdviceFee, mockOngoingAdviceFee));
    }

    private static ProductFeeComponent productFeeComponent(FeeType feeType) {
        ProductFeeComponentImpl licenseeFee = new ProductFeeComponentImpl();
        licenseeFee.setFeeType(feeType);
        return licenseeFee;
    }
}