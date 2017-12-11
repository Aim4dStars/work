package com.bt.nextgen.core.security.api.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * The products below are used as feature toggles on the UI
 *
 */

//Suppressing this known issue --> http://jira.sonarsource.com/browse/SONARJAVA-917
@SuppressWarnings("squid:S1948")
public enum ProductToggleEnum {
    NEW_SMSF_INDIVIDUAL(
            "PROD.OFFER.60f52dc6d17421eaf1632ac9e",
            "PROD.OFFER.d1b65704184ae3b87799400f7ab",
            "PROD.OF.COMP.060F52DC6D17421EAF1632AC9EFAE210.FEIT",
            "PROD.OF.COMP.35D1B65704184AE3B87799400F7AB93C.FEIT"),
    NEW_SMSF_CORPORATE(
            "PROD.OFFER.60f52dc6d17421eaf1632ac9",
            "PROD.OFFER.d1b65704184ae3b87799400f7a",
            "PROD.OF.COMP.060F52DC6D17421EAF1632AC9EFAE210.FECT",
            "PROD.OF.COMP.35D1B65704184AE3B87799400F7AB93C.FECT"),
    INSURANCE(
            "PROD.OFFER.65f52dc6d17421eaf1632ac7",
            "PROD.OFFER.66f52dc6d17421eaf1632ac7",
            "PROD.OFFER.67F52DC6D17421EAF1632AC7",
            "PROD.OF.COMP.060F52DC6D17421EAF1632AC9EFAE210.INSR",
            "PROD.OF.COMP.E76E2D220C254EB99CD02CF0B3687796.INSR",
            "PROD.OF.COMP.35D1B65704184AE3B87799400F7AB93C.INSR",
            "PROD.OF.COMP.797475D1E1B246528C49EF8A75A9315E.INSR",
            "PROD.OF.COMPH.35D1B65704184AE3B87799400F7AB93C.INSR",
            "PROD.OF.COMPH.797475D1E1B246528C49EF8A75A9315E.INSR",
            "PROD.OFFER.67F52DC6D17421EAF1632AC7.DIR");


    private List<String> productShortNameList = new ArrayList<>();

    ProductToggleEnum(String... productShortName) {
        this.productShortNameList = Arrays.asList(productShortName);
    }

    public List<String> getProductShortNameList() {
        return productShortNameList;
    }

    /**
     * This method validates whether the productShortName is valid or not
     *
     * @param productToggleEnum instance of {@link ProductToggleEnum}
     * @param productName       name of the product
     *
     * @return true, if the product name is valid; else false
     */
    public static boolean validateProductShortName(final ProductToggleEnum productToggleEnum, final String productName) {
        for (String productShortName : productToggleEnum.getProductShortNameList()) {
            if (productName.equalsIgnoreCase(productShortName)) {
                return true;
            }
        }
        return false;
    }
}
