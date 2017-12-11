package com.bt.nextgen.service.avaloq.insurance.model;

/**
 * Class to handle policy payment method
 */
public enum PaymentType {

    WCACC("Working cash account"),
    DIDEB("Direct debit"),
    CC("Credit card"),
    RLN("Roll in"),
    DICAS("Cash");

    private String value;

    PaymentType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name();
    }

}
