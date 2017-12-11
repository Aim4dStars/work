package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Customer;


/**
 * Created by m040398 on 24/03/2016.
 */
class ExtendedPersonDetailsForm extends PersonDetailsForm implements IExtendedPersonDetailsForm {

    private final boolean primaryContact;
    private final PaymentAuthorityEnum paymentSetting;
    private final boolean approver;

    public ExtendedPersonDetailsForm(Integer index, Customer personDetails, boolean primaryContact, PaymentAuthorityEnum paymentSetting, boolean approver) {
        super(index, personDetails);
        this.primaryContact = primaryContact;
        this.paymentSetting = paymentSetting;
        this.approver = approver;
    }

    @Override
    public boolean isPrimaryContact() {
        return primaryContact;
    }

    @Override
    public PaymentAuthorityEnum getPaymentSetting() {
        return paymentSetting;
    }

    @Override
    public boolean isApprover() {
        return approver;
    }

    @Override
    public String getClientKey() {
        return customer.getKey().getClientId();
    }

    @Override
    public boolean isExistingPerson() {
        return customer.getKey() != null;
    }

    @Override
    public boolean isBeneficiary() {
        return false;
    }

    @Override
    public boolean isShareholder(){
        return false;
    }

    @Override
    public boolean isMember(){
        return false;
    }

    @Override
    public boolean isBeneficialOwner() { return false; }

    @Override
    public boolean isControllerOfTrust() {
        return false;
    }

}
