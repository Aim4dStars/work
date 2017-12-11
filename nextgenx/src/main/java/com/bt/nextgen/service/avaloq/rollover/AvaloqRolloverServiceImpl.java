package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.integration.rollover.ContributionReceived;
import com.bt.nextgen.service.integration.rollover.RolloverHistory;
import com.bt.nextgen.service.integration.rollover.RolloverHistoryResponse;
import com.bt.nextgen.service.integration.rollover.RolloverReceived;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("avaloqRolloverIntegrationService")
public class AvaloqRolloverServiceImpl extends AbstractAvaloqIntegrationService implements AvaloqRolloverService {

    @Autowired
    private AvaloqReportService avaloqService;

    @Autowired
    private Validator validator;

    @Override
    public List<RolloverHistory> getRolloverHistory(String accountId, ServiceErrors serviceErrors) {
        if (StringUtils.isEmpty(accountId)) {
            throw new IllegalArgumentException("Account ID must be provided to retrieve rollover history");
        }

        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(RolloverTemplate.ROLLOVER_HISTORY).forParam(
                RolloverParams.ACCOUNT_ID_LIST, accountId);

        RolloverHistoryResponse response = avaloqService.executeReportRequestToDomain(avaloqRequest,
                RolloverHistoryResponseImpl.class, serviceErrors);

        if (response != null) {
            return response.getRolloverHistory();
        }
        return Collections.emptyList();
    }

    @Override
    public List<RolloverReceived> getReceivedFunds(AccountKey key, ServiceErrors serviceErrors) {

        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(RolloverTemplate.RECEIVED_ROLLOVER).forParam(
                RolloverParams.PARAM_ACCOUNT_ID, key.getAccountId());

        RolloverReceivedResponseImpl response = avaloqService.executeReportRequestToDomain(avaloqRequest,
                RolloverReceivedResponseImpl.class, serviceErrors);

        validator.validate(response, serviceErrors);
        if (response != null) {
            return response.getReceivedSuperfunds();
        }

        return Collections.emptyList();
    }

    @Override
    public List<ContributionReceived> getContributionReceived(AccountKey key, ServiceErrors serviceErrors) {
        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(RolloverTemplate.RECEIVED_CONTRIBUTION).forParam(
                RolloverParams.PARAM_ACCOUNT_ID, key.getAccountId());

        ContributionReceivedResponseImpl response = avaloqService.executeReportRequestToDomain(avaloqRequest,
                ContributionReceivedResponseImpl.class, serviceErrors);
        validator.validate(response, serviceErrors);
        if (response != null) {
            return response.getContributionReceived();
        }

        return Collections.emptyList();
    }
}
