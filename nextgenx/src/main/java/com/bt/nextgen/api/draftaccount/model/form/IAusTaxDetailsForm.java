package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.TaxOptionTypeEnum;
import com.bt.nextgen.service.integration.domain.ExemptionReason;

/**
 * Created by L070354 on 12/01/2017.
 */
public interface IAusTaxDetailsForm {

    String getTFN();

    TaxOptionTypeEnum getTaxOption();

    String getExemptionReason();

    boolean hasExemptionReason();

    boolean isTaxCountryAustralia();

    boolean hasTaxFileNumber();

}
