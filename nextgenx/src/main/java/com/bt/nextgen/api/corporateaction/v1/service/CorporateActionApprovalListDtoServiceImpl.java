package com.bt.nextgen.api.corporateaction.v1.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListResult;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;


@Service
public class CorporateActionApprovalListDtoServiceImpl implements CorporateActionApprovalListDtoService {
	@Autowired
	private CorporateActionConverter converter;

	@Autowired
	private CorporateActionServices corporateActionServices;

	@Autowired
	private CorporateActionCommonService corporateActionCommonService;

	@Override
	public CorporateActionListDto find(CorporateActionListDtoKey key, ServiceErrors serviceErrors) {
		final DateTime startDate = StringUtils.isNotEmpty(key.getStartDate()) ? new DateTime(key.getStartDate()) : new DateTime();
		final DateTime endDate = StringUtils.isNotEmpty(key.getEndDate()) ? new DateTime(key.getEndDate()) : (new DateTime()).plusYears(1);

		CorporateActionListResult corporateActionListResult =
				corporateActionServices.loadVoluntaryCorporateActionsForApproval(startDate, endDate, serviceErrors);

		return converter.toCorporateActionApprovalListDto(CorporateActionGroup.VOLUNTARY, corporateActionListResult, serviceErrors);
	}
}
