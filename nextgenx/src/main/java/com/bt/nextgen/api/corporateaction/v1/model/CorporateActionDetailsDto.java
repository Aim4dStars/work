package com.bt.nextgen.api.corporateaction.v1.model;

import java.util.List;

/**
 * Corporate action details Dto object.
 */
public class CorporateActionDetailsDto extends CorporateActionDetailsBaseDto {
    private List<CorporateActionAccountDetailsDto> accounts;

    public CorporateActionDetailsDto() {
        // Empty constructor
    }

    /**
     * The CA details constructor
     *
     * @param params the CA details wrapper class of params
     */
    public CorporateActionDetailsDto(CorporateActionDetailsDtoParams params) {
        super(params);
        this.accounts = params.getAccounts();
    }

    public List<CorporateActionAccountDetailsDto> getAccounts() {
        return accounts;
    }

    @Override
    public CorporateActionDtoKey getKey() {
        return null;
    }
}
