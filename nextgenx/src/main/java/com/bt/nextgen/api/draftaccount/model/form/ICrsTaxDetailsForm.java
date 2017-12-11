package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.OverseasTaxDetails;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.StringValue;

import java.util.List;

/**
 * Created by L070354 on 12/01/2017.
 */
public interface ICrsTaxDetailsForm {


    IAusTaxDetailsForm getAustralianTaxDetails();

    List<IOverseasTaxDetailsForm> getOverseasTaxDetails();

    void setOverseasTaxDetails(List<OverseasTaxDetails> overseasTaxDetails);

    String getOverseasTaxCountry();

    void setOverseasTaxCountry(StringValue value);

    boolean hasCrsTaxDetails();

    boolean hasOverseasTaxCountry();

}
