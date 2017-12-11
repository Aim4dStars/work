package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.schemas.v1.smsf.SmsfDetails;

/**
 * Factory class for accessing SmsfForm
 */
public class SmsfFormFactoryV1 {

    private SmsfFormFactoryV1(){

    }

    public static SmsfForm getNewSmsfForm(Integer index, SmsfDetails smsfDetails){
        return new SmsfForm(index, smsfDetails);
    }
}
