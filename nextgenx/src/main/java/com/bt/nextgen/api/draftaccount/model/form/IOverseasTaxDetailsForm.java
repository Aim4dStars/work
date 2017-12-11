package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.builder.v3.TINExemptionEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.TinOptionTypeEnum;
import com.bt.nextgen.service.integration.domain.ExemptionReason;

/**
 * Created by L070354 on 12/01/2017.
 */
public interface IOverseasTaxDetailsForm {

    String getTIN();

    TinOptionTypeEnum getTINOption();

    String getTINExemptionReason();

    String getOverseasTaxCountry();

}
