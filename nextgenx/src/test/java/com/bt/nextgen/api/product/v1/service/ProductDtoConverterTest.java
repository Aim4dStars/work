package com.bt.nextgen.api.product.v1.service;

import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.bt.nextgen.api.product.v1.model.ProductFeeComponentDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.avaloq.product.FeeType;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.product.ProductFeeComponent;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static ch.lambdaj.Lambda.convert;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductDtoConverterTest {

    @InjectMocks
    ProductDtoConverter productDtoConverter;

    @Mock
    ProductFeeComponentDtoConverter feeComponentDtoConverterService;

    ProductImpl product = new ProductImpl();
    ProductFeeComponentDto feeComponentDto = new ProductFeeComponentDto();

    @Before
    public void setUp() {

        product.setProductKey(ProductKey.valueOf("84967"));
        product.setProductName("Offer Direct 60f52dc6d17421eaf1632ac9efae210");
        product.setParentProductName("BT Investments");
        product.setProductType("Offer");
        product.setActive(true);
        product.setProductLevel(ProductLevel.OFFER);
        product.setParentProduct("84963");
        product.setShortName("PROD.OFFER.60F52DC6D17421EAF1632AC9EFAE2");
        product.setMinContribution(new BigDecimal(1250));
        product.setMinIntialInvestment(new BigDecimal(50000));
        product.setMinWithdrwal(new BigDecimal(1250));

        final ProductFeeComponent productFeeComponent = new ProductFeeComponent() {
            @Override
            public String getFeeComponentName() {
                return "Ongoing advice fee";
            }

            @Override
            public FeeType getFeeType() {
                return FeeType.ONGOING_ADVICE_FEE;
            }

            @Override
            public DateTime getFeeDateFrom() {
                return new DateTime();
            }

            @Override
            public DateTime getFeeDateTo() {
                return new DateTime();
            }

            @Override
            public BigDecimal getCapFactor() {
                return new BigDecimal(0.1);
            }

            @Override
            public BigDecimal getCapMin() {
                return new BigDecimal(2000);
            }

            @Override
            public BigDecimal getCapMax() {
                return new BigDecimal(20000);
            }

            @Override
            public BigDecimal getCapOffSet() {
                return new BigDecimal(1000);
            }

            @Override
            public BigDecimal getTarrifFactorMax() {
                return new BigDecimal(0.15);
            }

            @Override
            public BigDecimal getTarrifOffSetFactorMax() {
                return new BigDecimal(2000);
            }
        };
        product.setFeeComponents(singletonList(productFeeComponent));

        feeComponentDto.setFeeComponentName("Ongoing advice fee");
        feeComponentDto.setFeeDateFrom(new DateTime());
        feeComponentDto.setFeeType(FeeType.ONGOING_ADVICE_FEE.name());
        feeComponentDto.setFeeDateTo(new DateTime());
        feeComponentDto.setCapFactor(new BigDecimal(0.15));
        feeComponentDto.setCapMin(new BigDecimal(2000));
        feeComponentDto.setCapMax(new BigDecimal(20000));
        feeComponentDto.setCapOffSet(new BigDecimal(1000));
        feeComponentDto.setTariffFactorMax(new BigDecimal(0.15));
        feeComponentDto.setTariffOffSetFactorMax(new BigDecimal(2000));

        when(feeComponentDtoConverterService.convert(productFeeComponent)).thenReturn(feeComponentDto);
    }

    @Test
    public void convertMethod() {
        final ProductDto productDto = productDtoConverter.convert(product);
        assertNotNull(productDto);
        assertEquals(product.getProductName(), productDto.getProductName());
        assertEquals(product.getParentProductName(), productDto.getParentProductName());
        assertEquals(product.getProductKey().getId(), new EncodedString(productDto.getKey().getProductId()).plainText());
        assertEquals(product.getProductLevel(), ProductLevel.fromAvaloqStaticCode(productDto.getProductLevel()));
        assertEquals(product.getMinIntialInvestment(), productDto.getMinIntialInvestment());
        assertEquals(feeComponentDto.getFeeComponentName(), productDto.getFeeComponents().get(0).getFeeComponentName());
    }

    @Test
    public void convertViaLambda() {
        final List<ProductDto> productDtoList = convert(singletonList(product), productDtoConverter);
        assertNotNull(productDtoList);
        assertEquals(1, productDtoList.size());
    }
}