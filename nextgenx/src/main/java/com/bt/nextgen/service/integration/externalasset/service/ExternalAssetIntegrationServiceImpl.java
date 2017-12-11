package com.bt.nextgen.service.integration.externalasset.service;


import com.bt.nextgen.api.smsf.model.AssetHoldings;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AbstractUserCachedAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.externalasset.builder.AssetHoldingsConverter;
import com.bt.nextgen.service.integration.externalasset.builder.ExternalAssetConverter;
import com.bt.nextgen.service.integration.externalasset.builder.ExternalAssetResponseBuilder;
import com.bt.nextgen.service.integration.externalasset.model.AssetContainer;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAssetContainer;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAssetResponseHolder;
import com.btfin.abs.trxservice.extlhold.v1_0.ExtlHoldRsp;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"squid:S1188"})
@Service
public class ExternalAssetIntegrationServiceImpl extends AbstractUserCachedAvaloqIntegrationService implements ExternalAssetIntegrationService
{
    @Autowired
    private AvaloqGatewayHelperService webServiceClient;

    @Autowired
    private AvaloqExecute avaloqExecute;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;


    /**
     * <p>Submit a list of external assets to be saved or updated to an external holdings container on a specific account</p>
     *
     * <p>Note: This service is not accumulative. That is, all existing external assets in the container will be replaced by
     * the assets on each call<p>
     * <p>To remove existing external asset(s), omit the asset from the request</p>
     *
     * @param accountKey Account Id to save or update external assets to
     * @param containerKey Key of the container to save external assets to
     * @param externalAssets List of external assets to be saved or updated
     * @return
     */
    @Override
    public TransactionStatus saveOrUpdateExternalAssets(final AccountKey accountKey, final ContainerKey containerKey, final List<ExternalAsset> externalAssets, final DateTime bankDate)
    {
        return new AbstractAvaloqIntegrationService.IntegrationSingleOperation<TransactionStatus>("saveOrUpdateExternalAssets", new ServiceErrorsImpl())
        {
            @Override
            public TransactionStatus performOperation()
            {
                ExtlHoldRsp rsp = webServiceClient.sendToWebService(ExternalAssetConverter.toExternalAssetRequest(externalAssets, accountKey, containerKey, bankDate),
                        AvaloqOperation.EXTL_HOLD_REQ,
                        new ServiceErrorsImpl());

                TransactionStatus status = ExternalAssetResponseBuilder.toExternalAssetResponse(rsp, new ServiceErrorsImpl());

                return status;
            }
        }.run();
    }


    /**
     * Retrieve external holdings for a specific account(s).
     *
     * @param accountKeys List of one or more account keys to retrieve external holdings for
     * @param effectiveDate Valuation date for holdings -- if holdings don't exist yet they won't be returned
     * @return
     */
    public AssetHoldings getExternalAssets(final List<AccountKey> accountKeys, final DateTime effectiveDate)
    {
        return new AbstractAvaloqIntegrationService.IntegrationSingleOperation<AssetHoldings>("getExternalAssets", new ServiceErrorsImpl())
        {
            @Override
            public AssetHoldings performOperation()
            {
                List<String> accountIds = new ArrayList<>();

                for (AccountKey accountKey : accountKeys)
                {
                    accountIds.add(accountKey.getId());
                }

                ExternalAssetResponseHolder externalAssetResponseHolder = avaloqExecute.executeReportRequestToDomain(
                        new AvaloqReportRequest(Template.ACCOUNT_VALUATION.getName()).forIncludeAccountList(accountIds).forEffectiveDate(effectiveDate),
                        ExternalAssetResponseHolder.class,
                        new ServiceErrorsImpl());

                Map<String, Asset> assetDetails = assetIntegrationService.loadExternalAssets(new ServiceErrorsImpl());
                List<ExternalAsset> externalAssetList = new ArrayList<>();

                if (CollectionUtils.isNotEmpty(externalAssetResponseHolder.getAssetContainer())) {

                    for (AssetContainer assetContainer : externalAssetResponseHolder.getAssetContainer()) {
                        if (assetContainer instanceof ExternalAssetContainer) {
                            externalAssetList = ( (ExternalAssetContainer) assetContainer).getExternalAssetList();
                        }
                    }
                }
                AssetHoldings holdings = AssetHoldingsConverter.toAssetHoldings(externalAssetList, assetDetails);

                return holdings;
            }
        }.run();
    }
}