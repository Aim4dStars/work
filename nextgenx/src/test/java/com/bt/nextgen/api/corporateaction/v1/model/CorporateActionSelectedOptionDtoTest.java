package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionSelectedOptionDtoTest {
    @Test
    public void testCorporateActionSelectedOptionDto() {
        CorporateActionSelectedOptionDto dto1 = new CorporateActionSelectedOptionDto();
        CorporateActionSelectedOptionDto dto2 = new CorporateActionSelectedOptionDto();
        CorporateActionSelectedOptionDto dto3 = new CorporateActionSelectedOptionDto();
        CorporateActionSelectedOptionDto dto4 = new CorporateActionSelectedOptionDto();
        CorporateActionSelectedOptionDto dto5 = new CorporateActionSelectedOptionDto();
        CorporateActionDto otherDto =  new CorporateActionDto();

        dto1.setOptionId(1);
        dto1.setOversubscribe(BigDecimal.ONE);
        dto1.setUnits(BigDecimal.TEN);

        dto2.setOptionId(1);
        dto2.setOversubscribe(BigDecimal.ONE);
        dto2.setUnits(BigDecimal.TEN);

        dto3.setOptionId(2);
        dto3.setOversubscribe(BigDecimal.ONE);
        dto3.setUnits(BigDecimal.ONE);

        assertFalse(dto1.equals(null));
        assertTrue(dto1.equals(dto1));
        assertTrue(dto1.equals(dto2));
        assertFalse(dto1.equals(dto3));
        assertFalse(dto1.equals(otherDto));

        assertTrue(dto4.equals(dto5));
        assertFalse(dto1.equals(dto4));
        assertFalse(dto4.equals(dto1));

        dto2.setUnits(null);
        assertFalse(dto1.equals(dto2));
        assertFalse(dto2.equals(dto1));

        dto1.setUnits(null);
        assertTrue(dto1.equals(dto2));

        dto1.setUnits(BigDecimal.TEN);
        dto2.setUnits(BigDecimal.ONE);
        assertFalse(dto1.equals(dto2));

        dto2.setUnits(BigDecimal.TEN);
        dto2.setOversubscribe(BigDecimal.TEN);
        assertFalse(dto1.equals(dto2));

        assertTrue(dto1.hashCode() > 0);

        dto1.setUnits(null);
        assertTrue(dto1.hashCode() > 0);

        dto1.setUnits(BigDecimal.TEN);
        dto1.setOversubscribe(null);
        assertTrue(dto1.hashCode() > 0);
    }
}
