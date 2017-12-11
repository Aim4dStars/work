package com.bt.nextgen.api.corporateaction.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class CorporateActionListDto extends BaseDto implements KeyedDto<CorporateActionListDtoKey> {
	private final Boolean hasSuperPension;
	private final List<CorporateActionBaseDto> corporateActions;

	public CorporateActionListDto(Boolean hasSuperPension, List<CorporateActionBaseDto> corporateActions) {
		this.hasSuperPension = hasSuperPension;
		this.corporateActions = corporateActions;
	}

	public Boolean getHasSuperPension() {
		return hasSuperPension;
	}

	public List<CorporateActionBaseDto> getCorporateActions() {
		return corporateActions;
	}

	@Override
	public CorporateActionListDtoKey getKey() {
		return null;
	}
}
