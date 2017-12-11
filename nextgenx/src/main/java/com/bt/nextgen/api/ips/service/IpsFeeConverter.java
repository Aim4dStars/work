package com.bt.nextgen.api.ips.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.ips.model.IpsFeeDto;
import com.bt.nextgen.service.integration.ips.IpsTariff;
import com.bt.nextgen.service.integration.ips.IpsTariffBoundary;

import java.math.BigDecimal;

public class IpsFeeConverter {

    private IpsFeeConverter() {

    }

    public static IpsFeeDto toFlatFeeDto(IpsTariff tariff) {
        if (tariff.getTariffBndList() != null && !tariff.getTariffBndList().isEmpty()) {
            return (IpsFeeDto) Lambda.convert(tariff.getTariffBndList(), new Converter<IpsTariffBoundary, IpsFeeDto>() {
                @Override
                public IpsFeeDto convert(IpsTariffBoundary tariffBnd) {

                    return IpsFeeConverter.toTieredFeeDto(tariffBnd);

                }
            });

        } else {
            return new IpsFeeDto(tariff.getBoundFrom(), tariff.getBoundTo(), tariff.getTariffFactor()
                    .multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }

    protected static IpsFeeDto toTieredFeeDto(IpsTariffBoundary tariffBnd) {
        return new IpsFeeDto(tariffBnd.getBoundFrom(), tariffBnd.getBoundTo(), tariffBnd.getTariffFactor()
                .multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

}
