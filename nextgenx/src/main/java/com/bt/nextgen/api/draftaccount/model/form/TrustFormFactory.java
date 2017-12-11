package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.model.form.v1.TrustFormFactoryV1;
import com.bt.nextgen.api.draftaccount.schemas.v1.trust.TrustDetails;

import java.util.Map;

/**
 * Factory for accessing appropriate version of TrustForm
 */
public class TrustFormFactory {

    private TrustFormFactory(){}

    public static ITrustForm getNewTrustForm(Integer index, Object trustDetails){
        if(trustDetails instanceof Map){
            return new TrustForm((Map)trustDetails);
        }
        else if(trustDetails instanceof TrustDetails){
            return TrustFormFactoryV1.getNewTrustForm(index, (TrustDetails) trustDetails);
        }
        else{
            throw new IllegalArgumentException("Invalid TrustDetails Object : " +  trustDetails);
        }
    }
}
