package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.ITrusteeDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Customer;

/**
 * Created by m040398 on 4/04/2016.
 */
class TrusteeDetailsForm extends ExtendedPersonDetailsForm implements ITrusteeDetailsForm {

    private final boolean member;
    private final boolean beneficiary;

    TrusteeDetailsForm(Integer index, Customer personDetails, boolean primaryContact, PaymentAuthorityEnum paymentSetting, boolean approver, boolean member, boolean beneficiary) {
        super(index, personDetails, primaryContact, paymentSetting, approver);
        this.member = member;
        this.beneficiary = beneficiary;
    }

    @Override
    public boolean isMember() {
        return member;
    }

    @Override
    public boolean isBeneficiary() {
        return beneficiary;
    }

}
