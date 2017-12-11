package com.bt.nextgen.core.security.api.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link ProductToggleEnum}
 * Created by M035995 on 4/11/2016.
 */
public class ProductToggleEnumTest {

    @Test
    public void testInsuranceProductCodes() {
        Assert.assertTrue(ProductToggleEnum.validateProductShortName(ProductToggleEnum.INSURANCE, "PROD.OFFER.65f52dc6d17421eaf1632ac7"));
        Assert.assertTrue(ProductToggleEnum.validateProductShortName(ProductToggleEnum.INSURANCE, "PROD.OFFER.66f52dc6d17421eaf1632ac7"));
        Assert.assertTrue(ProductToggleEnum.validateProductShortName(ProductToggleEnum.INSURANCE, "PROD.OFFER.65F52DC6D17421EAF1632AC7"));
        Assert.assertTrue(ProductToggleEnum.validateProductShortName(ProductToggleEnum.INSURANCE, "PROD.OFFER.67F52DC6D17421EAF1632AC7"));
    }
}