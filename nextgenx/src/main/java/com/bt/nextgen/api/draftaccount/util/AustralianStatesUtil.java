package com.bt.nextgen.api.draftaccount.util;

import com.bt.nextgen.clients.api.model.AustralianStates;

public class AustralianStatesUtil {

    public static String getAustralianStateCode(String stateCodeOrName){
        AustralianStates state = AustralianStates.getAustralianStateByNameOrCode(stateCodeOrName);
        if(state!=null) {
            return state.getCode();
        }
        return "";
    }
}
