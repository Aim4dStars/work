package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;

import java.util.Map;

/**
 * Created by m040398 on 15/03/2016.
 */
public final class ExtendedPersonDetailsFormFactory {

    private ExtendedPersonDetailsFormFactory() {}

    public static IExtendedPersonDetailsForm getNewExtendedPersonDetailsForm(Map<String, Object> map, boolean primaryContact, PaymentAuthorityEnum paymentSetting, boolean approver) {
        return new ExtendedPersonDetailsForm(map, primaryContact, paymentSetting, approver);
    }
}
