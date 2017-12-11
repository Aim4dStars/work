package com.bt.nextgen.core.api.model;


import com.bt.nextgen.util.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Environment.class })
public class ApiResponseTest {

    @Before
    public void setup() {
        // base setup
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Environment.class);
    }

    @Test
    public void whenDataInResponseIsDeprecated_thenSupercededFlagIsTrue() {
        when(Environment.isProduction()).thenReturn(false);

        ApiResponse response = new ApiResponse("test", new SupercededDto());
        assertEquals(Boolean.TRUE, response.getSuperceded());
    }

    @Test
    public void whenDataInResponseIsNotDeprecated_thenSupercededFlagIsFalse() {
        when(Environment.isProduction()).thenReturn(false);

        ApiResponse response = new ApiResponse("test", new CurrentDto());
        assertEquals(Boolean.FALSE, response.getSuperceded());

    }

    @Test
    public void whenDataInResponseNull_thenSupercededFlagIsUndefined() {
        when(Environment.isProduction()).thenReturn(false);
        ApiResponse response = new ApiResponse("test", (Dto) null);
        assertNull(response.getSuperceded());
    }

    @Test
    public void whenInProductionEnvironment_thenSupercededFlagIsUndefined() {
        when(Environment.isProduction()).thenReturn(true);
        ApiResponse response = new ApiResponse("test", new CurrentDto());
        assertNull(response.getSuperceded());
    }

}

class CurrentDto extends BaseDto {

}

@Deprecated
class SupercededDto extends BaseDto {

}