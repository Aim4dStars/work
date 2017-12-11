package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListResult;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;

public interface CorporateActionConverter {
	/**
	 * Converts Avaloq corporate action objects to corporate action DTOs
	 *
	 * @param corporateActionListResult CorporateActionListResult object
	 * @param serviceErrors             the service errors object
	 * @return list of CorporateActionDtos
	 */
	CorporateActionListDto toCorporateActionListDto(CorporateActionGroup group, CorporateActionListResult corporateActionListResult,
													String accountId, ServiceErrors serviceErrors);

	/**
	 * Converts Avaloq corporate action objects to trustee/IRG corporate action DTOs
	 *
	 * @param corporateActionListResult CorporateActionListResult object
	 * @param serviceErrors             the service errors object
	 * @return list of CorporateActionDtos
	 */
	CorporateActionListDto toCorporateActionApprovalListDto(CorporateActionGroup group,
															CorporateActionListResult corporateActionListResult,
															ServiceErrors serviceErrors);

	/**
	 * Converts Avaloq corporate action objects to corporate action DTOs
	 *
	 * @param group                     the corporate action group voluntary or mandatory
	 * @param corporateActionListResult list of CorporateAction within CorporateActionListResult object
	 * @param portfolioModelId          the portfolio model (ips) ID
	 * @param serviceErrors             the service errors object
	 * @return list of CorporateActionDtos
	 */
	CorporateActionListDto toCorporateActionListDtoForIm(CorporateActionGroup group,
														 CorporateActionListResult corporateActionListResult,
														 String portfolioModelId, ServiceErrors serviceErrors);
}