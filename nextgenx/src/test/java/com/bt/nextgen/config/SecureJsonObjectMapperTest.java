package com.bt.nextgen.config;

import com.fasterxml.jackson.databind.MapperFeature;
import org.junit.Assert;
import org.junit.Test;

public class SecureJsonObjectMapperTest {

    private SecureJsonObjectMapper secureMapper;

    @Test
    public void testConstructor_initialisesSecureConfig() throws Exception {
        secureMapper = new SecureJsonObjectMapper();
        Assert.assertEquals(false, secureMapper.getDeserializationConfig().isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
    }

}
