package com.bt.nextgen.api.cgt.model;

public class CgtMpSecurityDto extends AbstractCgtSecurityDto {

    public CgtMpSecurityDto(String securityCode, String securityName, String securityType, String parentInvId,
            String parentInvCode, String parentInvName, String parentInvType) {
        super(securityCode, securityName, securityType, parentInvId, parentInvCode, parentInvName, parentInvType);
    }

}
