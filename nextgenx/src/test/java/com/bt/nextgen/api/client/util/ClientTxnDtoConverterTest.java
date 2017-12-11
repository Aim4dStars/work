package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.client.TaxResidenceCountryImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.joda.time.DateTime;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by L070353 on 22/01/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientTxnDtoConverterTest {

    @Mock
    StaticIntegrationService staticIntegrationService;

    @Mock
    private Code code1;

    @Mock
    private Code code2;

    ServiceErrors serviceErrors;

    @Before
    public void setUp(){
        when(staticIntegrationService.loadCode(eq(CodeCategory.COUNTRY), any(String.class), any(ServiceErrors.class))).thenReturn(code1);
        when(staticIntegrationService.loadCode(eq(CodeCategory.TIN_EXEMPTION_REASONS), any(String.class), any(ServiceErrors.class))).thenReturn(code2);
    }

    @Test
    public void testConvertTaxResidenceCountryDto() {
        List<TaxResidenceCountry> countriesDtos = getTaxResidenceCountriesWithAustralia();
        List<TaxResidenceCountriesDto> taxResidenceCountriesDtos = ClientTxnDtoConverter.convertTaxResidenceCountryDto(countriesDtos, staticIntegrationService, serviceErrors);
        assertNotNull(taxResidenceCountriesDtos);
        assertEquals("Australia", taxResidenceCountriesDtos.get(0).getTaxResidenceCountry());
        assertEquals("Afghanistan", taxResidenceCountriesDtos.get(1).getTaxResidenceCountry());
        assertEquals("TIN exempt - under age", taxResidenceCountriesDtos.get(1).getTaxExemptionReason());
        assertEquals("Singapore", taxResidenceCountriesDtos.get(2).getTaxResidenceCountry());
        assertEquals("United Kingdom", taxResidenceCountriesDtos.get(3).getTaxResidenceCountry());
    }

    @Test
    public void testConvertTaxResidenceCountryDtoWithoutAustralia() {
        List<TaxResidenceCountry> countriesDtos = getTaxResidenceCountriesWithOutAustralia();
        List<TaxResidenceCountriesDto> taxResidenceCountriesDtos = ClientTxnDtoConverter.convertTaxResidenceCountryDto(countriesDtos, staticIntegrationService, serviceErrors);
        assertNotNull(taxResidenceCountriesDtos);
        assertEquals("Afghanistan", taxResidenceCountriesDtos.get(0).getTaxResidenceCountry());
        assertEquals("Singapore", taxResidenceCountriesDtos.get(1).getTaxResidenceCountry());
        assertEquals("United Kingdom", taxResidenceCountriesDtos.get(2).getTaxResidenceCountry());
    }

    @Test
    public void testConvertTaxResidenceCountryDto_WithoutTaxCountries() {
        List<TaxResidenceCountry> countriesDtos = new ArrayList<>();
        List<TaxResidenceCountriesDto> taxResidenceCountriesDtos = ClientTxnDtoConverter.convertTaxResidenceCountryDto(countriesDtos, staticIntegrationService, serviceErrors);
        assertNotNull(taxResidenceCountriesDtos);
        assertTrue(taxResidenceCountriesDtos.isEmpty());
    }

    @Test
    public void testConvertTaxResidenceCountryDto_AllInactiveCountries() {
        List<TaxResidenceCountry> countriesDtos = getAllInactiveCountries();
        List<TaxResidenceCountriesDto> taxResidenceCountriesDtos = ClientTxnDtoConverter.convertTaxResidenceCountryDto(countriesDtos, staticIntegrationService, serviceErrors);
        assertNotNull(taxResidenceCountriesDtos);
        assertTrue(taxResidenceCountriesDtos.isEmpty());
    }
    @Test
    public void testStaticCodesForConvertTaxResidency(){
        List<TaxResidenceCountry> countriesDtos = getTaxResidenceCountries();
        when(staticIntegrationService.loadCode(eq(CodeCategory.COUNTRY), eq("2061"), any(ServiceErrors.class))).thenReturn(code1);
        when(code1.getUserId()).thenReturn("AU");
        when(staticIntegrationService.loadCode(eq(CodeCategory.TIN_EXEMPTION_REASONS), eq("1022"), any(ServiceErrors.class))).thenReturn(code2);
        when(code2.getIntlId()).thenReturn("tin");
        List<TaxResidenceCountriesDto> taxResidenceCountriesDtos = ClientTxnDtoConverter.convertTaxResidenceCountryDto(countriesDtos, staticIntegrationService, serviceErrors);
        assertEquals(taxResidenceCountriesDtos.get(0).getTaxResidencyCountryCode(),"AU");
        assertEquals(taxResidenceCountriesDtos.get(0).getTaxExemptionReasonCode(),"tin");
    }


    private List<TaxResidenceCountry> getTaxResidenceCountries() {
        List<TaxResidenceCountry> countriesDtos = new ArrayList<>();
        TaxResidenceCountryImpl taxResidenceCountry2 = new TaxResidenceCountryImpl();
        taxResidenceCountry2.setCountryCode(Integer.valueOf("2061"));
        taxResidenceCountry2.setCountryName("Australia");
        taxResidenceCountry2.setTin("");
        taxResidenceCountry2.setTinExemption("TIN never issued");
        taxResidenceCountry2.setTinExemptionCode(Integer.valueOf("1022"));
        countriesDtos.add(taxResidenceCountry2);
        return countriesDtos;
    }

    private List<TaxResidenceCountry> getTaxResidenceCountriesWithAustralia() {
        List<TaxResidenceCountry> countriesDtos = new ArrayList<>();
        TaxResidenceCountryImpl taxResidenceCountry1 = new TaxResidenceCountryImpl();
        taxResidenceCountry1.setCountryCode(Integer.valueOf("2007"));
        taxResidenceCountry1.setCountryName("Singapore");
        taxResidenceCountry1.setTin("");
        taxResidenceCountry1.setTinExemption("TIN never issued");
        taxResidenceCountry1.setTinExemptionCode(Integer.valueOf("1022"));
        countriesDtos.add(taxResidenceCountry1);


        TaxResidenceCountryImpl taxResidenceCountry2 = new TaxResidenceCountryImpl();
        taxResidenceCountry2.setCountryCode(Integer.valueOf("2061"));
        taxResidenceCountry2.setCountryName("Australia");
        taxResidenceCountry2.setTin("");
        taxResidenceCountry2.setTinExemption("TIN never issued");
        taxResidenceCountry2.setTinExemptionCode(Integer.valueOf("1022"));
        countriesDtos.add(taxResidenceCountry2);

        TaxResidenceCountryImpl taxResidenceCountry3 = new TaxResidenceCountryImpl();
        taxResidenceCountry3.setCountryCode(Integer.valueOf("2170"));
        taxResidenceCountry3.setCountryName("United Kingdom");
        taxResidenceCountry3.setTin("");
        taxResidenceCountry3.setTinExemption("TIN never issued");
        taxResidenceCountry3.setTinExemptionCode(Integer.valueOf("1022"));
        countriesDtos.add(taxResidenceCountry3);

        TaxResidenceCountryImpl taxResidenceCountry4 = new TaxResidenceCountryImpl();
        taxResidenceCountry4.setCountryCode(Integer.valueOf("2046"));
        taxResidenceCountry4.setCountryName("Afghanistan");
        taxResidenceCountry4.setTin("");
        taxResidenceCountry4.setTinExemption("Under age");
        taxResidenceCountry4.setTinExemptionCode(Integer.valueOf("1020"));
        countriesDtos.add(taxResidenceCountry4);


        return countriesDtos;
    }


    private List<TaxResidenceCountry> getTaxResidenceCountriesWithOutAustralia() {
        List<TaxResidenceCountry> countriesDtos = new ArrayList<>();

        TaxResidenceCountryImpl taxResidenceCountry1 = new TaxResidenceCountryImpl();
        taxResidenceCountry1.setCountryCode(Integer.valueOf("2007"));
        taxResidenceCountry1.setCountryName("Singapore");
        taxResidenceCountry1.setTin("");
        taxResidenceCountry1.setTinExemption("TIN never issued");
        taxResidenceCountry1.setTinExemptionCode(Integer.valueOf("1022"));
        countriesDtos.add(taxResidenceCountry1);

        TaxResidenceCountryImpl taxResidenceCountry3 = new TaxResidenceCountryImpl();
        taxResidenceCountry3.setCountryCode(Integer.valueOf("2170"));
        taxResidenceCountry3.setCountryName("United Kingdom");
        taxResidenceCountry3.setTin("");
        taxResidenceCountry3.setTinExemption("TIN never issued");
        taxResidenceCountry3.setTinExemptionCode(Integer.valueOf("1022"));
        countriesDtos.add(taxResidenceCountry3);

        TaxResidenceCountryImpl taxResidenceCountry4 = new TaxResidenceCountryImpl();
        taxResidenceCountry4.setCountryCode(Integer.valueOf("2046"));
        taxResidenceCountry4.setCountryName("Afghanistan");
        taxResidenceCountry4.setTin("");
        taxResidenceCountry4.setTinExemption("TIN never issued");
        taxResidenceCountry4.setTinExemptionCode(Integer.valueOf("1022"));
        countriesDtos.add(taxResidenceCountry4);

        return countriesDtos;
    }

    private List<TaxResidenceCountry> getAllInactiveCountries() {
        List<TaxResidenceCountry> countriesDtos = new ArrayList<>();

        TaxResidenceCountryImpl taxResidenceCountry1 = new TaxResidenceCountryImpl();
        taxResidenceCountry1.setCountryCode(Integer.valueOf("2007"));
        taxResidenceCountry1.setCountryName("Singapore");
        taxResidenceCountry1.setTin("");
        taxResidenceCountry1.setTinExemption("TIN never issued");
        taxResidenceCountry1.setTinExemptionCode(Integer.valueOf("1022"));
        taxResidenceCountry1.setEndDate(new DateTime());
        countriesDtos.add(taxResidenceCountry1);

        TaxResidenceCountryImpl taxResidenceCountry3 = new TaxResidenceCountryImpl();
        taxResidenceCountry3.setCountryCode(Integer.valueOf("2170"));
        taxResidenceCountry3.setCountryName("United Kingdom");
        taxResidenceCountry3.setTin("");
        taxResidenceCountry3.setTinExemption("TIN never issued");
        taxResidenceCountry3.setTinExemptionCode(Integer.valueOf("1022"));
        taxResidenceCountry3.setEndDate(new DateTime());
        countriesDtos.add(taxResidenceCountry3);

        return countriesDtos;
    }
}