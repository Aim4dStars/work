package com.bt.nextgen.api.draftaccount.model.form;

import org.apache.commons.lang.StringUtils;

import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

/**
 * @deprecated Use the v1 version of this class instead.
 */
@Deprecated
class TaxDetailsForm extends Correlated implements ITaxDetailsForm {

    public TaxDetailsForm(Map<String, Object> map) {
        super(map);
    }

    public boolean hasTaxFileNumber() {
        return hasText(getTaxFileNumber());
    }

    public String getTaxFileNumber() {
        return (String) map.get("tfn");
    }

    public String getExemptionReason() {
        return (String) map.get("exemptionreason");
    }

    public boolean hasExemptionReason() {
        return !StringUtils.isEmpty(getExemptionReason());
    }

    public String getTaxCountryCode() {
        return (String) map.get("taxcountry");
    }

    @Override
    public String getTaxoption() {
        return (String) map.get("taxoption");
    }
}
