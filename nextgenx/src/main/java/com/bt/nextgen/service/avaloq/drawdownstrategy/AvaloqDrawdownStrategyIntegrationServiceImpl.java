package com.bt.nextgen.service.avaloq.drawdownstrategy;

import ch.lambdaj.Lambda;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.account.AvaloqCacheManagedAccountIntegrationService;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AvaloqContainerIntegrationService;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyIntegrationService;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.hamcrest.core.IsEqual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("avaloqDrawdownStrategyIntegrationService")
public class AvaloqDrawdownStrategyIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        DrawdownStrategyIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(AvaloqDrawdownStrategyIntegrationServiceImpl.class);

    @Autowired
    private AvaloqReportService avaloqService;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private AvaloqCacheManagedAccountIntegrationService avaloqCacheAccountIntegrationService;

    @Autowired
    private AvaloqContainerIntegrationService avaloqContainerIntegrationService;

    @Autowired
    private TransactionValidationConverter validationConverter;

    @Autowired
    private DrawdownStrategyConverter converter;

    @Override
    public DrawdownStrategy loadDrawdownStrategy(AccountKey accountKey, final ServiceErrors serviceErrors) {
        SubAccount directContainer = loadDirectContainer(accountKey, serviceErrors);
        return loadDrawdownStrategyFromUncachedContainer(accountKey, directContainer.getSubAccountKey(), serviceErrors);
    }

    @Override
    public DrawdownStrategyDetails submitDrawdownStrategy(final DrawdownStrategyDetails strategy,
            final ServiceErrors serviceErrors) {
        final SubAccount directContainer = loadDirectContainer(strategy.getAccountKey(), serviceErrors);

        return new IntegrationSingleOperation<DrawdownStrategyDetails>("validateDrawdownStrategyDetails", serviceErrors) {
            @Override
            public DrawdownStrategyDetails performOperation() {
                DrawdownStrategyDetailsImpl response = webserviceClient.sendToWebService(
                        converter.toSubmitDrawdownStrategyRequest(strategy, directContainer.getSubAccountKey()),
                        AvaloqOperation.CONT_REQ, DrawdownStrategyDetailsImpl.class, serviceErrors);

                if (response.isErrorResponse()) {
                    String error = "Error when submitting drawdown strategy with CONT_REQ: " + response.getErrorMessage();
                    logger.error(error);
                    serviceErrors.addError(new ServiceErrorImpl(error));
                }

                response.setAccountKey(strategy.getAccountKey());
                response.setValidationErrors(validationConverter.toValidationError(response, response.getWarnings()));
                return response;
            }
        }.run();
    }

    @Override
    public DrawdownStrategyDetails validateDrawdownAssetPreferences(final DrawdownStrategyDetails strategy,
            final ServiceErrors serviceErrors) {
        final SubAccount directContainer = loadDirectContainer(strategy.getAccountKey(), serviceErrors);

        return new IntegrationSingleOperation<DrawdownStrategyDetails>("validateDrawdownStrategyDetails", serviceErrors) {
            @Override
            public DrawdownStrategyDetails performOperation() {
                DrawdownStrategyDetailsImpl response = webserviceClient.sendToWebService(
                        converter.toValidateAssetPreferencesRequest(strategy, directContainer.getSubAccountKey()),
                        AvaloqOperation.CONT_REQ,
                        DrawdownStrategyDetailsImpl.class, serviceErrors);

                if (response.isErrorResponse()) {
                    String error = "Error when validating asset priority list with CONT_REQ: " + response.getErrorMessage();
                    logger.error(error);
                    serviceErrors.addError(new ServiceErrorImpl(error));
                }

                response.setAccountKey(strategy.getAccountKey());
                response.setValidationErrors(validationConverter.toValidationError(response, response.getWarnings()));
                return response;
            }
        }.run();
    }

    @Override
    public DrawdownStrategyDetails submitDrawdownAssetPreferences(final DrawdownStrategyDetails strategy,
            final ServiceErrors serviceErrors) {
        final SubAccount directContainer = loadDirectContainer(strategy.getAccountKey(), serviceErrors);

        return new IntegrationSingleOperation<DrawdownStrategyDetails>("validateDrawdownStrategyDetails", serviceErrors) {
            @Override
            public DrawdownStrategyDetails performOperation() {
                DrawdownStrategyDetailsImpl response = webserviceClient.sendToWebService(
                        converter.toSubmitAssetPreferencesRequest(strategy, directContainer.getSubAccountKey()),
                        AvaloqOperation.CONT_REQ,
                        DrawdownStrategyDetailsImpl.class, serviceErrors);

                if (response.isErrorResponse()) {
                    String error = "Error when submitting asset priority list with CONT_REQ: " + response.getErrorMessage();
                    logger.error(error);
                    serviceErrors.addError(new ServiceErrorImpl(error));
                }

                response.setAccountKey(strategy.getAccountKey());
                response.setValidationErrors(validationConverter.toValidationError(response, response.getWarnings()));
                return response;
            }
        }.run();
    }

    @Override
    public DrawdownStrategyDetails loadDrawdownAssetPreferences(AccountKey accountKey, ServiceErrors serviceErrors) {
        SubAccount directContainer = loadDirectContainer(accountKey, serviceErrors);
        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(DrawdownStrategyTemplate.ASSET_PRIORITY_LIST).forParam(
                DrawdownStrategyParams.CONT_LIST_ID, directContainer.getSubAccountKey().getId());

        DrawdownStrategyDetailsImpl response = avaloqService.executeReportRequestToDomain(avaloqRequest,
                DrawdownStrategyDetailsImpl.class, serviceErrors);
        response.setAccountKey(accountKey);
        return response;
    }

    private SubAccount loadDirectContainer(AccountKey accountKey, ServiceErrors serviceErrors) {
        List<SubAccount> subAccounts = avaloqCacheAccountIntegrationService.loadSubAccounts(serviceErrors).get(accountKey);
        SubAccount directContainer = Lambda.selectFirst(subAccounts,
                Lambda.having(Lambda.on(SubAccount.class).getSubAccountType(), IsEqual.equalTo(ContainerType.DIRECT)));
        return directContainer;
    }

    private DrawdownStrategy loadDrawdownStrategyFromUncachedContainer(AccountKey accountKey, SubAccountKey subAccountKey,
            ServiceErrors serviceErrors) {
        List<String> containerIds = Collections.singletonList(subAccountKey.getId());
        List<SubAccount> containers = avaloqContainerIntegrationService.loadSpecificContainers(accountKey, containerIds,
                serviceErrors).get(accountKey);

        String drawdownStrategy = containers.get(0).getDrawdownStrategy();
        return DrawdownStrategy.forIntlId(drawdownStrategy);
    }
}
