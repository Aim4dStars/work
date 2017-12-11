package com.bt.nextgen.api.corporateaction.v1.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class ImCorporateActionDtoTest {
    @Test
    public void testImCorporateActionDto() {
        ImCorporateActionDto dto = new ImCorporateActionDto();

        assertNull(dto.getKey());
    }
}
