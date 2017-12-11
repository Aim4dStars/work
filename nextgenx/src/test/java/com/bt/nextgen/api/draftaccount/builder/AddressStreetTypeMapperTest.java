package com.bt.nextgen.api.draftaccount.builder;

import com.bt.nextgen.api.draftaccount.FormDataValidator;
import com.bt.nextgen.api.draftaccount.FormDataValidatorImpl;
import com.bt.nextgen.api.draftaccount.controller.ClientApplicationDtoDeserializer;
import com.bt.nextgen.config.JsonObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Tests for the AddressStreetTypeMapper, via Spring.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AddressStreetTypeMapperTest.Config.class)
public class AddressStreetTypeMapperTest {

    @Autowired
    private AddressStreetTypeMapper mapper;

    @Test
    public void getStandardStreetTypeWithNullReturnsEmptyString() {
        assertThat(mapper.getStandardStreetType(null), is(""));
    }

    @Test
    public void getStandardStreetTypeWithBlankReturnsEmptyString() {
        assertThat(mapper.getStandardStreetType("\t"), is(""));
    }

    @Test
    public void getStandardStreetTypeWithRegularValues() {
        assertThat(mapper.getStandardStreetType("Cul-de-Sac"), is("CDS"));
        assertThat(mapper.getStandardStreetType("Right of way"), is("ROWY"));
        assertThat(mapper.getStandardStreetType("RoseBowl"), is("RSBL"));
        assertThat(mapper.getStandardStreetType("STATE HIGHWAY"), is("SHWY"));
        assertThat(mapper.getStandardStreetType("Street"), is("ST"));
        assertThat(mapper.getStandardStreetType("thoroughfare"), is("THOR"));
    }

    /**
     * New test to check that underscored values also get correctly abbreviated. It was noticed that the Web UI was
     * storing these uncommon street types with underscores in the JSON client application document.
     */
    @Test
    public void getStandardStreetTypeWithUnderscoredValues() {
        assertThat(mapper.getStandardStreetType("CUL_DE_SAC"), is("CDS"));
        assertThat(mapper.getStandardStreetType("FIRE_TRACK"), is("FTRK"));
        assertThat(mapper.getStandardStreetType("RIGHT_OF_WAY"), is("ROWY"));
        assertThat(mapper.getStandardStreetType("SERVICE_WAY"), is("SWY"));
        assertThat(mapper.getStandardStreetType("STATE_HIGHWAY"), is("SHWY"));
    }

    @Configuration
    public static class Config {

        @Bean
        public AddressStreetTypeMapper mapper() {
            return new AddressStreetTypeMapper();
        }

        @Bean
        public ObjectMapper jsonObjectMapper() {
            return new JsonObjectMapper();
        }

        @Bean
        public FormDataValidator formDataValidator() {
            return new FormDataValidatorImpl();
        }

        @Bean
        public ClientApplicationDtoDeserializer clientApplicationDtoDeserializer() {
            return new  ClientApplicationDtoDeserializer();
        }
    }
}