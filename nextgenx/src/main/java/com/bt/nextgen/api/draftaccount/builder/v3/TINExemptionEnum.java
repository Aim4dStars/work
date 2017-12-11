package com.bt.nextgen.api.draftaccount.builder.v3;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by L070354 on 18/01/2017.
 */


public enum TINExemptionEnum {

    TIN_UNDERAGED("btfg$under_aged"),
    TIN_NEVERISSUED("btfg$tin_never_iss"),
    TIN_PENDING("btfg$tin_pend"),
    TAX_IDENTIFICATION_NUMBER("tin");

    private final String exemption;
    private static final Map <String, TINExemptionEnum> tinMap = new HashMap();

    static
    {
        for (TINExemptionEnum tin : TINExemptionEnum.values())
            tinMap.put(tin.getExemption(), tin);
    }

    public String getExemption() {
        return exemption;
    }

    TINExemptionEnum(String exemption) {
        this.exemption = exemption;
    }

    public static TINExemptionEnum getExemption(String exemption)
    {

        return (TINExemptionEnum)tinMap.get(exemption);
    }
}


