package com.bt.nextgen.api.draftaccount.model.form;

import java.util.Map;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
class FeeComponentTier implements IFeeComponentTier{

    private final Map<String, String> map;

    public FeeComponentTier(Map<String, String> map) {
        this.map = map;
    }

    public String getLowerBound() {
        return map.get("lowerBound");
    }

    public String getUpperBound() {
        return map.get("upperBound");
    }

    public String getPercentage() {
        return map.get("percentage");
    }
}
