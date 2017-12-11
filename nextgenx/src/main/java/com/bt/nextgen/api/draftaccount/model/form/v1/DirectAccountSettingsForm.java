package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IAccountSettingsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;


/**
 * Created by F058391 on 29/04/2016.
 */
class DirectAccountSettingsForm implements IAccountSettingsForm {

    @Override
    public PaymentAuthorityEnum getPaymentSettingForInvestor(int i) {
        return PaymentAuthorityEnum.ALLPAYMENTS;
    }

    @Override
    public Boolean getApproverSettingForInvestor(int i) {
        return true;
    }

    @Override
    public IOrganisationForm.OrganisationRole getRoleSettingForInvestor(int i) {
        return null;
    }

    @Override
    public PaymentAuthorityEnum getProfessionalsPayment() {
        return PaymentAuthorityEnum.NOPAYMENTS;
    }

    @Override
    public boolean isPrimaryContact(int index) {
        return true;
    }

    @Override
    public Integer getPrimaryContact() {
        return null;
    }

    @Override
    public String getAdviserName() {
        return null;
    }

    @Override
    public boolean hasSourceOfFunds() {
        return false;
    }

    @Override
    public String getSourceOfFunds() {
        return null;
    }

    @Override
    public String getAdditionalSourceOfFunds() {
        return null;
    }

    @Override
    public Boolean getPowerOfAttorney() {
        throw new UnsupportedOperationException("Power of Attorney is not supported for Direct");
    }
}
