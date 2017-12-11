package com.bt.nextgen.service.avaloq.modelportfolio.detail;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetailIntegrationService;
import com.btfin.abs.trxservice.ips.v1_0.IpsReq;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("avaloqModelPortfolioSetupIntegrationService")
public class ModelPortfolioDetailIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        ModelPortfolioDetailIntegrationService {

    @Autowired
    private AvaloqReportService avaloqService;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private ModelPortfolioDetailConverter modelPortfolioConverter;

    @Autowired
    private TransactionValidationConverter validationConverter;

    @Autowired
    private InvestmentPolicyStatementIntegrationService ipsService;

    @Override
    public ModelPortfolioDetail validateModelPortfolio(final ModelPortfolioDetail model, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<ModelPortfolioDetail>("validateModelPortfolio", serviceErrors) {

            @Override
            public ModelPortfolioDetail performOperation() {
                IpsReq req = modelPortfolioConverter.toValidateRequest(model);
                final Object objResponse = webserviceClient.sendToWebService(req, AvaloqOperation.IPS_REQ,
                        ModelPortfolioDetailImpl.class, serviceErrors);
                if (objResponse instanceof ModelPortfolioDetailImpl) {
                    ModelPortfolioDetailImpl response = (ModelPortfolioDetailImpl) objResponse;
                    response.setValidationErrors(modelPortfolioConverter.processErrors(response));
                    return response;
                }

                // Unexpected exception has occurred.
                serviceErrors.addError(new ServiceErrorImpl("Unknown response class " + objResponse.getClass().getName()));
                return null;
            }
        }.run();
    }

    @Override
    public ModelPortfolioDetail submitModelPortfolio(final ModelPortfolioDetail model, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<ModelPortfolioDetail>("submitModelPortfolio", serviceErrors) {
            @Override
            public ModelPortfolioDetail performOperation() {
                IpsReq req = modelPortfolioConverter.toSubmitRequest(model);
                Object objectResponse = webserviceClient.sendToWebService(req, AvaloqOperation.IPS_REQ,
                        ModelPortfolioDetailImpl.class, serviceErrors);
                if (objectResponse instanceof ModelPortfolioDetailImpl) {
                    ModelPortfolioDetailImpl response = (ModelPortfolioDetailImpl) objectResponse;
                    response.setValidationErrors(modelPortfolioConverter.processErrors(response));
                    // If there are no errors, refresh the IPS cache to capture updated/new model details
                    if (response.getValidationErrors().isEmpty()) {
                        ipsService.refreshInvestmentPolicyStatementsCache(serviceErrors);
                    }
                    return response;
                }
                // Unexpected exception has occurred.
                serviceErrors.addError(new ServiceErrorImpl("Unknown response class " + objectResponse.getClass().getName()));
                return null;
            }
        }.run();
    }

    @Override
    public ModelPortfolioDetail loadModelPortfolio(final ModelPortfolioKey modelKey, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<ModelPortfolioDetail>("loadModelPortfolio", serviceErrors) {

            @Override
            public ModelPortfolioDetail performOperation() {
                IpsReq req = modelPortfolioConverter.toLoadRequest(modelKey);
                ModelPortfolioDetailImpl response = webserviceClient.sendToWebService(req, AvaloqOperation.IPS_REQ,
                        ModelPortfolioDetailImpl.class, serviceErrors);
                return response;
            }
        }.run();
    }
}
