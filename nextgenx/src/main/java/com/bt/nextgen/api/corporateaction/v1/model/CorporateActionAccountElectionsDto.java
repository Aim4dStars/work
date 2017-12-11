package com.bt.nextgen.api.corporateaction.v1.model;

import java.util.List;

public interface CorporateActionAccountElectionsDto {
	List<CorporateActionAccountElectionDto> getOptions();

	CorporateActionAccountElectionDto getPrimaryAccountElection();
}
