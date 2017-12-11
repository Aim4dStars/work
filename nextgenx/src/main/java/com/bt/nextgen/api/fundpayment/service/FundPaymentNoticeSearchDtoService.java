package com.bt.nextgen.api.fundpayment.service;

import com.bt.nextgen.api.fundpayment.model.FundPaymentNoticeDto;
import com.bt.nextgen.api.fundpayment.model.FundPaymentNoticeSearchDtoKey;
import com.bt.nextgen.core.api.dto.SearchByKeyedCriteriaDtoService;

public interface FundPaymentNoticeSearchDtoService extends
    SearchByKeyedCriteriaDtoService<FundPaymentNoticeSearchDtoKey, FundPaymentNoticeDto>
{

}
