package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDtoKey;
import com.bt.nextgen.service.ServiceErrors;
import org.springframework.stereotype.Service;


@Service
public class CorporateActionListWithMetadataDtoServiceImpl extends CorporateActionListDtoServiceBaseImpl implements
		CorporateActionListWithMetadataDtoService {
	@Override
	public CorporateActionListDto find(CorporateActionListDtoKey key, ServiceErrors serviceErrors) {
		return searchCommon(key, serviceErrors);
	}
}
