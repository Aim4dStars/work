package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.service.avaloq.client.TaxResidenceCountryImpl;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.domain.OrganisationImpl;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import org.joda.time.DateTime;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.List;

/**
 * Created by L069552 on 15/03/17.
 */
public class MockTaxResidencyCountryBuilder {

    public static Builder make(){

        return new Builder();
    }

    public static class Builder{


        private TaxResidenceCountry taxResidenceCountry;

        public Builder(){

            taxResidenceCountry = mock(TaxResidenceCountryImpl.class);
        }
        public Builder withCountryOfResidence(String countryOfResidence){
            when(taxResidenceCountry.getCountryName()).thenReturn(countryOfResidence);
            return this;
        }

        public Builder withTinExemptionReason(String tinExemptionReason){
            when(taxResidenceCountry.getTinExemption()).thenReturn(tinExemptionReason);
            return this;
        }

        public Builder withTin(String tin){
            when(taxResidenceCountry.getTin()).thenReturn(tin);
            return this;
        }

        public Builder withIssueDate(DateTime issueDate){
            when(taxResidenceCountry.getIssueDate()).thenReturn(issueDate);
            return this;
        }

        public Builder withEndDate(DateTime endDate){
            when(taxResidenceCountry.getEndDate()).thenReturn(endDate);
            return this;
        }

        public TaxResidenceCountry collect(){

            return taxResidenceCountry;
        }
    }
}