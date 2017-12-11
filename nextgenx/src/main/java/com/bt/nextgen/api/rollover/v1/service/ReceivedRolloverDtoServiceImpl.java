package com.bt.nextgen.api.rollover.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.rollover.v1.model.ReceivedRolloverFundDto;
import com.bt.nextgen.api.rollover.v1.model.RolloverFundDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.rollover.AvaloqRolloverService;
import com.bt.nextgen.service.integration.rollover.RolloverReceived;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ReceivedRolloverDtoServiceImpl implements ReceivedRolloverDtoService {

    @Autowired
    private AvaloqRolloverService rolloverService;

    @Override
    public List<ReceivedRolloverFundDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        String accountId = null;

        for (ApiSearchCriteria criteria : criteriaList) {
            if ("accountId".equals(criteria.getProperty())) {
                accountId = EncodedString.toPlainText(criteria.getValue());
            }
        }
        AccountKey key = new AccountKey(accountId);
        List<RolloverReceived> result = rolloverService.getReceivedFunds(key, serviceErrors);
        if (result != null && !result.isEmpty()) {
            List<ReceivedRolloverFundDto> dtoList = new ArrayList<>();
            for (RolloverReceived fund : result) {
                dtoList.add(constructFundDto(key, fund));
            }
            return dtoList;
        }
        return Collections.emptyList();
    }

    /**
     * Construct an instance of the RolloverFundDto based on the account-key and fund specified.
     * 
     * @param key
     * @param fund
     * @return
     */
    private ReceivedRolloverFundDto constructFundDto(AccountKey key, RolloverReceived fund) {
        return new ReceivedRolloverFundDto(new RolloverFundDto(key.getAccountId(), fund.getFundId(), fund.getFundName(),
                fund.getFundAbn(), fund.getFundUsi(), fund.getRolloverType().getShortDisplayName(), fund.getAmount()),
                fund.getContributionStatus(), fund.getReceivedDate());

    }
}
