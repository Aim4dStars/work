package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.util.List;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;

public interface CorporateActionResponseConverterService {
    /**
     * Converts avaloq part list into corporate action option DTOS.
     *
     * @param context       CA context
     * @param serviceErrors The service errors object
     * @return list of populated CorporateActionOptionDto.  An empty list if nothing to convert.
     */
    List<CorporateActionOptionDto> toElectionOptionDtos(CorporateActionContext context, ServiceErrors serviceErrors);

    /**
     * Convert to submitted account election dto's
     *
     * @param context CA context
     * @param account the account object
     * @return account elections dto object
     */
    CorporateActionAccountElectionsDto toSubmittedAccountElectionsDto(CorporateActionContext context, CorporateActionAccount account);

    /**
     * Convert to saved account election dto's
     *
     * @param context      CA context
     * @param accountId    the corporate action account in question
     * @param savedDetails the loaded saved elections from database
     * @return account elections dto object
     */
    CorporateActionAccountElectionsDto toSavedAccountElectionsDto(CorporateActionContext context, String accountId,
                                                                  CorporateActionSavedDetails savedDetails);

    /**
     * Common implementation on summary text conversion.  Uses pipe symbol as a line separator.
     *
     * @param context       CA context
     * @param serviceErrors service errors
     * @return a list of summary text
     */
    List<String> toSummaryList(CorporateActionContext context, ServiceErrors serviceErrors);

    /**
     * Method to modify corporate action details dto params
     *
     * @param context CA context
     * @param params  the corporate action details params object
     * @return the params object
     */
    CorporateActionDetailsDtoParams setCorporateActionDetailsDtoParams(CorporateActionContext context,
                                                                       CorporateActionDetailsDtoParams params);

    /**
     * Method to modify corporate action account details dto params
     *
     * @param context CA context
     * @param account the corporate action account
     * @param params  the params object
     * @return the corporate action account details params object
     */
    CorporateActionAccountDetailsDtoParams setCorporateActionAccountDetailsDtoParams(CorporateActionContext context,
                                                                                     CorporateActionAccount account,
                                                                                     CorporateActionAccountDetailsDtoParams params);
}
