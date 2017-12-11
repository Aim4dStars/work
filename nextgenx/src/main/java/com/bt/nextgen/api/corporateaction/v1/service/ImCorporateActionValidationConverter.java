package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.List;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionResultDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionValidationError;

public interface ImCorporateActionValidationConverter {
    List<ImCorporateActionElectionResultDto> toElectionResultDtoList(CorporateActionElectionGroup electionGroup,
                                                                     ImCorporateActionElectionDetailsDto electionDetailsDto,
                                                                     List<CorporateActionAccount> accounts,
                                                                     List<CorporateActionValidationError> validationErrors);

    List<CorporateActionElectionResultDto> toElectionResultDtoListForDg(CorporateActionElectionGroup electionGroup,
                                                                        ImCorporateActionElectionDetailsDto electionDetailsDto,
                                                                        List<CorporateActionAccount> accounts,
                                                                        List<CorporateActionValidationError> validationErrors);

    boolean hasManagedPortfolioValidationError(CorporateActionElectionGroup electionGroup,
                                               List<CorporateActionValidationError> validationErrors);

    boolean isCompleteSubmissionFailure(CorporateActionElectionGroup electionGroup, List<CorporateActionValidationError> validationErrors);

    String getCmsText(String cmsId, String... params);

    boolean validateOptions(CorporateActionElectionDetailsBaseDto electionDetailsDto, CorporateActionContext context,
                            ServiceErrors serviceErrors);

    void handleValidationErrors(CorporateActionElectionGroup electionGroup, ImCorporateActionElectionDetailsDto electionDetails,
                                List<CorporateActionAccount> accounts,
                                List<CorporateActionValidationError> validationErrors,
                                ElectionResults electionResults);

    void handleServiceErrors(CorporateActionElectionDetailsBaseDto electionDetailsDto, ServiceErrors groupServiceErrors,
                             ElectionResults electionResults);

    ElectionResults createElectionResults();

    ServiceErrors createServiceErrors();
}
