package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.ITaxDetailsForm;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Implementation of the {@code ITaxDetailsForm} interface.
 */
class TaxDetailsForm implements ITaxDetailsForm {

    private final String taxFileNumber;
    private final String exemptionReason;
    private final String taxCountrycode;
    private final String taxoption;

    TaxDetailsForm(String taxFileNumber, String exemptionReason, String taxCountrycode, String taxoption) {
        this.taxFileNumber = taxFileNumber;
        this.exemptionReason = exemptionReason;
        this.taxCountrycode = taxCountrycode;
        this.taxoption = taxoption;
    }

    @Override
    public boolean hasTaxFileNumber() {
        return isNotBlank(this.taxFileNumber);
    }

    @Override
    public String getTaxFileNumber() {  return this.taxFileNumber; }

    @Override
    public String getExemptionReason() {
        return this.exemptionReason;
    }

    @Override
    public boolean hasExemptionReason() {
        return isNotBlank(exemptionReason);
    }

    @Override
    public String getTaxCountryCode() {
        return this.taxCountrycode;
    }

    @Override
    public String getTaxoption() {
        return taxoption;
    }

}
