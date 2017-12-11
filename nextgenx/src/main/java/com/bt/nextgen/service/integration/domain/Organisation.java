package com.bt.nextgen.service.integration.domain;

import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;

import java.util.List;

/**
 *
 * Abstract Domain object represents Legal Person in Application Document.
 */
public interface Organisation extends AbstractOrganisation,Trust, Company{

    public void setAddresses(List<Address> addresses);

    void setTaxResidenceCountries( List<TaxResidenceCountry> taxResidenceCountries );

}
