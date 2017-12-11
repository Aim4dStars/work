package com.bt.nextgen.api.draftaccount.model.form.v1;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.model.form.IAusTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.ICrsTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOverseasTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.OverseasTaxDetails;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.StringValue;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.TaxDetails;

import java.util.List;

/**
 * Created by L070354 on 12/01/2017.
 */
public class CrsTaxDetailsForm implements ICrsTaxDetailsForm {

    private final TaxDetails taxdetails;

    public CrsTaxDetailsForm(TaxDetails taxdetails) {
        this.taxdetails = taxdetails;
    }


    @Override
    public IAusTaxDetailsForm getAustralianTaxDetails() {
        return new AusTaxDetailsForm(this.taxdetails.getAusTaxDetails());
    }

    @Override
    public List<IOverseasTaxDetailsForm> getOverseasTaxDetails() {
        return Lambda.convert(this.taxdetails.getOverseasTaxDetails(), new Converter<OverseasTaxDetails, IOverseasTaxDetailsForm>() {
            @Override
            public IOverseasTaxDetailsForm convert(OverseasTaxDetails overseasTaxDetails) {
                return new OverseasTaxDetailsForm(overseasTaxDetails);
            }
        });
    }

    @Override
    public void setOverseasTaxDetails(List<OverseasTaxDetails> overseasTaxDetails) {
        if(hasCrsTaxDetails()){
            this.taxdetails.setOverseasTaxDetails(overseasTaxDetails);
        }
    }

    @Override
    public String getOverseasTaxCountry() {
        return this.hasOverseasTaxCountry() ? this.taxdetails.getSelectedOverseasTaxCountry().getValue(): "";
    }

    @Override
    public void setOverseasTaxCountry(StringValue value) {
        if(this.hasOverseasTaxCountry()){
            this.taxdetails.setSelectedOverseasTaxCountry(value);
        }
    }

    @Override
    public boolean hasCrsTaxDetails() {
        return null!=this.taxdetails;
    }

    @Override
    public boolean hasOverseasTaxCountry() {
        return null!=this.taxdetails.getSelectedOverseasTaxCountry();
    }

}
