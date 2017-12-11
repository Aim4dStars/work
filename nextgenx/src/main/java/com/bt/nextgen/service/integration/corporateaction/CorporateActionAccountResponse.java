package com.bt.nextgen.service.integration.corporateaction;

import java.util.List;

/**
 * Corporate action accounts response interface
 */ 
public interface CorporateActionAccountResponse
{
    /**
     * Return a list of corporate actions accounts
     *
     * @return list of corporate actions accounts
     */
    List<CorporateActionAccount> getCorporateActionAccounts();
    
}
