package com.bt.nextgen.api.corporateaction.v1.service;


import java.util.List;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;

public interface CorporateActionAccountDetailsDtoService {
    /**
     * Convert from Avaloq CA accounts to CA DTO
     *
     * @param context       corporate action context
     * @param savedDetails  savedDetails version of account elections
     * @param serviceErrors
     * @return list of corporate action account details dto.  Returns empty list if nothing.
     */
    List<CorporateActionAccountDetailsDto> toCorporateActionAccountDtoList(CorporateActionContext context,
                                                                           List<CorporateActionAccount> accounts,
                                                                           CorporateActionSavedDetails savedDetails,
                                                                           ServiceErrors serviceErrors);
}
