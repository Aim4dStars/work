package com.bt.nextgen.api.draftaccount.model.form;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface ICompanyForm extends IOrganisationForm {

    public String getAsicName();

    public IAddressForm getPlaceOfBusinessAddress();

    public String getOccupierName();

    public Boolean getPersonalInvestmentEntity();

}
