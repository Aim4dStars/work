package com.bt.nextgen.api.draftaccount.model.form;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface ITaxDetailsForm {
    public boolean hasTaxFileNumber();

    public String getTaxFileNumber();

    public String getExemptionReason();

    public boolean hasExemptionReason();

    public String getTaxCountryCode();

    String getTaxoption();
}
