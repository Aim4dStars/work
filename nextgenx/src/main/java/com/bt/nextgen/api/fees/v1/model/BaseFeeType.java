package com.bt.nextgen.api.fees.v1.model;

import java.util.Map;

/**
 * Created by l078480 on 29/11/2016.
 */

/**
 * @deprecated Use V2
 */
@Deprecated
public class BaseFeeType {

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private String label;

    public Map<String, String> getFees() {
        return fees;
    }

    public void setFees(Map<String, String> fees) {
        this.fees = fees;
    }

    private Map<String, String> fees;
}
