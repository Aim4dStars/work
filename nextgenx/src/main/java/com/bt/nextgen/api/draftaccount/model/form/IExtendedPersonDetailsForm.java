package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface IExtendedPersonDetailsForm extends IPersonDetailsForm {

    boolean isPrimaryContact();

    PaymentAuthorityEnum getPaymentSetting();

    boolean isApprover();

    boolean isBeneficiary();

    boolean isShareholder();

    boolean isMember();

    boolean isBeneficialOwner();

    public boolean isControllerOfTrust();

}
