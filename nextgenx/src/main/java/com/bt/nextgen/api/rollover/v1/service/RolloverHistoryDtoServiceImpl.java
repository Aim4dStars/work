package com.bt.nextgen.api.rollover.v1.service;

import com.bt.nextgen.api.rollover.v1.model.RolloverHistoryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.rollover.AvaloqRolloverService;
import com.bt.nextgen.service.integration.rollover.RolloverHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RolloverHistoryDtoServiceImpl implements RolloverHistoryDtoService {

    @Autowired
    private AvaloqRolloverService rolloverService;

    @Override
    public List<RolloverHistoryDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        String accountId = null;

        for (ApiSearchCriteria criteria : criteriaList) {
            if ("accountId".equals(criteria.getProperty())) {
                accountId = EncodedString.toPlainText(criteria.getValue());
            }
        }

        List<RolloverHistoryDto> historyDtos = new ArrayList<>();
        List<RolloverHistory> history = rolloverService.getRolloverHistory(accountId, serviceErrors);

        if (history != null) {
            for (RolloverHistory historyItem : history) {
                historyDtos.add(new RolloverHistoryDto(historyItem));
            }
        }
        return historyDtos;
    }
}
