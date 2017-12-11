package com.bt.nextgen.api.fees.v2.model;

import java.util.Map;

/**
 * Created by l078480 on 29/11/2016.
 */

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
