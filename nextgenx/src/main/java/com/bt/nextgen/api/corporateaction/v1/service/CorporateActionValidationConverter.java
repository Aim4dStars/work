package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.List;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionResultDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionValidationError;


public interface CorporateActionValidationConverter {
    List<CorporateActionElectionResultDto> toElectionResultDtoList(CorporateActionElectionGroup electionGroup,
                                                                   CorporateActionElectionDetailsDto electionDetailsDto,
                                                                   List<CorporateActionValidationError> validationErrors);

    String getCmsText(String cmsId, String... params);

    boolean validateOptions(CorporateActionElectionDetailsBaseDto electionDetailsDto, CorporateActionContext context,
                            ServiceErrors serviceErrors);

    List<CorporateActionElectionResultDto> createElectionResults();

    ServiceErrors createServiceErrors();

}
