package com.bt.nextgen.core.web.model;

/**
 * Created by L062329 on 13/05/2015.
 */
public class Accountant extends Intermediary {

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    private String companyName;
}
