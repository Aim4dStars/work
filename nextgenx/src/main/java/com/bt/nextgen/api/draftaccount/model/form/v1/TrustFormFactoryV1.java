package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.trust.TrustDetails;

/**
 * Factory class for accessing TrustForm
 */
public class TrustFormFactoryV1 {

    private TrustFormFactoryV1(){

    }

    public static ITrustForm getNewTrustForm(Integer index, TrustDetails trustDetails){
        return new TrustForm(index, trustDetails);
    }
}
