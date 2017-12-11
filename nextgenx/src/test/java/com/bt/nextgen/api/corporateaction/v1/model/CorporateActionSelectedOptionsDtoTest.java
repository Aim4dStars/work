package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionSelectedOptionsDtoTest {
    @Test
    public void testCorporateActionSelectedOptionsDto() {
        List<CorporateActionSelectedOptionDto> selectedOptionDtos = new ArrayList<>();
        selectedOptionDtos.add(new CorporateActionSelectedOptionDto(1, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE));

        CorporateActionSelectedOptionsDto dto = new CorporateActionSelectedOptionsDto(selectedOptionDtos, 1);

        assertNotNull(dto.getOptions());
        assertEquals((Integer) 1, dto.getMinimumPriceId());
        assertNotNull(dto.getPrimarySelectedOption());

        dto = new CorporateActionSelectedOptionsDto(new ArrayList<CorporateActionSelectedOptionDto>());
        assertNotNull(dto.getOptions());
        assertNull(dto.getMinimumPriceId());
    }
}
