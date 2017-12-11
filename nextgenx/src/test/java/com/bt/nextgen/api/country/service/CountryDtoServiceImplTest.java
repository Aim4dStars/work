package com.bt.nextgen.api.country.service;

import com.bt.nextgen.api.country.model.CountryCode;
import com.bt.nextgen.api.country.model.CountryDto;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.MockStaticIntegrationService;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.test.AttributeMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static ch.lambdaj.Lambda.selectFirst;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CountryDtoServiceImplTest.Config.class)
public class CountryDtoServiceImplTest {

    @Autowired
    private CountryDtoService countryDtoService;

    private ServiceErrors errors;

    @Before
    public void initErrors() {
        errors = new FailFastErrorsImpl();
    }

    @Test
    public void findAll() throws Exception {
        final List<CountryDto> countries = countryDtoService.findAll(errors);
        assertEquals(259, countries.size());
        Matcher<CountryDto> withCountryCodeBV = new AttributeMatcher<>("key", new CountryCode("BV"));
        CountryDto bouvetIsland = selectFirst(countries, withCountryCodeBV);
        assertCountry(bouvetIsland, "BV", "Bouvet Island", null, "BVT", "BV");
    }

    @Test
    public void findWithKnownCountryCode() throws Exception {
        final CountryDto australia = countryDtoService.find(CountryCode.AU, errors);
        assertCountry(australia, CountryCode.AU, "Australia", "61", "AUS","AU");
    }

    @Test
    public void findWithUnknownCountryCode() throws Exception {
        assertNull(countryDtoService.find(new CountryCode("XX"), errors));
    }

    private static void assertCountry(CountryDto country, CountryCode code, String name, String diallingCode, String ucmCode, String imCode) {
        assertEquals(code, country.getKey());
        assertEquals(name, country.getName());
        if (diallingCode == null) {
            assertNull(country.getDiallingCode());
        } else {
            assertEquals(diallingCode, country.getDiallingCode());
        }
        assertEquals(ucmCode, country.getUcmCode());
        assertEquals(imCode, country.getImCode());
    }

    private static void assertCountry(CountryDto country, String code, String name, String diallingCode, String ucmCode, String imCode) {
        assertCountry(country, new CountryCode(code), name, diallingCode, ucmCode, imCode);
    }

    @Configuration
    public static class Config {

        @Bean
        public StaticIntegrationService staticIntegrationService() {
            return new MockStaticIntegrationService();
        }

        @Bean
        public CountryDtoService countryDtoService() {
            return new CountryDtoServiceImpl();
        }
    }
}