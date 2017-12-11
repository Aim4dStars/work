package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummaryIntegrationService;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.panorama.core.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AvaloqModelPortfolioSummaryIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        ModelPortfolioSummaryIntegrationService {
    @Autowired
    private AvaloqExecute avaloqExecute;

    @Autowired
    private AvaloqReportService avaloqService;

    @Autowired
    private Validator validator;

    @Override
    /**
     * {@inheritDoc}
     */
    public List<ModelPortfolioSummary> loadModels(final BrokerKey brokerKey, final ServiceErrors serviceErrors) {
        final List<ModelPortfolioSummary> result = new ArrayList<>();

        new IntegrationOperation("loadModelPortfolios", serviceErrors) {
            @Override
            public void performOperation() {

                AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(ModelSummaryTemplate.MODEL_PORTFOLIOS_SUMMARY)
                        .forParam(ModelSummaryParams.PARAM_INVESTMENT_MANAGER_ID, brokerKey.getId());

                ModelPortfolioSummaryResponse response = avaloqService.executeReportRequestToDomain(avaloqRequest,
                        ModelPortfolioSummaryResponse.class, serviceErrors);
                if (response != null && response.getSummary() != null) {
                    result.addAll(response.getSummary());
                    validator.validate(result, serviceErrors);
                }
            }
        }.run();

        return result;
    }
}
