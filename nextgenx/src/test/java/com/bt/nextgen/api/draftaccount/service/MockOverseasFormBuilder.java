package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.builder.v3.TINExemptionEnum;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.IOverseasTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.v1.OverseasTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.TinOptionTypeEnum;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.domain.OrganisationImpl;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.List;

/**
 * Created by L069552 on 15/03/17.
 */
public class MockOverseasFormBuilder {


    public static Builder make(){

        return new Builder();
    }

    public static class Builder{

        IOverseasTaxDetailsForm overseasTaxDetailsForm;

        public Builder(){

            overseasTaxDetailsForm = mock(OverseasTaxDetailsForm.class);
        }
        public Builder withTIN(String tin){
           when(overseasTaxDetailsForm.getTIN()).thenReturn(tin);
            return this;
        }

        public Builder withTINOption(TinOptionTypeEnum tinOption){
           when(overseasTaxDetailsForm.getTINOption()).thenReturn(tinOption);
            return this;
        }

        public Builder withTINExemptionReason(String tinExemptionReason){
            when(overseasTaxDetailsForm.getTINExemptionReason()).thenReturn(tinExemptionReason);
            return this;
        }

        public Builder withOverseasTaxCountry(String overseasTaxCountry){
            when(overseasTaxDetailsForm.getOverseasTaxCountry()).thenReturn(overseasTaxCountry);
            return this;
        }

        public IOverseasTaxDetailsForm collect(){
            return overseasTaxDetailsForm;
        }

    }
}
