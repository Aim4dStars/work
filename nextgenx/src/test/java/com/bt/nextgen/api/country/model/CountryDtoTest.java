package com.bt.nextgen.api.country.model;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class CountryDtoTest {

    private CountryDto country;

    private ObjectMapper mapper;

    @Before
    public void initAnzsicCodeAndMapper() {
        country = new CountryDto("AU", "Australia", "61", "AUS", "AU");
        mapper = new ObjectMapper();
    }

    @Test
    public void jsonSerializable() {
        assertTrue(mapper.canSerialize(CountryDto.class));
    }

    @Test
    public void toJson() throws IOException {
        String expected = "{'name':'Australia','diallingCode':'61','ucmCode':'AUS','imCode':'AU','key':{'code':'AU'},'type':'Country'}";
        expected = expected.replace('\'', '"');
        assertEquals(expected, mapper.writeValueAsString(country));
    }
}