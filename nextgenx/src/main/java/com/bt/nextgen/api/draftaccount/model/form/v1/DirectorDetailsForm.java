package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IDirectorDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Customer;


/**
 * Created by m040398 on 4/04/2016.
 */
class DirectorDetailsForm extends ExtendedPersonDetailsForm implements IDirectorDetailsForm {

    private final boolean shareholder;
    private final boolean member;
    private final boolean beneficiary;
    private final boolean beneficialOwner;
    private final boolean controllerOfTrust;
    private final IOrganisationForm.OrganisationRole role;
    private final boolean isCompanySecretary;

    @SuppressWarnings("squid:S00107")
    public DirectorDetailsForm(Integer index, Customer personDetails, boolean primaryContact, PaymentAuthorityEnum paymentSetting, boolean approver, boolean shareholder, boolean member, boolean beneficiary, boolean beneficialOwner, boolean isCompanySecretary, boolean controllerOfTrust, IOrganisationForm.OrganisationRole role) {
        super(index, personDetails, primaryContact, paymentSetting, approver);
        this.shareholder = shareholder;
        this.member = member;
        this.beneficiary = beneficiary;
        this.beneficialOwner = beneficialOwner;
        this.role = role;
        this.isCompanySecretary = isCompanySecretary;
        this.controllerOfTrust = controllerOfTrust;
    }

    @Override
    public boolean isShareholder() {
        return shareholder;
    }

    @Override
    public boolean isMember() {
        return member;
    }

    @Override
    public boolean isBeneficiary() {
        return beneficiary;
    }

    @Override
    public boolean isBeneficialOwner() { return beneficialOwner; }

    @Override
    public IOrganisationForm.OrganisationRole getRole() {
        return this.role;
    }

    @Override
    public boolean isCompanySecretary() {return isCompanySecretary;}

    @Override
    public boolean isControllerOfTrust() {
        return this.controllerOfTrust;
    }

}
