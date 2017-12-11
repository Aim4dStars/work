package com.bt.nextgen.api.movemoney.v2.model;

public class EndPaymentDto extends PaymentDto {

    private Boolean hasDrawdownInprogress;

    public Boolean getHasDrawdownInprogress() {
        return hasDrawdownInprogress;
    }

    public void setHasDrawdownInprogress(Boolean hasDrawdownInprogress) {
        this.hasDrawdownInprogress = hasDrawdownInprogress;
    }
}
