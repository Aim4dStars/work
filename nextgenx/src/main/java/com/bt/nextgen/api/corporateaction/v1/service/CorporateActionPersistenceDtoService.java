package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionPersistenceDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;

import java.util.List;

public interface CorporateActionPersistenceDtoService extends SubmitDtoService<CorporateActionDtoKey, CorporateActionPersistenceDto> {

	CorporateActionSavedDetails loadAndValidateElectedOptions(String orderNumber, List<CorporateActionOptionDto> options);

	int deleteDraftParticipation(String orderNumber);

	int deleteSuccessfulDraftAccountElections(CorporateActionElectionDetailsDto electionDetailsDto,
											  List<CorporateActionElectionResultDto> electionResults);

    int deleteSuccessfulDraftElectionsForDg(ImCorporateActionElectionDetailsDto electionDetailsDto,
                                            List<CorporateActionElectionResultDto> electionResults);

	int deleteExpiredDraftParticipations();
}
