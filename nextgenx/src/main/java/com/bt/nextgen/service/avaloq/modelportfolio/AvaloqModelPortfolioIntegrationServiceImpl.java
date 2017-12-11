package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.CashForecast;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioUpload;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ShadowTransaction;
import com.btfin.panorama.core.validation.Validator;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AvaloqModelPortfolioIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        ModelPortfolioIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(AvaloqModelPortfolioIntegrationServiceImpl.class);

    @Autowired
    private AvaloqExecute avaloqExecute;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private Validator validator;

    @Autowired
    private TransactionValidationConverter validationConverter;

    @Autowired
    private ModelPortfolioConverter modelConverter;

    @Autowired
    private ModelPortfolioUploadConverter modelUploadConverter;

    @Override
    /**
     * {@inheritDoc}
     */
    public Collection<ModelPortfolio> loadModels(final BrokerKey investmentManagerKey, final ServiceErrors serviceErrors) {
        final Map<IpsKey, ModelPortfolio> models = new HashMap<>();
        final Map<IpsKey, CashForecast> cashForecasts = new HashMap<>();
        final Map<IpsKey, ShadowPortfolio> shadowPortfolios = new HashMap<>();
        final Map<IpsKey, List<ShadowTransaction>> shadowTransactions = new HashMap<>();

        new IntegrationOperation("loadModelHeaders", serviceErrors) {
            @Override
            public void performOperation() {
                com.avaloq.abs.screen_rep.hira.btfg$ui_ips_list_ips_im_rep_hdr.Rep report = avaloqExecute
                        .executeReportRequest(new AvaloqReportRequest(Template.MODEL_PORTFOLIOS_HEADER_BULK.getName())
                                .forInvestmentManager(investmentManagerKey.getId()));
                models.putAll(modelConverter.toModelHeader(report, serviceErrors));
                validator.validate(models, serviceErrors);
            }
        }.run();

        new IntegrationOperation("loadCashForecasts", serviceErrors) {
            @Override
            public void performOperation() {
                com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_ips_shdw_mp_cash.Rep report = avaloqExecute
                        .executeReportRequest(new AvaloqReportRequest(Template.MODEL_PORTFOLIOS_CASH_FORECASTING_BULK.getName())
                                .forInvestmentManager(investmentManagerKey.getId()));
                cashForecasts.putAll(modelConverter.toCashForecast(report, serviceErrors));
                validator.validate(cashForecasts, serviceErrors);
            }
        }.run();

        new IntegrationOperation("loadShadowPortfolios", serviceErrors) {
            @Override
            public void performOperation() {
                com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_asset_x_wgt.Rep report = avaloqExecute
                        .executeReportRequest(new AvaloqReportRequest(Template.MODEL_PORTFOLIOS_SHADOW_PORTFOLIO_BULK.getName())
                                .forInvestmentManager(investmentManagerKey.getId()));
                shadowPortfolios.putAll(modelConverter.toShadowPortfolioModel(report, serviceErrors));
                validator.validate(shadowPortfolios, serviceErrors);
            }
        }.run();

        new IntegrationOperation("loadShadowTransactions", serviceErrors) {
            @Override
            public void performOperation() {
                com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.Rep report = avaloqExecute
                        .executeReportRequest(new AvaloqReportRequest(Template.MODEL_PORTFOLIOS_SHADOW_TRANSACTIONS_BULK
                                .getName()).forInvestmentManager(investmentManagerKey.getId()));
                shadowTransactions.putAll(modelConverter.toShadowTransactionModel(report, serviceErrors));
                validator.validate(shadowTransactions, serviceErrors);
            }
        }.run();

        for (IpsKey modelKey : models.keySet()) {
            ModelPortfolioImpl modelPortfolio = (ModelPortfolioImpl) models.get(modelKey);
            modelPortfolio.setCashForecast(cashForecasts.get(modelKey));
            modelPortfolio.setShadowPortfolio(shadowPortfolios.get(modelKey));
            modelPortfolio.setShadowTransactions(shadowTransactions.get(modelKey));
        }

        return models.values();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public ModelPortfolio loadModel(final IpsKey modelKey, final ServiceErrors serviceErrors) {
        final ModelPortfolioImpl result = new ModelPortfolioImpl();

        new IntegrationOperation("loadModelHeader", serviceErrors) {
            @Override
            public void performOperation() {
                com.avaloq.abs.screen_rep.hira.btfg$ui_ips_list_ips_im_rep_hdr.Rep report = avaloqExecute
                        .executeReportRequest(new AvaloqReportRequest(Template.MODEL_PORTFOLIOS_HEADER_SINGLE.getName())
                                .forProductList(Collections.singletonList(modelKey.getId())));
                ModelPortfolio modelPortfolio = modelConverter.toModelHeader(report, serviceErrors).get(modelKey);
                result.setModelKey(modelPortfolio.getModelKey());
                result.setLastUpdateDate(modelPortfolio.getLastUpdateDate());
                result.setStatus(modelPortfolio.getStatus());
                validator.validate(result, serviceErrors);
            }
        }.run();

        new IntegrationOperation("loadCashForecast", serviceErrors) {
            @Override
            public void performOperation() {
                com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_ips_shdw_mp_cash.Rep report = avaloqExecute
                        .executeReportRequest(new AvaloqReportRequest(Template.MODEL_PORTFOLIOS_CASH_FORECASTING_SINGLE.getName())
                                .forIncludeProductList(Collections.singletonList(modelKey.getId())));
                CashForecast cashForecast = modelConverter.toCashForecast(report, serviceErrors).get(modelKey);
                if (cashForecast != null) {
                    validator.validate(cashForecast, serviceErrors);
                }
                result.setCashForecast(cashForecast);
            }
        }.run();

        result.setShadowPortfolio(loadShadowPortfolioModel(modelKey, serviceErrors));

        new IntegrationOperation("loadShadowTransaction", serviceErrors) {
            @Override
            public void performOperation() {
                com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.Rep report = avaloqExecute
                        .executeReportRequest(new AvaloqReportRequest(Template.MODEL_PORTFOLIOS_SHADOW_TRANSACTIONS_SINGLE
                                .getName()).forIncludeProductList(Collections.singletonList(modelKey.getId())));
                List<ShadowTransaction> shadowTransactions = modelConverter.toShadowTransactionModel(report, serviceErrors).get(
                        modelKey);
                if (shadowTransactions == null) {
                    shadowTransactions = new ArrayList<>();
                }
                validator.validate(shadowTransactions, serviceErrors);
                result.setShadowTransactions(shadowTransactions);
            }
        }.run();

        return result;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public ModelPortfolioUpload validateModel(final ModelPortfolioUpload model, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<ModelPortfolioUpload>("validateModel", serviceErrors) {
            @Override
            public ModelPortfolioUpload performOperation() {
                ModelPortfolioUploadImpl modelPortfolio = webserviceClient.sendToWebService(
                        modelUploadConverter.toModelValidateRequest(model, serviceErrors), AvaloqOperation.MP_CTON_REQ,
                        ModelPortfolioUploadImpl.class, serviceErrors);

                if (modelPortfolio.isErrorResponse()) {
                    logger.error("Error running MP_CTON_REQ in validateModel: " + modelPortfolio.getErrorMessage());
                    serviceErrors.addError(new ServiceErrorImpl(modelPortfolio.getErrorMessage()));
                    return null;
                }

                modelPortfolio.setValidationErrors(validationConverter.toValidationError(modelPortfolio,
                        modelPortfolio.getWarnings()));
                return modelPortfolio;
            }
        }.run();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public ModelPortfolioUpload submitModel(final ModelPortfolioUpload model, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<ModelPortfolioUpload>("submitModel", serviceErrors) {
            @Override
            public ModelPortfolioUpload performOperation() {
                ModelPortfolioUploadImpl modelPortfolio = webserviceClient.sendToWebService(
                        modelUploadConverter.toModelUploadRequest(model, serviceErrors), AvaloqOperation.MP_CTON_REQ,
                        ModelPortfolioUploadImpl.class, serviceErrors);

                if (modelPortfolio.isErrorResponse()) {
                    logger.error("Error running MP_CTON_REQ in submitModel: " + modelPortfolio.getErrorMessage());
                    serviceErrors.addError(new ServiceErrorImpl(modelPortfolio.getErrorMessage()));
                    return null;
                }

                modelPortfolio.setValidationErrors(validationConverter.toValidationError(modelPortfolio,
                        modelPortfolio.getWarnings()));
                return modelPortfolio;
            }
        }.run();
    }

    @Override
    public ModelPortfolioUpload loadUploadedModel(final IpsKey modelKey, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<ModelPortfolioUpload>("loadUploadedModel", serviceErrors) {
            @Override
            public ModelPortfolioUpload performOperation() {

                ModelPortfolioUploadImpl modelPortfolio = webserviceClient.sendToWebService(
                        modelUploadConverter.toGetModelRequest(modelKey.getId()), AvaloqOperation.MP_CTON_REQ,
                        ModelPortfolioUploadImpl.class, serviceErrors);
                modelPortfolio.setValidationErrors(validationConverter.toValidationError(modelPortfolio,
                        modelPortfolio.getWarnings()));
                return modelPortfolio;
            }
        }.run();
    }

    @Override
    public ShadowPortfolio loadShadowPortfolioModel(final IpsKey modelKey, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<ShadowPortfolio>("loadShadowPortfolio", serviceErrors) {
            @Override
            public ShadowPortfolio performOperation() {
                com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_asset_x_wgt.Rep report = avaloqExecute
                        .executeReportRequest(new AvaloqReportRequest(Template.MODEL_PORTFOLIOS_SHADOW_PORTFOLIO_SINGLE.getName())
                                .forIncludeProductList(Collections.singletonList(modelKey.getId())));
                ShadowPortfolio shadowPortfolio = modelConverter.toShadowPortfolioModel(report, serviceErrors).get(modelKey);
                if (shadowPortfolio != null) {
                    validator.validate(shadowPortfolio, serviceErrors);
                }
                return shadowPortfolio;
            }
        }.run();
    }
}
