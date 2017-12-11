/**
 * 
 */
package com.bt.nextgen.serviceops.model;

/**
 * @author L081050
 */
public class RetrieveIDVDetailsReqModel {
    private String cisKey;

    private String silo;

    private String personType;

    public String getCisKey() {
        return cisKey;
    }

    public void setCisKey(String cisKey) {
        this.cisKey = cisKey;
    }

    public String getSilo() {
        return silo;
    }

    public void setSilo(String silo) {
        this.silo = silo;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

}
