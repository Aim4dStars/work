package com.bt.nextgen.api.movemoney.v2.model;

/**
 * Created by L069679 on 26/09/2017.
 */
public class BsbValidationDto {
    private boolean valid;
    private String bankName;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
