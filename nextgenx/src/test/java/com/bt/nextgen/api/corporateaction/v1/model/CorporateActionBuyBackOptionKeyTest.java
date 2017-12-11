package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionBuyBackOptionKeyTest {
    @Test
    public void testCorporateActionBuyBackOptionKey() {
        List<CorporateActionSelectedOptionDto> options1 = new ArrayList<>();
        List<CorporateActionSelectedOptionDto> options2 = new ArrayList<>();
        List<CorporateActionSelectedOptionDto> options3 = new ArrayList<>();
        CorporateActionSelectedOptionDto optionDto = new CorporateActionSelectedOptionDto();

        options1.add(new CorporateActionSelectedOptionDto(1, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE));
        options1.add(new CorporateActionSelectedOptionDto(2, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE));
        options2.add(new CorporateActionSelectedOptionDto(3, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE));
        options3.add(new CorporateActionSelectedOptionDto(1, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE));
        options3.add(new CorporateActionSelectedOptionDto(2, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));

        CorporateActionBuyBackOptionKey key1 = new CorporateActionBuyBackOptionKey(options1, 10);
        CorporateActionBuyBackOptionKey key2 = new CorporateActionBuyBackOptionKey(options1, 10);
        CorporateActionBuyBackOptionKey key3 = new CorporateActionBuyBackOptionKey(options1, 1);
        CorporateActionBuyBackOptionKey key4 = new CorporateActionBuyBackOptionKey(options2, 10);
        CorporateActionBuyBackOptionKey key5 = new CorporateActionBuyBackOptionKey(null, 10);
        CorporateActionBuyBackOptionKey key6 = new CorporateActionBuyBackOptionKey(new ArrayList<CorporateActionSelectedOptionDto>(), 10);
        CorporateActionBuyBackOptionKey key7 = new CorporateActionBuyBackOptionKey(options3, 10);
        CorporateActionBuyBackOptionKey key8 = new CorporateActionBuyBackOptionKey(null, 10);
        CorporateActionBuyBackOptionKey key9 = new CorporateActionBuyBackOptionKey(options1, null);
        CorporateActionBuyBackOptionKey key10 = new CorporateActionBuyBackOptionKey(options1, null);

        assertTrue(key1.equals(key1));
        assertTrue(key1.equals(key2));
        assertFalse(key1.equals(key3));
        assertFalse(key3.equals(key1));
        assertFalse(key1.equals(key4));
        assertFalse(key1.equals(key6));
        assertFalse(key2.equals(key6));
        assertFalse(key5.equals(key6));
        assertFalse(key1.equals(null));
        assertFalse(key1.equals(optionDto));
        assertFalse(key1.equals(key7));
        assertFalse(key1.equals(key5));
        assertTrue(key5.equals(key8));
        assertTrue(key9.equals(key10));
        assertFalse(key1.equals(key9));
        assertFalse(key10.equals(key1));

        assertTrue(key1.hashCode() > 0);
        assertTrue(key9.hashCode() > 0);
        assertTrue(key5.hashCode() > 0);
    }
}
