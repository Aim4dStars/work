package com.bt.nextgen.api.fees.service;

import com.bt.nextgen.api.fees.model.OneOffFeesKey;
import com.bt.nextgen.api.fees.model.YearlyFeesDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.service.ServiceErrors;

public interface YearlyFeesDtoService extends FindByKeyDtoService <OneOffFeesKey, YearlyFeesDto>
{
	/**
	 * Method to get advice fees from service layer.
	 * @param accountId
	 * @param serviceErrors
	 * @return Yearly Advice fees
	 */
	public YearlyFeesDto getChargedFees(String accountId, ServiceErrors serviceErrors);
}
