package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.model.form.v1.SmsfFormFactoryV1;
import com.bt.nextgen.api.draftaccount.schemas.v1.smsf.SmsfDetails;

import java.util.Map;

/**
 * Factory for accessing appropriate version of SmsfForm
 */
public final class SmsfFormFactory {

    private SmsfFormFactory() {}

    public static ISmsfForm getNewSmsfForm(Integer index, Object smsDetails) {
        if(smsDetails instanceof Map){
            return new SmsfForm((Map)smsDetails);
        }
        else if(smsDetails instanceof SmsfDetails){
            return SmsfFormFactoryV1.getNewSmsfForm(index, (SmsfDetails) smsDetails);
        }
        else{
            throw new IllegalArgumentException("Invalid SMSDetails Object : " +  smsDetails);
        }
    }
}
