package com.bt.nextgen.api.fees.v1.model;

/**
 * Created by l078480 on 29/11/2016.
 */

/**
 * @deprecated Use V2
 */
@Deprecated
public class PercentageFee extends BaseFeeType {

    public String getTariffFactor() {
        return tariffFactor;
    }

    public void setTariffFactor(String tariffFactor) {
        this.tariffFactor = tariffFactor;
    }

    private String tariffFactor;

}
