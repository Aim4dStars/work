package com.bt.nextgen.service.avaloq.holdingbreach;

import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreachIntegrationService;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreachSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvaloqHoldingBreachIntegrationServiceImpl extends AbstractAvaloqIntegrationService
        implements HoldingBreachIntegrationService {
    @Autowired
    private AvaloqExecute avaloqExecute;

    @Autowired
    private Validator validator;

    @Override
    /**
     * {@inheritDoc}
     */
    public HoldingBreachSummary loadHoldingBreaches(final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<HoldingBreachSummaryImpl>("loadHoldingBreaches", serviceErrors) {
            @Override
            public HoldingBreachSummaryImpl performOperation() {
                Template template = Template.HOLDING_BREACH_REPORT;

                HoldingBreachSummaryImpl response = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(template),
                        HoldingBreachSummaryImpl.class, serviceErrors);

                if (response != null) {
                    validator.validate(response, serviceErrors);
                }

                return response;
            }
        }.run();
    }
}
