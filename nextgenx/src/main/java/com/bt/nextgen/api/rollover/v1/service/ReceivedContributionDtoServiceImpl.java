package com.bt.nextgen.api.rollover.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.rollover.v1.model.ReceivedContributionDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.rollover.AvaloqRolloverService;
import com.bt.nextgen.service.integration.rollover.ContributionReceived;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ReceivedContributionDtoServiceImpl implements ReceivedContributionDtoService {

    @Autowired
    private AvaloqRolloverService rolloverService;

    @Override
    public List<ReceivedContributionDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        String accountId = null;

        for (ApiSearchCriteria criteria : criteriaList) {
            if ("accountId".equals(criteria.getProperty())) {
                accountId = EncodedString.toPlainText(criteria.getValue());
            }
        }
        if (accountId != null) {
            AccountKey key = new AccountKey(accountId);
            List<ContributionReceived> result = rolloverService.getContributionReceived(key, serviceErrors);
            if (result != null && !result.isEmpty()) {
                List<ReceivedContributionDto> dtoList = new ArrayList<>();
                for (ContributionReceived fund : result) {
                    dtoList.add(constructDto(fund));
                }
                return dtoList;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Construct an instance of the RolloverFundDto based on the account-key and fund specified.
     * 
     * @param contri
     *            ContributionReceived
     * @return
     */
    private ReceivedContributionDto constructDto(ContributionReceived contri) {
        if (contri == null) {
            return null;
        }
        String status = contri.getContributionStatus() == null ? null : contri.getContributionStatus().getDisplayName();
        return new ReceivedContributionDto(contri.getContributionId(), contri.getDescription(), contri.getAmount(),
                contri.getPaymentDate(), status);
    }
}
