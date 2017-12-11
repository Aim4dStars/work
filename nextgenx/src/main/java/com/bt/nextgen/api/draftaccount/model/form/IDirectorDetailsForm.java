package com.bt.nextgen.api.draftaccount.model.form;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface IDirectorDetailsForm extends IExtendedPersonDetailsForm {

    public IOrganisationForm.OrganisationRole getRole();

    public boolean isCompanySecretary();
}
