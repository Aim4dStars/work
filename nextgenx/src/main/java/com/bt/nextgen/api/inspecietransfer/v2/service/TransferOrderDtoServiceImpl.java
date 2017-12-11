package com.bt.nextgen.api.inspecietransfer.v2.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v2.model.TransferDest;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.transfer.TransferOrderImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.integration.transfer.InspecieTransferIntegrationService;
import com.bt.nextgen.service.integration.transfer.TransferOrder;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Use V3
 */
@Deprecated
@Service("TransferOrderDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
public class TransferOrderDtoServiceImpl extends InspecieTransferBaseDtoServiceImpl implements TransferOrderDtoService {

    @Autowired
    private InspecieTransferIntegrationService inspecieTransferIntegrationService;

    @Autowired
    private TransferAssetHelper assetHelper;

    @Autowired
    @Qualifier("avaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioService;

    @Override
    public List<InspecieTransferDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {

        List<InspecieTransferDto> dtos = new ArrayList<>();
        for (ApiSearchCriteria parameter : criteriaList) {
            if (Attribute.ACCOUNT_ID.equals(parameter.getProperty())) {
                AccountKey accKey = AccountKey.valueOf(EncodedString.toPlainText(parameter.getValue()));
                List<TransferOrder> transferOrders = inspecieTransferIntegrationService.loadAccountTransferOrders(accKey,
                        serviceErrors);

                Map<String, Asset> contMap = getContainerMap(accKey, transferOrders);
                // convert and return dto
                for (TransferOrder order : transferOrders) {
                    dtos.add(toTransferDto((TransferOrderImpl) order, contMap));
                }
                break;
            } else {
                throw new IllegalArgumentException("Unsupported search");
            }
        }
        return dtos;
    }

    public InspecieTransferDto toTransferDto(TransferOrderImpl transferOrder, Map<String, Asset> contMap) {

        if (transferOrder == null)
            return null;

        InspecieTransferDtoImpl transferDto = new InspecieTransferDtoImpl(transferOrder.getTransferType().name(),
                populateSponsorDetailsDto(transferOrder.getSponsorDetails(), transferOrder.getTransferType()),
                assetHelper.toTransferItemDto(transferOrder.getTransferItems()), EncodedString.fromPlainText(
                        transferOrder.getDestContainerId()).toString(), new InspecieTransferKey(transferOrder.getAccountId(),
                        transferOrder.getTransferId()), transferOrder.isChangeOfBeneficialOwnership(), null);

        transferDto.setTransferDate(transferOrder.getTransferDate());

        // Take order status from parent if available, otherwise take it from the first child order
        if (transferOrder.getStatus() != null) {
            transferDto.setTransferStatus(transferOrder.getStatus().name());

        } else if (transferOrder.getTransferItems() != null && !transferOrder.getTransferItems().isEmpty()) {
            transferDto.setTransferStatus(transferOrder.getTransferItems().get(0).getTransferStatus().name());
        }

        transferDto.setDest(getContainerDetails(transferOrder, contMap));

        return transferDto;
    }

    public TransferDest getContainerDetails(TransferOrderImpl transferOrder, Map<String, Asset> contMap) {
        if (transferOrder == null) {
            return null;
        }
        Asset asset = contMap.get(transferOrder.getDestContainerId());
        if (asset != null) {
            TransferDest dest = new TransferDest(transferOrder.getDestContainerId(), asset.getAssetId(), asset.getAssetName(),
                    asset.getAssetType().name(), asset.getAssetCode());
            return dest;
        }
        return new TransferDest(transferOrder.getDestContainerId(), null, null, null, null);
    }

    protected Map<String, Asset> getContainerMap(AccountKey accountKey, List<TransferOrder> transferOrders) {
        // retrieve all asset-ids.
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
}
