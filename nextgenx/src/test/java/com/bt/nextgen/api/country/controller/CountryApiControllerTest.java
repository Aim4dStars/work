package com.bt.nextgen.api.country.controller;

import com.bt.nextgen.api.country.model.CountryCode;
import com.bt.nextgen.api.country.model.CountryDto;
import com.bt.nextgen.api.country.service.CountryDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountryApiControllerTest {

    @Mock
    private CountryDtoService service;

    @InjectMocks
    private CountryApiController controller;

    private ApiResponse response;

    @Test
    @SuppressWarnings("unchecked")
    public void getCountriesWillFetchAndSortCountriesByName() throws Exception {
        final CountryDto newZealand = new CountryDto("NZ", "New Zealand", "64", "NZL","NZ");
        final CountryDto niger = new CountryDto("NE", "Niger", "227", "NE","NE");
        final CountryDto morocco = new CountryDto("MA", "Morocco", "212", "MAR","MA");
        final CountryDto nicaragua = new CountryDto("NI", "Nicaragua", "505", "NIC", "NI");
        final CountryDto australia = new CountryDto("AU", "Australia", "61", "AUS","AU");
        final CountryDto mozambique = new CountryDto("MZ", "Mozambique", "258", "MOZ","MZ");
        when(service.findAll(any(ServiceErrors.class))).thenReturn(asList(newZealand, niger, mozambique, nicaragua, australia, morocco));
        response = controller.getCountries();
        List<CountryDto> countries = ((ResultListDto<CountryDto>) response.getData()).getResultList();
        assertThat(countries, contains(australia, morocco, mozambique, newZealand, nicaragua, niger));
    }

    @Test
    public void getCountryWithSingleCountryCode() throws Exception {
        final CountryDto morocco = new CountryDto("MA", "Morocco", "212", "MAR","MA");
        when(service.find(eq(new CountryCode("MA")), any(ServiceErrors.class))).thenReturn(morocco);
        response = controller.getCountry("ma");
        assertSame(morocco, response.getData());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getCountryWithMultipleCountryCodes() throws Exception {
        final CountryDto australia = new CountryDto("AU", "Australia", "61", "AUS","AU");
        final CountryDto newZealand = new CountryDto("NZ", "New Zealand", "64", "NZL","NZ");
        final CountryDto morocco = new CountryDto("MA", "Morocco", "212", "MAR","MA");
        when(service.find(eq(new CountryCode("AU")), any(ServiceErrors.class))).thenReturn(australia);
        when(service.find(eq(new CountryCode("MA")), any(ServiceErrors.class))).thenReturn(morocco);
        when(service.find(eq(new CountryCode("NZ")), any(ServiceErrors.class))).thenReturn(newZealand);
        response = controller.getCountry("au,nz,ma");
        List<CountryDto> countries = ((ResultListDto) response.getData()).getResultList();
        assertThat(countries, contains(australia, newZealand, morocco));
    }
}