package com.bt.nextgen.api.draftaccount.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@code ANZSICCodeDto}
 */
public class ANZSICCodeDtoTest {

    private ANZSICCodeDto code;

    private ObjectMapper mapper;

    @Before
    public void initAnzsicCodeAndMapper() {
        code = new ANZSICCodeDto("5710", "5710", "SIC 0317", "Accommodation and food services (H)",
                "Accommodation (H44)", "Accommodation (H440)", "Accommodation");
        mapper = new ObjectMapper();
    }

    @Test
    public void jsonSerializable() {
        assertTrue(mapper.canSerialize(ANZSICCodeDto.class));
    }

    @Test
    public void toJson() throws IOException {
        String expected = "{'key':'5710','code':'5710','ucmCode':'SIC 0317','industryDivision':'Accommodation and food services (H)','industrySubdivision':'Accommodation (H44)','industryGroup':'Accommodation (H440)','industryClass':'Accommodation','type':'ANZSICCode'}";
        expected = expected.replace('\'', '"');
        assertEquals(expected, mapper.writeValueAsString(code));
    }
}
