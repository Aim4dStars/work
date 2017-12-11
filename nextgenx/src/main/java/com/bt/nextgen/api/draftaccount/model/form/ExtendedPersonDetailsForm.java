package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;

import java.util.Map;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
class ExtendedPersonDetailsForm extends PersonDetailsForm implements IExtendedPersonDetailsForm{

    private final boolean primaryContact;
    private final PaymentAuthorityEnum paymentSetting;
    private final boolean approver;

    public ExtendedPersonDetailsForm(Map<String, Object> map, boolean primaryContact, PaymentAuthorityEnum paymentSetting, boolean approver) {
        super(map);
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
        return (String) ((Map<String, Object>) map.get("key")).get("clientId");
    }

    @Override
    public boolean isExistingPerson() {
        return map.containsKey("key")  && (map.get("key")!=null);
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
