package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.builder.v3.TINExemptionEnum;
import com.bt.nextgen.api.draftaccount.model.form.IOverseasTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.OverseasTaxDetails;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.TinOptionTypeEnum;

/**
 * Created by L070354 on 12/01/2017.
 */
public class OverseasTaxDetailsForm implements IOverseasTaxDetailsForm {

    private final OverseasTaxDetails overseasTaxDetails;

    public OverseasTaxDetailsForm(OverseasTaxDetails overseasTaxDetails) {
        this.overseasTaxDetails = overseasTaxDetails;
    }

    @Override
    public String getTIN() {
        return null!=this.overseasTaxDetails.getTin()? this.overseasTaxDetails.getTin().getValue():"";
    }

    @Override
    public TinOptionTypeEnum getTINOption() {
        return null!=this.overseasTaxDetails.getTinOption()? this.overseasTaxDetails.getTinOption().getValue(): null;
    }

    @Override
    public String getTINExemptionReason() {
        //Hack to circumvent the extra Conversion in the DTO Conversion layer (ClientTxnDtoConverter) where the TIn Exemption under age is being renamed.
        if(null!=this.overseasTaxDetails.getTinExemptionReason() && "TIN exempt - under age".equals(this.overseasTaxDetails.getTinExemptionReason().getValue())){
                this.overseasTaxDetails.getTinExemptionReason().setValue("btfg$under_aged");
        }
        return null!=this.overseasTaxDetails.getTinExemptionReason()? this.overseasTaxDetails.getTinExemptionReason().getValue(): null;
    }

    @Override
    public String getOverseasTaxCountry() {
        return null!=this.overseasTaxDetails.getOverseasTaxCountry()? this.overseasTaxDetails.getOverseasTaxCountry().getValue(): "";
    }
}
