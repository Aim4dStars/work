package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IAusTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.AusTaxDetails;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.TaxOptionTypeEnum;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import org.apache.commons.lang.StringUtils;

/**
 * Created by L070354 on 12/01/2017.
 */
public class AusTaxDetailsForm implements IAusTaxDetailsForm {


    private final AusTaxDetails ausTaxDetails;

    public AusTaxDetailsForm(AusTaxDetails ausTaxDetails) {
        this.ausTaxDetails = ausTaxDetails;
    }


    @Override
    public String getTFN() {
        return null!=this.ausTaxDetails.getTfn()? this.ausTaxDetails.getTfn().getValue() : "";
    }

    @Override
    public TaxOptionTypeEnum getTaxOption() {
        return null!=this.ausTaxDetails.getTaxOption()? this.ausTaxDetails.getTaxOption().getValue(): null;
    }

    @Override
    public String getExemptionReason() {
        return null!=this.ausTaxDetails.getExemptionReason()? this.ausTaxDetails.getExemptionReason().getValue() : "";
    }

    @Override
    public boolean hasExemptionReason() {
        return StringUtils.isNotEmpty(getExemptionReason());
    }

    @Override
    public boolean isTaxCountryAustralia() {
        return null!=this.ausTaxDetails.getIsTaxCountryAus()? this.ausTaxDetails.getIsTaxCountryAus().getValue(): false;
    }

    @Override
    public boolean hasTaxFileNumber() {
        return StringUtils.isNotBlank(this.getTFN());
    }

}
