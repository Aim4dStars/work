package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;

import java.util.Map;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
@SuppressWarnings({"squid:S00107"})
class DirectorDetailsForm extends ExtendedPersonDetailsForm implements IDirectorDetailsForm {

    private final boolean shareholder;
    private final boolean member;
    private final boolean beneficiary;
    private final boolean beneficialOwner;
    private final IOrganisationForm.OrganisationRole role;
    private final boolean isCompanySecretary;

    public DirectorDetailsForm(Map<String, Object> map, boolean primaryContact, PaymentAuthorityEnum paymentSetting, boolean approver, boolean shareholder, boolean member, boolean beneficiary, boolean beneficialOwner, boolean isCompanySecretary, IOrganisationForm.OrganisationRole role) {
        super(map, primaryContact, paymentSetting, approver);
        this.shareholder = shareholder;
        this.member = member;
        this.beneficiary = beneficiary;
        this.beneficialOwner = beneficialOwner;
        this.role = role;
        this.isCompanySecretary = isCompanySecretary;
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

    public IOrganisationForm.OrganisationRole getRole(){
        return role;
    }

    @Override
    public boolean isCompanySecretary() {
        return isCompanySecretary;
    }
}
