package com.bt.nextgen.api.inspecietransfer.v3.util;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.inspecietransfer.v3.model.SponsorDetailsDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.TaxParcelDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferAssetDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.transfer.transfergroup.TransferAssetImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferAsset;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("TransferAssetConverterV3")
public class TransferAssetConverter {

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    @Qualifier("avaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioIntegrationService;

    @Autowired
    private AssetDtoConverter assetDtoConverter;

    public TransferAssetConverter() {
        // default constructor
    }

    public List<TransferAsset> fromDtoList(List<TransferAssetDto> transferAssetDtos, String accountId, TransferType transferType) {
        List<TransferAsset> preferences = new ArrayList<>();

        for (TransferAssetDto transferAssetDto : transferAssetDtos) {
            AssetDto asset = transferAssetDto.getAsset();
            if (asset == null) {
                continue;
            }

            TransferAssetImpl transferAsset = new TransferAssetImpl();
            transferAsset.setAssetId(asset.getAssetId());
            transferAsset.setName(asset.getAssetName());
            transferAsset.setType(AssetType.forName(asset.getAssetType()));
            transferAsset.setQuantity(transferAssetDto.getQuantity());
            transferAsset.setIsCashTransfer(transferAssetDto.getIsCashTransfer() == null ? false : transferAssetDto
                    .getIsCashTransfer());
            if (transferAsset.getIsCashTransfer()) {
                // Retrieve BT Cash id
                transferAsset.setAssetId(getCashAssetId(AccountKey.valueOf(accountId)));
            }
            if (TransferType.INTERNAL_TRANSFER != transferType) {
                transferAsset.setSponsorDetails(SponsorDetailsConverter.fromDto(transferAssetDto.getSponsorDetails(),
                        transferType.getDisplayName()));
                transferAsset.setTaxParcels(TaxParcelConverter.fromDtoList(transferAssetDto.getTaxParcels()));
            }

            preferences.add(transferAsset);
        }
        return preferences;
    }

    public List<TransferAssetDto> toDtoList(List<TransferAsset> transferAssets, TransferType transferType,
            ServiceErrors serviceErrors) {
        List<TransferAssetDto> transferAssetDtos = new ArrayList<>();
        if (transferAssets != null) {
            Map<String, AssetDto> assetDtos = getAssetDtos(transferAssets, serviceErrors);

            for (TransferAsset transferAsset : transferAssets) {
                if (transferAsset.getAssetId() == null) {
                    continue;
                }

                AssetDto assetDto = assetDtos.get(transferAsset.getAssetId());
                SponsorDetailsDto sponsorDetailsDto = SponsorDetailsConverter.toDto(transferAsset.getSponsorDetails(),
                        transferType);
                List<TaxParcelDto> taxParcelDtos = TaxParcelConverter.toDtoList(transferAsset.getTaxParcels(), assetDto);
                transferAssetDtos.add(new TransferAssetDto(transferAsset, assetDto, sponsorDetailsDto, taxParcelDtos));
            }
        }
        return transferAssetDtos;
    }

    private Map<String, AssetDto> getAssetDtos(List<TransferAsset> transferAssets, ServiceErrors serviceErrors) {
        List<String> assetIds = getAssetIds(transferAssets);
        Map<String, Asset> assets = assetService.loadAssets(assetIds, serviceErrors);
        Map<String, TermDepositAssetDetail> tdMap = new HashMap<>();
        return assetDtoConverter.toAssetDto(assets, tdMap);
    }

    private List<String> getAssetIds(List<TransferAsset> transferAssets) {
        List<String> assetIds = new ArrayList<>();
        if (transferAssets != null && !transferAssets.isEmpty()) {
            for (TransferAsset asset : transferAssets) {
                if (asset.getAssetId() != null) {
                    assetIds.add(asset.getAssetId());
                }
            }
        }
        return assetIds;
    }

    private String getCashAssetId(AccountKey accountKey) {
        WrapAccountValuation val = portfolioIntegrationService.loadWrapAccountValuation(accountKey, DateTime.now(),
                new ServiceErrorsImpl());
        for (SubAccountValuation subVal : val.getSubAccountValuations()) {
            if (AssetType.CASH == subVal.getAssetType()) {
                for (AccountHolding holding : subVal.getHoldings()) {
                    if (AssetType.CASH == holding.getAsset().getAssetType())
                        return holding.getAsset().getAssetId();
                }
            }
        }
        return null;
    }
}
