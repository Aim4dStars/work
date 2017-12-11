package com.bt.nextgen.api.inspecietransfer.v3.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferOrderDto;
import com.bt.nextgen.api.inspecietransfer.v3.util.TransferOrderConverter;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.integration.transfer.InspecieTransferIntegrationService;
import com.bt.nextgen.service.integration.transfer.TransferItem;
import com.bt.nextgen.service.integration.transfer.TransferOrder;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("TransferOrderDtoServiceV3")
@Transactional(value = "springJpaTransactionManager")
public class TransferOrderDtoServiceImpl implements TransferOrderDtoService {

    @Autowired
    private InspecieTransferIntegrationService transferService;

    @Autowired
    @Qualifier("avaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Override
    public List<TransferOrderDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        List<TransferOrderDto> dtos = new ArrayList<>();
        for (ApiSearchCriteria parameter : criteriaList) {
            if (Attribute.ACCOUNT_ID.equals(parameter.getProperty())) {
                AccountKey accKey = AccountKey.valueOf(EncodedString.toPlainText(parameter.getValue()));
                List<TransferOrder> transferOrders = transferService.loadAccountTransferOrders(accKey, serviceErrors);
                if (transferOrders != null && !transferOrders.isEmpty()) {
                    Map<String, Asset> contMap = getContainerMap(accKey, transferOrders);
                    Map<String, Asset> assetMap = getAssetMap(transferOrders, serviceErrors);
                    dtos = TransferOrderConverter.toDtoList(transferOrders, contMap, assetMap);
                }
                break;
            } else {
                throw new IllegalArgumentException("Unsupported search");
            }
        }
        return dtos;
    }

    private Map<String, Asset> getContainerMap(AccountKey accountKey, List<TransferOrder> transferOrders) {
        final List<String> contIds = Lambda.convert(transferOrders, new Converter<TransferOrder, String>() {

            @Override
            public String convert(TransferOrder order) {
                return order.getDestContainerId();
            }
        });

        // retrieve corresponding asset from assetService.
        Map<String, Asset> idMap = new HashMap<>();
        WrapAccountValuation val = portfolioService.loadWrapAccountValuation(accountKey, new DateTime(), new ServiceErrorsImpl());
        for (SubAccountValuation sub : val.getSubAccountValuations()) {
            if (AssetType.MANAGED_PORTFOLIO.equals(sub.getAssetType()) || AssetType.TAILORED_PORTFOLIO.equals(sub.getAssetType())) {
                ManagedPortfolioAccountValuation mpVal = (ManagedPortfolioAccountValuation) sub;
                if (contIds.contains(mpVal.getSubAccountKey().getId())) {
                    idMap.put(mpVal.getSubAccountKey().getId(), mpVal.getAsset());
                }
            }
        }
        return idMap;
    }

    private Map<String, Asset> getAssetMap(List<TransferOrder> transferOrders, ServiceErrors serviceErrors) {
        Set<String> assetSet = new HashSet<>();
        if (transferOrders != null) {
            for (TransferOrder order : transferOrders) {
                if (order.getTransferItems() != null) {
                    for (TransferItem item : order.getTransferItems()) {
                        if (item.getAssetId() != null) {
                            assetSet.add(item.getAssetId());
                        }
                    }
                }
            }
        }
        return assetService.loadAssets(new ArrayList<String>(assetSet), serviceErrors);
    }
}
