package com.bt.nextgen.api.draftaccount.model.form;


import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;

import java.util.Map;

/**
 * @deprecated Use the v1 version of this class instead.
 */
@Deprecated
class TrusteeDetailsForm extends ExtendedPersonDetailsForm implements ITrusteeDetailsForm{

    private final boolean member;
    private final boolean beneficiary;

    public TrusteeDetailsForm(Map<String, Object> map, boolean primaryContact, PaymentAuthorityEnum paymentSetting, boolean approver, boolean member, boolean beneficiary) {
        super(map, primaryContact, paymentSetting, approver);
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
