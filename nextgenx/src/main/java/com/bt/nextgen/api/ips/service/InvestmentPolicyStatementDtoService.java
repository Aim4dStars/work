package com.bt.nextgen.api.ips.service;

import com.bt.nextgen.api.ips.model.InvestmentPolicyStatementDto;
import com.bt.nextgen.api.ips.model.InvestmentPolicyStatementKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

/**
 * IPS dto service interface
 * <p/>
 * Implemented by InvestmentPolicyStatementDtoServiceImpl
 */
public interface InvestmentPolicyStatementDtoService extends SearchByCriteriaDtoService<InvestmentPolicyStatementDto>,
        FindByKeyDtoService<InvestmentPolicyStatementKey, InvestmentPolicyStatementDto> {
}
