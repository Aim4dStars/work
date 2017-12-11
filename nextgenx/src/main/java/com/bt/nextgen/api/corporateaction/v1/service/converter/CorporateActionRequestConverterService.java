package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.util.Collection;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;

public interface CorporateActionRequestConverterService {
    Collection<CorporateActionElectionGroup> createElectionGroups(CorporateActionContext context,
                                                                  CorporateActionElectionDetailsDto electionDetailsDto);

    Collection<CorporateActionElectionGroup> createElectionGroupsForIm(CorporateActionContext context,
                                                                       ImCorporateActionElectionDetailsDto electionDetailsDto);

    Collection<CorporateActionElectionGroup> createElectionGroupsForDg(CorporateActionContext context,
                                                                       ImCorporateActionElectionDetailsDto electionDetailsDto);
}
