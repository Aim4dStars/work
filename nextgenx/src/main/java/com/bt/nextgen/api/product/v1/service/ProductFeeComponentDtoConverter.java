package com.bt.nextgen.api.product.v1.service;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.product.v1.model.ProductFeeComponentDto;
import com.btfin.panorama.service.avaloq.product.FeeType;
import com.bt.nextgen.service.integration.product.ProductFeeComponent;
import org.springframework.stereotype.Component;

@Component("ProductFeeComponentDtoConverterV1")
public class ProductFeeComponentDtoConverter implements Converter<ProductFeeComponent, ProductFeeComponentDto> {

    @Override
    public ProductFeeComponentDto convert(final ProductFeeComponent component) {
        final ProductFeeComponentDto feeComponentDto = new ProductFeeComponentDto();
        final FeeType feeType = component.getFeeType();
        if (feeType != null) {
            feeComponentDto.setFeeComponentName(component.getFeeComponentName());
            feeComponentDto.setFeeDateFrom(component.getFeeDateFrom());
            feeComponentDto.setFeeType(feeType.name());
            feeComponentDto.setFeeDateTo(component.getFeeDateTo());
            feeComponentDto.setCapFactor(component.getCapFactor());
            feeComponentDto.setCapMin(component.getCapMin());
            feeComponentDto.setCapMax(component.getCapMax());
            feeComponentDto.setCapOffSet(component.getCapOffSet());
            feeComponentDto.setTariffFactorMax(component.getTarrifFactorMax());
            feeComponentDto.setTariffOffSetFactorMax(component.getTarrifOffSetFactorMax());
        }
        return feeComponentDto;
    }
}
