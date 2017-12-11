package com.bt.nextgen.api.client.util;

import static ch.lambdaj.Lambda.filter;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class to set ClientTxn attributes from domain objects
 */
public class ClientTxnDtoConverter {

    private static final String UNDER_AGE_EXEMPT = "TIN exempt - under age";
    private static final String UNDER_AGE = "Under age";

    /**
     * public constructor - sonar fix
     */
    private ClientTxnDtoConverter() {
    }

    /**
     * converts domain taxresidence to dto taxresidence Object
     *
     * @param taxResidenceCountries list of domain taxresidencecountries
     *
     * @return List<TaxResidenceCountriesDto> list of dto tax residence countries
     */
    public static List<TaxResidenceCountriesDto> convertTaxResidenceCountryDto(List<TaxResidenceCountry> taxResidenceCountries, StaticIntegrationService staticIntegrationService, ServiceErrors serviceErrors) {
        List<TaxResidenceCountriesDto> countriesDtos = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(taxResidenceCountries)){
            List<TaxResidenceCountry> filteredTaxCountries = filterTaxResidenceCountriesByValidity(taxResidenceCountries);
            if(CollectionUtils.isNotEmpty(filteredTaxCountries)){
                for (TaxResidenceCountry taxResidenceCountry : filteredTaxCountries) {
                    final TaxResidenceCountriesDto countriesDto = new TaxResidenceCountriesDto();
                    countriesDto.setTin(taxResidenceCountry.getTin());
                    countriesDto.setTaxResidenceCountry(taxResidenceCountry.getCountryName());
                    if(null!=taxResidenceCountry.getCountryCode()) {
                        countriesDto.setTaxResidencyCountryCode(staticIntegrationService.loadCode(CodeCategory.COUNTRY, taxResidenceCountry.getCountryCode().toString(), serviceErrors).getUserId());
                    }
                    if (taxResidenceCountry.getTinExemption() != null && UNDER_AGE.equalsIgnoreCase(taxResidenceCountry.getTinExemption())) {
                        countriesDto.setTaxExemptionReason(UNDER_AGE_EXEMPT);
                    }
                    else {
                        countriesDto.setTaxExemptionReason(taxResidenceCountry.getTinExemption());
                    }
                    if(null!=taxResidenceCountry.getTinExemptionCode()){
                        countriesDto.setTaxExemptionReasonCode(staticIntegrationService.loadCode(CodeCategory.TIN_EXEMPTION_REASONS, taxResidenceCountry.getTinExemptionCode().toString(), serviceErrors).getIntlId());
                    }
                    countriesDtos.add(countriesDto);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(countriesDtos) && countriesDtos.size() > 1) {
            getSortedCountryName(countriesDtos);
        }
        return countriesDtos;
    }

    private static void getSortedCountryName(List<TaxResidenceCountriesDto> countriesDtos) {
        Collections.sort(countriesDtos, new Comparator<TaxResidenceCountriesDto>() {
            @Override
            public int compare(TaxResidenceCountriesDto o1, TaxResidenceCountriesDto o2) {
                if ("Australia".equalsIgnoreCase(o1.getTaxResidenceCountry())) {
                    return -1;
                }
                if ("Australia".equalsIgnoreCase(o2.getTaxResidenceCountry())) {
                    return 1;
                }
                return (o1.getTaxResidenceCountry()).compareTo(o2.getTaxResidenceCountry());
            }
        });
    }

    public static List<TaxResidenceCountry> filterTaxResidenceCountriesByValidity(List<TaxResidenceCountry> taxResidenceCountries) {

            return filter(new LambdaMatcher<TaxResidenceCountry>() {

                @Override
                protected boolean matchesSafely(TaxResidenceCountry taxResidenceCountry) {
                    return null == taxResidenceCountry.getEndDate();
                }
            }, taxResidenceCountries);

    }
}